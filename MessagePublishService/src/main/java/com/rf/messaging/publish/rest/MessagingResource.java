package com.rf.messaging.publish.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;

import com.rf.messaging.publish.domain.AccountDO;
import com.rf.messaging.publish.domain.MessageStructure;
import com.rf.messaging.publish.domain.OrderDO;
import com.rf.messaging.publish.producer.MessageSender;

@Path("/messaging")
public class MessagingResource {
	
	@Autowired
	private MessageSender messageSender;
	
	public MessageSender getMessageSender() {
		return messageSender;
	}

	public void setMessageSender(MessageSender messageSender) {
		this.messageSender = messageSender;
	}

	@POST
	@Path("/publish")
	@Consumes(MediaType.APPLICATION_JSON)	
	public void publishMessage(MessageStructure messageStructure){		
		messageSender.publishMessage(messageStructure.getDestinationName(), messageStructure.getDestinationType(), "This is test message");
		System.out.println("Request: " + messageStructure.toString());
	}
	
	@POST
	@Path("/publish/account")
	@Consumes(MediaType.APPLICATION_JSON)	
	public void publishAcctMessage(AccountDO account){		
		messageSender.publishAcctMessage(account);
		System.out.println("Request: " + account.toString());
	}
	
	@POST
	@Path("/publish/order")
	@Consumes(MediaType.APPLICATION_JSON)	
	public void publishOrderMessage(OrderDO order){		
		messageSender.publishOrderMessage(order);;
		System.out.println("Request: " + order.toString());
	}
}
