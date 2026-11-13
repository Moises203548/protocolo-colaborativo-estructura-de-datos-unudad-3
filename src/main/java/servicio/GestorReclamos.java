package servicio;

import comparadores.ComparadorPrioridad;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import modelo.Cliente;
import modelo.Estado;
import modelo.Prioridad;
import modelo.Reclamo;


public class GestorReclamos {

    public static final int DESCRIPCION_MIN = 10;
    public static final int DESCRIPCION_MAX = 200;

    private final List<Reclamo> reclamos = new ArrayList<>();
    private final Map<String, Reclamo> reclamosPorCodigo = new HashMap<>();
    private final SortedSet<Reclamo> reclamosOrdenados = new TreeSet<>();
    private final Queue<Reclamo> colaAtencion = new PriorityQueue<>(new ComparadorPrioridad());
    private final Map<String, Cliente> clientesPorDocumento = new HashMap<>();
    private final Set<String> tiposReclamo = new LinkedHashSet<>();
    private long contadorLlegada = 0;


    private String limpiar(String texto) {
        return texto == null ? "" : texto.trim();
    }

    /** Las claves (códigos y tipos) se guardan en mayúsculas para que "rec-1" y "REC-1" sean lo mismo. */
    private String normalizar(String texto) {
        return limpiar(texto).toUpperCase();
    }

    private Reclamo obtener(String codigo) {
        Reclamo r = reclamosPorCodigo.get(normalizar(codigo));
        if (r == null) {
            throw new ReclamoException("No existe un reclamo con código " + normalizar(codigo) + ".");
        }
        return r;
    }

    private String validarDescripcion(String descripcion) {
        String d = limpiar(descripcion);
        if (d.length() < DESCRIPCION_MIN || d.length() > DESCRIPCION_MAX) {
            throw new ReclamoException("La descripción debe tener entre " + DESCRIPCION_MIN
                    + " y " + DESCRIPCION_MAX + " caracteres.");
        }
        return d;
    }

    private String validarTipoExistente(String tipo) {
        String t = normalizar(tipo);
        if (!tiposReclamo.contains(t)) {
            throw new ReclamoException("El tipo '" + t + "' no está registrado. Regístrelo primero.");
        }
        return t;
    }


    public Cliente registrarCliente(String documento, String nombre, String correo) {
        String doc = limpiar(documento);
        if (!doc.matches("\\d{6,12}")) {
            throw new ReclamoException("El documento debe tener entre 6 y 12 dígitos numéricos.");
        }
        String nom = limpiar(nombre);
        if (nom.isEmpty()) {
            throw new ReclamoException("El nombre del cliente es obligatorio.");
        }
        String cor = limpiar(correo);
        if (!cor.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) {
            throw new ReclamoException("El correo no tiene un formato válido.");
        }
        if (clientesPorDocumento.containsKey(doc)) {
            throw new ReclamoException("Ya existe un cliente con documento " + doc + ".");
        }
        Cliente cliente = new Cliente(doc, nom, cor);
        clientesPorDocumento.put(doc, cliente);
        return cliente;
    }

    public Optional<Cliente> buscarCliente(String documento) {
        return Optional.ofNullable(clientesPorDocumento.get(limpiar(documento)));
    }

    public List<Cliente> listarClientes() {
        return new ArrayList<>(clientesPorDocumento.values());
    }


    /**
     * Registra un tipo de reclamo.
     * @return true si se agregó; false si ya existía (el Set no permite duplicados).
     */
    public boolean registrarTipo(String tipo) {
        String t = normalizar(tipo);
        if (t.isEmpty()) {
            throw new ReclamoException("El tipo de reclamo es obligatorio.");
        }
        if (t.length() > 30) {
            throw new ReclamoException("El tipo de reclamo no puede superar los 30 caracteres.");
        }
        return tiposReclamo.add(t);
    }

    public Set<String> listarTipos() {
        return Collections.unmodifiableSet(tiposReclamo);
    }


    /** Registra un reclamo y lo agrega a las cuatro colecciones que lo organizan. */
    public Reclamo registrarReclamo(String codigo, String documentoCliente, String tipo,
                                    String descripcion, Prioridad prioridad) {
        String cod = normalizar(codigo);
        if (!cod.matches("[A-Z0-9-]{3,15}")) {
            throw new ReclamoException("El código debe tener entre 3 y 15 caracteres (letras, números o guion).");
        }
        if (reclamosPorCodigo.containsKey(cod)) {
            throw new ReclamoException("Ya existe un reclamo con código " + cod + ".");
        }
        Cliente cliente = clientesPorDocumento.get(limpiar(documentoCliente));
        if (cliente == null) {
            throw new ReclamoException("No existe un cliente con documento " + limpiar(documentoCliente) + ".");
        }
        String tipoValido = validarTipoExistente(tipo);
        String desc = validarDescripcion(descripcion);
        if (prioridad == null) {
            throw new ReclamoException("La prioridad es obligatoria.");
        }

        Reclamo reclamo = new Reclamo(cod, cliente, tipoValido, desc, prioridad, ++contadorLlegada);
        reclamos.add(reclamo);
        reclamosPorCodigo.put(cod, reclamo);
        reclamosOrdenados.add(reclamo);
        colaAtencion.add(reclamo);
        return reclamo;
    }

