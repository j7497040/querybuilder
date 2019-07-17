package com.icando.querybuilder;

/**
 * QueryBuilderの内部表現の抽象基底クラス。
 *
 */
abstract class Term {

	/**
	 * 親要素
	 */
	private ContainerTerm up;
	
	void setUp(ContainerTerm up) {
		this.up = up;
	}
	
	ContainerTerm getUp() {
		return up;
	}
	
	/**
	 * TermVisitorを受け入れるメソッド。Visitorパターン。
	 * @param v
	 */
	abstract void accept(TermVisitor v);

}
