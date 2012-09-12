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

package org.springframework.scala.beans;

import java.beans.IntrospectionException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;

/** @author Arjen Poutsma */
public class ScalaBeanInfoFactoryTest {

	@Test
	public void registration() {
		DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
		BeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);

		reader.loadBeanDefinitions(
				new ClassPathResource("scalaBeanInfoFactoryIntegrationTest.xml",
						getClass()));

		ScalaBean bean = beanFactory.getBean("scalaBean", ScalaBean.class);

		assertEquals("Bar", bean.readWrite());
		assertEquals("Bar", bean.getBeanProperty());
	}

	@Test
	public void supports() throws IntrospectionException {
		ScalaBeanInfoFactory factory = new ScalaBeanInfoFactory();
		assertNotNull(factory.getBeanInfo(ScalaBean.class));
		assertNull(factory.getBeanInfo(getClass()));
	}

}
