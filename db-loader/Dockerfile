FROM sbtscala/scala-sbt:graalvm-ce-22.3.0-b2-java17_1.8.2_2.13.10

WORKDIR /app/db-loader
COPY . .

ENV SBT_OPTS="-Xmx2G -Xss2M"
ENV DBPATH="postgres-test-docker"
CMD sbt run