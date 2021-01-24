package com.vandenbreemen.cogthing.render;

import com.vandenbreemen.cogthing.Grid;
import com.vandenbreemen.cogthing.GridPoint;
import com.vandenbreemen.cogthing.SubGrid;
import com.vandenbreemen.cogthing.api.GridManager;
import com.vandenbreemen.jgdv.ApplicationWindow;
import com.vandenbreemen.jgdv.mvp.SystemModel;
import com.vandenbreemen.jgdv.mvp.SystemPresenter;

import java.awt.*;
import java.util.Random;

public class ThingThatMovesAround implements SystemModel {

    public static final int ENV_SIZE = 100;

    private Grid lifeformGrid;
    private SubGrid brain;
    private GridManager brainManager;

    private Grid environment;

    /**
     * Where the creature is in the environment
     */
    private int[] lifeformLocation;

    public ThingThatMovesAround() {
        super();
        this.lifeformGrid = new Grid(2, 5);
        this.environment = new Grid(2, ENV_SIZE);

        brain = lifeformGrid.subGrid(1, 3, 1, 3);
        new GridManager(environment).populateRandom();

        Random random = new Random(System.nanoTime());
        lifeformLocation = new int[]{
                random.nextInt(ENV_SIZE),
                random.nextInt(ENV_SIZE)
        };
    }

    @Override
    public void render(Graphics2D graphics2D, Dimension size) {

        //  Step 1 - sensory inputs
        GridPoint currentLocationInEnvironmentGrid = environment.at(lifeformLocation);

        GridPoint sensorLeft = lifeformGrid.at(0, 2);
        GridPoint sensorRight = lifeformGrid.at(4, 2);
        GridPoint sensorUp = lifeformGrid.at(2, 4);
        GridPoint sensorDown = lifeformGrid.at(2, 0);

        sensorLeft.setActivation(currentLocationInEnvironmentGrid.adjacent(0, false).getActivation());
        sensorRight.setActivation(currentLocationInEnvironmentGrid.adjacent(0, true).getActivation());
        sensorUp.setActivation(currentLocationInEnvironmentGrid.adjacent(1, true).getActivation());
        sensorDown.setActivation(currentLocationInEnvironmentGrid.adjacent(1, false).getActivation());

        brain.visit(new Grid.NodeVisitor() {

            double sigmoid(double x) {
                return (1/( 1 + Math.pow(Math.E,(-1*x))));
            }

            @Override
            public void visit(GridPoint gridPoint, int... location) {

                GridPoint[] points = gridPoint.vonNeumannNeighbourhood();
                double sum = 0;
                for(GridPoint p : points) {
                    sum += p.getActivation();
                }
                System.out.println("SUM @ ("+ location[0]+", " +location[1]+")="+sigmoid(sum));
                gridPoint.setActivation(sigmoid(sum));
            }
        });

        //  Determine next direction to move
        brain.visit(new Grid.NodeVisitor() {

            @Override
            public void visit(GridPoint gridPoint, int... location) {

                if(location[0] == 2 && location[1] == 2) {  //  Center location

                    //System.out.println("("+location[0]+", "+location[1]+")");
                    double max = 0;

                    GridPoint moveDirection = null;

                    GridPoint up = gridPoint.adjacent(1, true);
                    GridPoint down = gridPoint.adjacent(1, false);
                    GridPoint left = gridPoint.adjacent(0, false);
                    GridPoint right = gridPoint.adjacent(0, true);

                    //  Set up registers to tell the system what direction we're going in
                    GridPoint goingUp = up.adjacent(0, true);
                    GridPoint goingDown = down.adjacent(0, true);
                    GridPoint goingLeft = left.adjacent(1, true);
                    GridPoint goingRight = right.adjacent(1, true);

                    if(up.getActivation() > max) {
                        max = up.getActivation();
                        moveDirection = down;
                    }
                    if(down.getActivation() > max) {
                        max = down.getActivation();
                        moveDirection = up;
                    }
                    if(left.getActivation() > max) {
                        max = left.getActivation();
                        moveDirection = right;
                    }
                    if(right.getActivation() > max) {
                        max = right.getActivation();
                        moveDirection = left;
                    }

                    Random rand = new Random(System.nanoTime());
                    if(moveDirection == right) {
                        lifeformLocation[0] += 1;
                        goingRight.setActivation(rand.nextDouble());
                    } else if (moveDirection == left) {
                        lifeformLocation[0] -= 1;
                        goingLeft.setActivation(rand.nextDouble());
                    } else if (moveDirection == up) {
                        lifeformLocation[1] += 1;
                        goingUp.setActivation(rand.nextDouble());
                    } else if (moveDirection == down) {
                        lifeformLocation[1] -= 1;
                        goingDown.setActivation(rand.nextDouble());
                    }

                    if(lifeformLocation[0] < 0) {
                        lifeformLocation[0] = ENV_SIZE-1;
                    }
                    if(lifeformLocation[1] < 0) {
                        lifeformLocation[1] = ENV_SIZE-1;
                    }

                    lifeformLocation[0] %= ENV_SIZE;
                    lifeformLocation[1] %= ENV_SIZE;

                }
            }
        });

        MiniMaxColorCalculator calc = new MiniMaxColorCalculator();
        int numSquaresPerSide = environment.getNumPoints();
        int numPixelsPerSide = (int)Math.ceil(size.height / numSquaresPerSide);
        environment.visit(calc);
        environment.visit(new Grid.NodeVisitor() {
            @Override
            public void visit(GridPoint point, int... location) {

                Color color;

                color = new Color(calc.calculateColorValue(point.getActivation()), 0, 0);

                if(lifeformLocation[0] == location[0] && lifeformLocation[1] == location[1]) {
                    color = Color.GREEN;
                }

                graphics2D.setColor(color);
                graphics2D.fillRect(location[0]*numPixelsPerSide, location[1]*numPixelsPerSide, numPixelsPerSide, numPixelsPerSide);
            }
        });

    }

    public static void main(String[] args) {
        ThingThatMovesAround thing = new ThingThatMovesAround();
        new ApplicationWindow(new SystemPresenter(thing), "Thing that Moves Around", 1000,1000);
    }
}
