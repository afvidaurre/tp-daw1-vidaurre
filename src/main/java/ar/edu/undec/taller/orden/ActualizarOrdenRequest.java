package ar.edu.undec.taller.orden;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record ActualizarOrdenRequest(
        @NotBlank String descripcion,
        @DecimalMin("0.0") BigDecimal costoFinal
) {
}
