package GatllingTest

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class TestCSVFeeder extends Simulation {

	private val httpProtocol = http
		.baseUrl("https://demoblaze.com")
		.inferHtmlResources()

	// ch
	def USERCOUNT = System.getProperty("USERS", "10").toInt

	def RAMPDURATION = System.getProperty("RAMP_DURATION", "10").toInt

	def TESTDURATION: Int = System.getProperty("TEST_DURATION", "60").toInt


	private val headers_33 = Map(
		"accept" -> "*/*",
		"accept-encoding" -> "gzip, deflate, br",
		"accept-language" -> "en-GB,en-US;q=0.9,en;q=0.8",
		"content-type" -> "application/json",
		"origin" -> "https://demoblaze.com",
		"sec-ch-ua" -> """Chromium";v="104", " Not A;Brand";v="99", "Google Chrome";v="104""",
		"sec-ch-ua-mobile" -> "?0",
		"sec-ch-ua-platform" -> "macOS",
		"sec-fetch-dest" -> "empty",
		"sec-fetch-mode" -> "cors",
		"sec-fetch-site" -> "same-site",
		"user-agent" -> "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36"
	)


	private val uri1 = "https://api.demoblaze.com"
	val csvFeeder = csv("src/test/scala/data/data.csv").circular

	def getSpecifcPrduct() = {
		repeat(4) {
			feed(csvFeeder)
				.exec(
					http("product view of id : ${id} and product title match ${Product}")
						.post(uri1 + "/view")
						.headers(headers_33)
						.body(StringBody("{\"id\":\"${id}\"}"))
						.check(bodyString.saveAs("responseBody"))
						.check(jsonPath("$.title").is("${Product}"))
				)
				.exec { session => println(session("responseBody").as[String]); session }
		}
	}

	private val scn = scenario("CSV feeder")
		.forever {
			exec(getSpecifcPrduct())
				.pause(2)

		}

	setUp(scn.
		inject(
			nothingFor(5),
			atOnceUsers(5),
			rampUsers(10).during(10))
		.protocols(httpProtocol)).maxDuration(60)

	after {
		println("Test completed")
	}
}
