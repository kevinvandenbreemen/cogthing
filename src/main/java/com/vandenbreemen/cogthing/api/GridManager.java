package com.vandenbreemen.cogthing.api;

import com.vandenbreemen.cogthing.Grid;
import com.vandenbreemen.cogthing.GridPoint;
import com.vandenbreemen.cogthing.IGrid;

import java.util.Random;

public class GridManager {

    private IGrid grid;

    public GridManager(IGrid grid) {
        this.grid = grid;
    }

    /**
     * For testing.  Returns a copy of the grid
     * @return
     */
    public IGrid getGrid() {
        return grid.copy();
    }

    public void process(GridVisitor before, GridNodeVisitor nodeVisitor, GridVisitor after) {

        //  Optional initial processing/visit on the grid
        before.visit(grid);

        //  The nodes in the copy do things based on values in the original grid
        IGrid copy = grid.copy();
        Grid.NodeVisitor visitor = new Grid.NodeVisitor() {
            @Override
            public void visit(GridPoint gridPoint, int... location) {
                nodeVisitor.visit(gridPoint, grid, location);
            }
        };
        copy.visit(visitor);

        //  Replace the grid with the updated copy
        grid = copy;

        //  Optional post processing/visit on updated the grid
        after.visit(grid);
    }

    /**
     * Update the grid at the given point to data contained in the given point
     * @param usingData
     * @param atPoint
     */
    public void update(GridPoint usingData, int ... atPoint) {
        grid.at(atPoint).setActivation(usingData.getActivation());
    }

    /**
     * Gets grid point on underlying grid
     * @param at
     * @return
     */
    public GridPoint getPoint(int ... at) {
        return grid.at(at);
    }

    /**
     * Updates the underlying grid with the given grid.  This method is provided as processing re-sets
     * the internal reference
     * @param with
     */
    public void update(IGrid with) {
        this.grid = with;
    }

    /**
     * Populates the grid with random values between 0 and 1
     */
    public void populateRandom() {
        Random random = new Random(System.nanoTime());
        grid.visit(new Grid.NodeVisitor() {
            @Override
            public void visit(GridPoint gridPoint, int... location) {
                gridPoint.setActivation(random.nextDouble());
            }
        });
    }

}
