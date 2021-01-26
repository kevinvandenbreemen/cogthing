package com.vandenbreemen.cogthing.application;

import com.vandenbreemen.cogthing.GridPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ModifiedLocalMinimaSeeker extends LocalMinimaSeeker {

    private Random random;
    private double costPurturbationFactor = 0.1;

    private List<int[]> locationsVisited;

    public ModifiedLocalMinimaSeeker(int numDimensions, int environmentSize) {
        super(numDimensions, environmentSize);
        random = new Random(System.nanoTime());
        locationsVisited = new ArrayList<>();
    }

    /**
     * Value between 0 and 1.0 specifying how likely the agent will pick a random direction to move in
     * @param costPurturbationFactor
     */
    public void setCostPurturbationFactor(double costPurturbationFactor) {
        this.costPurturbationFactor = costPurturbationFactor;
    }

    @Override
    protected void calculateMoveCost(GridPoint gridPoint) {
        double sum = 0.0;
        for(GridPoint point : gridPoint.vonNeumannNeighbourhood()) {
            sum += point.getActivation();
        }

        if(random.nextDouble() <= costPurturbationFactor) {
            sum += random.nextDouble();
        }

        //  Determine dimension we're in
        for(int i=0; i<getNumDimensions(); i++) {
            if(gridPoint.location()[i] == 1 || gridPoint.location()[i] == 3) {

                //  Now work out what the next move would be
                int[] nextLocation = new int[getNumDimensions()];
                System.arraycopy(currentLocationInSpace, 0, nextLocation, 0, getNumDimensions());

                if(gridPoint.location()[i] == 0) {
                    nextLocation[i] -= 1;
                } else {
                    nextLocation[i] -= 1;
                }

                if(nextLocation[i] < 0) {
                    nextLocation[i] = environmentSize-1;
                } else {
                    nextLocation[i] %= environmentSize;
                }

                for(int[] visited : locationsVisited) {
                    if(Arrays.compare(nextLocation, visited) == 0) {
                        sum += 0.5;
                    }
                }

                break;

            }
        }

        gridPoint.setActivation(sigmoid(sum));
    }

    @Override
    protected void onUpdateCurrentLocation(int[] currentLocationInSpace) {
        locationsVisited.add(currentLocationInSpace);
        if(locationsVisited.size() > 1000) {
            locationsVisited.remove(0);
        }
    }
}
