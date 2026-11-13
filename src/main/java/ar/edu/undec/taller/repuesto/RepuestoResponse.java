package ar.edu.undec.taller.repuesto;

import java.math.BigDecimal;

public record RepuestoResponse(
        Long id,
        String nombre,
        BigDecimal precio,
        Integer stock
) {
}