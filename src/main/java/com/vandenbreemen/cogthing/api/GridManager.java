package com.vandenbreemen.cogthing.api;

import com.vandenbreemen.cogthing.Grid;
import com.vandenbreemen.cogthing.GridPoint;

import java.util.Random;

public class GridManager {

    private Grid grid;

    public GridManager(Grid grid) {
        this.grid = grid;
    }

    /**
     * For testing.  Returns a copy of the grid
     * @return
     */
    public Grid getGrid() {
        return grid.copy();
    }

    public void process(GridVisitor before, GridNodeVisitor nodeVisitor, GridVisitor after) {

        //  Optional initial processing/visit on the grid
        before.visit(grid);

        //  The nodes in the copy do things based on values in the original grid
        Grid copy = grid.copy();
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
