package com.icando.querybuilder;

/**
 * 括弧"()"によるネストの概念を実現するクラス。
 * ネストの深さへの制限はありません。
 */
public class Nest extends ContainerTerm {

	private Begin begin;
	/**
	 * ネストされた構造
	 */
	private Term descent;
	private End end;
	
	public Nest() {
		this.begin = new Begin();
		begin.setUp(this);
	}
	
	/**
	 * 式を追記します。
	 * 処理上、Beginからの後続処理は、すべてNestへ委譲するのため、ここに定義する。
	 * @param exp
	 * @param values
	 * @return
	 */
	public Exp exp(String exp, Object... values) {
		Exp e = Exp.append(this, exp, values);
		setDescent(e);
		return e;
	}
	
	/**
	 * exp(String, Object...) のショートカットです.
	 * @param exp
	 * @param values
	 * @return
	 */
	public Exp e(String exp, Object... values) {
		return exp(exp, values);
	}

	/**
	 * 左括弧"("によるネストを開始します。
	 * 処理上、Beginからの後続処理は、すべてNestへ委譲するのため、ここに定義する。
	 * @return
	 */
	public Begin begin() {
		Nest n = Nest.appendNest(this);
		setDescent(n);
		return n.getBegin();
	}

	/**
	 * 式として、Nestの状態でand()は呼べない。
	 * 処理上、Endからの後続処理は、すべてNestへ委譲するのため、ここに定義する。
	 * @return
	 */
	public BinOp and() {
		return BinOp.upperReplace("and", this);
	}

	/**
	 * 式として、Nestの状態でor()は呼べない。
	 * 処理上、Endからの後続処理は、すべてNestへ委譲するのため、ここに定義する。
	 * @return
	 */
	public BinOp or() {
		return BinOp.upperReplace("or", this);
	}

	@Override
	void accept(TermVisitor v) {
		begin.accept(v);
		v.visit(this);
		if(descent == null) {
			throw new IllegalStateException("Missing element after left parenthesis");
		} else {
			descent.accept(v);
		}
		if(end == null) {
			throw new IllegalStateException("Missing end clause.");
		} else {
			end.accept(v);
		}
	}

	@Override
	void replace(Term oldTerm, Term newTerm) {
		if(descent.equals(oldTerm)) {			
			descent = newTerm;
			newTerm.setUp(this);
		}
	}
	
	/**
	 * 与えられたtermから始めて上向きにたどりながら、{@link Nest#isClosed()}がfalseに
	 * なる{@link Nest}を見つけ、そのオブジェクトに対し、{@link Nest#close()}をコールします。
	 * コールした後、{@link Nest#getEnd()}で得られるオブジェクトを返却します。
	 * そのような{@link Nest}が存在しない場合、例外を投げます。
	 * @param term 検索を開始するTerm
	 * @return {@link Nest#getEnd()}で得られるオブジェクト
	 */
	static End tryClose(ContainerTerm term) {
		ContainerTerm tempTerm = term;
		while(tempTerm != null) {
			if(tempTerm instanceof Nest) {
				Nest nest = (Nest) tempTerm;
				if(!nest.isClosed()) {
					nest.close();
					return nest.getEnd();
				}
			}
			tempTerm = tempTerm.getUp();
		}
		throw new IllegalStateException("Unmatched end clause.");
	}

	/**
	 * Nestを生成し、その親要素としてtermをセットします。
	 * @param term
	 * @return
	 */
	static Nest appendNest(ContainerTerm term)
	{
		Nest b = new Nest();
		b.setUp(term);
		return b;
	}
	
	public Begin getBegin() {
		return begin;
	}
	private End getEnd() {
		return end;
	}
	
	private void setDescent(Term descent) {
		this.descent = descent;
	}

	/**
	 * このネストが閉じられている(close()がすでに呼ばれている)場合はtrue、
	 * それ以外はfalseを返却する。
	 * @return true or false
	 */
	private boolean isClosed() {
		return end != null;
	}
	
	/**
	 * このネストを閉じる。
	 */
	private void close() {
		end = new End();
		end.setUp(this);
	}
}
