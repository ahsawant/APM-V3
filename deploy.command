if [ ! -d "jetty-escala_server-base/webapps/client" ]; then
  mkdir "jetty-escala_server-base/webapps/client"
fi
cp -r ./escala_client/WebContent/* "jetty-escala_server-base/webapps/client"

cp ./escala_rest_apis/target/escala_apis-0.0.1-SNAPSHOT.war "jetty-escala_server-base/webapps/escala.war"

cp ./escala-util-servlets/target/escala-util-0.0.1-SNAPSHOT.war "jetty-escala_server-base/webapps/escala-util.war"

echo "Script finished successfully"

