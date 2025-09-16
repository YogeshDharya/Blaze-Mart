FROM eclipse-temurin:11-jdk-focal

VOLUME /tmp

# Copy all the source files
COPY ./gradle ./gradle
COPY gradlew build.gradle ./
COPY settings.gradle ./
COPY ./blazemartbackend/src ./blazemartbackend/src
COPY ./blazemartbackend/build.gradle ./blazemartbackend/build.gradle
COPY ./assets ./assets

ENTRYPOINT ["./gradlew", "bootRun", "--no-daemon"]