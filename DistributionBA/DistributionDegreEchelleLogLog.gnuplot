set terminal png
set encoding utf8
set output "distributionDegEchelleLogLog.png"
set logscale x 10
set logscale y 10
set xlabel "k"
set ylabel "p(k)"
set key on inside center top
plot"DegreeDistGrapheBarAlb.txt" title 'DBLP' with linesp lt 1 pt 1 linecolor rgb 'red'
