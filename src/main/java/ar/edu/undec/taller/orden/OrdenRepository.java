package ar.edu.undec.taller.orden;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface OrdenRepository extends JpaRepository<Orden, Long> {

    boolean existsByVehiculo_IdAndEstadoNotIn(
            Long vehiculoId,
            Collection<EstadoOrden> estados);

    List<Orden> findByEstado(EstadoOrden estado);

    List<Orden> findByVehiculo_Id(Long vehiculoId);

    boolean existsByVehiculo_Id(Long vehiculoId);
}