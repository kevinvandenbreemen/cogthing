package com.vandenbreemen.cogthing

import org.amshove.kluent.`should be empty`
import org.amshove.kluent.shouldBeEqualTo
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

    @Test
    fun `should provide a way to visit all nodes in a sub-grid`() {
        val grid = Grid(2, 5)
        var count = 0

        val expectedPoints = mutableListOf(
                listOf(1,1),
                listOf(1,2),
                listOf(1,3),
                listOf(2,1),
                listOf(2,2),
                listOf(2,3),
                listOf(3,1),
                listOf(3,2),
                listOf(3,3)
        )

        grid.subGrid(1, 3, 1, 3).visit(Grid.NodeVisitor { gridPoint, location ->
            print(location.asList())
            expectedPoints.remove(location.asList())
            count++
        })

        count shouldBeEqualTo 9
        expectedPoints.`should be empty`()
    }

    @Test
    fun `reproduce bug with 4 dimensional mixup`() {
        val grid = Grid(4, 5)

        grid.at(2,2,0,2).activation shouldBeEqualTo 0.0

        grid.at(2,2,2,2).activation = 1.0
        grid.at(2,2,2,2).activation shouldBeEqualTo  1.0

        grid.at(2,2,0,2).activation shouldBeEqualTo 0.0
        grid.at(2,2,4,2).activation shouldBeEqualTo 0.0

        grid.at(2,2,0,2).activation = 2.0
        grid.at(2,2,4,2).activation = 2.0
        grid.at(2,2,2,2).activation shouldBeEqualTo  1.0
    }

}