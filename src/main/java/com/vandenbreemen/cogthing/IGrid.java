package com.vandenbreemen.cogthing;

public interface IGrid {
    GridPoint at(int... location);

    IGrid copy();

    void visit(Grid.NodeVisitor visitor);
    void visit(Grid.NodeVisitor visitor, int startPoint, int endPoint);

    int getNumDimensions();

    int getNumPoints();
}
