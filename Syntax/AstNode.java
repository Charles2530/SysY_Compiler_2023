package Syntax;

import java.util.ArrayList;

public class AstNode {
  private String grammarType;
  private AstNode parent;
  private ArrayList<AstNode> childList = new ArrayList<>();

  public AstNode(String grammarType) {
    this.grammarType = grammarType;
    this.parent = null;
  }

  public void addChild(AstNode astNode) {
    astNode.setParent(this);
    childList.add(astNode);
  }

  private void setParent(AstNode astNode) {
    this.parent = astNode;
  }
}
