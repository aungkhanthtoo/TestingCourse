package com.plcoding.testingcourse.part8.domain

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.doesNotContain
import com.plcoding.testingcourse.util.MainCoroutineExtension
import com.plcoding.testingcourse.util.scheduledVideoCall
import io.mockk.every
import io.mockk.mockkObject
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDateTime
import kotlin.time.Duration.Companion.minutes


@ExtendWith(MainCoroutineExtension::class)
class VideoCallExpirationFlowTest {

    @Test
    fun `call expiration emission`() = runTest {
        mockkObject(TimeProvider) {
            val now = LocalDateTime.now()
            val inFiveMinutes = now.plusMinutes(5)
            val inTenMinutes = now.plusMinutes(10)

            val scheduledCalls = listOf(
                scheduledVideoCall(inFiveMinutes),
                scheduledVideoCall(inTenMinutes)
            )
            VideoCallExpirationFlow(scheduledCalls).test {
                awaitItem() // ignore initial

                advanceNowBy(6)
                advanceTimeBy(6.minutes)
                val emission1 = awaitItem()
                assertThat(emission1).contains(scheduledCalls[0])
                assertThat(emission1).doesNotContain(scheduledCalls[1])

                advanceNowBy(11)
                advanceTimeBy(11.minutes)
                val emission2 = awaitItem()
                assertThat(emission2).contains(scheduledCalls[0])
                assertThat(emission2).contains(scheduledCalls[1])
            }
        }
    }

    private fun advanceNowBy(minutes: Long) {
        every { TimeProvider.now() } returns LocalDateTime.now().plusMinutes(minutes)
    }
}