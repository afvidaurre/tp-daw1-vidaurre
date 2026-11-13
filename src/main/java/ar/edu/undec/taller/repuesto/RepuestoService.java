package ar.edu.undec.taller.repuesto;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ar.edu.undec.taller.orden.OrdenRepuestoRepository;
import java.util.List;

@Service
public class RepuestoService {

    private final RepuestoRepository repuestoRepository;
    private final OrdenRepuestoRepository ordenRepuestoRepository;

    public RepuestoService(RepuestoRepository repuestoRepository, OrdenRepuestoRepository ordenRepuestoRepository) {
        this.repuestoRepository = repuestoRepository;
        this.ordenRepuestoRepository = ordenRepuestoRepository;
    }

    public List<RepuestoResponse> listar() {
        return repuestoRepository.findAll()
                .stream()
                .map(this::aResponse)
                .toList();
    }

    public RepuestoResponse buscarPorId(Long id) {
        return aResponse(buscarEntidad(id));
    }

    public RepuestoResponse crear(RepuestoRequest datos) {
        Repuesto repuesto = new Repuesto();
        completarDatos(repuesto, datos);
        return aResponse(repuestoRepository.save(repuesto));
    }

    public RepuestoResponse actualizar(Long id, RepuestoRequest datos) {
        Repuesto repuesto = buscarEntidad(id);
        completarDatos(repuesto, datos);
        return aResponse(repuestoRepository.save(repuesto));
    }

    public void eliminar(Long id) {
        Repuesto repuesto = buscarEntidad(id);

        if (ordenRepuestoRepository.existsByRepuesto_Id(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede borrar el repuesto porque está asociado a una orden");
        }

        repuestoRepository.delete(repuesto);
    }

    private Repuesto buscarEntidad(Long id) {
        return repuestoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Repuesto no existe"));
    }

    private void completarDatos(Repuesto repuesto, RepuestoRequest datos) {
        repuesto.setNombre(datos.nombre());
        repuesto.setPrecio(datos.precio());
        repuesto.setStock(datos.stock());
    }

    private RepuestoResponse aResponse(Repuesto repuesto) {
        return new RepuestoResponse(
                repuesto.getId(),
                repuesto.getNombre(),
                repuesto.getPrecio(),
                repuesto.getStock());
    }
}