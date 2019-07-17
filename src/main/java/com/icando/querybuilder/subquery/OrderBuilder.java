package com.icando.querybuilder.subquery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 並び順を組み立てるためのヘルパークラス。
 * 
 * <pre>
 *     {@code // 基本的な使い方}
 *     OrderBuilder orderBuilder = new OrderBuilder();
 *     for(String name : list) {
 *         orderBuilder.asc(name);
 *     }
 *     
 *     orderBuilder.order("age", false)
 *                 .items("item1 desc", "item2 "asc");
 *     
 *     QueryBuilder query = new QueryBuilder();
 *     query.orderBy(orderBuilder);
 * </pre>
 * 
 * @since 1.1
 * @author T.TSUCHIE
 *
 */
public class OrderBuilder {
    
    private List<String> terms = new ArrayList<>();
    
    public OrderBuilder() {
    }
    
    /**
     * 並び順の項目を追加する。
     * ・asc/descなどは、自身で追加する必要がある。
     * <pre>
     * OrderBuilder orderBuilder = ...;
     * orderBuilder.items("name desc", "age desc");
     * </pre>
     * @param exps
     * @return 自身のインスタンス
     */
    public OrderBuilder items(final String... exps) {
        this.terms.addAll(Arrays.asList(exps));
        return this;
    }
    
    /**
     * 項目名を全て昇順で指定して追加する。
     * @param name
     * @return 自身のインスタンス
     */
    public OrderBuilder asc(final String... names) {
        for(String name : names) {
            this.terms.add(String.format("%s asc", name));
        }
        return this;
    }
    
    /**
     * 項目名を全て昇順で指定して追加する。
     * @param name
     * @return 自身のインスタンス
     */
    public OrderBuilder desc(final String... names) {
        for(String name : names) {
            this.terms.add(String.format("%s desc", name));
        }
        return this;
    }
    
    /**
     * 名前を指定して並び順を追加する。
     * @param name 項目名
     * @param ascending true:昇順かどうか。false:降順かどうか。
     * @return 自身のインスタンス
     */
    public OrderBuilder order(final String name, final boolean ascending) {
        
        return ascending ? asc(name) : desc(name);
        
    }
    
    /**
     * 項の個数が0個かどうか。
     * @return true:高の個数が0個。
     */
    public boolean isEmpty() {
        return terms.isEmpty();
    }
    
    /**
     * SQLを取得する。
     * @return
     */
    public String getSql() {
        StringBuilder sql = new StringBuilder();
        for(String term : terms) {
            
            if(term == null || term.isEmpty()) {
                continue;
            }
            
            if(sql.length() == 0) {
                sql.append(term);
            } else {
                sql.append(", ").append(term);
            }
        }
        
        return sql.toString();
        
    }
    
}
