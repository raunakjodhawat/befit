FROM sbtscala/scala-sbt:graalvm-ce-22.3.0-b2-java17_1.8.2_2.13.10

WORKDIR /app
COPY . .
COPY build.sbt .
EXPOSE 8081
CMD sbt run assembly