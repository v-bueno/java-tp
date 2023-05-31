import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 */
public class JeuMultiv1 implements Graphique, ActionListener  {
    private ArrayList<Question> listequestionsA;
    private ArrayList<Question> listequestionsB;
    private int scoreA;
    private int scoreB;
    private Partie partie;
    private BoutonReponse bouton1;
    private BoutonReponse bouton2;
    private BoutonReponse bouton3;
    private BoutonReponse bouton4;
    private BoutonReponse[] boutons;
    private JLabel labelReponse=new JLabel();
    private JLabel labelEquipe;
    private JLabel labelQuestion;
    private JLabel labelScoreA;
    private JLabel labelScoreB;

    private JLabel labelTempsRestant;
    private Timer chrono;
    private int tempsRestant;
    private int equipeDuTour;
    private int equipeDuProchainTour;
    private boolean equipeAFinie;
    private boolean equipeBFinie;
    private String nomEquipeA;
    private String nomEquipeB;

    /**
     * Affiche une interface avec 4 boutons pour répondre à la première question de la liste de l'équipe qui joue.
     * Si la liste n'a qu'une question le jeu s'arrête ensuite. Sinon on rappelle JeuMultiv1 en supprimant la première
     * question afin d'en avoir une nouvelle et en faisant toutes les modifications pour la gestion des équipes.
     * @param questionsA liste de questions restantes de l'équipe A
     * @param questionsB liste de questions restantes de l'équipe B
     * @param scoreA score en cours de l'équipe A
     * @param scoreB score en cours de l'équipe B
     * @param equipeDuTour échange entre 0 et 1 à chaque appel afin de déterminer l'équipe qui doit jouer
     * @param derniereReponse réponse de la question en cours
     * @param partie partie permettant de connaître les paramètres et de pouvoir rejouer avec les mêmes par la suite
     */
    JeuMultiv1(ArrayList<Question> questionsA,ArrayList<Question> questionsB,int scoreA,int scoreB,String nomEquipeA,String nomEquipeB,boolean equipeAFinie,boolean equipeBFinie,int equipeDuTour, String derniereReponse,Partie partie) {

        this.scoreA=scoreA;
        this.scoreB=scoreB;
        this.partie=partie;
        this.listequestionsA=questionsA;
        this.listequestionsB=questionsB;
        this.equipeDuTour = equipeDuTour;
        this.equipeAFinie = equipeAFinie;
        this.equipeBFinie = equipeBFinie;
        this.nomEquipeA = nomEquipeA;
        this.nomEquipeB=nomEquipeB;
        //Détermine quelle équipe doit jouer
        if (equipeDuTour==0){
            joueEquipeA();
        }
        else{
            joueEquipeB();
        }
        Connexion.menuBar.setVisible(false); //pour ne pas avoir de probleme avec le timer et un nouvel appel de fonction

    }


