package com.baha.sushigarden.data.services.delivery

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CourierSimulatorTest {

    @Test
    fun initialState_isFirstRoutePointWithZeroProgress() = runTest {
        val sim = CourierSimulator(StandardTestDispatcher(testScheduler))
        assertEquals(0f, sim.progress.value)
        assertEquals(30, sim.etaMinutes.value)
    }

    @Test
    fun start_guardAgainstDuplicateJobs_doesNotResetProgress() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val sim = CourierSimulator(dispatcher)
        sim.start()
        advanceTimeBy(3_001)
        val progressAfterOneStep = sim.progress.value
        sim.start()  // should be a no-op: job already active
        assertEquals(progressAfterOneStep, sim.progress.value)
    }

    @Test
    fun stop_cancelsSimulation() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val sim = CourierSimulator(dispatcher)
        sim.start()
        sim.stop()
        advanceTimeBy(10_000)
        assertEquals(0f, sim.progress.value)
    }

    @Test
    fun start_advancesPositionAfterDelay() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val sim = CourierSimulator(dispatcher)
        val initialPosition = sim.position.value
        sim.start()
        advanceTimeBy(3_001)
        assertNotEquals(initialPosition, sim.position.value)
        assertNotEquals(0f, sim.progress.value)
    }

    @Test
    fun start_etaDecreasesAsProgressIncreases() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val sim = CourierSimulator(dispatcher)
        sim.start()
        advanceTimeBy(3_001)
        assert(sim.etaMinutes.value < 30) {
            "ETA should decrease after first step, was ${sim.etaMinutes.value}"
        }
    }
}
