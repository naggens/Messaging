package com.rf.messaging.consumer.xml.parser;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class MessageConsumerBeanDefinitionParser implements
		BeanDefinitionParser {

	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		String destinationName = element.getAttribute("destinationName");
		String destinationType = element.getAttribute("destinationType");
		String listenerClass = element.getAttribute("listenerClass");
		String enableListener = element.getAttribute("enableListener");
		
		String concurrentConsumers = element.getAttribute("concurrentConsumers");
		String maxConcurrentConsumers = element.getAttribute("maxConcurrentConsumers");
		
		BeanDefinitionRegistry registry = parserContext.getRegistry();
		BeanDefinitionBuilder messageConsumerBuilder = BeanDefinitionBuilder.genericBeanDefinition(MessageConsumerPostProcessor.class);
		messageConsumerBuilder.addConstructorArgValue(destinationName);
		messageConsumerBuilder.addConstructorArgValue(destinationType);
		messageConsumerBuilder.addConstructorArgValue(listenerClass);
		messageConsumerBuilder.addConstructorArgValue(enableListener);
		messageConsumerBuilder.addConstructorArgValue(concurrentConsumers);
		messageConsumerBuilder.addConstructorArgValue(maxConcurrentConsumers);
		registry.registerBeanDefinition(MessageConsumerPostProcessor.class.getName() + destinationName, messageConsumerBuilder.getBeanDefinition());
		
		return null;
	}

}
