package ar.edu.undec.taller.vehiculo;

public record VehiculoResponse(
        Long id,
        String patente,
        String marca,
        String modelo,
        Long clienteId
) {
}
