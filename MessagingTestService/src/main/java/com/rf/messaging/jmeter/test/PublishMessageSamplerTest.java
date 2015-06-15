package com.rf.messaging.jmeter.test;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class PublishMessageSamplerTest {
	@BeforeClass
    public static void oneTimeSetUp() {        
         System.out.println("@BeforeClass - for junit testMessagePublisher");
    }

    @AfterClass
    public static void oneTimeTearDown() {       
         System.out.println("@AfterClass - for junit testMessagePublisher");
    } 
    
    @Before
    public void setUp() {
        System.out.println("@Before - testMessagePublisher setUp");
    }
 
    @After
    public void tearDown() {
        System.out.println("@After - testMessagePublisher tearDown");
    }

    @Test
    public void testMessagePublisher() {       
        System.out.println("@Test - testMessagePublisher method");
    }
}
