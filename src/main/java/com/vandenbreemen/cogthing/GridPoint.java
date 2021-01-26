package com.vandenbreemen.cogthing;

public interface GridPoint {

    double getActivation();

    void setActivation(double activation);

    /**
     * Gets the adjacent grid point along the given dimension
     * @param dimension Dimension
     * @param frontBack Front (ie dimension value + 1) or back (ie dimension value - 1)
     * @return
     */
    GridPoint adjacent(int dimension, boolean frontBack);

    /**
     * Get all points adjacent to this point (von Neumann neighbourhood)
     * @return
     */
    GridPoint[] vonNeumannNeighbourhood();

    /**
     * get the location of this point in the grid
     */
    int[] location();
}
