package com.icando.querybuilder;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.Map;

import junit.framework.TestCase;

import org.junit.Test;

import com.icando.querybuilder.QueryBuilder;
import com.icando.querybuilder.subquery.OrderBuilder;

public class QueryBuilderTest extends TestCase {
    
    @Test
    public void testGetQuery_exp_and_exp() {
        QueryBuilder q = new QueryBuilder();
        q.exp("name = :name", "smith").and().exp("passwd = :passwd", "passwd");
        
        assertThat(q.getQuery(), is("where name = :name and passwd = :passwd"));
        assertThat(q.getQueryWithoutOptionalTerm(), is("where name = :name and passwd = :passwd"));
        assertThat(q.getQueryWithoutWhere(), is("name = :name and passwd = :passwd"));
        assertThat(q.getQueryWithWhere(), is("where name = :name and passwd = :passwd"));
        assertThat(q.getOptionalTerm(), is(""));

    }

    @Test
    public void testGetQuery_exp_and_begin() {
        QueryBuilder q = new QueryBuilder();
        q.exp("name = :name", "smith").and().begin().exp("passwd = :passwd", "passwd").end();
        
        assertThat(q.getQuery(), is("where name = :name and (passwd = :passwd)"));
        assertThat(q.getQueryWithoutOptionalTerm(), is("where name = :name and (passwd = :passwd)"));
        assertThat(q.getQueryWithoutWhere(), is("name = :name and (passwd = :passwd)"));
        assertThat(q.getQueryWithWhere(), is("where name = :name and (passwd = :passwd)"));
        assertThat(q.getOptionalTerm(), is(""));
    }

    @Test
    public void testGetQuery_exp_or_exp() {
        QueryBuilder q = new QueryBuilder();
        q.exp("name = :name", "smith").or().exp("role = 'guest'");
        
        assertThat(q.getQuery(), is("where name = :name or role = 'guest'"));        
        assertThat(q.getQueryWithoutOptionalTerm(), is("where name = :name or role = 'guest'"));
        assertThat(q.getQueryWithoutWhere(), is("name = :name or role = 'guest'"));       
        assertThat(q.getQueryWithWhere(), is("where name = :name or role = 'guest'"));       
        assertThat(q.getOptionalTerm(), is(""));
    }

    @Test
    public void testGetQuery_exp_or_begin() {
        QueryBuilder q = new QueryBuilder();
        q.exp("name = :name", "smith").or().begin().exp("role = 'guest'").end();
        
        assertThat(q.getQuery(), is("where name = :name or (role = 'guest')"));      
        assertThat(q.getQueryWithoutOptionalTerm(), is("where name = :name or (role = 'guest')"));
        assertThat(q.getQueryWithoutWhere(), is("name = :name or (role = 'guest')"));     
        assertThat(q.getQueryWithWhere(), is("where name = :name or (role = 'guest')"));     
        assertThat(q.getOptionalTerm(), is(""));
    }

    @Test
    public void testGetQuery_begin_begin() {
        QueryBuilder q = new QueryBuilder();
        q.begin().begin()
            .exp("status = :status1", "shipped").or()
            .exp("status = :status2", "canceled")
        .end().end();
        assertThat(q.getQuery(), is("where ((status = :status1 or status = :status2))"));
        assertThat(q.getQueryWithoutOptionalTerm(), is("where ((status = :status1 or status = :status2))"));
        assertThat(q.getQueryWithoutWhere(), is("((status = :status1 or status = :status2))"));
        assertThat(q.getQueryWithWhere(), is("where ((status = :status1 or status = :status2))"));
        assertThat(q.getOptionalTerm(), is(""));
    }

    /**
     * See and() right after end() is processed properly. 
     */
    @Test
    public void testGetQuery_end_and() {
        QueryBuilder q = new QueryBuilder();
        q.begin().exp("name = :name", "anderson").end().and().exp("age = :age", new Integer(30));
        assertThat(q.getQuery(), is("where (name = :name) and age = :age"));
        assertThat(q.getQueryWithoutOptionalTerm(), is("where (name = :name) and age = :age"));
        assertThat(q.getQueryWithoutWhere(), is("(name = :name) and age = :age"));
        assertThat(q.getQueryWithWhere(), is("where (name = :name) and age = :age"));
        assertThat(q.getOptionalTerm(), is(""));
    }

    /**
     * See or() right after end() is processed properly.
     */
    @Test
    public void testGetQuery_end_or() {
        QueryBuilder q = new QueryBuilder();
        q.begin().exp("name = :name", "anderson").end().or().exp("age = :age", new Integer(30));
        assertThat(q.getQuery(), is("where (name = :name) or age = :age"));
        assertThat(q.getQueryWithoutOptionalTerm(), is("where (name = :name) or age = :age"));
        assertThat(q.getQueryWithoutWhere(), is("(name = :name) or age = :age"));
        assertThat(q.getQueryWithWhere(), is("where (name = :name) or age = :age"));
    }

