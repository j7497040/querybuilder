package com.icando.querybuilder;

/**
 * QueryBuilderが保持する条件式の内部構造を走査するインターフェース。
 * Visitorパターン。
 * 前順走査で走査する。詳しくは以下のとおりです。
 * For Nest:<br />
 * beginメンバーを走査、descentメンバーを走査、endメンバーを走査<br />
 * For BinOp:<br />
 * leftメンバーを走査、rightメンバーを走査。
 */
public interface TermVisitor {

	/**
	 * Nestを訪れたときの処理を書きます。
	 * 通常、ネスト構造は、Begin, descentメンバー（これはBinOpかExpで走査される）、
	 * Endで足りるため、このメソッドは空の場合が多いでしょう。
	 * @param nest
	 */
	void visit(Nest nest);
	
	/**
	 * Beginを訪れたときの処理を書きます。
	 * @param begin
	 */
	void visit(Begin begin);
	
	/**
	 * Endを訪れたときの処理を書きます。
	 * @param end
	 */
	void visit(End end);
	
	/**
	 * BinOpを訪れたときの処理を書きます。
	 * @param binop
	 */
	void visit(BinOp binop);
	
	/**
	 * Expを訪れたときの処理を書きます。
	 * @param exp
	 */
	void visit(Exp exp);
	
	/**
	 * GroupBy を訪れたときの処理を書きます。
	 * @param groupBy
	 */
	void visit(GroupBy groupBy);
	
	/**
	 * OrderBy を訪れたときの処理を書きます。
	 * @param orderBy
	 */
	void visit(OrderBy orderBy);
}
