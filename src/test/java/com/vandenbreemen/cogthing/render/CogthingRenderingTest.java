package com.vandenbreemen.cogthing.render;

import com.vandenbreemen.cogthing.Grid;
import com.vandenbreemen.cogthing.GridPoint;
import com.vandenbreemen.cogthing.IGrid;
import com.vandenbreemen.cogthing.api.GridManager;
import com.vandenbreemen.cogthing.api.GridNodeVisitor;
import com.vandenbreemen.cogthing.api.GridVisitor;
import com.vandenbreemen.jgdv.ApplicationWindow;
import com.vandenbreemen.jgdv.mvp.SystemModel;
import com.vandenbreemen.jgdv.mvp.SystemPresenter;

import java.awt.*;

class GridModel implements SystemModel {

    private GridManager gridManager;

    public GridModel(GridManager gridManager) {
        if(gridManager.getGrid().getNumDimensions() != 2) {
            throw new RuntimeException("Only 2-dimensional grids are supported");
        }
        this.gridManager = gridManager;
    }

    @Override
    public void render(Graphics2D graphics2D, Dimension size) {

        gridManager.process(new GridVisitor() {
            @Override
            public void visit(IGrid grid) {
                gridManager.populateRandom();
            }
        }, new GridNodeVisitor() {

            double sigmoid(double x) {
                return (1/( 1 + Math.pow(Math.E,(-1*x))));
            }

            @Override
            public void visit(GridPoint point, IGrid grid, int... location) {
                //  Step 1 fetch everything adjacent
                GridPoint currentPoint = grid.at(location);
                GridPoint up = currentPoint.adjacent(1, true);
                GridPoint down = currentPoint.adjacent(1, false);
                GridPoint left  = currentPoint.adjacent(0, false);
                GridPoint right = currentPoint.adjacent(0, true);

                double sum = up.getActivation()+down.getActivation()+left.getActivation()+right.getActivation();
                sum = sigmoid(sum);
                if(sum > 0.75||sum < 0.25) {
                    sum = 0;
                }

                point.setActivation(sum);
            }
        }, new GridVisitor() {
            @Override
            public void visit(IGrid grid) {
                MiniMaxColorCalculator calc = new MiniMaxColorCalculator();
                grid.visit(calc);


                int numSquaresPerSide = grid.getNumPoints();
                int numPixelsPerSide = (int)Math.ceil(size.height / numSquaresPerSide);

                Color color;
                GridPoint point;

                for(int i=0; i<numSquaresPerSide; i++) {
                    for(int j=0; j<numSquaresPerSide; j++) {
                        point = grid.at(i, j);
                        color = new Color(calc.calculateColorValue(point.getActivation()), 0, 0);
                        graphics2D.setColor(color);
                        graphics2D.fillRect(i*numPixelsPerSide, j*numPixelsPerSide, numPixelsPerSide, numPixelsPerSide);

                        color = Color.black;
                        graphics2D.setColor(color);
                        graphics2D.drawString(Double.toString(point.getActivation()), i*numPixelsPerSide, j*numPixelsPerSide);
                    }
                }
            }
        });


    }


}

public class CogthingRenderingTest {

    public static void main(String[] args) {

        Grid grid = new Grid(2, 10);
        GridManager manager = new GridManager(grid);
        manager.populateRandom();

        new ApplicationWindow(new SystemPresenter(new GridModel(manager)), "Test Grid", 1000,1000);

    }

}