    /**
     * See exp() followed by groupBy() is processed properly.
     */
    @Test
    public void testGetQuery_exp_groupBy() {
        QueryBuilder q = new QueryBuilder();
        q.exp("name = 'foo'").groupBy("section");
        assertThat(q.getQuery(), is("where name = 'foo' group by section"));
        assertThat(q.getQueryWithoutOptionalTerm(), is("where name = 'foo'"));
        assertThat(q.getQueryWithoutWhere(), is("name = 'foo'"));
        assertThat(q.getQueryWithWhere(), is("where name = 'foo'"));
        assertThat(q.getOptionalTerm(), is(" group by section"));
    }
    
    /**
     * See end() followed by groupBy() is processed properly.
     */
    @Test
    public void testGetQuery_end_groupBy() {
        QueryBuilder q = new QueryBuilder();
        q.begin().exp("name = 'foo'").end().groupBy("section");
        assertThat(q.getQuery(), is("where (name = 'foo') group by section"));
        assertThat(q.getQueryWithoutOptionalTerm(), is("where (name = 'foo')"));
        assertThat(q.getQueryWithoutWhere(), is("(name = 'foo')"));
        assertThat(q.getQueryWithWhere(), is("where (name = 'foo')"));
        assertThat(q.getOptionalTerm(), is(" group by section"));
    }

    /**
     * See exp() followed by orderBy() is processed properly. 
     */
    @Test
    public void testGetQuery_exp_orderBy() {
        QueryBuilder q = new QueryBuilder();
        q.exp("name = 'foo'").orderBy("section");
        assertThat(q.getQuery(), is("where name = 'foo' order by section"));
        assertThat(q.getQueryWithoutOptionalTerm(), is("where name = 'foo'"));
        assertThat(q.getQueryWithoutWhere(), is("name = 'foo'"));
        assertThat(q.getQueryWithWhere(), is("where name = 'foo'"));
        assertThat(q.getOptionalTerm(), is(" order by section"));
    }
    
    /**
     * See end() followed by orderBy() is processed properly.
     */
    @Test
    public void testGetQuery_end_orderBy() {
        QueryBuilder q = new QueryBuilder();
        q.begin().exp("name = 'foo'").end().orderBy("section");
        assertThat(q.getQuery(), is("where (name = 'foo') order by section")); 
        assertThat(q.getQueryWithoutOptionalTerm(), is("where (name = 'foo')"));
        assertThat(q.getQueryWithoutWhere(), is("(name = 'foo')")); 
        assertThat(q.getQueryWithWhere(), is("where (name = 'foo')")); 
        assertThat(q.getOptionalTerm(), is(" order by section"));
    }
    
    /**
     * See end() followed by orderBy() is processed properly.
     *  - variable argument
     */
    @Test
    public void testGetQuery_end_orderBy_variable_arg() {
        QueryBuilder q = new QueryBuilder();
        q.begin().exp("name = 'foo'").end().orderBy(new OrderBuilder().items("section asc", "name desc"));
        assertThat(q.getQuery(), is("where (name = 'foo') order by section asc, name desc")); 
        assertThat(q.getQueryWithoutOptionalTerm(), is("where (name = 'foo')"));
        assertThat(q.getQueryWithoutWhere(), is("(name = 'foo')")); 
        assertThat(q.getQueryWithWhere(), is("where (name = 'foo')")); 
        assertThat(q.getOptionalTerm(), is(" order by section asc, name desc"));
    }
    
    /**
     * See groupBy() followed by orderBy() is processed properly. 
     */
    @Test
    public void testGetQuery_groupBy_orderBy() {
        QueryBuilder q = new QueryBuilder();
        q.exp("name = 'foo'").groupBy("section").orderBy("updatetime");
        assertThat(q.getQuery(), is("where name = 'foo' group by section order by updatetime"));
        assertThat(q.getQueryWithoutOptionalTerm(), is("where name = 'foo'"));
        assertThat(q.getQueryWithoutWhere(), is("name = 'foo'"));
        assertThat(q.getQueryWithWhere(), is("where name = 'foo'"));
        assertThat(q.getOptionalTerm(), is(" group by section order by updatetime"));
    }
    
    /**
     * See mismatched begin()-end() pair followed by groupBy()
     * @throws Exception 
     */
    @Test
    public void testGetQuery_noend_groupBy() throws Exception {
        QueryBuilder q = new QueryBuilder();
        q.begin().exp("name = 'foo'").groupBy("section");
        try {
            q.getQuery();
            fail("Exception should be thrown.");
        } catch(IllegalStateException e) {
            // Success
        }
    }
    
