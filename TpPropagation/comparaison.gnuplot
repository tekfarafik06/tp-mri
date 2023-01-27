set terminal png 10
set encoding utf8
set output "ComparaisonDesTroisScen.png"

set  title 'Comparaison des 3 scénarios  '

set xrang[0:84]

set ylabel 'Nombre de patients infectés'
set xlabel 'jours'
set key top left
plot "Scenario1.dat" t"Scenario 1" with lines lt 1 lw 2, "Scenario2.dat" t"Scenario 2" with lines lt 2 lw 2 ,"Scenario3.dat" t"Scenario 3" with lines lt 3 lw 5
