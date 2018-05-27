# wodss-tippspiel_backend
Contains backend for World Cup 2018 betting competition.

# Instalation:
IMPORTANT: Not testet with java 9 and 10 due to gradle and lombok issues with java 10.

Make sure you have a local MySQL database. The db name and credentials can be found in the application.propperties file.
At the time of writing you have to use the follwing parameters:
DB Name: localhost:3306/bettinggame
DB User: bettinggame
DB User Password: 3k:Vp:JzS,R)r5U](g

If you want the wikipedia scraper to load on application startup all games, teams, locations and tournament groups from wikipedia into the local database, you have to set the scraper.onstartup parameter in application.propperties to true.

To use the backend localy you have to set those parameters in the application.properties file:
server.ssl.key-store=classpath:keystore.jks
server.ssl.key-password=keypassword
server.ssl.key-store-password=storepassword
server.ssl.key-alias=tomcat
server.ssl.enabled-protocols=TLSv1.2
server.ssl.ciphers=TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384,TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384,TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256,TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256,TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384,TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384

Now you can run the application by executing:
./gradlew bootRun

You can terminate the backend by pressing ctrl + c

# Testing:
Run ./gradlew test
185 tests should be executed.
