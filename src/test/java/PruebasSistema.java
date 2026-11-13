import java.util.List;
import modelo.Prioridad;
import modelo.Reclamo;
import servicio.GestorReclamos;
import servicio.ReclamoException;
import util.DatosPrueba;

public class PruebasSistema {

    private static int pasaron = 0;
    private static int fallaron = 0;

    public static void main(String[] args) {
        datosCargadosCorrectamente();
        codigoDuplicadoSeRechaza();
        tipoDuplicadoNoEntraAlSet();
        busquedaExistenteEInexistente();
        treeSetOrdenaPorCodigo();
        colaAtiendePorPrioridadYLlegada();
        cambioDePrioridadReordenaLaCola();
        eliminarMantieneConsistencia();
        validacionesRechazanDatosInvalidos();
        System.out.println();
        System.out.println("Resultado: " + pasaron + " correctas, " + fallaron + " fallidas.");
        if (fallaron > 0) {
            System.exit(1);
        }
    }

    private static GestorReclamos gestorConDatos() {
        GestorReclamos gestor = new GestorReclamos();
        DatosPrueba.cargar(gestor);
        return gestor;
    }

    private static void verificar(String nombre, boolean condicion) {
        if (condicion) {
            pasaron++;
            System.out.println("[OK]    " + nombre);
        } else {
            fallaron++;
            System.out.println("[FALLA] " + nombre);
        }
    }

    private static boolean colaContiene(GestorReclamos g, String codigo) {
        for (Reclamo r : g.pendientesEnOrdenDeAtencion()) {
            if (r.getCodigo().equals(codigo)) {
                return true;
            }
        }
        return false;
    }

    private static boolean lanzaReclamoException(Runnable accion) {
        try {
            accion.run();
            return false;
        } catch (ReclamoException e) {
            return true;
        }
    }

    private static void datosCargadosCorrectamente() {
        GestorReclamos g = gestorConDatos();
        verificar("Se cargan 16 reclamos, 6 clientes y 7 tipos",
                g.cantidadReclamos() == 16 && g.cantidadClientes() == 6 && g.listarTipos().size() == 7);
        verificar("Las colecciones quedan consistentes tras la carga", g.verificarConsistencia().isEmpty());
    }

    private static void codigoDuplicadoSeRechaza() {
        GestorReclamos g = gestorConDatos();
        verificar("Un código de reclamo repetido se rechaza (también en minúsculas)",
                lanzaReclamoException(() -> g.registrarReclamo("rec-105", "1045678901", "Facturacion",
                        "Intento de registrar un código repetido", Prioridad.BAJA)));
        verificar("El reclamo rechazado no cambia el tamaño de las colecciones", g.cantidadReclamos() == 16);
    }

    private static void tipoDuplicadoNoEntraAlSet() {
        GestorReclamos g = gestorConDatos();
        verificar("El Set rechaza un tipo repetido ignorando mayúsculas", !g.registrarTipo("  facturacion "));
        verificar("Un tipo nuevo sí se agrega", g.registrarTipo("Mala informacion") && g.listarTipos().size() == 8);
    }

    private static void busquedaExistenteEInexistente() {
        GestorReclamos g = gestorConDatos();
        verificar("Búsqueda de un código existente", g.buscarReclamo("REC-103").isPresent());
        verificar("Búsqueda de un código inexistente devuelve vacío", !g.buscarReclamo("REC-999").isPresent());
        verificar("Cliente inexistente devuelve vacío", !g.buscarCliente("99999999").isPresent());
    }

    private static void treeSetOrdenaPorCodigo() {
        GestorReclamos g = gestorConDatos();
        List<Reclamo> ordenados = g.listarReclamosOrdenados();
        verificar("El primero ordenado es REC-101 aunque se registró segundo",
                ordenados.get(0).getCodigo().equals("REC-101"));
        verificar("El último ordenado es REC-116", ordenados.get(15).getCodigo().equals("REC-116"));
    }

