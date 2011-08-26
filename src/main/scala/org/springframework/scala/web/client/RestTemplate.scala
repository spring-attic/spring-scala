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

package org.springframework.scala.web.client

import java.net.URI
import scalaj.collection.Imports._
import org.springframework.web.client.{RequestCallback, ResponseExtractor}
import org.springframework.http.HttpMethod
import org.springframework.http.client.ClientHttpResponse

class RestTemplate(val javaTemplate: org.springframework.web.client.RestOperations) {

  def this() {
    this (new org.springframework.web.client.RestTemplate())
  }

  // DELETE
  def delete(url: String, urlVariables: Any*) {
    javaTemplate.delete(url, asInstanceOfAnyRef(urlVariables): _*)
  }

  def delete[T](url: String, urlVariables: Map[String, T]) {
    javaTemplate.delete(url, urlVariables.asJava[String, T])
  }

  def delete(url: URI) {
    javaTemplate.delete(url)
  }

  def execute[T](url: URI, method: HttpMethod, requestCallback: RequestCallback)
                (responseClosure: ClientHttpResponse => T) = {
    javaTemplate.execute(url, method, requestCallback, new ResponseExtractor[T] {
      def extractData(response: ClientHttpResponse) = {
        responseClosure(response)
      }
    })
  }

  private def asInstanceOfAnyRef(seq: Seq[Any]) = {
    seq.map(_.asInstanceOf[AnyRef]);
  }


}
