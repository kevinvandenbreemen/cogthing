package com.vandenbreemen.cogthing;

public class SubGrid {

    private Grid parent;
    private int[] fromAndToPositions;

    SubGrid(Grid parent, int...fromAndToPositions) {
        if(fromAndToPositions.length != parent.getNumDimensions()*2) {
            throw new RuntimeException("Invalid dimension specification -- expected " + parent.getNumDimensions()*2+", but got "+ fromAndToPositions.length);
        }

        this.parent = parent;
        this.fromAndToPositions = fromAndToPositions;
    }

    public GridPoint at(int ... location) {
        if(location.length != parent.getNumDimensions()) {
            throw new RuntimeException("Invalid dimension specification -- expected " + parent.getNumDimensions()+", but got "+ fromAndToPositions.length);
        }

        int[] transformed = new int[location.length];
        int diff;
        for(int i=0; i<transformed.length; i++) {
            diff = (fromAndToPositions[(2*i)+1] - fromAndToPositions[2*i]) + 1; //  Recall, range of subset is inclusive
            transformed[i] = location[i] % diff;
            transformed[i] += fromAndToPositions[2*i];
        }

        return parent.at(transformed);
    }

    public void visit(Grid.NodeVisitor visitor) {
        parent.visit(visitor, fromAndToPositions[0], fromAndToPositions[1]);
    }

}
