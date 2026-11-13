package util;

import modelo.Prioridad;
import servicio.GestorReclamos;

public class DatosPrueba {

    private DatosPrueba() {
    }

    public static void cargar(GestorReclamos gestor) {
        gestor.registrarCliente("1045678901", "Laura Gomez", "laura.gomez@correo.com");
        gestor.registrarCliente("1023456789", "Carlos Perez", "carlos.perez@correo.com");
        gestor.registrarCliente("1098765432", "Marta Rodriguez", "marta.rodriguez@correo.com");
        gestor.registrarCliente("1067890123", "Andres Torres", "andres.torres@correo.com");
        gestor.registrarCliente("1034567890", "Sofia Herrera", "sofia.herrera@correo.com");
        gestor.registrarCliente("1056789012", "Julian Castro", "julian.castro@correo.com");

        gestor.registrarTipo("Facturacion");
        gestor.registrarTipo("Servicio tecnico");
        gestor.registrarTipo("Garantia");
        gestor.registrarTipo("Entrega tardia");
        gestor.registrarTipo("Atencion al cliente");
        gestor.registrarTipo("Producto defectuoso");
        gestor.registrarTipo("Cobro indebido");

        gestor.registrarReclamo("REC-105", "1045678901", "Facturacion", "Error en el valor de la factura de octubre", Prioridad.BAJA);
        gestor.registrarReclamo("REC-101", "1023456789", "Garantia", "El televisor dejo de encender a los dos meses", Prioridad.NORMAL);
        gestor.registrarReclamo("REC-112", "1098765432", "Cobro indebido", "Me cobraron dos veces el mismo servicio", Prioridad.URGENTE);
        gestor.registrarReclamo("REC-103", "1067890123", "Producto defectuoso", "La licuadora llego con la jarra partida", Prioridad.ALTA);
        gestor.registrarReclamo("REC-116", "1034567890", "Atencion al cliente", "Nadie contesto la linea de soporte en dos dias", Prioridad.NORMAL);
        gestor.registrarReclamo("REC-108", "1056789012", "Entrega tardia", "El pedido llego diez dias despues de lo prometido", Prioridad.BAJA);
        gestor.registrarReclamo("REC-102", "1045678901", "Servicio tecnico", "El tecnico no se presento a la visita acordada", Prioridad.URGENTE);
        gestor.registrarReclamo("REC-114", "1023456789", "Garantia", "Negaron la garantia sin dar una razon clara", Prioridad.ALTA);
        gestor.registrarReclamo("REC-107", "1098765432", "Facturacion", "Llego una factura con un servicio que no pedi", Prioridad.NORMAL);
        gestor.registrarReclamo("REC-110", "1067890123", "Atencion al cliente", "El asesor fue grosero durante la llamada", Prioridad.BAJA);
        gestor.registrarReclamo("REC-104", "1034567890", "Cobro indebido", "Descontaron dinero de mi cuenta sin autorizacion", Prioridad.URGENTE);
        gestor.registrarReclamo("REC-115", "1056789012", "Producto defectuoso", "El celular se apaga solo y la bateria no dura", Prioridad.ALTA);
        gestor.registrarReclamo("REC-109", "1045678901", "Entrega tardia", "Mi paquete no ha llegado y la guia no se mueve", Prioridad.NORMAL);
        gestor.registrarReclamo("REC-113", "1023456789", "Servicio tecnico", "La reparacion quedo mal y el equipo sigue fallando", Prioridad.BAJA);
        gestor.registrarReclamo("REC-106", "1098765432", "Garantia", "Piden documentos que ya habia entregado antes", Prioridad.ALTA);
        gestor.registrarReclamo("REC-111", "1067890123", "Facturacion", "Aplicaron un descuento menor al ofrecido", Prioridad.NORMAL);
    }
}
