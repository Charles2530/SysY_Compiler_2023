package syntax;

import lexer.SymToken;

import java.util.ArrayList;

public class AstNode {
    private String grammarType;
    private ArrayList<AstNode> childList = new ArrayList<>();

    private SymToken symToken = null;

    public AstNode(String grammarType) {
        this.grammarType = grammarType;
        if (!grammarType.matches("<.*>")) {
            this.symToken = AstRecursion.getPreSymToken();
        }
    }

    public void addChild(AstNode astNode) {
        childList.add(astNode);
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
