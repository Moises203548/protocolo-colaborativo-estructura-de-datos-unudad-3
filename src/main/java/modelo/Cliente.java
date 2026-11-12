package modelo;

import java.util.Objects;

public class Cliente {

    private final String documento;
    private String nombre;
    private String correo;

    public Cliente(String documento, String nombre, String correo) {
        this.documento = documento;
        this.nombre = nombre;
        this.correo = correo;
    }

    public String getDocumento() {
        return documento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    /** Dos clientes son iguales si tienen el mismo documento. */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Cliente)) {
            return false;
        }
        Cliente otro = (Cliente) o;
        return documento.equals(otro.documento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(documento);
    }

    @Override
    public String toString() {
        return documento + " - " + nombre + " (" + correo + ")";
    }
}
