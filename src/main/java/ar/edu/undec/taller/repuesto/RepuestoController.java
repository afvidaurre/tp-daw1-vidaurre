package ar.edu.undec.taller.repuesto;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/repuestos")
public class RepuestoController {

    private final RepuestoService repuestoService;

    public RepuestoController(RepuestoService repuestoService) {
        this.repuestoService = repuestoService;
    }

    @GetMapping
    public List<RepuestoResponse> listar() {
        return repuestoService.listar();
    }

    @GetMapping("/{id}")
    public RepuestoResponse buscarPorId(@PathVariable Long id) {
        return repuestoService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<RepuestoResponse> crear(
            @Valid @RequestBody RepuestoRequest datos) {
        RepuestoResponse creado = repuestoService.crear(datos);
        URI location = URI.create("/api/repuestos/" + creado.id());
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{id}")
    public RepuestoResponse actualizar(
            @PathVariable Long id,
            @Valid @RequestBody RepuestoRequest datos) {
        return repuestoService.actualizar(id, datos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        repuestoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}