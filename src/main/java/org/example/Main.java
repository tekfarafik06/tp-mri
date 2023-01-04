package org.example;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.BreadthFirstIterator;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSourceEdge;
import static org.graphstream.algorithm.Toolkit.*;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Main {

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
            System.out.println(" La connexité d'un graphe aléatoire avec la même taille et degré moyen :  " + (averageDegree(graphe1)> Math.log(graphe1.getNodeCount())));
            System.out.println(" Un réseau aléatoire avec la même taille, devient connexe à partir du degré moyen : " + Math.log(graphe1.getNodeCount()));

            //calculer le degrés Distrubution
            //nombre de noeud du graphe
            int nbNoeud = graphe1.getNodeCount();
            /* stocker dans un tableau d'entiers où chaque indice de cellule représente le degré d'un noeud et la valeur de la cellule le nombre de noeuds ayant ce degré
            */
            int [] degreProba = Toolkit.degreeDistribution(graphe1);

            // création d'un fichier pour stocker les résultats de la distribution des degrés
            try {
                PrintWriter fichier = new PrintWriter(new FileWriter("/home/c2i/Musique/tp-mri/Distribution/distributionDegre.txt"));
                for (int i = 0; i < degreProba.length; i++) {
                    if (degreProba[i] != 0) {
                        fichier.write(String.format(Locale.US,"%6d%20.8f%n", i, (double) degreProba[i] / nbNoeud));
                        // degreProba[i]/nbNoeud : represente la probabilité  qu’un noeud choisi au hasard ait degré i
                        //fichier.println();
                    }
                }
                fichier.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("\n*********** Calculer de la distance moyenne d'un réseau ***********");

        }
}

