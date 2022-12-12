package org.example;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSourceEdge;
import static org.graphstream.algorithm.Toolkit.*;

import java.io.IOException;

public class Main {
        public static void main(String[] args) {
            System.setProperty("org.graphstream.ui", "swing");
            Graph graphe1 = new DefaultGraph("Graphe");
            FileSourceEdge fs = new FileSourceEdge();

            fs.addSink(graphe1);
            graphe1.display();

            try {
                fs.readAll("/home/c2i/Bureau/TP-mesureReseau/tp-mri/fichier.txt");
            } catch (IOException e) {
                System.out.println(e);

            }
            System.out.println("Le nombre de noeuds:" + graphe1.getNodeCount());
            System.out.println("Le degré moyen :" + averageDegree(graphe1));
            System.out.println("Le coefficient de clustering:" + averageClusteringCoefficient(graphe1));
            System.out.println("Le coefficient de clustering pour un réseau aléatoire de la même taille et du même degré moyen :" + averageDegree(graphe1) / graphe1.getNodeCount());
        }
    }
