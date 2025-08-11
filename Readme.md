# Hotel booking service

Сервис бронирования номеров в отелях с интеграцией через Kafka (учебный проект).
Поддерживает создание бронирования, обработку событий оплаты (оплата имитирована) и отправку уведомлений.

## Стек технологий

- Java 21
- Spring Boot 3.5.4
- Spring Web
- Embedded Kafka (тесты)
- JUnit
- Mockito
- Maven
- PostgreSQL

---

## Схема взаимодействия
```mermaid
sequenceDiagram
    participant Client
    participant BookingService
    participant Kafka
    participant PaymentService

    Client->>BookingService: POST /api/booking
    BookingService->>Kafka: publish booking.created
    Kafka->>PaymentService: consume booking.created
    PaymentService-->>Kafka: publish payment.completed / payment.failed
    Kafka-->>BookingService: consume payment.completed / payment.failed
    BookingService->>Client: return booking status

## Запуск проекта

### 1. Клонировать репозиторий
git clone https://github.com/BicEv/HotelBooking.git

### 2.
запустить Kafka (если ее нет в системе)
docker-compose up -d

### 3.
собрать и запустить 
mvn clean package
java -jar target/hotel-booking-0.0.1-SNAPSHOT.jar

### 4.
запуск в профиле test
mvn test

## Конфигурация

В application.properties указаны настройки Kafka и PostgreSQL:

spring.application.name=hotel_booking
spring.datasource.url=jdbc:postgresql://localhost:5432/booking_db
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.admin.auto-create=true

## API

### Создание бронирования
POST /api/booking

Пример запроса:
{
    "userId": "2e7b2a6d-caf8-4c94-bb9f-347b204d2e29",
    "roomId": "40ae849e-415e-4e72-9ffb-54d12e39ec78",
    "checkIn": "2026-01-01",
    "cheсkOut": "2026-01-05",
    "amount": 100
}

Пример ответа:
{
  "id": "1a6b6c8a-cc21-4e2a-8d9e-52a930dfaa77",
  "userId": "2e7b2a6d-caf8-4c94-bb9f-347b204d2e29",
  "roomId": "40ae849e-415e-4e72-9ffb-54d12e39ec78",
  "checkIn": "2026-01-01",
  "checkOut": "2026-01-05",
  "amount": 100,
  "status": "NEW"
}

### Получение бронирования по UUID
GET /api/booking/{bookingId}

Пример запроса: /api/booking/1a6b6c8a-cc21-4e2a-8d9e-52a930dfaa77

Пример ответа:
{
  "id": "1a6b6c8a-cc21-4e2a-8d9e-52a930dfaa77",
  "userId": "2e7b2a6d-caf8-4c94-bb9f-347b204d2e29",
  "roomId": "40ae849e-415e-4e72-9ffb-54d12e39ec78",
  "checkIn": "2026-01-01",
  "checkOut": "2026-01-05",
  "amount": 100,
  "status": "CONFIRMED"
}

### Удаление бронирования по UUID
DELETE /api/booking/{bookingId}

Пример запроса: /api/booking/1a6b6c8a-cc21-4e2a-8d9e-52a930dfaa77

### Отмена бронирования по UUID
PATCH /api/booking/{bookingId}/cancel

Пример запроса: /api/booking/1a6b6c8a-cc21-4e2a-8d9e-52a930dfaa77/cancel

### Создание комнаты
POST /api/rooms

Пример запроса:
{
    "number": "1a",
    "type": "Luxe"
}

Пример ответа:
{
    "id": "40ae849e-415e-4e72-9ffb-54d12e39ec78",
    "number": "1a",
    "type": "Luxe"
}

### Получение комнаты по UUID
POST /api/rooms/{roomId}
Пример запроса:
/api/rooms/40ae849e-415e-4e72-9ffb-54d12e39ec78

Пример ответа:
{
    "id": "40ae849e-415e-4e72-9ffb-54d12e39ec78",
    "number": "1a",
    "type": "Luxe"
}

### Изменение комнаты
PUT /api/rooms

Пример запроса:
{
    "id": "40ae849e-415e-4e72-9ffb-54d12e39ec78",
    "number": "101",
    "type": "Regular"
}

Пример ответа:
{
    "id": "40ae849e-415e-4e72-9ffb-54d12e39ec78",
    "number": "101",
    "type": "Regular"
}

### Удаление комнаты по UUID
DELETE /api/rooms/{roomId}
Пример запроса:
/api/rooms/40ae849e-415e-4e72-9ffb-54d12e39ec78

## Лицензия
MIT License — свободно используйте и модифицируйте проект.