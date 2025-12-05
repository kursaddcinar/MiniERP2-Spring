# 1. Base Image: Eclipse Temurin (Java 17'nin en stabil sürümü)
FROM eclipse-temurin:17-jdk-jammy

# 2. Çalışma dizini
WORKDIR /app

# 3. Jar dosyasını kopyala
COPY target/*.jar app.jar

# 4. Portu dışarı aç
EXPOSE 8080

# 5. Başlatma komutu
ENTRYPOINT ["java", "-jar", "app.jar"]