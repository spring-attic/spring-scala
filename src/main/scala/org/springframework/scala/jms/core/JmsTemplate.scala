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

package org.springframework.scala.jms.core

import javax.jms.{ConnectionFactory, Destination, Message, MessageProducer, Queue, QueueBrowser, Session}

import org.springframework.jms.JmsException
import org.springframework.jms.core.{JmsTemplate => JavaTemplate, _}

/**
 * Scala-based convenience wrapper for the Spring [[org.springframework.jms.core.JmsTemplate]], taking
 * advantage of functions and Scala types.
 *
 * @author Arjen Poutsma
 * @since 1.0
 * @constructor Creates a `JmsTemplate` that wraps the given Java template
 * @param javaTemplate the Java `JmsTemplate` to wrap
 */
class JmsTemplate(val javaTemplate: JavaTemplate) {

	/**
	 * Create a new JmsTemplate, given a ConnectionFactory.
	 *
	 * @param connectionFactory the ConnectionFactory to obtain Connections from
	 */
	def this(connectionFactory: ConnectionFactory) {
		this (new org.springframework.jms.core.JmsTemplate(connectionFactory))
	}

	//-------------------------------------------------------------------------
	// Convenience methods for sending messages
	//-------------------------------------------------------------------------

	/**
	 * Send a message to the default destination.
	 *
	 * @param function function to create a message
	 * @note This will only work with a default destination specified!
	 */
	def send()(function: Session => Message) {
		javaTemplate.send(functionToMessageCreator(function))
	}

	/**
	 * Send a message to the specified destination.
	 *
	 * @param destination the destination to send this message to
	 * @param function function to create a message
	 */
	def send(destination: Destination)(function: Session => Message) {
		javaTemplate.send(destination, functionToMessageCreator(function))
	}

	/**
	 * Send a message to the specified destination.
	 *
	 * @param destinationName the name of the destination to send this message to (to be
	 *                        resolved to an actual destination by a DestinationResolver)
	 * @param function function to create a message
	 */
	def send(destinationName: String)(function: Session => Message) {
		javaTemplate.send(destinationName, functionToMessageCreator(function))
	}

	private def functionToMessageCreator(function: Session => Message): MessageCreator =
		new MessageCreator {
			def createMessage(session: Session) = function(session)
		}


	//-------------------------------------------------------------------------
	// Convenience methods for sending auto-converted messages
	//-------------------------------------------------------------------------

	/**
	 * Send the given object to the default destination, converting the object to a JMS
	 * message with a configured MessageConverter.
	 *
	 * @param message the object to convert to a message
	 * @throws JmsException converted checked JMSException to unchecked
	 * @note This will only work with a default destination specified!
	 */
	def convertAndSend(message: Any) {
		javaTemplate.convertAndSend(message)
	}

	/**
	 * Send the given object to the specified destination, converting the object to a JMS
	 * message with a configured MessageConverter.
	 *
	 * @param destination the destination to send this message to
	 * @param message the object to convert to a message
	 */
	def convertAndSend(destination: Destination, message: Any) {
		javaTemplate.convertAndSend(destination, message)
	}

	/**
	 * Send the given object to the specified destination, converting the object to a JMS
	 * message with a configured MessageConverter.
	 *
	 * @param destinationName the name of the destination to send this message to (to be
	 *                        resolved to an actual destination by a DestinationResolver)
	 * @param message the object to convert to a message
	 */
	def convertAndSend(destinationName: String, message: Any) {
		javaTemplate.convertAndSend(destinationName, message)
	}

	/**
	 * Send the given object to the default destination, converting the object to a JMS
	 * message with a configured MessageConverter. The function allows for modification
	 * of the message after conversion.
	 *
	 * @param message the object to convert to a message
	 * @param function the function to modify the message
	 * @throws JmsException checked JMSException converted to unchecked
	 * @note This will only work with a default destination specified!
	 */
	def convertModifyAndSend(message: Any)(function: Message => Message) {
		javaTemplate.convertAndSend(message, functionToMessagePostProcessor(function))
	}

