package Syntax.MainFuncDef;

import Syntax.AstNode;

public class MainFuncDef {
  private final boolean isDebugMode;
  private AstNode rootAst;

  public MainFuncDef(boolean isDebugMode, AstNode rootAst) {
    this.isDebugMode = isDebugMode;
    this.rootAst = rootAst;
    this.analysis();
  }

  private void analysis() {
  }
}
