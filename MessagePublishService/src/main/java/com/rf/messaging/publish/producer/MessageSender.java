package com.rf.messaging.publish.producer;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.commons.lang.StringUtils;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.rf.messaging.consumer.domain.AccountDO;
import com.rf.messaging.consumer.domain.OrderDO;

public class MessageSender {
	private JmsTemplate jmsTemplate;

	public JmsTemplate getJmsTemplate() {
		return jmsTemplate;
	}

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	} 
	
	public void publishAcctMessage(final AccountDO message){
		publishMessage("AccountTopic", "T", message);
	}
	
	public void publishOrderMessage(final OrderDO message){
		publishMessage("OrderTopic", "T", message);
	}
	
	public void publishMessage(String destinationName, String destinationType, final Serializable message){
		ActiveMQDestination destination = null;
		if(StringUtils.equals("Q", destinationType)){
			destination = new ActiveMQQueue(destinationName);
		}else if(StringUtils.equals("T", destinationType)){
			destination = new ActiveMQTopic(destinationName);
		}
		
		jmsTemplate.setDefaultDestination(destination);		
		jmsTemplate.send(new MessageCreator() {
			
			@Override
			public Message createMessage(Session session) throws JMSException {				
				return session.createObjectMessage(message);
			}
		});
	}
}
