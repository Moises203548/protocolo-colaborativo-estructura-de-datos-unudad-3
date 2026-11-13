package ui;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import modelo.Cliente;
import modelo.Prioridad;
import modelo.Reclamo;
import servicio.GestorReclamos;
import servicio.ReclamoException;
import util.DatosPrueba;

public class MenuConsola {

    private final GestorReclamos gestor;
    private final Scanner entrada = new Scanner(System.in);
    private boolean datosCargados = false;

    public MenuConsola(GestorReclamos gestor) {
        this.gestor = gestor;
    }

    public void iniciar() {
        System.out.println("=== SISTEMA DE ATENCION DE RECLAMOS ===");
        int opcion;
        do {
            mostrarMenu();
            opcion = leerOpcion();
            try {
                ejecutar(opcion);
            } catch (ReclamoException | IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } while (opcion != 0);
    }

    private void mostrarMenu() {
        System.out.println();
        System.out.println(" 1. Registrar cliente");
        System.out.println(" 2. Registrar reclamo (entra a la cola de atención)");
        System.out.println(" 3. Consultar todos los reclamos");
        System.out.println(" 4. Buscar reclamo por código");
        System.out.println(" 5. Tipos de reclamo (registrar o consultar)");
        System.out.println(" 6. Mostrar reclamos ordenados por código");
        System.out.println(" 7. Consultar próximo reclamo a atender");
        System.out.println(" 8. Atender siguiente reclamo");
        System.out.println(" 9. Modificar reclamo");
        System.out.println("10. Eliminar reclamo");
        System.out.println("11. Mostrar información de las colecciones");
        System.out.println("12. Buscar cliente por documento");
        System.out.println("13. Cargar datos de prueba");
        System.out.println(" 0. Salir");
    }

    private void ejecutar(int opcion) {
        switch (opcion) {
            case 1:
                registrarCliente();
                break;
            case 2:
                registrarReclamo();
                break;
            case 3:
                imprimirReclamos("Todos los reclamos (orden de registro)", gestor.listarReclamos());
                break;
            case 4:
                buscarReclamo();
                break;
            case 5:
                gestionarTipos();
                break;
            case 6:
                imprimirReclamos("Reclamos ordenados por código", gestor.listarReclamosOrdenados());
                break;
            case 7:
                consultarProximo();
                break;
            case 8:
                atenderSiguiente();
                break;
            case 9:
                modificarReclamo();
                break;
            case 10:
                eliminarReclamo();
                break;
            case 11:
                mostrarColecciones();
                break;
            case 12:
                buscarCliente();
                break;
            case 13:
                cargarDatosPrueba();
                break;
            case 0:
                System.out.println("Hasta luego.");
                break;
            default:
                System.out.println("Opción no válida.");
        }
    }


    private void registrarCliente() {
        String documento = leer("Documento (6 a 12 dígitos): ");
        String nombre = leer("Nombre: ");
        String correo = leer("Correo: ");
        Cliente cliente = gestor.registrarCliente(documento, nombre, correo);
        System.out.println("Cliente registrado: " + cliente);
    }

    private void registrarReclamo() {
        String codigo = leer("Código del reclamo: ");
        String documento = leer("Documento del cliente: ");
        System.out.println("Tipos disponibles: " + gestor.listarTipos());
        String tipo = leer("Tipo de reclamo: ");
        String descripcion = leer("Descripción: ");
        Prioridad prioridad = leerPrioridad();
        Reclamo reclamo = gestor.registrarReclamo(codigo, documento, tipo, descripcion, prioridad);
        System.out.println("Reclamo registrado y en cola de atención:");
        System.out.println(reclamo.detalle());
    }

    private void buscarReclamo() {
        String codigo = leer("Código a buscar: ");
        Optional<Reclamo> reclamo = gestor.buscarReclamo(codigo);
        if (reclamo.isPresent()) {
            System.out.println("Reclamo encontrado (consulta directa por HashMap):");
            System.out.println(reclamo.get().detalle());
        } else {
            System.out.println("No existe ningún reclamo con el código " + codigo.trim().toUpperCase() + ".");
        }
    }

    private void gestionarTipos() {
        System.out.println("a) Registrar tipo   b) Consultar tipos");
        String opcion = leer("Opción: ").toLowerCase();
        if (opcion.equals("a")) {
            String tipo = leer("Nuevo tipo de reclamo: ");
            if (gestor.registrarTipo(tipo)) {
                System.out.println("Tipo registrado.");
            } else {
                System.out.println("Ese tipo ya existe; el Set no permite duplicados.");
            }
        } else if (opcion.equals("b")) {
            System.out.println("Tipos de reclamo (" + gestor.listarTipos().size() + "): " + gestor.listarTipos());
        } else {
            System.out.println("Opción no válida.");
        }
    }

    private void consultarProximo() {
        Optional<Reclamo> proximo = gestor.proximoReclamo();
        if (proximo.isPresent()) {
            System.out.println("Próximo reclamo a atender (peek, no se retira de la cola):");
            System.out.println(proximo.get().detalle());
        } else {
            System.out.println("No hay reclamos pendientes.");
        }
    }

    private void atenderSiguiente() {
        Reclamo atendido = gestor.atenderSiguiente();
        System.out.println("Reclamo atendido (poll):");
        System.out.println(atendido.detalle());
        System.out.println("Pendientes restantes: " + gestor.cantidadPendientes());
    }

    private void modificarReclamo() {
        String codigo = leer("Código del reclamo a modificar: ");
        System.out.println("a) Descripción   b) Tipo   c) Prioridad");
        String opcion = leer("Opción: ").toLowerCase();
        switch (opcion) {
            case "a":
                gestor.modificarDescripcion(codigo, leer("Nueva descripción: "));
                break;
            case "b":
                System.out.println("Tipos disponibles: " + gestor.listarTipos());
                gestor.modificarTipo(codigo, leer("Nuevo tipo: "));
                break;
            case "c":
                gestor.modificarPrioridad(codigo, leerPrioridad());
                break;
            default:
                System.out.println("Opción no válida.");
                return;
        }
        System.out.println("Reclamo actualizado:");
        System.out.println(gestor.buscarReclamo(codigo).get().detalle());
    }

    private void eliminarReclamo() {
        String codigo = leer("Código del reclamo a eliminar: ");
        Reclamo eliminado = gestor.eliminarReclamo(codigo);
        System.out.println("Reclamo " + eliminado.getCodigo() + " eliminado de todas las colecciones.");
    }

    private void buscarCliente() {
        String documento = leer("Documento a buscar: ");
        Optional<Cliente> cliente = gestor.buscarCliente(documento);
        if (cliente.isPresent()) {
            System.out.println("Cliente encontrado: " + cliente.get());
        } else {
            System.out.println("No existe un cliente con documento " + documento.trim() + ".");
        }
    }

    private void mostrarColecciones() {
        System.out.println("---- Estado de las colecciones ----");
        System.out.println("ArrayList  reclamos            : " + gestor.cantidadReclamos() + " elementos");
        System.out.println("HashMap    reclamosPorCodigo   : " + gestor.cantidadIndexados() + " claves");
        System.out.println("TreeSet    reclamosOrdenados   : " + gestor.cantidadOrdenados() + " elementos");
        System.out.println("PriorityQueue colaAtencion     : " + gestor.cantidadPendientes() + " pendientes");
        System.out.println("HashMap    clientesPorDocumento: " + gestor.cantidadClientes() + " clientes");
        System.out.println("LinkedHashSet tiposReclamo     : " + gestor.listarTipos().size() + " tipos");
        System.out.println();
        System.out.println("Claves del HashMap (sin orden garantizado): " + gestor.codigosIndexados());
        System.out.print("Cola en orden interno del montículo:        [");
        List<Reclamo> interno = gestor.colaEnOrdenInterno();
        for (int i = 0; i < interno.size(); i++) {
            System.out.print((i > 0 ? ", " : "") + interno.get(i).getCodigo() + "/" + interno.get(i).getPrioridad());
        }
        System.out.println("]");
        System.out.print("Cola en orden real de atención:             [");
        List<Reclamo> ordenados = gestor.pendientesEnOrdenDeAtencion();
        for (int i = 0; i < ordenados.size(); i++) {
            System.out.print((i > 0 ? ", " : "") + ordenados.get(i).getCodigo() + "/" + ordenados.get(i).getPrioridad());
        }
        System.out.println("]");
        List<String> problemas = gestor.verificarConsistencia();
        if (problemas.isEmpty()) {
            System.out.println("Consistencia entre colecciones: CORRECTA");
        } else {
            System.out.println("Consistencia entre colecciones: PROBLEMAS");
            for (String p : problemas) {
                System.out.println(" - " + p);
            }
        }
    }

    private void cargarDatosPrueba() {
        if (datosCargados) {
            System.out.println("Los datos de prueba ya fueron cargados.");
            return;
        }
        DatosPrueba.cargar(gestor);
        datosCargados = true;
        System.out.println("Datos de prueba cargados: " + gestor.cantidadClientes() + " clientes, "
                + gestor.listarTipos().size() + " tipos y " + gestor.cantidadReclamos() + " reclamos.");
    }


    private void imprimirReclamos(String titulo, List<Reclamo> lista) {
        System.out.println("---- " + titulo + " (" + lista.size() + ") ----");
        if (lista.isEmpty()) {
            System.out.println("No hay reclamos registrados.");
            return;
        }
        System.out.println(String.format("%-8s | %-9s | %-20s | %-9s | %-15s | %s",
                "CODIGO", "PRIORIDAD", "TIPO", "ESTADO", "CLIENTE", "DESCRIPCION"));
        for (Reclamo r : lista) {
            System.out.println(r);
        }
    }

    private Prioridad leerPrioridad() {
        System.out.println("Prioridad: 1=URGENTE 2=ALTA 3=NORMAL 4=BAJA");
        return Prioridad.desdeTexto(leer("Prioridad: "));
    }

    private int leerOpcion() {
        String texto = leer("Seleccione una opción: ");
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private String leer(String mensaje) {
        System.out.print(mensaje);
        if (!entrada.hasNextLine()) {
            System.out.println("\nFin de la entrada. Saliendo.");
            System.exit(0);
        }
        return entrada.nextLine();
    }
}
