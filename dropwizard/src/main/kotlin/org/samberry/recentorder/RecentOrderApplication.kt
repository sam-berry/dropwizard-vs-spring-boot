package org.samberry.recentorder

import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.dropwizard.Application
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment

class RecentOrderApplication : Application<RecentOrderConfiguration>() {
    fun main(args: Array<String>) {
        println("BEFORE RUN")
        RecentOrderApplication().run(*args)
        println("AFTER RUN")
    }

    override fun getName(): String {
        return "hello-world"
    }

    override fun initialize(bootstrap: Bootstrap<RecentOrderConfiguration>) {
        println("IN INIT METHOD")
        bootstrap.objectMapper.registerKotlinModule()
    }

    override fun run(
        configuration: RecentOrderConfiguration,
        environment: Environment
    ) {
        println("IN RUN METHOD")

        environment.jersey().register(OrderResource(OrderService(OrderTimeline())))
    }
}
