package org.samberry.recentorder

import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.dropwizard.jackson.Jackson
import io.dropwizard.testing.junit.ResourceTestRule
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.ws.rs.client.Entity

class APILoadTest {
    private val numberOfThreads = System.getProperty("threads")?.replace("_", "")?.toInt()
        ?: throw RuntimeException("'threads' argument must be provided")

    private val numberOfOrders = System.getProperty("requests")?.replace("_", "")?.toInt()
        ?: throw RuntimeException("'requests' argument must be provided")

    private lateinit var amounts: List<Double>
    private lateinit var executorService: ExecutorService

    @Before
    fun setUp() {
        amounts = listOf(22.31, 22.11, 10.1, 0.02, 0.03, 155.2, 7.73)
        executorService = Executors.newFixedThreadPool(numberOfThreads)
    }

    @After
    fun tearDown() {
        resources.target("/orders").request().delete()
    }

    private fun addAmounts(left: Double, right: Double): Double {
        return BigDecimal(left).plus(BigDecimal(right))
            .setScale(2, RoundingMode.HALF_UP)
            .toDouble()
    }

    @Test(timeout = (ORDER_DURATION_SECONDS + 3) * 1000L)
    fun `can process the maximum number of orders fast enough concurrently`() {
        val workers = (1..numberOfOrders)
            .chunked(numberOfOrders / numberOfThreads)
            .map { jobsForWorker ->
                Callable {
                    Thread.currentThread().id to jobsForWorker
                        .map { amounts[it % amounts.size] }
                        .map {
                            resources.target("/orders")
                                .request()
                                .post(Entity.json(Order(OrderAmount(it), OrderTimestamp.now())))
                            it
                        }
                        .reduce { total, amount -> addAmounts(total, amount) }
                }
            }

        val results = executorService.invokeAll(workers)
            .map { it.get() }

        val actualNumberOfThreads = results.map { it.first }.toHashSet().size
        if (actualNumberOfThreads != numberOfThreads)
            throw RuntimeException("$actualNumberOfThreads threads used when $numberOfThreads was desired")

        val totalAmount = results
            .map { it.second }
            .reduce { total, amount -> addAmounts(total, amount) }


        val stats = resources.target("/statistics").request().get(OrderStatistics::class.java)

        assertThat(stats.sum).isEqualTo(OrderAmount(totalAmount))
    }

    companion object {
        private val objectMapper = Jackson.newObjectMapper()
            .registerModule(KotlinModule())

        @ClassRule
        @JvmField
        val resources = ResourceTestRule.builder()
            .setMapper(objectMapper)
            .addResource(OrderResource(OrderService(OrderTimeline())))
            .build()!!
    }
}