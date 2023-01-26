package org.example;
import org.graphstream.algorithm.Toolkit;
import org.graphstream.algorithm.generator.BarabasiAlbertGenerator;
import org.graphstream.algorithm.generator.Generator;
import org.graphstream.algorithm.generator.RandomGenerator;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceEdge;
import org.graphstream.graph.Node;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

import static org.graphstream.algorithm.Toolkit.averageDegree;

public class propagationReseau {
    public static Graph graphe;

    //constructure de la classe PropagationReseau
    public propagationReseau(Graph graphe) throws IOException {
        this.graphe = graphe;
    }

    public Graph getG() {
        return this.graphe;
    }

    //Lire les données du réseau DBLP en utilisant GraphStream
    public static Graph readFile(String path, String Graph) {
        // Création d'un nouveau graphe avec le nom spécifié
        Graph graphe = new DefaultGraph(Graph);

        // Chargement des données à partir du fichier spécifié
        FileSource fs = new FileSourceEdge();
        fs.addSink(graphe);
        try {
            fs.readAll(path);
        } catch (IOException e) {
            System.out.println("ERREUR, fichier introuvable !");
        } finally {
            fs.removeSink(graphe);
        }

        // Retourner le graphe rempli avec les données chargées
        return graphe;
    }

    public static double calculerDistributionDegres() {
        double k = 0.0;
        int i;
        // Récupération des degrés des noeuds dans un tableau
        int[] degreeDistribution = Toolkit.degreeDistribution(graphe);
        // Parcours du tableau pour calculer la somme des degrés au carré divisée par le nombre de noeuds
        for (i = 0; i < degreeDistribution.length; i++) {
            if (degreeDistribution[i] > 0) {
                k += Math.pow(i, 2) * ((double) degreeDistribution[i] / graphe.getNodeCount());
            }
        }
        return k;
    }

    public static Graph ReseauAleatoire(int NombreNoeuds, int degreeMoyen) {
        System.setProperty("org.graphstream.ui", "user.dir");
        Graph grapheAlea = new SingleGraph("Random");
        Generator gen = new RandomGenerator(degreeMoyen, false, false);
        gen.addSink(grapheAlea);
        gen.begin();
        for (int i = 0; i < NombreNoeuds; i++)
            gen.nextEvents();
        gen.end();

        return grapheAlea;

    }

    public static Graph BarabasiAlbert(int NombreNoueds, int degreMoyen) {
        Graph grapheBA = new SingleGraph("BarAlb");
        Generator gen = new BarabasiAlbertGenerator(degreMoyen);

        gen.addSink(grapheBA);
        gen.begin();
        for (int i = 0; i < NombreNoueds; i++)
            gen.nextEvents();
        gen.end();
        return grapheBA;
    }

