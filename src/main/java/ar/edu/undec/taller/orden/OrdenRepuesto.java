package ar.edu.undec.taller.orden;

import ar.edu.undec.taller.repuesto.Repuesto;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;

@Entity
@Table(name = "orden_repuestos")
public class OrdenRepuesto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "orden_id", nullable = false)
    private Orden orden;

    @ManyToOne(optional = false)
    @JoinColumn(name = "repuesto_id", nullable = false)
    private Repuesto repuesto;

    @Min(1)
    @Column(nullable = false)
    private Integer cantidad;

    public OrdenRepuesto() {
    }

    public Long getId() {
        return id;
    }

    public Orden getOrden() {
        return orden;
    }

    public void setOrden(Orden orden) {
        this.orden = orden;
    }

    public Repuesto getRepuesto() {
        return repuesto;
    }

    public void setRepuesto(Repuesto repuesto) {
        this.repuesto = repuesto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}