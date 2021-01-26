package com.vandenbreemen.cogthing.application

import org.amshove.kluent.shouldNotBeEqualTo
import org.junit.jupiter.api.Test

internal class ModifiedLocalMinimaSeekerTest {

    @Test
    fun `should be penalized for visiting same location again`() {

        val seeker = ModifiedLocalMinimaSeeker(2, 10)
        seeker.setCostPurturbationFactor(0.0)
        seeker.setCurrentLocationInSpace(4,4)

        seeker.setAdjacentValues(1.0, 1.0, 1.0, 0.95)

        println(seeker.nextLocation.asList())

        //  Location behind us on y-axis is less expensive
        seeker.setAdjacentValues(1.0, 1.0, 0.95, 1.0)

        println(seeker.nextLocation.asList())

        seeker.setAdjacentValues(1.0, 1.0, 1.0, 0.95)
        val next = seeker.nextLocation
        next.asList() shouldNotBeEqualTo listOf(4,4)
        println(next.asList())

    }
}