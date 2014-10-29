DiTop Server Component
============
##### for more information on the DiTop project visit: [ditop.hs8.de](http://ditop.hs8.de)

clone repository:

	git clone https://github.com/HendrikStrobelt/ditop_server.git

This is a [Spring Boot](http://projects.spring.io/spring-boot/) project using [Maven](http://maven.apache.org/guides/getting-started/maven-in-five-minutes.html). You can.. 

- .. execute the project by:
```
	cd ditop_server
	mvn spring-boot:run
```

- .. generate a runnable (fat) JAR:
```
cd ditop_server
mvn package
java -jar target/ditop_server-1.0.jar
```

!!! Before starting you should change the data directory in [src/main/resources/application.yaml](src/main/resources/application.yaml)


The software is licensed under the new BSD.

