# plus-smart-home-tech

[![Java](https://img.shields.io/badge/Java-21-blue)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2023.0.3-green)](https://spring.io/projects/spring-cloud)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-42.7.3-blue)](https://www.postgresql.org/)

Система умного дома для сбора телеметрии, управления инфраструктурой и коммерческого учета ресурсов.

## 🏗️ Архитектура

### Инфраструктурные сервисы (`infra`)
- **Config Server** — централизованное управление конфигурациями
- **Discovery Server (Eureka)** — сервис регистрации и обнаружения микросервисов
- **Gateway Server** — API Gateway для маршрутизации запросов

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

### 1. `telemetry`
Сбор, хранение и визуализация данных с датчиков (температура, влажность, освещенность, движение).

### 2. `infra`
Управление устройствами инфраструктуры (освещение, отопление, кондиционирование, доступ).

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
git clone https://github.com/your-org/plus-smart-home-tech.git
cd plus-smart-home-tech