# plus-smart-home-tech

[![Java](https://img.shields.io/badge/Java-21-blue)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2023.0.3-green)](https://spring.io/projects/spring-cloud)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-42.7.3-blue)](https://www.postgresql.org/)
[![Apache Kafka](https://img.shields.io/badge/Apache%20Kafka-3.6.1-black)](https://kafka.apache.org/)
[![gRPC](https://img.shields.io/badge/gRPC-1.63.0-purple)](https://grpc.io/)

Система умного дома для сбора телеметрии, управления инфраструктурой и коммерческого учета ресурсов.

## 📋 Содержание
- [Архитектура](#-архитектура)
- [Технологический стек](#-технологический-стек)
- [Инфраструктурные сервисы (infra)](#-инфраструктурные-сервисы-infra)
- [Модуль telemetry](#-модуль-telemetry)
  - [Collector](#-collector--сбор-данных)
  - [Aggregator](#-aggregator--агрегация-событий)
  - [Analyzer](#-analyzer--анализ-и-сценарии)
  - [Serialization (Avro и Proto схемы)](#-serialization-avro-схемы)
- [Модуль commerce](#-модуль-commerce)
- [Быстрый старт](#-быстрый-старт)
- [Структура проекта](#-структура-проекта)


## 🏗️ Архитектура
```markdown
┌─────────────────────────────────────────────────────────────────────────────┐
│ INFRASTRUCTURE                                                              │
│ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐                             │
│ │ Config      │ │ Discovery   │ │ Gateway     │                             │
│ │ Server      │ │ Server      │ │ Server      │                             │
│ └─────────────┘ └─────────────┘ └─────────────┘                             │
└─────────────────────────────────────────────────────────────────────────────┘
                                       │ 
                                       ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│ TELEMETRY                                                                   │
│ ┌─────────────┐    ┌─────────────┐    ┌─────────────┐                       │
│ │ Collector   │───▶│ Kafka       │───▶│ Aggregator │                       │
│ │ (gRPC)      │    │ Sensors     │    │             │                       │
│ └─────────────┘    └─────────────┘    └──────┬──────┘                       │
│                                              │                              │
│                                              ▼                              │
│                                   ┌─────────────────────┐                   │
│                                   │ Kafka Snapshots     │                   │
│                                   └──────────┬──────────┘                   │
│                                              │                              │
│                                              ▼                              │
│                    ┌─────────────┐    ┌─────────────┐ ┌─────────────┐       │
│                    │ Analyzer    │◀───│ Kafka       │ │ PostgreSQL  │       │
│                    │ (gRPC)→Hub  │    │ Hubs        │ │ Scenarios   │       │
│                    └─────────────┘    └─────────────┘ └─────────────┘       │
└─────────────────────────────────────────────────────────────────────────────┘
                                     │
                                     ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│ COMMERCE                                                                    │
│ ┌───────────┐ ┌───────────┐ ┌───────────┐ ┌───────────┐                     │
│ │ Shopping  │ │ Order     │ │ Payment   │ │ Delivery  │                     │
│ │ Store     │ │           │ │           │ │           │                     │
│ └───────────┘ └───────────┘ └───────────┘ └───────────┘                     │
│ ┌───────────┐ ┌───────────┐ ┌───────────┐ ┌───────────┐                     │
│ │ Shopping  │ │ Warehouse │ │ Error     │ │Interaction│                     │
│ │ Cart      │ │           │ │ Handler   │ │API        │                     │
│ └───────────┘ └───────────┘ └───────────┘ └───────────┘                     │
└─────────────────────────────────────────────────────────────────────────────┘
```

## 🛠️ Технологический стек

### 🗄️ Базы данных
- PostgreSQL 42.7.3

### 🔌 Интеграции
- MapStruct 1.5.5.Final (маппинг DTO ↔ Entity)
- Lombok 1.18.32
- Jakarta Validation 3.0.2
- SpringDoc OpenAPI 2.6.0 (Swagger)

### 📡 Микросервисы & Messaging
- Spring Cloud Circuit Breaker (Resilience4j)
- Kafka Clients 3.6.1
- Avro 1.11.3
- gRPC 1.63.0 + Protobuf 3.23.4

## 📦 Модули проекта

### 1. `telemetry` — телеметрия и анализ
#### 1.1. `Collector` — сбор данных
Принимает события от датчиков и хабов через gRPC, конвертирует в Avro и отправляет в Kafka.

**gRPC эндпоинты:**
```protobuf
service CollectorController {
  rpc CollectSensorEvent (SensorEventProto) returns (Empty);
  rpc CollectHubEvent (HubEventProto) returns (Empty);
} 
```

**gRPC сервис для отправки команд:**
```protobuf
service HubRouterController {
    rpc handleDeviceAction (DeviceActionRequest) returns (Empty);
}
```

### Поддерживаемые типы датчиков:

- Motion Sensor (движение)
- Temperature Sensor (температура)
- Light Sensor (освещенность)
- Climate Sensor (температура, влажность, CO2)
- Switch Sensor (переключатель)

### 📋 Типы условий

| Тип (ConditionTypeAvro) | Описание | Источник данных |
|-------------|------|----------------|
| `MOTION` | `Движение` | `MotionSensorEventAvro.motion` |
| `LUMINOSITY` | `Освещённость` | `LightSensorEventAvro.luminosity` |
| `TEMPERATURE` | `Температура` | `ClimateSensorEventAvro.temperatureC` |
| `HUMIDITY` | `Влажность` | `ClimateSensorEventAvro.humidity` |
| `CO2LEVEL` | `Уровень CO2` | `ClimateSensorEventAvro.co2Level` |
| `SWITCH` | `Переключатель` | `SwitchSensorEventAvro.state` |

**Операции сравнения: EQUALS, GREATER_THAN, LOWER_THAN**

### Поддерживаемые действия:
| Тип  | Описание |
|-----------|------|
| `ACTIVATE` | `активировать устройство` |
|`DEACTIVATE`|`деактивировать устройство`|
|`INVERSE`|`инвертировать состояние`|
|`SET_VALUE`|`установить значение`|
#### Топики Kafka:

- `telemetry.sensors.v1` — события датчиков

- `telemetry.hubs.v1` — события хабов (добавление/удаление устройств, сценарии)

### Конвертеры gRPC → Avro:

**Сенсоры:**
- ClimateToAvroConverter
- TemperatureToAvroConverter
- LightToAvroConverter
- SwitchToAvroConverter
- MotionToAvroConverter

**Хабы:**
- DeviceAddedToAvroConverter
- DeviceRemoveToAvroConverter
- ScenarioAddedToAvroConverter
- ScenarioRemoveToAvroConverter

### Конфигурация
```yaml
grpc:
  server:
    port: 59091

spring:
  kafka:
    bootstrap-servers: localhost:9092
    topics:
      sensors-topic-name: telemetry.sensors.v1
      hubs-topic-name: telemetry.hubs.v1
```
#### 1.2. `Aggregator` — агрегация событий
**Сервис-агрегатор, потребляющий события датчиков из Kafka, поддерживающий актуальное состояние всех сенсоров и формирующий снапшоты.**

|Роль	| Топик	| Тип сообщения|
|-------|-------|--------------|
|`Входящий`|	`telemetry.sensors.v1`	| `SensorEventAvro`|
|`Исходящий` |	`telemetry.snapshots.v1`|	`SensorsSnapshotAvro`|

### Структура снапшота:
```
record SensorsSnapshotAvro {
    string hubId;                           // ID хаба
    long timestamp;                         // Время последнего обновления
    map<SensorStateAvro> sensorsState;      // Состояние всех сенсоров
}

record SensorStateAvro {
    long timestamp;                         // Время последнего события
    union {                                 // Данные сенсора
        ClimateSensorEventAvro,
        LightSensorEventAvro,
        MotionSensorEventAvro,
        SwitchSensorEventAvro,
        TemperatureSensorEventAvro
    } data;
}
```

### Логика работы:
* Подписка на топик telemetry.sensors.v1
* Получение события датчика
* Обновление состояния в Map<String, SensorsSnapshotAvro>
* При изменении состояния → отправка снапшота в telemetry.snapshots.v1

### Компоненты
|Класс|Ответственность|
|-----|---------------|
|`AggregationStarter`|Основной цикл обработки, polling Kafka|
|`CheckUpdateState`|Хранение и обновление состояния сенсоров|
|`SnapshotProducer`|Отправка снапшотов в Kafka|
|`KafkaConsumerConfig`|Настройка consumers|
|`KafkaSnapshotProducerConfig`|Настройка producer|

#### 1.3. `Analyzer` — анализ и сценарии
**Сервис-анализатор, потребляющий снапшоты и hub-события из Kafka, проверяющий условия сценариев и отправляющий команды на устройства через gRPC.**
### Топики Kafka
|Топик|	Тип сообщения	|Описание|
|-----|-----------------|--------|
|telemetry.snapshots.v1	|SensorsSnapshotAvro	|Снапшоты состояния сенсоров|
|telemetry.hubs.v1|	HubEventAvro	|События хабов (сценарии)|

### gRPC сервис для отправки команд
```protobuf
service HubRouterController {
    rpc handleDeviceAction (DeviceActionRequest) returns (Empty);
}
```
### Структура DeviceActionRequest
```
message DeviceActionRequest {
    string hub_id = 1;
    string scenario_name = 2;
    DeviceActionProto action = 3;
    google.protobuf.Timestamp timestamp = 4;
}

message DeviceActionProto {
    string sensor_id = 1;
    ActionTypeProto type = 2;
    optional int32 value = 3;
}
```

### 2. `infra`
Управление устройствами инфраструктуры (освещение, отопление, кондиционирование, доступ).
#### 2.1. `config-server` — централизованное управление конфигурациями

#### 2.2. `discovery-server` — Eureka для обнаружения сервисов

#### 2.3. `gateway-server` — API Gateway с маршрутизацией и ретраями

#### Маршруты Gateway

| ID маршрута | Путь | Целевой сервис | Преобразование |
|-------------|------|----------------|----------------|
| `shopping_cart_route` | `/shopping-cart/**` | `shopping-cart` | `/api/v1/shopping-cart/{segment}` |
| `shopping-store-route` | `/shopping-store/**` | `shopping-store` | `/api/v1/shopping-store/{segment}` |
| `warehouse-route` | `/warehouse/**` | `warehouse` | `/api/v1/warehouse/{segment}` |
| `delivery-route` | `/delivery/**` | `delivery` | `/api/v1/delivery/{segment}` |
| `order-route` | `/order/**` | `order` | `/api/v1/order/{segment}` |
| `payment-route` | `/payment/**` | `payment` | `/api/v1/payment/{segment}` |

**Пример обращения:**
```bash
# Внешний запрос
GET http://gateway:8080/shopping-cart/user123

# Преобразуется во внутренний
GET http://shopping-cart:8080/api/v1/shopping-cart/user123
```

### 3. `commerce` — коммерческий модуль (полный цикл заказа и оплаты)

#### 3.1. `shopping-store` — магазин товаров
- Управление каталогом товаров (`ProductDto`, `ProductDao`)
- Фильтрация по категориям (`ProductCategory`)
- Изменение состояния товара (`ProductState`: ACTIVE, DEACTIVATE)
- Управление остатками через `SetProductQuantityStateRequest`

#### 3.2. `shopping-cart` — корзина пользователя
- Добавление/удаление товаров, изменение количества
- Деактивация корзины (`ShoppingCartState.DEACTIVATED`)
- Проверка доступного количества через `WarehouseClient`

#### 3.3. `order` — управление заказами
- Создание заказа из корзины
- Интеграция с `WarehouseClient` (резервирование товаров)
- Интеграция с `PaymentClient` (расчет стоимости)
- Интеграция с `DeliveryClient` (создание доставки)
- Жизненный цикл заказа:
  - `ASSEMBLED`, `PAID`, `DELIVERED`, `COMPLETED`
  - Ошибки: `PAYMENT_FAILED`, `DELIVERY_FAILED`, `ASSEMBLY_FAILED`
- Возврат товаров на склад

#### 3.4. `payment` — оплата заказов
- Расчет стоимости товаров (с налогом `TAX = 10%`)
- Расчет итоговой стоимости (товары + доставка)
- Создание платежа (`PaymentDao`, `PaymentDto`)
- Подтверждение/отмена оплаты (`PaymentStatus.SUCCESS/FAILED`)

#### 3.5. `delivery` — доставка
- Расчет стоимости доставки (базовая + коэффициенты на хрупкость, вес, объем, адрес)
- Обновление статуса доставки:
  - `IN_PROGRESS`, `DELIVERED`, `CANCELLED`
- Интеграция с `OrderClient` и `WarehouseClient`

#### 3.6. `warehouse` — складской учет
- Добавление товаров на склад (`ProductInWarehouseDao`)
- Приемка товаров (`acceptProduct`)
- Резервирование товаров при сборке заказа (`assemblyProducts`)
- Проверка достаточности остатков для корзины
- Возврат товаров на склад
- Вычисление объема товара (ширина × глубина × высота)

#### 3.7. `error-handler` — централизованная обработка ошибок
- `@RestControllerAdvice` + `GlobalErrorHandler`
- Поддержка специфических исключений:
  - `NoDeliveryFoundException`, `NoOrderFoundException`, `PaymentNotFound`
  - `ProductLowQuantityInWarehouse`, `NoSpecifiedProductInWarehouseException`
  - `CartNotFoundException`, `DeactivatedCartException`
- Обработка `FeignException`, `HttpMessageNotReadableException`

## 🚀 Быстрый старт

### Требования
- Java 21
- Maven 3.8+
- PostgreSQL 15+
- Docker (опционально)

### Установка

```bash
git clone https://github.com/YrioTries/plus-smart-home-tech.git
```

### Запуск через Docker Compose

```bash
docker-compose up -d
```