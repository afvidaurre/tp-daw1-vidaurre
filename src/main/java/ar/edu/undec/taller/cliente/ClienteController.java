package ar.edu.undec.taller.cliente;

import ar.edu.undec.taller.vehiculo.VehiculoResponse;
import ar.edu.undec.taller.vehiculo.VehiculoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;
    private final VehiculoService vehiculoService;

    public ClienteController(
            ClienteService clienteService,
            VehiculoService vehiculoService) {
        this.clienteService = clienteService;
        this.vehiculoService = vehiculoService;
    }

    @GetMapping
    public List<Cliente> listar() {
        return clienteService.listar();
    }

    @GetMapping("/{id}")
    public Cliente buscarPorId(@PathVariable Long id) {
        return clienteService.buscarPorId(id);
    }

    @GetMapping("/{id}/vehiculos")
    public List<VehiculoResponse> listarVehiculos(@PathVariable Long id) {
        return vehiculoService.listarPorCliente(id);
    }

    @PostMapping
    public ResponseEntity<Cliente> crear(@Valid @RequestBody Cliente cliente) {
        Cliente creado = clienteService.crear(cliente);
        URI location = URI.create("/api/clientes/" + creado.getId());
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{id}")
    public Cliente actualizar(
            @PathVariable Long id,
            @Valid @RequestBody Cliente cliente) {
        return clienteService.actualizar(id, cliente);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        clienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}