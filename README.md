# Library Management Backend

API REST desarrollada con Java 17 y Spring Boot para la gestión de una biblioteca.

Permite administrar usuarios, libros, ejemplares y préstamos, aplicando reglas de negocio para disponibilidad de ejemplares y control de préstamos.

---

## Tecnologías

- Java 17
- Spring Boot 3.5.4
- Spring Data JPA
- Spring Web
- Bean Validation
- MySQL 8.4
- Maven Wrapper
- Docker y Docker Compose
- Lombok

---

## Ejecución

### 1. Copiar el archivo de configuración

Copie `.env.example` como `.env` y reemplace las contraseñas de ejemplo.

### 2. Levantar los contenedores

```powershell
docker compose up -d --build
```

### 3. Restaurar los datos de prueba

```powershell
docker compose cp database/library_test.dump mysql:/tmp/library_test.dump
docker compose exec -T mysql sh -c 'mysql -u root -p"$MYSQL_ROOT_PASSWORD" < /tmp/library_test.dump'
```

### 4. Verificar la API

```powershell
curl.exe http://localhost:8080/api/usuarios
```

La API estará disponible por defecto en `http://localhost:8080`. El puerto externo se puede cambiar mediante `BACKEND_PORT` en `.env`.

Para detener los servicios:

```powershell
docker compose down
```

---

## Variables de entorno

Ejemplo del .env:

```env
# MySQL container configuration
MYSQL_DATABASE=library_test
MYSQL_ROOT_PASSWORD=12345
DB_PORT=3306
BACKEND_PORT=8080

# Spring Boot datasource configuration
DB_URI=jdbc:mysql://localhost:3306/library_test
DB_USER=library_user
DB_PASSWORD=12345
DB_DRIVER=com.mysql.cj.jdbc.Driver
```

| Variable | Descripción |
| --- | --- |
| `MYSQL_DATABASE` | Nombre de la base de datos MySQL |
| `MYSQL_ROOT_PASSWORD` | Contraseña del usuario root de MySQL |
| `DB_PORT` | Puerto publicado para MySQL |
| `BACKEND_PORT` | Puerto publicado para la API |
| `DB_URI` | URL JDBC utilizada por Spring Boot en ejecución local |
| `DB_USER` | Usuario de la base de datos |
| `DB_PASSWORD` | Contraseña del usuario de aplicación |
| `DB_DRIVER` | Driver JDBC |

---

## Funcionalidades

### Usuarios

- Crear usuario
- Listar usuarios
- Consultar usuario por ID
- Actualizar usuario
- Eliminar usuario

### Libros

- Crear libro
- Listar libros
- Consultar libro por ID
- Actualizar libro
- Eliminar libro

### Ejemplares

- Crear ejemplares
- Listar ejemplares de un libro
- Consultar ejemplares disponibles por libro
- Consultar ejemplares disponibles por ISBN
- Eliminar ejemplares sin préstamos pendientes ni históricos asociados

### Préstamos

- Registrar préstamo
- Registrar devolución
- Consultar préstamos por usuario
- Consultar préstamos por libro

---

## Estados del préstamo

El estado se calcula dinámicamente según las fechas y la devolución registrada, usando la zona horaria `America/Bogota`.

- **ACTIVO**
- **VENCIDO**
- **DEVUELTO**

---

## Datos de prueba

El directorio `database/` contiene:

- `library_test.dump`: respaldo de la base de datos con datos de prueba.
- `seed.ps1`: script opcional para regenerar los datos utilizando la API REST.
- `README.md`: documentación relacionada con los datos de prueba.

Consulta [database/README.md](database/README.md) para el procedimiento completo de seed, exportación y restauración.

---

## Endpoints principales

### Usuarios

```text
POST   /api/usuarios
GET    /api/usuarios
GET    /api/usuarios/{id}
PUT    /api/usuarios/{id}
DELETE /api/usuarios/{id}
```

### Libros

```text
POST   /api/libros
GET    /api/libros
GET    /api/libros/{id}
PUT    /api/libros/{id}
DELETE /api/libros/{id}
```

### Ejemplares

```text
POST   /api/libros/{libroId}/ejemplares
GET    /api/libros/{libroId}/ejemplares
GET    /api/libros/{libroId}/ejemplares/disponibles
GET    /api/libros/isbn/{isbn}/ejemplares/disponibles
GET /api/ejemplares/{id}
DELETE /api/ejemplares/{id}
```

### Préstamos

```text
POST   /api/prestamos
GET    /api/prestamos/usuario/{usuarioId}
GET    /api/prestamos/libro/{libroId}
PATCH  /api/prestamos/{id}/devolver
```

---

## Decisiones técnicas

- Arquitectura por capas: Controller, Service y Repository.
- DTOs para desacoplar la API de las entidades JPA.
- Mappers dedicados para conversiones entre entidades y DTOs.
- Bean Validation para validaciones de entrada.
- Bloqueos pesimistas para evitar inconsistencias en préstamos concurrentes.
- Dockerfile multi-stage para reducir el tamaño de la imagen final.
- Variables de entorno para la configuración del despliegue.

---

## Verificación previa a la entrega

```powershell
.\mvnw.cmd test
.\mvnw.cmd clean package
```
