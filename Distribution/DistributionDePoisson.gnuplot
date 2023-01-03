set terminal png 10
set xlabel 'k'
set ylabel 'p(k)'
set output 'DistributionDePoisson.png'
set logscale xy
set yrange [1e-6:1]

# Poisson
lambda = 6.62208890914917
poisson(k) = lambda ** k * exp(-lambda) / gamma(k + 1)

plot 'distributionDegre.txt' title 'DBLP', \
  poisson(x) title 'Poisson law'


