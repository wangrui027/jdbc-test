mvn clean compile assembly:single
rm dist -rf
mkdir -p dist/lib dist/logs
cp src/main/resources/jdbc-test.properties dist/jdbc-test.properties
cp src/main/resources/jdbc-test.sql dist/jdbc-test.sql
cp src/main/resources/run.sh dist/run.sh
cp src/main/resources/run.bat dist/run.bat
mv target/jdbc-test*.jar dist/jdbc-test.jar
