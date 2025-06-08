# Use Eclipse Temurin JDK 24 for building
FROM eclipse-temurin:24-jdk AS build

WORKDIR /app

# Copy Maven Wrapper scripts and pom.xml first
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies (кэшируем слой)
RUN ./mvnw dependency:go-offline -B

# Copy project source code
COPY . .

# Build jar, пропуская тесты
RUN ./mvnw clean package -DskipTests

# Use JDK image for development (не JRE!)
FROM eclipse-temurin:24-jdk

WORKDIR /app

# Copy the fat jar
COPY --from=build /app/target/*.jar app.jar

# Копируем исходный код для горячей перезагрузки
COPY . .

# Для горячей перезагрузки нужно копировать исходники, а не только jar
VOLUME /app

EXPOSE 8080
EXPOSE 5005

# Команда для запуска с поддержкой горячей перезагрузки
CMD ["./mvnw", "spring-boot:run", "-Dspring-boot.run.jvmArguments=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-Dspring.devtools.restart.enabled=true"]
