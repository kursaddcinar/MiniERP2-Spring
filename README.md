# MiniERP - Kurumsal Kaynak Planlama Backend Sistemi

![Java](https://img.shields.io/badge/Java-17-007396)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.8-6DB33F)
![Security](https://img.shields.io/badge/Spring_Security-6.0-6DB33F)
![Database](https://img.shields.io/badge/PostgreSQL-14-336791)
![Build](https://img.shields.io/badge/Maven-Build_Passing-4c1)

## Proje Hakkında

MiniERP, kurumsal iş süreçlerini yönetmek amacıyla tasarlanmış, ölçeklenebilir, güvenli ve modüler bir RESTful Backend projesidir. **Java** ve **Spring Boot** ekosistemi üzerine inşa edilen bu sistem, endüstri standardı yazılım prensipleri (SOLID, Clean Code) ve tasarım desenleri gözetilerek geliştirilmiştir.

Projenin temel amacı; kullanıcı yönetimi, yetkilendirme, ürün ve sipariş takibi gibi ERP fonksiyonlarını güvenli bir altyapı üzerinden sunmaktır. Özellikle güvenlik katmanında **Stateless JWT (JSON Web Token)** mimarisi kullanılarak, sunucu tarafında oturum maliyeti (session overhead) ortadan kaldırılmış ve yüksek performans hedeflenmiştir.

---

## Teknik Mimari ve Tasarım Kararları

Proje, sorumlulukların net bir şekilde ayrıldığı **Katmanlı Mimari (Layered Architecture)** prensibine uygun olarak tasarlanmıştır. Bu yapı, kodun okunabilirliğini artırırken, bakımını ve test edilebilirliğini kolaylaştırır.

### Mimari Katmanlar

1.  **Controller Layer (Sunum Katmanı):**
    * Dış dünya ile iletişim kuran REST API uç noktalarını barındırır.
    * İstekleri karşılar ve `ApiResponse<T>` sarmalayıcısı ile standart bir JSON formatında cevap döner.
    * Giriş validasyonları ve yetki kontrolleri (Pre-Authorization) burada tetiklenir.

2.  **Service Layer (İş Mantığı Katmanı):**
    * Uygulamanın kalbidir. Tüm iş kuralları, veri manipülasyonları ve transaction yönetimi (`@Transactional`) burada işlenir.
    * Entity - DTO dönüşümleri bu katmanda yönetilerek veritabanı nesnelerinin dışarıya sızması engellenir.

3.  **Repository Layer (Veri Erişim Katmanı):**
    * Spring Data JPA kullanılarak veritabanı ile iletişim sağlanır.
    * Karmaşık sorgular için JPQL (Java Persistence Query Language) kullanılarak performans optimizasyonu yapılmıştır.
    * SQL Injection saldırılarına karşı parametrik sorgu yapısı benimsenmiştir.

4.  **Security Layer (Güvenlik Katmanı):**
    * İsteklerin API'ye ulaşmadan önce süzüldüğü filtre katmanıdır.
    * Custom Filter (`JwtAuthenticationFilter`) ile her istekte token doğrulaması yapılır.

---

## Öne Çıkan Özellikler

### 1. Gelişmiş Güvenlik Altyapısı
* **JWT Authentication:** Oturum bağımsız (Stateless) kimlik doğrulama mekanizması.
* **Role-Based Access Control (RBAC):** Dinamik rol yönetimi.
    * `ROLE_ADMIN`: Sistemin tüm kaynaklarına erişim yetkisi.
    * `ROLE_USER`: Sadece tanımlı iş fonksiyonlarına erişim yetkisi.
* **Password Hashing:** Kullanıcı şifreleri veritabanında düz metin olarak değil, **BCrypt** algoritması ile hashlenerek saklanır.
* **Security Context:** Doğrulanmış kullanıcı bilgisi, uygulamanın her yerinden erişilebilecek şekilde `SecurityContextHolder` içerisine yüklenir.

### 2. Hata Yönetimi (Exception Handling)
* Global hata yakalama mekanizması (`@RestControllerAdvice`) ile çalışma zamanı hataları merkezileştirilmiştir.
* İstemciye, Java stack trace yerine anlaşılır, HTTP standartlarına uygun hata mesajları (401 Unauthorized, 403 Forbidden, 404 Not Found) dönülür.

### 3. Veritabanı ve Veri Yönetimi
* **Entity İlişkileri:** User ve Role tabloları arasında Many-to-Many ilişki kurgusu.
* **Data Initialization:** Uygulama ayağa kalktığında `CommandLineRunner` aracılığıyla varsayılan rollerin ve Admin kullanıcısının otomatik oluşturulması.


### 4. Otomatik Seed Data (Tohum Verileri):**
    * Sistem ilk kez ayağa kalktığında `DataInitializer` sınıfı devreye girer.
    * Gerekli tüm roller (`ROLE_ADMIN`, `ROLE_MANAGER`, `ROLE_USER`, `ROLE_SALES`, `ROLE_FINANCE` vb.) otomatik oluşturulur.
    * Varsayılan **Admin** kullanıcısı oluşturulur ve tüm yetkiler atanır.
---

## Kullanılan Teknolojiler

| Teknoloji | Kullanım Amacı |
| :--- | :--- |
| **Java 17** | Ana programlama dili |
| **Spring Boot 3.x** | Uygulama çatısı ve bağımlılık yönetimi |
| **Spring Security** | Kimlik doğrulama ve yetkilendirme |
| **JJWT (Java JWT)** | Token oluşturma ve çözümleme |
| **PostgreSQL** | İlişkisel veritabanı yönetim sistemi |
| **Hibernate / JPA** | ORM (Object-Relational Mapping) aracı |
| **Lombok** | Boilerplate kod azaltma |
| **Swagger / OpenAPI** | API dokümantasyonu ve test arayüzü |
| **Maven** | Proje inşası ve kütüphane yönetimi |

---

## Kurulum ve Çalıştırma

Projeyi yerel ortamınızda çalıştırmak için aşağıdaki adımları izleyebilirsiniz.

### Ön Gereksinimler
* JDK 17 veya üzeri
* Maven
* PostgreSQL Veritabanı

### Adım 1: Projeyi Klonlayın
```bash
git clone [https://github.com/kursaddcinar/MiniERP2-Spring.git](https://github.com/kursaddcinar/MiniERP2-Spring.git)
cd MiniERP2-Spring
```


### Adım 2: Veritabanı Konfigürasyonu
```properties
src/main/resources/application.properties dosyasını kendi veritabanı bilgilerinizle güncelleyin.
spring.datasource.url=jdbc:postgresql://localhost:5432/minierp_db
spring.datasource.username=postgres
spring.datasource.password=sifreniz
spring.jpa.hibernate.ddl-auto=update
```


### Adım 3: JWT Ayarları
Güvenli bir token üretimi için en az 256-bit (32 karakter) uzunluğunda bir gizli anahtar belirleyin.
```Properties
jwt.secret=BURAYA_COK_GIZLI_VE_KARMASIK_BIR_ANAHTAR_YAZIN
jwt.expiration=86400000
```


### Adım 4: Derleme ve Çalıştırma
Güvenli bir token üretimi için en az 256-bit (32 karakter) uzunluğunda bir gizli anahtar belirleyin.
```bash
mvn clean install
mvn spring-boot:run
```

### Alternatif: Docker ile Hızlı Kurulum (Önerilen)

Projeyi veritabanı ile birlikte tek komutla ayağa kaldırmak için Docker kullanabilirsiniz.

1.  **Build Alın:**
    ```bash
    mvn clean package -DskipTests
    ```
2.  **Konteynerleri Başlatın:**
    ```bash
    docker-compose up -d
    ```
    *Bu komut PostgreSQL ve Backend servisini otomatik olarak kurup başlatacaktır.*

3.  **Sistemi Durdurma:**
    ```bash
    docker-compose down
    ```

Uygulama başarıyla başlatıldığında, sistem otomatik olarak ROLE_ADMIN ve ROLE_USER yetkilerini veritabanına ekleyecek ve varsayılan bir Admin kullanıcısı oluşturacaktır.
Varsayılan Admin: admin / admin123
----------
##### Temel Uç Noktalar (Endpoints)
POST	/api/auth/register	Yeni kullanıcı kaydı oluşturur	Herkes
POST	/api/auth/login	Giriş yapar ve JWT döner	Herkes
GET	/api/users/role/{role}	Belirtilen role sahip kullanıcıları listeler	Sadece Admin

##### Proje Klasör Yapısı


src/main/java/com/kursaddcinar/minierp
├── common          # Ortak kullanılan yardımcı sınıflar (ApiResponse vb.)
├── config          # Konfigürasyon sınıfları (Security, OpenAPI, Cors)
├── controller      # HTTP isteklerini karşılayan sınıflar
├── dto             # Veri transfer objeleri (Request/Response modelleri)
├── entity          # Veritabanı tablolarına karşılık gelen sınıflar
├── exception       # Merkezi hata yönetimi
├── repository      # Veri erişim arayüzleri (Spring Data JPA)
├── security        # JWT filtreleri, UserDetailsService ve güvenlik mantığı
└── service         # İş kurallarının işletildiği servis katmanı

##### Gelecek Planları
[ ] Redis Entegrasyonu: Sık kullanılan verilerin (Cache) önbelleğe alınması.

[ ] Loglama: AOP (Aspect Oriented Programming) ile tüm işlemlerin loglanması.

[ ] Unit Test: JUnit ve Mockito ile birim testlerin yazılması.
