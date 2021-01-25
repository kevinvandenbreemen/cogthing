package com.vandenbreemen.cogthing.api;

import com.vandenbreemen.cogthing.GridPoint;
import com.vandenbreemen.cogthing.IGrid;

@FunctionalInterface
public interface GridNodeVisitor {

    /**
     * Visit particular point on the grid
     * @param point
     * @param grid      The rest of the grid
     * @param location  Location of the point
     */
    void visit(GridPoint point, IGrid grid, int...location);

}
