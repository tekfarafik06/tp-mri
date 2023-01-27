set terminal png 10
set encoding utf8
set output "SimulationScénario3_GA.png"

set  title 'Simulation Scénario 3 graphe aléatoire  '
set yrange [0:300000]
set xrange [0:100]

set ylabel 'Nombre de patients infectés '
set xlabel 'jours'
set key font ",10" top left samplen 5 spacing 1
plot "Scenario3_GA.dat" t"Scenario03" with linespoints linecolor rgb "red"  , \
    





