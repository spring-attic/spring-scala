package org.springframework.scala.beans.factory

import org.scalatest.FunSuite
import org.springframework.beans.factory.support.DefaultListableBeanFactory

/**
 * @author Arjen Poutsma
 */
class RichBeanFactoryTest extends FunSuite {

	test("getBean") {
		val beanFactory = new DefaultListableBeanFactory()
		beanFactory.registerSingleton("foo", "Foo")
		val richBeanFactory = RichBeanFactory(beanFactory)

		val result = richBeanFactory.getBean[String]
		assert("Foo" == result)
	}

	test("getBean(String)") {
		val beanFactory = new DefaultListableBeanFactory()
		beanFactory.registerSingleton("foo", "Foo")
		val richBeanFactory = RichBeanFactory(beanFactory)

		val result = richBeanFactory.getBean[String]("foo")
		assert("Foo" == result)
	}

}
