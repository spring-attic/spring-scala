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
import scala.collection.Map
import org.springframework.web.client.{RequestCallback, ResponseExtractor}
import org.springframework.http.client.{ClientHttpRequest, ClientHttpRequestFactory, ClientHttpResponse}
import org.springframework.http.{HttpEntity, ResponseEntity, HttpMethod}

/**
 * @author Arjen Poutsma
 * @since 1.0
 * @constructor Creates a `RestTemplate` that wraps the given Java template
 * @param javaTemplate the Java `RestTemplate` to wrap
 */
class RestTemplate(val javaTemplate: org.springframework.web.client.RestOperations) {

	/**
	 * Create a new instance of the `RestTemplate` given the ClientHttpRequestFactory to obtain requests from
	 *
	 * @param requestFactory HTTP request factory to use
	 */
	def this(requestFactory: ClientHttpRequestFactory) {
		this (new org.springframework.web.client.RestTemplate(requestFactory))
	}

	/**
	 * Create a new instance of the `RestTemplate` using default settings.
	 */
	def this() {
		this (new org.springframework.web.client.RestTemplate())
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
	def getForAny[T](url: String, uriVariables: Any*)(implicit manifest: Manifest[T]): Option[T] = {
		Option(javaTemplate.getForObject(url, responseType(manifest), asInstanceOfAnyRef(uriVariables): _*))
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
	def getForAny[T](url: String, uriVariables: Map[String, _])(implicit manifest: Manifest[T]): Option[T] = {
		Option(javaTemplate.getForObject(url, responseType(manifest), uriVariables.asJava))
	}

	/**
	 * Retrieve a representation by doing a GET on the URL.
	 * The response (if any) is converted and returned.
	 *
	 * @param url the URL
	 * @return the converted object
	 */
	def getForAny[T](url: URI)(implicit manifest: Manifest[T]): Option[T] = {
		Option(javaTemplate.getForObject(url, responseType(manifest)))
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
	def getForEntity[T](url: String, uriVariables: Any*)(implicit manifest: Manifest[T]): ResponseEntity[T] = {
		javaTemplate.getForEntity(url, responseType(manifest), asInstanceOfAnyRef(uriVariables): _*)
	}

	/**
	 * Retrieve a representation by doing a GET on the URL.
	 * The response is converted and stored in an {@link ResponseEntity}.
	 *
	 * URI Template variables are expanded using the given map.
	 * @param url the URL
	 * @param uriVariables the map containing variables for the URI template
	 * @return the converted object
	 */
	def getForEntity[T](url: String, uriVariables: Map[String, _])
	                   (implicit manifest: Manifest[T]): ResponseEntity[T] = {
		javaTemplate.getForEntity(url, responseType(manifest), uriVariables.asJava)
	}

	/**
	 * Retrieve a representation by doing a GET on the URL .
	 * The response is converted and stored in an {@link ResponseEntity}.
	 * @param url the URL
	 * @param responseType the type of the return value
	 * @return the converted object
	 */
	def getForEntity[T](url: URI)(implicit manifest: Manifest[T]): ResponseEntity[T] = {
		javaTemplate.getForEntity(url, responseType(manifest))
	}

	// DELETE
	def delete(url: String, urlVariables: Any*) {
		javaTemplate.delete(url, asInstanceOfAnyRef(urlVariables): _*)
	}

	def delete(url: String, urlVariables: Map[String, _]) {
		javaTemplate.delete(url, urlVariables.asJava)
	}

	def delete(url: URI) {
		javaTemplate.delete(url)
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
	def exchange[T](url: String, method: HttpMethod, requestEntity: Option[HttpEntity[_]], uriVariables: Any*)
	               (implicit manifest: Manifest[T]): ResponseEntity[T] = {
		javaTemplate.exchange(url, method, requestEntity.orNull, responseType(manifest),
			asInstanceOfAnyRef(uriVariables): _*)
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
	 * @param responseType the type of the return value
	 * @param uriVariables the variables to expand in the template
	 * @return the response as entity
	 * @since 3.0.2
	 */
	def exchange[T](url: String, method: HttpMethod, requestEntity: Option[HttpEntity[_]], uriVariables: Map[String, _])
	               (implicit manifest: Manifest[T]): ResponseEntity[T] = {
		javaTemplate.exchange(url, method, requestEntity.orNull, responseType(manifest), uriVariables.asJava)
	}

	/**
	 * Execute the HTTP method to the given URI template, writing the given request entity to the request, and
	 * returns the response as `ResponseEntity`.
	 *
	 * @param url the URL
	 * @param method the HTTP method (GET, POST, etc)
	 * @param requestEntity the entity (headers and/or body) to write to the request
	 * @param responseType the type of the return value
	 * @return the response as entity
	 */
	def exchange[T](url: URI, method: HttpMethod, requestEntity: Option[HttpEntity[_]])
	               (implicit manifest: Manifest[T]): ResponseEntity[T] = {
		javaTemplate.exchange(url, method, requestEntity.orNull, responseType(manifest))
	}

	def execute[T](url: URI, method: HttpMethod)
	              (requestFunction: ClientHttpRequest => Unit)
	              (responseFunction: ClientHttpResponse => T): Option[T] = {
		Option(javaTemplate.execute(url, method, functionToRequestCallback(requestFunction),
			functionToResponseExtractor(responseFunction)))
	}

	private def asInstanceOfAnyRef(seq: Seq[Any]) = {
		seq.map(_.asInstanceOf[AnyRef]);
	}

	def responseType[T](manifest: Manifest[T]): Class[T] = {
		manifest.erasure.asInstanceOf[Class[T]]
	}

	def functionToRequestCallback(function: ClientHttpRequest => Unit) =
		new RequestCallback {
			def doWithRequest(request: ClientHttpRequest) {
				function(request)
			}
		}

	def functionToResponseExtractor[T](function: ClientHttpResponse => T): ResponseExtractor[T] =
		new ResponseExtractor[T] {
			def extractData(response: ClientHttpResponse) = function(response)
		}


}
