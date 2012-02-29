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
import collection.immutable.VectorBuilder
import collection.mutable.{ListBuffer, SetBuilder, Builder, ArrayBuffer}

class ScalaCollectionEditorTest extends FunSuite {

	test("null as empty collection") {
		val editor = new ScalaCollectionEditor(new ArrayBuffer[String](), true)
		editor.setValue(null)
		val result = editor.getValue.asInstanceOf[ArrayBuffer[String]]
		assert(result.isEmpty)
	}

	test("null not as empty collection") {
		val editor = new ScalaCollectionEditor(new ArrayBuffer[String](), false)
		editor.setValue(null)
		val result = editor.getValue
		assert(result == null)
	}

	test("array buffer") {
		doTest(new ArrayBuffer[String]())
	}

	test("list buffer") {
		doTest(new ListBuffer[String]())
	}

	test("vector") {
		doTest(new VectorBuilder[String]())
	}

	test("set") {
		doTest(new SetBuilder(Set.empty[String]))
	}

	private def doTest[To](builder: Builder[String, To]) {
		val editor = new ScalaCollectionEditor[String](builder)
		val value: String = "foo"
		editor.setValue(value)
		val result = editor.getValue
		result match {
			case seq: Seq[String] => assert(seq.contains(value))
			case set: Set[String] => assert(set.toSeq.contains(value))
			case x => fail("Unexpected result: [" + x.getClass + "]")
		}

	}


}
