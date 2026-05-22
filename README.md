# Plazoleta Soft

Proyecto de microservicios para un sistema de gestión de restaurantes y pedidos.

## Descripción

Este proyecto está compuesto por varios microservicios Java basados en Spring Boot:

- `ms-auth`: Autenticación y autorización de usuarios.
- `ms-notificaciones`: Gestión de notificaciones del sistema.
- `ms-pedidos`: Procesamiento y administración de pedidos.
- `ms-restaurante`: Gestión de restaurantes, platos y usuarios del restaurante.
- `ms-trazabilidad`: Trazabilidad de eventos del sistema.

También incluye un `docker-compose.yml` para orquestar los servicios.

## Estructura

- `docker-compose.yml`: Definición de servicios en contenedores.
- `ms-auth/`, `ms-notificaciones/`, `ms-pedidos/`, `ms-restaurante/`, `ms-trazabilidad/`: proyectos Maven independientes.

## Requisitos

- Java 17+ 
- Maven 3.6+ 
- Docker y Docker Compose (para despliegue con contenedores)

## Uso

### Ejecutar con Maven

Cada microservicio se puede ejecutar individualmente desde su carpeta:

```bash
cd ms-auth && mvn spring-boot:run
```

Repetir para los demás microservicios según sea necesario.

### Ejecutar con Docker Compose

```bash
docker-compose up --build
```

## GitHub

Repositorio remoto: `https://github.com/GamaYuto/Plazoleta_Soft.git`
