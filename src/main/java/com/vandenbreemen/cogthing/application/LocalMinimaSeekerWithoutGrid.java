package com.vandenbreemen.cogthing.application;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Random;

/**
 * Minima seeker that does not employ a grid (grids won't work beyond a couple of dimensions due to memory constraints)
 */
public class LocalMinimaSeekerWithoutGrid {

    private static final byte FORWARD = 1;
    private static final byte BACKWARD = 2;
    private static final byte BACKWARD_FORWARD = 3;

    private int numDimensions;
    private int numPoints;
    private int[] currentLocationInSpace;

    /**
     * Cost / calculated values surrounding the {@link #currentLocationInSpace}
     */
    private double[] adjacentValues;

    public LocalMinimaSeekerWithoutGrid(int numDimensions, int numPoints) {
        this.numDimensions = numDimensions;
        this.numPoints = numPoints;
    }

    public void setCurrentLocationInSpace(int ... currentLocationInSpace) {
        this.currentLocationInSpace = currentLocationInSpace;
    }

    public void setAdjacentValues(double ... adjacentValues) {
        this.adjacentValues = adjacentValues;
    }

    double sigmoid(double x) {
        return (1/( 1 + Math.pow(Math.E,( (-8*x) +4))));
    }

    public int[] getNextLocation() {

        //  First calculate the move costs along each direction
        double[] moveCosts = calculateMoveCosts();

        //  Next determine all possible directions (cheapest) we can move in
        byte[] viableDirections = calculateViableDirections(moveCosts);

        //  Step 3a:  Determine all the directions we can move in
        int directionCount = 0;
        int lastViableDirectionIndex = -1;
        for(int d = 0; d<numDimensions; d++) {
            if(viableDirections[d] > 0) {
                directionCount ++;
                lastViableDirectionIndex = d;
            }
        }
        int[] directionLocations = new int[directionCount];
        int dirIndex = 0;
        for(int d = 0; d<numDimensions; d++) {
            if(viableDirections[d] > 0) {
                directionLocations[dirIndex] = d;
                dirIndex ++;
            }
        }

        Random random = new Random(System.nanoTime());
        int dimensionToMoveAlong = 0;
        boolean moveForward = false;
        int nextDirectionIndex = directionCount > 1 ? random.nextInt(directionCount) : lastViableDirectionIndex;
        dimensionToMoveAlong = nextDirectionIndex;
        if(viableDirections[nextDirectionIndex] == BACKWARD_FORWARD) {
            moveForward = random.nextBoolean();
        } else {
            moveForward = viableDirections[nextDirectionIndex] == FORWARD;
        }

        //  Step 4:  Make the move
        if(moveForward){
            currentLocationInSpace[dimensionToMoveAlong] += 1;
        } else {
            currentLocationInSpace[dimensionToMoveAlong] -= 1;
        }

        //  Step 5:  Make sure we're not out of bounds
        if(currentLocationInSpace[dimensionToMoveAlong] < 0) {
            currentLocationInSpace[dimensionToMoveAlong] = numPoints-1;
        } else {
            currentLocationInSpace[dimensionToMoveAlong] %= numPoints;
        }


        return currentLocationInSpace;
    }

    @NotNull
    private byte[] calculateViableDirections(double[] moveCosts) {
        double minValue = Double.MAX_VALUE;
        double backValue;
        double frontValue;
        byte[] viableDirections = new byte[numDimensions];
        for(int dimension = 0; dimension<numDimensions; dimension++) {

            backValue = moveCosts[2*dimension];
            frontValue = moveCosts[(2*dimension) + 1];

            if(backValue < minValue || frontValue < minValue) {
                Arrays.fill(viableDirections, (byte)0);

                if(backValue < frontValue) {
                    viableDirections[dimension] = BACKWARD;
                    minValue = backValue;
                } else {
                    viableDirections[dimension] = FORWARD;
                    minValue = frontValue;
                }
            } else if (backValue == minValue || frontValue == minValue) {
                if(backValue == minValue && frontValue == minValue) {
                    viableDirections[dimension] = BACKWARD_FORWARD;
                } else {
                    if(backValue == minValue) {
                        viableDirections[dimension] = BACKWARD;
                    } else {
                        viableDirections[dimension] = FORWARD;
                    }
                }
            }

        }
        return viableDirections;
    }

    private double[] calculateMoveCosts() {
        double[] moveCosts = new double[numDimensions*2];
        double sum;
        for(int dimension = 0; dimension<numDimensions; dimension++) {

            double moveBackRaw = adjacentValues[2*dimension];
            double moveForwRaw = adjacentValues[(2*dimension) + 1];

            sum = 0;
            sum += moveBackRaw;
            moveCosts[2*dimension] = sigmoid(sum);

            sum = 0;
            sum += moveForwRaw;
            moveCosts[(2*dimension)+1] = sigmoid(sum);

        }

        return moveCosts;
    }
}
