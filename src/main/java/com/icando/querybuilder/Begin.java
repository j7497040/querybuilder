package com.icando.querybuilder;

/**
 * 左括弧"("を表すクラス。ネストの開始を表します。
 *
 */
public class Begin extends Term {

	Begin() {}
	
	/**
	 * 式を追記します。
	 * @param exp
	 * @param values
	 * @return
	 */
	public Exp exp(String exp, Object... values) {
		return ((Nest)getUp()).exp(exp, values);
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
	 * 左括弧"("によるネストを開始します。
	 * @return
	 */
	public Begin begin() {
		return ((Nest)getUp()).begin();
	}

	@Override
	void accept(TermVisitor v) {
		v.visit(this);
	}

}
