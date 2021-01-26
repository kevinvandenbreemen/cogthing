package com.vandenbreemen.cogthing.render;

import com.vandenbreemen.cogthing.Grid;
import com.vandenbreemen.cogthing.GridPoint;
import com.vandenbreemen.cogthing.IGrid;
import com.vandenbreemen.cogthing.SubGrid;
import com.vandenbreemen.cogthing.api.GridManager;
import com.vandenbreemen.cogthing.api.GridNodeVisitor;
import com.vandenbreemen.cogthing.api.GridToVectorSpace;
import com.vandenbreemen.cogthing.api.GridVisitor;
import com.vandenbreemen.jgdv.ApplicationWindow;
import com.vandenbreemen.jgdv.mvp.LogicCycleObserver;
import com.vandenbreemen.jgdv.mvp.MenuItem;
import com.vandenbreemen.jgdv.mvp.SystemModel;
import com.vandenbreemen.jgdv.mvp.SystemPresenter;

import java.awt.*;
import java.util.Random;

interface TwoDimensionalFunction{
    double compute(double x, double y);
}

public class ThingThatMovesAround implements SystemModel {

    public static final int ENV_SIZE = 100;
    public static final int NUM_DIMENSIONS = 2;

    private Grid lifeformGrid;
    GridManager brainManager;

    private Grid environment;

    /**
     * Where the creature is in the environment
     */
    private int[] lifeformLocation;

    private TwoDimensionalFunction function;
    private GridToVectorSpace gridToVectorSpace;

    public ThingThatMovesAround(TwoDimensionalFunction function, double ... minimax2D) {
        super();
        this.function = function;
        this.lifeformGrid = new Grid(2, 5);
        this.environment = new Grid(2, ENV_SIZE);
        gridToVectorSpace = new GridToVectorSpace(environment, minimax2D);

        SubGrid brain = lifeformGrid.subGrid(1, 3, 1, 3);
        this.brainManager = new GridManager(brain);

        Random random = new Random(System.nanoTime());
        lifeformLocation = new int[]{
                random.nextInt(ENV_SIZE),
                random.nextInt(ENV_SIZE)
        };
    }

    double sigmoid(double x) {
        return (1/( 1 + Math.pow(Math.E,( (-8*x) +4))));
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

        GridPoint currentCenter = brainManager.getPoint(2,2);
        brainManager.update(lifeformGrid.subGrid(1, 3, 1, 3));
        brainManager.update(currentCenter, 2,2);

        brainManager.process(new GridVisitor() {
            @Override
            public void visit(IGrid grid) {

            }
        }, new GridNodeVisitor() {
            @Override
            public void visit(GridPoint gridPoint, IGrid grid, int... location) {
                GridPoint[] points = gridPoint.vonNeumannNeighbourhood();
                double sum = 0;
                for(GridPoint p : points) {
                    sum += p.getActivation();
                }

                if(sum > 0 ){
                    gridPoint.setActivation(sigmoid(sum));
                }
            }
        }, new GridVisitor() {
            @Override
            public void visit(IGrid grid) {

            }
        });

        //  Determine next direction to move
        brainManager.process(new GridVisitor() {
            @Override
            public void visit(IGrid grid) {

            }
        }, new GridNodeVisitor() {
            @Override
            public void visit(GridPoint gridPoint, IGrid grid, int... location) {
                if(location[0] == 2 && location[1] == 2) {  //  Center location

                    Random random = new Random(System.nanoTime());
                    double min = 1;
                    int preferredDimension = -1;
                    boolean preferredForward =  false;
                    for(int i=0; i<NUM_DIMENSIONS; i++) {

                        GridPoint directionForward = gridPoint.adjacent(i, true);
                        GridPoint directionBackward = gridPoint.adjacent(i, false);
                        if(directionBackward.getActivation() <= min) {
                            if(directionBackward.getActivation() == min && random.nextBoolean()) {

                            } else {
                                min = directionBackward.getActivation();
                                preferredDimension = i;
                                preferredForward = false;
                            }
                        }
                        if(directionForward.getActivation() <= min) {
                            if(directionBackward.getActivation() == min && random.nextBoolean()) {
                            } else {

                                min = directionForward.getActivation();
                                preferredDimension = i;
                                preferredForward = true;
                            }
                        }
                        else {
                            continue;
                        }

                    }

                    if(preferredDimension >= 0) {
                        Random rand = new Random(System.nanoTime());
                        if(preferredForward) {
                            lifeformLocation[preferredDimension] += 1;
                        } else {
                            lifeformLocation[preferredDimension] -= 1;
                        }

                        if(lifeformLocation[preferredDimension] < 0) {
                            lifeformLocation[preferredDimension] = ENV_SIZE-1;
                        }

                        lifeformLocation[preferredDimension] %= ENV_SIZE;

                        gridPoint.setActivation(-min);
                    }

                }
            }
        }, new GridVisitor() {
            @Override
            public void visit(IGrid grid) {

            }
        });

        if(environment.at(lifeformLocation).getActivation() == 0.0) {
            double[] xy = gridToVectorSpace.toVectorSpace(lifeformLocation);
            environment.at(lifeformLocation).setActivation( sigmoid(function.compute(xy[0], xy[1])) );
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
        ThingThatMovesAround thing = new ThingThatMovesAround(new TwoDimensionalFunction() {
            @Override
            public double compute(double x, double y) {
                return x*x + y*y;   //  Crude parabaloid
            }
        }, -1.0, 1.0, -1.0, 1.0);

        SecondaryGridVisualizer visualizer = new SecondaryGridVisualizer("Brain", new SystemModel() {
            @Override
            public void render(Graphics2D graphics2D, Dimension size) {

                IGrid brain = thing.brainManager.getGrid();

                MiniMaxColorCalculator calc = new MiniMaxColorCalculator();
                int numSquaresPerSide = brain.getNumPoints();
                int numPixelsPerSide = (int)Math.ceil(size.height / numSquaresPerSide);
                brain.visit(calc);

                brain.visit(new Grid.NodeVisitor() {
                    @Override
                    public void visit(GridPoint point, int... location) {
                        Color color;

                        float calculatedRed = calc.calculateColorValue(point.getActivation());
                        try {
                            color = new Color(calculatedRed, 0, 0);
                            graphics2D.setColor(color);
                            graphics2D.fillRect((location[0]-1)*numPixelsPerSide, (location[1]-1)*numPixelsPerSide, numPixelsPerSide, numPixelsPerSide);
                        } catch(Exception ex) {
                            ex.printStackTrace();
                        }


                    }
                });

            }
        });

        SystemPresenter presenter = new SystemPresenter(thing);
        ApplicationWindow window = new ApplicationWindow(presenter, "Thing that Moves Around", 1000,1000);

        window.addFileMenuItem(new MenuItem() {
            @Override
            public String getName() {
                return "BRAIN VISUALIZER";
            }

            @Override
            public void doAction() {
                visualizer.display();
            }
        });

        presenter.addObserver(new LogicCycleObserver() {
            @Override
            public void update() {
                if(visualizer.isVisible()) {
                    visualizer.repaint();
                }
            }
        });
    }
}
