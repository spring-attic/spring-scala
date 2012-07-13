/*
 * Copyright 2011-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;

/**
 * Implementation of the {@link BeanInfoFactory} that creates {@link ExtendedBeanInfo}
 * instances.
 *
 * @author Arjen Poutsma
 */
public class ExtendedBeanInfoFactory implements BeanInfoFactory {

	/**
	 * Returns {@code true}.
	 */
	public boolean supports(Class<?> beanClass) {
		return true;
	}

	public BeanInfo getBeanInfo(Class<?> beanClass) throws IntrospectionException {
		return new ExtendedBeanInfo(Introspector.getBeanInfo(beanClass));
	}
}
