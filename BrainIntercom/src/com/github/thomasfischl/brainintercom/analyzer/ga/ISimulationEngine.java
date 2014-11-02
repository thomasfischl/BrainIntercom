package com.github.thomasfischl.brainintercom.analyzer.ga;

public interface ISimulationEngine {

  void setHeatFactor(double heatFactor);

  void simulate(Solution[] population, int iteration);

}
