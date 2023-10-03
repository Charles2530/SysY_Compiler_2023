package semantic;

import semantic.symbolTable.Symbol;
import semantic.symbolTable.SymbolTable;
import semantic.symbolTable.symbol.ConstSymbol;
import semantic.symbolTable.symbol.VarSymbol;
import semantic.utils.symCalc;
import syntax.AstNode;

import java.util.ArrayList;

public class SemanticAnalysisChecker {
    private AstNode rootAst;
    private SymbolTable symbolTable;

    public SemanticAnalysisChecker(AstNode rootAst, SymbolTable symbolTable) {
        this.rootAst = rootAst;
        this.symbolTable = symbolTable;
    }

    public Symbol createAnalysis(AstNode rootAst) {
        switch (rootAst.getGrammarType()) {
            //Decl.java
            case "<Decl>":
                return createDeclChecker(rootAst);
            //Definer.java
            case "<ConstDecl>":
                return createConstDeclChecker(rootAst);
            case "<ConstDef>":
                return createConstDefChecker(rootAst);
            case "<ConstInitVal>":
                return createConstInitValChecker(rootAst);
            case "<VarDecl>":
                return createVarDeclChecker(rootAst);
            case "<VarDef>":
                return createVarDefChecker(rootAst);
//            case "<InitVal>":
//                return createInitValChecker(rootAst);
            case "<Block>":
                return createBlockChecker(rootAst);
            case "<BlockItem>":
                return createBlockItemChecker(rootAst);
            case "<Stmt>":
                return createStmtChecker(rootAst);
            case "IFTK":
                return createIfStmtChecker(rootAst);
            case "FORTK":
                return createForStmtChecker(rootAst);
            case "BREAKTK":
                return createBreakStmtChecker(rootAst);
            case "CONTINUETK":
                return createContinueStmtChecker(rootAst);
            case "RETURNTK":
                return createReturnStmtChecker(rootAst);
            case "PRINTFTK":
                return createPrintStmtChecker(rootAst);
            case "<ForStmt>":
                return createForStmtValChecker(rootAst);
            case "<Exp>":
                return createExpChecker(rootAst);
            case "<Cond>":
                return createCondChecker(rootAst);
            case "<LVal>":
                return createLValChecker(rootAst);
            case "<PrimaryExp>":
                return createPrimaryExpChecker(rootAst);
            case "<Number>":
                return createNumberCallChecker(rootAst);
            case "<UnaryExp>":
                return createUnaryExpChecker(rootAst);
            case "<UnaryOp>":
                return createUnaryOpChecker(rootAst);
            case "<FuncRParams>":
                return createFuncRParamsChecker(rootAst);
            case "<MulExp>":
                return createMulExpChecker(rootAst);
            case "<AddExp>":
                return createAddExpChecker(rootAst);
            case "<RelExp>":
                return createRelExpChecker(rootAst);
            case "<EqExp>":
                return createEqExpChecker(rootAst);
            case "<LAndExp>":
                return createLAndExpChecker(rootAst);
            case "<LOrExp>":
                return createLOrExpChecker(rootAst);
//            case "<ConstExp>":
//                return createConstExpChecker(rootAst);
            case "<BType>":
                return createBTypeChecker(rootAst);
            case "IDENFR":
                return createIdentChecker(rootAst);
            //FuncDef.java
            case "<FuncDef>":
                return createFuncDefChecker(rootAst);
            case "<FuncType>":
                return createFuncTypeChecker(rootAst);
            case "<FuncFParams>":
                return createFuncFParamsChecker(rootAst);
            case "<FuncFParam>":
                return createFuncFParamChecker(rootAst);
            //MainFuncDef.java
            case "<MainFuncDef>":
                return createMainFuncDefChecker(rootAst);
            //Lexer_part
            case "INTTK":
                return createINTTKChecker(rootAst);
            case "VOIDTK":
                return createVOIDTKChecker(rootAst);
            case "MAINTK":
                return createMAINTKChecker(rootAst);
            case "LPARENT":
                return createLPARENTChecker(rootAst);
            case "RPARENT":
                return createRPARENTChecker(rootAst);
            case "LBRACE":
                return createLBRACEChecker(rootAst);
            case "RBRACE":
                return createRBRACEChecker(rootAst);
            case "LBRACK":
                return createLBRACKChecker(rootAst);
            case "RBRACK":
                return createRBRACKChecker(rootAst);
            case "SEMICN":
                return createSEMICNChecker(rootAst);
            case "COMMA":
                return createCOMMAChecker(rootAst);
            case "ASSIGN":
                return createASSIGNChecker(rootAst);
            case "PLUS":
                return createPLUSChecker(rootAst);
            case "MINU":
                return createMINUChecker(rootAst);
            case "INTCON":
                return createINTCONChecker(rootAst);
            case "NOT":
                return createNOTChecker(rootAst);
            case "DIV":
                return createDIVChecker(rootAst);
            case "MULT":
                return createMULTChecker(rootAst);
            case "MOD":
                return createMODChecker(rootAst);
            case "AND":
                return createANDChecker(rootAst);
            case "OR":
                return createORChecker(rootAst);
            case "NEQ":
                return createNEQChecker(rootAst);
            case "EQL":
                return createEQLChecker(rootAst);
            case "LSS":
                return createLSSChecker(rootAst);
            case "LEQ":
                return createLEQChecker(rootAst);
            case "GRE":
                return createGREChecker(rootAst);
            case "GEQ":
                return createGEQChecker(rootAst);
            case "STRCON":
                return createSTRCONChecker(rootAst);
            case "CONSTTK":
                return createCONSTTKChecker(rootAst);
            case "ELSETK":
                return createELSETKChecker(rootAst);
            case "GETINTTK":
                return createGETINTTKChecker(rootAst);
            default:
                return null;
        }
    }

