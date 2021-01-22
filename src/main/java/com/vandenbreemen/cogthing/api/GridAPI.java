package com.vandenbreemen.cogthing.api;

import com.vandenbreemen.cogthing.Grid;

public interface GridAPI {

    static GridAPI getDefault(Grid grid) {
        return new GridAPI() {
            @Override
            public void input(Position position, double value) {
                grid.at(position.getCoordinates()).setActivation(value);
            }
        };
    }

    void input(Position position, double value);

}
