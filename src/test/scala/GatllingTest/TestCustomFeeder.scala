package GatllingTest

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.util.Random

class TestCustomFeeder extends Simulation {

	private val httpProtocol = http
		.baseUrl("https://demoblaze.com")
		.inferHtmlResources()


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
	val rnd = new Random()
	def randomString(length: Int) = {
		rnd.alphanumeric.filter(_.isLetter).take(length).mkString
	}
	val customFeeder = Iterator.continually(Map(
		"username" -> ("usr" + randomString(5)),
		"password" -> ("pa@" + randomString(4))
	))
	def signUp() = {
		repeat(3) {
			feed(customFeeder)
			.exec(
				http("sign up for ${username} & ${password}")
					.post(uri1 + "/signup")
					.headers(headers_33)
					//.body(StringBody("{\"username\":\"${username}\",\"password\":\"${password}\"}"))
					.body(ElFileBody("src/test/scala/GatllingTest/jsonBody/create.json")).asJson
			)
		}
	}


  private val scn = scenario("CSV feeder")
		.exec(signUp())


	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}