    @Test
    public void testGetParams() {
        QueryBuilder q = new QueryBuilder();
        q.exp("name in (:name1, :name2)", "smith", "anderson")
            .and().exp("passwd = :passwd", "8ucJKeh")
            .and().exp("expires < 1000").and()
            .begin().exp("role in (:role1, :role2)", "admin", "poweruser").end()
            .and().exp("city in (:city1, :city2)", "Birmingham", "London");

        Map<String, Object> params = q.getParams();
        assertThat(params.size(), is(7));
        assertThat(params.get("name1").toString(), is("smith"));
        assertThat(params.get("name2").toString(), is("anderson"));
        assertThat(params.get("passwd").toString(), is("8ucJKeh"));
        assertThat(params.get("role1"), is((Object)"admin"));
        assertThat(params.get("role2"), is((Object)"poweruser"));
        assertThat(params.get("city1").toString(), is("Birmingham"));
        assertThat(params.get("city2").toString(), is("London"));
    }
    
    @Test
    public void testGetParams_mismatchedParamsValues() {
        {
            // Too few values.
            QueryBuilder q = new QueryBuilder();
            try {
                q.exp("name in :foo");
                q.getParams();
                fail("Exception should be thrown.");
            } catch(IllegalArgumentException e) {
                assertThat(e.getMessage(), is("Too few values in exp."));
                // Success
            }
        }
        {
            // Too few values.
            QueryBuilder q = new QueryBuilder();
            try {
                q.exp("name in (:foo :bar)", "foo");
                q.getParams();
                fail("Exception should be thrown.");
            } catch(IllegalArgumentException e) {
                assertThat(e.getMessage(), is("Too few values in exp."));
                // Success
            }
        }
        {
            // Too few parameters.
            QueryBuilder q = new QueryBuilder();
            try {
                q.exp("name = 'foo'", "foo");
                q.getParams();
                fail("Exception should be thrown.");
            } catch(IllegalArgumentException e) {
                assertThat(e.getMessage(), is("Too few parameters in exp."));
                // Success
            }
        }
        {
            // Too few parameters.
            QueryBuilder q = new QueryBuilder();
            try {
                q.exp("name = :foo", "foo", "bar");
                q.getParams();
                fail("Exception should be thrown.");
            } catch(IllegalArgumentException e) {
                assertThat(e.getMessage(), is("Too few parameters in exp."));
                // Success
            }
        }
        {
            // Too few values.
            QueryBuilder q = new QueryBuilder();
            try {
                q.exp("name = :foo", null);
                q.getParams();
                fail("Exception should be thrown.");
            } catch(IllegalArgumentException e) {
                assertThat(e.getMessage(), is("Too few values in exp."));
                // Success
            }
        }
        {
            // Too few parameters.
            QueryBuilder q = new QueryBuilder();
            try {
                q.exp("name = :foo", "foo", null);
                q.getParams();
                fail("Exception should be thrown.");
            } catch(IllegalArgumentException e) {
                assertThat(e.getMessage(), is("Too few parameters in exp."));
                // Success
            }
        }
    }
    
    /**
     * See that the exception is thrown if end() appears without begin().
     * @throws Exception
     */
    @Test
    public void testMismatchedEndClause_nobegin_end() throws Exception {
        QueryBuilder q = new QueryBuilder();
        try {
            q.exp("name = :name", "smith").end();
            fail("Exception should be thrown.");
        } catch(IllegalStateException e) {
            // Success
        }
    }

    /**
     * See that the exception is thrown if end() appears but corresponding begin() doesn't
     * exist.
     * @throws Exception
     */
    @Test
    public void testMismatchedEndClause_begin_end_end() throws Exception {
        QueryBuilder q = new QueryBuilder();
        try {
            q.begin().exp("name = :name", "smith").end().end();
            fail("Exception must be thrown.");
        } catch(IllegalStateException e) {
            // Success
        }
    }
    
    /**
     * See that the exception is thrown when begin() appears without end(). 
     * @throws Exception
     */
    @Test
    public void testGetQuery_noend() throws Exception
    {
        QueryBuilder q = new QueryBuilder();
        q.begin().exp("name = :name");
        try {
            q.getQuery();
            fail("Exception must be thrown.");
        } catch(IllegalStateException e) {
            // Success
        }
    }
    
    /**
     * See that the exception is thrown when no end() exists corresponding to the preceding begin(). 
     * @throws Exception
     */
    @Test
    public void testGetQuery_begin_begin_end() throws Exception
    {
        QueryBuilder q = new QueryBuilder();
        q.begin().begin().exp("name = :name").end();
        try {
            q.getQuery();
            fail("Exception must be thrown.");
        } catch(IllegalStateException e) {
            // Success
        }
    }

