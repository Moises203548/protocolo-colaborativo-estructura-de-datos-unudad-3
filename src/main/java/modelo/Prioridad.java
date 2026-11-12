package modelo;


public enum Prioridad {
    URGENTE(1),
    ALTA(2),
    NORMAL(3),
    BAJA(4);

    private final int nivel;

    Prioridad(int nivel) {
        this.nivel = nivel;
    }

    public int getNivel() {
        return nivel;
    }

    public static Prioridad desdeTexto(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("La prioridad es obligatoria.");
        }
        String valor = texto.trim();
        for (Prioridad p : values()) {
            if (String.valueOf(p.nivel).equals(valor) || p.name().equalsIgnoreCase(valor)) {
                return p;
            }
        }
        throw new IllegalArgumentException(
                "Prioridad inválida: \u0027" + valor + "\u0027. Use 1 (URGENTE), 2 (ALTA), 3 (NORMAL) o 4 (BAJA).");
    }
}
