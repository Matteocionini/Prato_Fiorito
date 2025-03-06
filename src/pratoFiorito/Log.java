package pratoFiorito;

import java.time.format.DateTimeFormatter;

public class Log {
    public static final int MESSAGGIO = 0;
    public static final int AVVISO = 1;
    public static final int ERRORE = 2;
    public static final int INFO = 3;
    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static void log(String messaggio, int grado){
        System.out.print("[" + dtf.format(java.time.LocalTime.now()) + "] ");

        switch (grado) {
            case MESSAGGIO:
                System.out.print("MESSAGGIO: ");
                break;
            
            case AVVISO:
                System.out.print("!AVVISO!: ");
                break;

            case ERRORE:
                System.out.print("!!!ERRORE!!!: ");
                break;

            default:
                break;
        }

        System.out.println(messaggio);
    }
}
