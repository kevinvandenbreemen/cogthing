package com.vandenbreemen.cogthing.render;

import com.vandenbreemen.cogthing.Grid;
import com.vandenbreemen.cogthing.GridPoint;
import com.vandenbreemen.cogthing.SubGrid;
import com.vandenbreemen.jgdv.ApplicationWindow;
import com.vandenbreemen.jgdv.mvp.SystemModel;
import com.vandenbreemen.jgdv.mvp.SystemPresenter;

import java.awt.*;
import java.util.Random;

public class ThingThatMovesAround implements SystemModel {

    public static final int ENV_SIZE = 100;
    public static final int NUM_DIMENSIONS = 2;

    private Grid lifeformGrid;
    private SubGrid brain;

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

                gridPoint.setActivation(sigmoid(sum));
            }
        });

        //  Determine next direction to move
        brain.visit(new Grid.NodeVisitor() {

            @Override
            public void visit(GridPoint gridPoint, int... location) {

                if(location[0] == 2 && location[1] == 2) {  //  Center location

                    double max = 0;

                    GridPoint currentDirectionCostRegister = null; //  Store random value to artificially inflate the cost of moving in the direction
                    int preferredDimension = -1;
                    boolean preferredForward =  false;
                    for(int i=0; i<NUM_DIMENSIONS; i++) {

                        GridPoint preferredDirection = null;   //  Direction to go that is opposite of costliest direction

                        GridPoint directionForward = gridPoint.adjacent(i, true);
                        GridPoint directionBackward = gridPoint.adjacent(i, false);
                        if(directionBackward.getActivation() > max) {
                            max = directionBackward.getActivation();
                            preferredDimension = i;
                            preferredForward = true;
                            preferredDirection = directionForward;
                        }
                        else if(directionForward.getActivation() > max) {
                            max = directionForward.getActivation();
                            preferredDimension = i;
                            preferredForward = false;
                            preferredDirection = directionBackward;
                        }
                        else {
                            continue;
                        }

                        int nonIAxis = i+1;
                        if(nonIAxis >= NUM_DIMENSIONS) {
                            nonIAxis = 0;
                        }
                        currentDirectionCostRegister = preferredDirection.adjacent(nonIAxis, true);

                    }

                    if(preferredDimension >= 0) {
                        Random rand = new Random(System.nanoTime());
                        currentDirectionCostRegister.setActivation(rand.nextDouble());
                        if(preferredForward) {
                            lifeformLocation[preferredDimension] += 1;
                        } else {
                            lifeformLocation[preferredDimension] -= 1;
                        }

                        if(lifeformLocation[preferredDimension] < 0) {
                            lifeformLocation[preferredDimension] = ENV_SIZE-1;
                        }

                        lifeformLocation[preferredDimension] %= ENV_SIZE;
                    }

                }
            }
        });

        if(environment.at(lifeformLocation).getActivation() == 0.0) {
            environment.at(lifeformLocation).setActivation( new Random(System.nanoTime()).nextDouble() );
        }

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
