package ar.edu.undec.taller.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ProblemDetail> manejarEstado(
            ResponseStatusException ex) {
        HttpStatusCode status = ex.getStatusCode();
        String titulo = switch (status.value()) {
            case 404 -> "No encontrado";
            case 409 -> "Conflicto";
            default -> "Error";
        };
        String detalle = ex.getReason() == null
                ? "La petición no pudo completarse"
                : ex.getReason();

        return respuesta(status, titulo, detalle);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> manejarValidacion(
            MethodArgumentNotValidException ex) {
        Map<String, String> errores = new LinkedHashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errores.putIfAbsent(error.getField(), error.getDefaultMessage());
        }

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "La petición tiene errores");
        problem.setTitle("Datos inválidos");
        problem.setProperty("errores", errores);

        return ResponseEntity.badRequest().body(problem);
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ProblemDetail> manejarFormatoInvalido(Exception ex) {
        return respuesta(
                HttpStatus.BAD_REQUEST,
                "Datos inválidos",
                "El JSON o uno de los parámetros de la petición no es válido");
    }

    private ResponseEntity<ProblemDetail> respuesta(
            HttpStatusCode status,
            String titulo,
            String detalle) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detalle);
        problem.setTitle(titulo);
        return ResponseEntity.status(status).body(problem);
    }
}