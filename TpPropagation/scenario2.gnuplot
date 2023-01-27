set terminal png 10
set encoding utf8
set output "SimulationScénario2.png"

set  title 'Simulation Scénario 2  '
set yrange [0:300000]
set xrange [0:100]

set ylabel 'Nombre de patients infectés '
set xlabel 'jours'
set key font ",10" top left samplen 5 spacing 1
plot "Scenario2.dat" t"Scenario02" with linespoints linecolor rgb "red " 
    





