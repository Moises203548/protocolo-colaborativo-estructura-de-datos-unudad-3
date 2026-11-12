package modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;


public class Reclamo implements Comparable<Reclamo> {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final String codigo;
    private final Cliente cliente;
    private final long numeroLlegada;
    private final LocalDateTime fechaRegistro;
    private String tipo;
    private String descripcion;
    private Prioridad prioridad;
    private Estado estado;

    public Reclamo(String codigo, Cliente cliente, String tipo, String descripcion,
                   Prioridad prioridad, long numeroLlegada) {
        this.codigo = codigo;
        this.cliente = cliente;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
        this.numeroLlegada = numeroLlegada;
        this.estado = Estado.PENDIENTE;
        this.fechaRegistro = LocalDateTime.now();
    }

    public String getCodigo() {
        return codigo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public long getNumeroLlegada() {
        return numeroLlegada;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Prioridad prioridad) {
        this.prioridad = prioridad;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    @Override
    public int compareTo(Reclamo otro) {
        return this.codigo.compareTo(otro.codigo);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Reclamo)) {
            return false;
        }
        Reclamo otro = (Reclamo) o;
        return codigo.equals(otro.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return String.format("%-8s | %-9s | %-20s | %-9s | %-15s | %s",
                codigo, prioridad, tipo, estado, cliente.getNombre(), descripcion);
    }

    public String detalle() {
        return "Código:       " + codigo + "\n"
                + "Cliente:      " + cliente + "\n"
                + "Tipo:         " + tipo + "\n"
                + "Descripción:  " + descripcion + "\n"
                + "Prioridad:    " + prioridad + "\n"
                + "Estado:       " + estado + "\n"
                + "Registrado:   " + fechaRegistro.format(FORMATO) + " (llegada #" + numeroLlegada + ")";
    }
}
