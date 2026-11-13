package ar.edu.undec.taller.orden;

import java.math.BigDecimal;

public record FacturacionMensualResponse(
        int cantidadOrdenes,
        BigDecimal totalFacturado
) {
}