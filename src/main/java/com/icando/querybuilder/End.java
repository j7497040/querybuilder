package com.icando.querybuilder;

import com.icando.querybuilder.subquery.OrderBuilder;

/**
 * 右括弧")"を表すクラス。ネストの終了を表します。
 *
 */
public class End extends Term {

	End() {}
	
	/**
	 * and を追記します。
	 * @return
	 */
	public BinOp and() {
		return ((Nest)getUp()).and();
	}
	
	/**
	 * or を追記します。
	 * @return
	 */
	public BinOp or() {
		return ((Nest)getUp()).or();
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
     * {@link OrderBuilder}で組み立てな並び順を追記します。
     * @param builder
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
	
	@Override
	void accept(TermVisitor v) {
		v.visit(this);
	}

}
