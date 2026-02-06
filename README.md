
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

## 📦 Kurulum

### Backend

1. Docker-Compose ayağa kaldırın.
```bash
docker compose up -d
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
