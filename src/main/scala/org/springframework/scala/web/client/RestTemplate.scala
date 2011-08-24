package org.springframework.scala.web.client

import java.net.URI
import scalaj.collection.Imports._
import org.springframework.web.client.{RequestCallback, ResponseExtractor}
import org.springframework.http.HttpMethod
import org.springframework.http.client.{ClientHttpRequest, ClientHttpResponse}

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

  def execute[T](url: URI, method: HttpMethod)
                (requestClosure: ClientHttpRequest => Unit)
                (responseClosure: ClientHttpResponse => T) = {
    javaTemplate.execute(url, method, new RequestCallback {
      def doWithRequest(request: ClientHttpRequest) {

      }
    }, new ResponseExtractor[T] {
      def extractData(response: ClientHttpResponse) = {
        responseClosure(response)
      }
    })
  }

  private def asInstanceOfAnyRef(seq: Seq[Any]) = {
    seq.map(_.asInstanceOf[AnyRef]);
  }


}
