package com.icando.querybuilder;

public class OrderBy extends Term {

	/**
	 * order by の評価式
	 */
	private String exp;
	

	/**
	 * @param exp 評価式
	 */
	public OrderBy(String exp) {
		super();
		this.exp = exp;
	}

	/**
	 * 文字列 "order by" を返します。
	 * @return
	 */
	public String getOperator() {
		return "order by";
	}

	/**
	 * 評価式を返します。
	 * @return
	 */
	public String getExp() {
		return exp;
	}

	@Override
	void accept(TermVisitor v) {
		v.visit(this);
	}
}
