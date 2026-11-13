package ar.edu.undec.taller.orden;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/ordenes")
public class OrdenController {

    private final OrdenService ordenService;

    public OrdenController(OrdenService ordenService) {
        this.ordenService = ordenService;
    }

    @GetMapping
    public List<OrdenResponse> listar(
            @RequestParam(required = false) EstadoOrden estado) {
        return ordenService.listar(estado);
    }

    @GetMapping("/facturacion")
    public FacturacionMensualResponse facturacionMensual(
            @RequestParam String mes) {
        return ordenService.facturacionMensual(mes);
    }

    @GetMapping("/{id}")
    public OrdenResponse buscarPorId(@PathVariable Long id) {
        return ordenService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<OrdenResponse> crear(
            @Valid @RequestBody CrearOrdenRequest datos) {
        OrdenResponse creada = ordenService.crear(datos);
        URI location = URI.create("/api/ordenes/" + creada.id());
        return ResponseEntity.created(location).body(creada);
    }

    @PostMapping("/{id}/repuestos")
    public ResponseEntity<OrdenResponse> agregarRepuesto(
            @PathVariable Long id,
            @Valid @RequestBody AgregarRepuestoRequest datos) {
        OrdenResponse actualizada = ordenService.agregarRepuesto(id, datos);
        URI location = URI.create("/api/ordenes/" + id);
        return ResponseEntity.created(location).body(actualizada);
    }

    @PutMapping("/{id}")
    public OrdenResponse actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarOrdenRequest datos) {
        return ordenService.actualizar(id, datos);
    }

    @PatchMapping("/{id}/estado")
    public OrdenResponse cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody CambiarEstadoRequest datos) {
        return ordenService.cambiarEstado(id, datos.estado());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        ordenService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}