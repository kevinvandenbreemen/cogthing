package com.vandenbreemen.cogthing.application;

import com.vandenbreemen.cogthing.Grid;
import com.vandenbreemen.cogthing.GridPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LocalMinimaSeeker extends Grid  {

    /**
     * Current location in our space
     */
    private int[] currentLocationInSpace;

    public LocalMinimaSeeker(int numDimensions) {
        super(numDimensions, 5);
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
            for(int dim = 0; dim<getNumDimensions(); dim++) {
                locationMin = new int[getNumDimensions()];
                locationMax = new int[getNumDimensions()];
                for(int d = 0; d<getNumDimensions(); d++) {
                    if(d == dim) {
                        locationMin[d] = 1;
                        locationMax[d] = 3;
                    } else {
                        locationMin[d] = 2;
                        locationMax[d] = 2;
                    }
                }

                locationsToPerformLogicOn.add(locationMin);
                locationsToPerformLogicOn.add(locationMax);
            }
        }

        //  Step 2:  Calculate move costs
        visit(new NodeVisitor() {
            @Override
            public void visit(GridPoint gridPoint, int... location) {

                //   1:  Is this a location
                boolean shouldDoMath = false;
                for(int[] loc : locationsToPerformLogicOn) {
                    if(Arrays.compare(loc, location) == 0) {
                        shouldDoMath = true;
                        break;
                    }
                }

                //  If so, calculate its cost based on adjacent input and possible discount factor at center of grid
                if(shouldDoMath) {
                    double sum = 0.0;
                    for(GridPoint point : gridPoint.vonNeumannNeighbourhood()) {
                        sum += point.getActivation();
                    }
                    gridPoint.setActivation(sigmoid(sum));
                }

            }
        });

        //  Step 3:  Find cheapest move
        double minValue = Double.MAX_VALUE;
        int dimensionToMoveAlong = 0;
        boolean moveForward = false;
        for(int dimension = 0; dimension<getNumDimensions(); dimension++) {
            double backValue = at(locationsToPerformLogicOn.get(2*dimension)).getActivation();
            double frontValue = at(locationsToPerformLogicOn.get((2*dimension) + 1)).getActivation();

            if(backValue < minValue || frontValue < minValue) {
                dimensionToMoveAlong = dimension;
                if(backValue < frontValue) {
                    moveForward = false;
                    minValue = backValue;
                } else {
                    moveForward = true;
                    minValue = frontValue;
                }
            }
        }

        //  Step 4:  Make the move
        if(moveForward){
            currentLocationInSpace[dimensionToMoveAlong] += 1;
        } else {
            currentLocationInSpace[dimensionToMoveAlong] -= 1;
        }

        return currentLocationInSpace;
    }


}
