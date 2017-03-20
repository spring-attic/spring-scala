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
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.springframework.context.support.ClassPathXmlApplicationContext
import org.scalatest.matchers.ShouldMatchers

@RunWith(classOf[JUnitRunner])
class ScalaCollectionEditorTests extends FunSuite with ShouldMatchers {

	test("null as empty collection") {
		val editor = new ScalaCollectionEditor(Seq.newBuilder[String] _, true)
		editor.setValue(null)
		val result = editor.getValue.asInstanceOf[Seq[String]]
		assert(result.isEmpty)
	}

	test("null not as empty collection") {
		val editor = new ScalaCollectionEditor(Seq.newBuilder[String] _, false)
		editor.setValue(null)
		val result = editor.getValue
		assert(result == null)
	}

  test("the returned map when not forcing the right key type will fail"){
     val applicationContext = new ClassPathXmlApplicationContext("collectionErasureTest.xml",getClass)
      val map = applicationContext.getBean("erasedMapBean1").asInstanceOf[ErasedMapBean]
       map.data should contain key (JavaEnum.FIRST)

  }

  test("the returned map when forcing the key type should have the right keys"){
       val applicationContext = new ClassPathXmlApplicationContext("collectionErasureTest.xml",getClass)
       val map = applicationContext.getBean("erasedMapBean2").asInstanceOf[ErasedMapBean]
       map.data should contain key (JavaEnum.FIRST)

  }

  test("you should not be able to inject in a scala class a wrong typed map"){
    val applicationContext = new ClassPathXmlApplicationContext("collectionErasureTest.xml",getClass)
    val map = applicationContext.getBean(classOf[WrongMapBean])
    map should be (null)

   }


}
