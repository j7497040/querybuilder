package com.icando.querybuilder;


public class OptionalTermVisitor implements TermVisitor {
    
    private StringBuilder sb;

    public OptionalTermVisitor() {
        this.sb = new StringBuilder();
    }
    
    public String getQuery() {
        return sb.toString();
    }
    
    public void visit(Nest nest) {
        // do nothing.
    }
    
    public void visit(Begin begin) {
        // do nothing.
    }
    
    public void visit(End end) {
        // do nothing.
    }
    
    public void visit(BinOp binop) {
        // do nothing.
    }
    
    public void visit(Exp exp) {
        // do nothing.
    }
    
    public void visit(GroupBy groupBy) {
        sb.append(" ").append(groupBy.getOperator())
        .append(" ").append(groupBy.getExp());
    }
    
    public void visit(OrderBy orderBy) {
        sb.append(" ").append(orderBy.getOperator())
        .append(" ").append(orderBy.getExp());      
    }
    
}
