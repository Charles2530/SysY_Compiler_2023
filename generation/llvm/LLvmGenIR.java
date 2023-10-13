package generation.llvm;

import generation.GenerationMain;
import generation.utils.IrNameController;
import generation.utils.IrType;
import generation.utils.irtype.VarType;
import generation.value.Value;
import generation.value.construction.BasicBlock;
import generation.value.construction.Constant;
import generation.value.construction.Param;
import generation.value.construction.user.Function;
import generation.value.construction.user.Instr;
import generation.value.instr.*;
import semantic.SemanticAnalysisChecker;
import semantic.symtable.Symbol;
import semantic.symtable.SymbolTable;
import semantic.symtable.symbol.FuncSymbol;
import semantic.symtable.symbol.VarSymbol;
import syntax.AstNode;

import java.util.ArrayList;

public class LLvmGenIR {
    private final SemanticAnalysisChecker semanticAnalysisChecker;

    public LLvmGenIR() {
        this.semanticAnalysisChecker = new SemanticAnalysisChecker();
    }

    public Value genIrAnalysis(AstNode rootAst) {
        return switch (rootAst.getGrammarType()) {
            //Decl.java
            case "<Decl>" -> genIrDeclChecker(rootAst);
            //Definer.java
            case "<ConstDecl>" -> genIrConstDeclChecker(rootAst);
            case "<ConstDef>" -> genIrConstDefChecker(rootAst);
            case "<ConstInitVal>" -> genIrConstInitValChecker(rootAst);
            case "<VarDecl>" -> genIrVarDeclChecker(rootAst);
            case "<VarDef>" -> genIrVarDefChecker(rootAst);
            case "<InitVal>" -> genIrInitValChecker(rootAst);
            case "<Block>" -> genIrBlockChecker(rootAst);
            case "<BlockItem>" -> genIrBlockItemChecker(rootAst);
            case "<Stmt>" -> genIrStmtChecker(rootAst);
            case "IFTK" -> genIrIfStmtChecker(rootAst);
            case "FORTK" -> genIrForStmtChecker(rootAst);
            case "BREAKTK" -> genIrBreakStmtChecker(rootAst);
            case "CONTINUETK" -> genIrContinueStmtChecker(rootAst);
            case "RETURNTK" -> genIrReturnStmtChecker(rootAst);
            case "PRINTFTK" -> genIrPrintStmtChecker(rootAst);
            case "<ForStmt>" -> genIrForStmtValChecker(rootAst);
            case "<Exp>" -> genIrExpChecker(rootAst);
            case "<Cond>" -> genIrCondChecker(rootAst);
            case "<LVal>" -> genIrLValChecker(rootAst);
            case "<PrimaryExp>" -> genIrPrimaryExpChecker(rootAst);
            case "<Number>" -> genIrNumberCallChecker(rootAst);
            case "<UnaryExp>" -> genIrUnaryExpChecker(rootAst);
            case "<UnaryOp>" -> genIrUnaryOpChecker(rootAst);
            case "<FuncRParams>" -> genIrFuncRParamsChecker(rootAst);
            case "<MulExp>" -> genIrMulExpChecker(rootAst);
            case "<AddExp>" -> genIrAddExpChecker(rootAst);
            case "<RelExp>" -> genIrRelExpChecker(rootAst);
            case "<EqExp>" -> genIrEqExpChecker(rootAst);
            case "<LAndExp>" -> genIrLAndExpChecker(rootAst);
            case "<LOrExp>" -> genIrLOrExpChecker(rootAst);
            case "<ConstExp>" -> genIrConstExpChecker(rootAst);
            case "<BType>" -> genIrBTypeChecker(rootAst);
            case "IDENFR" -> genIrIdentChecker(rootAst);
            //FuncDef.java
            case "<FuncDef>" -> genIrFuncDefChecker(rootAst);
            case "<FuncType>" -> genIrFuncTypeChecker(rootAst);
            case "<FuncFParams>" -> genIrFuncFParamsChecker(rootAst);
            case "<FuncFParam>" -> genIrFuncFParamChecker(rootAst);
            //MainFuncDef.java
            case "<MainFuncDef>" -> genIrMainFuncDefChecker(rootAst);
            //Lexer_part
            case "INTTK" -> genIrINTTKChecker(rootAst);
            case "VOIDTK" -> genIrVOIDTKChecker(rootAst);
            case "MAINTK" -> genIrMAINTKChecker(rootAst);
            case "LPARENT" -> genIrLPARENTChecker(rootAst);
            case "RPARENT" -> genIrRPARENTChecker(rootAst);
            case "LBRACE" -> genIrLBRACEChecker(rootAst);
            case "RBRACE" -> genIrRBRACEChecker(rootAst);
            case "LBRACK" -> genIrLBRACKChecker(rootAst);
            case "RBRACK" -> genIrRBRACKChecker(rootAst);
            case "SEMICN" -> genIrSEMICNChecker(rootAst);
            case "COMMA" -> genIrCOMMAChecker(rootAst);
            case "ASSIGN" -> genIrASSIGNChecker(rootAst);
            case "PLUS" -> genIrPLUSChecker(rootAst);
            case "MINU" -> genIrMINUChecker(rootAst);
            case "INTCON" -> genIrINTCONChecker(rootAst);
            case "NOT" -> genIrNOTChecker(rootAst);
            case "DIV" -> genIrDIVChecker(rootAst);
            case "MULT" -> genIrMULTChecker(rootAst);
            case "MOD" -> genIrMODChecker(rootAst);
            case "AND" -> genIrANDChecker(rootAst);
            case "OR" -> genIrORChecker(rootAst);
            case "NEQ" -> genIrNEQChecker(rootAst);
            case "EQL" -> genIrEQLChecker(rootAst);
            case "LSS" -> genIrLSSChecker(rootAst);
            case "LEQ" -> genIrLEQChecker(rootAst);
            case "GRE" -> genIrGREChecker(rootAst);
            case "GEQ" -> genIrGEQChecker(rootAst);
            case "STRCON" -> genIrSTRCONChecker(rootAst);
            case "CONSTTK" -> genIrCONSTTKChecker(rootAst);
            case "ELSETK" -> genIrELSETKChecker(rootAst);
            case "GETINTTK" -> genIrGETINTTKChecker(rootAst);
            default -> {
                GenerationMain.preTraverse(rootAst);
                yield null;
            }
        };
    }

