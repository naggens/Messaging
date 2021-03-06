package com.rf.messaging.consumer.xml.parser;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.jms.connection.SingleConnectionFactory;

import com.rf.messaging.consumer.xml.processor.DefaultMessageListenerContainerAdaptar;

public class DurableConsumerPostProcessor implements BeanFactoryPostProcessor {
	
	private static Logger log = Logger.getLogger(DurableConsumerPostProcessor.class);
	private String destinationName;
	private String destinationType;
	private String listenerClass;
	private boolean enableListener;
	private String enableListValue;
	
	private String consumerName;
	private String clientId;
	
	private int concurrentConsumers = 1;
	private int maxConcurrentConsumers = 1;
	
	public DurableConsumerPostProcessor(String destinationName, String destinationType, String listenerClass, String enableListener, String consumerName, String clientId, String concurrentConsumers, String maxConcurrentConsumers){
		this.destinationName = destinationName;
		this.destinationType = destinationType;
		this.listenerClass = listenerClass;
		this.enableListener = Boolean.valueOf(enableListener);
		this.enableListValue = enableListener;
		this.consumerName = consumerName;
		this.clientId = clientId;
		try{
			this.concurrentConsumers = Integer.parseInt(concurrentConsumers);
		}catch(Exception ex){
			log.error("Illegal value set for concurrentConsumers. value is " + concurrentConsumers, ex);
			this.concurrentConsumers = 1;
		}
		try{
			this.maxConcurrentConsumers = Integer.parseInt(maxConcurrentConsumers);
		}catch(Exception ex){
			log.error("Illegal value set for maxConcurrentConsumers. value is " + maxConcurrentConsumers, ex);
			this.maxConcurrentConsumers = 1;
		}
	}
	
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
			throws BeansException {
		if(this.enableListener){
			configureMessageConsumer(beanFactory);
		}else{
			log.info("Message Listener for Destination " + this.destinationName + " and destination type " + this.destinationType + " is disabled.");
			log.info("Vlaue of attribute enableListener is " + this.enableListValue);		
		}
	}
	
	private void configureMessageConsumer(ConfigurableListableBeanFactory beanFactory){
		try {
			
			SingleConnectionFactory jmsFactory = (SingleConnectionFactory)beanFactory.getBean("singleConnectionFactory");
			BeanDefinitionBuilder listenerBuilder = BeanDefinitionBuilder.genericBeanDefinition(DefaultMessageListenerContainerAdaptar.class);
			listenerBuilder.addConstructorArgValue(jmsFactory);
			listenerBuilder.addConstructorArgValue(this.destinationName);
			if(log.isDebugEnabled()){
				log.debug("Message Consumer name is: " + this.destinationName);
				log.debug("Message Consumer type is: " + this.destinationType);
			}
			
			listenerBuilder.addConstructorArgValue(this.destinationType);
			
			listenerBuilder.addConstructorArgValue(Class.forName(this.listenerClass).newInstance());
			//SESSION_TRANSACTED(0), AUTO_ACKNOWLEDGE(1), CLIENT_ACKNOWLEDGE(2),  DUPS_OK_ACKNOWLEDGE(3);
			listenerBuilder.addConstructorArgValue(1);
			
			listenerBuilder.addConstructorArgValue(consumerName);
			listenerBuilder.addConstructorArgValue(clientId);
			
			listenerBuilder.addConstructorArgValue(concurrentConsumers);
			listenerBuilder.addConstructorArgValue(maxConcurrentConsumers);
			
			BeanDefinition beanDefinition = listenerBuilder.getBeanDefinition();	
			BeanDefinitionRegistry beanDefinitionRegistry = null;
			if(beanFactory instanceof BeanDefinitionRegistry){
				beanDefinitionRegistry = (BeanDefinitionRegistry)beanFactory;	
				beanDefinitionRegistry.registerBeanDefinition(this.destinationName + "Bean", beanDefinition);
			}else{
				log.info("Message Listener is not created for: " + this.destinationName);
			}
			
			
		} catch (InstantiationException ex) {
			log.error("InstantiationException while creating Listener bean for Message Consumer in createMessageConsumer: ", ex);
			throw new RuntimeException(ex);
		} catch (IllegalAccessException ex) {
			log.error("IllegalAccessException while creating Listener bean for Message Consumer in createMessageConsumer: ", ex);
			throw new RuntimeException(ex);
		} catch (ClassNotFoundException ex) {
			log.error("ClassNotFoundException while creating Listener bean for Message Consumer in createMessageConsumer: ", ex);
			throw new RuntimeException(ex);
		}
	}

}
