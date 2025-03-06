package pratoFiorito;
public class GameHandler{ //classe che si occuperà di gestire ogni aspetto del gioco
    private static int colonne;
    private static int righe;
    private static int nMine;
    private static Tabella tab;

    public static void inizializzaGioco(){
        GUI.inizializzaGui(); //inizializzo la GUI
    }

    public static void setDifficolta(String difficolta){ //metodo che rende effettiva la selezione della difficoltà
        Log.log(difficolta, Log.MESSAGGIO);

        //in base alla difficoltà, imposto di conseguenza la dimensione del campo di gioco e il numero di mine
        if(difficolta == "principiante"){ 
            colonne = 9;
            righe = 9;
            nMine = 10;
            GUI.setWinSize(450, 450);
        }

        if(difficolta == "intermedio"){
            colonne = 16;
            righe = 16;
            nMine = 40;
            GUI.setWinSize(800, 800);
        }

        if(difficolta == "avanzato"){
            colonne = 30;
            righe = 16;
            nMine = 99;
            GUI.setWinSize(1500, 800);
        }

        GUI.setCampoGiocoDim(colonne, righe); //chiamo il metodo che cambia la dimensione del campo di gioco per renderlo adatto alla difficoltà corrente

        tab = new Tabella(colonne, righe, nMine); //inizializzo la tabella con i valori appena impostati

        GUI.setWinVisibile(true); //rendo la finestra di gioco principale visibile
        stampaCampo();
    }

    public static int controllaCasella(int index){
        if(tab.getMina(index)){
            tab.setVisibile(true, index);
            return 0;
        }
        
        if(tab.getnMineAdiacenti(index) != 0){
            tab.setVisibile(true, index);
            return 1;
        }
        else{
            controllaCasellaVuota(index);
            return 1;
        }
    }

    public static boolean controllaVittoria(){
        int contatore = 0;

        for(int i = 0; i < colonne*righe; i++){
            if(!tab.getVisibile(i)) contatore++;
        }

        if(contatore == nMine) return true;
        else return false;
    }

    public static void stampaCampo(){ //metodo per il debug che consente di vedere il campo di gioco sul terminale
        boolean bordoDestro = false;

        for(int i = 0; i < colonne*righe; i++){
            System.out.print("|");

            if(tab.getMina(i)) System.out.print("x");
            else System.out.print(tab.getnMineAdiacenti(i));

            System.out.print("|");

            for(int j = 1; j <= righe; j++){ //controllo che la casella non appartenga al bordo destro, verificando che essa non sia uguale ad un numero divisibile per nColonne - 1 (es. 8 colonne --> numero diverso da 7;15;23...)
                if(i == (j*colonne) - 1){
                    bordoDestro = true;
                    break;
                } 
            }

            if(bordoDestro) System.out.print("\n");
            bordoDestro = false;
        }
    }

    private static void controllaCasellaVuota(int index){ //metodo per scoprire gruppi di caselle vuote con 0 mine adiacenti
        boolean bordoDestro = false;

        tab.setVisibile(true, index);
        if(tab.getnMineAdiacenti(index) == 0){ //sfrutto la ricorsione: controllo le 8 caselle adiacenti alla casella e le 8 adiacenti a quella controllata e così via
            if(index % colonne != 0){
                if(index - 1 >= 0){
                    if(!tab.getVisibile(index - 1)) controllaCasellaVuota(index - 1);
                }
                if(index - (colonne + 1) >= 0){
                    if(!tab.getVisibile(index - (colonne + 1))) controllaCasellaVuota(index - (colonne + 1));
                }
                if(index + (colonne - 1) < colonne*righe){
                    if(!tab.getVisibile(index + (colonne - 1))) controllaCasella(index + (colonne - 1));
                }
            }

            for(int j = 1; j <= righe; j++){ //controllo che la casella non appartenga al bordo destro, verificando che essa non sia uguale ad un numero divisibile per nColonne - 1 (es. 8 colonne --> numero diverso da 7;15;23...)
                if(index == (j*colonne) - 1){
                    bordoDestro = true;
                    break;
                } 
            }

            if(!bordoDestro){
                if(index + 1 < colonne*righe){
                    if(!tab.getVisibile(index + 1)) controllaCasellaVuota(index + 1);
                }
                if(index + (colonne + 1) < colonne*righe){
                    if(!tab.getVisibile(index + (colonne + 1))) controllaCasellaVuota(index + (colonne + 1));
                }
                if(index - (colonne - 1) >= 0){
                    if(!tab.getVisibile(index - (colonne - 1))) controllaCasellaVuota(index - (colonne - 1));
                }
            }

            if(index - colonne >= 0){
                if(!tab.getVisibile(index - colonne)) controllaCasellaVuota(index - colonne);
            }

            if(index + colonne < colonne*righe){
                if(!tab.getVisibile(index + colonne)) controllaCasellaVuota(index + colonne);
            }
        }
    }
}
