set terminal png 10
set encoding utf8
set output "ComparaisonDesTroisScen_BA.png"

set  title 'Comparaison des 3 scénarios BA '
set yrange [0:300000]
set xrang[0:100]

set ylabel 'Nombre de patients infectés'
set xlabel 'jours'
set key top left
plot "Scenario1_BA.dat" t"Scenario 1" with lines lt 1 lw 2, "Scenario2_BA.dat" t"Scenario 2" with lines lt 2 lw 2 ,"Scenario3_BA.dat" t"Scenario 3" with lines lt 3 lw 5
