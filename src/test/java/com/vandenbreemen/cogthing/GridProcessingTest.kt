package com.vandenbreemen.cogthing

import org.junit.jupiter.api.Test

class GridProcessingTest {

    @Test
    fun `should allow structure that updates adjacent nodes values`() {
        val grid = Grid(2, 2)


        val copy = grid.copy()
        grid.at(0,0).activation = 0.5

        copy.visit(Grid.NodeVisitor{ point, location ->
            val adjacentPoints = listOf(
                    grid.at(location[0] + 1, location[1]),
                    grid.at(location[0] + 1, location[1]+1),
                    grid.at(location[0], location[1]+1),
                    grid.at(location[0] - 1, location[1]),
                    grid.at(location[0] - 1, location[1]-1),
                    grid.at(location[0], location[1] - 1),
                    grid.at(location[0], location[1]-1)
            )
            adjacentPoints.forEach { p->
                if(p.activation > 0) {
                    point.activation += 0.1
                    return@forEach
                }
            }
        })

        for (i in 0 until 2) {
            for (j in 0 until 2) {
                println("($i, $j) = ${copy.at(i, j).activation}")
            }
        }
    }

}