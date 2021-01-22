package com.vandenbreemen.cogthing

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class GridTest {

    @Test
    fun `should address cell in 2-space`() {
        val grid = Grid(2, 10)

        assertNotNull(grid.at(1,1))
    }

    @Test
    fun `should update cell in 2-space`() {
        val grid = Grid(2, 10)
        grid.at(1,2).activation = 3.14;
        assertEquals(grid.at(1,2).activation, 3.14)
    }

    @Test
    fun `should update cell in 3-space`() {
        val grid = Grid(3, 10)
        grid.at(1,2, 3).activation = 3.14;
        assertEquals(grid.at(1,2, 3).activation, 3.14)
    }

    @Test
    fun `should provide for creating copies of a grid`() {
        val grid = Grid(3, 10)
        val copy = grid.copy()
        grid.at(1,2, 3).activation = 3.14;

        assertEquals(copy.at(1,2, 3).activation, 0.0)

        copy.at(3,2,1).activation = 1.22;
        assertEquals(1.22, copy.at(3,2,1).activation);
    }

}