    /**
     * Fait jouer l'équipe A. Elle joue tant que ça liste n'est pas épuisé, qu'elle a la bonne réponse ou qu'elle a la
     * mauvaise réponse mais que l'équipe B à sa liste de questions épuisée.
     */
    public void joueEquipeA(){
        this.equipeDuProchainTour=(equipeDuTour+1)%2;
        this.chrono = new Timer(true);
        this.tempsRestant = listequestionsA.get(0).getTemps();
        TimerTask timeOutTask = new TimerTask() {
            @Override
            public void run() {
                if (tempsRestant==1) { //S'il n'y a plus de temps pour répondre
                    if(listequestionsA.size()!=1) { //S'il n'y a plus de questions après
                        //Définie les paramètres
                        String derniereReponse = listequestionsA.get(0).getReponses()[listequestionsA.get(0).getBonnereponse() - 1];
                        listequestionsA.remove(0);
                        if (!(equipeBFinie)) {
                            //Appel récursif en ajustant le joueur
                            new JeuMultiv1(listequestionsA, listequestionsB, scoreA, scoreB,nomEquipeA,nomEquipeB,equipeAFinie,equipeBFinie, equipeDuProchainTour, derniereReponse, partie).setLabelReponse("Temps écoulé ! La bonne réponse était " + derniereReponse, Color.orange);
                            CARD.show(CONTAINER, "JeuMulti");
                        }
                        else {
                            //Appel récursif en ajustant le joueur
                            new JeuMultiv1(listequestionsA, listequestionsB, scoreA, scoreB,nomEquipeA,nomEquipeB,equipeAFinie,equipeBFinie, equipeDuTour, derniereReponse, partie).setLabelReponse("Temps écoulé ! La bonne réponse était " + derniereReponse, Color.orange);
                            CARD.show(CONTAINER, "JeuMulti");
                        }
                        chrono.cancel();
                    }else{
                        equipeAFinie=true;
                        if (equipeBFinie) {
                            try {
                                //Le jeu s'arrête
                                new FinJeuMulti(scoreA, scoreB,nomEquipeA,nomEquipeB, 1,partie).setLabelReponse("Temps écoulé ! La bonne réponse était " + listequestionsA.get(0).getReponses()[listequestionsA.get(0).getBonnereponse() - 1], Color.orange);
                                CARD.show(CONTAINER, "FinJeu");
                                chrono.cancel();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            } catch (ClassNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        else{
                            String dernierereponse = listequestionsA.get(0).getReponses()[listequestionsA.get(0).getBonnereponse() - 1];
                            new JeuMultiv1(listequestionsA,listequestionsB, scoreA,scoreB,nomEquipeA,nomEquipeB,equipeAFinie,equipeBFinie,equipeDuProchainTour, dernierereponse, partie).setLabelReponse("Temps écoulé ! La bonne réponse était "+dernierereponse,Color.orange);
                            CARD.show(CONTAINER, "JeuMulti");
                            chrono.cancel();

                        }
                    }
                }
                else{
                    tempsRestant-=1;
                    labelTempsRestant.setText("Temps Restant : "+tempsRestant);
                }
            }
        };

        //Instancie les boutons correspondant à chaque réponse possible
        bouton1 = new BoutonReponse(chrono,0,listequestionsA);
        bouton2 = new BoutonReponse(chrono,1,listequestionsA);
        bouton3 = new BoutonReponse(chrono,2,listequestionsA);
        bouton4 = new BoutonReponse(chrono,3,listequestionsA);

        boutons = new BoutonReponse[]{bouton1, bouton2, bouton3, bouton4};

        labelEquipe =new JLabel();
        labelEquipe.setText("Au tour de l'équipe "+nomEquipeA);

        labelQuestion=new JLabel(listequestionsA.get(0).getQuestion());
        labelScoreA =new JLabel("Score équipe A : "+scoreA);
        labelScoreB =new JLabel("Score équipe B : "+scoreB);

        labelTempsRestant = new JLabel("Temps Restant : "+tempsRestant);
        labelTempsRestant.setForeground(Color.BLACK);
        labelTempsRestant.setVisible(true);

        //Place les composants le panel à l'endroit souhaité
        JPanel panel= new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill=GridBagConstraints.CENTER;
        gbc.insets=new Insets(25,0,0,0);
        gbc.gridx=0;
        gbc.gridy=0;
        gbc.gridwidth=2;
        panel.add(labelEquipe,gbc);
        gbc.gridx=0;
        gbc.gridy=1;
        panel.add(labelQuestion,gbc);

        gbc.gridx=0;
        gbc.gridy=2;
        panel.add(bouton1.getBouton(),gbc);
        gbc.gridx=0;
        gbc.gridy=3;
        gbc.insets=new Insets(5,0,0,0);
        panel.add(bouton2.getBouton(),gbc);
        gbc.gridx=0;
        gbc.gridy=4;
        panel.add(bouton3.getBouton(),gbc);
        gbc.gridx=0;
        gbc.gridy=5;
        panel.add(bouton4.getBouton(),gbc);
        gbc.gridx=0;
        gbc.gridy=6;
        gbc.gridwidth=1;
        gbc.insets=new Insets(25,0,0,0);
        panel.add(labelScoreA,gbc);
        gbc.gridx=1;
        gbc.gridy=6;
        panel.add(labelScoreB,gbc);
        gbc.gridwidth=2;
        gbc.gridx=0;
        gbc.gridy=7;
        panel.add(labelTempsRestant,gbc);
        gbc.gridx=0;
        gbc.gridy=8;

        panel.add(labelReponse,gbc);
        CONTAINER.add("JeuMulti",panel);
        bouton1.getBouton().addActionListener(this);
        bouton2.getBouton().addActionListener(this);
        bouton3.getBouton().addActionListener(this);
        bouton4.getBouton().addActionListener(this);


        chrono.schedule(timeOutTask,1000,1000);

    }

    /**
     * Fais jouer l'équipe B (fonctionne de la même que l'équipe A)
     */
    public void joueEquipeB(){
        this.equipeDuProchainTour=(equipeDuTour+1)%2;
        this.chrono = new Timer(true);
        this.tempsRestant = listequestionsB.get(0).getTemps();
        TimerTask timeOutTask = new TimerTask() {
            @Override
            public void run() {
                if (tempsRestant==1) {
                    if(listequestionsB.size()!=1) {
                        String dernierereponse = listequestionsB.get(0).getReponses()[listequestionsB.get(0).getBonnereponse() - 1];
                        listequestionsB.remove(0);
                        if (!(equipeAFinie)) {
                            new JeuMultiv1(listequestionsA, listequestionsB, scoreA, scoreB,nomEquipeA,nomEquipeB,equipeAFinie,equipeBFinie, equipeDuProchainTour, dernierereponse, partie).setLabelReponse("Temps écoulé ! La bonne réponse était " + dernierereponse, Color.orange);
                            CARD.show(CONTAINER, "JeuMulti");
                        }
                        else {
                            new JeuMultiv1(listequestionsA, listequestionsB, scoreA, scoreB,nomEquipeA,nomEquipeB,equipeAFinie,equipeBFinie, equipeDuTour, dernierereponse, partie).setLabelReponse("Temps écoulé ! La bonne réponse était " + dernierereponse, Color.orange);
                            CARD.show(CONTAINER, "JeuMulti");
                        }
                        chrono.cancel();
                    }else{
                        equipeBFinie=true;
                        if(equipeAFinie) {
                            try {
                                new FinJeuMulti(scoreA, scoreB, nomEquipeA,nomEquipeB,1,partie).setLabelReponse("Temps écoulé ! La bonne réponse était " + listequestionsB.get(0).getReponses()[listequestionsB.get(0).getBonnereponse() - 1], Color.orange);
                                CARD.show(CONTAINER, "FinJeu");
                                chrono.cancel();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            } catch (ClassNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        else{
                            String dernierereponse = listequestionsA.get(0).getReponses()[listequestionsA.get(0).getBonnereponse() - 1];
                            new JeuMultiv1(listequestionsA, listequestionsB, scoreA, scoreB,nomEquipeA,nomEquipeB,equipeAFinie,equipeBFinie, equipeDuProchainTour, dernierereponse, partie).setLabelReponse("Temps écoulé ! La bonne réponse était " + dernierereponse, Color.orange);
                            CARD.show(CONTAINER, "JeuMulti");

                        }
                    }
                }
                else{
                    tempsRestant-=1; //Decrémente le temps restants chaque seconde et set le label
                    labelTempsRestant.setText("Temps Restant : "+tempsRestant);
                }
            }
        };

        bouton1 = new BoutonReponse(chrono,0,listequestionsB);
        bouton2 = new BoutonReponse(chrono,1,listequestionsB);
        bouton3 = new BoutonReponse(chrono,2,listequestionsB);
        bouton4 = new BoutonReponse(chrono,3,listequestionsB);

        boutons = new BoutonReponse[]{bouton1, bouton2, bouton3, bouton4};

        labelEquipe =new JLabel();
        labelEquipe.setText("Au tour de l'équipe "+nomEquipeB);
        labelQuestion=new JLabel(listequestionsB.get(0).getQuestion());
        labelScoreA =new JLabel("Score équipe "+nomEquipeA+" : "+scoreA);
        labelScoreB =new JLabel("Score équipe "+nomEquipeB+ " : "+scoreB);

        labelTempsRestant = new JLabel("Temps Restant : "+tempsRestant);
        labelTempsRestant.setForeground(Color.BLACK);
        labelTempsRestant.setVisible(true);


        JPanel panel= new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill=GridBagConstraints.CENTER;
        gbc.insets=new Insets(25,0,0,0);
        gbc.gridx=0;
        gbc.gridy=0;
        gbc.gridwidth=2;
        panel.add(labelEquipe,gbc);
        gbc.gridx=0;
        gbc.gridy=1;
        panel.add(labelQuestion,gbc);

        gbc.gridx=0;
        gbc.gridy=2;
        panel.add(bouton1.getBouton(),gbc);
        gbc.gridx=0;
        gbc.gridy=3;
        gbc.insets=new Insets(5,0,0,0);
        panel.add(bouton2.getBouton(),gbc);
        gbc.gridx=0;
        gbc.gridy=4;
        panel.add(bouton3.getBouton(),gbc);
        gbc.gridx=0;
        gbc.gridy=5;
        panel.add(bouton4.getBouton(),gbc);
        gbc.gridx=0;
        gbc.gridy=6;
        gbc.gridwidth=1;
        gbc.insets=new Insets(25,0,0,0);
        panel.add(labelScoreA,gbc);
        gbc.gridx=1;
        gbc.gridy=6;
        panel.add(labelScoreB,gbc);
        gbc.gridwidth=2;
        gbc.gridx=0;
        gbc.gridy=7;
        panel.add(labelTempsRestant,gbc);
        gbc.gridx=0;
        gbc.gridy=8;

        panel.add(labelReponse,gbc);
        CONTAINER.add("JeuMulti",panel);
        bouton1.getBouton().addActionListener(this);
        bouton2.getBouton().addActionListener(this);
        bouton3.getBouton().addActionListener(this);
        bouton4.getBouton().addActionListener(this);


        chrono.schedule(timeOutTask,1000,1000);

    }
    /**
     *
     * @param e the event to be processed
     */
    public void actionPerformed(ActionEvent e){
        if (equipeDuTour == 0) {
            if (e.getSource() == boutons[listequestionsA.get(0).getBonnereponse() - 1].getBouton()) {
                if (listequestionsA.size() == 1) {
                    equipeAFinie = true;
                    scoreA = scoreA + listequestionsA.get(0).getPoints();
                    partie.setScore(scoreA);
                    String dernierereponse = listequestionsA.get(0).getReponses()[listequestionsA.get(0).getBonnereponse() - 1];
                    if (equipeBFinie) {
                        try {
                            new FinJeuMulti(scoreA, scoreB, nomEquipeA,nomEquipeB,1,partie).setLabelReponse("Bonne réponse !", Color.green);
                            ;
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        } catch (ClassNotFoundException ex) {
                            throw new RuntimeException(ex);
                        }
                        CARD.show(CONTAINER, "FinJeu");
                    }
                    else {
                        new JeuMultiv1(listequestionsA, listequestionsB, scoreA, scoreB,nomEquipeA,nomEquipeB,equipeAFinie,equipeBFinie, equipeDuProchainTour, dernierereponse, partie).setLabelReponse("Bonne réponse !", Color.green);
                        CARD.show(CONTAINER, "JeuMulti");
                    }
                }
                else {
                    String dernierereponse = listequestionsA.get(0).getReponses()[listequestionsA.get(0).getBonnereponse() - 1];
                    scoreA = scoreA + listequestionsA.get(0).getPoints();
                    listequestionsA.remove(0);
                    new JeuMultiv1(listequestionsA, listequestionsB, scoreA, scoreB,nomEquipeA,nomEquipeB,equipeAFinie,equipeBFinie, equipeDuTour, dernierereponse, partie).setLabelReponse("Bonne réponse !", Color.green);
                    CARD.show(CONTAINER, "JeuMulti");
                }
            } else {
                if (listequestionsA.size() == 1) {
                    equipeAFinie=true;
                    String dernierereponse = listequestionsA.get(0).getReponses()[listequestionsA.get(0).getBonnereponse() - 1];
                    scoreA = scoreA + listequestionsA.get(0).getMalus();
                    if (scoreA < 0) {
                        scoreA = 0;
                    }
                    partie.setScore(scoreA);
                    if (equipeBFinie) {
                        try {
                            new FinJeuMulti(scoreA, scoreB, nomEquipeA,nomEquipeB,1,partie).setLabelReponse("Mauvaise réponse ! La bonne réponse était " + listequestionsA.get(0).getReponses()[listequestionsA.get(0).getBonnereponse() - 1], Color.red);
                            ;
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        } catch (ClassNotFoundException ex) {
                            throw new RuntimeException(ex);
                        }
                        CARD.show(CONTAINER, "FinJeu");
                    }
                    else{
                        new JeuMultiv1(listequestionsA, listequestionsB, scoreA, scoreB,nomEquipeA,nomEquipeB,equipeAFinie,equipeBFinie, equipeDuProchainTour, dernierereponse, partie).setLabelReponse("Mauvaise réponse ! La bonne réponse était " + dernierereponse, Color.red);
                        CARD.show(CONTAINER, "JeuMulti");
                    }
                }

                else {
                    scoreA = scoreA + listequestionsA.get(0).getMalus();
                    if (scoreA < 0) {
                        scoreA = 0;
                    }
                    String dernierereponse = listequestionsA.get(0).getReponses()[listequestionsA.get(0).getBonnereponse() - 1];
                    listequestionsA.remove(0);
                    if (!(equipeBFinie)) {
                        new JeuMultiv1(listequestionsA, listequestionsB, scoreA, scoreB,nomEquipeA,nomEquipeB,equipeAFinie,equipeBFinie, equipeDuProchainTour, dernierereponse, partie).setLabelReponse("Mauvaise réponse ! La bonne réponse était " + dernierereponse, Color.red);
                        CARD.show(CONTAINER, "JeuMulti");
                    }
                    else{
                        new JeuMultiv1(listequestionsA, listequestionsB, scoreA, scoreB,nomEquipeA,nomEquipeB,equipeAFinie,equipeBFinie, equipeDuTour, dernierereponse, partie).setLabelReponse("Mauvaise réponse ! La bonne réponse était " + dernierereponse, Color.red);
                        CARD.show(CONTAINER, "JeuMulti");
                    }
                }
            }
        }
        else{
            if (e.getSource() == boutons[listequestionsB.get(0).getBonnereponse() - 1].getBouton()) {
                if (listequestionsB.size() == 1) {
                    equipeBFinie=true;
                    scoreB = scoreB + listequestionsB.get(0).getPoints();
                    partie.setScore(scoreB);
                    String dernierereponse = listequestionsB.get(0).getReponses()[listequestionsB.get(0).getBonnereponse() - 1];
                    if(equipeAFinie) {
                        try {
                            new FinJeuMulti(scoreA, scoreB, nomEquipeA,nomEquipeB,1,partie).setLabelReponse("Bonne réponse !", Color.green);
                            ;
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        } catch (ClassNotFoundException ex) {
                            throw new RuntimeException(ex);
                        }
                        CARD.show(CONTAINER, "FinJeu");
                    }
                    else{
                        new JeuMultiv1(listequestionsA, listequestionsB, scoreA, scoreB,nomEquipeA,nomEquipeB,equipeAFinie,equipeBFinie, equipeDuProchainTour, dernierereponse, partie).setLabelReponse("Bonne réponse !", Color.green);
                        CARD.show(CONTAINER, "JeuMulti");
                    }
                } else {
                    String dernierereponse = listequestionsB.get(0).getReponses()[listequestionsB.get(0).getBonnereponse() - 1];
                    scoreB = scoreB + listequestionsB.get(0).getPoints();
                    listequestionsB.remove(0);
                    new JeuMultiv1(listequestionsA, listequestionsB, scoreA, scoreB,nomEquipeA,nomEquipeB,equipeAFinie,equipeBFinie, equipeDuTour, dernierereponse, partie).setLabelReponse("Bonne réponse !", Color.green);
                    CARD.show(CONTAINER, "JeuMulti");
                }
            }
            else{
                    if (listequestionsB.size() == 1) {
                        equipeBFinie=true;
                        String dernierereponse = listequestionsB.get(0).getReponses()[listequestionsB.get(0).getBonnereponse() - 1];
                        scoreB = scoreB + listequestionsB.get(0).getMalus();
                        if (scoreB < 0) {
                            scoreB = 0;
                        }
                        partie.setScore(scoreB);
                        if (equipeAFinie) {
                            try {
                                new FinJeuMulti(scoreA, scoreB, nomEquipeA,nomEquipeB,1,partie).setLabelReponse("Mauvaise réponse ! La bonne réponse était " + listequestionsB.get(0).getReponses()[listequestionsB.get(0).getBonnereponse() - 1], Color.red);
                                ;
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            } catch (ClassNotFoundException ex) {
                                throw new RuntimeException(ex);
                            }
                            CARD.show(CONTAINER, "FinJeu");
                        }
                        else {
                            new JeuMultiv1(listequestionsA,listequestionsB, scoreA, scoreB,nomEquipeA,nomEquipeB,equipeAFinie,equipeBFinie, equipeDuProchainTour, dernierereponse, partie).setLabelReponse("Mauvaise réponse ! La bonne réponse était " + dernierereponse, Color.red);
                            CARD.show(CONTAINER, "JeuMulti");
                        }
                    } else {
                        scoreB = scoreB + listequestionsB.get(0).getMalus();
                        if (scoreB < 0) {
                            scoreB = 0;
                        }
                        String dernierereponse = listequestionsB.get(0).getReponses()[listequestionsB.get(0).getBonnereponse() - 1];
                        listequestionsB.remove(0);
                        if (!(equipeAFinie)) {
                            new JeuMultiv1(listequestionsA, listequestionsB, scoreA, scoreB,nomEquipeA,nomEquipeB,equipeAFinie,equipeBFinie, equipeDuProchainTour, dernierereponse, partie).setLabelReponse("Mauvaise réponse ! La bonne réponse était " + dernierereponse, Color.red);
                            CARD.show(CONTAINER, "JeuMulti");
                        }
                        else{
                            new JeuMultiv1(listequestionsA, listequestionsB, scoreA, scoreB,nomEquipeA,nomEquipeB,equipeAFinie,equipeBFinie, equipeDuTour, dernierereponse, partie).setLabelReponse("Mauvaise réponse ! La bonne réponse était " + dernierereponse, Color.red);
                            CARD.show(CONTAINER, "JeuMulti");
                        }
                    }
                }
            }
        chrono.cancel();
    }
    public void setLabelReponse(String text,Color color){
        labelReponse.setText(text);
        labelReponse.setForeground(color);
    }

}
