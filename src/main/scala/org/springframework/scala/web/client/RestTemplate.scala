/*
 * Copyright 2011-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.scala.web.client

import com.fasterxml.jackson.module.scala.DefaultScalaModule
import java.net.URI
import org.springframework.http.client.{ClientHttpRequest, ClientHttpRequestFactory, ClientHttpResponse}
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.http.{HttpHeaders, HttpEntity, ResponseEntity, HttpMethod}
import org.springframework.scala.util.TypeTagUtils.typeToClass
import org.springframework.util.ClassUtils
import org.springframework.web.client.{RequestCallback, ResponseExtractor}
import scala.collection.JavaConverters._
import scala.collection.{Map, Set}
import scala.reflect.ClassTag

/**
 * Scala-based convenience wrapper for the Spring [[org.springframework.web.client.RestTemplate]], taking
 * advantage of functions and Scala types.
 *
 * @author Arjen Poutsma
 * @since 1.0
 * @constructor Creates a `RestTemplate` that wraps the given Java template, defaulting to the standard `RestTemplate`
 * @param javaTemplate the Java `RestTemplate` to wrap
 */
class RestTemplate(val javaTemplate: org.springframework.web.client.RestOperations =
                   new org.springframework.web.client.RestTemplate) {

	if (RestTemplate.jackson2Present && RestTemplate.jacksonScalaModulePresent) {
		val messageConverters = javaTemplate
				.asInstanceOf[org.springframework.web.client.RestTemplate].getMessageConverters.asScala
	    val converterOption = messageConverters.collectFirst { case c:MappingJackson2HttpMessageConverter => c }
	    converterOption.foreach(_.getObjectMapper.registerModule(DefaultScalaModule))
	}


  /**
   * Create a new instance of the `RestTemplate` given the ClientHttpRequestFactory to obtain requests from
   *
   * @param requestFactory HTTP request factory to use
   */
  def this(requestFactory: ClientHttpRequestFactory) {
    this(new org.springframework.web.client.RestTemplate(requestFactory))
  }

  // GET
  /**
   * Retrieve a representation by doing a GET on the specified URL.
   * The response (if any) is converted and returned.
   *
   * URI Template variables are expanded using the given URI variables, if any.
   *
   * @param url the URL
   * @param uriVariables the variables to expand the template
   * @return the converted object
   */
  def getForAny[T: ClassTag](url: String, uriVariables: Any*): Option[T] = {
    Option(javaTemplate.getForObject(url, typeToClass[T], asInstanceOfAnyRef(uriVariables): _*))
  }

  /**
   * Retrieve a representation by doing a GET on the URI template.
   * The response (if any) is converted and returned.
   *
   * URI Template variables are expanded using the given map.
   *
   * @param url the URL
   * @param uriVariables the map containing variables for the URI template
   * @return the converted object
   */
  def getForAny[T: ClassTag](url: String, uriVariables: Map[String, _]): Option[T] = {
    Option(javaTemplate.getForObject(url, typeToClass[T], uriVariables.asJava))
  }

  /**
   * Retrieve a representation by doing a GET on the URL.
   * The response (if any) is converted and returned.
   *
   * @param url the URL
   * @return the converted object
   */
  def getForAny[T: ClassTag](url: URI): Option[T] = {
    Option(javaTemplate.getForObject(url, typeToClass[T]))
  }

  /**
   * Retrieve an entity by doing a GET on the specified URL.
   * The response is converted and stored in an `ResponseEntity`.
   *
   * URI Template variables are expanded using the given URI variables, if any.
   * @param url the URL
   * @param uriVariables the variables to expand the template
   * @return the entity
   */
  def getForEntity[T: ClassTag](url: String, uriVariables: Any*): ResponseEntity[T] = {
    javaTemplate.getForEntity(url, typeToClass[T], asInstanceOfAnyRef(uriVariables): _*)
  }

  /**
   * Retrieve a representation by doing a GET on the URL.
   * The response is converted and stored in an [[org.springframework.http.ResponseEntity]].
   *
   * URI Template variables are expanded using the given map.
   * @param url the URL
   * @param uriVariables the map containing variables for the URI template
   * @return the converted object
   */
  def getForEntity[T: ClassTag](url: String, uriVariables: Map[String, _]): ResponseEntity[T] = {
    javaTemplate.getForEntity(url, typeToClass[T], uriVariables.asJava)
  }

  /**
   * Retrieve a representation by doing a GET on the URL .
   * The response is converted and stored in an [[org.springframework.http.ResponseEntity]]}.
   * @param url the URL
   * @return the converted object
   */
  def getForEntity[T: ClassTag](url: URI): ResponseEntity[T] = {
    javaTemplate.getForEntity(url, typeToClass[T])
  }

  // HEAD
  /**
   * Retrieve all headers of the resource specified by the URI template.
   *
   * URI Template variables are expanded using the given URI variables, if any.
   *
   * @param url the URL
   * @param uriVariables the variables to expand the template
   * @return all HTTP headers of that resource
   */
  def headForHeaders(url: String, uriVariables: Any*): HttpHeaders = {
    javaTemplate.headForHeaders(url, asInstanceOfAnyRef(uriVariables): _*)
  }

  /**
   * Retrieve all headers of the resource specified by the URI template.
   *
   * URI Template variables are expanded using the given map.
   *
   * @param url the URL
   * @param uriVariables the map containing variables for the URI template
   * @return all HTTP headers of that resource
   */
  def headForHeaders(url: String, uriVariables: Map[String, _]): HttpHeaders = {
    javaTemplate.headForHeaders(url, uriVariables.asJava)
  }

  /**
   * Retrieve all headers of the resource specified by the URL.
   * @param url the URL
   * @return all HTTP headers of that resource
   */
  def headForHeaders(url: URI): HttpHeaders = {
    javaTemplate.headForHeaders(url)
  }

  // POST
  /**
   * Create a new resource by POSTing the given object to the URI template, and returns the value of the `Location`
   * header.
   * This header typically indicates where the new resource is stored.
   *
   * URI Template variables are expanded using the given URI variables, if any.
   *
   * The `request` parameter can be a [[org.springframework.http.HttpEntity]] in order to add additional HTTP headers
   * to the request.
   *
   * @param url the URL
   * @param request the Object to be POSTed
   * @param uriVariables the variables to expand the template
   * @return the value for the `Location` header
   * @see HttpEntity
   */
  def postForLocation(url: String, request: Option[Any], uriVariables: Any*): URI = {
    javaTemplate.postForLocation(url, request.orNull, asInstanceOfAnyRef(uriVariables): _*)
  }

  /**
   * Create a new resource by POSTing the given object to the URI template, and returns the value of the `Location`
   * header.
   * This header typically indicates where the new resource is stored.
   *
   * URI Template variables are expanded using the given map.
   *
   * The `request` parameter can be a [[org.springframework.http.HttpEntity]] in order to add additional HTTP headers
   * to the request.
   *
   * @param url the URL
   * @param request the Object to be POSTed
   * @param uriVariables the variables to expand the template
   * @return the value for the `Location` header
   * @see HttpEntity
   */
  def postForLocation(url: String, request: Option[Any], uriVariables: Map[String, _]): URI = {
    javaTemplate.postForLocation(url, request.orNull, uriVariables.asJava)
  }

  /**
   * Create a new resource by POSTing the given object to the URL, and returns the value of the `Location` header.
   * This header typically indicates where the new resource is stored.
   *
   * The `request` parameter can be a [[org.springframework.http.HttpEntity]] in order to add additional HTTP headers
   * to the request.
   *
   * @param url the URL
   * @param request the Object to be POSTed
   * @return the value for the `Location` header
   * @see HttpEntity
   */
  def postForLocation(url: URI, request: Option[Any]): URI = {
    javaTemplate.postForLocation(url, request.orNull)
  }

  /**
   * Create a new resource by POSTing the given object to the URI template, and returns the representation found in
   * the response.
   *
   * URI Template variables are expanded using the given URI variables, if any.
   *
   * The `request` parameter can be a [[org.springframework.http.HttpEntity]] in order to add additional HTTP headers
   * to the request.
   *
   * @param url the URL
   * @param request the Object to be POSTed
   * @param uriVariables the variables to expand the template
   * @return the converted object
   * @see HttpEntity
   */
  def postForObject[T: ClassTag](url: String, request: Option[Any], uriVariables: Any*): Option[T] = {
    Option(javaTemplate.postForObject(url, request.orNull, typeToClass[T], asInstanceOfAnyRef(uriVariables)))
  }

  /**
   * Create a new resource by POSTing the given object to the URI template, and returns the representation found in
   * the response.
   *
   * URI Template variables are expanded using the given map.
   *
   * The `request` parameter can be a [[org.springframework.http.HttpEntity]] in order to add additional HTTP headers
   * to the request.
   *
   * @param url the URL
   * @param request the Object to be POSTed, may be `null`
   * @param uriVariables the variables to expand the template
   * @return the converted object
   * @see HttpEntity
   */
  def postForObject[T: ClassTag](url: String, request: Option[Any], uriVariables: Map[String, _]): Option[T] = {
    Option(javaTemplate.postForObject(url, request.orNull, typeToClass[T], uriVariables.asJava))
  }

  /**
   * Create a new resource by POSTing the given object to the URL, and returns the representation found in the
   * response.
   *
   * The `request` parameter can be a [[org.springframework.http.HttpEntity]] in order to add additional HTTP headers
   * to the request.
   *
   * @param url the URL
   * @param request the Object to be POSTed
   * @return the converted object
   * @see HttpEntity
   */
  def postForObject[T: ClassTag](url: URI, request: Option[Any]): Option[T] = {
    Option(javaTemplate.postForObject(url, request.orNull, typeToClass[T]))
  }

  /**
   * Create a new resource by POSTing the given object to the URI template, and returns the response as
   * [[org.springframework.http.ResponseEntity]].
   *
   * URI Template variables are expanded using the given URI variables, if any.
   *
   * The `request` parameter can be a [[org.springframework.http.HttpEntity]] in order to add additional HTTP headers
   * to the request.
   *
   * @param url the URL
   * @param request the Object to be POSTed
   * @param uriVariables the variables to expand the template
   * @return the converted object
   * @see HttpEntity
   */
  def postForEntity[T: ClassTag](url: String, request: Option[Any], uriVariables: Any*): ResponseEntity[T] = {
    javaTemplate.postForEntity(url, request.orNull, typeToClass[T], asInstanceOfAnyRef(uriVariables))
  }

  /**
   * Create a new resource by POSTing the given object to the URI template, and returns the response as
   * [[org.springframework.http.HttpEntity]].
   *
   * URI Template variables are expanded using the given map.
   *
   * The `request` parameter can be a [[org.springframework.http.HttpEntity]] in order to add additional HTTP headers
   * to the request.
   *
   * @param url the URL
   * @param request the Object to be POSTed
   * @param uriVariables the variables to expand the template
   * @return the converted object
   * @see HttpEntity
   */
  def postForEntity[T: ClassTag](url: String, request: Option[Any], uriVariables: Map[String, _]): ResponseEntity[T] = {
    javaTemplate.postForEntity(url, request.orNull, typeToClass[T], uriVariables.asJava)
  }

  /**
   * Create a new resource by POSTing the given object to the URL, and returns the response as
   * [[org.springframework.http.ResponseEntity]].
   *
   * The `request` parameter can be a [[org.springframework.http.HttpEntity]] in order to add additional HTTP headers
   * to the request.
   *
   * @param url the URL
   * @param request the Object to be POSTed, may be `null`
   * @return the converted object
   * @see HttpEntity
   * @since 3.0.2
   */
  def postForEntity[T: ClassTag](url: URI, request: Option[Any]): ResponseEntity[T] = {
    javaTemplate.postForEntity(url, request.orNull, typeToClass[T])
  }

  // PUT
  /**
   * Create or update a resource by PUTting the given object to the URI.
   *
   * URI Template variables are expanded using the given URI variables, if any.
   *
   * The `request` parameter can be a [[org.springframework.http.HttpEntity]] in order to add additional HTTP headers
   * to the request.
   *
   * @param url the URL
   * @param request the Object to be PUT
   * @param uriVariables the variables to expand the template
   * @see HttpEntity
   */
  def put(url: String, request: Option[Any], uriVariables: Any*) {
    javaTemplate.put(url, request.orNull, asInstanceOfAnyRef(uriVariables))
  }

  /**
   * Creates a new resource by PUTting the given object to URI template.
   *
   * URI Template variables are expanded using the given map.
   *
   * The `request` parameter can be a [[org.springframework.http.HttpEntity]] in order to add additional HTTP headers
   * to the request.
   *
   * @param url the URL
   * @param request the Object to be PUT
   * @param uriVariables the variables to expand the template
   * @see HttpEntity
   */
  def put(url: String, request: Option[Any], uriVariables: Map[String, _]) {
    javaTemplate.put(url, request.orNull, uriVariables.asJava)
  }

  /**
   * Creates a new resource by PUTting the given object to URL.
   *
   * The `request` parameter can be a [[org.springframework.http.HttpEntity]] in order to add additional HTTP headers
   * to the request.
   *
   * @param url the URL
   * @param request the Object to be PUT
   * @see HttpEntity
   */
  def put(url: URI, request: Option[Any]) {
    javaTemplate.put(url, request.orNull)
  }

  // DELETE
  /**
   * Delete the resources at the specified URI.
   *
   * URI Template variables are expanded using the given URI variables, if any.
   * @param url the URL
   * @param uriVariables the variables to expand in the template
   */
  def delete(url: String, uriVariables: Any*) {
    javaTemplate.delete(url, asInstanceOfAnyRef(uriVariables))
  }

  /**
   * Delete the resources at the specified URI.
   *
   * URI Template variables are expanded using the given map.
   *
   * @param url the URL
   * @param uriVariables the variables to expand the template
   */
  def delete(url: String, uriVariables: Map[String, _]) {
    javaTemplate.delete(url, uriVariables.asJava)
  }

  /**
   * Delete the resources at the specified URL.
   *
   * @param url the URL
   */
  def delete(url: URI) {
    javaTemplate.delete(url)
  }

  // OPTIONS
  /**
   * Return the value of the Allow header for the given URI.
   *
   * URI Template variables are expanded using the given URI variables, if any.
   *
   * @param url the URL
   * @param uriVariables the variables to expand in the template
   * @return the value of the allow header
   */
  def optionsForAllow(url: String, uriVariables: Any*): Set[HttpMethod] = {
    javaTemplate.optionsForAllow(url, asInstanceOfAnyRef(uriVariables)).asScala
  }

  /**
   * Return the value of the Allow header for the given URI.
   *
   * URI Template variables are expanded using the given map.
   *
   * @param url the URL
   * @param uriVariables the variables to expand in the template
   * @return the value of the allow header
   */
  def optionsForAllow(url: String, uriVariables: Map[String, _]): Set[HttpMethod] = {
    javaTemplate.optionsForAllow(url, uriVariables.asJava).asScala
  }

  /**
   * Return the value of the Allow header for the given URL.
   *
   * @param url the URL
   * @return the value of the allow header
   */
  def optionsForAllow(url: URI): Set[HttpMethod] = {
    javaTemplate.optionsForAllow(url).asScala
  }

  // exchange
  /**
   * Execute the HTTP method to the given URI template, writing the given request entity to the request, and
   * returns the response as `ResponseEntity`.
   *
   * URI Template variables are expanded using the given URI variables, if any.
   * @param url the URL
   * @param method the HTTP method (GET, POST, etc)
   * @param requestEntity the entity (headers and/or body) to write to the request
   * @param uriVariables the variables to expand in the template
   * @return the response as entity
   */
  def exchange[T: ClassTag](url: String, method: HttpMethod, requestEntity: Option[HttpEntity[_]], uriVariables: Any*): ResponseEntity[T] = {
    javaTemplate
        .exchange(url, method, requestEntity.orNull, typeToClass[T], asInstanceOfAnyRef(uriVariables): _*)
  }

  /**
   * Execute the HTTP method to the given URI template, writing the given request entity to the request, and
   * returns the response as `ResponseEntity`.
   *
   * URI Template variables are expanded using the given URI variables.
   *
   * @param url the URL
   * @param method the HTTP method (GET, POST, etc)
   * @param requestEntity the entity (headers and/or body) to write to the request
   * @param uriVariables the variables to expand in the template
   * @return the response as entity
   * @since 3.0.2
   */
  def exchange[T: ClassTag](url: String, method: HttpMethod, requestEntity: Option[HttpEntity[_]], uriVariables: Map[String, _]): ResponseEntity[T] = {
    javaTemplate.exchange(url, method, requestEntity.orNull, typeToClass[T], uriVariables.asJava)
  }

  /**
   * Execute the HTTP method to the given URI template, writing the given request entity to the request, and
   * returns the response as `ResponseEntity`.
   *
   * @param url the URL
   * @param method the HTTP method (GET, POST, etc)
   * @param requestEntity the entity (headers and/or body) to write to the request
   * @return the response as entity
   */
  def exchange[T: ClassTag](url: URI, method: HttpMethod, requestEntity: Option[HttpEntity[_]]): ResponseEntity[T] = {
    javaTemplate.exchange(url, method, requestEntity.orNull, typeToClass[T])
  }

  // general execution
  /**
   * Execute the HTTP method to the given URI template, preparing the request with the given function, and reading the
   * response with a function.
   *
   * URI Template variables are expanded using the given URI variables, if any.
   *
   * @param url the URL
   * @param method the HTTP method (GET, POST, etc)
   * @param requestFunction function that prepares the request
   * @param responseFunction function that extracts the return value from the response
   * @param uriVariables the variables to expand in the template
   * @return an arbitrary object, as returned by the response function
   */
  def execute[T](url: String, method: HttpMethod, uriVariables: Any*)
                (requestFunction: ClientHttpRequest => Unit)
                (responseFunction: ClientHttpResponse => T): Option[T] = {
    Option(javaTemplate
        .execute(url, method, functionToRequestCallback(requestFunction), functionToResponseExtractor(responseFunction),
      asInstanceOfAnyRef(uriVariables)))
  }

  /**
   * Execute the HTTP method to the given URI template, preparing the request with the given function, and reading the
   * response with a function
   *
   * URI Template variables are expanded using the given URI variables map.
   *
   * @param url the URL
   * @param method the HTTP method (GET, POST, etc)
   * @param requestFunction function that prepares the request
   * @param responseFunction function that extracts the return value from the response
   * @param uriVariables the variables to expand in the template
   * @return an arbitrary object, as returned by the response function
   */
  def execute[T](url: String, method: HttpMethod, uriVariables: Map[String, _])
                (requestFunction: ClientHttpRequest => Unit)
                (responseFunction: ClientHttpResponse => T): Option[T] = {
    Option(javaTemplate
        .execute(url, method, functionToRequestCallback(requestFunction), functionToResponseExtractor(responseFunction),
      uriVariables.asJava))
  }

  /**
   * Execute the HTTP method to the given URL, preparing the request with the given function, and reading the response
   * with a function.
   *
   * @param url the URL
   * @param method the HTTP method (GET, POST, etc)
   * @param requestFunction function that prepares the request
   * @param responseFunction function that extracts the return value from the response
   * @return an arbitrary object, as returned by the response object
   */
  def execute[T](url: URI, method: HttpMethod)
                (requestFunction: ClientHttpRequest => Unit)
                (responseFunction: ClientHttpResponse => T): Option[T] = {
    Option(javaTemplate.execute(url, method, functionToRequestCallback(requestFunction),
      functionToResponseExtractor(responseFunction)))

  }

  private def asInstanceOfAnyRef(seq: Seq[Any]) = {
    seq.map(_.asInstanceOf[AnyRef])
  }

  private def functionToRequestCallback(function: ClientHttpRequest => Unit): RequestCallback =
    new RequestCallback {
      def doWithRequest(request: ClientHttpRequest) {
        function(request)
      }
    }

  private def functionToResponseExtractor[T](function: ClientHttpResponse => T): ResponseExtractor[T] =
    new ResponseExtractor[T] {
      def extractData(response: ClientHttpResponse): T = function(response)
    }

}

object RestTemplate {

	private val jacksonScalaModulePresent =
		ClassUtils.isPresent("com.fasterxml.jackson.module.scala.DefaultScalaModule",
		                     classOf[RestTemplate].getClassLoader)

	private val jackson2Present =
		ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper",
		                     classOf[RestTemplate].getClassLoader) &&
				ClassUtils.isPresent("com.fasterxml.jackson.core.JsonGenerator",
				                     classOf[RestTemplate].getClassLoader);
}
