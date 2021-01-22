package com.vandenbreemen.cogthing.api;

public class Position {

    private int[] coordinates;

    public Position(int ... coordinates) {
        this.coordinates = coordinates;
    }

    public int[] getCoordinates() {
        return coordinates;
    }
}
