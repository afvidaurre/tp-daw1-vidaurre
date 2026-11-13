package ar.edu.undec.taller.vehiculo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {

    boolean existsByPatente(String patente);

    boolean existsByPatenteAndIdNot(String patente, Long id);

    List<Vehiculo> findByCliente_Id(Long clienteId);

    boolean existsByCliente_Id(Long clienteId);
}


