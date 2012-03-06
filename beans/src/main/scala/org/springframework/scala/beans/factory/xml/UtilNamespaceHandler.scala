package org.springframework.scala.beans.factory.xml

import org.springframework.util.StringUtils
import org.w3c.dom.Element
import org.springframework.beans.factory.xml.{ParserContext, AbstractSingleBeanDefinitionParser, NamespaceHandlerSupport}
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.scala.beans.factory.config.SeqFactoryBean
import scala.collection.JavaConversions._

/**
 * @author Arjen Poutsma
 */
class UtilNamespaceHandler extends NamespaceHandlerSupport {

	private final val SCOPE_ATTRIBUTE: String = "scope"

	def init() {
		registerBeanDefinitionParser("seq", new SeqBeanDefinitionParser());
	}

	private class SeqBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

		protected override def getBeanClass(element: Element) = classOf[SeqFactoryBean[_]]

		protected override def doParse(element: Element, parserContext: ParserContext, builder: BeanDefinitionBuilder) {
			val parsedList: Seq[_] = parserContext.getDelegate.parseListElement(element, builder.getRawBeanDefinition)
			builder.addConstructorArgValue(parsedList)
/*
			if (StringUtils.hasText(listClass)) {
				builder.addPropertyValue("targetListClass", listClass)
			}
*/
			val scope: String = element.getAttribute(SCOPE_ATTRIBUTE)
			if (StringUtils.hasLength(scope)) {
				builder.setScope(scope)
			}
		}
	}

}