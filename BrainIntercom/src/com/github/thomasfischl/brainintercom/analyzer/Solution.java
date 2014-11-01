package com.github.thomasfischl.brainintercom.analyzer;

import java.util.LinkedList;

import com.github.thomasfischl.brainintercom.recorder.recognize.RecognizerPattern;

public class Solution implements Comparable<Solution> {

  private int fitness;

  private int missingPositivMatch;

  private int negativMatch;

  protected RecognizerPattern mask;

  public Solution() {
    mask = new RecognizerPattern("", GA.model.getDimension(), GA.model.getWindowSize(), GA.model.getRange());
  }

  public void randomize() {
    LinkedList<Integer> pos = new LinkedList<>();
    for (int i = 0; i < mask.getSize(); i++) {
      pos.add(i);
    }

    int[] data = mask.getData();
    for (int i = 0; i < mask.getSize() * 0.7; i++) {
      data[pos.remove(GA.rand.nextInt(pos.size()))] = GA.rand.nextInt(4) + 1;
    }
  }

  private void setFitness(int fitness) {
    this.fitness = fitness;
  }

  public int getFitness() {
    return fitness;
  }

  public RecognizerPattern getMask() {
    return mask;
  }

  @Override
  public int compareTo(Solution o) {
    return fitness - o.fitness;
  }

  public Solution cross(Solution solution) {
    Solution newSolution = new Solution();

    int cutPoint = GA.rand.nextInt(mask.getSize());

    int[] newData = newSolution.getMask().getData();
    int[] oldData = mask.getData();

    for (int i = 0; i < cutPoint; i++) {
      newData[i] = oldData[i];
    }

    oldData = solution.mask.getData();
    for (int i = cutPoint; i < mask.getSize(); i++) {
      newData[i] = oldData[i];
    }

    return newSolution;
  }

  public int getNumberOfFreePlaces() {
    int numberOfZeros = 0;
    int[] data = mask.getData();
    for (int idx = 0; idx < mask.getSize(); idx++) {
      if (data[idx] == 0) {
        numberOfZeros++;
      }
    }
    return numberOfZeros;
  }

  public void mutate(int iteration) {
    if (iteration < 180) {
      muteateYoungGeneration();
    } else {
      mutateOldGeneration();
    }
  }

  private void mutateOldGeneration() {
    int[] data = mask.getData();

    for (int i = 0; i < 2; i++) {
      int idx = GA.rand.nextInt(mask.getSize());
      data[idx] = 0;
    }
    if (GA.rand.nextDouble() < 0.4) {
      for (int i = 0; i < 2; i++) {
        int idx = GA.rand.nextInt(mask.getSize());
        data[idx] = GA.rand.nextInt(5);
      }
    }
  }

  private void muteateYoungGeneration() {
    int[] data = mask.getData();

    for (int i = 0; i < mask.getSize() * 0.2; i++) {
      int idx = GA.rand.nextInt(mask.getSize());
      data[idx] = 0;
    }
    for (int i = 0; i < mask.getSize() * 0.05; i++) {
      int idx = GA.rand.nextInt(mask.getSize());
      data[idx] = GA.rand.nextInt(5);
    }
  }

  public void calcFitness(int positivMatch, int negativMatch, int missingPositivMatch, int totalMinMaskDiff) {
    this.negativMatch = negativMatch;
    this.missingPositivMatch = missingPositivMatch;

    int fitness = (missingPositivMatch * 100) + (negativMatch * 100) + (totalMinMaskDiff * 2);
    fitness += getNumberOfFreePlaces();
    setFitness(fitness);
  }

  @Override
  public String toString() {
    return String.format("Sol[%2d,%3d,%4d]", missingPositivMatch, negativMatch, getNumberOfFreePlaces());
  }

}
