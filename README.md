# Sistema Web de Gestión Integrada del CRAI — UPeU Tarapoto

Proyecto articulado de los cursos **Análisis y Diseño de Sistemas**, **Lenguaje de Programación** y **Administración de Base de Datos**. Gestiona el catálogo de libros, los préstamos, la reserva grupal de salas de estudio y el control de ingresos del CRAI.

## Estructura

```
backend/    API REST · Spring Boot 3.4 · Java 21
frontend/   SPA · Angular 18 · TypeScript
docs/       Documento de análisis y diseño, guías y notas de despliegue
```

## Tecnologías

| Capa | Tecnología |
| --- | --- |
| Frontend | Angular 18, TypeScript, signals, interceptores y guards funcionales |
| Backend | Spring Boot 3.4, Spring Security + JWT, Spring Data JPA, JdbcTemplate |
| Base de datos | Oracle XE 21c: esquema `CRAI` (dueño) y usuario de aplicación `US_CRAI` (mínimo privilegio) |

## Base de datos

Los scripts están en `backend/src/main/resources/db/` y se ejecutan en este orden:

1. `00_system_crear_usuarios.sql` — como SYSTEM: crea `CRAI` y `US_CRAI`.
2. Crear las tablas desde las entidades JPA conectado como `CRAI` (una sola vez).
3. `sp_registrar_prestamo.sql` — como CRAI.
4. `01_usuario_aplicacion_us_crai.sql` — como CRAI: vista y permisos de `US_CRAI`.
5. `02_datos_iniciales.sql` — como CRAI.
6. `03_demo_minimo_privilegio.sql` — como US_CRAI: demostración de permisos.

## Variables de entorno del backend

Las credenciales no se guardan en el repositorio. Configúralas en la terminal o en la configuración de ejecución del IDE:

| Variable | Valor |
| --- | --- |
| `CRAI_DB_PASSWORD` | Contraseña de `US_CRAI` en Oracle |
| `CRAI_JWT_SECRET` | Clave Base64 privada para firmar el JWT |
| `CRAI_DB_URL` | Opcional. Por defecto `jdbc:oracle:thin:@localhost:1521/xe`; para Oracle en otra máquina, `jdbc:oracle:thin:@//IP:1521/xe` |

## Cómo ejecutarlo

**Modo desarrollo**

```bash
# backend/
./mvnw spring-boot:run                       # http://localhost:8082/crai

# frontend/
npm install
ng serve --proxy-config proxy.config.json    # http://localhost:4200
```

**Modo despliegue (un solo .jar)**

```bash
# frontend/
ng build --base-href /crai/
# copiar el contenido de frontend/dist/pcrai/browser/ a backend/src/main/resources/static/

# backend/
./mvnw clean package -Dmaven.test.skip=true
java -jar target/crai-0.0.1-SNAPSHOT.jar     # http://IP:8082/crai/
```

Para exponerlo fuera de la red local: `cloudflared tunnel --url http://localhost:8082` y abrir la URL generada con `/crai/` al final.

## Equipo

| Integrante | Rama | Rol |
| --- | --- | --- |
| Nimer | `main` | Backend y base de datos · revisa y aprueba los Pull Requests |
| Matthew | `dev_matthew` | Backend y base de datos |
| Jhosmer | `dev_jhosmer` | Frontend y despliegue |
| Steeward | `dev_steeward` | Frontend y documentación |

## Forma de trabajo

- `main` está protegida: nadie sube directamente.
- Cada integrante trabaja en su rama `dev_<nombre>` y abre un **Pull Request** hacia `main`, que Nimer revisa y aprueba.
- Mensajes de commit con prefijo: `feat:` funcionalidad, `fix:` corrección, `docs:` documentación, `chore:` configuración.

## Historial

Los primeros commits del backend (19/11/2026) fueron reconstruidos a partir de la versión final para mostrar las etapas de construcción del sistema. Desde la reorganización en `backend/` y `frontend/`, cada commit corresponde al integrante que hizo el cambio.
