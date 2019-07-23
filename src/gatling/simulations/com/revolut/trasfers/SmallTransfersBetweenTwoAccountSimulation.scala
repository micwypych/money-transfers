package com.revolut.trasfers


import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration._
import scala.util.Random


class SmallTransfersBetweenTwoAccountSimulation extends Simulation {
  private val baseUrl = "http://localhost:4567"
  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl(baseUrl)
    .inferHtmlResources()
    .acceptHeader(HttpHeaderValues.ApplicationJson)
    .contentTypeHeader(HttpHeaderValues.ApplicationJson)
    .userAgentHeader("curl/7.54.0")


  val users = scenario("Users").exec(CreateAccount.create, MakeTransfer.transfer)
  setUp(users.inject(rampUsers(100) during (10 seconds)).protocols(httpProtocol))
}

object MakeTransfer {

  val feeder = Iterator.continually(Map("transferToId" -> {
    Random.nextInt(100) + 1
  }))

  private val endpoint = "/api/account/${accountId}/transferTo/${transferToId}"
  private val transferBody = StringBody("{\"currency\":\"PLN\",\"amount\": 0.01}")
  val reaptTransfer = exec(repeat(100) {
    tryMax(10) {
      feed(feeder)
        .exec(http("make transfer")
          .post(endpoint)
          .body(transferBody)
          .check(status.is(200)))
    }
  })

  private val transferToFirstAccount = "/api/account/${accountId}/transferTo/1"
  val transfer = feed(feeder)
    .exec(http("make transfer")
      .post(transferToFirstAccount)
      .body(transferBody)
      .check(status.is(204)))
}