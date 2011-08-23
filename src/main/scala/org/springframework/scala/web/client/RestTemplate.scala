package org.springframework.scala.web.client

import org.springframework.http.client.ClientHttpRequestFactory
import java.net.URI

class RestTemplate private (val javaTemplate: org.springframework.web.client.RestTemplate) {

  // DELETE
  def delete(url: String, urlVariables: AnyRef*) {
    javaTemplate.delete(url, urlVariables: _*)
  }

  def delete(url: String, urlVariables: Map[String, _]) {
    javaTemplate.delete(url, urlVariables)
  }

  def delete(url: URI) {
    javaTemplate.delete(url)
  }

}

object RestTemplate {

  def apply() = new RestTemplate(new org.springframework.web.client.RestTemplate())

  def apply(requestFactory: ClientHttpRequestFactory) =
    new org.springframework.web.client.RestTemplate(requestFactory)

  def apply(javaTemplate: org.springframework.web.client.RestTemplate) =
    new RestTemplate(javaTemplate)

}