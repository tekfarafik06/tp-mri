set terminal png 10
set encoding utf8
set output "SimulationScénario1_GA.png"

set  title 'Simulation Scénario 1 GA '
set yrange [0:300000]
set xrange [0:100]

set ylabel 'Nombre de patients infectés '
set xlabel 'jours'
set key font ",10" top left samplen 5 spacing 1
plot "Scenario1_GA.dat" t"Scenario 01 graphe aléatoire" with linespoints linecolor rgb "red"  , \
    





