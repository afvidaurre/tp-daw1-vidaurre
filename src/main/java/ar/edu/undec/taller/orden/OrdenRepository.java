package ar.edu.undec.taller.orden;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.time.LocalDateTime;

public interface OrdenRepository extends JpaRepository<Orden, Long> {

    boolean existsByVehiculo_IdAndEstadoNotIn(
            Long vehiculoId,
            Collection<EstadoOrden> estados);

    List<Orden> findByEstado(EstadoOrden estado);

    List<Orden> findByVehiculo_Id(Long vehiculoId);

    boolean existsByVehiculo_Id(Long vehiculoId);
    List<Orden> findByEstadoAndFechaEntregaGreaterThanEqualAndFechaEntregaLessThan(
            EstadoOrden estado,
            LocalDateTime desde,
            LocalDateTime hasta);
}