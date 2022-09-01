package GatllingTest

import io.gatling.core.Predef.{rampUsers, _}
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

class TestUserFlow extends Simulation {

	private val httpProtocol = http
		.baseUrl("https://demoblaze.com")
		.inferHtmlResources()

	private val headers_0 = Map(
		"accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
		"accept-encoding" -> "gzip, deflate, br",
		"accept-language" -> "en-GB,en-US;q=0.9,en;q=0.8",
		"sec-ch-ua" -> """Chromium";v="104", " Not A;Brand";v="99", "Google Chrome";v="104""",
		"sec-ch-ua-mobile" -> "?0",
		"sec-ch-ua-platform" -> "macOS",
		"sec-fetch-dest" -> "document",
		"sec-fetch-mode" -> "navigate",
		"sec-fetch-site" -> "none",
		"sec-fetch-user" -> "?1",
		"upgrade-insecure-requests" -> "1",
		"user-agent" -> "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36"
	)


	private val headers_32 = Map(
		"accept" -> "*/*",
		"accept-encoding" -> "gzip, deflate, br",
		"accept-language" -> "en-GB,en-US;q=0.9,en;q=0.8",
		"access-control-request-headers" -> "content-type",
		"access-control-request-method" -> "POST",
		"origin" -> "https://demoblaze.com",
		"sec-fetch-dest" -> "empty",
		"sec-fetch-mode" -> "cors",
		"sec-fetch-site" -> "same-site",
		"user-agent" -> "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36"
	)

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

	private val headers_34 = Map(
		"accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
		"accept-encoding" -> "gzip, deflate, br",
		"accept-language" -> "en-GB,en-US;q=0.9,en;q=0.8",
		"sec-ch-ua" -> """Chromium";v="104", " Not A;Brand";v="99", "Google Chrome";v="104""",
		"sec-ch-ua-mobile" -> "?0",
		"sec-ch-ua-platform" -> "macOS",
		"sec-fetch-dest" -> "document",
		"sec-fetch-mode" -> "navigate",
		"sec-fetch-site" -> "same-origin",
		"sec-fetch-user" -> "?1",
		"upgrade-insecure-requests" -> "1",
		"user-agent" -> "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36"
	)

	private val uri1 = "https://api.demoblaze.com"

	private val scn = scenario("My first Gatling test")
    .exec(
      http("home page load")
        .get("/")
        .headers(headers_0)
    )
    .pause(2)
		.exec(
			http("list by category")
				.options(uri1 + "/bycat")
				.headers(headers_32)
				.resources(
					http("category list")
						.post(uri1 + "/bycat")
						.headers(headers_33)
						.body(RawFileBody("src/test/scala/GatllingTest/jsonBody/bycat.json")))
				.check(bodyString.saveAs("responseBody1"))
		)
		.exec { session => println(session("responseBody1").as[String]); session }
    .pause(2)
    .exec(
      http("product view")
            .post(uri1 + "/view")
            .headers(headers_33)
            .body(RawFileBody("src/test/scala/GatllingTest/jsonBody/id.json"))
				.check(bodyString.saveAs("responseBody"))
				.check(status.in(200 to 210))
		.check(jsonPath("$.title").is("Samsung galaxy s6")))

		.exec { session => println(session("responseBody").as[String]); session }

		.exec { session => println(session); session }

	setUp(scn.inject(
		nothingFor(5),
		rampUsers(2).during(RAMPDURATION))
		.protocols(httpProtocol))
}
