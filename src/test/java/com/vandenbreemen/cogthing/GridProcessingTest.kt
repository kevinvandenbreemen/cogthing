package com.vandenbreemen.cogthing

import com.vandenbreemen.cogthing.api.GridManager
import com.vandenbreemen.cogthing.api.GridNodeVisitor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GridProcessingTest {

    @Test
    fun `should allow structure that updates adjacent nodes values`() {
        val grid = Grid(2, 4)
        grid.at(0,0).activation = 0.5
        val processor = GridManager(grid)

        processor.process({_->}, GridNodeVisitor{ point, originalGrid, location ->
            val adjacentPoints = listOf(
                    originalGrid.at(location[0] - 1, location[1]+1),
                    originalGrid.at(location[0], location[1]+1),
                    originalGrid.at(location[0]+1, location[1]+1),
                    originalGrid.at(location[0]-1, location[1]),
                    originalGrid.at(location[0] + 1, location[1]),
                    originalGrid.at(location[0] - 1, location[1]-1),
                    originalGrid.at(location[0], location[1] - 1),
                    originalGrid.at(location[0]+1, location[1]-1)
            )

            var updated = false
            adjacentPoints.forEach { p->
                if(p.activation > 0 && !updated) {
                    point.activation += 0.1
                    println("Increment act at (${location.asList()}) due to activ = ${p.activation}")
                    updated = true
                }
            }
            if(updated) {
                println("Updated at ${location.asList()}")
            } else {
                println("Did not update at ${location.asList()}")
            }
        }, { copy->
            for (i in 0 until 4) {
                for (j in 0 until 4) {
                    println("($i, $j) = ${copy.at(i, j).activation}")
                }
            }
        })

        assertEquals(0.0, processor.grid.at(2,2).activation)
        assertEquals(0.1, processor.grid.at(1,1).activation)
    }

}