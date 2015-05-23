package com.rf.messaging.consumer.xml;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

import com.rf.messaging.consumer.xml.parser.DurableConsumerBeanDefinitionParser;
import com.rf.messaging.consumer.xml.parser.MessageConsumerBeanDefinitionParser;

public class MessagingNamespaceHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {
		registerBeanDefinitionParser("messageConsumer", new MessageConsumerBeanDefinitionParser());
		registerBeanDefinitionParser("durableConsumer", new DurableConsumerBeanDefinitionParser());
	}

}
