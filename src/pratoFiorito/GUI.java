package pratoFiorito;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.MouseInputListener;
import pratoFiorito.Tabella.Casella;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;

public class GUI extends JFrame{ //classe che si occupa della gestione della GUI
    private static JFrame win = new JFrame("Prato Fiorito"); //inizializzo la finestra principale
    private static Container c = win.getContentPane(); //inizializzo il container della finestra
    private static JMenuBar menubar = new JMenuBar(); //inizializzo la barra superiore dei menu
    private static JMenu vista = new JMenu("Vista"); //inizializzo il menu vista
    private static JMenu tema = new JMenu("Tema"); //inizializzo il sottomenu tema
    private static JMenuItem temaScuro = new JMenuItem("Tema scuro"); //inizializzo l'oggetto del menu
    private static JMenuItem temaChiaro = new JMenuItem("Tema chiaro"); //inizializzo l'oggetto del menu
    private static JMenu aiuto = new JMenu("Aiuto"); //inizializzo il menu aiuto
    private static JMenuItem regole = new JMenuItem("Regole"); //inizializzo l'oggetto del menu
    private static PannelloSelezDifficolta pannello = new PannelloSelezDifficolta(); //inizializzo il pannello di selezione difficolta
    private static JLabel[] caselle; //array che contiene le caselle del campo di gioco
    private static BufferedImage immagine; //immagine provvisoria in cui salvo il fiore prima di ridimensionarlo
    private static ImageIcon fiore; //variabile definitiva per l'immagine del fiore
    private static JPanel campoGioco; //pannello che contiene il campo di gioco
    private static Color darkColor = new Color(45, 45, 45);
    private static Color lightColor = new Color(220, 220, 220);
    private static Color caselleBgDarkColor = new Color(75, 75, 75);
    private static Color caselleBgLightColor = new Color(230, 230, 230);
    private static Color menuBarDarkColor = new Color(45, 45, 45);
    private static JLabel istruzioni = new JLabel();
    private static Casella[] tabella;
    private static PannelloGameOver gameOver = new PannelloGameOver();
    private static PannelloVittoria vittoria = new PannelloVittoria();

