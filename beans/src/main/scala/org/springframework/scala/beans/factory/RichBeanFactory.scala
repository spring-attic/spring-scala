package org.springframework.scala.beans.factory

import org.springframework.beans.factory.BeanFactory

/**
 * @author Arjen Poutsma
 */
class RichBeanFactory(beanFactory: BeanFactory) {

	/**
	 * Return the bean instance that uniquely matches the given object type, if any.
	 *
	 * @param manifest an implicit ``Manifest`` representing the type of the specified
	 *                 type parameter.
	 * @return an instance of the single bean matching the required type
	 * @throws NoSuchBeanDefinitionException if there is not exactly one matching bean found
	 */
	def getBean[T](implicit manifest: Manifest[T]): T = {
		beanFactory.getBean(manifest.erasure.asInstanceOf[Class[T]])
	}

	/**
	 * Return an instance, which may be shared or independent, of the specified
	 * bean.
	 *
	 * Translates aliases back to the corresponding canonical bean name.
	 * Will ask the parent factory if the bean cannot be found in this factory
	 * instance.
	 *
	 * @param name the name of the bean to retrieve
	 * @param manifest an implicit ``Manifest`` representing the type of the specified
	 *                 type parameter.
	 * @return an instance of the bean
	 * @throws NoSuchBeanDefinitionException if there's no such bean definition
	 * @throws BeanNotOfRequiredTypeException if the bean is not of the required type
	 * @throws BeansException if the bean could not be created
	 */
	def getBean[T](name: String)(implicit manifest: Manifest[T]): T = {
		beanFactory.getBean(name, manifest.erasure.asInstanceOf[Class[T]])
	}

}

object RichBeanFactory {

	def apply(beanFactory: BeanFactory): RichBeanFactory = new
					RichBeanFactory(beanFactory)

	implicit def enrichBeanFactory(beanFactory: BeanFactory): RichBeanFactory =
		apply(beanFactory)

}
