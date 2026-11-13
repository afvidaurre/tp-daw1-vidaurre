package ar.edu.undec.taller.orden;

import java.math.BigDecimal;

public record OrdenResponse(
        Long id,
        Long vehiculoId,
        String descripcion,
        EstadoOrden estado,
        BigDecimal costoFinal
) {
}
