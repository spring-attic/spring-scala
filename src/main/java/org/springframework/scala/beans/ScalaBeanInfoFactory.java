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

package org.springframework.scala.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.lang.reflect.Method;

import org.springframework.beans.BeanInfoFactory;
import org.springframework.core.Ordered;

/**
 * Implementation of the {@code BeanInfoFactory} interface for Scala beans.
 *
 * <p>The {@link BeanInfo} returned from {@link #getBeanInfo(Class)} includes {@linkplain
 * java.beans.BeanInfo#getPropertyDescriptors() property descriptors} for "normal"
 * JavaBean properties as well as Scala properties (i.e. setter methods that end with
 * {@code _$eq}).
 *
 * @author Arjen Poutsma
 */
public class ScalaBeanInfoFactory implements BeanInfoFactory, Ordered {

	@Override
	public BeanInfo getBeanInfo(Class<?> beanClass) throws IntrospectionException {
		return supports(beanClass) ? new ScalaBeanInfo(beanClass) : null;

	}

	private boolean supports(Class<?> beanClass) {
		for (Method method : beanClass.getMethods()) {
			if (ScalaBeanInfo.isScalaSetter(method)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE - 1000;
	}
}
