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
import org.graphstream.graph.Node;
import org.graphstream.graph.Edge;

import java.io.IOException;
import java.util.Random;

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
    //Méthode pour calculer les individus contaminés
    // en utilisant la probabilité de contaminer un collaborateur qui est 1/7
    public static int patientPositif(Node n, int nbrPositif) {
        Random r = new Random();
        //Si la probabilité est égale à 1 sur 7, et que l'individu n'est pas immunisé
        if (r.nextInt(7) + 1 == 1 && !n.hasAttribute("immunise")) {
            //Si l'individu n'est pas déjà infecté
            if (!n.hasAttribute("infected")) {
                //on lui attribue l'attribut infecté
                n.setAttribute("infected", true);
                //On augmente le compteur de personne infecté
                nbrPositif++;
            }
        }
        return nbrPositif;
    }

    //Méthode pour calculer les individus guéris
    // en utilisant la probabilité de mettre à jour l'anti virus qui est 1/14
    public static int patientNegatif(Node n, int nbrPositif) {
        Random r = new Random();
        //Si la probabilité est égale à 1 sur 14
        if (r.nextInt(14) + 1 == 1) {
            //Si l'individu est infecté
            if (n.hasAttribute("infected")) {
                //On retire l'attribut infecté
                n.removeAttribute("infected");
                //On décrémente le compteur de personne infecté
                nbrPositif--;
            }
        }
        return nbrPositif;
    }
    public static void simulateScenario1(Graph graphe) {
        // Sélectionnez un patient zéro pour commencer l'épidémie
        Node patientZero = graphe.getNode(0);
        patientZero.setAttribute("infected", true);
        int nbrPositif = 1;

        // Boucle pour simuler 84 jours de propagation (12 semaines)
        for (int day = 1; day <= 84; day++) {
            // Parcourir tous les noeuds du réseau
            for (Node node : graphe) {
                // Si un noeud est infecté, calculer la probabilité de transmettre le virus à ses voisins
                if (node.hasAttribute("infected")) {
                    for (Edge e : node) {
                        nbrPositif = patientPositif(e.getOpposite(node), nbrPositif);
                        }
                    }
                nbrPositif = patientNegatif(node, nbrPositif);
                }
            System.out.println("Jour " + day + ": " + nbrPositif + " personnes infectées");
        }
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
        simulateScenario1(graphe);




    }
}
