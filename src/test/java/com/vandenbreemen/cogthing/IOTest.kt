package com.vandenbreemen.cogthing

import com.vandenbreemen.cogthing.api.GridAPI
import com.vandenbreemen.cogthing.api.Position
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class IOTest {

    @Test
    fun `should accept input to the system`() {
        val grid = Grid(3, 10)
        val gridApi = GridAPI.getDefault(grid)

        gridApi.input(
                Position(2,2,2),
                3.14
        )

        gridApi.input(
                Position(9,3,1),
                9.44
        )

        assertEquals(3.14, grid.at(2,2,2).activation)
        assertEquals(9.44, grid.at(9, 3, 1).activation)
    }

}