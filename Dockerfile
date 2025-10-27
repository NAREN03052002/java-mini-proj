# --- STAGE 1: BUILD (Use a Maven image to compile and package the WAR) ---
FROM maven:3.9.5-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# --- STAGE 2: RUN (Use a lightweight Tomcat image to host the WAR) ---
FROM tomcat:10.1-jdk17-temurin
# Tomcat runs on port 8080 by default. Render needs to map this.
EXPOSE 8080

# Remove existing webapps (optional, for clean deployment)
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy the built WAR file from the build stage into Tomcat's webapps directory.
# The WAR file name is defined as 'ROOT.war' in the pom.xml's <finalName> tag.
COPY --from=build /app/target/ROOT.war /usr/local/tomcat/webapps/ROOT.war

# Command to start Tomcat
CMD ["catalina.sh", "run"]
