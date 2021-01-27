package com.vandenbreemen.cogthing.api

import com.vandenbreemen.cogthing.Grid
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

internal class GridToVectorSpaceTest {

    @Test
    fun `should convert point in vector space to point in grid`() {
        val grid = Grid(2, 100)
        val gridTo2Space = GridToVectorSpace(grid, -1.0, 1.0, -1.0, 1.0)

        val mappedPoint = gridTo2Space.map(0.0,0.0)
        println(mappedPoint.asList())
        mappedPoint.asList() shouldBeEqualTo  listOf(50, 50)
    }

    @Test
    fun `should convert point in grid to point in vector space`() {
        val grid = Grid(2, 100)
        val gridTo2Space = GridToVectorSpace(grid, -1.0, 1.0, -1.0, 1.0)

        val mappedPoint = gridTo2Space.toVectorSpace(25, 25)

        println(mappedPoint.asList())
        mappedPoint.asList() shouldBeEqualTo  listOf(-0.5, -0.5)
    }

    @Test
    fun `should allow for constructing without a grid`() {
        val gridTo2Space = GridToVectorSpace(100, -1.0, 1.0, -1.0, 1.0)
        val mappedPointInVectorSpace = gridTo2Space.toVectorSpace(25, 25)

        println(mappedPointInVectorSpace.asList())
        mappedPointInVectorSpace.asList() shouldBeEqualTo  listOf(-0.5, -0.5)

        val mappedPoint = gridTo2Space.map(0.0,0.0)
        println(mappedPoint.asList())
        mappedPoint.asList() shouldBeEqualTo  listOf(50, 50)
    }

}