    /**
     * See empty string is got when no terms is added.
     */
    @Test
    public void testEmptyQuery()
    {
        QueryBuilder q = new QueryBuilder();
        assertThat(q.getQuery(), is(""));
    }

    /**
     * See that the exception is thrown when right operand is missing.
     * @throws Exception
     */
    @Test
    public void testGetQuery_missingRightOperand() throws Exception
    {
        QueryBuilder q = new QueryBuilder();
        q.exp("name = 'alice'").and();
        try {
            q.getQuery();
            fail("Exception must be thrown.");
        } catch(IllegalStateException e) {
            // Success
        }
    }
    
    /**
     * See that the exception is thrown when begin() is not followed by any term.
     * @throws Exception
     */
    @Test
    public void testGetQuery_endRightAfterLeftParen() throws Exception
    {
        QueryBuilder q = new QueryBuilder();
        q.begin();
        try {
            q.getQuery();
            fail("Exception must be thrown.");
        } catch(IllegalStateException e) {
            // Success
        }
    }
    
    /**
     * See empty exp is processed with throwing exception.
     */
    @Test
    public void testGetQuery_emptyexp() {
        QueryBuilder q = new QueryBuilder();
        try {
            q.exp("");
            fail("Exception must be thrown.");
            //assertThat(q.getQueryWithoutWhere(), is(""));
        } catch(IllegalStateException e) {
            // Success
        }
    }
    

    /**
     * See null exp is processed without throwing exception.
     */
    @Test
    public void testGetQuery_nullexp() {
        try {
            QueryBuilder q = new QueryBuilder();
            q.exp(null);
            //assertThat(q.getQueryWithoutWhere(), is(""));
            fail("Exception must be thrown.");
        } catch(Exception e) {
            assertThat(e, instanceOf(NullPointerException.class));
        }
    }

    /**
     * See null value is processed without throwing exception.
     */
    @Test
    public void testGetParams_nullvalue() {
        QueryBuilder q = new QueryBuilder();
        q.exp(":boolean", (Object)null);
        Map<String, Object> params = q.getParams();
        assertThat(params.containsKey("boolean"), is(true));
        assertThat(params.get("boolean"), is(nullValue()));
    }

    /**
     * See whether abbreviation of exp works
     */
    @Test
    public void testGetQuery_exp_abbrev() {
        QueryBuilder q = new QueryBuilder();
        q.e("name = :name", "anderson").and().e("born = :date", "19000101")
            .or().begin().e("profession = :prof", "flowery").end();
        
        assertThat(q.getQueryWithoutWhere(), is("name = :name and born = :date or (profession = :prof)"));
        assertThat(q.getQueryWithWhere(), is("where name = :name and born = :date or (profession = :prof)"));
    }
    
    /**
     * see orderBy right after QueryBuilder
     */
    @Test
    public void testGetQuery_orderBy() {
        QueryBuilder q = new QueryBuilder();
        q.orderBy("created_at DESC");
        
        assertThat(q.getQuery(), is(" order by created_at DESC"));
    }
    
    /**
     * see groupBy right after QueryBuilder
     */
    @Test
    public void testGetQuery_groupBy() {
        QueryBuilder q = new QueryBuilder();
        q.groupBy("section");
        
        assertThat(q.getQuery(), is(" group by section"));
    }
    
    @Test
    public void testIsEmpty() {
        // not empty
        QueryBuilder q = new QueryBuilder();
        q.e("name = 'alice'"); 
        assertThat(q.isEmpty(), is(false));
       
        // not empty
        QueryBuilder q2 = new QueryBuilder();
        q2.e("name = :name", "alice");
        assertThat(q2.isEmpty(), is(false));

    }
    
    @Test
    public void testWithoutWhere() {
        QueryBuilder q = new QueryBuilder();
        q.orderBy("created_at");
        assertThat(q.getQuery(), is(" order by created_at"));
    }
    @Test
    public void testWithoutWhere2() {
        QueryBuilder q = new QueryBuilder();
        q.groupBy("id");
        assertThat(q.getQuery(), is(" group by id"));
    }
    
    @Test
    public void testWithWhere1() {
        QueryBuilder q = new QueryBuilder();
        q.e("name = 'kenji'").orderBy("name");
        assertThat(q.getQuery(), is("where name = 'kenji' order by name"));
    }
    
    @Test
    public void testWithWhere2() {
        QueryBuilder q = new QueryBuilder();
        q.e("name = :name", "bob")
        .orderBy("created_at");
        assertThat(q.getQuery(), is("where name = :name order by created_at"));
       
    }
}
