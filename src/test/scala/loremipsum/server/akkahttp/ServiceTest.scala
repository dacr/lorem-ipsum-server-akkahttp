/*
 * Copyright 2020-2022 David Crosson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package loremipsum.server.akkahttp

import com.github.plokhotnyuk.jsoniter_scala.core.readFromString
import loremipsum.server.akkahttp.api.{ContentCodecs, Health, ServiceInfo}
import loremipsum.server.akkahttp.api.ContentCodecs.given
import loremipsum.server.akkahttp.dependencies.loremipsumgenerator.{DefaultLoremIpsumGenerator, LoremIpsumGenerator}
import org.apache.pekko.http.scaladsl.model.{ContentTypes, StatusCodes}
import org.apache.pekko.http.scaladsl.model.headers.`Cache-Control`
import org.apache.pekko.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.matchers.*
import org.scalatest.wordspec.*

class ServiceTest extends AnyWordSpec with should.Matchers with ScalatestRouteTest {

  val routes = ServiceRoutes(ServiceDependencies.defaults).routes

  "Lorem Ipsum Service" should {
    "Respond OK when pinged" in {
      Get("/health") ~> routes ~> check {
        status shouldBe StatusCodes.OK
        readFromString[Health](responseAs[String]) shouldBe Health(true, "alive")
        responseAs[String] should include("\"alive\":true")
      }
    }
    "Be able to return a static asset" in {
      Get("/txt/LICENSE.txt") ~> routes ~> check {
        responseAs[String] should include regex "Apache License"
        header[`Cache-Control`].map(_.value) shouldBe Some("max-age=3600, must-revalidate, proxy-revalidate")
      }
    }
    "Be able to return an embedded webjar asset" in {
      Get("/assets/jquery/jquery.js") ~> routes ~> check {
        responseAs[String] should include regex "jQuery JavaScript Library"
      }
    }
    "Respond a lorem ipsum home page content" in {
      Get() ~> routes ~> check {
        status shouldBe StatusCodes.OK
        contentType shouldBe ContentTypes.`text/html(UTF-8)`
        responseAs[String] should include regex "Lorem ipsum dolor sit amet,"
        header[`Cache-Control`].map(_.value) shouldBe Some("no-store")
      }
    }
    "Not respond unknown paths" in {
      Get("/unknown") ~> routes ~> check {
        handled shouldBe false
      }
    }
  }

  "Lorem Ipsum API" should {
    "Give service information" in {
      Get("/api/info") ~> routes ~> check {
        status shouldBe StatusCodes.OK
        val info = readFromString[ServiceInfo](responseAs[String])
        info.version should not be empty
      }
    }
    "Generate paragraphs" in {
      Get("/api/paragraphs?minWordCount=20&maxWordCount=30") ~> routes ~> check {
        status shouldBe StatusCodes.OK
        contentType shouldBe ContentTypes.`application/json`
        val paragraphs = readFromString[List[String]](responseAs[String])
        paragraphs should not be empty
        paragraphs.head should startWith("Lorem ipsum")
      }
    }
    "Generate text" in {
      Get("/api/text?maxWordCount=10") ~> routes ~> check {
        status shouldBe StatusCodes.OK
        readFromString[String](responseAs[String]) should startWith("Lorem ipsum")
      }
    }
    "Generate text with default word counts" in {
      Get("/api/text") ~> routes ~> check {
        status shouldBe StatusCodes.OK
        readFromString[String](responseAs[String]) should startWith("Lorem ipsum")
      }
    }
    "Reject invalid word counts" in {
      Get("/api/paragraphs?maxWordCount=many") ~> routes ~> check {
        status shouldBe StatusCodes.BadRequest
      }
    }
  }

  "Lorem Ipsum API documentation" should {
    "Be available as an OpenAPI yaml specification" in {
      Get("/swagger/docs.yaml") ~> routes ~> check {
        status shouldBe StatusCodes.OK
        val spec = responseAs[String]
        spec should include("openapi:")
        spec should include("/api/paragraphs:")
        spec should include("/api/text:")
        spec should include("/api/info:")
        spec should include("minWordCount")
      }
    }
    "Be available as an OpenAPI json specification" in {
      Get("/swagger/swagger.json") ~> routes ~> check {
        status shouldBe StatusCodes.OK
        contentType.mediaType.toString shouldBe "application/json"
        responseAs[String] should include("\"/api/paragraphs\"")
      }
    }
    "Provide a swagger user interface" in {
      Get("/swagger/") ~> routes ~> check {
        status shouldBe StatusCodes.OK
        responseAs[String] should include regex "(?i)swagger"
      }
    }
  }

  "Lorem Ipsum Service with a site prefix" should {
    val defaultConfig  = ServiceDependencies.defaultConfig
    val prefixedConfig = defaultConfig.copy(loremIpsum = defaultConfig.loremIpsum.copy(site = defaultConfig.loremIpsum.site.copy(prefix = Some("lorem"))))
    val prefixedRoutes = ServiceRoutes(new ServiceDependencies {
      override val config: ServiceConfig      = prefixedConfig
      override val lorem: LoremIpsumGenerator = DefaultLoremIpsumGenerator(prefixedConfig)
    }).routes

    "Serve the home page and the API under the prefix" in {
      Get("/lorem/") ~> prefixedRoutes ~> check {
        status shouldBe StatusCodes.OK
        responseAs[String] should include regex "Lorem ipsum dolor sit amet,"
      }
      Get("/lorem/api/text") ~> prefixedRoutes ~> check {
        status shouldBe StatusCodes.OK
      }
      Get("/lorem/swagger/docs.yaml") ~> prefixedRoutes ~> check {
        status shouldBe StatusCodes.OK
        responseAs[String] should include("/lorem")
      }
    }
    "Not serve anything outside the prefix" in {
      Get("/api/text") ~> prefixedRoutes ~> check {
        handled shouldBe false
      }
    }
  }
}
