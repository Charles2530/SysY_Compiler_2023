package Syntax;

import Lexer.SymToken;

import java.util.ArrayList;

public class SyntaxAnalysis {
  private final ArrayList<SymToken> symTokens;
  private boolean isDebugMode;

  private AstNode RootAst = new AstNode("<CompUnit>");

  public SyntaxAnalysis(ArrayList<SymToken> symTokens, boolean isDebugMode) {
    this.symTokens = symTokens;
    this.isDebugMode = isDebugMode;
  }

  public void analysis() {
    AstRecursion astRecursion = new AstRecursion(symTokens, isDebugMode, RootAst);
    astRecursion.CompUnit();
  }

  public AstNode getAst() {
    return RootAst;
  }
}
