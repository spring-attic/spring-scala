package org.springframework.scala.beans.factory.xml

import org.scalatest.FunSuite
import org.springframework.context.support.ClassPathXmlApplicationContext
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

/**
 * @author Arjen Poutsma
 */
@RunWith(classOf[JUnitRunner])
class UtilNamespaceHandlerTests extends FunSuite {

	val applicationContext = new ClassPathXmlApplicationContext("utilNamespaceHandlerTest.xml", getClass)

	test("seq") {
		val seq = applicationContext.getBean("seq", classOf[scala.collection.Seq[String]])
		assert(seq != null)
		assert(seq.size == 2)
		assert(seq.contains("one"))
		assert(seq.contains("two"))
	}

	test("set") {
		val set = applicationContext.getBean("set", classOf[scala.collection.Set[String]])
		assert(set != null)
		assert(set.size == 2)
		assert(set.contains("one"))
		assert(set.contains("two"))
	}

	test("map") {
		val map = applicationContext.getBean("map", classOf[scala.collection.Map[String, String]])
		assert(map != null)
		assert(map.size == 1)
		assert(map.contains("one"))
		assert(map("one") == "two")
	}

}