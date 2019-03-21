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

import java.awt.Image;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.EventSetDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

/**
 * Implementation of {@link BeanInfo} for Scala classes. Decorates a standard {@link
 * BeanInfo} object by including Scala setter methods (ending in {@code _$eq} in the
 * collection of {@linkplain #getPropertyDescriptors() property descriptors}.
 *
 * @author Arjen Poutsma
 */
class ScalaBeanInfo implements BeanInfo {

	private static final Log logger = LogFactory.getLog(ScalaBeanInfo.class);

	private static final String SCALA_SETTER_SUFFIX = "_$eq";

	private final BeanInfo delegate;

	private final PropertyDescriptor[] propertyDescriptors;

	public ScalaBeanInfo(Class<?> beanClass) throws IntrospectionException {
		this(Introspector.getBeanInfo(beanClass));
	}

	public ScalaBeanInfo(BeanInfo delegate) throws IntrospectionException {
		Assert.notNull(delegate, "'delegate' must not be null");
		this.delegate = delegate;
		this.propertyDescriptors = initPropertyDescriptors(delegate);
	}

	private static PropertyDescriptor[] initPropertyDescriptors(BeanInfo beanInfo) {
		Map<String, PropertyDescriptor> propertyDescriptors =
				new TreeMap<String, PropertyDescriptor>(new PropertyNameComparator());
		for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
			propertyDescriptors.put(pd.getName(), pd);
		}

		for (MethodDescriptor md : beanInfo.getMethodDescriptors()) {
			Method method = md.getMethod();

			if (ReflectionUtils.isObjectMethod(method)) {
				continue;
			}
			if (isScalaSetter(method)) {
				addScalaSetter(propertyDescriptors, method);
			}
			else if (isScalaGetter(method)) {
				addScalaGetter(propertyDescriptors, method);
			}
		}
		return propertyDescriptors.values()
				.toArray(new PropertyDescriptor[propertyDescriptors.size()]);
	}

	private static boolean isScalaGetter(Method method) {
		return method.getParameterTypes().length == 0 &&
				!method.getReturnType().equals(Void.TYPE) &&
				!(method.getName().startsWith("get") ||
						method.getName().startsWith("is"));
	}

	public static boolean isScalaSetter(Method method) {
		return method.getParameterTypes().length == 1 &&
				method.getReturnType().equals(Void.TYPE) &&
				method.getName().endsWith(SCALA_SETTER_SUFFIX);
	}

	private static void addScalaSetter(Map<String, PropertyDescriptor> propertyDescriptors,
	                                   Method writeMethod) {
		String propertyName = writeMethod.getName().substring(0,
				writeMethod.getName().length() - SCALA_SETTER_SUFFIX.length());

		PropertyDescriptor pd = propertyDescriptors.get(propertyName);
		if (pd != null && pd.getWriteMethod() == null) {
			try {
				pd.setWriteMethod(writeMethod);
			}
			catch (IntrospectionException ex) {
				logger.debug("Could not add write method [" + writeMethod + "] for " +
						"property [" + propertyName + "]: " + ex.getMessage());
			}
		}
		else if (pd == null) {
			try {
				pd = new PropertyDescriptor(propertyName, null, writeMethod);
				propertyDescriptors.put(propertyName, pd);
			}
			catch (IntrospectionException ex) {
				logger.debug("Could not create new PropertyDescriptor for " +
						"writeMethod [" + writeMethod + "] property [" + propertyName +
						"]: " + ex.getMessage());
			}
		}
	}

	private static void addScalaGetter(Map<String, PropertyDescriptor> propertyDescriptors,
	                                   Method readMethod) {
		String propertyName = readMethod.getName();

		PropertyDescriptor pd = propertyDescriptors.get(propertyName);
		if (pd != null && pd.getReadMethod() == null) {
			try {
				pd.setReadMethod(readMethod);
			}
			catch (IntrospectionException ex) {
				logger.debug("Could not add read method [" + readMethod + "] for " +
						"property [" + propertyName + "]: " + ex.getMessage());
			}
		}
		else if (pd == null) {
			try {
				pd = new PropertyDescriptor(propertyName, readMethod, null);
				propertyDescriptors.put(propertyName, pd);
			}
			catch (IntrospectionException ex) {
				logger.debug("Could not create new PropertyDescriptor for " +
						"readMethod [" + readMethod + "] property [" + propertyName +
						"]: " + ex.getMessage());
			}
		}
	}

	public PropertyDescriptor[] getPropertyDescriptors() {
		return propertyDescriptors;
	}

	public BeanInfo[] getAdditionalBeanInfo() {
		return delegate.getAdditionalBeanInfo();
	}

	public BeanDescriptor getBeanDescriptor() {
		return delegate.getBeanDescriptor();
	}

	public int getDefaultEventIndex() {
		return delegate.getDefaultEventIndex();
	}

	public int getDefaultPropertyIndex() {
		return delegate.getDefaultPropertyIndex();
	}

	public EventSetDescriptor[] getEventSetDescriptors() {
		return delegate.getEventSetDescriptors();
	}

	public Image getIcon(int iconKind) {
		return delegate.getIcon(iconKind);
	}

	public MethodDescriptor[] getMethodDescriptors() {
		return delegate.getMethodDescriptors();
	}

	/**
	 * Sorts property names alphanumerically to emulate the behavior of {@link
	 * java.beans.BeanInfo#getPropertyDescriptors()}.
	 */
	private static class PropertyNameComparator implements Comparator<String> {

		public int compare(String left, String right) {
			byte[] leftBytes = left.getBytes();
			byte[] rightBytes = right.getBytes();

			for (int i = 0; i < left.length(); i++) {
				if (right.length() == i) {
					return 1;
				}
				int result = leftBytes[i] - rightBytes[i];
				if (result != 0) {
					return result;
				}
			}
			return left.length() - right.length();
		}
	}

}
