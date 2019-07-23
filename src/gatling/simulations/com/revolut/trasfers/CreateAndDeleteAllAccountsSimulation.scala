package com.revolut.trasfers


import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration._


class CreateAndDeleteAllAccountsSimulation extends Simulation {
  private val baseUrl = "http://localhost:4567"
  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl(baseUrl)
    .inferHtmlResources()
    .acceptHeader(HttpHeaderValues.ApplicationJson)
    .contentTypeHeader(HttpHeaderValues.ApplicationJson)
    .userAgentHeader("curl/7.54.0")


  val users = scenario("Users").exec(CreateAccount.create, DeleteAccount.delete)
  setUp(users.inject(rampUsers(100) during (10 seconds)).protocols(httpProtocol))
}

object DeleteAccount {
  private val endpoint = "/api/account/${accountId}"
  val delete = exec(http("retrieve account's data")
    .delete(endpoint)
    .check(status.is(204)))
}