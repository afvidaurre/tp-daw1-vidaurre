package ar.edu.undec.taller.vehiculo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record VehiculoRequest(
        @NotBlank
        @Pattern(regexp = "([A-Z]{3}[0-9]{3}|[A-Z]{2}[0-9]{3}[A-Z]{2})")
        String patente,

        @NotBlank
        String marca,

        @NotBlank
        String modelo,

        @NotNull
        @Positive
        Long clienteId
) {
}
