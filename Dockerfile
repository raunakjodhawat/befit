FROM sbtscala/scala-sbt:graalvm-ce-22.3.0-b2-java17_1.8.2_2.13.10

WORKDIR /app
COPY . .

WORKDIR /app/db-schema
RUN sbt publishLocal

WORKDIR /app/app
RUN sbt publishLocal

WORKDIR /app/e2e-test
CMD sbt run