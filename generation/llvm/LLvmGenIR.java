package generation.llvm;

import generation.GenerationMain;
import generation.utils.IrNameController;
import generation.utils.IrType;
import generation.utils.irtype.ArrayType;
import generation.utils.irtype.VarType;
import generation.value.Value;
import generation.value.construction.BasicBlock;
import generation.value.construction.Constant;
import generation.value.construction.Param;
import generation.value.construction.procedure.Loop;
import generation.value.construction.user.Function;
import generation.value.construction.user.GlobalVar;
import generation.value.construction.user.Instr;
import generation.value.instr.*;
import iostream.declare.GetIntDeclare;
import iostream.declare.PutIntDeclare;
import iostream.declare.PutStrDeclare;
import semantic.SemanticAnalysisChecker;
import semantic.symtable.Symbol;
import semantic.symtable.SymbolTable;
import semantic.symtable.symbol.ConstSymbol;
import semantic.symtable.symbol.FuncSymbol;
import semantic.symtable.symbol.VarSymbol;
import syntax.AstNode;

import java.util.ArrayList;
import java.util.List;

public class LLvmGenIR {
    private final SemanticAnalysisChecker semanticAnalysisChecker;

    public LLvmGenIR() {
        this.semanticAnalysisChecker = new SemanticAnalysisChecker();
    }

    public Value genIrAnalysis(AstNode rootAst) {
        return switch (rootAst.getGrammarType()) {
            case "<CompUnit>" -> genIrCompUnitChecker(rootAst);
            //Definer.java
            case "<ConstDef>" -> genIrConstDefChecker(rootAst);
            case "<VarDef>" -> genIrVarDefChecker(rootAst);
            case "<Block>" -> genIrBlockChecker(rootAst);
            case "IFTK" -> genIrIfStmtChecker(rootAst);
            case "FORTK" -> genIrForStmtChecker(rootAst);
            case "BREAKTK" -> genIrBreakStmtChecker(rootAst);
            case "CONTINUETK" -> genIrContinueStmtChecker(rootAst);
            case "RETURNTK" -> genIrReturnStmtChecker(rootAst);
            case "PRINTFTK" -> genIrPrintStmtChecker(rootAst);
            case "<Exp>" -> genIrExpChecker(rootAst);
            case "<PrimaryExp>" -> genIrPrimaryExpChecker(rootAst);
            case "<Number>" -> genIrNumberCallChecker(rootAst);
            case "<UnaryExp>" -> genIrUnaryExpChecker(rootAst);
            case "<MulExp>" -> genIrMulExpChecker(rootAst);
            case "<AddExp>" -> genIrAddExpChecker(rootAst);
            case "<RelExp>" -> genIrRelExpChecker(rootAst);
            case "<EqExp>" -> genIrEqExpChecker(rootAst);
            //FuncDef.java
            case "<FuncDef>" -> genIrFuncDefChecker(rootAst);
            case "<FuncFParam>" -> genIrFuncFParamChecker(rootAst);
            //MainFuncDef.java
            case "<MainFuncDef>" -> genIrMainFuncDefChecker(rootAst);
            //Lexer_part
            case "ASSIGN" -> genIrAssignChecker(rootAst);
            case "GETINTTK" -> genIrGetIntChecker(rootAst);
            default -> {
                GenerationMain.preTraverse(rootAst);
                yield null;
            }
        };
    }

    private Value genIrCompUnitChecker(AstNode rootAst) {
        SymbolTable.setGlobalArea(true);
        SymbolTable.createStackSymbolTable();
        GenerationMain.preTraverse(rootAst);
        SymbolTable.destroyStackSymbolTable();
        return null;
    }

