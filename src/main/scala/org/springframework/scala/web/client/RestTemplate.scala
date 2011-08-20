package org.springframework.scala.web.client

import org.springframework.http.client.ClientHttpRequestFactory
import org.springframework.http.HttpMethod
import java.net.URI

class RestTemplate(val javaTemplate: org.springframework.web.client.RestTemplate) {

  def this(requestFactory: ClientHttpRequestFactory) {
    this (new org.springframework.web.client.RestTemplate(requestFactory))
  }

  def this() {
    this (new org.springframework.web.client.RestTemplate())
  }

  // DELETE
  def delete(url: String, urlVariables: AnyRef*) {
    javaTemplate.delete(url, urlVariables)
  }

  def delete(url: String, urlVariables: Map[String, _]) {
    javaTemplate.delete(url, urlVariables)
  }

  def delete(url: URI) {
    javaTemplate.delete(url)
  }

}