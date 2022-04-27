package com.example.IoCContainerAndDI.IoCContainer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.context.support.StaticApplicationContext;

import com.example.IoCContainerAndDI.bean.Hello;
import com.example.IoCContainerAndDI.bean.StringPrinter;

public class ApplicationContextTest {

	@Test
	public void registerBean() {
		StaticApplicationContext ac = new StaticApplicationContext();

		// registering a bean
		ac.registerSingleton("hello1", Hello.class);

		Hello hello1 = ac.getBean("hello1", Hello.class);

		assertNotNull(hello1);

		// registering a bean with the 'BeanDefinition'
		BeanDefinition helloDef = new RootBeanDefinition(Hello.class);
		helloDef.getPropertyValues().addPropertyValue("name", "Spring");
		ac.registerBeanDefinition("hello2", helloDef);

		Hello hello2 = ac.getBean("hello2", Hello.class);

		assertEquals(hello2.sayHello(), "Hello Spring");
		assertNotEquals(hello1, hello2);
		assertEquals(ac.getBeanFactory().getBeanDefinitionCount(), 2);

		ac.close();
	}

	@Test
	public void registerBeanWithDependency() {
		StaticApplicationContext ac = new StaticApplicationContext();

		ac.registerBeanDefinition("printer", new RootBeanDefinition(StringPrinter.class));

		BeanDefinition helloDef = new RootBeanDefinition(Hello.class);
		helloDef.getPropertyValues().addPropertyValue("name", "Spring");
		helloDef.getPropertyValues().addPropertyValue("printer", new RuntimeBeanReference("printer"));
		ac.registerBeanDefinition("hello", helloDef);

		Hello hello = ac.getBean("hello", Hello.class);
		hello.print();

		assertEquals(ac.getBean("printer").toString(), "Hello Spring");

		ac.close();
	}

	@Test
	public void genericApplicationContext() {
		GenericApplicationContext ac = new GenericApplicationContext();

		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(ac);
		reader.loadBeanDefinitions("com/example/IoCContainerAndDI/genericApplicationContext.xml");

		ac.refresh();

		Hello hello = ac.getBean("hello", Hello.class);
		hello.print();

		assertEquals(ac.getBean("printer").toString(), "Hello Spring");
	}

	@Test
	public void genericXmlApplicationContext() {
		GenericApplicationContext ac = new GenericXmlApplicationContext(
				"com/example/IoCContainerAndDI/genericApplicationContext.xml");

		Hello hello = ac.getBean("hello", Hello.class);
		hello.print();

		assertEquals(ac.getBean("printer").toString(), "Hello Spring");

		ac.close();

	}
}
