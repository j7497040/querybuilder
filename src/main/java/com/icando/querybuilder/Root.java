package com.icando.querybuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * QueryBuilderの内部表現は木構造ですが、その根を表すクラスです。
 *
 */
class Root extends ContainerTerm {

    private Term next;
    
    /**
     * 条件式終了後に付加するオプション(optional term)を格納する。
     */
    private List<Term> optionalTerms = new ArrayList<Term>();
    
    Exp exp(String exp, Object... values) {
        Exp e = Exp.append(this, exp, values);
        next = e;
        return e;
    }
    
    Nest begin() {
        Nest n = Nest.appendNest(this);
        next = n;
        return n;
    }

    void setNext(Term next) {
        this.next = next;
    }
    
    @Override
    void accept(TermVisitor v) {
        if(null != next) {
            next.accept(v);
        }
        for(Term term:optionalTerms) {
            term.accept(v);
        }
    }

    @Override
    void replace(Term oldTerm, Term newTerm) {
        if(next.equals(oldTerm)) {
            next = newTerm;
            newTerm.setUp(this);
        }
    }
    
    /**
     * 次の式が存在するか判定する.
     * @return Termが存在する場合true
     * Termが存在しない場合false
     */
    boolean hasNextTerm() {
        return next != null;
    }
    
    /**
     * Term から木を上方にたどり、Root を見つけて返却します。
     * Root が見つからない場合は、null を返却します。
     * @param term 探索を開始する Term。term 自身が Root オブジェクトでもよい。
     * @return Root オブジェクト。それが見つからない場合は、null。
     */
    private static Root getRoot(Term term) {
        while(term.getUp() != null) {
            term = term.getUp();
        }
        if(term instanceof Root) {
            return (Root)term;
        } else {
            return null;
        }
    }

    /**
     * searchFrom から上方へ検索し、Root オブジェクトを見つけ、Root オブジェクトに optionalTerm を
     * を追加する。
     * @param <T> optionalTerm の型
     * @param searchFrom Root オブジェクトを検索する起点。searchForm が Root オブジェクトでもよい。
     * @param optionalTerm 追加する optionalTerm
     * @return 追加した optionalTerm
     */
    static <T extends Term> T appendOptionalTerm(Term searchFrom, T optionalTerm) {
        Root root = getRoot(searchFrom);
        if(root == null) {
            throw new IllegalStateException("root not found.");
        }
        optionalTerm.setUp(root);
        root.optionalTerms.add(optionalTerm);
        return optionalTerm;
    }   
}
