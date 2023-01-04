package org.example;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.algorithm.generator.Generator;
import org.graphstream.algorithm.generator.RandomGenerator;
import org.graphstream.graph.BreadthFirstIterator;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSourceEdge;
import static org.graphstream.algorithm.Toolkit.*;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Main {
    // 6-1 Création de graphe aléatoire ayant la même taille que le réseau DBLP
    public static Graph genererGraphRandom(int NombreNoueds, int degreMoyen) {
        System.out.println("\n************************ 6-1 Mesures de base d'un graphe généré aléatoirement ****************** \n ");
        //création et instanciation du générateur aléatoire
        Graph graphe2 = new SingleGraph("Random");
        Generator gen = new RandomGenerator(degreMoyen);
        gen.addSink(graphe2);
        gen.begin();
        for (int i = 0; i < NombreNoueds; i++) {
            gen.nextEvents();
        }
        gen.end();
        //Les mesures de base pour un graphe générer aléatoirement en utilisant les méthodes de la classe Toolkit
        System.out.println("********** Les mesures de base pour un graphe générer aléatoirement **********\n");
        System.out.println("Le nombre de noeuds pour le graphe  généré aléatoirement : " + graphe2.getNodeCount());
        System.out.println("Le nombre de liens pour le graphe généré aléatoirement : " + graphe2.getEdgeCount());
        System.out.println("Le degré moyen du graphe généré aléatoirement : " + averageDegree(graphe2));
        System.out.println("****************** vérifier la connexité du réseau aléatoire générer aléatoirement ************** ");
        if (isConnected(graphe2))
            System.out.println("Le graphe est connexe");
        else
            System.out.println("Le graphe n'est pas connexe");
        System.out.println("Le coefficient de clustering du graphe généré aléatoirement : " + averageClusteringCoefficient(graphe2));

        //Distribution des degrés du graphe aléatoire
        int n = graphe2.getNodeCount();
        int[] dd = Toolkit.degreeDistribution(graphe2);
        //Stocker les données dans un fichier.txt
        String filename = "Aleatoirement/DegreeDistGrapheAlea.txt";
        try {
            String filepath = System.getProperty("user.dir") + File.separator + filename;
            FileWriter fw = new FileWriter(filepath);
            BufferedWriter fichier2 = new BufferedWriter(fw);
            for (int i = 0; i < dd.length; i++) {
                fichier2.write(String.format(Locale.US, "%6d%20.8f%n", i, (double)dd[i] / n));
                //System.out.printf("%6d%20.8f%n",i, (double) dd[i]/n);
            }
            fichier2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //graphe2.display();
        return graphe2;
    }
    public static void main(String[] args) {
        System.setProperty("org.graphstream.ui", "swing");
        Graph graphe1 = new DefaultGraph("Graphe");
        FileSourceEdge fs = new FileSourceEdge();

        fs.addSink(graphe1);
        //graphe1.display();

        try {
            fs.readAll("/home/c2i/Musique/tp-mri/fichier.txt");
        } catch (IOException e) {
            System.out.println(e);

        }

        System.out.println("***************** Quelques mesures de bases ******************");
        System.out.println("Le nombre de noeuds:" + graphe1.getNodeCount());
        System.out.println("Le nombre de liens : " + graphe1.getEdgeCount());
        System.out.println("Le degré moyen :" + averageDegree(graphe1));
        System.out.println("Le coefficient de clustering:" + averageClusteringCoefficient(graphe1));
        System.out.println("Le coefficient de clustering pour un réseau aléatoire de la même taille et du même degré moyen :" + averageDegree(graphe1) / graphe1.getNodeCount());
        /* la méthode de toolkit "isconnected" nous permet de  savoir si le graphe est connexe ou non*/
        System.out.println("****************** vérifier la connexité du réseau aléatoire ************** ");
        if (isConnected(graphe1))
            System.out.println("Le graphe est connexe");
        else
            System.out.println("Le graphe n'est pas connexe");
        System.out.println("**************** Vérifier la connexité du réseau aléatoire de la meme taille et degré moyen *********");
        System.out.println(" La connexité d'un graphe aléatoire avec la même taille et degré moyen :  " + (averageDegree(graphe1) > Math.log(graphe1.getNodeCount())));
        System.out.println(" Un réseau aléatoire avec la même taille, devient connexe à partir du degré moyen : " + Math.log(graphe1.getNodeCount()));

        //calculer le degrés Distrubution
        //nombre de noeud du graphe
        int nbNoeud = graphe1.getNodeCount();
        /* stocker dans un tableau d'entiers où chaque indice de cellule représente le degré d'un noeud et la valeur de la cellule le nombre de noeuds ayant ce degré
         */
        int[] degreProba = Toolkit.degreeDistribution(graphe1);

        // création d'un fichier pour stocker les résultats de la distribution des degrés
        try {
            PrintWriter fichier = new PrintWriter(new FileWriter("/home/c2i/Musique/tp-mri/Distribution/distributionDegre.txt"));
            for (int i = 0; i < degreProba.length; i++) {
                if (degreProba[i] != 0) {
                    fichier.write(String.format(Locale.US, "%6d%20.8f%n", i, (double) degreProba[i] / nbNoeud));
                    // degreProba[i]/nbNoeud : represente la probabilité  qu’un noeud choisi au hasard ait degré i
                    //fichier.println();
                }
            }
            fichier.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("\n*********** Calculer de la distance moyenne d'un réseau ***********");
        // Calculer de la distance moyenne d'un réseau, en utilisant le parcours en largeur pour 1000 sommets choisis alearoirement :
        List<Node> l = Toolkit.randomNodeSet(graphe1, 1000);
        HashMap<Integer, Integer> distancesMap = new HashMap<Integer, Integer>();
        double distance = 0;
        int a = 0;
        for (int i = 0; i < 1000; i++) {
            BreadthFirstIterator bf = new BreadthFirstIterator(l.get(i));
            while (bf.hasNext()) {
                Node Noeud = bf.next();
                int key = bf.getDepthOf(Noeud);
                if (distancesMap.containsKey(key)) {
                    int t = distancesMap.get(key);
                    int value = 1 + t;
                    distancesMap.put(key, value);
                } else {
                    distancesMap.put(key, 1);
                }
                distance += bf.getDepthOf(bf.next());
                a++;
            }
        }
        double distMoy = distance / (a);
        System.out.println("La distance moyenne avec 1000 noeuds est : " + distMoy);
        System.out.println("La distance moyenne dans un réseau aléatoire avec les mêmes caractéristiques est :" + Math.log(graphe1.getNodeCount()) / Math.log(averageDegree(graphe1)));
        String filename = "Distribution/Distance.txt";
        try {
            String filepath = System.getProperty("user.dir") + File.separator + filename;
            FileWriter dd = new FileWriter(filepath);
            BufferedWriter fichier = new BufferedWriter(dd);
            for (Integer i : distancesMap.keySet()) {
                fichier.write(String.format(Locale.US, "%6d%20.8f%n", i, (double) distancesMap.get(i) / a));
            }
            fichier.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //reprendre les meme resultats(meme degre et meme nombre de noeud)
        Main.genererGraphRandom(317080,6);
    }
}

