package com.vandenbreemen.cogthing;

/**
 * Cubic grid
 */
public class Grid {

    @FunctionalInterface
    public interface NodeVisitor {
        void visit(GridPoint gridPoint, int...location);
    }

    private class GridDimension {
        private GridDimension[] dimensionIntersects;
        private double[] activations;

        private GridDimension(GridDimension[] dimensionIntersects) {
            this.dimensionIntersects = dimensionIntersects;
        }

        private GridDimension(double[] activations) {
            this.activations = activations;
        }


        private GridDimension copy() {
            if(activations != null) {
                double[] copiedActivations = new double[activations.length];
                System.arraycopy(activations, 0, copiedActivations, 0, activations.length);
                return new GridDimension(copiedActivations);
            } else {
                GridDimension[] copiedIntersects = new GridDimension[dimensionIntersects.length];
                for(int i=0; i<copiedIntersects.length; i++) {
                    copiedIntersects[i] = dimensionIntersects[i].copy();
                }
                return new GridDimension(copiedIntersects);
            }
        }
    }

    private GridDimension[] firstDimension;


    public Grid(int numDimensions, int numPoints) {
        firstDimension = buildDimensions(numDimensions-1, numPoints);
    }

    private Grid(GridDimension[] firstDimension) {
        this.firstDimension = firstDimension;
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

        int[] massaged = new int[location.length];
        System.arraycopy(location, 0, massaged, 0, location.length);
        for(int i=0; i<location.length; i++) {
            if(massaged[i] < 0) {
                massaged[i] = firstDimension.length + massaged[i];
            } else {
                massaged[i] %= firstDimension.length;
            }
        }

        int[] nextLocationSequence = new int[massaged.length-1];
        System.arraycopy(massaged, 1, nextLocationSequence, 0, massaged.length-1);
        return doFetchPoint(firstDimension[massaged[0]], location, nextLocationSequence);
    }

    private GridPoint doFetchPoint(GridDimension from, int[] originalPoint, int ... location) {
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

                @Override
                public GridPoint adjacent(int dimension, boolean frontBack) {
                    int[] locationCopy = new int[originalPoint.length];
                    System.arraycopy(originalPoint, 0, locationCopy, 0, originalPoint.length);
                    if(frontBack) {
                        locationCopy[dimension] += 1;
                    } else {
                        locationCopy[dimension] -= 1;
                    }
                    return at(locationCopy);
                }
            };
        } else {
            int[] nextLocationSequence = new int[location.length-1];
            System.arraycopy(location, 0, nextLocationSequence, 0, location.length-1);
            return doFetchPoint(from.dimensionIntersects[location[0]], originalPoint, nextLocationSequence);
        }
    }

    public Grid copy() {
        GridDimension[] copiedDimensions = new GridDimension[firstDimension.length];
        for(int i=0; i<copiedDimensions.length; i++) {
            copiedDimensions[i] = firstDimension[i].copy();
        }
        return new Grid(copiedDimensions);
    }

    public void visit(NodeVisitor visitor) {
        visit(visitor, 0, firstDimension.length-1);
    }

    public void visit(NodeVisitor visitor, int startPoint, int endPoint) {
        for(int i=startPoint; i<=endPoint; i++) {
            doVisits(visitor, startPoint, endPoint, firstDimension[i], i);
        }
    }

    private void doVisits(NodeVisitor visitor, int startPoint, int endPoint, GridDimension dimension, int...location) {
        if(dimension.activations != null) {
            int[] finalLocation = new int[location.length+1];
            System.arraycopy(location, 0, finalLocation, 0, location.length);
            for(int i=startPoint; i<=endPoint; i++) {
                finalLocation[location.length] = i;
                visitor.visit(at(finalLocation), finalLocation);
            }
        } else {
            int[] finalLocation = new int[location.length+1];
            System.arraycopy(location, 0, finalLocation, 0, location.length);
            for(int i=0; i<dimension.dimensionIntersects.length; i++) {
                finalLocation[location.length] = i;
                doVisits(visitor, startPoint, endPoint, dimension.dimensionIntersects[i], finalLocation);
            }
        }
    }

    /**
     * Get the number of axes this grid supports
     * @return
     */
    public int getNumDimensions() {
        int result =  doGetNumDim(firstDimension[0], 1);
        return result;
    }

    private int doGetNumDim(GridDimension dim, int numDim) {
        if(dim.dimensionIntersects != null) {
            return doGetNumDim(dim.dimensionIntersects[0], numDim + 1);
        }
        return numDim + 1;
    }

    public int getNumPoints() {
        return firstDimension.length;
    }

    public SubGrid subGrid(int ... fromAndToDimensions) {
        return new SubGrid(this, fromAndToDimensions);
    }
}
