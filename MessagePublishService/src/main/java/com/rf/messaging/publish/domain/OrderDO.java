package com.rf.messaging.publish.domain;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.thoughtworks.xstream.XStream;

@XmlRootElement
public class OrderDO implements Serializable {
	private String number;
	private String name;
	
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		XStream stream = new XStream();
		return stream.toXML(this);
	}
	
}