    //Decl.java
    public Symbol createDeclChecker(AstNode rootAst) {
        return null;
    }

    //Definer.java
    public Symbol createConstDeclChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createConstDefChecker(AstNode rootAst) {
        String symbolName = rootAst.getChildList().get(0).getSymToken().getWord();
        Symbol.SymType symbolType = Symbol.SymType.CONST;
        return new ConstSymbol(symbolName, symbolType);
    }

    public Symbol createConstInitValChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createVarDeclChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createVarDefChecker(AstNode rootAst) {
        String symbolName = rootAst.getChildList().get(0).getSymToken().getWord();
        int dim = 0;
        Integer value = null;
        AstNode initValAst = null;
        int space = 1;
        for (AstNode astNode : rootAst.getChildList()) {
            if (astNode.getGrammarType().equals("<ConstExp>")) {
                dim++;
                space *= createConstExpChecker(astNode);
            }
            if (astNode.getGrammarType().equals("<InitVal>")) {
                initValAst = astNode;
            }
        }
        Symbol.SymType symbolType;
        if (dim == 0) {
            symbolType = Symbol.SymType.INT;
        } else {
            symbolType = Symbol.SymType.ARRAY;
        }
        Symbol initValue = null;
        if (initValAst != null) {
//            initValue = createInitValChecker(initValAst);
        }
        return new VarSymbol(symbolName, symbolType, dim, initValue, space);
    }

    public ArrayList<Integer> createInitValChecker(int dim, AstNode rootAst) {
        ArrayList<Integer> ans = new ArrayList<>();
        if (dim == 0) {
            ans.add(symCalc.calc(rootAst));
        } else {
            for (AstNode astNode : rootAst.getChildList()) {
                if (astNode.getGrammarType().equals("<InitVal>")) {
                    ans.addAll(createInitValChecker(dim - 1, astNode));
                }
            }
        }
        return ans;
    }

    public Symbol createBlockChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createBlockItemChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createStmtChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createIfStmtChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createForStmtChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createBreakStmtChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createContinueStmtChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createReturnStmtChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createPrintStmtChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createForStmtValChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createExpChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createCondChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createLValChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createPrimaryExpChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createNumberCallChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createUnaryExpChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createUnaryOpChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createFuncRParamsChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createMulExpChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createAddExpChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createRelExpChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createEqExpChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createLAndExpChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createLOrExpChecker(AstNode rootAst) {
        return null;
    }

    public int createConstExpChecker(AstNode rootAst) {
        return 0;
    }

    public Symbol createBTypeChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createIdentChecker(AstNode rootAst) {
        return null;
    }

    //FuncDef.java
    public Symbol createFuncDefChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createFuncTypeChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createFuncFParamsChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createFuncFParamChecker(AstNode rootAst) {
        return null;
    }

    //MainFuncDef.java
    public Symbol createMainFuncDefChecker(AstNode rootAst) {
        return null;
    }

    //Lexer_part
    public Symbol createINTTKChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createVOIDTKChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createMAINTKChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createLPARENTChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createRPARENTChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createLBRACEChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createRBRACEChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createLBRACKChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createRBRACKChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createSEMICNChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createCOMMAChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createASSIGNChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createPLUSChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createMINUChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createINTCONChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createNOTChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createDIVChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createMULTChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createMODChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createANDChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createORChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createNEQChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createEQLChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createLSSChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createLEQChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createGREChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createGEQChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createSTRCONChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createCONSTTKChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createELSETKChecker(AstNode rootAst) {
        return null;
    }

    public Symbol createGETINTTKChecker(AstNode rootAst) {
        return null;
    }
}
