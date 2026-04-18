# Proyecto Piedra Azul — Versión Microservicios

Sistema de gestión de citas médicas migrado a arquitectura de microservicios con Spring Boot, RabbitMQ y Docker.

---

## Requisitos previos

Instalar Docker Desktop para levantar todos los contenedores:
https://www.docker.com

Para correr el frontend JavaFX en tu PC instalar JDK 17:
https://adoptium.net/es/temurin/releases?version=17

Extensiones recomendadas en VS Code:
- Extension Pack for Java
- JavaFX Support
- Spring Boot Extension Pack
- Database Client (para ver las bases de datos)

Scene Builder para editar las vistas FXML:
https://gluonhq.com/products/scene-builder/

Después de instalar Scene Builder, en VS Code buscar:
`>SceneBuilder: Configure Path` y seleccionar el ejecutable.

---

## Estructura del proyecto

```
ProyectoPiedraAzulMicroServicios/
├── microservicio_usuarios/       → Gestión de personas, usuarios y autenticación  (puerto 8081)
├── microservicio_agendamiento/   → Citas y disponibilidad horaria                 (puerto 8082)
├── microservicio_configuracion/  → Jornadas laborales y especialidades            (puerto 8083)
├── api_gateway/                  → Punto de entrada único, valida JWT             (puerto 8080)
├── frontend/                     → Interfaz JavaFX, se conecta al gateway
└── docker-compose.yml            → Levanta todo el sistema
```

---

## Levantar el sistema por primera vez

Abrir una terminal en la carpeta `ProyectoPiedraAzulMicroServicios` y ejecutar:

```bash
docker-compose up --build -d
```

El `--build` compila todos los microservicios dentro de Docker.
El `-d` los corre en segundo plano.

Esto levanta:
- 3 bases de datos PostgreSQL (puertos 5433, 5434, 5435)
- RabbitMQ con panel web (puerto 5672 / panel en 15672)
- microservicio_usuarios (puerto 8081)
- microservicio_agendamiento (puerto 8082)
- microservicio_configuracion (puerto 8083)
- api_gateway (puerto 8080)

---

## Comandos del día a día

Apagar todos los contenedores:
```bash
docker-compose down
```

Encender los contenedores (sin recompilar):
```bash
docker-compose up -d
```

Ver los logs de un microservicio:
```bash
docker logs microservicio_usuarios
docker logs microservicio_agendamiento
docker logs microservicio_configuracion
```

Ver si todos los contenedores están corriendo:
```bash
docker ps
```

---

## Panel de administración RabbitMQ

Una vez levantado el sistema, entrar a:
http://localhost:15672

Usuario: `admin`
Contraseña: `admin123`

Desde ahí se pueden ver los exchanges, queues y mensajes en tiempo real.

---

## Documentación de las APIs (Swagger)

Con los contenedores corriendo, abrir en el navegador:

- Usuarios:       http://localhost:8081/swagger-ui/index.html
- Agendamiento:   http://localhost:8082/swagger-ui/index.html
- Configuración:  http://localhost:8083/swagger-ui/index.html

---

## Correr el frontend

El frontend JavaFX se corre directamente desde VS Code o IntelliJ (no va en Docker).
Asegurarse de tener JDK 17 instalado y los contenedores levantados antes de abrirlo.

El frontend se conecta al gateway en: `http://localhost:8080`
