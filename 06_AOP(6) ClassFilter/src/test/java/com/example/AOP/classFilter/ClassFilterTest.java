package com.example.AOP.classFilter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

public class ClassFilterTest {
	@Test
	public void classNamePointcutAdvisor() {
		@SuppressWarnings("serial")
		NameMatchMethodPointcut classMethodPointcut = new NameMatchMethodPointcut() {
			public ClassFilter getClassFilter() {
				return new ClassFilter() {
					public boolean matches(Class<?> clazz) {
						return clazz.getSimpleName().startsWith("HelloT");
					}
				};
			}
		};

		classMethodPointcut.setMappedName("sayH*");

		checkAdviced(new HelloTarget(), classMethodPointcut, true);

		class HelloWorld extends HelloTarget {
		}
		;
		checkAdviced(new HelloWorld(), classMethodPointcut, false);

		class HelloTaro extends HelloTarget {
		}
		;
		checkAdviced(new HelloTaro(), classMethodPointcut, true);
	}

	private void checkAdviced(Object target, Pointcut pointcut, boolean adviced) {
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		pfBean.setTarget(target);
		pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));
		Hello proxiedHello = (Hello) pfBean.getObject();

		if (adviced) {
			assertEquals(proxiedHello.sayHello("Taro"), "HELLO TARO");
			assertEquals(proxiedHello.sayHi("Taro"), "HI TARO");
			assertEquals(proxiedHello.sayThankYou("Taro"), "Thank You Taro");
		} else {
			assertEquals(proxiedHello.sayHello("Taro"), "Hello Taro");
			assertEquals(proxiedHello.sayHi("Taro"), "Hi Taro");
			assertEquals(proxiedHello.sayThankYou("Taro"), "Thank You Taro");
		}
	}

	static class UppercaseAdvice implements MethodInterceptor {
		public Object invoke(MethodInvocation invocation) throws Throwable {
			String ret = (String) invocation.proceed();
			return ret.toUpperCase();
		}
	}

	static interface Hello {
		String sayHello(String name);

		String sayHi(String name);

		String sayThankYou(String name);
	}

	static class HelloTarget implements Hello {
		public String sayHello(String name) {
			return "Hello " + name;
		}

		public String sayHi(String name) {
			return "Hi " + name;
		}

		public String sayThankYou(String name) {
			return "Thank You " + name;
		}
	}
}
