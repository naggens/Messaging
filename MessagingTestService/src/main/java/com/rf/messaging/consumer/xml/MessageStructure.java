package com.rf.messaging.consumer.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.rf.messaging.consumer.domain.AccountDO;
import com.rf.messaging.consumer.domain.OrderDO;
import com.thoughtworks.xstream.XStream;

@XmlRootElement
public class MessageStructure {
	private String destinationName;
	private String destinationType;
	private String messageHeader;
	
	@XmlElement(name = "object", required = false)
	private Object message;
	
	@XmlElement(name = "account", required = false)
	private AccountDO account;
	
	@XmlElement(name = "order", required = false)
	private OrderDO order;
	
	public OrderDO getOrder() {
		return order;
	}
	public void setOrder(OrderDO order) {
		this.order = order;
	}
	public AccountDO getAccount() {
		return account;
	}
	public void setAccount(AccountDO account) {
		this.account = account;
	}
	public String getMessageHeader() {
		return messageHeader;
	}
	public void setMessageHeader(String messageHeader) {
		this.messageHeader = messageHeader;
	}	
	public String getDestinationName() {
		return destinationName;
	}
	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}
	public String getDestinationType() {
		return destinationType;
	}
	public void setDestinationType(String destinationType) {
		this.destinationType = destinationType;
	}
	public Object getMessage() {
		return message;
	}
	public void setMessage(Object message) {
		this.message = message;
	}
	@Override
	public String toString() {
		XStream stream = new XStream();
		return stream.toXML(this);
	}
	
	
	
}
