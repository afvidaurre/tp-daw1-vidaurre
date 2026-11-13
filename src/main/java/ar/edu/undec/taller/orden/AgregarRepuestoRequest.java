package ar.edu.undec.taller.orden;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AgregarRepuestoRequest(
        @NotNull @Positive Long repuestoId,
        @NotNull @Positive Integer cantidad
) {
}