    //Decl.java
    private Value genIrDeclChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    //Definer.java
    private Value genIrConstDeclChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrConstDefChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrConstInitValChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrVarDeclChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrVarDefChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrInitValChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrBlockChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrBlockItemChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrStmtChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrIfStmtChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrForStmtChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrBreakStmtChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrContinueStmtChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrReturnStmtChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrPrintStmtChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrForStmtValChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrExpChecker(AstNode rootAst) {
        Value ans = genIrAnalysis(rootAst.getChildList().get(0));
        GenerationMain.preTraverse(rootAst);
        return ans;
    }

    private Value genIrCondChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrLValChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    /*TODO:change later */
    private Value genIrPrimaryExpChecker(AstNode rootAst) {
        AstNode child = rootAst.getChildList().get(0);
        Value ans;
        if (child.getGrammarType().equals("LPARENT")) {
            ans = genIrAnalysis(rootAst.getChildList().get(1));
        } else if (child.getGrammarType().equals("<LVal>")) {
            ans = genIrAnalysis(child);
        } else {
            ans = genIrAnalysis(child);
        }
        GenerationMain.preTraverse(rootAst);
        return ans;
    }

    private Value genIrNumberCallChecker(AstNode rootAst) {
        Constant constant = new Constant(rootAst.getChildList().get(0)
                .getSymToken().getWord(), new VarType(32));
        GenerationMain.preTraverse(rootAst);
        return constant;
    }

