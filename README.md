## Evolutionary Computation - Assignment 1

This is the readme for the assignment in the module Evelutionary Computation May 2015 at UCT.

**HLTSIR001**

Result after 50 runs with the input parameters `1000 500 50`:

	MIN: 160034.0 AVG: 165668.06 MAX: 171017.0


**EDXRIK001**

Result after 50 runs with the input parameters `1000 100 50`:

	MIN: 139829.0 AVG: 143989.88 MAX: 147753.0
	
---
	
## ANALYSIS

In order to figure out if the two samples were from the same probability distribution, we used the Kolmogorov-Smirnov (K-S) test. The difference between the cumulative distributions D, were `1.0`, with a corresponding `P = 0.0`. Both datasets were found to be consistent with both a log normal and a normal distribution.

In order to figure out if the data sets were significantly different, we run a T-test. This was possible because both our data sets were determined to be consistent with a normal distribution. It gave us the result of `t = -51.9`, the probability P of that result is less than `.0001`, assuming the null hypothesis.

Based on these results, it was decided that our data sets had a significant difference. This can be backed up by the graph below, displaying Siri's (HLTSIR001) results to the right and mine (EDXRIK001) to the left.

![Screen Shot 2015 05 28 At 11.27.38](Screen%20Shot%202015-05-28%20at%2011.27.38.png)

##IMPLEMENTATION COMPARISON

Our implementation details differs on several points. For parent selection we both use tournament selection, but Siri has a tournament size of 6 whilst I draw from 5 chromosomes. The best fittest is removed from the candidates, until the pool is empty. Because we both generate more children than our population, the candidate pool is reset when this happened. Siri generated 6000 children each generation, and selected the 497 fittest along with the 3 fittest parents to survive for the next generation. I generated 10000 children each round, and selected the 80 fittest along with the 20 fittest parents.

We both recombined our parents with an approach inspired by the n-point crossover, so there shouldn't be any difference there. Both Siri and I implemented a version of Rechenberg's "1/5 success rule" for controlling the likelihood of mutating each genome, but she used far lower start Sigma-values than me. Her mutation threshold started at 0.015 and were increased/decreased based on the Rechenberg's rule. I started mine at 0.3 i.e. far more likely to mutate. For the mutation itself Siri used Translocation, while I used a three-point exchange approach - this combined with the different Sigma-values is most likely the source of our differences. Mine is far more explorative in the fairly limited (100 generations) lifespan of the algorithm.