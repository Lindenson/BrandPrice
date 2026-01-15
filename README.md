# Servicio de Precios de Marca


![Coverage](https://img.shields.io/badge/coverage-100%25-brightgreen)
###### measured by JaCoCo

## Disclaimer

De acuerdo con los requisitos del proyecto:
No se ha realizado un JOIN con la tabla BRANDS para obtener los nombres de las marcas.
No se han utilizado frameworks como Spring Data o Hibernate; se optó por consultas SQL de bajo nivel para optimizar el acceso a la base de datos.
Si se requiere, se pueden hacer cambios posteriores para incluir estas funcionalidades o frameworks adicionales.

## Descripción del Proyecto

Este proyecto implementa un servicio REST en **Spring Boot 4.0.1** que permite consultar el precio final de un producto en una cadena de tiendas para un rango de fechas determinado, siguiendo los criterios de prioridad y tarifas definidos en la tabla `PRICES` de ejemplo.

### Tabla de ejemplo `PRICES`:

| BRAND_ID | START_DATE          | END_DATE            | PRICE_LIST | PRODUCT_ID | PRIORITY | PRICE | CURR |
|----------|-------------------|-------------------|------------|------------|----------|-------|------|
| 1        | 2020-06-14 00:00:00 | 2020-12-31 23:59:59 | 1          | 35455      | 0        | 35.50 | EUR  |
| 1        | 2020-06-14 15:00:00 | 2020-06-14 18:30:00 | 2          | 35455      | 1        | 25.45 | EUR  |
| 1        | 2020-06-15 00:00:00 | 2020-06-15 11:00:00 | 3          | 35455      | 1        | 30.50 | EUR  |
| 1        | 2020-06-15 16:00:00 | 2020-12-31 23:59:59 | 4          | 35455      | 1        | 38.95 | EUR  |

**Campos:**
- `BRAND_ID`: Identificador de la cadena (1 = ZARA).
- `START_DATE`, `END_DATE`: Rango de fechas de validez del precio.
- `PRICE_LIST`: Identificador de la tarifa aplicable.
- `PRODUCT_ID`: Código del producto.
- `PRIORITY`: Prioridad para desambiguar entre tarifas superpuestas.
- `PRICE`: Precio final de venta.
- `CURR`: Moneda (ISO 4217).

---

## Funcionalidad

El servicio REST provee un endpoint:


**Parámetros de entrada:**
- `date` (fecha de aplicación)
- `productId` (identificador del producto)
- `brandId` (identificador de la cadena)

**Salida esperada:**
- `productId`
- `brandId`
- `priceList`
- `startDate`
- `endDate`
- `price`
- `currency`

El servicio retorna la tarifa aplicable siguiendo la prioridad indicada en la tabla.

---
## API REST – Consulta de Precio Final

### Endpoint:
```json
GET /prices/final
```

#### Parámetros de Entrada (query parameters)

| Nombre      | Tipo     | Descripción                                                                 | Obligatorio |
|------------|----------|-----------------------------------------------------------------------------|------------|
| `date`     | string   | Fecha y hora de aplicación del precio (ISO 8601, ej. `2020-06-14T10:00:00`) | Sí |
| `productId`| long     | Identificador del producto, productId > 0                                   | Sí |
| `brandId`  | long     | Identificador de la cadena/brand, brandId > 0                               | Sí |

---
#### Ejemplo

```json
curl "http://localhost:8080/prices/final?date=2020-06-14T10:00:00&productId=XXX&brandId=XXX"
```

1. Respuesta exitosa (200 OK)

```json
   {
   "productId": 35455,
   "brandId": 1,
   "priceList": 1,
   "startDate": "2020-06-14T00:00:00",
   "endDate": "2020-12-31T23:59:59",
   "price": 35.50,
   "curr": "EUR"
   }
```

---
2. Errores posibles
   
- Precio no encontrado
```
HTTP Status: 200 OK, body: null
```
- Parámetro inválido
```
HTTP Status: 400 Bad Request
```
#### Ejemplo:
```json
{
  "error": "Bad Request",
  "fields": {
      "productId": "must be greater than or equal to 0"
  },
  "message": "Constraint violation",
  "status": 400,
  "timestamp": "2026-01-15T11:41:04Z"
}
```
- Error de base de datos / interno
```
HTTP Status: 500 Internal Server Error
```
---

## Headers de respuesta
- Cache-Control: no-cache
- X-Request-ID: <ID único por request>

---
## Tecnologías y Decisiones de Diseño

- **Spring Boot**: Framework principal para el servicio REST y configuración de la aplicación.
- **H2 en memoria**: Base de datos ligera para inicialización de datos de ejemplo y pruebas.
- **JDBC con NamedParameterJdbcTemplate**: Se optó por consultas de bajo nivel en vez de Spring Data o Hibernate, para tener control total sobre la optimización de las consultas y ordenamiento por prioridad.
- **Arquitectura hexagonal mínima**: Separación de capas `port-in` (casos de uso), `port-out` (repositorio) y adaptadores (`in` y `out`) para mantener flexibilidad y testabilidad.
- **Lombok**: Utilizado con *annotation processing* para reducir código boilerplate (constructores, getters/setters, equals/hashCode), mejorando la legibilidad y mantenibilidad del código.
- **MapStruct**: Empleado para la generación automática de mappers entre entidades y DTOs en tiempo de compilación, garantizando alto rendimiento y evitando reflexión en tiempo de ejecución.
- **Logback**: Sistema de logging configurado con appender rotativo, para capturar eventos importantes de ejecución y errores.
- **Pruebas**:
    - Unitarias para validar creación de entidades y excepciones.
    - Integración con MockMvc para verificar los cinco escenarios solicitados.
    - Cobertura medida con **JaCoCo**.

---

## Pruebas

Se validaron los siguientes escenarios según los datos de ejemplo:

1. **14-06-2020 10:00** → Product 35455, Brand 1 → `priceList=1`, `price=35.50 EUR`
2. **14-06-2020 16:00** → Product 35455, Brand 1 → `priceList=2`, `price=25.45 EUR`
3. **14-06-2020 21:00** → Product 35455, Brand 1 → `priceList=1`, `price=35.50 EUR`
4. **15-06-2020 10:00** → Product 35455, Brand 1 → `priceList=3`, `price=30.50 EUR`
5. **16-06-2020 21:00** → Product 35455, Brand 1 → `priceList=4`, `price=38.95 EUR`

Todos los tests se ejecutan sobre la base de datos H2 en memoria, inicializada al arranque de la aplicación con los datos de ejemplo.

---

## Ejecución

1. Clonar el repositorio
```bash
git clone https://github.com/Lindenson/BrandPrice.git
cd BrandPrice
mvn clean install
mvn spring-boot:run

curl "http://localhost:8080/prices/final?date=2020-06-14T10:00:00&productId=35455&brandId=1" 
```

2. H2 Console
```bash
http://localhost:8080/h2-console
```

## Estructura del proyecto
```
src/
├─ main/java/com/wolper/prices # Código principal de la aplicación
├─ test/java/com/wolper/prices/* # Unit tests, and...
└─ test/java/com/wolper/prices/it # Integración tests
```
 


