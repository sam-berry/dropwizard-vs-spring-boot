# Dropwizard vs Spring Boot

Simple multithreaded load test of both application frameworks.

## Results

Dropwizard is able to process 300k requests over 10 threads out of the box. Spring Boot handles around 150k. 

### Test

The applications can be tested using the following commands:

* `MAVEN_OPTS='-Xmx100m -Xms100m' mvn -f spring-boot test -Dtest=APILoadTest -Dthreads=<threads> -Drequests=<requests>`
* `MAVEN_OPTS='-Xmx100m -Xms100m' mvn -f dropwizard test -Dtest=APILoadTest -Dthreads=<threads> -Drequests=<requests>`

`APILoadTest` posts the specified number of requests over the specified number of threads and verifies that they return fast enough.
