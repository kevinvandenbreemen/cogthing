package com.vandenbreemen.cogthing

import org.junit.jupiter.api.Assertions.*
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
        assertEquals(0.0, grid.at(3,2,1).activation)
    }

    @Test
    fun `should provide a way to visit each node in the grid`() {
        val grid = Grid(3, 2)
        var count = 0

        val expectedPoints = mutableSetOf(
                listOf(0,0,0),
                listOf(0,0,1),
                listOf(0,1,0),
                listOf(0,1,1),
                listOf(1,1,0),
                listOf(1,1,1),
                listOf(1,0,1),
                listOf(1,0,0),
        )

        grid.visit { gridPoint, location ->
            println(location.asList())
            expectedPoints.remove(location.asList())
            count++
        }

        assertEquals(8, count)
        print(expectedPoints)
        assertTrue(expectedPoints.isEmpty())
    }

}