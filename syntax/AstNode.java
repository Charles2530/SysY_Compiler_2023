package syntax;

import lexer.SymToken;

import java.util.ArrayList;

public class AstNode {
    private String grammarType;
    private ArrayList<AstNode> childList = new ArrayList<>();
    private SymToken symToken = null;
    private Span span;
    private AstNode parent;

    public AstNode(String grammarType) {
        this.grammarType = grammarType;
        if (!grammarType.matches("<.*>")) {
            this.symToken = AstRecursion.getPreSymToken();
            this.span = new Span(symToken.getLineNum(), symToken.getLineNum());
        } else {
            this.span = new Span(-1, -1);
            span.setStartLine(AstRecursion.getPreSymToken().getLineNum());
            AstRecursion.setPreviousNoTerminalAst(this);
        }
    }

    public void setParent(AstNode parent) {
        this.parent = parent;
    }

    public void addChild(AstNode astNode) {
        childList.add(astNode);
        astNode.setParent(this);
        if (astNode.getSpan().getEndLine() != -1) {
            AstNode t = this;
            while (t != null && t.getSpan().getEndLine() < astNode.getSpan().getEndLine()) {
                t.getSpan().setEndLine(astNode.getSpan().getEndLine());
                t = t.parent;
            }
        }
    }

    public Span getSpan() {
        return span;
    }

    public boolean isLeaf() {
        return childList.isEmpty();
    }

    public ArrayList<AstNode> getChildList() {
        return childList;
    }

    public String getGrammarType() {
        return grammarType;
    }

    public SymToken getSymToken() {
        return symToken;
    }

    public AstNode getParent() {
        return parent;
    }
}
