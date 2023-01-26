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
        saveData("Scenario01" ,   simulateScenario1(graphe));


    }
}
