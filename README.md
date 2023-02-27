# Monix MDC with Log4j2

Example code for blog post [Monix Async, MDC, ThreadLocal, Correlation ID in Scala](https://sonhmai.github.io/software/monix-async-and-mdc-in-scala)

- How to separate MDC when using Monix with `IO` and `Future`?
- See blog post for more details

## Issue Replication
- Run AppOld.scala to spin up server with buggy code on port 8081
- Request 
```shell
curl http://localhost:8081/api -H "correlationId: 1" -d '{"someKey": "someValue"}' &
curl http://localhost:8081/api -H "correlationId: 2" -d '{"someKey": "someValue"}' &
curl http://localhost:8081/api -H "correlationId: 3" -d '{"someKey": "someValue"}' &
```
- Server logging
```shell
2023-02-27 11:45:36.333 [main] INFO  com.scala.monixmdc.old.ServerOld - MDC: correlationId= - Http server started on port Some(8081)
2023-02-27 11:45:41.726 [blaze-selector-3] INFO  org.http4s.blaze.channel.nio1.NIO1SocketServerGroup - MDC: correlationId= - Accepted connection from /127.0.0.1:57919
2023-02-27 11:45:41.726 [blaze-selector-1] INFO  org.http4s.blaze.channel.nio1.NIO1SocketServerGroup - MDC: correlationId= - Accepted connection from /127.0.0.1:57917
2023-02-27 11:45:41.726 [blaze-selector-2] INFO  org.http4s.blaze.channel.nio1.NIO1SocketServerGroup - MDC: correlationId= - Accepted connection from /127.0.0.1:57918
2023-02-27 11:45:41.738 [IO-30] INFO  com.scala.monixmdc.old.MiddlewareIO - MDC: correlationId= - putting mdc, corrId 2
2023-02-27 11:45:41.738 [IO-29] INFO  com.scala.monixmdc.old.MiddlewareIO - MDC: correlationId= - putting mdc, corrId 3
2023-02-27 11:45:41.738 [IO-31] INFO  com.scala.monixmdc.old.MiddlewareIO - MDC: correlationId= - putting mdc, corrId 1
2023-02-27 11:45:41.784 [IO-32] INFO  com.scala.monixmdc.old.RoutesIO - MDC: correlationId=2 - Calling another service to get data
2023-02-27 11:45:41.784 [IO-33] INFO  com.scala.monixmdc.old.RoutesIO - MDC: correlationId=2 - Calling another service to get data
2023-02-27 11:45:41.784 [IO-34] INFO  com.scala.monixmdc.old.RoutesIO - MDC: correlationId=2 - Calling another service to get data
2023-02-27 11:45:41.995 [IO-29] INFO  com.scala.monixmdc.old.RoutesIO - MDC: correlationId=2 - Calling the database for correlationId 2
2023-02-27 11:45:41.995 [IO-30] INFO  com.scala.monixmdc.old.RoutesIO - MDC: correlationId=2 - Calling the database for correlationId 3
2023-02-27 11:45:41.995 [IO-31] INFO  com.scala.monixmdc.old.RoutesIO - MDC: correlationId=2 - Calling the database for correlationId 1
2023-02-27 11:45:43.076 [IO-32] INFO  com.scala.monixmdc.old.MiddlewareIO - MDC: correlationId= - cleared mdc for request corrId 1
2023-02-27 11:45:43.076 [IO-34] INFO  com.scala.monixmdc.old.MiddlewareIO - MDC: correlationId= - cleared mdc for request corrId 3
2023-02-27 11:45:43.076 [IO-33] INFO  com.scala.monixmdc.old.MiddlewareIO - MDC: correlationId= - cleared mdc for request corrId 2
```
- Observations
    - 3 concurrent requests are processed in different threads and can be switched to another thread on async boundaries.
    - `correlationId` in MDC are overwritten and not separated for each thread. The expected behavior: they are isolated for different requests.
      (see logs `Calling another service to get data` and `Calling the database` for example, they all have `correlationId=2`)


## After Fixing
- Run AppImproved.scala to spin up server with buggy code on port 8082
- Request
```shell
curl http://localhost:8082/api -H "correlationId: 1" -d '{"someKey": "someValue"}' &
curl http://localhost:8082/api -H "correlationId: 2" -d '{"someKey": "someValue"}' &
curl http://localhost:8082/api -H "correlationId: 3" -d '{"someKey": "someValue"}' &
```
- Server logging
```shell
2023-02-27 13:39:41.552 [blaze-selector-2] INFO  org.http4s.blaze.channel.nio1.NIO1SocketServerGroup - MDC: correlationId= - Accepted connection from /127.0.0.1:62790
2023-02-27 13:39:41.553 [blaze-selector-1] INFO  org.http4s.blaze.channel.nio1.NIO1SocketServerGroup - MDC: correlationId= - Accepted connection from /127.0.0.1:62791
2023-02-27 13:39:41.553 [blaze-selector-3] INFO  org.http4s.blaze.channel.nio1.NIO1SocketServerGroup - MDC: correlationId= - Accepted connection from /127.0.0.1:62793
2023-02-27 13:39:41.568 [IO-30] INFO  com.scala.monixmdc.improved.MiddlewareTask - MDC: correlationId= - putting mdc, corrId 3
2023-02-27 13:39:41.568 [IO-29] INFO  com.scala.monixmdc.improved.MiddlewareTask - MDC: correlationId= - putting mdc, corrId 1
2023-02-27 13:39:41.568 [IO-31] INFO  com.scala.monixmdc.improved.MiddlewareTask - MDC: correlationId= - putting mdc, corrId 2
2023-02-27 13:39:41.588 [IO-32] INFO  com.scala.monixmdc.improved.Routes - MDC: correlationId=2 - Calling another service to get data for correlationId 2
2023-02-27 13:39:41.588 [IO-34] INFO  com.scala.monixmdc.improved.Routes - MDC: correlationId=3 - Calling another service to get data for correlationId 3
2023-02-27 13:39:41.588 [IO-33] INFO  com.scala.monixmdc.improved.Routes - MDC: correlationId=1 - Calling another service to get data for correlationId 1
2023-02-27 13:39:41.800 [IO-36] INFO  com.scala.monixmdc.improved.Routes - MDC: correlationId=3 - Calling the database for correlationId 3
2023-02-27 13:39:41.800 [IO-38] INFO  com.scala.monixmdc.improved.Routes - MDC: correlationId=1 - Calling the database for correlationId 1
2023-02-27 13:39:41.800 [IO-37] INFO  com.scala.monixmdc.improved.Routes - MDC: correlationId=2 - Calling the database for correlationId 2
2023-02-27 13:39:42.877 [IO-37] INFO  com.scala.monixmdc.improved.MiddlewareTask - MDC: correlationId= - cleared mdc for request corrId 2
2023-02-27 13:39:42.877 [IO-36] INFO  com.scala.monixmdc.improved.MiddlewareTask - MDC: correlationId= - cleared mdc for request corrId 3
2023-02-27 13:39:42.877 [IO-38] INFO  com.scala.monixmdc.improved.MiddlewareTask - MDC: correlationId= - cleared mdc for request corrId 1
```
- Observations
    - 3 concurrent requests are processed in 3 different threads and can be switched to another thread on async boundaries.
    - `correlationId`'s are correct for each request (comparing in `MDC: correlationId=` vs `for correlationId` in logs)
    

