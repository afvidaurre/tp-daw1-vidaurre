package ar.edu.undec.taller.vehiculo;

import ar.edu.undec.taller.cliente.Cliente;
import ar.edu.undec.taller.cliente.ClienteRepository;
import ar.edu.undec.taller.orden.OrdenRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;
    private final ClienteRepository clienteRepository;
    private final OrdenRepository ordenRepository;

    public VehiculoService(
            VehiculoRepository vehiculoRepository,
            ClienteRepository clienteRepository,
            OrdenRepository ordenRepository) {
        this.vehiculoRepository = vehiculoRepository;
        this.clienteRepository = clienteRepository;
        this.ordenRepository = ordenRepository;
    }

    public List<VehiculoResponse> listar() {
        return vehiculoRepository.findAll()
                .stream()
                .map(this::aResponse)
                .toList();
    }

    public VehiculoResponse buscarPorId(Long id) {
        return aResponse(buscarEntidad(id));
    }

    public VehiculoResponse crear(VehiculoRequest datos) {
        if (vehiculoRepository.existsByPatente(datos.patente())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Ya existe un vehículo con esa patente");
        }

        Cliente cliente = buscarCliente(datos.clienteId());
        Vehiculo vehiculo = new Vehiculo();
        completarDatos(vehiculo, datos, cliente);

        return aResponse(vehiculoRepository.save(vehiculo));
    }

    public VehiculoResponse actualizar(Long id, VehiculoRequest datos) {
        Vehiculo vehiculo = buscarEntidad(id);

        if (vehiculoRepository.existsByPatenteAndIdNot(datos.patente(), id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Ya existe un vehículo con esa patente");
        }

        Cliente cliente = buscarCliente(datos.clienteId());
        completarDatos(vehiculo, datos, cliente);

        return aResponse(vehiculoRepository.save(vehiculo));
    }

    public void eliminar(Long id) {
        Vehiculo vehiculo = buscarEntidad(id);

        if (ordenRepository.existsByVehiculo_Id(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede borrar el vehículo porque tiene órdenes asociadas");
        }

        vehiculoRepository.delete(vehiculo);
    }

    private Vehiculo buscarEntidad(Long id) {
        return vehiculoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Vehículo no existe"));
    }

    private Cliente buscarCliente(Long clienteId) {
        return clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cliente no existe"));
    }

    private void completarDatos(
            Vehiculo vehiculo,
            VehiculoRequest datos,
            Cliente cliente) {
        vehiculo.setPatente(datos.patente());
        vehiculo.setMarca(datos.marca());
        vehiculo.setModelo(datos.modelo());
        vehiculo.setCliente(cliente);
    }

    private VehiculoResponse aResponse(Vehiculo vehiculo) {
        return new VehiculoResponse(
                vehiculo.getId(),
                vehiculo.getPatente(),
                vehiculo.getMarca(),
                vehiculo.getModelo(),
                vehiculo.getCliente().getId()
        );
    }

    public List<VehiculoResponse> listarPorCliente(Long clienteId) {
        if (!clienteRepository.existsById(clienteId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Cliente no existe");
        }

        return vehiculoRepository.findByCliente_Id(clienteId)
                .stream()
                .map(this::aResponse)
                .toList();
    }
}
