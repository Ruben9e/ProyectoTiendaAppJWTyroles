# TiendaApp — API REST con JWT y roles

API de tienda para administrar usuarios, categorías, productos y pedidos. Está hecha con Java 17+, Spring Boot, JPA, MySQL, jjwt y BCrypt. La seguridad se implementa con un `OncePerRequestFilter` y un `HandlerInterceptor`; **no se usa Spring Security completo**.

## Requisitos

- JDK 17 o superior y Maven 3.8+.
- MySQL 8 en ejecución.
- OpenSSL para generar la clave JWT (en Windows se puede usar Git Bash o WSL).

## Preparar y ejecutar

1. Crea la base de datos y las tablas, e inserta roles y ADMIN:

   ```sql
   SOURCE C:/ruta/al/proyecto/tienda.sql;
   ```

   O abre y ejecuta `tienda.sql` desde MySQL Workbench. Si ya hay tablas, revisa el script antes de ejecutarlo; utiliza `IF NOT EXISTS` y `INSERT IGNORE`.

2. Desde la raíz del proyecto configura las variables. En PowerShell:

   ```powershell
   $bytes = New-Object byte[] 32
   [System.Security.Cryptography.RandomNumberGenerator]::Fill($bytes)
   $env:JWT_SECRET = [Convert]::ToBase64String($bytes)
   $env:JWT_EXPIRATION_MS = "600000"
   $env:DB_URL = "jdbc:mysql://localhost:3306/tienda_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC"
   $env:DB_USERNAME = "root"
   $env:DB_PASSWORD = ""
   ```

   `JWT_SECRET` es obligatorio y no debe publicarse ni guardarse en Git. La expiración predeterminada es 10 minutos. Las variables solo viven en esa ventana de PowerShell.

3. Inicia la API:

   ```powershell
   mvn spring-boot:run
   ```

   URL base: `http://localhost:9090/api/v1/tienda`.

4. Credenciales del ADMIN inicial: `admin` / `password`. Esta clave predeterminada es solo para desarrollo/entrega local; antes de publicar el servicio, reemplaza el hash bcrypt del usuario inicial en la BD por el hash de una clave robusta. La API nunca expone contraseñas.

5. Si deseas, importa `postman/TiendaApp.postman_collection.json` en Insomnia (Import/Export → Import Data → From File). También puedes crear las solicitudes manualmente usando la sección Insomnia de abajo. Los scripts de Postman pueden requerir ajuste al importarlos; para las evidencias, guarda los tokens copiándolos a las variables del Environment.

## Configuración de Insomnia y evidencias

1. Crea un Environment (por ejemplo, `Local`) en Manage Environments con este JSON. Los IDs se completan después de crear cada recurso:

   ```json
   {
     "base_url": "http://localhost:9090/api/v1/tienda",
     "admin_token": "",
     "seller_token": "",
     "second_seller_token": "",
     "client_token": "",
     "category_id": "",
     "seller_id": "",
     "product_id": "",
     "order_id": ""
   }
   ```

2. Usa `{{ _.base_url }}` al principio de cada URL. En Insomnia, crea un folder **Auth** y agrega:

   **Login ADMIN** — `POST {{ _.base_url }}/auth/login`, body JSON:

   ```json
   {"user":"admin","password":"password"}
   ```

   Copia `data.token` de la respuesta en `admin_token` del Environment. En una solicitud protegida, en la pestaña Auth selecciona **Bearer Token** y escribe `{{ _.admin_token }}`. No escribas el prefijo en el campo Bearer Token; Insomnia lo agrega. Para solicitudes de vendedor/cliente cambia la variable por `{{ _.seller_token }}` o `{{ _.client_token }}` según corresponda.

