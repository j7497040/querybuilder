package com.icando.querybuilder;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.icando.querybuilder.subquery.OrderBuilder;

import static org.hamcrest.CoreMatchers.*;

public class OrderBuilderTest {
    
    @Before
    public void setUp() throws Exception {
    }
    
    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void testItems() {
        OrderBuilder builder = new OrderBuilder();
        assertThat(builder.getSql(), is(""));
        
        builder.items("name asc", "age desc");
        assertThat(builder.getSql(), is("name asc, age desc"));
        
    }
    
    @Test
    public void testNamesByAsc() {
        OrderBuilder builder = new OrderBuilder();
        
        builder.asc("name", "age");
        assertThat(builder.getSql(), is("name asc, age asc"));
    }
    
    @Test
    public void testNamesByDesc() {
        OrderBuilder builder = new OrderBuilder();
        
        builder.desc("name", "age");
        assertThat(builder.getSql(), is("name desc, age desc"));
    }
    
    @Test
    public void testName() {
        OrderBuilder builder = new OrderBuilder();
        
        builder.order("name", true);
        builder.order("age", false);
        assertThat(builder.getSql(), is("name asc, age desc"));
    }
}
