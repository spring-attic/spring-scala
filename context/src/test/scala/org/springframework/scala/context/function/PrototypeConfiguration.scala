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

import org.springframework.beans.factory.support.DefaultListableBeanFactory

/**
 * @author Arjen Poutsma
 */
object PrototypeConfiguration {

	implicit val beanFactory = new DefaultListableBeanFactory()

	var singletonCount = 0;

	var prototypeCount = 0;

	class Config extends FunctionalConfiguration {

		val s = singleton(name = "singleton") {
			singletonCount += 1
			singletonCount
		}

		val p = prototype(name = "prototype") {
			prototypeCount += 1
			prototypeCount
		}

	}

	def main(args: Array[String]) {
		val config = new Config

		val singleton1 = config.s
		val singleton2 = config.s
		assert(singleton1 == singleton2)
		assert(singletonCount == 1)

		val prototype1 = config.p()
		val prototype2 = config.p()
		assert(prototype1 != prototype2)
		assert(prototypeCount == 2)
	}

}
