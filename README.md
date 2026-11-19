# Backend CRAI

Este historial fue reconstruido a partir de la versión final del proyecto para la
sustentación de LP. Los commits intermedios representan etapas funcionales
inferidas por dependencias. Las fechas indican cuándo se hizo la reconstrucción.
El equipo confirmó que Spring Security, BCrypt y JWT se integraron al final.

## Revisar el historial

```sh
git log --reverse --oneline
git show --stat HEAD
```

## Ejecutar la versión final

Se requiere Java 21 y el entorno Oracle XE del proyecto. La aplicación escucha
en el puerto 8082 y usa el prefijo /crai. Configura las siguientes variables en
la terminal o en la configuración de ejecución del IDE antes de iniciar Spring.

| Variable | Valor |
|---|---|
| `CRAI_DB_PASSWORD` | Contraseña de la cuenta CRAI en Oracle |
| `CRAI_JWT_SECRET` | Clave Base64 privada usada para firmar el JWT |

Los valores locales están en la copia original del proyecto. Conserva la misma
configuración para reproducir el entorno existente. En Windows puedes usar
`mvnw.cmd` y en Linux o macOS puedes usar `./mvnw` para ejecutar
`spring-boot:run`. El wrapper necesita acceso a Maven Central la primera vez.

El procedimiento `src/main/resources/db/sp_registrar_prestamo.sql` se instala
en Oracle. La vista de reportes y los triggers del entorno Oracle se conservan
en la base de datos de la instalación original.

Los commits anteriores al último se conservan para explicar la construcción
del sistema. Usa la versión final para la demostración con JWT.
