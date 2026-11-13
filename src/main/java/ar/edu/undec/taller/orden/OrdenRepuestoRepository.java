package ar.edu.undec.taller.orden;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdenRepuestoRepository
        extends JpaRepository<OrdenRepuesto, Long> {

    List<OrdenRepuesto> findByOrden_Id(Long ordenId);

    boolean existsByRepuesto_Id(Long repuestoId);
}