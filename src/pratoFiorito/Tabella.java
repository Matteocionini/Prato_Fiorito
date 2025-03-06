package pratoFiorito;
import java.util.Arrays;
import java.util.Random;

public class Tabella {
    private int colonne;
    private int righe;
    private Casella[] tab;
    private Random rand = new Random();

    public Tabella(int colonne, int righe, int nMine) { //costruttore della classe Tabella: inizializzo il numero di righe e colonne della tabella e creo l'array di caselle
        this.colonne = colonne;
        this.righe = righe;
        tab = new Casella[colonne*righe];

        for(int i = 0; i < tab.length; i++){
            tab[i] = new Casella();
        }

        posizMine(nMine);
        setDistanzaMine();
        GUI.aggiungiCaselle(Arrays.copyOf(tab, tab.length)); //aggiungo le caselle alla GUI
    }

    public boolean getMina(int index){
        return tab[index].getMina();
    }

    public int getnMineAdiacenti(int index){
        return tab[index].getnMineAdiacenti();
    }

    public void setVisibile(boolean visibile, int index){
        tab[index].setVisibile(visibile);
    }

    public boolean getVisibile(int index){
        return tab[index].getVisibile();
    }

    private void posizMine(int nMine){
        int colonna;
        int riga;
        
        for(int i = 0; i < nMine; i++){ //finché ci sono mine da piazzare
            do{
                //genero delle coordinate casuali
                colonna = rand.nextInt(colonne);
                riga = rand.nextInt(righe);
            } while(tab[riga*colonne + colonna].getMina()); //controllo che non siano occupate

            tab[riga*colonne + colonna].setMina(); //piazzo la mina
        }
    }


    private void setDistanzaMine(){
        int counter = 0; //contatore del numero di mine adiacenti alla casella
        boolean bordoDestro = false; //boolean che diventa vera qualora la casella fosse appartenente al bordo destro (uguale a un numero divisibile per il numero di colonne - 1)
        for(int i = 0; i < tab.length; i++){ //per ogni casella
            if(!tab[i].getMina()){ //se non contiene una mina controllo quante mine sono adiacenti alla stessa
                if(i % colonne != 0){ //se la casella non appartiene al bordo sinistro
                    if(i - 1 >= 0){ //controllo la casella dietro di essa 
                        if(tab[i - 1].getMina()) counter ++;
                    }

                    if(i - (colonne + 1) >= 0){ //controllo la casella in alto a sinistra
                        if(tab[i - (colonne + 1)].getMina()) counter ++;
                    }

                    if(i + (colonne - 1) < colonne* righe){ //controllo la casella in basso a sinistra
                        if(tab[i + (colonne - 1)].getMina()) counter ++;
                    }
                }


                for(int j = 1; j <= righe; j++){ //controllo che la casella non appartenga al bordo destro, verificando che essa non sia uguale ad un numero divisibile per nColonne - 1 (es. 8 colonne --> numero diverso da 7;15;23...)
                    if(i == (j*colonne) - 1){
                        bordoDestro = true;
                        break;
                    } 
                }

                if(!bordoDestro){ //se la casella non appartiene al bordo destro
                    if(i + 1 < colonne*righe){ //controllo la casella davanti
                        if(tab[i + 1].getMina()) counter ++;
                    }

                    if(i + (colonne + 1) < colonne*righe){ //controllo la casella in basso a destra
                        if(tab[i + (colonne + 1)].getMina()) counter ++;
                    }

                    if(i - (colonne - 1) >= 0){ //controllo la casella in alto a destra
                        if(tab[i - (colonne - 1)].getMina()) counter ++;
                    }
                }
                
                //in qualsiasi caso
                if(i - colonne >= 0){ //controllo la casella direttamente sopra
                    if(tab[i - colonne].getMina()) counter ++;
                }

                if(i + colonne < colonne*righe){ //controllo la casella direttamente sotto
                    if(tab[i + colonne].getMina()) counter ++;
                }

                
                tab[i].setnMineAdiacenti(counter);
            }
            counter = 0;
            bordoDestro = false;
        }
    }

    public static class Casella { //classe privata delle classe tabella che rappresenta le singole caselle
        private boolean mina; //se è vero c'è una mina, se è falso non c'è
        private int nMineAdiacenti;
        private boolean visibile = false;
        
        protected void setMina() {
            mina = true;
        }

        protected void setnMineAdiacenti(int nMineAdiacenti) {
            this.nMineAdiacenti = nMineAdiacenti;
        }
    
        protected int getnMineAdiacenti() {
            return nMineAdiacenti;
        }
    
        protected boolean getMina(){
            return mina;
        }

        protected void setVisibile(boolean visibile) {
            this.visibile = visibile;
        }

        protected boolean getVisibile(){
            return visibile;
        }
    }
}
