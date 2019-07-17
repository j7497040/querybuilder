package com.icando.querybuilder;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.icando.querybuilder.subquery.BooleanBuilder;


public class BooleanBuilderTest {
    
    @Before
    public void setUp() throws Exception {
    }
    
    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void testOr() {
        BooleanBuilder builder = new BooleanBuilder();
        assertThat(builder.getSql(), is(""));
        assertThat(builder.getParams().length, is(0));
        
        builder.or("name = :name", "Hitachi");
        assertThat(builder.getSql(), is("(name = :name)"));
        assertThat(builder.getParams().length, is(1));
        
        builder.or("age = :age", 19);
        assertThat(builder.getSql(), is("(name = :name) or (age = :age)"));
        assertThat(builder.getParams().length, is(2));
    
    }
    
    @Test
    public void testOrNot() {
        BooleanBuilder builder = new BooleanBuilder();
        assertThat(builder.getSql(), is(""));
        assertThat(builder.getParams().length, is(0));
        
        builder.orNot("name = :name", "Hitachi");
        assertThat(builder.getSql(), is("(name = :name)"));
        assertThat(builder.getParams().length, is(1));
        
        builder.orNot("age = :age", 19);
        assertThat(builder.getSql(), is("(name = :name) or not (age = :age)"));
        assertThat(builder.getParams().length, is(2));
    
    }
    
    @Test
    public void testOr_builder() {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and("name = :name", "Hitachi");
        assertThat(builder.getSql(), is("(name = :name)"));
        assertThat(builder.getParams().length, is(1));
        
        BooleanBuilder sub = new BooleanBuilder();
        sub.or("status = :status1", 1);
        sub.or("status = :status2", 2);
        builder.and(sub);
        
        assertThat(builder.getSql(), is("(name = :name) and ((status = :status1) or (status = :status2))"));
        assertThat(builder.getParams().length, is(3));
        
    
    }
    
    @Test
    public void testOrNot_builder() {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and("name = :name", "Hitachi");
        assertThat(builder.getSql(), is("(name = :name)"));
        assertThat(builder.getParams().length, is(1));
        
        BooleanBuilder sub = new BooleanBuilder();
        sub.orNot("status = :status1", 1);
        sub.orNot("status = :status2", 2);
        builder.andNot(sub);
        
        assertThat(builder.getSql(), is("(name = :name) and not ((status = :status1) or not (status = :status2))"));
        assertThat(builder.getParams().length, is(3));
        
    
    }
    
    @Test
    public void testAnd() {
        BooleanBuilder builder = new BooleanBuilder();
        assertThat(builder.getSql(), is(""));
        assertThat(builder.getParams().length, is(0));
        
        builder.and("name = :name", "Hitachi");
        assertThat(builder.getSql(), is("(name = :name)"));
        assertThat(builder.getParams().length, is(1));
        
        builder.and("age = :age", 19);
        assertThat(builder.getSql(), is("(name = :name) and (age = :age)"));
        assertThat(builder.getParams().length, is(2));
        
    }
    
    @Test
    public void testAndNot() {
        BooleanBuilder builder = new BooleanBuilder();
        assertThat(builder.getSql(), is(""));
        assertThat(builder.getParams().length, is(0));
        
        builder.andNot("name = :name", "Hitachi");
        assertThat(builder.getSql(), is("(name = :name)"));
        assertThat(builder.getParams().length, is(1));
        
        builder.andNot("age = :age", 19);
        assertThat(builder.getSql(), is("(name = :name) and not (age = :age)"));
        assertThat(builder.getParams().length, is(2));
        
    }
    
    @Test
    public void testAnd_builder() {
        BooleanBuilder builder = new BooleanBuilder();
        builder.or("name = :name", "Hitachi");
        assertThat(builder.getSql(), is("(name = :name)"));
        assertThat(builder.getParams().length, is(1));
        
        BooleanBuilder sub = new BooleanBuilder();
        sub.and("status = :status1", 1);
        sub.and("status = :status2", 2);
        builder.or(sub);
        
        assertThat(builder.getSql(), is("(name = :name) or ((status = :status1) and (status = :status2))"));
        assertThat(builder.getParams().length, is(3));
        
    
    }
    
    @Test
    public void testAndNot_builder() {
        BooleanBuilder builder = new BooleanBuilder();
        builder.or("name = :name", "Hitachi");
        assertThat(builder.getSql(), is("(name = :name)"));
        assertThat(builder.getParams().length, is(1));
        
        BooleanBuilder sub = new BooleanBuilder();
        sub.andNot("status = :status1", 1);
        sub.andNot("status = :status2", 2);
        builder.orNot(sub);
        
        assertThat(builder.getSql(), is("(name = :name) or not ((status = :status1) and not (status = :status2))"));
        assertThat(builder.getParams().length, is(3));
        
    
    }
    
    @Test
    public void testOrByName() {
        BooleanBuilder builder = new BooleanBuilder();
        
        builder.orByName("name", "=", "Hitachi");
        assertThat(builder.getSql(), is("(name = :name_0)"));
        
        builder.orByName("age", "=", 19);
        assertThat(builder.getSql(), is("(name = :name_0) or (age = :age_1)"));
    }
    
    @Test
    public void testOrNotByName() {
        BooleanBuilder builder = new BooleanBuilder();
        
        builder.orNotByName("name", "=", "Hitachi");
        assertThat(builder.getSql(), is("(name = :name_0)"));
        
        builder.orNotByName("age", "=", 19);
        assertThat(builder.getSql(), is("(name = :name_0) or not (age = :age_1)"));
    }
    
    @Test
    public void testAndByName() {
        BooleanBuilder builder = new BooleanBuilder();
        
        builder.andByName("name", "=", "Hitachi");
        assertThat(builder.getSql(), is("(name = :name_0)"));
        
        builder.andByName("age", "=", 19);
        assertThat(builder.getSql(), is("(name = :name_0) and (age = :age_1)"));
    }
    
    @Test
    public void testAndNotByName() {
        BooleanBuilder builder = new BooleanBuilder();
        
        builder.andNotByName("name", "=", "Hitachi");
        assertThat(builder.getSql(), is("(name = :name_0)"));
        
        builder.andNotByName("age", "=", 19);
        assertThat(builder.getSql(), is("(name = :name_0) and not (age = :age_1)"));
    }
    
}