	/**
	 * Send the given object to the specified destination, converting the object to a JMS
	 * message with a configured MessageConverter.  The function allows for modification of
	 * the message after conversion.
	 *
	 * @param destination the destination to send this message to
	 * @param message the object to convert to a message
	 * @param function the function to modify the message
	 * @throws JmsException checked JMSException converted to unchecked
	 */
	def convertModifyAndSend(destination: Destination, message: Any)(function: Message => Message) {
		javaTemplate.convertAndSend(message, functionToMessagePostProcessor(function))
	}

	/**
	 * Send the given object to the specified destination, converting the object to a JMS
	 * message with a configured MessageConverter.  The function allows for modification
	 * of the message after conversion.
	 *
	 * @param destinationName the name of the destination to send this message to (to be
	 *                        resolved to an actual destination by a DestinationResolver)
	 * @param message the object to convert to a message.
	 * @param function the function to modify the message
	 * @throws JmsException checked JMSException converted to unchecked
	 */
	def convertModifyAndSend(destinationName: String, message: Any)(function: Message => Message) {
		javaTemplate.convertAndSend(destinationName, message,
		                            functionToMessagePostProcessor(function))
	}

	private def functionToMessagePostProcessor(function: Message => Message) =
		new MessagePostProcessor {
			def postProcessMessage(message: Message) = function(message)
		}

	//-------------------------------------------------------------------------
	// Convenience methods for receiving messages
	//-------------------------------------------------------------------------

	/**
	 * Receive a message synchronously from the default destination, but only wait up to a
	 * specified time for delivery.
	 *
	 * @return the message received by the consumer, or `None` if the timeout expires
	 * @note This will only work with a default destination specified!
	 * @note This method should be used carefully, since it will block the thread until the
	 *       message becomes available or until the timeout value is exceeded.
	 */
	def receive: Option[Message] = {
		Option(javaTemplate.receive())
	}

	/**
	 * Receive a message synchronously from the specified destination, but only wait up to
	 * a specified time for delivery.
	 *
	 * @param destination the destination to receive a message from
	 * @return the message received by the consumer, or `None` if the timeout expires
	 * @note This method should be used carefully, since it will block the thread until the
	 *       message becomes available or until the timeout value is exceeded.
	 */
	def receive(destination: Destination): Option[Message] = {
		Option(javaTemplate.receive(destination))
	}

	/**
	 * Receive a message synchronously from the specified destination, but only wait up to
	 * a specified time for delivery.
	 *
	 * @param destinationName the name of the destination to send this message to (to be
	 *                        resolved to an actual destination by a DestinationResolver)
	 * @return the message received by the consumer, or `None` if the timeout expires
	 * @note This method should be used carefully, since it will block the thread until the
	 *       message becomes available or until the timeout value is exceeded.
	 */
	def receive(destinationName: String): Option[Message] = {
		Option(javaTemplate.receive(destinationName))
	}

	/**
	 * Receive a message synchronously from the default destination, but only wait up to a
	 * specified time for delivery.
	 *
	 * @param messageSelector the JMS message selector expression. See the JMS
	 *                        specification for a detailed definition of selector expressions.
	 * @return the message received by the consumer, or `None` if the timeout expires
	 * @note This will only work with a default destination specified!
	 * @note This method should be used carefully, since it will block the thread until the
	 *       message becomes available or until the timeout value is exceeded.
	 */
	def receiveSelected(messageSelector: String): Option[Message] = {
		Option(javaTemplate.receiveSelected(messageSelector))
	}

	/**
	 * Receive a message synchronously from the specified destination, but only wait up to
	 * a specified time for delivery.
	 *
	 * @param destination the destination to receive a message from
	 * @param messageSelector the JMS message selector expression. See the JMS specification
	 *                        for a detailed definition of selector expressions.
	 * @return the message received by the consumer, or `None` if the timeout expires
	 * @note This method should be used carefully, since it will block the thread until the
	 *       message becomes available or until the timeout value is exceeded.
	 */
	def receiveSelected(destination: Destination, messageSelector: String): Option[Message] = {
		Option(javaTemplate.receiveSelected(destination, messageSelector))
	}

