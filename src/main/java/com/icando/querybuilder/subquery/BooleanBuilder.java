package com.icando.querybuilder.subquery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 条件分を組み立てるためのヘルパークラス。
 * <pre>
 *     {@code // 基本的な使い方}
 *     BooleanBuiilder subQuery = new BooleanBuilder();
 *     for(String name : list) {
 *         subQuery.orByName("name", "=", name);
 *     }
 *     
 *     QueryBuilder query = new QueryBuilder();
 *     query.exp(subQuery);
 *     
 * </pre>
 * 
 * <pre>
 *  式を1つしか追加していない場合は、SQLとして演算子は付与されません。
 * 
 * </pre>
 * 
 * @since 1.1
 * @author T.TSUCHIE
 *
 */
public class BooleanBuilder {
    
    /**
     * 変数名の自動採番する際のオフセット 
     */
    private int offset ;
    
    private List<BooleanTerm> terms = new ArrayList<>();
    
    /**
     * 標準のコンストラクタ
     */
    public BooleanBuilder() {
        this(0);
    }
    
    /**
     * 自動再番スrう際の開始インデックスの番号を指定するコンストラクタ。
     * <p>指定した引数は、メソッド{@link #orByName(String, String, Object)}、
     *    {@link #andByName(String, String, Object)}のために使用されます。
     * <p>インデックス番号は、デフォルト'0'から始まります。
     * @param termIndexOffset 変数名の自動採番する際の開始インデックス番号。
     */
    public BooleanBuilder(final int termIndexOffset) {
        this.offset = termIndexOffset;
    }
    
    /**
     * 他の{@link BooleanBuilder}を引数を取るコンストラクタ。
     * <p>引数で渡した{@link BooleanBuilder}の項は、自身の項として追加されます。
     * @param builder 他の{@link BooleanBuilder}のインスタンス
     */
    public BooleanBuilder(final BooleanBuilder builder) {
        this();
        this.terms.addAll(builder.terms);
    }
    
    /**
     * OR演算子の項を追加する。
     * @param exp 式
     * @param values パラメータ
     * @return 自身のインスタンス
     */
    public BooleanBuilder or(final String exp, final Object... values) {
        terms.add(BooleanTerm.createOrTerm(exp, values));
        return this;
    }
    
    /**
     * OR演算子の項を追加する。
     * <p>副条件のSQLは、括弧で囲まれ、<code>' or (...副条件のSQL)'</code>のように追加される。
     * @param sub 副条件{@link BooleanBuilder}
     * @return 自身のインスタンス
     */
    public BooleanBuilder or(final BooleanBuilder sub) {
        if(!sub.isEmpty()) {
            or(sub.getSql(), sub.getParams());
        }
        return this;
    }
    
    /**
     * OR NOT演算子の項を追加する。
     * @param exp 式
     * @param values パラメータ
     * @return 自身のインスタンス
     */
    public BooleanBuilder orNot(final String exp, final Object... values) {
        terms.add(BooleanTerm.createOrNotTerm(exp, values));
        return this;
    }
    
    /**
     * OR NOT 演算子の項を追加する。
     * <p>副条件のSQLは、括弧で囲まれ、<code>' or not (...副条件のSQL)'</code>のように追加される。
     * @param sub 副条件{@link BooleanBuilder}
     * @return 自身のインスタンス
     */
    public BooleanBuilder orNot(final BooleanBuilder sub) {
        if(!sub.isEmpty()) {
            orNot(sub.getSql(), sub.getParams());
        }
        return this;
    }
    
    /**
     * AND演算子の項を追加する。
     * @param exp 式
     * @param values パラメータ
     * @return 自身のインスタンス
     */
    public BooleanBuilder and(final String exp, final Object... values) {
        terms.add(BooleanTerm.createAndTerm(exp, values));
        return this;
    }
    
    /**
     * AND演算子の項を追加する。
     * <p>副条件のSQLは、括弧で囲まれ、<code>' and (...副条件のSQL)'</code>のように追加される。
     * @param sub 副条件{@link BooleanBuilder}
     * @return 自身のインスタンス
     */
    public BooleanBuilder and(final BooleanBuilder sub) {
        if(!sub.isEmpty()) {
            and(sub.getSql(), sub.getParams());
        }
        return this;
    }
    
    /**
     * AND NOT演算子の項を追加する。
     * @param exp 式
     * @param values パラメータ
     * @return 自身のインスタンス
     */
    public BooleanBuilder andNot(final String exp, final Object... values) {
        terms.add(BooleanTerm.createAndNotTerm(exp, values));
        return this;
    }
    
    /**
     * AND NOT演算子の項を追加する。
     * <p>副条件のSQLは、括弧で囲まれ、<code>' and not (...副条件のSQL)'</code>のように追加される。
     * @param sub 副条件{@link BooleanBuilder}
     * @return 自身のインスタンス
     */
    public BooleanBuilder andNot(final BooleanBuilder sub) {
        if(!sub.isEmpty()) {
            andNot(sub.getSql(), sub.getParams());
        }
        return this;
    }
    
    /**
     * 項目名を元にOR条件文を組み立てる。
     * ・その際にインデックス番号を自動的に付与する。
     * <pre>
     *  orByName("age", "=", 1); => 「or (age = :age_1)」というSQLを組み立てる。
     * </pre>
     * @param name 項目名
     * @param op 演算子
     * @param value 値
     * @return 自身のインスタンス
     */
    public BooleanBuilder orByName(final String name, final String op, final Object value) {
        return or(String.format("%s %s :%s_%d", name, op, name, offset + terms.size()), value);
    }
    
    /**
     * 項目名を元にOR NOT条件文を組み立てる。
     * ・その際にインデックス番号を自動的に付与する。
     * <pre>
     *  orNotByName("age", "=", 1); => 「or not (age = :age_1)」というSQLを組み立てる。
     * </pre>
     * @param name 項目名
     * @param op 演算子
     * @param value 値
     * @return 自身のインスタンス
     */
    public BooleanBuilder orNotByName(final String name, final String op, final Object value) {
        return orNot(String.format("%s %s :%s_%d", name, op, name, offset + terms.size()), value);
    }
    
    /**
     * 項目名を元にAND条件分を組み立てる。
     * ・その際にインデックス番号を自動的に付与する。
     * <pre>
     *  andByName("age", "=", 1); => 「and (age = :age_1)」というSQLを組み立てる。
     * </pre>
     * @param name 項目名
     * @param op 演算子
     * @param value 値
     * @return 自身のインスタンス
     */
    public BooleanBuilder andByName(final String name, final String op, final Object value) {
        return and(String.format("%s %s :%s_%d", name, op, name, offset + terms.size()), value);
    }
    
    /**
     * 項目名を元にAND NOT条件分を組み立てる。
     * ・その際にインデックス番号を自動的に付与する。
     * <pre>
     *  andNotByName("age", "=", 1); => 「and not (age = :age_1)」というSQLを組み立てる。
     * </pre>
     * @param name 項目名
     * @param op 演算子
     * @param value 値
     * @return 自身のインスタンス
     */
    public BooleanBuilder andNotByName(final String name, final String op, final Object value) {
        return andNot(String.format("%s %s :%s_%d", name, op, name, offset + terms.size()), value);
    }
    
    /**
     * 項の個数が0個かどうか。
     * @return
     */
    public boolean isEmpty() {
        return terms.isEmpty();
    }
    
    /**
     * SQLを取得する。
     * <p>先頭の項は演算子を付けないSQLが返される。
     * @return
     */
    public String getSql() {
        
        StringBuilder sql = new StringBuilder();
        
        for(int i=0; i < terms.size(); i++) {
            final BooleanTerm term = terms.get(i);
            
            if(i == 0) {
                sql.append(String.format("(%s)", term.getExp()));
            } else {
                sql.append(String.format(" %s (%s)", term.operator(), term.getExp()));
            }
        }
        
        return sql.toString();
        
    }
    
    /**
     * パラメータを取得する。
     * @return
     */
    public Object[] getParams() {
        
        List<Object> list = new ArrayList<>();
        
        for(BooleanTerm term : terms) {
            if(term.getValues() != null && term.getValues().length > 0) {
                list.addAll(Arrays.asList(term.getValues()));
            }
        }
        
        return list.toArray(new Object[list.size()]);
    }
    
}
