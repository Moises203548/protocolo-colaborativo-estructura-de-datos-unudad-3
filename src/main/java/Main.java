import servicio.GestorReclamos;
import ui.MenuConsola;

public class Main {

    public static void main(String[] args) {
        GestorReclamos gestor = new GestorReclamos();
        new MenuConsola(gestor).iniciar();
    }
}
