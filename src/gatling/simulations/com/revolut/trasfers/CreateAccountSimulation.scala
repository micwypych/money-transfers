package com.revolut.trasfers


import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder


class CreateAccountSimulation extends Simulation {
  private val baseUrl = "http://localhost:4567"
  private val contentType = "application/json"
  private val endpoint = "/api/account"
  private val requestCount = 100
  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl(baseUrl)
    .inferHtmlResources()
    .acceptHeader("*/*")
    .contentTypeHeader(contentType)
    .userAgentHeader("curl/7.54.0")
  val headers_0 = Map("Expect" -> "100-continue")
  val body_0 = StringBody("{\"currency\":\"PLN\"}")
  val scn: ScenarioBuilder = scenario("RecordedSimulation")
    .exec(http("create an account")
      .post(endpoint)
      .headers(headers_0)
      .body(body_0)
      .check(status.is(201)))

  val users = scenario(s"Users").exec(CreateAccount.create)
  setUp(
    users.inject(atOnceUsers(requestCount)),
  ).protocols(httpProtocol)
}

object CreateAccount {

  private val endpoint = "/api/account"
  val createPlnAccountBody = StringBody("{\"currency\":\"PLN\",\"initialBalance\":1000.00}")
  val create = exec(http("create an account")
    .post(endpoint)
    .body(createPlnAccountBody)
    .check(status.is(201))
    .check(jsonPath("$..id").saveAs("accountId")))
    .pause(3)
}