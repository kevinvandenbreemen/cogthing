package com.vandenbreemen.cogthing.api;

import com.vandenbreemen.cogthing.Grid;

@FunctionalInterface
public interface GridVisitor {

    void visit(Grid grid);

}
