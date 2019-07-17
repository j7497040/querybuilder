package com.icando.querybuilder;

/**
 * 子要素を必ずもつTerm
 *
 */
abstract class ContainerTerm extends Term {

	/**
	 * 子要素の中に、oldTermがあれば、newTermと置換する。
	 * 同一性は、{@link Term#equals(Object)}を用いる。
	 * @param oldTerm
	 * @param newTerm
	 */
	abstract void replace(Term oldTerm, Term newTerm);

}