3. Envía estas solicitudes en orden; revisa la respuesta y copia cada `data.id` a la variable indicada:

   - **Crear categoría (ADMIN):** `POST {{ _.base_url }}/categories`, Authorization Bearer, `{"name":"Tecnologia"}`. Copia el ID a `category_id`.
   - **Crear vendedor (ADMIN):** `POST {{ _.base_url }}/users`, Authorization Bearer, `{"username":"vendedor1","email":"vendedor1@example.com","password":"Vendedor123!","role":"VENDEDOR"}`. Copia el ID a `seller_id`.
   - Haz Login con `{"user":"vendedor1","password":"Vendedor123!"}` y copia `data.token` a `seller_token`.
   - **Crear producto (VENDEDOR):** `POST {{ _.base_url }}/products`, Authorization Bearer, `{"name":"Teclado","description":"Teclado USB","price":25.50,"stock":10,"categoryId":{{ _.category_id }}}`. Copia el ID a `product_id`.
   - Registra un cliente con `POST {{ _.base_url }}/auth/register`, body `{"username":"cliente1","email":"cliente1@example.com","password":"Cliente123!"}`. Luego haz Login con `{"user":"cliente1","password":"Cliente123!"}` y copia `data.token` a `client_token`.
   - **Crear pedido (CLIENTE):** `POST {{ _.base_url }}/orders`, Authorization Bearer, `{"productId":{{ _.product_id }},"quantity":2}`. Copia el ID a `order_id`.

   Los cuerpos deben enviarse como JSON (`application/json`). Conserva el token solo en el Environment local; no lo adjuntes en capturas ni lo subas a GitHub.

4. Prepara los casos de evidencia:

   - **Login correcto:** ejecuta Login ADMIN; respuesta `200` y `data.token` no vacío.
   - **Token ausente:** quita Auth y solicita `GET {{ _.base_url }}/users/me`; respuesta `401`.
   - **Token expirado:** reinicia temporalmente la app con `JWT_EXPIRATION_MS=10000`, vuelve a iniciar sesión, espera más de 10 segundos y llama `/users/me`; respuesta `401`. Reinicia luego con `600000`.
   - **Rol no permitido:** vuelve a hacer Login como cliente y llama `GET {{ _.base_url }}/users`; respuesta `403`.
   - **Vendedor edita producto ajeno:** con token ADMIN crea otro usuario VENDEDOR con un username/email distintos; inicia sesión con ese usuario y guarda el JWT en `second_seller_token`. Usa ese token para `PUT {{ _.base_url }}/products/{{ _.product_id }}` con `{"name":"Intento ajeno","description":"Prueba","price":25.50,"stock":10,"categoryId":{{ _.category_id }}}`; respuesta `403`.
   - **Pedido sin stock:** inicia sesión como vendedor propietario y actualiza el producto a `{"name":"Teclado","description":"Prueba stock","price":25.50,"stock":1,"categoryId":{{ _.category_id }}}`; vuelve a iniciar sesión como cliente e intenta `POST /orders` con `{"productId":{{ _.product_id }},"quantity":2}`; respuesta `400`.

   En Insomnia cada solicitud muestra el código HTTP. Guarda capturas de la solicitud, respuesta y estado, sin incluir tokens.

## Ejemplos iniciales

Registrar un CLIENTE (la API ignora cualquier rol enviado y asigna CLIENTE):

```http
POST /api/v1/tienda/auth/register
Content-Type: application/json

{"username":"cliente1","email":"cliente1@example.com","password":"Cliente123!"}
```

Iniciar sesión:

```http
POST /api/v1/tienda/auth/login
Content-Type: application/json

{"user":"admin","password":"password"}
```

Para una ruta protegida, envía `Authorization: Bearer <token>`. En Postman puedes usar `{{token}}`.

## Seguridad y flujo

1. `AuthService` compara la contraseña con BCrypt; el registro público siempre asigna el rol CLIENTE.
2. `JwtService` crea un JWT firmado HS256 con `sub=username`, `userId`, `rolId`, `iat` y `exp`.
3. `JwtValidationFilter` exige un token en todas las rutas protegidas, verifica firma/expiración y comprueba que usuario/rol sigan activos en la BD. Añade `username`, `userId` y `rolId` al request.
4. El filtro deja públicas todas las rutas `/auth/**` y `GET /products` / `GET /products/{id}`.
5. `RoleInterceptor` comprueba `@RequiresRole` de método o clase; el rol no permitido produce 403.
6. Los servicios verifican propiedad: un vendedor solo modifica sus productos y pedidos de ventas propias; un cliente solo ve/cancela sus pedidos.
7. El total se calcula en servidor (`precio × cantidad`), el stock se descuenta al comprar y se repone al cancelar.
8. `ApiExceptionHandler` mantiene las respuestas con forma `{"message":"...","data":...}` para errores de validación y negocio.