	/**
	 * Receive a message synchronously from the specified destination, but only wait up to
	 * a specified time for delivery.
	 *
	 * @param destinationName the name of the destination to send this message to (to be
	 *                        resolved to an actual destination by a DestinationResolver)
	 * @param messageSelector the JMS message selector expression. See the JMS
	 *                        specification for a detailed definition of selector
	 *                        expressions.
	 * @return the message received by the consumer, or `None` if the timeout expires
	 * @note This method should be used carefully, since it will block the thread until the
	 *       message becomes available or until the timeout value is exceeded.
	 */
	def receiveSelected(destinationName: String, messageSelector: String): Option[Message] = {
		Option(javaTemplate.receiveSelected(destinationName, messageSelector))
	}

	//-------------------------------------------------------------------------
	// Convenience methods for receiving auto-converted messages
	//-------------------------------------------------------------------------

	/**
	 * Receive a message synchronously from the default destination, but only wait up to a
	 * specified time for delivery. Convert the message into an object with a configured
	 * MessageConverter.
	 *
	 * @return the message produced for the consumer or `None` if the timeout expires
	 * @throws JmsException checked JMSException converted to unchecked
	 * @note This will only work with a default destination specified!
	 * @note This method should be used carefully, since it will block the thread until the
	 *       message becomes available or until the timeout value is exceeded.
	 */
	def receiveAndConvert: Option[Any] = {
		Option(javaTemplate.receiveAndConvert())
	}

	/**
	 * Receive a message synchronously from the specified destination, but only wait up to
	 * a specified time for delivery. Convert the message into an object with a configured
	 * MessageConverter.

	 * @param destination the destination to receive a message from
	 * @return the message produced for the consumer or `None` if the timeout expires
	 * @throws JmsException checked JMSException converted to unchecked
	 * @note This method should be used carefully, since it will block the thread until the
	 *       message becomes available or until the timeout value is exceeded.
	 */
	def receiveAndConvert(destination: Destination): Option[Any] = {
		Option(javaTemplate.receiveAndConvert(destination))
	}

	/**
	 * Receive a message synchronously from the specified destination, but only wait up to
	 * a specified time for delivery. Convert the message into an object with a configured
	 * MessageConverter.
	 *
	 * @param destinationName the name of the destination to send this message to (to be
	 *                        resolved to an actual destination by a DestinationResolver)
	 * @return the message produced for the consumer or `None` if the timeout expires
	 * @throws JmsException checked JMSException converted to unchecked
	 * @note This method should be used carefully, since it will block the thread until
	 *       the message becomes available or until the timeout value is exceeded.
	 */
	def receiveAndConvert(destinationName: String): Option[Any] = {
		Option(javaTemplate.receiveAndConvert(destinationName))
	}

	/**
	 * Receive a message synchronously from the default destination, but only wait up to a
	 * specified time for delivery. Convert the message into an object with a configured
	 * MessageConverter.
	 *
	 * @param messageSelector the JMS message selector expression. See the JMS specification
	 *                        for a detailed definition of selector expressions.
	 * @return the message produced for the consumer or `None` if the timeout expires.
	 * @throws JmsException checked JMSException converted to unchecked
	 * @note This will only work with a default destination specified!
	 * @note This method should be used carefully, since it will block the thread until the
	 *       message becomes available or until the timeout value is exceeded.
	 */
	def receiveSelectedAndConvert(messageSelector: String): Option[Any] = {
		Option(javaTemplate.receiveSelectedAndConvert(messageSelector))
	}

	/**
	 * Receive a message synchronously from the specified destination, but only wait up to
	 * a specified time for delivery. Convert the message into an object with a configured
	 * MessageConverter.
	 *
	 * @param destination the destination to receive a message from
	 * @param messageSelector the JMS message selector expression. See the JMS specification
	 *                        for a detailed definition of selector expressions.
	 * @return the message produced for the consumer or `None` if the timeout expires.
	 * @throws JmsException checked JMSException converted to unchecked
	 * @note This method should be used carefully, since it will block the thread until the
	 *       message becomes available or until the timeout value is exceeded.
	 */
	def receiveSelectedAndConvert(destination: Destination, messageSelector: String): Option[Any] = {
		Option(javaTemplate.receiveSelectedAndConvert(destination, messageSelector))
	}