    //Definer.java
    private Value genIrConstDefChecker(AstNode rootAst) {
        ConstSymbol constSymbol = (ConstSymbol) semanticAnalysisChecker
                .createConstDefChecker(rootAst);
        SymbolTable.addSymbol(constSymbol);
        if (constSymbol.getSymbolLevel().equals(0)) {
            String globalVarName = IrNameController.getGlobalVarName(constSymbol.getSymbolName());
            GlobalVar globalVar = null;
            // =new GlobalVar(new PointerType(IrType.getIrType(constSymbol.getSymbolType())),
            // globalVarName,null);
            constSymbol.setValue(globalVar);
        } else {
            Instr instr;
            if (constSymbol.getDim().equals(0)) {
                instr = new AllocaInstr(IrNameController.getLocalVarName(),
                        "alloca", new VarType(32));
                constSymbol.setValue(instr);
                Value value = null;
                //=rootAst.getChildList().get(rootAst.getChildList().size() - 1);//Error
                new StoreInstr(IrNameController.getLocalVarName(),
                        "store", value, instr);

            } else {
                instr = new AllocaInstr(IrNameController.getLocalVarName(), "alloca",
                        new ArrayType(constSymbol.getSpaceTot(), new VarType(32)));
                constSymbol.setValue(instr);
                Value pointer = instr;
                ArrayList<Value> valuelist = null;
                //=rootAst.getChildList().get(rootAst.getChildList().size() - 1);//Error
                int off = 0;
                for (Value value : valuelist) {
                    instr = new GetEleInstr(IrNameController.getLocalVarName(), "getEle",
                            pointer, new Constant(String.valueOf(off), new VarType(32)));
                    new StoreInstr(IrNameController.getLocalVarName(),
                            "store", value, instr);
                    off++;
                }
            }
        }
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    /*TODO:change later*/
    private Value genIrVarDefChecker(AstNode rootAst) {
        VarSymbol varSymbol = (VarSymbol) semanticAnalysisChecker.createVarDefChecker(rootAst);
        SymbolTable.addSymbol(varSymbol);
        if (varSymbol.getSymbolLevel().equals(0)) {
            String globalVarName = IrNameController.getGlobalVarName(varSymbol.getSymbolName());
            GlobalVar globalVar = null;
            // =new GlobalVar(new PointerType(IrType.getIrType(varSymbol.getSymbolType())),
            // globalVarName,null);
            varSymbol.setValue(globalVar);
        } else {
            Instr instr;
            if (varSymbol.getDim().equals(0)) {
                instr = new AllocaInstr(IrNameController.getLocalVarName(),
                        "alloca", new VarType(32));
                varSymbol.setValue(instr);
                if (rootAst.getChildList().get(rootAst.getChildList().size() - 1)
                        .getGrammarType().equals("<InitVal>")) {
                    Value value = null;
                    //=rootAst.getChildList().get(rootAst.getChildList().size() - 1);//Error
                    new StoreInstr(IrNameController.getLocalVarName(),
                            "store", value, instr);

                }
            } else {
                instr = new AllocaInstr(IrNameController.getLocalVarName(), "alloca",
                        new ArrayType(varSymbol.getSpaceTot(), new VarType(32)));
                varSymbol.setValue(instr);
                if (rootAst.getChildList().get(rootAst.getChildList().size() - 1)
                        .getGrammarType().equals("<InitVal>")) {
                    Value pointer = instr;
                    ArrayList<Value> valuelist = null;
                    //=rootAst.getChildList().get(rootAst.getChildList().size() - 1);//Error
                    int off = 0;
                    for (Value value : valuelist) {
                        instr = new GetEleInstr(IrNameController.getLocalVarName(), "getEle",
                                pointer, new Constant(String.valueOf(off), new VarType(32)));
                        new StoreInstr(IrNameController.getLocalVarName(),
                                "store", value, instr);
                        off++;
                    }
                }
            }
        }
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrBlockChecker(AstNode rootAst) {
        SymbolTable.createStackSymbolTable();
        GenerationMain.preTraverse(rootAst);
        SymbolTable.destroyStackSymbolTable();
        return null;
    }

    /*TODO:change later*/
    private Value genIrIfStmtChecker(AstNode sonAst) {
        AstNode rootAst = sonAst.getParent();
        BasicBlock thenblock = new BasicBlock(IrNameController.getBlockName());
        if (rootAst.getChildList().size() > 5) {
            BasicBlock elseblock = new BasicBlock(IrNameController.getBlockName());
            BasicBlock followblock = new BasicBlock(IrNameController.getBlockName());
            //((CondExp) rootAst.getChildList().get(2)).genIrForCond(thenblock, elseblock);
            IrNameController.setCurrentBlock(thenblock);
            genIrAnalysis(rootAst.getChildList().get(4));
            new JumpInstr(IrNameController.getLocalVarName(), followblock);
            IrNameController.setCurrentBlock(elseblock);
            genIrAnalysis(rootAst.getChildList().get(6));
            new JumpInstr(IrNameController.getLocalVarName(), followblock);
            IrNameController.setCurrentBlock(followblock);
            IrNameController.setCurrentBlock(followblock);
        } else {
            BasicBlock followblock = new BasicBlock(IrNameController.getBlockName());
            //((CondExp) rootAst.getChildList().get(2)).genIrForCond(thenblock, followblock);
            IrNameController.setCurrentBlock(thenblock);
            genIrAnalysis(rootAst.getChildList().get(4));
            new JumpInstr(IrNameController.getLocalVarName(), followblock);
            IrNameController.setCurrentBlock(followblock);
        }
        GenerationMain.preTraverse(sonAst);
        return null;
    }

    /*TODO:change later*/
    private Value genIrForStmtChecker(AstNode sonAst) {
        SymbolTable.enterLoop();
        AstNode rootAst = sonAst.getParent();
        BasicBlock condBlock = new BasicBlock(IrNameController.getBlockName());
        BasicBlock currentLoopBlock = new BasicBlock(IrNameController.getBlockName());
        BasicBlock followBlock = new BasicBlock(IrNameController.getBlockName());
        IrNameController.pushLoop(new Loop(condBlock, currentLoopBlock, followBlock));
        new JumpInstr(IrNameController.getLocalVarName(), condBlock);
        IrNameController.setCurrentBlock(condBlock);
        //((CondExp) rootAst.getChildList().get(2)).genIrForCond(CurrentLoopBlock, followBlock);
        IrNameController.setCurrentBlock(currentLoopBlock);
        genIrAnalysis(rootAst.getChildList().get(4));
        new JumpInstr(IrNameController.getLocalVarName(), condBlock);
        IrNameController.setCurrentBlock(followBlock);
        IrNameController.popLoop();
        SymbolTable.leaveLoop();
        return null;
    }

    private Value genIrBreakStmtChecker(AstNode rootAst) {
        new JumpInstr(IrNameController.getLocalVarName(),
                IrNameController.getCurrentLoop().getFollowBlock());
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrContinueStmtChecker(AstNode rootAst) {
        new JumpInstr(IrNameController.getLocalVarName(),
                IrNameController.getCurrentLoop().getCondBlock());
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrReturnStmtChecker(AstNode sonAst) {
        AstNode rootAst = sonAst.getParent();
        Value retValue;
        if (rootAst.getChildList().get(1).getGrammarType().equals("<Exp>")) {
            retValue = genIrAnalysis(rootAst.getChildList().get(1));
        } else {
            retValue = new Constant("0", new VarType(32));
        }
        Instr instr = new RetInstr(IrNameController.getLocalVarName(), "return", retValue);
        GenerationMain.preTraverse(rootAst);
        return instr;
    }

    private Value genIrPrintStmtChecker(AstNode sonAst) {
        AstNode rootAst = sonAst.getParent();
        StringBuilder sb = new StringBuilder();
        List<Value> expValueList = rootAst.getChildList().stream().
                filter(child -> child.getGrammarType().equals("<Exp>")).
                map(this::genIrAnalysis).toList();
        String subformatString = rootAst.getChildList().get(2).getSymToken().getWord().substring(1,
                rootAst.getChildList().get(2).getSymToken().getWord().length() - 1);
        int expCnt = 0;
        for (int i = 0; i < subformatString.length(); i++) {
            if (subformatString.charAt(i) == '%') {
                if (!sb.isEmpty()) {
                    String str = IrNameController.getStringLiteral(
                            IrNameController.getStringLiteralName(), sb.toString());
                    new PutStrDeclare(IrNameController.getLocalVarName(), str);
                    sb.setLength(0);
                }
                Value value = expValueList.get(expCnt++);
                new PutIntDeclare(IrNameController.getLocalVarName(), value);
                i = i + 1;
            } else if (subformatString.charAt(i) == '\\') {
                sb.append('\n');
                i = i + 1;
            } else {
                sb.append(subformatString.charAt(i));
            }
        }
        if (!sb.isEmpty()) {
            String str = IrNameController.getStringLiteral(
                    IrNameController.getStringLiteralName(), sb.toString());
            new PutStrDeclare(IrNameController.getLocalVarName(), str);
        }
        GenerationMain.preTraverse(sonAst);
        return null;
    }

    private Value genIrExpChecker(AstNode rootAst) {
        Value ans = genIrAnalysis(rootAst.getChildList().get(0));
        GenerationMain.preTraverse(rootAst);
        return ans;
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

    //FuncDef.java
    private Value genIrFuncDefChecker(AstNode rootAst) {
        return genFuncDefSubChecker(rootAst);
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
        return genFuncDefSubChecker(rootAst);
    }

    private Value genFuncDefSubChecker(AstNode rootAst) {
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

    //Lexer_part
    /*TODO:change later*/
    private Value genIrAssignChecker(AstNode sonAst) {
        AstNode rootAst = sonAst.getParent();
        Value lval = genIrAnalysis(rootAst.getChildList().get(0));//Error
        Value exp = genIrAnalysis(rootAst.getChildList().get(2));
        Instr instr = new StoreInstr(IrNameController.getLocalVarName(), "store", exp, lval);
        GenerationMain.preTraverse(sonAst);
        return instr;
    }

    /*TODO:change later*/
    private Value genIrGetIntChecker(AstNode rootAst) {
        Value pointer = null;//Error
        GetIntDeclare getIntDeclare = new GetIntDeclare(IrNameController.getLocalVarName(),
                "call");
        Instr instr = new StoreInstr(IrNameController.getLocalVarName(),
                "store", getIntDeclare, pointer);
        GenerationMain.preTraverse(rootAst);
        return instr;
    }
}
