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

package org.springframework.scala.beans.factory

import scala.reflect.ClassTag

/**
 * Rich wrapper for [[org.springframework.beans.factory.ListableBeanFactory]], offering
 * Scala-specific methods.
 *
 * @author Arjen Poutsma
 */
trait RichListableBeanFactory extends RichBeanFactory {

	/**
	 * Return the names of beans matching the given type (including subclasses),
	 * judging from either bean definitions or the value of `getObjectType`
	 * in the case of [[org.springframework.beans.factory.FactoryBean]]s.
	 *
	 * @note '''This method introspects top-level beans only.''' It does ''not''
	 *       check nested beans which might match the specified type as well.
	 * @note Does consider objects created by FactoryBeans if the `allowEagerInit` flag is set,
	 *       which means that FactoryBeans will get initialized. If the object created by the
	 *       FactoryBean doesn't match, the raw FactoryBean itself will be matched against the
	 *       type. If `allowEagerInit` is not set, only raw FactoryBeans will be checked
	 *       (which doesn't require initialization of each FactoryBean).
	 * @note Does not consider any hierarchy this factory may participate in.
	 *       Use [[org.springframework.beans.factory.BeanFactoryUtils]]'
	 *       `beanNamesForTypeIncludingAncestors` to include beans in ancestor factories too.
	 * @note Does ''not'' ignore singleton beans that have been registered
	 *       by other means than bean definitions.
	 * @param includeNonSingletons whether to include prototype or scoped beans too
	 *                             or just singletons (also applies to FactoryBeans). Defaults to `true`.
	 * @param allowEagerInit whether to initialize ''lazy-init singletons'' and
	 *                       ''objects created by FactoryBeans'' (or by factory methods with a
	 *                       "factory-bean" reference) for the type check. Note that FactoryBeans need to be
	 *                       eagerly initialized to determine their type: So be aware that passing in "true"
	 *                       for this flag will initialize FactoryBeans and "factory-bean" references. Defaults to
	 *                       `true`.
	 * @tparam T the class or interface to match
	 * @return the names of beans (or objects created by FactoryBeans) matching
	 *         the given object type (including subclasses), or an empty array if none
	 * @see FactoryBean#getObjectType
	 * @see BeanFactoryUtils#beanNamesForTypeIncludingAncestors(ListableBeanFactory, Class, boolean, boolean)
	 */
	def beanNamesForType[T : ClassTag](includeNonSingletons: Boolean = true, allowEagerInit: Boolean = true): Seq[String]

	/**
	 * Return the bean instances that match the given object type (including
	 * subclasses), judging from either bean definitions or the value of
	 * `getObjectType` in the case of [[org.springframework.beans.factory.FactoryBean]]s.
	 *
	 * @note '''This method introspects top-level beans only.''' It does ''not''
	 *       check nested beans which might match the specified type as well.
	 * @note Does consider objects created by FactoryBeans if the `allowEagerInit` flag is set,
	 *       which means that FactoryBeans will get initialized. If the object created by the
	 *       FactoryBean doesn't match, the raw FactoryBean itself will be matched against the
	 *       type. If `allowEagerInit` is not set, only raw FactoryBeans will be checked
	 *       (which doesn't require initialization of each FactoryBean).
	 * @note Does not consider any hierarchy this factory may participate in.
	 *       Use [[org.springframework.beans.factory.BeanFactoryUtils]]'
	 *       `beansOfTypeIncludingAncestors` to include beans in ancestor factories too.
	 * @note Does ''not'' ignore singleton beans that have been registered by other means
	 *       than bean definitions.
	 *       <p>The Map returned by this method should always return bean names and
	 *       corresponding bean instances <i>in the order of definition</i> in the
	 *       backend configuration, as far as possible.
	 * @tparam T the class or interface to match
	 * @return a Map with the matching beans, containing the bean names as
	 *         keys and the corresponding bean instances as values
	 * @throws BeansException if a bean could not be created
	 * @see FactoryBean#getObjectType
	 * @see BeanFactoryUtils#beansOfTypeIncludingAncestors(ListableBeanFactory, Class, boolean, boolean)
	 */
	def beansOfType[T : ClassTag]: Map[String, T] =
		beansOfType[T]()

	/**
	 * Return the bean instances that match the given object type (including
	 * subclasses), judging from either bean definitions or the value of
	 * `getObjectType` in the case of [[org.springframework.beans.factory.FactoryBean]]s.
	 *
	 * @note '''This method introspects top-level beans only.''' It does ''not''
	 *       check nested beans which might match the specified type as well.
	 * @note Does consider objects created by FactoryBeans if the `allowEagerInit` flag is set,
	 *       which means that FactoryBeans will get initialized. If the object created by the
	 *       FactoryBean doesn't match, the raw FactoryBean itself will be matched against the
	 *       type. If `allowEagerInit` is not set, only raw FactoryBeans will be checked
	 *       (which doesn't require initialization of each FactoryBean).
	 * @note Does not consider any hierarchy this factory may participate in.
	 *       Use [[org.springframework.beans.factory.BeanFactoryUtils]]'
	 *       `beansOfTypeIncludingAncestors` to include beans in ancestor factories too.
	 * @note Does ''not'' ignore singleton beans that have been registered by other means
	 *       than bean definitions.
	 *       <p>The Map returned by this method should always return bean names and
	 *       corresponding bean instances <i>in the order of definition</i> in the
	 *       backend configuration, as far as possible.
	 * @param includeNonSingletons whether to include prototype or scoped beans too
	 *                             or just singletons (also applies to FactoryBeans).
	 *                             Defaults to `true`.
	 * @param allowEagerInit whether to initialize <i>lazy-init singletons</i> and
	 *                       <i>objects created by FactoryBeans</i> (or by factory methods with a
	 *                       "factory-bean" reference) for the type check. Note that FactoryBeans need to be
	 *                       eagerly initialized to determine their type: So be aware that passing in "true"
	 *                       for this flag will initialize FactoryBeans and "factory-bean" references.
	 *                       Defaults to `true`.
	 * @tparam T the class or interface to match
	 * @return a Map with the matching beans, containing the bean names as
	 *         keys and the corresponding bean instances as values
	 * @throws BeansException if a bean could not be created
	 * @see FactoryBean#getObjectType
	 * @see BeanFactoryUtils#beansOfTypeIncludingAncestors(ListableBeanFactory, Class, boolean, boolean)
	 */
	def beansOfType[T : ClassTag](includeNonSingletons: Boolean = true, allowEagerInit: Boolean = true) : Map[String, T]

}
