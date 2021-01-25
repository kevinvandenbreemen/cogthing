package com.vandenbreemen.cogthing.api;

import com.vandenbreemen.cogthing.IGrid;

@FunctionalInterface
public interface GridVisitor {

    void visit(IGrid grid);

}
