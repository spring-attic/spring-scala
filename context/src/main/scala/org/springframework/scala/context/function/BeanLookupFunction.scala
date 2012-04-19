package org.springframework.scala.context.function

/**
 * @author Arjen Poutsma
 */
trait BeanLookupFunction[T] extends Function0[T] {

	/**
	 * Registers an initialization function.
	 *
	 * @param initFunction the initialization function
	 */
	def init(initFunction: T => Unit): BeanLookupFunction[T]

	/**
	 * Registers a destruction function.
	 *
	 * @param destroyFunction the destruction function
	 */
	def destroy(destroyFunction: T => Unit): BeanLookupFunction[T]

}
