package com.icando.querybuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * このクラスは、{@link Term}ツリーを走査し、{@link Exp}オブジェクトの{@link Exp#getExp()}
 * で得られる式からパラメータ名と、{@link Exp#getValue()}から得られるパラメータへの設定値を含む
 * {@link Map}を返却します。
 * このクラスが認識できるパラメータ名は、`:'で始まり、0-9a-zA-Z_の文字クラスで構成される
 * 文字列です。1つの{@link Exp}オブジェクトが複数のパラメータを含む場合、そのすべてのパラメータ
 * と設定値のペアを認識できます。パラメータの数と設定値の数は正確に一致している必要があります。
 * そうでない場合は、実行時例外がスローされます。
 * 
 * 
 * @version 1.1
 */
public class ParamAccumulateVisitor implements TermVisitor {
    
    /**
     * 列挙型を文字列に変換するかどうか。
     */
    private boolean enumToStr;
    
	private Pattern pattern;
	
	private Map<String, Object> params;
	
	public ParamAccumulateVisitor() {
		this(false);
	}
	
	/**
	 * 
	 * @param enumToStr true: 文字列に変換する。
	 */
	public ParamAccumulateVisitor(boolean enumToStr) {
	    this.enumToStr = enumToStr;
        pattern = Pattern.compile(":(\\w+)");
        params = new HashMap<String, Object>();
    }
	
	public void visit(Nest nest) {}

	public void visit(Begin begin) {}

	public void visit(End end) {}

	public void visit(BinOp binop) {}

	public void visit(Exp exp) {
		Matcher m = pattern.matcher(exp.getExp());
		int valuesSize = exp.valuesSize();
		int valuesIndex = 0;
		while(m.find()) {
			String param = m.group(1);
			if(valuesIndex < valuesSize) {
			    Object value = exp.getValueAt(valuesIndex);
			    if(isEnumToStr() && value != null && value instanceof Enum) {
			        // 列挙型の場合、文字列に変換する。
			        String strValue = ((Enum<?>) value).name();
			        params.put(param, strValue);
			    } else {
			        params.put(param, value);
			    }
			    
			} else {
				// パラメータ  > 設定値
				throw new IllegalArgumentException("Too few values in exp.");
			}
			++valuesIndex;
		}
		if(valuesIndex != valuesSize) {
			// パラメータ < 設定値
			throw new IllegalArgumentException("Too few parameters in exp.");
		}
	}

	public void visit(GroupBy groupBy) {}

	public void visit(OrderBy orderBy) {}

	public Map<String, Object> getParams() {
		return params;
	}
	
    /**
     * 列挙型を文字列に変換するかどうか。
     * @return true: 文字列に変換する。
     */
    public boolean isEnumToStr() {
        return enumToStr;
    }
}
