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

package org.springframework.scala.beans.propertyeditors

import org.scalatest.FunSuite
import org.springframework.context.support.ClassPathXmlApplicationContext
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

/**
 * @author Arjen Poutsma
 */
@RunWith(classOf[JUnitRunner])
class ScalaEditorRegistrarTests extends FunSuite {

	val applicationContext = new ClassPathXmlApplicationContext("scalaEditorRegistrarTest.xml", getClass)

	test("primitives") {
		val bean = applicationContext.getBean("primitivesBean", classOf[PrimitivesBean])
		assert(bean.byte == 42)
		assert(bean.short == 42)
		assert(bean.int == 42)
		assert(bean.long == 42)
		assert(bean.char == '4')
		assert(bean.float == 42)
		assert(bean.double == 42)
		assert(bean.bool)
		assert(bean.string == "foo")
	}

	test("types") {
		val bean = applicationContext.getBean("typesBean", classOf[TypesBean])
		assert(bean.regex.toString() == "\\d")
	}

	test("collections") {
		val bean = applicationContext.getBean("collectionBean", classOf[CollectionsBean])
		testSeq(bean.seq)
		testSeq(bean.immutableSeq)
		testSeq(bean.mutableSeq)

		testSeq(bean.indexedSeq)
		testSeq(bean.immutableIndexedSeq)
		testSeq(bean.mutableIndexedSeq)

		testSeq(bean.resizableArray)

		testSeq(bean.linearSeq)
		testSeq(bean.immutableLinearSeq)
		testSeq(bean.mutableLinearSeq)

		testSeq(bean.buffer)

		testSet(bean.set)
		testSet(bean.immutableSet)
		testSet(bean.mutableSet)

		testMap(bean.map)
		testMap(bean.immutableMap)
		testMap(bean.mutableMap)
	}
	
	private def testSeq(seq: Seq[String]) {
		assert(seq != null)
		assert(seq.size == 3)
		assert(seq.contains("one"))
		assert(seq.contains("two"))
		assert(seq.contains("three"))
	}

	private def testSet(set: scala.collection.Set[String]) {
		assert(set != null)
		assert(set.size == 3)
		assert(set.contains("one"))
		assert(set.contains("two"))
		assert(set.contains("three"))
	}
	
	private def testMap(map: scala.collection.Map[String, String]) {
		assert(map != null)
		assert(map.size == 1)
		assert(map.contains("foo"))
		assert(map("foo") == "bar")
	}


}
