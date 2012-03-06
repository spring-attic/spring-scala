package org.springframework.scala.beans.factory.xml

import org.springframework.util.StringUtils
import org.w3c.dom.Element
import org.springframework.beans.factory.xml.{ParserContext, AbstractSingleBeanDefinitionParser, NamespaceHandlerSupport}
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.scala.beans.factory.config.{SetFactoryBean, SeqFactoryBean}

/**
 * @author Arjen Poutsma
 */
class UtilNamespaceHandler extends NamespaceHandlerSupport {

	private final val SCOPE_ATTRIBUTE: String = "scope"

	def init() {
		registerBeanDefinitionParser("seq", new SeqBeanDefinitionParser())
		registerBeanDefinitionParser("set", new SetBeanDefinitionParser())
	}

	private class SeqBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

		protected override def getBeanClass(element: Element) = classOf[SeqFactoryBean[_]]

		protected override def doParse(element: Element,
		                               parserContext: ParserContext,
		                               builder: BeanDefinitionBuilder) {
			val parsedList: java.util.List[_] = parserContext.getDelegate
					.parseListElement(element, builder.getRawBeanDefinition)
			builder.addConstructorArgValue(parsedList)
			/*
//			val listClass: String = element.getAttribute("list-class")
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

	private class SetBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

		protected override def getBeanClass(element: Element): Class[_] = classOf[SetFactoryBean[_]]

		protected override def doParse(element: Element,
		                               parserContext: ParserContext,
		                               builder: BeanDefinitionBuilder) {
			val parsedSet: java.util.Set[_] = parserContext.getDelegate
					.parseSetElement(element, builder.getRawBeanDefinition)
			builder.addConstructorArgValue(parsedSet)
			/*
				 val setClass: String = element.getAttribute("set-class")
				 if (StringUtils.hasText(setClass)) {
					 builder.addPropertyValue("targetSetClass", setClass)
				 }
	 */
			val scope: String = element.getAttribute(SCOPE_ATTRIBUTE)
			if (StringUtils.hasLength(scope)) {
				builder.setScope(scope)
			}
		}
	}

}