    private Value genIrUnaryExpChecker(AstNode rootAst) {
        Value ans = genIrAnalysis(rootAst.getChildList().get(0));
        if (rootAst.getChildList().get(0).getGrammarType().equals("<PrimaryExp>")) {
            GenerationMain.preTraverse(rootAst);
            return ans;
        } else if (rootAst.getChildList().get(0).getGrammarType().equals("<UnaryOp>")) {
            ans = genIrAnalysis(rootAst.getChildList().get(1));
            if (rootAst.getChildList().get(0).getChildList().
                    get(0).getGrammarType().equals("MINU")) {
                ans = new CalcInstr(IrNameController.getLocalVarName(),
                        "sub", new Constant("0", new VarType(32)), ans);
            } else {
                ans = new IcmpInstr(IrNameController.getLocalVarName(),
                        "eq", ans, new Constant("0", new VarType(32)));
                ans = new ZextInstr(IrNameController.getLocalVarName(),
                        "zext", ans, new VarType(32));
            }
            GenerationMain.preTraverse(rootAst);
            return ans;
        } else {
            FuncSymbol funcSymbol = (FuncSymbol) SymbolTable.getSymByName(
                    rootAst.getChildList().get(0).getSymToken().getWord());
            Function function = funcSymbol.getfunction();
            ArrayList<Value> params = new ArrayList<>();
            if (rootAst.getChildList().get(2).getGrammarType().equals("<FuncRParams>")) {
                for (AstNode child : rootAst.getChildList().get(2).getChildList()) {
                    if (child.getGrammarType().equals("<Exp>")) {
                        params.add(genIrAnalysis(child));
                    }
                }
            }
            ans = new CallInstr(IrNameController.getLocalVarName(), "call",
                    function, params);
            GenerationMain.preTraverse(rootAst);
            return ans;
        }
    }

