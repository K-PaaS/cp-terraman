FROM openjdk:17-alpine
ARG JAR_FILE=build/libs/container-terraman-api.jar
RUN apk add --no-cache \
  openssh-client \
  ca-certificates \
  curl \
  sudo \
  bash
RUN curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl" \
    && sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl
RUN addgroup -S 1000 && adduser -S 1000 -G 1000
RUN mkdir -p /home/1000
COPY ${JAR_FILE} /home/1000/container-terraman-api.jar
RUN chown -R 1000:1000 /home/1000
RUN apk update && apk upgrade && apk add --no-cache bash
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=prod", "/home/1000/container-terraman-api.jar"]
