package Syntax;

import Lexer.SymToken;
import Syntax.Decl.Decl;
import Syntax.FuncDef.FuncDef;
import Syntax.MainFuncDef.MainFuncDef;

import java.util.ArrayList;

public class AstRecursion {
  private static ArrayList<SymToken> symTokens;
  private static int symPos;
  private static SymToken preSymToken;
  private final boolean isDebugMode;
  private AstNode rootAst;

  public AstRecursion(ArrayList<SymToken> symTokens, boolean isDebugMode, AstNode rootAst) {
    AstRecursion.symTokens = symTokens;
    symPos = 0;
    preSymToken = symTokens.get(symPos);
    this.isDebugMode = isDebugMode;
    this.rootAst = rootAst;
  }

  public static void nextSym() {
    symPos++;
    preSymToken = symTokens.get(symPos);
  }

  public static SymToken getPreSymToken() {
    return preSymToken;
  }

  public void CompUnit() {
    new Decl(isDebugMode, rootAst);
    new FuncDef(isDebugMode, rootAst);
    new MainFuncDef(isDebugMode, rootAst);
  }
}
