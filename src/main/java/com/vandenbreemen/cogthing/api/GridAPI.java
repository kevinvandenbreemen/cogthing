package com.vandenbreemen.cogthing.api;

import com.vandenbreemen.cogthing.Grid;

public interface GridAPI {

    static GridAPI getDefault(Grid grid) {
        return new GridAPI() {
            @Override
            public void input(Position position, double value) {
                grid.at(position.getCoordinates()).setActivation(value);
            }

            @Override
            public double[] output(Position ... position) {

                double[] outputs = new double[position.length];
                for(int i=0; i<outputs.length; i++) {
                    outputs[i] = grid.at(position[i].getCoordinates()).getActivation();
                }

                return outputs;
            }
        };
    }

    void input(Position position, double value);
    double[] output(Position ... position);

}
