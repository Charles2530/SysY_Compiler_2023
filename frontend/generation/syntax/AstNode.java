package frontend.generation.syntax;

import frontend.generation.lexer.SymToken;

import java.util.ArrayList;

public class AstNode {
    /**
     * AstNode 用于存储语法分析的结果,s属于AST树的节点
     * grammarType 用于存储当前节点的语法类型,如<CompUnit>
     * childList 用于存储当前节点的子节点
     * symToken 用于存储当前节点的对应的词法分析终结符
     * span 用于存储当前节点的行号范围
     * parent 用于存储当前节点的父节点
     */
    private final String grammarType;
    private final ArrayList<AstNode> childList = new ArrayList<>();
    private SymToken symToken = null;
    private final Span span;
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

    /**
     * addChild() 用于为当前节点添加子节点
     */
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

    /**
     * deleteChild() 用于删除当前节点的某个子节点
     */
    public void deleteChild(AstNode astNode) {
        childList.remove(astNode);
    }

    public Span getSpan() {
        return span;
    }

    /**
     * isLeaf() 用于判断当前节点是否为叶子节点
     */
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
