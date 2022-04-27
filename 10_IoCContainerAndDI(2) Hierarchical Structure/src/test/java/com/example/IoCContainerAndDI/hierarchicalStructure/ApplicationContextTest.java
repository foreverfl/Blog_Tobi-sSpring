package com.example.IoCContainerAndDI.hierarchicalStructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import com.example.IoCContainerAndDI.bean.Hello;
import com.example.IoCContainerAndDI.bean.Printer;

public class ApplicationContextTest {
	@Test
	public void contextHierachy() {
		ApplicationContext parent = new GenericXmlApplicationContext("com/example/IocContainerAndDI/parentContext.xml");

		GenericApplicationContext child = new GenericApplicationContext(parent);
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(child);
		reader.loadBeanDefinitions("com/example/IocContainerAndDI/childContext.xml");
		child.refresh();

		Printer printer = child.getBean("printer", Printer.class);
		assertNotNull(printer);

		Hello hello = child.getBean("hello", Hello.class);
		assertNotNull(hello);

		hello.print();
		assertEquals(printer.toString(), "Hello Child");
	}
}
