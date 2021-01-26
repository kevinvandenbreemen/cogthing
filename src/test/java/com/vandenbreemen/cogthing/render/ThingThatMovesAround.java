package com.vandenbreemen.cogthing.render;

import com.vandenbreemen.cogthing.Grid;
import com.vandenbreemen.cogthing.GridPoint;
import com.vandenbreemen.cogthing.IGrid;
import com.vandenbreemen.cogthing.api.GridToVectorSpace;
import com.vandenbreemen.cogthing.application.LocalMinimaSeeker;
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

    private LocalMinimaSeeker seeker;

    private Grid environment;

    /**
     * Where the creature is in the environment
     */
    private int[] lifeformLocation;

    private TwoDimensionalFunction function;
    private GridToVectorSpace gridToVectorSpace;

    public ThingThatMovesAround(TwoDimensionalFunction function, double ... minimax2D) {
        super();
        this.seeker = new LocalMinimaSeeker(NUM_DIMENSIONS, ENV_SIZE);
        this.function = function;
        this.environment = new Grid(2, ENV_SIZE);
        gridToVectorSpace = new GridToVectorSpace(environment, minimax2D);

        Random random = new Random(System.nanoTime());
        lifeformLocation = new int[]{
                random.nextInt(ENV_SIZE),
                random.nextInt(ENV_SIZE)
        };
        seeker.setCurrentLocationInSpace(lifeformLocation);
    }

    double sigmoid(double x) {
        return (1/( 1 + Math.pow(Math.E,( (-8*x) +4))));
    }

    @Override
    public void render(Graphics2D graphics2D, Dimension size) {

        //  Step 1 - sensory inputs
        GridPoint currentLocationInEnvironmentGrid = environment.at(lifeformLocation);

        double[] adjacentValues = new double[4];
        adjacentValues[0] = (currentLocationInEnvironmentGrid.adjacent(0, false).getActivation());
        adjacentValues[1] = (currentLocationInEnvironmentGrid.adjacent(0, true).getActivation());
        adjacentValues[2] = (currentLocationInEnvironmentGrid.adjacent(1, false).getActivation());
        adjacentValues[3] = (currentLocationInEnvironmentGrid.adjacent(1, true).getActivation());

        seeker.setAdjacentValues(adjacentValues);

        lifeformLocation = seeker.getNextLocation();

        if(environment.at(lifeformLocation).getActivation() == 0.0) {
            double[] xy = gridToVectorSpace.toVectorSpace(lifeformLocation);
            environment.at(lifeformLocation).setActivation( function.compute(xy[0], xy[1]) );
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

        //  Function with multiple minima
        TwoDimensionalFunction multimin = new TwoDimensionalFunction() {
            @Override
            public double compute(double x, double y) {
                return Math.sin(x) + Math.cos(y);
            }
        };

        TwoDimensionalFunction parabaloid = new TwoDimensionalFunction() {
            @Override
            public double compute(double x, double y) {
                return x*x + y*y;   //  Crude parabaloid
            }
        };

        ThingThatMovesAround thing = new ThingThatMovesAround(multimin, -5.0, 5.0, -5.0, 5.0);

        SecondaryGridVisualizer visualizer = new SecondaryGridVisualizer("Brain", new SystemModel() {
            @Override
            public void render(Graphics2D graphics2D, Dimension size) {

                IGrid brain = thing.seeker;

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
                            graphics2D.fillRect((location[0])*numPixelsPerSide, (location[1])*numPixelsPerSide, numPixelsPerSide, numPixelsPerSide);
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
