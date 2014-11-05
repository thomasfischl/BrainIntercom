package com.github.thomasfischl.brainintercom.analyzer.ga.iterationanalyzer;

import com.github.thomasfischl.brainintercom.analyzer.ga.GA;
import com.google.common.base.Stopwatch;

public interface IIterationAnalyzer {

  void analyze(GA ga, int iteration, Stopwatch duration);

}
