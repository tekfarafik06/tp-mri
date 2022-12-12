package org.example;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSourceEdge;
import static org.graphstream.algorithm.Toolkit.*;

import java.io.IOException;

public class Main {
        public static void main(String[] args) {
            System.setProperty("org.graphstream.ui", "swing");
            Graph mongraphe = new DefaultGraph("Graphe");
            FileSourceEdge fs = new FileSourceEdge();

            fs.addSink(mongraphe);
            mongraphe.display();

            try {
                fs.readAll("/home/c2i/Bureau/TP-mesureReseau/tp-mri/fichier.txt");
            } catch (IOException e) {
                System.out.println(e);

            }
            System.out.println("Le nombre de noeuds:" + mongraphe.getNodeCount());
            System.out.println("Le degré moyen :" + averageDegree(mongraphe));
            System.out.println("Le coefficient de clustering:" + averageClusteringCoefficient(mongraphe));

        }
    }
