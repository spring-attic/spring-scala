package org.springframework.scala.beans.factory

import java.lang.reflect.Constructor
import org.springframework.stereotype.Component
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter
import org.springframework.beans.factory.annotation.Autowired

/**
 * Simple extension of [[org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor]]
 * that detects the presence of @Autowired or @Inject on a class with a single constructor.
 *
 * If both conditions are met then the constructor will be marked as a candidate constructor,
 * equivalent to marking the constructor with @Autowired in Java.
 *
 * This allows Scala inline constructors to be eligable for autowiring
 * using the following natural syntax.
 *
 * <pre>
 * @Autowired
 * class MyBean(dep: MyDependency) { ... }
 * </pre>
 *
 * @author Stephen Samuel
 **/
@Component
class ConstructorAutowiredAnnotationBeanPostProcessor
  extends InstantiationAwareBeanPostProcessorAdapter
  with org.springframework.core.Ordered {

  override def determineCandidateConstructors(beanClass: Class[_], beanName: String): Array[Constructor[_]] = {
    if (isAutowiredClass(beanClass) && beanClass.getDeclaredConstructors.size == 1) {
      Array(beanClass.getDeclaredConstructors()(0))
    } else {
      null
    }
  }

  def isAutowiredClass(beanClass: Class[_]) = beanClass.isAnnotationPresent(classOf[Autowired])
  def getOrder: Int = Integer.MAX_VALUE
}