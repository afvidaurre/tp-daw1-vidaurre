package ar.edu.undec.taller.orden;

import jakarta.validation.constraints.NotNull;

public record CambiarEstadoRequest(
        @NotNull EstadoOrden estado
) {
}