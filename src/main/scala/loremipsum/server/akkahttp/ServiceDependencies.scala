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

import loremipsum.server.akkahttp.dependencies.loremipsumgenerator.{DefaultLoremIpsumGenerator, LoremIpsumGenerator}

trait ServiceDependencies {
  val config:ServiceConfig
  val lorem:LoremIpsumGenerator
}

object ServiceDependencies {
  val defaultConfig = ServiceConfig()
  def defaults:ServiceDependencies = new ServiceDependencies {
    override val config: ServiceConfig = defaultConfig
    override val lorem: LoremIpsumGenerator = DefaultLoremIpsumGenerator(defaultConfig)
  }
}