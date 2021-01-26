package com.vandenbreemen.cogthing.application

import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

internal class LocalMinimaSeekerTest {

    @Test
    fun `should calculate next move to lowest direction - right`() {
        val seeker = LocalMinimaSeeker(2, 100)

        seeker.setCurrentLocationInSpace(5, 5)
        seeker.setAdjacentValues(1.0, 0.0, 2.0, 2.0)    //  1 <-- --> 0 along X axis, 2 <-- --> 2 along Y axis

        val next = seeker.nextLocation
        next.asList() shouldBeEqualTo listOf(6, 5)      //  since Lowest cost positive X direction
    }

    @Test
    fun `should calculate next move to lowest direction - left`() {
        val seeker = LocalMinimaSeeker(2, 100)

        seeker.setCurrentLocationInSpace(5, 5)
        seeker.setAdjacentValues(0.5, 1.0, 2.0, 2.0)

        val next = seeker.nextLocation
        next.asList() shouldBeEqualTo listOf(4, 5)      //  since Lowest cost positive X direction
    }

    @Test
    fun `should calculate next move to lowest direction - up`() {
        val seeker = LocalMinimaSeeker(2, 100)

        seeker.setCurrentLocationInSpace(5, 5)
        seeker.setAdjacentValues(2.0, 2.0, 1.0, 0.5)

        val next = seeker.nextLocation
        next.asList() shouldBeEqualTo listOf(5, 6)
    }

    @Test
    fun `should calculate next move to lowest direction - down`() {
        val seeker = LocalMinimaSeeker(2, 100)

        seeker.setCurrentLocationInSpace(5, 5)
        seeker.setAdjacentValues(2.0, 2.0, 0.5, 1.5)

        val next = seeker.nextLocation
        next.asList() shouldBeEqualTo listOf(5, 4)
    }

    @Test
    fun `should work in more dimensions`() {
        val seeker = LocalMinimaSeeker(4, 100)
        seeker.at(2,2,2,2).activation shouldBeEqualTo 0.0

        seeker.setCurrentLocationInSpace(5, 5, 6, 6)
        seeker.setAdjacentValues(2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 1.0)

        val next = seeker.nextLocation
        next.asList() shouldBeEqualTo listOf(5, 5, 6, 7)
    }

    @Test
    fun `should try random diection if all directions have same cost`() {
        val seeker = LocalMinimaSeeker(2, 100)

        seeker.setCurrentLocationInSpace(5, 5)
        seeker.setAdjacentValues(0.0, 0.0, 0.0, 0.0)

        for(i in 1 until 10) {
            println(seeker.nextLocation.asList())
        }
    }

}