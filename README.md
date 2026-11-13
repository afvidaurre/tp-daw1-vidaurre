# TP Integrador DAW1 — Taller Mecánico

**Alumno/a:** Vidaurre Terrazas Ayrton Fabricio · **Legajo:** 510119  
**Enunciado elegido:** taller

## Descripción

API REST para registrar clientes y vehículos, administrar órdenes de reparación y consultar la facturación del taller. También permite controlar el stock de repuestos utilizados en cada orden.

## Cómo ejecutarlo

Requisito: Java 21. Maven se ejecuta mediante el wrapper incluido.

En Windows PowerShell, desde la raíz del repositorio:

```powershell
.\mvnw.cmd spring-boot:run
```

La API queda disponible en `http://localhost:8080/api`. La base H2 se guarda en `data/` y esa carpeta está excluida de Git.

Consola H2: `http://localhost:8080/h2-console`  
URL JDBC: `jdbc:h2:file:./data/tallerdb` · Usuario: `sa` · Contraseña: vacía.

## Modelo

| Entidad | Atributos principales | Relaciones |
|---|---|---|
| Cliente | id, nombre, telefono | — |
| Vehiculo | id, patente, marca, modelo, clienteId | N a 1 con Cliente |
| Orden | id, vehiculoId, descripcion, estado, costoFinal, fechaEntrega | N a 1 con Vehiculo |
| Repuesto | id, nombre, precio, stock | — |
| OrdenRepuesto | id, ordenId, repuestoId, cantidad | N a 1 con Orden y Repuesto |

`costoRepuestos` se calcula como la suma de `precio × cantidad` de los repuestos asociados a la orden. `fechaEntrega` se registra automáticamente al entregar una orden.

Estados de Orden: `INGRESADO → EN_REPARACION | CANCELADO`; `EN_REPARACION → TERMINADO`; `TERMINADO → ENTREGADO`. `CANCELADO` y `ENTREGADO` son estados finales.

## Endpoints

| Método | Ruta | Descripción | Respuestas principales |
|---|---|---|---|
| GET, POST | `/api/clientes` | Listar y crear clientes | 200, 201, 400 |
| GET, PUT, DELETE | `/api/clientes/{id}` | Consultar, modificar y borrar un cliente | 200, 204, 400, 404, 409 |
| GET | `/api/clientes/{id}/vehiculos` | Vehículos de un cliente | 200, 404 |
| GET, POST | `/api/vehiculos` | Listar y crear vehículos | 200, 201, 400, 404, 409 |
| GET, PUT, DELETE | `/api/vehiculos/{id}` | Consultar, modificar y borrar un vehículo | 200, 204, 400, 404, 409 |
| GET | `/api/vehiculos/patente/{patente}/historial` | Historial de órdenes, de más nueva a más vieja | 200, 404 |
| GET | `/api/ordenes` | Listar órdenes; acepta `?estado=EN_REPARACION` | 200, 400 |
| GET, POST | `/api/ordenes` | Consultar la lista y crear órdenes | 200, 201, 400, 404, 409 |
| GET, PUT, DELETE | `/api/ordenes/{id}` | Consultar, modificar y borrar una orden | 200, 204, 400, 404 |
| PATCH | `/api/ordenes/{id}/estado` | Cambiar el estado de una orden | 200, 400, 404, 409 |
| POST | `/api/ordenes/{id}/repuestos` | Agregar repuestos y descontar stock | 201, 400, 404, 409 |
| GET | `/api/ordenes/facturacion?mes=2026-11` | Cantidad de órdenes entregadas y total del mes | 200, 400 |
| GET, POST | `/api/repuestos` | Listar y crear repuestos | 200, 201, 400 |
| GET, PUT, DELETE | `/api/repuestos/{id}` | Consultar, modificar y borrar repuestos | 200, 204, 400, 404, 409 |

## Reglas de negocio

1. La patente debe tener formato `ABC123` o `AB123CD` y ser única (409 si se repite).
2. Un vehículo no puede tener más de una orden abierta. Una orden está abierta hasta quedar `ENTREGADO` o `CANCELADO` (409 si ya tiene una).
3. No se puede pasar una orden a `ENTREGADO` sin `costoFinal` (409).
4. Solo se permiten las transiciones de estado indicadas en el modelo (409 si la transición no está permitida).
5. No se puede descontar más stock del disponible (409).
6. `costoFinal` no puede ser menor que `costoRepuestos` (400).
7. No se puede borrar un cliente con vehículos, un vehículo con órdenes ni un repuesto asociado a una orden (409).

Los errores se devuelven en formato `ProblemDetail`. Los errores de validación incluyen el mapa `errores` con los campos inválidos.

## Extras implementados

- Facturación mensual de órdenes entregadas.
- Historial de órdenes por patente.
- Gestión de repuestos, descuento de stock y cálculo de `costoRepuestos`.

## Pruebas

`pruebas.http` contiene peticiones de ejemplo para ejecutar desde IntelliJ. Algunas comprueban errores esperados, como 400 por datos inválidos y 409 por reglas de negocio.
