set terminal png
set encoding utf8
set output "distributionDegEchelleLin.png"
set xlabel "k"
set ylabel "p(k)"
set key on inside center top
plot"DegreeDistGrapheBarAlb.txt" title 'DBLP' with lines lt 2 lw 3 linecolor rgb 'red'
