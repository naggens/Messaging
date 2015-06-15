package com.rf.messaging.consumer.xml.processor;

import javax.jms.MessageListener;

import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jms.JmsException;
import org.springframework.jms.connection.SingleConnectionFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.util.Assert;

public class DefaultMessageListenerContainerAdaptar implements InitializingBean, DisposableBean, ApplicationContextAware{
	private static final Log log = LogFactory.getLog(DefaultMessageListenerContainerAdaptar.class);
	private DefaultMessageListenerContainer defaultMsgListCont;
	private MessageListener messageListener;
	private SingleConnectionFactory jmsFactory;
	private String destinationName;
	private String destinationType;
	private int acknowledgeMode;
	private ApplicationContext applicationContext;
	private String durableSubscriptionName;
	private String clientId;
	
	private int concurrentConsumers = 1;
	private int maxConcurrentConsumers = 1;
	
	public DefaultMessageListenerContainerAdaptar(SingleConnectionFactory jmsFactory, String destinationName, String destinationType, MessageListener messageConsumerImpl, int acknowledgeMode){
		Assert.notNull(jmsFactory, "jmsFactory cannot be null");
		Assert.notNull(destinationName, "destinationName cannot be null");
		Assert.notNull(destinationType, "destinationType cannot be null");
		Assert.notNull(messageConsumerImpl, "Message Consumer Object cannot be null");
		Assert.notNull(acknowledgeMode, "Session Acknowledge Mode cannot be null");
		this.jmsFactory = jmsFactory ;
		this.destinationName = destinationName;
		this.destinationType = destinationType;
		this.messageListener = messageConsumerImpl;
		this.acknowledgeMode = acknowledgeMode;
	}
	
	public DefaultMessageListenerContainerAdaptar(SingleConnectionFactory jmsFactory, String destinationName, String destinationType, MessageListener messageConsumerImpl, int acknowledgeMode, String durableSubscriptionName, String clientId, int concurrentConsumers, int maxConcurrentConsumers){
		Assert.notNull(jmsFactory, "jmsFactory cannot be null");
		Assert.notNull(destinationName, "destinationName cannot be null");
		Assert.notNull(destinationType, "destinationType cannot be null");
		Assert.notNull(messageConsumerImpl, "Message Consumer Object cannot be null");
		Assert.notNull(acknowledgeMode, "Session Acknowledge Mode cannot be null");
		this.jmsFactory = jmsFactory;
		this.destinationName = destinationName;
		this.destinationType = destinationType;
		this.messageListener = messageConsumerImpl;
		this.acknowledgeMode = acknowledgeMode;
		this.durableSubscriptionName = durableSubscriptionName;
		this.clientId = clientId;
		this.concurrentConsumers = concurrentConsumers;
		this.maxConcurrentConsumers = maxConcurrentConsumers;
	}
		
	public void startActivities(){
		defaultMsgListCont = new DefaultMessageListenerContainer();
		defaultMsgListCont.setConnectionFactory(jmsFactory);
		defaultMsgListCont.setSessionAcknowledgeMode(acknowledgeMode);
		
		if(StringUtils.isNotBlank(durableSubscriptionName)){
			if(log.isDebugEnabled()){log.debug("Creating durable subscriber with durableSubscriptionName: " + durableSubscriptionName);}
			defaultMsgListCont.setDurableSubscriptionName(durableSubscriptionName);
			//defaultMsgListCont.setClientId(this.clientId);
			jmsFactory.setClientId(this.clientId);
			defaultMsgListCont.setSubscriptionDurable(true);
		}
		
		ActiveMQDestination destination;
		if(StringUtils.equals("T", this.destinationType)){
			destination = new ActiveMQTopic(destinationName);			
		}else{
			destination = new ActiveMQQueue(destinationName);
		}
		
		defaultMsgListCont.setMessageListener(messageListener);
		defaultMsgListCont.setDestination(destination);
		
		defaultMsgListCont.setConcurrentConsumers(concurrentConsumers);
		defaultMsgListCont.setMaxConcurrentConsumers(maxConcurrentConsumers);
		
		if(log.isDebugEnabled()){
			log.debug("concurrentConsumers for destination " + this.destinationName + " is " + concurrentConsumers);
			log.debug("maxConcurrentConsumers for destination " + this.destinationName + " is " + maxConcurrentConsumers);
		}
		
		try{
			defaultMsgListCont.initialize();
			defaultMsgListCont.start();
			log.info("Message Listener Container started for " + destinationName);
			
		}catch(RuntimeException ex){
			log.error("Message Listener Container did not start for " + destinationName, ex);
		}
	}
	
	public void endActivities(){
		try{
			defaultMsgListCont.stop();
			defaultMsgListCont.shutdown();
			log.info("Message Listener Container Stoped for " + destinationName);
		}catch(JmsException ex){
			log.error("Error while stoping message listener container", ex);
			
		}
	}

	public void afterPropertiesSet() {
		startActivities();		
	}

	public void destroy() {
		endActivities();
	}

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;		
	}
	
}
