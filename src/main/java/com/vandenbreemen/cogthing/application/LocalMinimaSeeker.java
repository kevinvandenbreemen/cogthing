package com.vandenbreemen.cogthing.application;

import com.vandenbreemen.cogthing.Grid;
import com.vandenbreemen.cogthing.GridPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class LocalMinimaSeeker extends Grid  {

    private static final byte FORWARD = 1;
    private static final byte BACKWARD = 2;
    private static final byte BACKWARD_FORWARD = 3;

    /**
     * Current location in our space
     */
    protected int[] currentLocationInSpace;

    /**
     * Number of points in the environment this seeker is exploring
     */
    protected int environmentSize;

    public LocalMinimaSeeker(int numDimensions, int environmentSize) {
        super(numDimensions, 5);
        this.environmentSize = environmentSize;
    }

    public void setCurrentLocationInSpace(int ... currentLocationInSpace) {
        this.currentLocationInSpace = currentLocationInSpace;
    }

    double sigmoid(double x) {
        return (1/( 1 + Math.pow(Math.E,( (-8*x) +4))));
    }

    /**
     * Input values
     * @param backToFrontValues
     */
    public void setAdjacentValues(double ... backToFrontValues) {

        /*
        Trying to create something like this (where 0 is a "sensor") in an arbitrary number of dimensions
        * * 0 * *
        * * * * *
        0 * * * 0   //  <-- Where the guy on the left (min) is first of a tuple, and guy on right (max) is second of a tuple
        * * * * *
        * * 0 * *
         */

        int[] locationMin;
        int[] locationMax;
        for(int dim = 0; dim<getNumDimensions(); dim++) {
            locationMin = new int[getNumDimensions()];
            locationMax = new int[getNumDimensions()];
            for(int d = 0; d<getNumDimensions(); d++) {
                if(d == dim) {
                    locationMin[d] = 0;
                    locationMax[d] = 4;
                } else {
                    locationMin[d] = 2;
                    locationMax[d] = 2;
                }
            }

            at(locationMin).setActivation(backToFrontValues[2*dim]);
            at(locationMax).setActivation(backToFrontValues[(2*dim) + 1]);
        }
    }

    /**
     * Get the next location to move to based on adjacent values
     * @return
     */
    public int[] getNextLocation() {

        int[] newCurrentLocation = new int[currentLocationInSpace.length];
        System.arraycopy(currentLocationInSpace, 0, newCurrentLocation, 0, currentLocationInSpace.length);
        onUpdateCurrentLocation(newCurrentLocation);

        /*
        Trying to create something like this (where c is a "move cost") in an arbitrary number of dimensions
        * * 0 * *
        * * c * *
        0 c * c 0   //  <-- Where the guy on the left (min) is first of a tuple, and guy on right (max) is second of a tuple
        * * c * *
        * * 0 * *
         */

        //  Step 1:  Calculate move cost (c) along all dimensions

        //  Step 1a:  What locations do we need to check?
        int[] locationMin;
        int[] locationMax;

        List<int[]> locationsToPerformLogicOn = new ArrayList<>();

        for(int dimension = 0; dimension < getNumDimensions(); dimension++) {

            locationMin = new int[getNumDimensions()];
            locationMax = new int[getNumDimensions()];

            for(int dim = 0; dim<getNumDimensions(); dim++) {

                if(dim == dimension) {
                    locationMin[dim] = 1;
                    locationMax[dim] = 3;
                } else {
                    locationMin[dim] = 2;
                    locationMax[dim] = 2;
                }

            }
            locationsToPerformLogicOn.add(locationMin);
            locationsToPerformLogicOn.add(locationMax);
        }

        //  Step 2:  Calculate move costs
        visit(new NodeVisitor() {
            @Override
            public void visit(GridPoint gridPoint, int... location) {

                //   1:  Is this a location
                boolean shouldDoMath = false;
                for(int[] loc : locationsToPerformLogicOn) {
                    if(Arrays.equals(loc, location)) {
                        shouldDoMath = true;
                        break;
                    }
                }

                //  If so, calculate its cost based on adjacent input and possible discount factor at center of grid
                if(shouldDoMath) {
                    calculateMoveCost(gridPoint);
                }

            }
        });

        //  Step 3:  Find cheapest move
        double minValue = Double.MAX_VALUE;
        Random random = new Random(System.nanoTime());
        int dimensionToMoveAlong = 0;
        boolean moveForward = false;

        byte[] viableDirections = new byte[getNumDimensions()];

        for(int dimension = 0; dimension<getNumDimensions(); dimension++) {
            double backValue = at(locationsToPerformLogicOn.get(2*dimension)).getActivation();
            double frontValue = at(locationsToPerformLogicOn.get((2*dimension) + 1)).getActivation();

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

        //  Step 3a:  Determine all the directions we can move in
        int directionCount = 0;
        int lastViableDirectionIndex = -1;
        for(int d = 0; d<getNumDimensions(); d++) {
            if(viableDirections[d] > 0) {
                directionCount ++;
                lastViableDirectionIndex = d;
            }
        }
        int[] directionLocations = new int[directionCount];
        int dirIndex = 0;
        for(int d = 0; d<getNumDimensions(); d++) {
            if(viableDirections[d] > 0) {
                directionLocations[dirIndex] = d;
                dirIndex ++;
            }
        }

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
            currentLocationInSpace[dimensionToMoveAlong] = environmentSize-1;
        } else {
            currentLocationInSpace[dimensionToMoveAlong] %= environmentSize;
        }


        return currentLocationInSpace;
    }

    protected void onUpdateCurrentLocation(int[] currentLocationInSpace) {

    }

    /**
     * Calculate the move cost for the given point
     * @param gridPoint
     */
    protected void calculateMoveCost(GridPoint gridPoint) {
        double sum = 0.0;
        for(GridPoint point : gridPoint.vonNeumannNeighbourhood()) {
            sum += point.getActivation();
        }
        gridPoint.setActivation(sigmoid(sum));
    }


}
