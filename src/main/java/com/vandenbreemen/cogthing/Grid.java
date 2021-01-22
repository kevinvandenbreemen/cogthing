package com.vandenbreemen.cogthing;

/**
 * Cubic grid
 */
public class Grid {

    private class GridDimension {
        private GridDimension[] dimensionIntersects;
        private double[] activations;

        private GridDimension(GridDimension[] dimensionIntersects) {
            this.dimensionIntersects = dimensionIntersects;
        }

        private GridDimension(double[] activations) {
            this.activations = activations;
        }
    }

    private GridDimension[] firstDimension;


    public Grid(int numDimensions, int numPoints) {
        firstDimension = buildDimensions(numDimensions-1, numPoints);
    }

    private GridDimension[] buildDimensions(int numDimensions, int numPoints) {

        GridDimension[] dimension = new GridDimension[numPoints];

        if(numDimensions == 1) {
            for(int i=0; i<numPoints; i++) {
                dimension[i] = new GridDimension(new double[numPoints]);
            }
        } else {
            for(int i=0; i<numPoints; i++) {
                dimension[i] = new GridDimension(buildDimensions(numDimensions-1, numPoints));
            }
        }
        return dimension;
    }

    public GridPoint at(int ... location) {
        int[] nextLocationSequence = new int[location.length-1];
        System.arraycopy(location, 1, nextLocationSequence, 0, location.length-1);
        return doFetchPoint(firstDimension[location[0]], nextLocationSequence);
    }

    private GridPoint doFetchPoint(GridDimension from, int ... location) {
        if(location.length == 1) {
            return new GridPoint() {

                @Override
                public double getActivation() {
                    return from.activations[location[0]];
                }

                @Override
                public void setActivation(double activation) {
                    from.activations[location[0]] = activation;
                }
            };
        } else {
            int[] nextLocationSequence = new int[location.length-1];
            System.arraycopy(location, 0, nextLocationSequence, 0, location.length-1);
            return doFetchPoint(from.dimensionIntersects[location[0]], nextLocationSequence);
        }
    }

}
