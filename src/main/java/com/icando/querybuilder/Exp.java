package com.icando.querybuilder;

import com.icando.querybuilder.subquery.OrderBuilder;



/**
 * 式を表すクラス。
 * 式の中にパラメータを含み、それへの設定値を設定することができます。
 * しかし式の書式やパラメータの書式については、このクラスでは規定しません。
 * 実際、このクラスは、文字列としてあらわされるならどんなものでも式として受け取り、
 * また、どんな形式のパラメータ、パラメータの値も受け取ります。
 */
public class Exp extends Term {

    /**
     * 式
     */
    private String exp;
    
    /**
     * パラメータへの設定値
     */
    private Object[] values;
    
    /**
     * 新規のパラメータ付の式を生成します。
     * パラメータの書式については、このクラスでは規定しません。
     * TermVisitorのサブクラスを参照してください。
     * 式の書式についても何ら規定しません。
     * <s>exp が null である場合、空文字列が渡されたものとします。</s>
     * expが null 空文字の場合はIllegalArgumentExceptionをスローします.
     * 
     * @param exp パラメータ付式
     * @param values パラメータに埋め込む値。可変長引数です。
     * @throws IllegalArgumentException expにnullまたは空文字が指定された場合にスローする.
     */
    Exp(String exp, Object... values) {
        if(exp == null || exp.equals("")) {
            throw new IllegalStateException("exp is empty. please input any expression.");
        } else {
            this.exp = exp;
        }
        this.values = values;
    }
    
    /**
     * and を追記します。
     * @return
     */
    public BinOp and() {
        return BinOp.upperReplace("and", this);
    }

    /**
     * or を追記します。
     * @return
     */
    public BinOp or() {
        return BinOp.upperReplace("or", this);
    }
    
    /**
     * GroupBy を追記します。
     * @param exp
     * @return
     */
    public GroupBy groupBy(String exp) {
        return Root.appendOptionalTerm(this, new GroupBy(exp));
    }

    /**
     * OrderBy を追記します。
     * @param exp
     * @return
     */
    public OrderBy orderBy(String exp) {
        return Root.appendOptionalTerm(this, new OrderBy(exp));
    }
    
    /**
     * OrderBy を追記します。
     * @param builder {@link OrderBuilder}のインスタンス。
     * @return
     */
    public OrderBy orderBy(OrderBuilder builder) {
        return orderBy(builder.getSql());
    }
    
    /**
     * 右括弧")"によりネストを終了します。
     * @return
     */
    public End end() {
        return Nest.tryClose(getUp());
    }
    
    public String getExp() {
        return this.exp;
    }
    
    /**
     * 指定したインデックス番号のパラメータの設定値を取得する。
     * @param index インデックス番号。0から始まる。
     * @return
     */
    public Object getValueAt(int index) {
        return values[index];
    }
    
    public int valuesSize() {
        if(values == null) {
            return 0;
        } else {
            return values.length;
        }
    }
    
    @Override
    void accept(TermVisitor v) {
        v.visit(this);
    }
    
    static Exp append(ContainerTerm term, String exp, Object... values) {
        Exp e = new Exp(exp, values);
        e.setUp(term);
        return e;
    }
    
}
