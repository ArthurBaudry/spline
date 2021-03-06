/*
 * Copyright 2017 Barclays Africa Group Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package za.co.absa.spline.web

import java.util.Arrays.asList
import java.{util => ju}

import org.apache.commons.configuration.{CompositeConfiguration, EnvironmentConfiguration, SystemConfiguration}
import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.web.method.support.HandlerMethodReturnValueHandler
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import za.co.absa.spline.persistence.api.DataLineageReader
import za.co.absa.spline.persistence.mongo.{MongoConnection, MongoDataLineageReader}
import za.co.absa.spline.web.handler.ScalaFutureMethodReturnValueHandler
import za.co.absa.spline.web.rest.service.LineageService

@Configuration
class LineageWebAppConfig extends WebMvcConfigurerAdapter with ExecutionContextImplicit {

  import za.co.absa.spline.common.ConfigurationImplicits._

  private val confProps = new CompositeConfiguration(asList(
    new SystemConfiguration,
    new EnvironmentConfiguration
  ))

  override def addReturnValueHandlers(returnValueHandlers: ju.List[HandlerMethodReturnValueHandler]): Unit = {
    returnValueHandlers.add(new ScalaFutureMethodReturnValueHandler)
  }

  @Bean def lineageReader: DataLineageReader =
    new MongoDataLineageReader(new MongoConnection(
      dbUrl = confProps getRequiredString "spline.mongodb.url",
      dbName = confProps getRequiredString "spline.mongodb.name"))

  @Bean def lineageService(reader: DataLineageReader): LineageService =
    new LineageService(reader)
}
