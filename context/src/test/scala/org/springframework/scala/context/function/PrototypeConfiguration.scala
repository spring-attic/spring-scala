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

package org.springframework.scala.context.function

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

import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.support.DefaultListableBeanFactory

/**
 * @author Arjen Poutsma
 */
object PrototypeConfiguration {

	implicit val beanFactory = new DefaultListableBeanFactory()

	var singletonCount = 0;

	var prototypeCount = 0;

	class Config extends FunctionalConfiguration {

		val singleton = bean(name = "singleton") {
			singletonCount += 1
			singletonCount
		}

		val prototype = bean(name = "prototype",
			scope = BeanDefinition.SCOPE_PROTOTYPE) {
			prototypeCount += 1
			prototypeCount
		}

		val prototypeVal = prototype()
	}

	def main(args: Array[String]) {
		val config = new Config

		val singleton1 = config.singleton()
		val singleton2 = config.singleton()
		assert(singleton1 == singleton2)
		assert(singletonCount == 1)

		val prototype1 = config.prototype()
		val prototype2 = config.prototype()
		assert(prototype1 != prototype2)
		assert(prototypeCount > 1)

		val prototypeInstance1 = config.prototypeVal
		val prototypeInstance2 = config.prototypeVal
		assert(prototypeInstance1 == prototypeInstance2) // <-- Undesirable
	}

}
