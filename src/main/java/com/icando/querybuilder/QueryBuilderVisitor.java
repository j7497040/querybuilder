package com.icando.querybuilder;

/**
 * 条件式を組み立てるクラスです。
 *
 */
public class QueryBuilderVisitor implements TermVisitor {

    private StringBuilder sb;
    
    public QueryBuilderVisitor() {
        this.sb = new StringBuilder();
    }
    
    public String getQuery() {
        return sb.toString().trim();
    }
    
    public String getQueryWithWhere() {
        return "where " + sb.toString().trim();
    }
    
    public void visit(Nest nest) {}

    public void visit(Begin begin) {
        sb.append("(");
    }
    
    public void visit(End end) {
        sb.append(")");
    }
    
    public void visit(BinOp binop) {
        sb.append(" ").append(binop.getOperator()).append(" ");
    }

    public void visit(Exp exp) {
        sb.append(exp.getExp());
    }
    
    public void visit(GroupBy groupBy) {
        // do nothing.
    }
    
    public void visit(OrderBy orderBy) {
        // do nothing.
    }
}
