package ar.edu.undec.taller.orden;

import ar.edu.undec.taller.repuesto.RepuestoService;
import ar.edu.undec.taller.vehiculo.Vehiculo;
import ar.edu.undec.taller.vehiculo.VehiculoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import ar.edu.undec.taller.repuesto.Repuesto;
import ar.edu.undec.taller.repuesto.RepuestoRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrdenService {

    private final OrdenRepository ordenRepository;
    private final VehiculoRepository vehiculoRepository;
    private final OrdenRepuestoRepository ordenRepuestoRepository;
    private final RepuestoRepository repuestoRepository;
    public OrdenService(
            OrdenRepository ordenRepository,
            VehiculoRepository vehiculoRepository,
            OrdenRepuestoRepository ordenRepuestoRepository, RepuestoRepository repuestoRepository) {
        this.ordenRepository = ordenRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.ordenRepuestoRepository = ordenRepuestoRepository;
        this.repuestoRepository = repuestoRepository;
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

        if (datos.costoFinal() != null
                && datos.costoFinal().compareTo(
                calcularCostoRepuestos(id)) < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "costoFinal no puede ser menor que costoRepuestos");
        }

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
        if (nuevoEstado == EstadoOrden.ENTREGADO) {
            orden.setFechaEntrega(LocalDateTime.now());
        }
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

    public List<OrdenResponse> historialPorPatente(String patente) {
        if (!vehiculoRepository.existsByPatente(patente)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Vehículo no existe");
        }

        return ordenRepository
                .findByVehiculo_PatenteOrderByIdDesc(patente)
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
                orden.getCostoFinal(),
                calcularCostoRepuestos(orden.getId())
        );
    }

    private BigDecimal calcularCostoRepuestos(Long ordenId) {
        return ordenRepuestoRepository.findByOrden_Id(ordenId)
                .stream()
                .map(detalle -> detalle.getRepuesto().getPrecio()
                        .multiply(BigDecimal.valueOf(detalle.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public FacturacionMensualResponse facturacionMensual(String mesTexto) {
        YearMonth mes;

        try {
            mes = YearMonth.parse(mesTexto);
        } catch (DateTimeParseException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El mes debe tener formato yyyy-MM");
        }

        LocalDateTime desde = mes.atDay(1).atStartOfDay();
        LocalDateTime hasta = mes.plusMonths(1).atDay(1).atStartOfDay();

        List<Orden> entregadas =
                ordenRepository
                        .findByEstadoAndFechaEntregaGreaterThanEqualAndFechaEntregaLessThan(
                                EstadoOrden.ENTREGADO,
                                desde,
                                hasta);

        BigDecimal total = entregadas.stream()
                .map(Orden::getCostoFinal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new FacturacionMensualResponse(entregadas.size(), total);
    }
    @Transactional
    public OrdenResponse agregarRepuesto(
            Long ordenId,
            AgregarRepuestoRequest datos) {
        Orden orden = buscarEntidad(ordenId);

        Repuesto repuesto = repuestoRepository.findById(datos.repuestoId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Repuesto no existe"));

        if (repuesto.getStock() < datos.cantidad()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Stock insuficiente");
        }

        BigDecimal costoNuevo = calcularCostoRepuestos(ordenId).add(
                repuesto.getPrecio()
                        .multiply(BigDecimal.valueOf(datos.cantidad())));

        if (orden.getCostoFinal() != null
                && orden.getCostoFinal().compareTo(costoNuevo) < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "costoFinal no puede ser menor que costoRepuestos");
        }

        repuesto.setStock(repuesto.getStock() - datos.cantidad());
        repuestoRepository.save(repuesto);

        OrdenRepuesto detalle = new OrdenRepuesto();
        detalle.setOrden(orden);
        detalle.setRepuesto(repuesto);
        detalle.setCantidad(datos.cantidad());
        ordenRepuestoRepository.save(detalle);

        return aResponse(orden);
    }
}