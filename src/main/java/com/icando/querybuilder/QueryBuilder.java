package com.icando.querybuilder;

import java.util.Map;

import com.icando.querybuilder.subquery.BooleanBuilder;
import com.icando.querybuilder.subquery.OrderBuilder;

/**
 * SQLのクエリを組み立てるためのクラス。
 * 
 * @version 1.1
 * 
 */
public class QueryBuilder {
    
    /**
     * パラメータが列挙型の場合、文字列に変換するかどうか。
     */
    private boolean enumToStr;
    
    private Root root;
    
    public QueryBuilder() {
        this.enumToStr = false;
        this.root = new Root(); 
    }
    
    /**
     * 式を追記します。
     * @param exp
     * @param values
     * @return
     */
    public Exp exp(String exp, Object... values) {
        return root.exp(exp, values);
    }
    
    /**
     * {@link #exp(String, Object...)}の省略形。
     * @param exp
     * @param values
     * @return
     */
    public Exp e(String exp, Object... values) {
        return exp(exp, values);
    }
    
    /**
     * {@link BooleanBuilder}で組み立てた式を追加します。
     * @param builder
     * @return
     */
    public Exp exp(BooleanBuilder builder) {
        return exp(builder.getSql(), builder.getParams());
    }
    
    /**
     * {@link #exp(BooleanBuilder)}の省略系
     * @param builder
     * @return
     */
    public Exp e(BooleanBuilder builder) {
        return exp(builder);
    }
    
    /**
     * 左括弧"("によるネストを開始します。
     * @return
     */
    public Nest begin() {
        return root.begin();
    }
    
    /**
     * GroupBy を追記します。
     * @param exp
     * @return
     */
    public GroupBy groupBy(String exp) {
        return Root.appendOptionalTerm(root, new GroupBy(exp));
    }

    /**
     * OrderBy を追記します。
     * @param exp
     * @return
     */
    public OrderBy orderBy(String exp) {
        return Root.appendOptionalTerm(root, new OrderBy(exp));
    }
    
    /**
     * OrderByを追記します。
     * @param builder OrderByのビルダ
     * @return
     */
    public OrderBy orderBy(OrderBuilder builder) {
        return orderBy(builder.getSql());
    }
    
    /**
     *条件式を組み立てて返却します。
     * 
     * この条件式は式が追加されている場合、whereを付与し、式が追加されていない場合、whereを付与しません.
     * group by, order byのみ追加されている場合もwhereを付与しません.
     * @return 条件式. 
     */
    public String getQuery() {
        return ( root.hasNextTerm() ? getQueryWithWhere() : getQueryWithoutWhere() ) + getOptionalTerm() ;
    }
    
    /**
     * optional term (group by, order by)を含まない条件式を組み立てて返却します。
     * countなどソート条件が必要ないSQLを組み立てる時に使用してください.
     * 
     * この条件式は式が追加されている場合、whereを付与し、式が追加されていない場合、whereを付与しません.
     * @return 条件式. 
     */
    public String getQueryWithoutOptionalTerm() {
        return root.hasNextTerm() ? getQueryWithWhere() : getQueryWithoutWhere();
    }
    
    /**
     * 条件式を組み立てて返却します。
     * このメソッドはwhere および optional term (group by, order by) は含みません.
     * @return whereなしの条件式.
     */
    public String getQueryWithoutWhere() {
        return accept(new QueryBuilderVisitor()).getQuery();
    }
    
    /**
     * 条件式を組み立てて返却します。
     * このメソッドはwhereから始まる条件式を返します.
     * optional term (group by, order by) は含みません.
     * @return whereから始まる条件式.
     */
    public String getQueryWithWhere() {
        return accept(new QueryBuilderVisitor()).getQueryWithWhere();
    }
    
    
    /**
     * optional term (group by, order by) のみ出力します.
     * 
     * @return optional term
     */
    public String getOptionalTerm() {
        return accept(new OptionalTermVisitor()).getQuery();
    }
    
    
    /**
     * パラメータ名とその設定値を抽出し、返却します。
     * @return
     */
    public Map<String, Object> getParams() {
        return accept(new ParamAccumulateVisitor(isEnumToStr())).getParams();
    }
    
    /**
     * 式が存在するか確認します。
     * 式が存在する場合はtrueを返し、式が存在しない場合はfalseを返します。
     * order by、 group byのみ追加されている場合はfalseを返します。
     * @return 
     * true: 式が存在する場合
     * false: 式が存在しない場合
     */
    public boolean isEmpty() {
        return !root.hasNextTerm();
    }
    
    /**
     * TermVisitorを受け取り、内部構造を前順走査で走査するメソッドです。
     * 戻り値は、引数で渡したTermVisitorです。
     * ユーザが作成するカスタムなTermVisitorを使う場合は、このメソッドを使用して、
     * TermVisitorをQueryBuilderへ渡してください。
     * @param visitor
     * @return
     */
    public <T extends TermVisitor> T accept(T v) {
        root.accept(v);
        return v;
    }
    
    /**
     * パラメータを指定した場合、列挙型の場合は文字列に変換するかどうか。
     * @since 1.1
     * @return true: 列挙型を文字列に変換する。デフォルトはfalseです。
     */
    public boolean isEnumToStr() {
        return enumToStr;
    }
    
    /**
     * パラメータを指定した場合、列挙型の場合は文字列に変換するかどうか設定する。
     * @since 1.1
     * @param enumToStr true: 列挙型を文字列に変換する。デフォルトはfalseです。
     */
    public void setEnumToStr(boolean enumToStr) {
        this.enumToStr = enumToStr;
    }
    
    /**
     * パラメータを指定した場合、列挙型の場合は文字列に変換するかどうか設定する。
     * 
     * @since 1.1
     * @param enumToStr enumToStr true: 列挙型を文字列に変換する。デフォルトはfalseです。
     * @return 
     */
    public QueryBuilder enumToStr(boolean enumToStr) {
        setEnumToStr(enumToStr);
        return this;
    }
}
