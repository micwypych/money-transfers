package com.revolut.trasfers


import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration._


class CreateAndRetriveAccountDataSimulation extends Simulation {
  private val baseUrl = "http://localhost:4567"
  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl(baseUrl)
    .inferHtmlResources()
    .acceptHeader(HttpHeaderValues.ApplicationJson)
    .contentTypeHeader(HttpHeaderValues.ApplicationJson)
    .userAgentHeader("curl/7.54.0")


  val users = scenario("Users").exec(CreateAccount.create, RetrieveAccountData.retrieve)
  setUp(users.inject(rampUsers(100) during (10 seconds)).protocols(httpProtocol))
}

object RetrieveAccountData {
  private val endpoint = "/api/account/${accountId}"
  val createPlnAccountBody = StringBody("{\"currency\":\"PLN\"}")
  val retrieve = exec(http("retrieve account's data")
    .get(endpoint)
    .body(createPlnAccountBody)
    .check(status.is(200)))
}