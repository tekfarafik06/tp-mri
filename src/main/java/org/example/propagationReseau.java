package org.example;
import org.graphstream.algorithm.Toolkit;
import org.graphstream.algorithm.generator.BarabasiAlbertGenerator;
import org.graphstream.algorithm.generator.Generator;
import org.graphstream.algorithm.generator.RandomGenerator;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceEdge;

import java.io.IOException;

import static org.graphstream.algorithm.Toolkit.averageDegree;

public class propagationReseau {
    public static Graph graphe;
    //constructure de la classe PropagationReseau
    public propagationReseau(Graph graphe) throws IOException {
        this.graphe = graphe;
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
    public static double calculerDistributionDegres(){
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
        for (int i=0;i < NombreNoueds;i++ )
            gen.nextEvents();
        gen.end();
        return grapheBA;
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




    }
}
