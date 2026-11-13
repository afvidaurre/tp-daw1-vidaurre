package ar.edu.undec.taller.orden;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CrearOrdenRequest(
        @NotNull @Positive Long vehiculoId,
        @NotBlank String descripcion
) {
}
