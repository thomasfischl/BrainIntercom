package com.github.thomasfischl.brainintercom.analyzer;

import java.util.LinkedList;

public class Solution implements Comparable<Solution> {

  private int[] mask;

  private int fitness;

  private int windowSize;

  private int dimension;

  private int maskSize;

  public Solution(int windowSize, int dimension) {
    this.windowSize = windowSize;
    this.dimension = dimension;
    maskSize = windowSize * dimension;
    mask = new int[maskSize];
  }

  public void randomize() {
    LinkedList<Integer> pos = new LinkedList<>();
    for (int i = 0; i < maskSize; i++) {
      pos.add(i);
    }

    for (int i = 0; i < maskSize * 0.7; i++) {
      mask[pos.remove(GA.rand.nextInt(pos.size()))] = GA.rand.nextInt(4) + 1;
    }
    // for (int i = 0; i < maskSize; i++) {
    // mask[i] = GA.rand.nextInt(5);
    // }
    // for (int i = 0; i < windowSize; i++) {
    // for (int j = 0; j < dimension; j++) {
    // mask[(i * dimension) + j] = rand.nextInt(5);
    // }
    // }
  }

  public void setFitness(int fitness) {
    this.fitness = fitness;
  }

  public int getFitness() {
    return fitness;
  }

  public int[] getMask() {
    return mask;
  }

  @Override
  public int compareTo(Solution o) {
    return fitness - o.fitness;
  }

  public Solution cross(Solution solution) {
    Solution newSolution = new Solution(windowSize, dimension);

    int cutPoint = GA.rand.nextInt(mask.length);

    for (int i = 0; i < cutPoint; i++) {
      newSolution.mask[i] = mask[i];
    }

    for (int i = cutPoint; i < maskSize; i++) {
      newSolution.mask[i] = solution.mask[i];
    }

    return newSolution;
  }

  public int getNumberOfFreePlaces() {
    int numberOfZeros = 0;
    for (int idx = 0; idx < maskSize; idx++) {
      if (mask[idx] == 0) {
        numberOfZeros++;
      }
    }
    return numberOfZeros;
  }

  public void mutate(int iteration) {
    if (iteration < 180) {
      for (int i = 0; i < maskSize * 0.2; i++) {
        int idx = GA.rand.nextInt(maskSize);
        mask[idx] = 0;
      }
      for (int i = 0; i < maskSize * 0.05; i++) {
        int idx = GA.rand.nextInt(maskSize);
        mask[idx] = GA.rand.nextInt(5);
      }
    } else {
      for (int i = 0; i < 2; i++) {
        int idx = GA.rand.nextInt(maskSize);
        mask[idx] = 0;
      }
    }
  }

  public void mutate1() {
    for (int i = 0; i < maskSize * 0.7; i++) {
      int idx = GA.rand.nextInt(maskSize);
      mask[idx] = GA.rand.nextInt(5);
    }
  }

}