    public static void saveData(String filename , String liste){
        try {
            FileWriter file = new FileWriter("TpPropagation/"+filename+".dat");
            file.write(liste);

            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static String simulateScenario1(Graph graphe) throws IOException {
        int jours = 90 ;// Trois mois
        // Sélectionnez un patient zéro pour commencer l'épidémie
        Node patientZero = graphe.getNode(0);
        patientZero.setAttribute("infecté", true);
        int nbrPositif = 1;
        String res = " ";
        // Boucle pour simuler 90 jours de propagation
        for (int i = 0; i < jours; i++) {
        // Parcourir tous les noeuds du réseau
            for (Node node : graphe) {
        // Si un noeud est infecté, calculer la probabilité de transmettre le virus à ses voisins
                if (node.hasAttribute("infecté")) {
                    for (Edge e : node) {
                        Random r = new Random();
        //Si la probabilité est égale à 1 sur 7, et que l'individu n'est pas immunisé
                        if (r.nextInt(7) + 1 == 1 && !e.getOpposite(node).hasAttribute("immunise")) {
        //Si l'individu n'est pas déjà infecté
                            if (!e.getOpposite(node).hasAttribute("infecté")) {
        //on lui attribue l'attribut infecté
                                e.getOpposite(node).setAttribute("infecté", true);
        //On augmente le compteur de personne infecté
                                nbrPositif++;
                            }
                        }
                    }
                }
                Random r = new Random();
        //Si la probabilité est égale à 1 sur 14
                if (r.nextInt(14) + 1 == 1) {
        //Si l'individu est infecté
                    if (node.hasAttribute("infecté")) {
        //On retire l'attribut infecté
                        node.removeAttribute("infecté");
        //On décrémente le compteur de personne infecté
                        nbrPositif--;
                    }
                }
            }
            System.out.println("jour " + i + " " +nbrPositif+"\n");
            res += (i+1)+" "+nbrPositif+"\n";
        }
        return res;
    }
    public static String simulateScenario2(Graph graphe) throws IOException {
        int nbJours = 90 ;// Trois mois
        String res = " ";
        Random rand = new Random();

        //Toute la population est en bonne santé
        for(Node node : graphe )
            node.setAttribute("state", "sain");

        List<Node> lst_immunise = Toolkit.randomNodeSet(graphe, graphe.getNodeCount()/2);
        for(Node node:lst_immunise) node.setAttribute("state", "immuniser");

        //on retire les noeuds immunisés
        for(Node node:lst_immunise){
            graphe.removeNode(node);
        }

        // definition d'un patient zéro
        int k = rand.nextInt(graphe.getNodeCount());
        Node patientZero = graphe.getNode(k);
        patientZero.setAttribute("state", "infecté");
//pour stocker les individus infectés qui s'occupe de propager le virus chaque jour
        ArrayList<Node> infecte = new ArrayList<>();
        infecte.add(patientZero);
        //un tableau pour stocker les individus de l'etat "sain" en l'etat "infecte" ou de l'etat "infecte" en l'etat "sain"
        ArrayList<Node> temp = new ArrayList<>();
        for(int i=0;i<nbJours;i++){
            //parcourir tous les individus infectes
            for(Node node:infecte){
                if(!temp.contains(node)) temp.add(node);
                //la probabilité de recevoir le mail pour chaque voisin de l'individu infecté est 1/7
                if(rand.nextInt(7)+1==1) {
                    for(Edge e:node){
                        //obtenir tous les voisins de noeud node
                        Node voisin = e.getOpposite(node);
                        if(!temp.contains(voisin) && !lst_immunise.contains(voisin)){
                            voisin.setAttribute("state", "infecté");
                            temp.add(voisin);
                        }
                    }
                }
            }

            //vider le tableau infected,prepare les individus infectés pour lendemain
            infecte.clear();
            //mettre à jour
            for(Node node:temp){
                if(rand.nextInt(14)+1==1)
                    node.setAttribute("state", "sain");
                else infecte.add(node);
            }

            System.out.println("jour " + i + " " + infecte.size()+"\n");

            res += (i+1)+" "+infecte.size()+"\n";
        }

        return res ;
    }
    public static String simulateScenario3(Graph graphe) throws IOException{
        Random numRandom = new Random();
        List<Node> immunises = new ArrayList();
        graphe.forEach(n -> { n.setAttribute("state","sain"); }); // Chaque noeud non malade

        String res = "";
        int jours = 90; // Trois mois

        List<Node> moitieIndiv = Toolkit.randomNodeSet(graphe,graphe.getNodeCount()/2);//  50 % des individus (pour cas 3)


        for(Node node : moitieIndiv) {
            // Un des contacts pour les 50 %
            Node nodeImmunise = node.getEdge(numRandom.nextInt(node.getDegree())).getOpposite(node);
            nodeImmunise.setAttribute("state","immuniser");
            immunises.add(nodeImmunise);
        }


        // Liste pour stocker les individus infectés qui propagent le virus
        ArrayList<Node> infectes = new ArrayList<>();
        ArrayList<Node> temp = new ArrayList<>();

        Consumer<Node> setMalade = (n) -> { n.setAttribute("state", "infecté "); };
        Consumer<Node> setGueri = (n) -> { n.setAttribute("state", "sain"); };

        // Patient 0 infecté
        int k = numRandom.nextInt(immunises.size());
        Node premiereInfecte = immunises.get(k);
        premiereInfecte.setAttribute("state", "infecté ");
        infectes.add(premiereInfecte);

        for(int i=0;i<jours;i++) {
            for (Node node : infectes) {
                if (!temp.contains(node)) temp.add(node);
                if (numRandom.nextInt(7) + 1 == 1) {
                    for (Edge e : node) {
                        Node voisin = e.getOpposite(node);
                        if (!temp.contains(voisin) && !moitieIndiv.contains(voisin)) {
                            voisin.setAttribute("state", "infecté");
                            temp.add(voisin);
                        }
                    }
                }
            }
            //vider le tableau infected,prepare les individus infectés pour lendemain
            infectes.clear();
            //mettre à jour
            for (Node node1 : temp) {
                if (numRandom.nextInt(14) + 1 == 1)
                    node1.setAttribute("state", "sain");
                else infectes.add(node1);
            }
            System.out.println("j " + i+ " " +infectes.size()+"\n");

            res += (i + 1) + " " + infectes.size() + "\n";
            //res += "Il y a " + (g.getNodeCount() - immunises.size() - infectes.size()) + " personnes guéries, " + infectes.size() + " personnes malades et " + immunises.size() + " personnes immunisées.\n";
        }
        return res;
    }


    public static void main(String[] args) throws IOException{
        //Création d'un objet pour la propagation du réseau en utilisant le graphe créé à partir du fichier
        Graph graphe = readFile("./fichier.txt", "graphe1");
        //Instanciation
        propagationReseau grapheP = new propagationReseau(graphe);
        System.out.println("************ 1-Taux de propagation du virus  ************");
        //Calcul du degré moyen du graphe
        double degreMoyen = averageDegree(graphe);
        System.out.println("******* Le Seuil épidémique du réseau DBLP et du réseau aléatoire ********") ;
        //Calcul du seuil épidémique pour le graphe DBLP
        double GrapheDBLP = degreMoyen / grapheP.calculerDistributionDegres();
        //Calcul du seuil épidémique pour le graphe aléatoire avec le même degré moyen
        double GrapheAlea = 1 / (degreMoyen+1);
        //Affichage des résultats obtenus pour les deux graphes
        System.out.println("Le seuil épidémique du réseau DBLP est : " + degreMoyen +" / "+ grapheP.calculerDistributionDegres() + " = " +  GrapheDBLP);
        System.out.println("Le seuil épidémique du réseau Aléatoire est => " + 1 +" / "+  (degreMoyen+1) + " = " +  GrapheAlea);
        System.out.println("\n******* Simulation du scénarios 01 ********") ;
        //saveData("Scenario01" ,   simulateScenario1(graphe));
        System.out.println("\n******* Simulation du scénarios 02 ********") ;
        //saveData("Scenario02" ,   simulateScenario2(graphe));
        System.out.println("\n******* Simulation du scénarios 03 ********") ;
        saveData("Scenario3" , simulateScenario3(graphe));


    }
}