	/**
	 * Receive a message synchronously from the specified destination, but only wait up to
	 * a specified time for delivery. Convert the message into an object with a configured
	 * MessageConverter.
	 *
	 * @param destinationName the name of the destination to send this message to (to be
	 *                        resolved to an actual destination by a DestinationResolver)
	 * @param messageSelector the JMS message selector expression. See the JMS specification
	 *                        for a detailed definition of selector expressions.
	 * @return the message produced for the consumer or `None` if the timeout expires.
	 * @throws JmsException checked JMSException converted to unchecked
	 * @note This method should be used carefully, since it will block the thread until the
	 *       message becomes available or until the timeout value is exceeded.
	 */
	def receiveSelectedAndConvert(destinationName: String, messageSelector: String): Option[Any] = {
		Option(javaTemplate.receiveSelectedAndConvert(destinationName, messageSelector))
	}

	//-------------------------------------------------------------------------
	// Convenience methods for browsing messages
	//-------------------------------------------------------------------------

	/**
	 * Browse messages in the default JMS queue. The function gives access to the JMS
	 * Session and QueueBrowser in order to browse the queue and react to the contents.
	 *
	 * @param function function that exposes the session/browser pair
	 * @return the result object from working with the session
	 * @throws JmsException checked JMSException converted to unchecked
	 */
	def browse[T]()(function: (Session, QueueBrowser) => T): T = {
		javaTemplate.browse(functionToBrowserCallback(function))
	}

	/**
	 * Browse messages in a JMS queue. The function gives access to the JMS Session and
	 * QueueBrowser in order to browse the queue and react to the contents.
	 *
	 * @param queue the queue to browse
	 * @param function function that exposes the session/browser pair
	 * @return the result object from working with the session
	 * @throws JmsException checked JMSException converted to unchecked
	 */
	def browse[T](queue: Queue)(function: (Session, QueueBrowser) => T): T = {
		javaTemplate.browse(queue, functionToBrowserCallback(function))
	}

	/**
	 * Browse messages in a JMS queue. The function gives access to the JMS Session and
	 * QueueBrowser in order to browse the queue and react to the contents.
	 *
	 * @param queueName the name of the queue to browse
	 * (to be resolved to an actual destination by a DestinationResolver)
	 * @param function function that exposes the session/browser pair
	 * @return the result object from working with the session
	 * @throws JmsException checked JMSException converted to unchecked
	 */
	def browse[T](queueName: String)(function: (Session, QueueBrowser) => T): T = {
		javaTemplate.browse(queueName, functionToBrowserCallback(function))
	}

	/**
	 * Execute the action specified by the given action object within a JMS Session.
	 *
	 * @param function function that exposes the session
	 * @return the result object from working with the session
	 * @throws JmsException if there is any problem
	 */
	def execute[T](function: Session => T): T = {
		javaTemplate.execute(new SessionCallback[T] {
			override def doInJms(session: Session): T = function(session)
		})
	}

	/**
	 * Send messages to the default JMS destination (or one specified
	 * for each send operation). The callback gives access to the JMS Session
	 * and MessageProducer in order to perform complex send operations.
	 *
	 * @param function function that exposes the session/producer pair
	 * @return the result object from working with the session
	 * @throws JmsException checked JMSException converted to unchecked
	 */
	def execute[T](function: (Session, MessageProducer) => T): T = {
		javaTemplate.execute(new ProducerCallback[T] {
			override def doInJms(session: Session, producer: MessageProducer): T =
				function(session, producer)
		})
	}

	private def functionToBrowserCallback[T](function: (Session, QueueBrowser) => T): BrowserCallback[T] =
		new BrowserCallback[T] {
			def doInJms(session: Session, browser: QueueBrowser) = function(session,
			                                                                browser)
		}

}