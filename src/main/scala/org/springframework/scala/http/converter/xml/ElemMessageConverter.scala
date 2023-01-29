/*
 * Copyright 2011-2012 the original author or authors.
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

package org.springframework.scala.http.converter.xml

import org.springframework.http.converter.AbstractHttpMessageConverter
import org.springframework.http.{MediaType, HttpInputMessage, HttpOutputMessage}
import scala.xml.{XML, Elem}
import java.nio.charset.Charset
import java.io.OutputStreamWriter

/**
 * @author Arjen Poutsma
 */
class ElemMessageConverter extends AbstractHttpMessageConverter[Elem](MediaType.APPLICATION_XML, MediaType.TEXT_XML,
	new MediaType("application", "*+xml")) {

	final val DEFAULT_CHARSET: Charset = Charset.forName("UTF-8")

	def supports(clazz: Class[_]) = {
		classOf[Elem] == clazz
	}

	def readInternal(clazz: Class[_ <: Elem], inputMessage: HttpInputMessage): Elem = {
		XML.load(inputMessage.getBody)
	}

	def writeInternal(t: Elem, outputMessage: HttpOutputMessage) {
		val contentType = getContentType(outputMessage)
		val writer = new OutputStreamWriter(outputMessage.getBody, contentType)

		XML.write(writer, t, contentType.toString, false, null)
	}

	private def getContentType(outputMessage: HttpOutputMessage) = {
		val contentType: MediaType = outputMessage.getHeaders.getContentType
		if (contentType != null && contentType.getCharSet != null) {
			contentType.getCharSet
		}
		DEFAULT_CHARSET
	}
}