    public Optional<Reclamo> buscarReclamo(String codigo) {
        return Optional.ofNullable(reclamosPorCodigo.get(normalizar(codigo)));
    }

    public boolean existeReclamo(String codigo) {
        return reclamosPorCodigo.containsKey(normalizar(codigo));
    }

    public List<Reclamo> listarReclamos() {
        return Collections.unmodifiableList(reclamos);
    }

    public List<Reclamo> listarReclamosOrdenados() {
        return new ArrayList<>(reclamosOrdenados);
    }


    /** Consulta el próximo reclamo a atender sin sacarlo de la cola (peek). */
    public Optional<Reclamo> proximoReclamo() {
        return Optional.ofNullable(colaAtencion.peek());
    }

    /** Saca de la cola el reclamo más prioritario (poll) y lo marca como ATENDIDO. */
    public Reclamo atenderSiguiente() {
        Reclamo reclamo = colaAtencion.poll();
        if (reclamo == null) {
            throw new ReclamoException("No hay reclamos pendientes por atender.");
        }
        reclamo.setEstado(Estado.ATENDIDO);
        return reclamo;
    }

    public int cantidadPendientes() {
        return colaAtencion.size();
    }

    /** Pendientes en el orden real de atención (se copia la cola para no alterarla). */
    public List<Reclamo> pendientesEnOrdenDeAtencion() {
        PriorityQueue<Reclamo> copia = new PriorityQueue<>(new ComparadorPrioridad());
        copia.addAll(colaAtencion);
        List<Reclamo> resultado = new ArrayList<>();
        while (!copia.isEmpty()) {
            resultado.add(copia.poll());
        }
        return resultado;
    }


    public void modificarDescripcion(String codigo, String nuevaDescripcion) {
        Reclamo reclamo = obtener(codigo);
        reclamo.setDescripcion(validarDescripcion(nuevaDescripcion));
    }

    public void modificarTipo(String codigo, String nuevoTipo) {
        Reclamo reclamo = obtener(codigo);
        reclamo.setTipo(validarTipoExistente(nuevoTipo));
    }

    public void modificarPrioridad(String codigo, Prioridad nueva) {
        Reclamo reclamo = obtener(codigo);
        if (nueva == null) {
            throw new ReclamoException("La prioridad es obligatoria.");
        }
        if (reclamo.getEstado() != Estado.PENDIENTE) {
            throw new ReclamoException("Solo se puede cambiar la prioridad de reclamos pendientes.");
        }
        colaAtencion.remove(reclamo);
        reclamo.setPrioridad(nueva);
        colaAtencion.add(reclamo);
    }


    /** Elimina el reclamo de TODAS las colecciones donde participa. */
    public Reclamo eliminarReclamo(String codigo) {
        Reclamo reclamo = obtener(codigo);
        reclamos.remove(reclamo);
        reclamosPorCodigo.remove(reclamo.getCodigo());
        reclamosOrdenados.remove(reclamo);
        colaAtencion.remove(reclamo);
        return reclamo;
    }


    public Set<String> codigosIndexados() {
        return Collections.unmodifiableSet(reclamosPorCodigo.keySet());
    }

    /** Contenido de la cola tal como está guardado en el montículo (no es el orden de atención). */
    public List<Reclamo> colaEnOrdenInterno() {
        return new ArrayList<>(colaAtencion);
    }

    public int cantidadReclamos() {
        return reclamos.size();
    }

    public int cantidadClientes() {
        return clientesPorDocumento.size();
    }

    public int cantidadOrdenados() {
        return reclamosOrdenados.size();
    }

    public int cantidadIndexados() {
        return reclamosPorCodigo.size();
    }

    /**
     * Revisa que las colecciones sigan sincronizadas.
     * @return lista de problemas encontrados; vacía si todo es consistente.
     */
    public List<String> verificarConsistencia() {
        List<String> problemas = new ArrayList<>();
        if (reclamos.size() != reclamosPorCodigo.size()) {
            problemas.add("La lista (" + reclamos.size() + ") y el HashMap (" + reclamosPorCodigo.size()
                    + ") tienen distinto tamaño.");
        }
        if (reclamos.size() != reclamosOrdenados.size()) {
            problemas.add("La lista (" + reclamos.size() + ") y el TreeSet (" + reclamosOrdenados.size()
                    + ") tienen distinto tamaño.");
        }
        int pendientes = 0;
        for (Reclamo r : reclamos) {
            if (reclamosPorCodigo.get(r.getCodigo()) != r) {
                problemas.add(r.getCodigo() + " no está bien indexado en el HashMap.");
            }
            if (!reclamosOrdenados.contains(r)) {
                problemas.add(r.getCodigo() + " no está en el TreeSet.");
            }
            if (r.getEstado() == Estado.PENDIENTE) {
                pendientes++;
                if (!colaAtencion.contains(r)) {
                    problemas.add(r.getCodigo() + " está pendiente pero no está en la PriorityQueue.");
                }
            }
        }
        if (pendientes != colaAtencion.size()) {
            problemas.add("Pendientes en la lista (" + pendientes + ") y tamaño de la cola ("
                    + colaAtencion.size() + ") no coinciden.");
        }
        for (Reclamo r : colaAtencion) {
            if (!reclamosPorCodigo.containsKey(r.getCodigo())) {
                problemas.add(r.getCodigo() + " está en la cola pero fue eliminado del sistema.");
            }
        }
        return problemas;
    }
}
