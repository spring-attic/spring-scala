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

package org.springframework.scala.context

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterEach, GivenWhenThen, FunSuite}
import org.springframework.context.support.GenericApplicationContext
import org.springframework.scala.context.function.FunctionalConfiguration
import org.springframework.beans.factory.support.DefaultBeanNameGenerator
import org.springframework.context.ApplicationEvent
import scala.collection.mutable.ArrayBuffer
import org.springframework.context.event.{ContextClosedEvent, ContextRefreshedEvent}

/**
 * @author Tomasz Nurkiewicz
 */
@RunWith(classOf[JUnitRunner])
class ApplicationListenerAdapterTest extends FunSuite with GivenWhenThen with BeforeAndAfterEach {

	var applicationContext: GenericApplicationContext = _

	val beanNameGenerator = new DefaultBeanNameGenerator()

	override def beforeEach() {
		super.beforeEach()
		applicationContext = new GenericApplicationContext()
	}

	test("should capture startup and shutdown events") {
		Given("context with catch-all listener")
		val config = new FunctionalConfiguration() {
			bean() {
				new AggregatingAllBean
			}
		}
		config.register(applicationContext, beanNameGenerator)
		val aggregator = applicationContext.getBean(classOf[AggregatingAllBean])

		When("context started and destroyed")
		applicationContext.refresh()
		applicationContext.destroy()

		Then("two events captured")
		val classesOfEvents = aggregator.receivedEvents.map(_.getClass)
		assert(classesOfEvents === Seq(classOf[ContextRefreshedEvent], classOf[ContextClosedEvent]))
	}

	test("should only capture destroy event") {
		Given("context with listener listening for destroy event")
		val config = new FunctionalConfiguration() {
			bean() {
				new AggregatingDestroyBean
			}
		}
		config.register(applicationContext, beanNameGenerator)
		val aggregator = applicationContext.getBean(classOf[AggregatingDestroyBean])

		When("context started and destroyed")
		applicationContext.refresh()
		applicationContext.destroy()

		Then("destroy event captured")
		val classesOfEvents = aggregator.receivedEvents.map(_.getClass)
		assert(classesOfEvents === Seq(classOf[ContextClosedEvent]))
	}

	test("should allow capturing custom events") {
		Given("context with listener listening for custom event")
		val config = new FunctionalConfiguration() {
			bean() {
				new AggregatingCustomEventsBean
			}
		}
		config.register(applicationContext, beanNameGenerator)
		val aggregator = applicationContext.getBean(classOf[AggregatingCustomEventsBean])
		applicationContext.refresh()

		When("custom events sent")
		applicationContext.publishEvent(CustomEvent(42, enabled = true, this))
		applicationContext.publishEvent(CustomEvent(43, enabled = true, this))
		applicationContext.publishEvent(CustomEvent(44, enabled = true, this))

		Then("all custom events sent and nothing else")
		assert(aggregator.receivedIds === Seq(42, 43, 44))
	}

	test("should capture both built-in and custom events") {
		Given("context with catch-all listener")
		val config = new FunctionalConfiguration() {
			bean() {
				new AggregatingAllBean
			}
		}
		config.register(applicationContext, beanNameGenerator)
		val aggregator = applicationContext.getBean(classOf[AggregatingAllBean])

		When("context started and destroyed + custom event")
		applicationContext.refresh()
		applicationContext.publishEvent(CustomEvent(42, enabled = false, this))
		applicationContext.destroy()

		Then("both built-in and custom events captured")
		val classesOfEvents = aggregator.receivedEvents.map(_.getClass)
		assert(classesOfEvents === Seq(classOf[ContextRefreshedEvent], classOf[CustomEvent], classOf[ContextClosedEvent]))
	}

	test("should allow advanced pattern matching on custom events") {
		Given("context with listener listening for custom event")
		val config = new FunctionalConfiguration() {
			bean() {
				new AdvancedAggregatingCustomEventsBean
			}
		}
		config.register(applicationContext, beanNameGenerator)
		val aggregator = applicationContext.getBean(classOf[AdvancedAggregatingCustomEventsBean])
		applicationContext.refresh()

		When("custom events sent")
		applicationContext.publishEvent(CustomEvent(42, enabled = false, this))
		applicationContext.publishEvent(CustomEvent(42, enabled = true, this))
		applicationContext.publishEvent(CustomEvent(43, enabled = false, this))
		applicationContext.publishEvent(CustomEvent(44, enabled = true, this))

		Then("only custom events matching pattern are captured")
		assert(aggregator.receivedIds === Seq(44))
	}

	test("should allow registering multiple listeners") {
		Given("context with two different listeners")
		val config = new FunctionalConfiguration() {
			bean() {
				new AggregatingAllBean
			}
			bean() {
				new AggregatingCustomEventsBean
			}
		}
		config.register(applicationContext, beanNameGenerator)
		val allAggregator = applicationContext.getBean(classOf[AggregatingAllBean])
		val customAggregator = applicationContext.getBean(classOf[AggregatingCustomEventsBean])

		When("context started and destroyed")
		applicationContext.refresh()
		applicationContext.publishEvent(CustomEvent(47, enabled = true, this))
		applicationContext.destroy()

		Then("both listeners reached")
		assert(allAggregator.receivedEvents.map(_.getClass) === Seq(classOf[ContextRefreshedEvent], classOf[CustomEvent], classOf[ContextClosedEvent]))
		assert(customAggregator.receivedIds === Seq(47))
	}

}

class AggregatingAllBean extends ApplicationListenerAdapter {
	val receivedEvents = new ArrayBuffer[ApplicationEvent]()
	def onEvent = {
		case x => receivedEvents += x
	}
}

class AggregatingDestroyBean extends ApplicationListenerAdapter {
	val receivedEvents = new ArrayBuffer[ApplicationEvent]()
	def onEvent = {
		case closed: ContextClosedEvent => receivedEvents += closed
	}
}

class AggregatingCustomEventsBean extends ApplicationListenerAdapter {
	val receivedIds = new ArrayBuffer[Int]()
	def onEvent = {
		case CustomEvent(id, _, _) => receivedIds += id
	}
}

case class CustomEvent(id: Int, enabled: Boolean, eventSource: AnyRef) extends ApplicationEvent(eventSource)

class AdvancedAggregatingCustomEventsBean extends ApplicationListenerAdapter {
	val receivedIds = new ArrayBuffer[Int]()
	def onEvent = {
		case CustomEvent(id, true, _) if id > 42 => receivedIds += id
	}
}


