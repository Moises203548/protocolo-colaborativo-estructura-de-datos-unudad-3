package comparadores;

import java.util.Comparator;
import modelo.Reclamo;

/**
 * Criterio de atención de la PriorityQueue:
 * 1. Primero el reclamo con prioridad más importante (URGENTE antes que BAJA).
 * 2. Si tienen la misma prioridad, primero el que llegó antes.
 */
public class ComparadorPrioridad implements Comparator<Reclamo> {

    @Override
    public int compare(Reclamo a, Reclamo b) {
        int porPrioridad = Integer.compare(a.getPrioridad().getNivel(), b.getPrioridad().getNivel());
        if (porPrioridad != 0) {
            return porPrioridad;
        }
        return Long.compare(a.getNumeroLlegada(), b.getNumeroLlegada());
    }
}
