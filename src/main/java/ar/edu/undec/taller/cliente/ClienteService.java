package ar.edu.undec.taller.cliente;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ar.edu.undec.taller.vehiculo.VehiculoRepository;

import java.util.List;


@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final VehiculoRepository vehiculoRepository;

    

    public ClienteService(
            ClienteRepository clienteRepository,
            VehiculoRepository vehiculoRepository) {
        this.clienteRepository = clienteRepository;
        this.vehiculoRepository = vehiculoRepository;
    }

    public List<Cliente> listar() {
        return clienteRepository.findAll();
    }

    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cliente no existe"));
    }

    public Cliente crear(Cliente cliente) {
        cliente.setId(null);
        return clienteRepository.save(cliente);
    }

    public Cliente actualizar(Long id, Cliente datos) {
        Cliente existente = buscarPorId(id);
        existente.setNombre(datos.getNombre());
        existente.setTelefono(datos.getTelefono());
        return clienteRepository.save(existente);
    }

    public void eliminar(Long id) {
        Cliente existente = buscarPorId(id);

        if (vehiculoRepository.existsByCliente_Id(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede borrar el cliente porque tiene vehículos asociados");
        }

        clienteRepository.delete(existente);
    }
}
