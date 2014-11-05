package com.github.thomasfischl.brainintercom.analyzer.ga.iterationanalyzer;

import com.github.thomasfischl.brainintercom.analyzer.ga.GA;
import com.github.thomasfischl.brainintercom.analyzer.ga.Solution;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

public class GenomeAnalyzer implements IIterationAnalyzer {

  private int[] genomes;

  private long counter;

  @Override
  public void analyze(GA ga, int iteration, Stopwatch duration) {
    if (counter % 2 == 0) {
      analyzeGenome(ga.getPopulation());
    }
    counter++;
  }

  public void clear() {
    genomes = null;
    counter++;
  }

  private void analyzeGenome(Solution[] population) {
    if (genomes == null) {
      genomes = new int[population[0].getMask().getSize()];
    }

    Multimap<Integer, Integer> genes = MultimapBuilder.hashKeys().hashSetValues().build();

    for (Solution sol : population) {
      int[] data = sol.getMask().getData();
      for (int i = 0; i < data.length; i++) {
        genes.put(i, data[i]);
      }
    }

    for (int i = 0; i < genomes.length; i++) {
      genomes[i] = genes.get(i).size();
    }
  }

  public int[] getGenomes() {
    return genomes;
  }

}
