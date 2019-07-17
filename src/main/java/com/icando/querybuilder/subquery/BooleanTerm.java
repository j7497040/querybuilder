package com.icando.querybuilder.subquery;

/**
 * {@link BooleanBuilder}の項。
 * 
 * @since 1.1
 * @author T.TSUCHIE
 *
 */
public abstract class BooleanTerm {
    
    private final String exp;
    
    private final Object[] values;
    
    public BooleanTerm(final String exp, final Object[] values) {
        this.exp = exp;
        this.values = values;
    }
    
    /**
     * 演算子を取得する。
     * @return
     */
    public abstract String operator();
    
    /**
     * OR演算子を持つ項を作成する。
     * @param exp
     * @param values
     * @return
     */
    public static BooleanTerm createOrTerm(final String exp, final Object... values) {
        
        return new BooleanTerm(exp, values) {
            
            @Override
            public String operator() {
                return "or";
            }
        };
    }
    
    /**
     * OR NOT 演算子を持つ項を作成する。
     * @param exp
     * @param values
     * @return
     */
    public static BooleanTerm createOrNotTerm(final String exp, final Object... values) {
        
        return new BooleanTerm(exp, values) {
            
            @Override
            public String operator() {
                return "or not";
            }
        };
    }
    
    /**
     * AND演算子を持つ項を作成する。
     * @param exp
     * @param values
     * @return
     */
    public static BooleanTerm createAndTerm(final String exp, final Object... values) {
        return new BooleanTerm(exp, values) {
            
            @Override
            public String operator() {
                return "and";
            }
        };
    }
    
    /**
     * AND NOT演算子を持つ項を作成する。
     * @param exp
     * @param values
     * @return
     */
    public static BooleanTerm createAndNotTerm(final String exp, final Object... values) {
        return new BooleanTerm(exp, values) {
            
            @Override
            public String operator() {
                return "and not";
            }
        };
    }
    
    /**
     * 式を取得する。
     * @return
     */
    public String getExp() {
        return exp;
    }
    
    /**
     * 式のパラメータを取得する。
     * @return
     */
    public Object[] getValues() {
        return values;
    }
    
    
}
