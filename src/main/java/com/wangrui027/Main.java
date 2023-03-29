package com.wangrui027;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Main {

    public static void main(String[] args) {
        Main main = new Main();
        main.execute();
    }

    private String jdbcUrl;
    private String jdbcDriverName;
    private String jdbcUsername;
    private String jdbcPassword;
    private String sqlFile;
    private String scriptRunnerLogFile;

    private void execute() {
        initConfig();
        executeSql();
    }

    private void executeSql() {
        ScriptRunner scriptRunner = getScriptRunner();
        InputStream is = null;
        InputStreamReader reader = null;
        try {
            is = Files.newInputStream(Paths.get(sqlFile));
            reader = new InputStreamReader(is);
            scriptRunner.runScript(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            close(is, reader);
            scriptRunner.closeConnection();
            System.out.println("SQL执行日志：" + getJarDirectory() + File.separator + "logs" + File.separator + "jdbc-test.log");
        }
    }

    private void close(AutoCloseable... closeable) {
        if (closeable != null) {
            for (AutoCloseable autoCloseable : closeable) {
                if (autoCloseable != null) {
                    try {
                        autoCloseable.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void initConfig() {
        String path = getJarDirectory();
        scriptRunnerLogFile = path + File.separator + "logs" + File.separator + "jdbc-test.log";
        File file = new File(scriptRunnerLogFile);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        sqlFile = path + File.separator + "jdbc-test.sql";
        Properties properties = new Properties();
        try (InputStream is = new FileInputStream(path + File.separator + "jdbc-test.properties")) {
            properties.load(is);
            jdbcUrl = properties.getProperty("jdbc.url");
            jdbcDriverName = properties.getProperty("jdbc.driverName");
            jdbcUsername = properties.getProperty("jdbc.username");
            jdbcPassword = properties.getProperty("jdbc.password");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getJarDirectory() {
        //获取当前类所在路径
        String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        //解决中文路径
        try {
            path = URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        File file = null;
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            file = new File(path.substring(1));
        } else if (System.getProperty("os.name").toLowerCase().contains("linux")) {
            file = new File(path);
        }
        if (file.exists()) {
            return file.getParentFile().getAbsolutePath();
        } else {
            throw new RuntimeException("获取jar包所在目录异常");
        }
    }

    private ScriptRunner getScriptRunner() {
        try {
            Class.forName(jdbcDriverName);
            Connection conn = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword);
            ScriptRunner scriptRunner = new ScriptRunner(conn);
            scriptRunner.setLogWriter(new PrintWriter(scriptRunnerLogFile));
            Resources.setCharset(StandardCharsets.UTF_8);
            return scriptRunner;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
