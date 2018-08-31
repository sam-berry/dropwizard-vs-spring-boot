# Dropwizard vs Spring Boot

Simple multithreaded load test of both application frameworks.

## Results

Let the facts be clear. Dropwizard is superior to Spring Boot in every single way, primarily; concurrent performance and being better. 

### Test

The applications can be tested using the following commands:

* `MAVEN_OPTS='-Xmx100m -Xms100m' mvn -f spring-boot test -Dtest=APILoadTest -Dthreads=<threads> -Drequests=<requests>`
* `MAVEN_OPTS='-Xmx100m -Xms100m' mvn -f dropwizard test -Dtest=APILoadTest -Dthreads=<threads> -Drequests=<requests>`

Executing 700k requests over 10 threads displays Dropwizard's capabilities. Spring Boot fails at <200k requests.

`APILoadTest` posts the specified number of requests over the specified number of threads and verifies that they return fast enough.

## Improvements

This repository would be better if the application code was imported as a module to both implementations, Spring Boot and Dropwizard. That would make it easier to keep the two implementations in sync.
