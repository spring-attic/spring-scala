package org.springframework.scala.web.client

import java.net.URI
import scalaj.collection.Imports._

class RestTemplate (val javaTemplate: org.springframework.web.client.RestOperations) {

  def this() {
    this(new org.springframework.web.client.RestTemplate())
  }
  
  // DELETE

  def delete(url: String, urlVariables: Any*) {
    javaTemplate.delete(url, asInstanceOfAnyRef(urlVariables): _*)
  }

  def delete[T](url: String, urlVariables: Map[String, T]) {
    val javaMap = urlVariables.asJava[String, T]
    javaTemplate.delete(url, javaMap)
  }

  def delete(url: URI) {
    javaTemplate.delete(url)
  }

  private def asInstanceOfAnyRef(seq: Seq[Any]) = {
    seq.map(_.asInstanceOf[AnyRef]);
  }


}