    private Value genIrUnaryOpChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrFuncRParamsChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrMulExpChecker(AstNode rootAst) {
        Value ans = genIrAnalysis(rootAst.getChildList().get(0));
        for (int i = 1; i < rootAst.getChildList().size(); i++) {
            if (rootAst.getChildList().get(i).getGrammarType().equals("MULT")) {
                Value operand2 = genIrAnalysis(rootAst.getChildList().get(++i));
                ans = new CalcInstr(IrNameController.getLocalVarName(), "mul", ans, operand2);
            } else if (rootAst.getChildList().get(i).getGrammarType().equals("DIV")) {
                Value operand2 = genIrAnalysis(rootAst.getChildList().get(++i));
                ans = new CalcInstr(IrNameController.getLocalVarName(), "sdiv", ans, operand2);
            } else {
                Value operand2 = genIrAnalysis(rootAst.getChildList().get(++i));
                ans = new CalcInstr(IrNameController.getLocalVarName(), "srem", ans, operand2);
            }
        }
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrAddExpChecker(AstNode rootAst) {
        Value ans = genIrAnalysis(rootAst.getChildList().get(0));
        for (int i = 1; i < rootAst.getChildList().size(); i++) {
            Value operand2 = genIrAnalysis(rootAst.getChildList().get(++i));
            if (rootAst.getChildList().get(i).getGrammarType().equals("PLUS")) {
                ans = new CalcInstr(IrNameController.getLocalVarName(), "add", ans, operand2);
            } else {
                ans = new CalcInstr(IrNameController.getLocalVarName(), "sub", ans, operand2);
            }
        }
        GenerationMain.preTraverse(rootAst);
        return ans;
    }

    private Value genIrRelExpChecker(AstNode rootAst) {
        Value ans = genIrAnalysis(rootAst.getChildList().get(0));
        if (rootAst.getChildList().size() == 1) {
            GenerationMain.preTraverse(rootAst);
            return ans;
        }
        for (int i = 1; i < rootAst.getChildList().size(); i++) {
            if (!ans.getType().isInt32()) {
                ans = new ZextInstr(IrNameController.getLocalVarName(),
                        "zext", ans, new VarType(32));
            }
            Value res = genIrAnalysis(rootAst.getChildList().get(++i));
            if (!res.getType().isInt32()) {
                res = new ZextInstr(IrNameController.getLocalVarName(),
                        "zext", res, new VarType(32));
            }
            ans = switch (rootAst.getChildList().get(i).getGrammarType()) {
                case "LSS" -> new IcmpInstr(IrNameController.getLocalVarName(), "slt", ans, res);
                case "LEQ" -> new IcmpInstr(IrNameController.getLocalVarName(), "sle", ans, res);
                case "GRE" -> new IcmpInstr(IrNameController.getLocalVarName(), "sgt", ans, res);
                case "GEQ" -> new IcmpInstr(IrNameController.getLocalVarName(), "sge", ans, res);
                default -> ans;
            };
        }
        GenerationMain.preTraverse(rootAst);
        return ans;
    }

    private Value genIrEqExpChecker(AstNode rootAst) {
        Value ans = genIrAnalysis(rootAst.getChildList().get(0));
        if (rootAst.getChildList().size() == 1) {
            if (ans.getType().isInt32()) {
                ans = new IcmpInstr(IrNameController.getLocalVarName(),
                        "ne", ans, new Constant("0", new VarType(32)));
            }
            GenerationMain.preTraverse(rootAst);
            return ans;
        }
        for (int i = 1; i < rootAst.getChildList().size(); i++) {
            if (!ans.getType().isInt32()) {
                ans = new ZextInstr(IrNameController.getLocalVarName(),
                        "zext", ans, new VarType(32));
            }
            Value res = genIrAnalysis(rootAst.getChildList().get(++i));
            if (!res.getType().isInt32()) {
                res = new ZextInstr(IrNameController.getLocalVarName(),
                        "zext", res, new VarType(32));
            }
            if (rootAst.getChildList().get(i).getGrammarType().equals("EQL")) {
                ans = new IcmpInstr(IrNameController.getLocalVarName(), "eq", ans, res);
            } else if (rootAst.getChildList().get(i).getGrammarType().equals("NEQ")) {
                ans = new IcmpInstr(IrNameController.getLocalVarName(), "ne", ans, res);
            }
        }
        GenerationMain.preTraverse(rootAst);
        return ans;
    }

    private Value genIrLAndExpChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrLOrExpChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrConstExpChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrBTypeChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrIdentChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    //FuncDef.java
    private Value genIrFuncDefChecker(AstNode rootAst) {
        SymbolTable.setGlobalArea(false);
        FuncSymbol funcSymbol = (FuncSymbol) semanticAnalysisChecker.createFuncDefChecker(rootAst);
        SymbolTable.addSymbol(funcSymbol);
        SymbolTable.setCurrentFunc(funcSymbol);
        SymbolTable.createStackSymbolTable();
        String funcName = IrNameController.getFuncName(funcSymbol.getSymbolName());
        IrType returnType = funcSymbol.getReturnType().equals(Symbol.SymType.INT) ?
                new VarType(32) : new VarType(0);
        Function function = new Function(funcName, returnType);
        funcSymbol.setFunction(function);
        IrNameController.setCurrentFunc(function);
        String blockName = IrNameController.getBlockName();
        BasicBlock basicBlock = new BasicBlock(blockName);
        IrNameController.setCurrentBlock(basicBlock);
        GenerationMain.preTraverse(rootAst);
        BasicBlock lastBlock = IrNameController.getCurrentBlock();
        if (lastBlock.isEmpty() || !(lastBlock.getLastInstr() instanceof RetInstr)) {
            if (returnType.isInt32()) {
                new RetInstr(IrNameController.getLocalVarName(),
                        "return", new Constant("0", new VarType(32)));
            } else {
                new RetInstr(IrNameController.getLocalVarName(),
                        "return", null);
            }
        }
        SymbolTable.destroyStackSymbolTable();
        return null;
    }

    private Value genIrFuncTypeChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrFuncFParamsChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrFuncFParamChecker(AstNode rootAst) {
        VarSymbol symbol = (VarSymbol) semanticAnalysisChecker.createFuncFParamChecker(rootAst);
        SymbolTable.addSymbol(symbol);
        IrType type = symbol.getDim() == 0 ? new VarType(32) : new VarType(0);
        Param param = new Param(type, IrNameController.getParamName());
        if (param.getType().isInt32()) {
            Instr allocaInstr = new AllocaInstr(IrNameController.getLocalVarName(),
                    "alloca", param.getType());
            symbol.setValue(allocaInstr);
            allocaInstr = new StoreInstr(IrNameController.getLocalVarName(),
                    "store", param, allocaInstr);
        } else {
            symbol.setValue(param);
        }
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    //MainFuncDef.java
    private Value genIrMainFuncDefChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    //Lexer_part
    private Value genIrINTTKChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrVOIDTKChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrMAINTKChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrLPARENTChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrRPARENTChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrLBRACEChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrRBRACEChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrLBRACKChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrRBRACKChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrSEMICNChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrCOMMAChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrASSIGNChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrPLUSChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrMINUChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrINTCONChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrNOTChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrDIVChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrMULTChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrMODChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrANDChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrORChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrNEQChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrEQLChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrLSSChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrLEQChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrGREChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrGEQChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrSTRCONChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrCONSTTKChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrELSETKChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrGETINTTKChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }
}
