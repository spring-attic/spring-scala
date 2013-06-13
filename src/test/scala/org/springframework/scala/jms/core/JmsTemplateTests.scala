package org.springframework.scala.jms.core

import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.springframework.jms.core.{ JmsTemplate => JavaJmsTemplate }
import org.scalatest.BeforeAndAfter
import org.mockito.Mockito._
import javax.naming.Context
import javax.jms.ConnectionFactory
import javax.jms.Connection
import javax.jms.Session
import javax.jms.Queue
import org.springframework.jms.support.destination.JndiDestinationResolver
import org.springframework.jndi.JndiTemplate
import javax.jms.MessageProducer
import javax.jms.TextMessage

@RunWith(classOf[JUnitRunner])
class JmsTemplateTests extends FunSuite with BeforeAndAfter {

  private var jmsTemplate: JmsTemplate = _

  private var session: Session = _
  private var queue: Queue = _
  private var messageProducer: MessageProducer = _
  private var jndiContext: Context = _
  private var javaJmsTemplate: JavaJmsTemplate = _

  before {
    jndiContext = mock(classOf[Context])
    val connectionFactory = mock(classOf[ConnectionFactory])
    val connection = mock(classOf[Connection])
    session = mock(classOf[Session])
    queue = mock(classOf[Queue])
    messageProducer = mock(classOf[MessageProducer]);
    val textMessage = mock(classOf[TextMessage]);

    when(connectionFactory.createConnection()).thenReturn(connection)
    when(connection.createSession(false, Session.AUTO_ACKNOWLEDGE)).thenReturn(session)
    when(session.getTransacted()).thenReturn(false)
    when(jndiContext.lookup("testDestination")).thenReturn(queue, null)
    when(session.createProducer(queue)).thenReturn(messageProducer);

    javaJmsTemplate = new JavaJmsTemplate()
    val destMan = new JndiDestinationResolver();
    destMan.setJndiTemplate(new JndiTemplate() {
      override def createInitialContext() = jndiContext
    });
    javaJmsTemplate.setDestinationResolver(destMan);
    javaJmsTemplate.setSessionTransacted(false);
    javaJmsTemplate.setDefaultDestinationName("testDestination")
    javaJmsTemplate.setConnectionFactory(connectionFactory)
    jmsTemplate = new JmsTemplate(javaJmsTemplate)
  }

  test("send with function to a default destination") {
    jmsTemplate.send() { _.createTextMessage("test message") }
    verify(session).createProducer(queue)
  }

  test("send with function to a specific destination queue") {
    val q1 = mock(classOf[Queue])
    when(session.createProducer(q1)).thenReturn(messageProducer);
    jmsTemplate.send(q1) { _.createTextMessage("test message") }
    verify(session).createProducer(q1)
  }

  test("send with function to a specific destination queue, resolved by name") {
    jmsTemplate.send("testDestination") { _.createTextMessage("test message") }
    verify(jndiContext).lookup("testDestination")
    verify(session).createProducer(queue)
  }
}