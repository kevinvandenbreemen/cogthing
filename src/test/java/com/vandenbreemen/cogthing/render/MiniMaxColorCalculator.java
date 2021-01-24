package com.vandenbreemen.cogthing.render;

import com.vandenbreemen.cogthing.Grid;
import com.vandenbreemen.cogthing.GridPoint;

class MiniMaxColorCalculator implements Grid.NodeVisitor {

    double maxCol = 0;
    double minCol = 0;

    @Override
    public void visit(GridPoint gridPoint, int... location) {
        if(gridPoint.getActivation() < minCol) {
            minCol = gridPoint.getActivation();
        }
        if(gridPoint.getActivation() > maxCol) {
            maxCol = gridPoint.getActivation();
        }
    }

    float calculateColorValue(double activation) {
        float increment = (float)((maxCol - minCol) / 256f);
        return (float)( (increment * activation) * 256);
    }
}