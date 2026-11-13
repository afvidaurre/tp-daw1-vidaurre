package ar.edu.undec.taller.vehiculo;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoController {

    private final VehiculoService vehiculoService;

    public VehiculoController(VehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }

    @GetMapping
    public List<VehiculoResponse> listar() {
        return vehiculoService.listar();
    }

    @GetMapping("/{id}")
    public VehiculoResponse buscarPorId(@PathVariable Long id) {
        return vehiculoService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<VehiculoResponse> crear(
            @Valid @RequestBody VehiculoRequest datos) {
        VehiculoResponse creado = vehiculoService.crear(datos);
        URI location = URI.create("/api/vehiculos/" + creado.id());
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{id}")
    public VehiculoResponse actualizar(
            @PathVariable Long id,
            @Valid @RequestBody VehiculoRequest datos) {
        return vehiculoService.actualizar(id, datos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        vehiculoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
