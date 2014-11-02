package com.github.thomasfischl.brainintercom.analyzer.ga;

public interface IProblem {

	Solution[] createInitialPopulation(int populationSize);

	void evaluateFitness(Solution[] population, int iteration);

	Solution cross(Solution solution, Solution solution2, int iteration);

	void mutate(Solution solution, int iteration);

}
