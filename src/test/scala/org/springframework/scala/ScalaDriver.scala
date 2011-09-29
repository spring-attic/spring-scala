/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.scala

import web.client.RestTemplate
import java.net.URI
import org.springframework.http.HttpMethod
import org.springframework.util.FileCopyUtils
import org.springframework.http.client.{ClientHttpResponse, ClientHttpRequest}
import java.io.InputStreamReader

object ScalaDriver {

	def main(args: Array[String]) {
		val template = new RestTemplate()
//		println("1")
//		template.getForAny[String]("http://{host}", "localhost") foreach println
//		println("2")
//		template.getForAny[String]("http://{host}", Map("host" -> "localhost")) foreach println
//		println("3")
//		template.getForAny[String](new URI("http://localhost")) foreach println
//		println("4")
//		val entity = template.getForEntity[String]("http://localhost")
//		entity.headers.asScala foreach println
//		println(entity.body)

		val r = template.exchange[String]("http://localhost", HttpMethod.GET, None)
		println("r = " + r.getBody)

		val t = template.exchange[String]("http://localhost", HttpMethod.GET,  None)
		println("t = " + t.getBody)


		val result = template.execute(new URI("http://localhost"), HttpMethod.GET) {
			request: ClientHttpRequest => Unit
		} {
			response: ClientHttpResponse => FileCopyUtils.copyToString(new InputStreamReader(response.getBody))
		}
		println(result foreach  println )


	}
}