    private static void colaAtiendePorPrioridadYLlegada() {
        GestorReclamos g = gestorConDatos();
        String[] esperado = {"REC-112", "REC-102", "REC-104", "REC-103", "REC-114", "REC-115", "REC-106"};
        boolean correcto = true;
        for (String codigo : esperado) {
            if (!g.atenderSiguiente().getCodigo().equals(codigo)) {
                correcto = false;
            }
        }
        verificar("Orden de atención: 3 urgentes y 4 altas, cada grupo por orden de llegada", correcto);
        verificar("Atender un reclamo no lo elimina del registro general", g.cantidadReclamos() == 16);
        verificar("Pendientes después de atender 7 reclamos", g.cantidadPendientes() == 9);
        verificar("Consistencia después de atender", g.verificarConsistencia().isEmpty());
    }

    private static void cambioDePrioridadReordenaLaCola() {
        GestorReclamos g = gestorConDatos();
        g.modificarPrioridad("REC-105", Prioridad.URGENTE);
        List<Reclamo> orden = g.pendientesEnOrdenDeAtencion();
        verificar("REC-105 pasó de BAJA a URGENTE y, al ser la urgente que llegó primero, sale de primero",
                orden.get(0).getCodigo().equals("REC-105")
                        && orden.get(1).getCodigo().equals("REC-112")
                        && orden.get(2).getCodigo().equals("REC-102")
                        && orden.get(3).getCodigo().equals("REC-104")
                        && orden.get(4).getCodigo().equals("REC-103"));
        verificar("Consistencia después de cambiar prioridad", g.verificarConsistencia().isEmpty());
    }

    private static void eliminarMantieneConsistencia() {
        GestorReclamos g = gestorConDatos();
        g.eliminarReclamo("REC-112");
        verificar("El reclamo eliminado no se encuentra por código", !g.buscarReclamo("REC-112").isPresent());
        verificar("El reclamo eliminado no aparece en la cola",
                !colaContiene(g, "REC-112"));
        verificar("Tamaños: lista 15, TreeSet 15, HashMap 15, cola 15",
                g.cantidadReclamos() == 15 && g.cantidadOrdenados() == 15
                        && g.cantidadIndexados() == 15 && g.cantidadPendientes() == 15);
        verificar("Consistencia después de eliminar", g.verificarConsistencia().isEmpty());
        verificar("Eliminar un código inexistente lanza error", lanzaReclamoException(() -> g.eliminarReclamo("REC-112")));
    }

    private static void validacionesRechazanDatosInvalidos() {
        GestorReclamos g = gestorConDatos();
        verificar("Cliente inexistente en un reclamo se rechaza",
                lanzaReclamoException(() -> g.registrarReclamo("REC-200", "99999999", "Garantia",
                        "Reclamo de un cliente que no existe", Prioridad.ALTA)));
        verificar("Tipo no registrado se rechaza",
                lanzaReclamoException(() -> g.registrarReclamo("REC-201", "1045678901", "Inventado",
                        "Reclamo con un tipo que no existe", Prioridad.ALTA)));
        verificar("Descripción demasiado corta se rechaza",
                lanzaReclamoException(() -> g.registrarReclamo("REC-202", "1045678901", "Garantia",
                        "corta", Prioridad.ALTA)));
        verificar("Código vacío se rechaza",
                lanzaReclamoException(() -> g.registrarReclamo("  ", "1045678901", "Garantia",
                        "Reclamo sin código asignado", Prioridad.ALTA)));
        verificar("Documento de cliente repetido se rechaza",
                lanzaReclamoException(() -> g.registrarCliente("1045678901", "Otra Persona", "otra@correo.com")));
        boolean prioridadInvalida = false;
        try {
            Prioridad.desdeTexto("9");
        } catch (IllegalArgumentException e) {
            prioridadInvalida = true;
        }
        verificar("Prioridad inválida se rechaza", prioridadInvalida);
    }
}
