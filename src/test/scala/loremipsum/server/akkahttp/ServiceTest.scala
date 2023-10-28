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

import loremipsum.server.akkahttp.ServiceDependencies
import loremipsum.server.akkahttp.routing.{AdminRouting, Health}
import org.scalatest.wordspec._
import org.scalatest.matchers._
import org.apache.pekko.http.scaladsl.model.StatusCodes
import org.apache.pekko.http.scaladsl.testkit.ScalatestRouteTest
import org.apache.pekko.http.scaladsl.server._
import Directives._
import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.ext.{JavaTimeSerializers, JavaTypesSerializers}

class ServiceTest extends AnyWordSpec with should.Matchers with ScalatestRouteTest {
  implicit val chosenSerialization: Serialization.type = Serialization
  implicit val chosenFormats: Formats                  = DefaultFormats.lossless ++ JavaTimeSerializers.all ++ JavaTypesSerializers.all

  val routes = ServiceRoutes(ServiceDependencies.defaults).routes

  "Lorem Ipsum Service" should {
    "Respond OK when pinged" in {
      Get("/health") ~> routes ~> check {
        import com.github.pjfanning.pekkohttpjson4s.Json4sSupport._
        responseAs[Health] shouldBe Health(true, "alive")
      }
    }
    "Be able to return a static asset" in {
      Get("/txt/LICENSE.txt") ~> routes ~> check {
        responseAs[String] should include regex "Apache License"
      }
    }
    "Be able to return an embedded webjar asset" in {
      Get("/assets/jquery/jquery.js") ~> routes ~> check {
        responseAs[String] should include regex "jQuery JavaScript Library"
      }
    }
    "Respond a lorem ipsum home page content" in {
      info("The first content page can be slow because of templates runtime compilation")
      Get() ~> routes ~> check {
        responseAs[String] should include regex "Lorem ipsum dolor sit amet,"
      }
    }
  }
}
