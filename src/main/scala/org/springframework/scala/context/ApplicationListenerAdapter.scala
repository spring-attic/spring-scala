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

import org.springframework.context.{ApplicationListener, ApplicationEvent}

/**
 * Adapter to allow handling [[org.springframework.context.ApplicationEvent]]s with
 * pattern matching.
 *
 * @author Tomasz Nurkiewicz
 * @author Arjen Poutsma
 */
trait ApplicationListenerAdapter extends ApplicationListener[ApplicationEvent] {

	final def onApplicationEvent(event: ApplicationEvent) {
		if (onEvent isDefinedAt event) {
			onEvent(event)
		}
	}

	/**
	 * Handle an application event with a function.
	 *
	 * @return a function that takes an [[org.springframework.context.ApplicationEvent]] as
	 *         parameter
	 */
	def onEvent: PartialFunction[ApplicationEvent, Unit]

}
