package org.springframework.scala.beans.factory.xml

import org.scalatest.FunSuite
import org.springframework.context.support.ClassPathXmlApplicationContext

/**
 * @author Arjen Poutsma
 */
class UtilNamespaceHandlerTest extends FunSuite {

	val applicationContext = new ClassPathXmlApplicationContext("utilNamespaceHandlerTest.xml", getClass)

	test("seq") {
		val seq = applicationContext.getBean("seq", classOf[scala.collection.Seq[_]])
		assert(seq != null)
		assert(seq.size == 2)
		assert(seq.contains("one"))
		assert(seq.contains("two"))
	}

}