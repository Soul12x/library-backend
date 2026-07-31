# Datos de prueba

Este directorio contiene los datos de prueba utilizados para evaluar la aplicación.

## Archivos

- `library_test.dump`: respaldo de la base de datos con los datos de prueba.
- `seed.ps1`: script opcional para regenerar los datos utilizando únicamente la API REST.
- `README.md`: instrucciones de uso.

## Contenido del dump

El respaldo incluye:

- 4 usuarios (uno sin préstamos)
- 4 libros (uno sin ejemplares)
- 8 ejemplares
- 4 préstamos:
  - 1 ACTIVO
  - 1 VENCIDO
  - 2 DEVUELTOS

## Restaurar el dump

Levante MySQL:

```powershell
docker compose up -d mysql
```

Copie el dump al contenedor:

```powershell
docker cp .\database\library_test.dump <mysql-container>:/tmp/library_test.dump
```

Restáurelo:

```powershell
docker compose exec mysql sh -c 'mysql -u root -p"$MYSQL_ROOT_PASSWORD" < /tmp/library_test.dump'
```

## Regenerar los datos

Si desea generar nuevamente los datos de prueba:

```powershell
.\database\seed.ps1
```

Después genere un nuevo respaldo:

```powershell
docker compose exec mysql sh -c 'mysqldump -u root -p"$MYSQL_ROOT_PASSWORD" --databases "$MYSQL_DATABASE" --routines --triggers --single-transaction --set-gtid-purged=OFF' > database/library_test.dump
```