/*
 * Copyright 2011-2012 the original author or authors.
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

package org.springframework.scala.beans.propertyeditors

import org.scalatest.FunSuite
import scala.collection.mutable.{Buffer, ArrayBuffer}

/**
 * @author Arjen Poutsma
 */
class SeqEditorTest extends FunSuite {
	
	test("arrayBuffer") {
		val editor = new SeqEditor[String](classOf[ArrayBuffer[String]])
		editor.setValue("foo")
		val result = editor.getValue
		assert(result.isInstanceOf[ArrayBuffer[String]])
	}

	test("buffer") {
		val editor = new SeqEditor[String](classOf[Buffer[String]])
		editor.setValue("foo")
		val result = editor.getValue
		assert(result.isInstanceOf[Buffer[String]])
	}

	test("list") {
		val editor = new SeqEditor[String](classOf[List[String]])
		editor.setValue("foo")
		val result = editor.getValue
		assert(result.isInstanceOf[List[String]])
	}

	test("indexSeq") {
		val editor = new SeqEditor[String](classOf[IndexedSeq[String]])
		editor.setValue("foo")
		val result = editor.getValue
		assert(result.isInstanceOf[IndexedSeq[String]])
	}

	test("stream") {
		val editor = new SeqEditor[String](classOf[Stream[String]])
		editor.setValue("foo")
		val result = editor.getValue
		assert(result.isInstanceOf[Stream[String]])
	}



}
