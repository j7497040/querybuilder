package com.icando.querybuilder;

public class GroupBy extends Term {

	/**
	 * group by の評価式
	 */
	private String exp;
	
	/**
	 * @param exp　評価式
	 */
	public GroupBy(String exp) {
		super();
		this.exp = exp;
	}

	/**
	 * 文字列 "group by" を返します。
	 * @return
	 */
	public String getOperator() {
		return "group by";
	}

	/**
	 * 評価式を返します。
	 * @return
	 */
	public String getExp() {
		return exp;
	}

	/**
	 * OrderBy を追記します。
	 * @param exp
	 * @return
	 */
	public OrderBy orderBy(String exp) {
		return Root.appendOptionalTerm(this, new OrderBy(exp));
	}
	
	@Override
	void accept(TermVisitor v) {
		v.visit(this);
	}
}