    public static void inizializzaGui(){ //metodo che inizializza la GUI
        inizializzaFinestraPrincipale(); //inizializzo la finestra di gioco
        pannello.inizializzaSelezioneDifficolta(); //inizializzo il pannello di selezione difficoltà
        try {
            immagine = ImageIO.read(GUI.class.getResource("fiore.png")); //ottengo la risorsa dell'immagine in modo che 
            fiore = new ImageIcon(new ImageIcon(immagine).getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT)); //ridimensiono l'immagine
        } catch (IOException e) {
            Log.log("Eccezione rilevata nella classe GUI, metodo inizializzaGui(): problema nell'elaborazione dell'immagine", Log.ERRORE);
        }
        istruzioni.setHorizontalAlignment(SwingConstants.CENTER);
        //formatto le istruzioni di modo che vadano a capo nel JOptionPane
        istruzioni.setText("<html><p style=\"width:250px\">"+ "Il campo di gioco consiste in un campo rettangolare (o quadrato)\na sua volta suddiviso in tanti piccoli quadrati. Ogni quadrato viene ripulito, o scoperto,\ncliccando su di esso.  Molti quadrati\ncontengono mine: quando viene cliccato un quadrato con una mina,\nessa esploderà e farà terminare il gioco. Se il quadrato\ncliccato non contiene una mina, possono accadere due eventi.\nSe nel quadrato appare un numero, ciò indica la quantità di quadrati adiacenti\n(inclusi quelli in diagonale) che contengono mine, che possono essere da 1 a 8.\nSe non appare nessun numero significa che tutti gli 8 quadrati adiacenti non contengono una mina:\nin questo caso il gioco ripulisce automaticamente tutti i quadrati vuoti\nadiacenti a quello cliccato\n(fino a quando non si arriva a quadrati che contengano un numero).\nSi vince la partita quando tutti i quadrati che non contengono mine saranno individuati." +"</p></html>");
    }

    public static void setWinVisibile(boolean visibile){ //metodo per rendere la finestra principale visibile o invisibile
        win.setLocationRelativeTo(null); //la centro nello schermo
        win.setVisible(visibile); //la rendo visibile
    }

    public static void setCampoGiocoDim(int colonne, int righe){ //metodo per impostare le dimensioni del campo di gioco
        campoGioco = new JPanel(new GridLayout(righe, colonne)); //assegno al pannello campoGioco un layout a griglia delle dimensioni desiderate
        campoGioco.setCursor(new Cursor(Cursor.HAND_CURSOR));
        c.add(campoGioco, BorderLayout.CENTER); //aggiungo il pannello alla finestra, o meglio al pannello principale
    }

    public static void setWinSize(int width, int height){ //metodo per cambiare le dimensioni della finestra originale
        win.setSize(width, height); //imposto le dimensioni
        win.setResizable(false); //le rendo immutabili
    }

    public static void aggiungiCaselle(Casella[] tab){ //metodo che aggiunge le JLabel al pannello campoGioco
        caselle = new JLabel[tab.length]; //assegno all'array caselle la dimensione dell'array tab
        tabella = Arrays.copyOf(tab, tab.length);

        for(int i = 0; i < caselle.length; i++){ //per ogni casella
            caselle[i] = new JLabel(); //la inizializzo
            caselle[i].setHorizontalAlignment(SwingConstants.CENTER); //faccio in modo che testo e immagine al suo interno siano centrati
            caselle[i].setBorder(BorderFactory.createLineBorder(Color.BLACK, 1)); //creo il bordo attorno ad essa
            caselle[i].setBackground(caselleBgLightColor); //imposto il colore di background
            caselle[i].setOpaque(true); //la rendo opaca, cosicché si veda il suo colore di sottofondo
            caselle[i].addMouseListener(new MouseInputListener() { //aggiungo a ogni casella un listener in grado di identificare quale casella è stata cliccata
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    for(int i = 0; i < caselle.length; i++){
                        if(e.getSource() == caselle[i]){
                            Log.log("Casella cliccata: " + i, Log.MESSAGGIO);
                            if(GameHandler.controllaCasella(i) == 0){
                                for(int j = 0; j < caselle.length; j++){
                                    if(tabella[j].getMina()) tabella[j].setVisibile(true);
                                }
                                mostraCaselle();
                                gameOver();
                            }
                            else{
                                mostraCaselle();
                                if(GameHandler.controllaVittoria()) vittoria();
                            }
                        }
                    }
                }

                @Override
                public void mousePressed(java.awt.event.MouseEvent e) {
                    // TODO Auto-generated method stub
                    
                }

                @Override
                public void mouseReleased(java.awt.event.MouseEvent e) {
                    // TODO Auto-generated method stub
                    
                }

                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) { //all'ingresso del mouse nella casella, ne cambio il colore per evidenziarla
                    JLabel casAttuale = (JLabel) e.getSource();
                    if(casAttuale.getBackground() == caselleBgDarkColor) casAttuale.setBackground(new Color(110, 110, 110));
                    else casAttuale.setBackground(new Color(240, 240, 255));
                    win.repaint();
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) { //all'uscita del mouse da casella, riporto lo sfondo della casella allo stato originale
                    JLabel casAttuale = (JLabel) e.getSource();
                    if(menubar.getBackground() == menuBarDarkColor) casAttuale.setBackground(caselleBgDarkColor);
                    else casAttuale.setBackground(caselleBgLightColor);
                    win.repaint();
                }

                @Override
                public void mouseDragged(java.awt.event.MouseEvent e) {
                    // TODO Auto-generated method stub
                    
                }

                @Override
                public void mouseMoved(java.awt.event.MouseEvent e) {
                    //TODO
                    
                }

            });
            campoGioco.add(caselle[i]); //aggiungo la casella al pannello
        }

        mostraCaselle();
        Log.log("Dimensione caselle: " + caselle[0].getWidth() + ", " + caselle[0].getHeight(), Log.INFO);
    }

    public static void mostraCaselle(){
        for(int i = 0; i < caselle.length; i++){
            if(tabella[i].getVisibile()){
                if(tabella[i].getMina()){ //se la casella contiene una mina
                    caselle[i].setIcon(fiore); //inserisco al suo interno l'immagine del fiore
                }
                else{
                    caselle[i].setText(Integer.toString(tabella[i].getnMineAdiacenti())); //altrimenti scrivo al suo interno il numero di mine ad essa adiacenti
                }
            }
            else{
                caselle[i].setIcon(null);
                caselle[i].setText(null);
                if(menubar.getBackground() == menuBarDarkColor) caselle[i].setBackground(caselleBgDarkColor);
                else caselle[i].setBackground(caselleBgLightColor);
            }
        }
        win.repaint();
    }

    public static void gameOver(){
        gameOver.inizializzaPannello();
    }

    public static void vittoria(){
        vittoria.inizializzaPannello();
    }

    private static void inizializzaFinestraPrincipale(){
        c.setLayout(new BorderLayout()); //imposto il layout della finestra a un layout composto da 4 zone (nord, sud, est e ovest)

        //aggiungo alla barra dei menu il menu vista e quello aiuto
        menubar.add(vista); 
        menubar.add(aiuto);
        
        //aggiungo al menu vista il sottomenu tema e a tema gli elementi temaChiaro e temaScuro
        vista.add(tema);
        temaScuro.setCursor(new Cursor(Cursor.HAND_CURSOR));
        temaChiaro.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tema.add(temaScuro);
        tema.add(temaChiaro);

        //aggiungo al menu aiuto l'elemento regole
        regole.setCursor(new Cursor(Cursor.HAND_CURSOR));
        aiuto.add(regole);

        //setto il messaggio restituito all'action listener da parte degli elementi del menu
        temaScuro.setActionCommand("temaScuro");
        temaChiaro.setActionCommand("temaChiaro");
        regole.setActionCommand("regole");

        //aggiungo a tali elementi l'action listener
        temaScuro.addActionListener(new MenuItemListener());
        temaChiaro.addActionListener(new MenuItemListener());
        regole.addActionListener(new MenuItemListener());

        //aggiungo la menubar completa alla parte nord della finestra
        c.add(menubar, BorderLayout.NORTH);
        win.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); 

        //imposto i colori corretti della finestra
        c.setBackground(lightColor);
        menubar.setBackground(Color.WHITE);
        tema.setBackground(lightColor);
        vista.setBackground(lightColor);
        tema.setBackground(lightColor);
        aiuto.setBackground(lightColor);

        //aggiungo shortcut agli elementi del menu
        temaScuro.setAccelerator(KeyStroke.getKeyStroke('D', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        temaChiaro.setAccelerator(KeyStroke.getKeyStroke('L', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        regole.setAccelerator(KeyStroke.getKeyStroke('R', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
    }

    private static class MenuItemListener implements ActionListener{ //action listener proprio degli elementi del menu
        @Override
        public void actionPerformed(ActionEvent e) {
            switch (e.getActionCommand()) {
                case "temaScuro": //se il bottone schiacciato è quello di tema chiaro o scuro, cambio tutti i colori
                    win.getContentPane().setBackground(darkColor);
                    c.setBackground(darkColor);
                    menubar.setBackground(menuBarDarkColor);
                    vista.setForeground(lightColor);
                    aiuto.setForeground(lightColor);
                    for(int i = 0; i < caselle.length; i++){
                        caselle[i].setBackground(caselleBgDarkColor);
                        caselle[i].setForeground(lightColor);
                        caselle[i].setBorder(BorderFactory.createLineBorder(lightColor, 1));
                    }
                    win.repaint();
                    break;
                
                case "temaChiaro":
                    win.getContentPane().setBackground(lightColor);
                    c.setBackground(lightColor);
                    menubar.setBackground(Color.WHITE);
                    vista.setForeground(Color.BLACK);
                    aiuto.setForeground(Color.BLACK);
                    for(int i = 0; i < caselle.length; i++){
                        caselle[i].setBackground(caselleBgLightColor);
                        caselle[i].setForeground(Color.BLACK);
                        caselle[i].setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                    }
                    win.repaint();
                    break;

                case "regole": //se l'elemento selezionato è quello delle regole, faccio spuntare il pannello con le istruzioni
                    JOptionPane.showMessageDialog(win, istruzioni, "Istruzioni", JOptionPane.INFORMATION_MESSAGE);
                    break;

                default:
                    break;
            }
        }
        
    }

    private static class PannelloSelezDifficolta{ //classe che gestisce la finestra di dialogo per la selezione della difficoltà: ciò è necessario per fare in modo che alla chiusura della finestra di dialogo si chiuda anche il programma (di base in Java la finestra si chiude ma il programma resta in esecuzione in background)
        private static JDialog selezDifficolta = new JDialog(win, "Selezione difficoltà"); //inizializzo la finestra di dialogo stessa
        private static JPanel flow = new JPanel(new FlowLayout()); //creo un pannello con un layout lineare
        private static JLabel messaggioDifficolta = new JLabel("Selezionare la difficoltà desiderata"); //creo la label che contiene il testo della finestra di dialogo

        private void inizializzaSelezioneDifficolta(){ //metodo che inizializza effettivamente la finestra di dialogo
            JButton pPrincipiante = new JButton("Principiante");
            JButton pIntermedio = new JButton("Intermedio");
            JButton pAvanzato = new JButton("Avanzato");
            ButtonListener bListener = new ButtonListener();
            windowAction catcher = new windowAction(); //creo l'oggetto che capterà le azioni effettuate sulla finestra (eg. chiusura, ridimensionamento... )

            selezDifficolta.addWindowListener(catcher); //aggiungo tale oggetto alla finestra di dialogo
            selezDifficolta.setAlwaysOnTop(true); //faccio in modo che la finestra di dialogo sia sopra alla finestra principale
            selezDifficolta.setLayout(new BorderLayout()); //assegno alla finestra di dialogo lo stesso layout a zone della finestra principale
            messaggioDifficolta.setHorizontalAlignment(SwingConstants.CENTER); //faccio in modo che la jlabel sia centrata orizzontalmente
            selezDifficolta.add(messaggioDifficolta, BorderLayout.NORTH); //metto la jlabel nella parte superiore della finestra
            selezDifficolta.add(flow, BorderLayout.CENTER); //assegno il pannello con layout lineare alla parte centrale della finestra

            pPrincipiante.setCursor(new Cursor(Cursor.HAND_CURSOR));
            pIntermedio.setCursor(new Cursor(Cursor.HAND_CURSOR));
            pAvanzato.setCursor(new Cursor(Cursor.HAND_CURSOR));

            //aggiungo a tale pannello i 3 bottoni con le difficoltà
            flow.add(pPrincipiante); 
            flow.add(pIntermedio);
            flow.add(pAvanzato);

            //assegno ad ogni bottone un action listener, che capterà la pressione del bottone
            pPrincipiante.addActionListener(bListener);
            pIntermedio.addActionListener(bListener);
            pAvanzato.addActionListener(bListener);

            pPrincipiante.setActionCommand("principiante");
            pIntermedio.setActionCommand("intermedio");
            pAvanzato.setActionCommand("avanzato");

            selezDifficolta.setSize(400, 100); //imposto le dimensioni della finestra
            selezDifficolta.setLocationRelativeTo(null); //la centro
            selezDifficolta.setResizable(false); //la rendo non ridimensionabile
            selezDifficolta.setVisible(true); //la rendo visibile
        }

        private static class ButtonListener implements ActionListener{ //classe che si occupa di interpretare la pressione dei bottoni del pannello di selezione difficoltà
            @Override
            public void actionPerformed(ActionEvent e) {
                //rendo il pannello invisibile e lo disabilito
                selezDifficolta.setVisible(false); 
                selezDifficolta.setEnabled(false);
                //chiamo il metodo di GameHandler che si occuperà di impostare la difficoltà
                GameHandler.setDifficolta(e.getActionCommand());
            }
            
        }

        private static class windowAction implements WindowListener{ //classe per captare le azioni della finestra (l'unica azione su cui intervengo è la chiusura della finestra)
            @Override
            public void windowOpened(WindowEvent e) {
                //metodo vuoto in quanto non necessario ma obbligatorio
            }
        
            @Override
            public void windowClosing(WindowEvent e) { //alla chiusura della finestra forzo l'arresto del programma
                System.exit(0);
                
            }
        
            @Override
            public void windowClosed(WindowEvent e) {
                //metodo vuoto in quanto non necessario ma obbligatorio
            }
        
            @Override
            public void windowIconified(WindowEvent e) {
                //metodo vuoto in quanto non necessario ma obbligatorio
            }
        
            @Override
            public void windowDeiconified(WindowEvent e) {
                //metodo vuoto in quanto non necessario ma obbligatorio
            }
        
            @Override
            public void windowActivated(WindowEvent e) {
                //metodo vuoto in quanto non necessario ma obbligatorio
            }
        
            @Override
            public void windowDeactivated(WindowEvent e) {
                //metodo vuoto in quanto non necessario ma obbligatorio
            }
        }
    }

    private static class PannelloGameOver{ //classe della finestra di dialogo di game over
        private static JDialog gameOver = new JDialog(win, "Game over"); //finestra di dialogo
        private static JPanel bottoni = new JPanel(new FlowLayout()); //pannello in cui aggiungere i bottoni
        private static JLabel messaggio = new JLabel("Game over!", SwingConstants.CENTER); //messaggio presente nella finestra

        private void inizializzaPannello(){
            JButton ok = new JButton("Ok");
            JButton restart = new JButton("Riavvia gioco");
            ButtonListener bListener = new ButtonListener();
            windowAction catcher = new windowAction();

            //aggiungo alla finestra il messaggio e il pannello che conterrà i bottoni
            gameOver.addWindowListener(catcher);
            gameOver.setLayout(new BorderLayout());
            gameOver.add(messaggio, BorderLayout.NORTH);
            
            ok.addActionListener(bListener);
            restart.addActionListener(bListener);

            //aggiungo al pannello i bottoni
            bottoni.add(ok);
            bottoni.add(restart);
            Log.log("Bottone aggiunto", Log.MESSAGGIO);

            gameOver.add(bottoni, BorderLayout.CENTER);

            //imposto il messaggio restituito all'action listener dai bottoni
            ok.setActionCommand("ok");
            restart.setActionCommand("restart");

            //aggiungo ai bottoni il loro action listener
            

            //rendo la finestra di dialogo visibile
            win.setEnabled(false);
            gameOver.setAlwaysOnTop(true);
            gameOver.setSize(300, 100);
            gameOver.setLocationRelativeTo(null);
            gameOver.setResizable(false);
            gameOver.setVisible(true);
        }

        private static class windowAction implements WindowListener{ //classe per captare le azioni della finestra (l'unica azione su cui intervengo è la chiusura della finestra)
            @Override
            public void windowOpened(WindowEvent e) {
                //metodo vuoto in quanto non necessario ma obbligatorio
            }
        
            @Override
            public void windowClosing(WindowEvent e) { //alla chiusura della finestra forzo l'arresto del programma
                System.exit(0);
            }
        
            @Override
            public void windowClosed(WindowEvent e) {
                System.exit(0);
            }
        
            @Override
            public void windowIconified(WindowEvent e) {
                //metodo vuoto in quanto non necessario ma obbligatorio
            }
        
            @Override
            public void windowDeiconified(WindowEvent e) {
                //metodo vuoto in quanto non necessario ma obbligatorio
            }
        
            @Override
            public void windowActivated(WindowEvent e) {
                //metodo vuoto in quanto non necessario ma obbligatorio
            }
        
            @Override
            public void windowDeactivated(WindowEvent e) {
                //metodo vuoto in quanto non necessario ma obbligatorio
            }
        }   

        private static class ButtonListener implements ActionListener{ //action listener
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getActionCommand() == "ok"){ //se il bottone schiacciato è ok esco dal programma
                    System.exit(0);
                }
                else{ //altrimenti elimino tutte le finestre generate e riavvio il gioco
                    try {
                        restartApplication();
                    } catch (URISyntaxException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            }
            
        }
    }

    private static class PannelloVittoria{
        private static JDialog vittoria = new JDialog(win, "Vittoria");
        private static JLabel messaggio = new JLabel("Vittoria!", SwingConstants.CENTER);
        private static JPanel bottoni = new JPanel(new FlowLayout());

        private void inizializzaPannello(){
            JButton okVit = new JButton("Ok");
            JButton riavviaVit = new JButton("Riavvia gioco");

            vittoria.setLayout(new BorderLayout());
            vittoria.add(messaggio, BorderLayout.NORTH);
            vittoria.add(bottoni, BorderLayout.CENTER);

            bottoni.add(okVit);
            bottoni.add(riavviaVit);

            okVit.setActionCommand("ok");
            riavviaVit.setActionCommand("riavvia");

            okVit.addActionListener(new ButtonListener());
            riavviaVit.addActionListener(new ButtonListener());

            win.setEnabled(false);
            vittoria.setAlwaysOnTop(true);
            vittoria.setSize(300, 100);
            vittoria.setLocationRelativeTo(null);
            vittoria.setResizable(false);
            vittoria.setVisible(true);
        }

        private static class ButtonListener implements ActionListener{ //action listener
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getActionCommand() == "ok"){ //se il bottone schiacciato è ok esco dal programma
                    System.exit(0);
                }
                else{ //altrimenti elimino tutte le finestre generate e riavvio il gioco
                    try {
                        restartApplication();
                    } catch (URISyntaxException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            }
        }
    }

    public static void restartApplication() throws URISyntaxException, IOException {
        final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        final File currentJar = new File(GUI.class.getProtectionDomain().getCodeSource().getLocation().toURI());

        /* is it a jar file? */
        if(!currentJar.getName().endsWith(".jar"))
          return;

        /* Build command: java -jar application.jar */
        final ArrayList<String> command = new ArrayList<String>();
        command.add(javaBin);
        command.add("-jar");
        command.add(currentJar.getPath());

        final ProcessBuilder builder = new ProcessBuilder(command);
        builder.start();
        System.exit(0);
    }
}
