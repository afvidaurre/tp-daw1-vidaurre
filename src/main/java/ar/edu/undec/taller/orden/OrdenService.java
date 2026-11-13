package ar.edu.undec.taller.orden;

import ar.edu.undec.taller.vehiculo.Vehiculo;
import ar.edu.undec.taller.vehiculo.VehiculoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class OrdenService {

    private final OrdenRepository ordenRepository;
    private final VehiculoRepository vehiculoRepository;

    public OrdenService(
            OrdenRepository ordenRepository,
            VehiculoRepository vehiculoRepository) {
        this.ordenRepository = ordenRepository;
        this.vehiculoRepository = vehiculoRepository;
    }

    public List<OrdenResponse> listar(EstadoOrden estado) {
        List<Orden> ordenes = estado == null
                ? ordenRepository.findAll()
                : ordenRepository.findByEstado(estado);

        return ordenes.stream().map(this::aResponse).toList();
    }

    public OrdenResponse buscarPorId(Long id) {
        return aResponse(buscarEntidad(id));
    }

    public OrdenResponse crear(CrearOrdenRequest datos) {
        Vehiculo vehiculo = vehiculoRepository.findById(datos.vehiculoId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Vehículo no existe"));

        boolean tieneOrdenAbierta =
                ordenRepository.existsByVehiculo_IdAndEstadoNotIn(
                        vehiculo.getId(),
                        List.of(EstadoOrden.ENTREGADO, EstadoOrden.CANCELADO));

        if (tieneOrdenAbierta) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El vehículo ya tiene una orden abierta");
        }

        Orden orden = new Orden();
        orden.setVehiculo(vehiculo);
        orden.setDescripcion(datos.descripcion());
        // El estado inicial INGRESADO lo asigna la entidad.

        return aResponse(ordenRepository.save(orden));
    }

    public OrdenResponse actualizar(Long id, ActualizarOrdenRequest datos) {
        Orden orden = buscarEntidad(id);
        orden.setDescripcion(datos.descripcion());
        orden.setCostoFinal(datos.costoFinal());
        return aResponse(ordenRepository.save(orden));
    }

    public OrdenResponse cambiarEstado(Long id, EstadoOrden nuevoEstado) {
        Orden orden = buscarEntidad(id);

        if (!transicionPermitida(orden.getEstado(), nuevoEstado)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Transición de estado no permitida");
        }

        if (nuevoEstado == EstadoOrden.ENTREGADO && orden.getCostoFinal() == null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede entregar la orden sin costoFinal");
        }

        orden.setEstado(nuevoEstado);
        return aResponse(ordenRepository.save(orden));
    }

    public void eliminar(Long id) {
        Orden orden = buscarEntidad(id);
        ordenRepository.delete(orden);
    }

    public List<OrdenResponse> listarPorVehiculo(Long vehiculoId) {
        return ordenRepository.findByVehiculo_Id(vehiculoId)
                .stream()
                .map(this::aResponse)
                .toList();
    }

    private Orden buscarEntidad(Long id) {
        return ordenRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Orden no existe"));
    }

    private boolean transicionPermitida(
            EstadoOrden actual,
            EstadoOrden siguiente) {
        return switch (actual) {
            case INGRESADO ->
                    siguiente == EstadoOrden.EN_REPARACION
                            || siguiente == EstadoOrden.CANCELADO;
            case EN_REPARACION -> siguiente == EstadoOrden.TERMINADO;
            case TERMINADO -> siguiente == EstadoOrden.ENTREGADO;
            case ENTREGADO, CANCELADO -> false;
        };
    }

    private OrdenResponse aResponse(Orden orden) {
        return new OrdenResponse(
                orden.getId(),
                orden.getVehiculo().getId(),
                orden.getDescripcion(),
                orden.getEstado(),
                orden.getCostoFinal()
        );
    }
}