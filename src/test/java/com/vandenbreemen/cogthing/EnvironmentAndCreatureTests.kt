package com.vandenbreemen.cogthing

import com.vandenbreemen.cogthing.api.GridManager
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class EnvironmentAndCreatureTests {

    @Test
    fun `can accept input from a virtual environment`() {
        val environment = Grid(2, 10)
        val environmentManager = GridManager(environment)
        environmentManager.populateRandom()

        val creature = Grid(2, 10)
        val sensorLeft = creature.at(0, 4)
        val sensorRight = creature.at(9, 4)
        val sensorUp = creature.at(4, 9)
        val sensorDown = creature.at(4, 0)

        var position = arrayOf(4,4)

        //  Actual input from environment
        sensorLeft.activation = environment.at(position[0]-1, position[1]).activation
        sensorRight.activation = environment.at(position[0] + 1, position[1]).activation
        sensorUp.activation = environment.at(position[0], position[1]+1).activation
        sensorDown.activation = environment.at(position[0], position[1]-1).activation

        sensorLeft.activation shouldBeEqualTo environment.at(position[0]-1, position[1]).activation
        sensorRight.activation shouldBeEqualTo environment.at(position[0] + 1, position[1]).activation
        sensorUp.activation shouldBeEqualTo environment.at(position[0], position[1]+1).activation
        sensorDown.activation shouldBeEqualTo environment.at(position[0], position[1]-1).activation

    }

    @Test
    fun `can have a brain as well as sensors`() {
        val environment = Grid(2, 10)
        val environmentManager = GridManager(environment)
        environmentManager.populateRandom()

        val creature = Grid(2, 10)
        val sensorLeft = creature.at(0, 4)
        val sensorRight = creature.at(9, 4)
        val sensorUp = creature.at(4, 9)
        val sensorDown = creature.at(4, 0)

        var position = arrayOf(4,4)

        //  Actual input from environment
        sensorLeft.activation = environment.at(position[0]-1, position[1]).activation
        sensorRight.activation = environment.at(position[0] + 1, position[1]).activation
        sensorUp.activation = environment.at(position[0], position[1]+1).activation
        sensorDown.activation = environment.at(position[0], position[1]-1).activation

        //  Trying to get cell beside sensorLeft
        val brain = creature.subGrid(1, 8, 1, 8)
        brain.at(0, 3).activation = 42.0;

        creature.at(1,4).activation shouldBeEqualTo 42.0

        creature.at(1,4).adjacent(0, false).activation shouldBeEqualTo sensorLeft.activation
    }
}