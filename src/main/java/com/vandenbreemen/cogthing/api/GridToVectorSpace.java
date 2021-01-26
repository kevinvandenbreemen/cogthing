package com.vandenbreemen.cogthing.api;

import com.vandenbreemen.cogthing.Grid;

/**
 * Mapping from a grid to a vector space
 */
public class GridToVectorSpace {

    private Grid grid;

    /**
     * Min/max points along the coordinate space
     */
    private double[] vectorSpaceMiniMaxPoints;

    /**
     *
     * @param grid
     * @param vectorSpaceMiniMaxPoints  Min and max points along all dimensions.
     */
    public GridToVectorSpace(Grid grid, double ... vectorSpaceMiniMaxPoints) {
        this.grid = grid;
        this.vectorSpaceMiniMaxPoints = vectorSpaceMiniMaxPoints;
    }

    /**
     * Map from the point in the vector space to a in the grid
     * @param pointCoordinates
     * @return  Int position of point in the grid
     */
    public int[] map(double ... pointCoordinates) {

        double max;
        double min;

        int[] result = new int[pointCoordinates.length];

        for(int i=0; i<pointCoordinates.length; i++) {
            min = vectorSpaceMiniMaxPoints[2*i];
            max = vectorSpaceMiniMaxPoints[(2*i)+1];
            double diff = max-min;

            int numPoints = grid.getNumPoints();
            double step = diff / (double)numPoints;
            double distance = pointCoordinates[i] - min;
            result[i] = (int)Math.floor(distance / step);
        }

        return result;
    }

    /**
     * Convert the given set of coordinates in the grid to a point in the vector space
     * @param gridCoordinates
     * @return
     */
    public double[] toVectorSpace(int ... gridCoordinates) {
        double max;
        double min;

        double[] result = new double[gridCoordinates.length];

        for(int i=0; i<gridCoordinates.length; i++) {
            min = vectorSpaceMiniMaxPoints[2*i];
            max = vectorSpaceMiniMaxPoints[(2*i)+1];
            double diff = max-min;
            int numPoints = grid.getNumPoints();
            double step = diff / (double)numPoints;

            result[i] = min + (double)(gridCoordinates[i] * step);
        }

        return result;
    }
}
