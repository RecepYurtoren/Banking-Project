# Banking System

Spring Boot 3 ve React ile geliştirilmiş kapsamlı bir bankacılık sistemi.

## 🏗️ Proje Yapısı

```
├── backend/                    # Spring Boot Backend
│   ├── src/main/java/com/banking/
│   │   ├── model/             # Entity sınıfları (domain)
│   │   ├── repository/        # JPA Repository interface'leri
│   │   ├── service/           # Service interface'leri
│   │   │   └── impl/          # Service implementasyonları
│   │   ├── controller/        # REST Controller'lar
│   │   ├── dto/               # Data Transfer Objects (Record)
│   │   ├── exception/         # Exception handling
│   │   └── config/            # Konfigürasyon (CORS, ModelMapper)
│   └── src/test/              # Unit testler
│
└── frontend/                   # React Frontend
    └── src/
        ├── api/               # Axios API istemcileri
        └── components/        # React bileşenleri
```

## 🚀 Özellikler

### Backend
- **Model Katmanı**: Abstract `Account` sınıfı, `SavingsAccount` ve `CheckingAccount` implementasyonları
- **Polimorfik withdraw()**: Checking için limit kontrolü, Savings için bakiye kontrolü
- **Transferable Interface**: Transfer işlemleri için interface
- **Interface-Implementation Ayrımı**: Tüm service'ler soyut (interface) ve somut (impl) olarak ayrılmış
- **ModelMapper**: DTO dönüşümleri için ModelMapper kullanımı
- **Transaction Logging**: Tüm işlemler veritabanına kaydedilir
- **Faiz Hesaplama**: Birikim hesapları için aylık faiz hesaplama ve uygulama
- **Aylık Raporlar**: Detaylı aylık hesap raporları
- **Record DTO'lar**: Controller'larda Entity yerine Record tipi DTO'lar kullanılır
- **JUnit Testler**: Kapsamlı unit test coverage

### Frontend
- **React + Vite**: Modern ve hızlı geliştirme ortamı
- **Tailwind CSS**: Özelleştirilmiş tema ile modern tasarım
- **Axios**: Backend API iletişimi
- **React Hot Toast**: Bildirimler

## 🛠️ Teknolojiler

### Backend
- Java 17
- Spring Boot 3.2.2
- Spring Data JPA
- PostgreSQL
- ModelMapper 3.2.0
- Lombok
- JUnit 5

### Frontend
- React 18
- Vite
- Tailwind CSS
- Axios
- React Icons
- React Hot Toast

## 📦 Kurulum

### Backend

1. PostgreSQL veritabanı oluşturun:
```sql
CREATE DATABASE banking_db;
```

2. Backend'i çalıştırın:
```bash
cd backend
./mvnw spring-boot:run
```

Veya Maven ile:
```bash
cd backend
mvn spring-boot:run
```

### Frontend

1. Bağımlılıkları yükleyin:
```bash
cd frontend
npm install
```

2. Geliştirme sunucusunu başlatın:
```bash
npm run dev
```

Frontend `http://localhost:3000` adresinde çalışacaktır.

## 🌐 API Endpoints

### Accounts
- `POST /api/accounts/savings` - Birikim hesabı oluştur
- `POST /api/accounts/checking` - Vadesiz hesap oluştur
- `GET /api/accounts` - Tüm hesapları listele
- `GET /api/accounts/{id}` - Hesap detayı
- `POST /api/accounts/{id}/deposit` - Para yatır
- `POST /api/accounts/{id}/withdraw` - Para çek
- `POST /api/accounts/transfer` - Havale yap

### Transactions
- `GET /api/transactions/account/{accountId}` - Hesap işlemleri
- `GET /api/transactions/account/{accountId}/monthly` - Aylık işlemler
- `GET /api/transactions/reference/{referenceNumber}` - İşlem detayı

### Reports
- `GET /api/reports/monthly/{accountId}` - Aylık rapor
- `GET /api/reports/interest/calculate/{accountId}` - Faiz hesaplama (önizleme)
- `POST /api/reports/interest/apply/{accountId}` - Faiz uygula

## 🏛️ Mimari Yapı

### Service Katmanı (Interface + Implementation)

```
service/
├── AccountService.java           # Account service interface
├── TransactionService.java       # Transaction service interface
├── InterestService.java          # Interest service interface
├── ReportService.java            # Report service interface
└── impl/
    ├── AccountServiceImpl.java       # Account service implementation
    ├── TransactionServiceImpl.java   # Transaction service implementation
    ├── InterestServiceImpl.java      # Interest service implementation
    └── ReportServiceImpl.java        # Report service implementation
```

### ModelMapper Kullanımı

```java
@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
            .setMatchingStrategy(MatchingStrategies.STRICT)
            .setSkipNullEnabled(true);
        return modelMapper;
    }
}
```

## 🚂 Railway Deployment

`application.properties` dosyası Railway ortam değişkenleriyle uyumludur:

```properties
server.port=${PORT:8080}
spring.datasource.url=${DATABASE_URL:jdbc:postgresql://localhost:5432/banking_db}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:postgres}
```

## 🧪 Testler

Backend testlerini çalıştırmak için:
```bash
cd backend
./mvnw test
```

## 📝 Hesap Türleri

### Birikim Hesabı (SavingsAccount)
- Minimum bakiye gereksinimi
- Yıllık faiz oranı
- Minimum bakiyenin altına düşecek çekimler engellenir

### Vadesiz Hesap (CheckingAccount)
- Kredi limiti (overdraft)
- Aylık bakım ücreti
- Negatif bakiyeye izin verilir (limite kadar)

## 📄 Lisans

MIT License
