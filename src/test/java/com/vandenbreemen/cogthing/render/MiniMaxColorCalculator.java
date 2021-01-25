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

    @Override
    public String toString() {
        return "MiniMaxColorCalculator{" +
                "maxCol=" + maxCol +
                ", minCol=" + minCol +
                '}';
    }

    float calculateColorValue(double activation) {
        float increment = (float)( 1 / (maxCol - minCol));
        float ret = (float)( ( (activation - minCol) * increment ));
        if(ret > 1 || ret < 0) {
            System.out.println("HUZZAH");
        }
        return ret;
    }
}