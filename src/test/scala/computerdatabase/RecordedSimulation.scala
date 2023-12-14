package computerdatabase

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.features.FileLogger


class RecordedSimulation extends Simulation {

  val fileLogger = new FileLogger()

  private val httpProtocol = http
    .baseUrl("http://computer-database.gatling.io")
    .inferHtmlResources(AllowList(), DenyList(""".*\.js""", """.*\.css""", """.*\.gif""", """.*\.jpeg""", """.*\.jpg""", """.*\.ico""", """.*\.woff""", """.*\.woff2""", """.*\.(t|o)tf""", """.*\.png""", """.*\.svg""", """.*detectportal\.firefox\.com.*"""))
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .upgradeInsecureRequestsHeader("1")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:120.0) Gecko/20100101 Firefox/120.0")
  


  private val scn = scenario("RecordedSimulation")
    .exec(session => session.set("currentRequest", "Request 0"))
    .exec(
      http("request_0")
        .get("/computers")
        .check(status.saveAs("status"))
    )
    .pause(5)
    .exec { session =>
      fileLogger.logErrorToFile(session)
      session
    }
    // select_pc
    .exec(session => session.set("currentRequest", "Request 1"))
    .exec(
      http("request_1")
        .get("/computers/381asd")
        .check(status.saveAs("status"))
    )
    .pause(5)
    .exec { session =>
      fileLogger.logErrorToFile(session)
      session
    }
    // back_home
    .exec(session => session.set("currentRequest", "Request 1"))
    .exec(
      http("request_2")
        .get("/computerswazaaap")
        .check(status.saveAs("status"))
    )
    .exec { session =>
      fileLogger.logErrorToFile(session)
      session
    }
    .pause(5)

	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}
