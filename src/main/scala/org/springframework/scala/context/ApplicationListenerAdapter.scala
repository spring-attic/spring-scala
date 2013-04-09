package org.springframework.scala.context

import org.springframework.context.{ApplicationListener, ApplicationEvent}

/**
 * @author Tomasz Nurkiewicz
 */
trait ApplicationListenerAdapter extends ApplicationListener[ApplicationEvent] {
	def onApplicationEvent(event: ApplicationEvent) {
		if(onEvent isDefinedAt event) {
			onEvent(event)
		}
	}

	def onEvent: PartialFunction[ApplicationEvent, Unit]

}
