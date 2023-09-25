package syntax;

import lexer.SymToken;

import java.util.ArrayList;

public class AstNode {
    private String grammarType;
    private AstNode parent;
    private ArrayList<AstNode> childList = new ArrayList<>();

    private SymToken symToken = null;

    public AstNode(String grammarType) {
        this.grammarType = grammarType;
        this.parent = null;
        if (!grammarType.matches("<.*>")) {
            this.symToken = AstRecursion.getPreSymToken();
        }
    }

    public void addChild(AstNode astNode) {
        astNode.setParent(this);
        childList.add(astNode);
    }

    private void setParent(AstNode astNode) {
        this.parent = astNode;
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
}
