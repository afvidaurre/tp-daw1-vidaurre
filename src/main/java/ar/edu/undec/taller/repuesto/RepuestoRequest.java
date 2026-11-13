package ar.edu.undec.taller.repuesto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RepuestoRequest(
        @NotBlank String nombre,
        @NotNull @DecimalMin("0.0") BigDecimal precio,
        @NotNull @Min(0) Integer stock
) {
}