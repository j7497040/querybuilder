package com.icando.querybuilder;

import com.icando.querybuilder.subquery.BooleanBuilder;

/**
 * 二項演算子をあらわすクラス。
 * 演算子は文字列表現です。書式の規定はありません。
 */
public class BinOp extends ContainerTerm {

	/**
	 * 演算子 
	 */
	private String operator;
	
	/**
	 * 左オペランド
	 */
	private Term left;
	
	/**
	 * 右オペランド
	 */
	private Term right;

	/**
	 * 与えられた演算子によりBinOpを生成します。
	 * @param operator 演算子
	 */
	BinOp(String operator)
	{
		this.operator = operator;
	}

	/**
	 * 式を追記します。
	 * @param exp
	 * @param values
	 * @return
	 */
	public Exp exp(String exp, Object... values) {
		Exp e = Exp.append(this, exp, values);
		setRight(e);
		return e;
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
	public Begin begin() {
		Nest n = Nest.appendNest(this);
		setRight(n);
		return n.getBegin();
	}

	/**
	 * 左オペランドをセットする。
	 * @param left
	 */
	void setLeft(Term left) {
		this.left = left;
	}

	/**
	 * 右オペランドをセットする。
	 * @param right the right to set
	 */
	void setRight(Term right) {
		this.right = right;
	}
	
	/**
	 * 演算子を返す。
	 * @return
	 */
	public String getOperator() {
		return operator;
	}

	@Override
	void accept(TermVisitor v) {
		left.accept(v);
		v.visit(this);
		if(null == right) {
			throw new IllegalStateException("Missing right operand for binary operator.");
		} else {
			right.accept(v);
		}
	}

	@Override
	void replace(Term oldTerm, Term newTerm) {
		if(left.equals(oldTerm)) {
			left = newTerm;
			newTerm.setUp(this);
		} else if(right.equals(oldTerm)) {
			right = newTerm;
			newTerm.setUp(this);
		}
	}

	/**
	 * ヘルパーメソッド。
	 * operatorのBinOpを作り、そのleftにtermを代入する。
	 * termと、term.upの間にBinOpを挿入する。
	 * @param operator
	 * @param term
	 * @return
	 */
	static BinOp upperReplace(String operator, Term term) {
		BinOp a = new BinOp(operator);
		a.setLeft(term);
		term.getUp().replace(term, a);
		term.setUp(a);
			
		return a;
	}
}