## Rutas y permisos

Las rutas siguientes se agregan a `/api/v1/tienda`.

| Método | Ruta | Acceso |
|---|---|---|
| POST | `/auth/register`, `/auth/login`, `/auth/refreshtoken` | Público |
| GET | `/users/me` | ADMIN, CLIENTE, VENDEDOR |
| GET, POST | `/users` | ADMIN |
| PATCH | `/users/{id}/role`, `/users/{id}/status` | ADMIN |
| GET | `/categories` | ADMIN, CLIENTE, VENDEDOR |
| POST, DELETE | `/categories`, `/categories/{id}` | ADMIN |
| GET | `/products`, `/products/{id}` | Público |
| GET | `/products/mine`; POST `/products` | VENDEDOR |
| PUT, DELETE | `/products/{id}` | VENDEDOR dueño o ADMIN |
| POST, GET | `/orders`, `/orders/mine`, `/orders/sales`, `/orders` | CLIENTE, CLIENTE, VENDEDOR, ADMIN respectivamente |
| PATCH | `/orders/{id}/status` | VENDEDOR dueño o ADMIN |
| PATCH | `/orders/{id}/cancel` | CLIENTE dueño, solo si está PENDIENTE |

## Códigos HTTP

- `200`: consulta o actualización correcta.
- `201`: registro, usuario, producto, categoría o pedido creado.
- `400`: datos inválidos o stock insuficiente.
- `401`: token ausente/incorrecto/expirado, usuario inactivo o credenciales incorrectas.
- `403`: rol o propiedad insuficiente.
- `404`: entidad inexistente.
- `409`: duplicado o conflicto con una relación/estado existente.

## Casos que debes demostrar en la entrega

1. **Login correcto:** POST `/auth/login`; comprueba que hay token.
2. **Token ausente:** GET `/users/me` sin Authorization → 401.
3. **Token expirado:** usa temporalmente `JWT_EXPIRATION_MS=10000`, inicia sesión, espera 10 segundos y llama `/users/me` → 401. Reinicia con `600000`.
4. **Rol no permitido:** usa token CLIENTE en GET `/users` → 403.
5. **Producto ajeno:** crea dos vendedores, crea un producto con uno y usa el token del otro en PUT `/products/{id}` → 403.
6. **Stock insuficiente:** establece un stock pequeño e intenta un pedido por más unidades → 400.

Adjunta capturas o conserva esas solicitudes/resultados en Postman. No subas tokens vigentes ni secretos a GitHub.

## PostgreSQL

El proyecto está configurado inicialmente para MySQL (incluidos el driver y `tienda.sql`). Para PostgreSQL se debe cambiar la dependencia del driver, el `DB_URL`/driver y adaptar el script SQL (por ejemplo, `AUTO_INCREMENT` por `GENERATED ... AS IDENTITY`).

## Publicar en GitHub

1. Crea un repositorio vacío en GitHub (no agregues README ni licencia desde GitHub, porque ya existen localmente).
2. Desde esta carpeta ejecuta:

   ```powershell
   git init
   git add .
   git commit -m "Completa API de tienda con JWT y roles"
   git branch -M main
   git remote add origin https://github.com/TU_USUARIO/TU_REPOSITORIO.git
   git push -u origin main
   ```

   Revisa antes que `.env` y cualquier secreto/token no estén en el repositorio. `.gitignore` excluye `.env`.

## Retos extra implementados

Los DTO se validan con `spring-boot-starter-validation`, `@RestControllerAdvice` centraliza los errores y `@RequiresRole` puede aplicarse a clases o métodos. El catálogo está paginado: `GET /products?page=0&size=10`; el tamaño permitido es de 1 a 100 y se puede combinar con `categoryId`.
