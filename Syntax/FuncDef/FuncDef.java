package Syntax.FuncDef;

import Syntax.AstNode;

public class FuncDef {
  private final boolean isDebugMode;
  private AstNode rootAst;

  public FuncDef(boolean isDebugMode, AstNode rootAst) {
    this.isDebugMode = isDebugMode;
    this.rootAst = rootAst;
    this.analysis();
  }

  private void analysis() {
  }
}
