package midend.generation.llvm;

import frontend.generation.semantic.SemanticAnalysisChecker;
import frontend.generation.semantic.symtable.Symbol;
import frontend.generation.semantic.symtable.SymbolTable;
import frontend.generation.semantic.symtable.symbol.FuncSymbol;
import frontend.generation.semantic.symtable.symbol.varsymbol.ConstSymbol;
import frontend.generation.semantic.symtable.symbol.varsymbol.IntSymbol;
import frontend.generation.semantic.utils.SymDefiner;
import frontend.generation.syntax.AstNode;
import iostream.declare.PutIntDeclare;
import iostream.declare.PutStrDeclare;
import midend.generation.GenerationMain;
import midend.generation.utils.IrNameController;
import midend.generation.utils.IrType;
import midend.generation.utils.irtype.ArrayType;
import midend.generation.utils.irtype.PointerType;
import midend.generation.utils.irtype.VarType;
import midend.generation.value.Value;
import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.Constant;
import midend.generation.value.construction.FormatString;
import midend.generation.value.construction.Param;
import midend.generation.value.construction.procedure.Loop;
import midend.generation.value.construction.user.Function;
import midend.generation.value.construction.user.GlobalVar;
import midend.generation.value.construction.user.Instr;
import midend.generation.value.instr.basis.AllocaInstr;
import midend.generation.value.instr.basis.CalcInstr;
import midend.generation.value.instr.basis.CallInstr;
import midend.generation.value.instr.basis.GetEleInstr;
import midend.generation.value.instr.basis.IcmpInstr;
import midend.generation.value.instr.basis.JumpInstr;
import midend.generation.value.instr.basis.RetInstr;
import midend.generation.value.instr.basis.StoreInstr;
import midend.generation.value.instr.basis.ZextInstr;

import java.util.ArrayList;
import java.util.List;

/**
 * LLvmGenIR 用于生成LLVM IR中间代码的解释器
 */
public class LLvmGenIR {
    /**
     * semanticAnalysisChecker 用于语义分析的检查器
     * llvmGenUtils 用于生成LLVM IR中间代码的工具类
     */
    private final SemanticAnalysisChecker semanticAnalysisChecker;
    private final LLvmGenUtils llvmGenUtils;

    public LLvmGenIR() {
        this.semanticAnalysisChecker = new SemanticAnalysisChecker();
        this.llvmGenUtils = new LLvmGenUtils(this);
    }

    /**
     * genIrAnalysis 用于生成LLVM IR中间代码的解释程序，利用递归下降
     * 的方式依次解析各种语法单元，生成对应的LLVM IR中间代码，下方定义的
     * 是其子函数
     */
    public Value genIrAnalysis(AstNode rootAst) {
        return switch (rootAst.getGrammarType()) {
            case "<CompUnit>" -> genIrCompUnitChecker(rootAst);
            //Definer.java
            case "<ConstDef>" -> genIrConstDefChecker(rootAst);
            case "<VarDef>" -> genIrVarDefChecker(rootAst);
            case "<Block>" -> genIrBlockChecker(rootAst);
            case "IFTK" -> genIrIfStmtChecker(rootAst);
            case "FORTK" -> genIrForStmtChecker(rootAst);
            case "BREAKTK" -> genIrBreakStmtChecker();
            case "CONTINUETK" -> genIrContinueStmtChecker();
            case "RETURNTK" -> genIrReturnStmtChecker(rootAst);
            case "PRINTFTK" -> genIrPrintStmtChecker(rootAst);
            case "<Exp>", "<ConstExp>" -> genIrExpChecker(rootAst);
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
        ConstSymbol constSymbol =
                (ConstSymbol) semanticAnalysisChecker.createConstDefChecker(rootAst);
        SymbolTable.addSymbol(constSymbol);
        if (constSymbol.getSymbolLevel().equals(0)) {
            GlobalVar globalVar = new GlobalVar(
                    new PointerType(constSymbol.getInitial().getType()),
                    IrNameController.getGlobalVarName(
                            constSymbol.getSymbolName()), constSymbol.getInitial());
            constSymbol.setValue(globalVar);
        } else {
            Instr instr;
            if (constSymbol.getDim().equals(0)) {
                instr = new AllocaInstr(IrNameController.getLocalVarName(), new VarType(32));
                constSymbol.setValue(instr);
                if (rootAst.getChildList().get(rootAst.getChildList().size() - 1)
                        .getGrammarType().equals("<ConstInitVal>")) {
                    Value value = SymDefiner.genIrConstValues(rootAst.getChildList()
                            .get(rootAst.getChildList().size() - 1), 0).get(0);
                    new StoreInstr(value, instr);
                }
            } else {
                instr = new AllocaInstr(IrNameController.getLocalVarName()
                        , new ArrayType(constSymbol.getSpace(), new VarType(32)));
                constSymbol.setValue(instr);
                if (rootAst.getChildList().get(rootAst.getChildList().size() - 1)
                        .getGrammarType().equals("<ConstInitVal>")) {
                    Value pointer = instr;
                    ArrayList<Value> valuelist = SymDefiner.genIrConstValues(
                            rootAst.getChildList().get(rootAst.getChildList().size() - 1),
                            constSymbol.getDim());
                    int off = 0;
                    for (Value value : valuelist) {
                        instr = new GetEleInstr(IrNameController.getLocalVarName(), pointer,
                                new Constant(String.valueOf(off), new VarType(32)));
                        new StoreInstr(value, instr);
                        off++;
                    }
                }
            }
        }
        return null;
    }

    private Value genIrVarDefChecker(AstNode rootAst) {
        IntSymbol intSymbol = (IntSymbol) semanticAnalysisChecker.createVarDefChecker(rootAst);
        SymbolTable.addSymbol(intSymbol);
        if (intSymbol.getSymbolLevel().equals(0)) {
            GlobalVar globalVar = new GlobalVar(
                    new PointerType(intSymbol.getInitial().getType()),
                    IrNameController.getGlobalVarName(
                            intSymbol.getSymbolName()), intSymbol.getInitial());
            intSymbol.setValue(globalVar);
        } else {
            Instr instr;
            if (intSymbol.getDim().equals(0)) {
                instr = new AllocaInstr(IrNameController.getLocalVarName(), new VarType(32));
                intSymbol.setValue(instr);
                if (rootAst.getChildList().get(rootAst.getChildList().size() - 1)
                        .getGrammarType().equals("<InitVal>")) {
                    Value value = SymDefiner.genIrValues(rootAst.getChildList()
                            .get(rootAst.getChildList().size() - 1), 0).get(0);
                    new StoreInstr(value, instr);
                }
            } else {
                instr = new AllocaInstr(IrNameController.getLocalVarName(),
                        new ArrayType(intSymbol.getSpace(), new VarType(32)));
                intSymbol.setValue(instr);
                if (rootAst.getChildList().get(rootAst.getChildList().size() - 1)
                        .getGrammarType().equals("<InitVal>")) {
                    Value pointer = instr;
                    ArrayList<Value> valuelist = SymDefiner.genIrValues(
                            rootAst.getChildList().get(rootAst.getChildList().size() - 1),
                            intSymbol.getDim());
                    int off = 0;
                    for (Value value : valuelist) {
                        instr = new GetEleInstr(IrNameController.getLocalVarName(), pointer,
                                new Constant(String.valueOf(off), new VarType(32)));
                        new StoreInstr(value, instr);
                        off++;
                    }
                }
            }
        }
        return null;
    }

    private Value genIrBlockChecker(AstNode rootAst) {
        SymbolTable.createStackSymbolTable();
        GenerationMain.preTraverse(rootAst);
        SymbolTable.destroyStackSymbolTable();
        return null;
    }

    /**
     * genIrIfStmtChecker 主要处理if语句的生成LLVM IR中间代码的过程
     * 这里如果childList的长度大于5，说明有else语句，需要生成对应的
     * 条件跳转指令，否则直接生成对应的条件跳转指令即可
     */
    private Value genIrIfStmtChecker(AstNode sonAst) {
        AstNode rootAst = sonAst.getParent();
        BasicBlock thenBlock = new BasicBlock(IrNameController.getBlockName());
        if (rootAst.getChildList().size() > 5) {
            BasicBlock elseBlock = new BasicBlock(IrNameController.getBlockName());
            llvmGenUtils.genCondIr(rootAst.getChildList().get(2), thenBlock, elseBlock);
            IrNameController.setCurrentBlock(thenBlock);
            genIrAnalysis(rootAst.getChildList().get(4));
            BasicBlock followBlock = new BasicBlock(IrNameController.getBlockName());
            new JumpInstr(followBlock);
            IrNameController.setCurrentBlock(elseBlock);
            genIrAnalysis(rootAst.getChildList().get(6));
            new JumpInstr(followBlock);
            IrNameController.setCurrentBlock(followBlock);
        } else {
            BasicBlock followBlock = new BasicBlock(IrNameController.getBlockName());
            llvmGenUtils.genCondIr(rootAst.getChildList().get(2), thenBlock, followBlock);
            IrNameController.setCurrentBlock(thenBlock);
            genIrAnalysis(rootAst.getChildList().get(4));
            new JumpInstr(followBlock);
            IrNameController.setCurrentBlock(followBlock);
        }
        return null;
    }

    /**
     * genIrForStmtChecker 主要处理for语句的生成LLVM IR中间代码的过程
     * 这里由于forStmtVal1,CondAst和forStmtVal2均可能缺省，所以需要分多
     * 种情况分类讨论
     */
    private Value genIrForStmtChecker(AstNode sonAst) {
        AstNode rootAst = sonAst.getParent();
        AstNode forStmtVal1 = null;
        AstNode condAst = null;
        AstNode forStmtVal2 = null;
        for (int i = 0; i < rootAst.getChildList().size(); i++) {
            if (rootAst.getChildList().get(i).getGrammarType().equals("<ForStmt>")) {
                if (i == 2) {
                    forStmtVal1 = rootAst.getChildList().get(i);
                } else {
                    forStmtVal2 = rootAst.getChildList().get(i);
                }
            } else if (rootAst.getChildList().get(i).getGrammarType().equals("<Cond>")) {
                condAst = rootAst.getChildList().get(i);
            }
        }
        if (forStmtVal1 != null) {
            genIrAnalysis(forStmtVal1);
        }
        SymbolTable.enterLoop();
        BasicBlock condBlock = null;
        if (condAst != null) {
            condBlock = new BasicBlock(IrNameController.getBlockName());
        }
        BasicBlock currentLoopBlock = new BasicBlock(IrNameController.getBlockName());
        BasicBlock followBlock = new BasicBlock(IrNameController.getBlockName());
        IrNameController.pushLoop(new Loop(forStmtVal1, condBlock, forStmtVal2,
                currentLoopBlock, followBlock));
        if (condAst != null) {
            new JumpInstr(condBlock);
            IrNameController.setCurrentBlock(condBlock);
            llvmGenUtils.genCondIr(condAst, currentLoopBlock, followBlock);
        } else {
            new JumpInstr(currentLoopBlock);
        }
        IrNameController.setCurrentBlock(currentLoopBlock);
        genIrAnalysis(rootAst.getChildList().get(rootAst.getChildList().size() - 1));
        if (forStmtVal2 != null) {
            genIrAnalysis(forStmtVal2);
        }
        if (condAst != null) {
            new JumpInstr(condBlock);
        } else {
            new JumpInstr(currentLoopBlock);
        }
        IrNameController.setCurrentBlock(followBlock);
        IrNameController.popLoop();
        SymbolTable.leaveLoop();
        return null;
    }

    private Value genIrBreakStmtChecker() {
        new JumpInstr(IrNameController.getCurrentLoop().getFollowBlock());
        return null;
    }

    /**
     * genIrContinueStmtChecker 主要处理continue语句的生成LLVM IR中间代码的过程
     * 这里continue的跳转逻辑决定于Cond是否缺省
     */
    private Value genIrContinueStmtChecker() {
        if (IrNameController.getCurrentLoop().getForStmtVal2() != null) {
            genIrAnalysis(IrNameController.getCurrentLoop().getForStmtVal2());
        }
        if (IrNameController.getCurrentLoop().getCondBlock() != null) {
            new JumpInstr(IrNameController.getCurrentLoop().getCondBlock());
        } else {
            new JumpInstr(IrNameController.getCurrentLoop().getCurrentLoopBlock());
        }
        return null;
    }

    private Value genIrReturnStmtChecker(AstNode sonAst) {
        AstNode rootAst = sonAst.getParent();
        Value retValue = null;
        if (rootAst.getChildList().get(1).getGrammarType().equals("<Exp>")) {
            retValue = genIrAnalysis(rootAst.getChildList().get(1));
        } else if (IrNameController.getCurrentFunc().getReturnType().isInt32()) {
            retValue = new Constant("0", new VarType(32));
        }
        return new RetInstr(retValue);
    }

    private Value genIrPrintStmtChecker(AstNode sonAst) {
        AstNode rootAst = sonAst.getParent();
        StringBuilder sb = new StringBuilder();
        List<Value> expValueList = rootAst.getChildList().stream()
                .filter(child -> child.getGrammarType().equals("<Exp>"))
                .map(this::genIrAnalysis).toList();
        String subformatString = rootAst.getChildList().get(2)
                .getSymToken().getWord().substring(1, rootAst.getChildList().get(2)
                        .getSymToken().getWord().length() - 1);
        int expCnt = 0;
        for (int i = 0; i < subformatString.length(); i++) {
            if (subformatString.charAt(i) == '%') {
                if (!sb.isEmpty()) {
                    ArrayList<Integer> arrayList = new ArrayList<>();
                    arrayList.add(sb.length() + 1);
                    FormatString str = new FormatString(IrNameController.getStringLiteralName(),
                            sb.toString(), arrayList);
                    new PutStrDeclare(str);
                    sb.setLength(0);
                }
                Value value = expValueList.get(expCnt++);
                new PutIntDeclare(value);
                i = i + 1;
            } else if (subformatString.charAt(i) == '\\') {
                sb.append('\n');
                i = i + 1;
            } else {
                sb.append(subformatString.charAt(i));
            }
        }
        if (!sb.isEmpty()) {
            ArrayList<Integer> arrayList = new ArrayList<>();
            arrayList.add(sb.length() + 1);
            FormatString str = new FormatString(IrNameController.getStringLiteralName(),
                    sb.toString(), arrayList);
            new PutStrDeclare(str);
        }
        return null;
    }

    private Value genIrExpChecker(AstNode rootAst) {
        return genIrAnalysis(rootAst.getChildList().get(0));
    }

    private Value genIrPrimaryExpChecker(AstNode rootAst) {
        AstNode child = rootAst.getChildList().get(0);
        Value ans;
        if (child.getGrammarType().equals("LPARENT")) {
            ans = genIrAnalysis(rootAst.getChildList().get(1));
        } else if (child.getGrammarType().equals("<LVal>")) {
            ans = llvmGenUtils.genLValIr(child);
        } else {
            ans = genIrAnalysis(child);
        }
        return ans;
    }

    private Value genIrNumberCallChecker(AstNode rootAst) {
        return new Constant(rootAst.getChildList().get(0).getSymToken().getWord(), new VarType(32));
    }

    private Value genIrUnaryExpChecker(AstNode rootAst) {
        Value ans = genIrAnalysis(rootAst.getChildList().get(0));
        if (rootAst.getChildList().get(0).getGrammarType().equals("<PrimaryExp>")) {
            return ans;
        } else if (rootAst.getChildList().get(0).getGrammarType().equals("<UnaryOp>")) {
            ans = genIrAnalysis(rootAst.getChildList().get(1));
            if (rootAst.getChildList().get(0).getChildList().get(0)
                    .getGrammarType().equals("PLUS")) {
                return ans;
            } else if (rootAst.getChildList().get(0).getChildList().get(0)
                    .getGrammarType().equals("MINU")) {
                return new CalcInstr(IrNameController.getLocalVarName(),
                        "sub", new Constant("0", new VarType(32)), ans);
            } else {
                ans = new IcmpInstr(IrNameController.getLocalVarName(),
                        "eq", new Constant("0", new VarType(32)), ans);
                return new ZextInstr(IrNameController.getLocalVarName(),
                        "zext", ans, new VarType(32));
            }
        } else {
            FuncSymbol funcSymbol = (FuncSymbol) SymbolTable.getSymByName(
                    rootAst.getChildList().get(0).getSymToken().getWord());
            if (funcSymbol != null) {
                Function function = funcSymbol.getfunction();
                ArrayList<Value> params = new ArrayList<>();
                if (rootAst.getChildList().get(2).getGrammarType().equals("<FuncRParams>")) {
                    for (AstNode child : rootAst.getChildList().get(2).getChildList()) {
                        if (child.getGrammarType().equals("<Exp>")) {
                            params.add(genIrAnalysis(child));
                        }
                    }
                }
                ans = new CallInstr(IrNameController.getLocalVarName(), function, params);
                return ans;
            }
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
        return ans;
    }

    private Value genIrAddExpChecker(AstNode rootAst) {
        Value ans = genIrAnalysis(rootAst.getChildList().get(0));
        for (int i = 1; i < rootAst.getChildList().size(); i++) {
            if (rootAst.getChildList().get(i).getGrammarType().equals("PLUS")) {
                Value operand2 = genIrAnalysis(rootAst.getChildList().get(++i));
                ans = new CalcInstr(IrNameController.getLocalVarName(), "add", ans, operand2);
            } else {
                Value operand2 = genIrAnalysis(rootAst.getChildList().get(++i));
                ans = new CalcInstr(IrNameController.getLocalVarName(), "sub", ans, operand2);
            }
        }
        return ans;
    }

    private Value genIrRelExpChecker(AstNode rootAst) {
        Value ans = genIrAnalysis(rootAst.getChildList().get(0));
        for (int i = 1; i < rootAst.getChildList().size(); i++) {
            if (!ans.getType().isInt32()) {
                ans = new ZextInstr(IrNameController.getLocalVarName(),
                        "zext", ans, new VarType(32));
            }
            Value res = genIrAnalysis(rootAst.getChildList().get(i + 1));
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
            i++;
        }
        return ans;
    }

    private Value genIrEqExpChecker(AstNode rootAst) {
        Value ans = genIrAnalysis(rootAst.getChildList().get(0));
        for (int i = 1; i < rootAst.getChildList().size(); i++) {
            if (rootAst.getChildList().get(i).getGrammarType().matches("EQL|NEQ")) {
                if (!ans.getType().isInt32()) {
                    ans = new ZextInstr(IrNameController.getLocalVarName(),
                            "zext", ans, new VarType(32));
                }
                Value res = genIrAnalysis(rootAst.getChildList().get(i + 1));
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
        }
        return ans;
    }

    //FuncDef.java
    private Value genIrFuncDefChecker(AstNode rootAst) {
        SymbolTable.setGlobalArea(false);
        FuncSymbol funcSymbol = (FuncSymbol) semanticAnalysisChecker.createFuncDefChecker(rootAst);
        return genFuncDefSubChecker(rootAst, funcSymbol);
    }

    private Value genIrFuncFParamChecker(AstNode rootAst) {
        IntSymbol symbol = (IntSymbol) semanticAnalysisChecker.createFuncFParamChecker(rootAst);
        SymbolTable.addSymbol(symbol);
        IrType type = symbol.getDim() == 0 ? new VarType(32) : new PointerType(new VarType(32));
        Param param = new Param(type, IrNameController.getParamName());
        if (param.getType().isInt32()) {
            Instr allocaInstr = new AllocaInstr(
                    IrNameController.getLocalVarName(), param.getType());
            symbol.setValue(allocaInstr);
            new StoreInstr(param, allocaInstr);
        } else {
            symbol.setValue(param);
        }
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    //MainFuncDef.java
    private Value genIrMainFuncDefChecker(AstNode rootAst) {
        SymbolTable.setGlobalArea(false);
        FuncSymbol funcSymbol =
                (FuncSymbol) semanticAnalysisChecker.createMainFuncDefChecker(rootAst);
        return genFuncDefSubChecker(rootAst, funcSymbol);
    }

    private Value genFuncDefSubChecker(AstNode rootAst, FuncSymbol funcSymbol) {
        SymbolTable.addSymbol(funcSymbol);
        SymbolTable.setCurrentFunc(funcSymbol);
        SymbolTable.createStackSymbolTable();
        IrType returnType = funcSymbol.getReturnType().equals(Symbol.SymType.INT) ?
                new VarType(32) : new VarType(0);
        Function function = new Function(IrNameController.getFuncName(
                funcSymbol.getSymbolName()), returnType);
        funcSymbol.setFunction(function);
        IrNameController.setCurrentFunc(function);
        BasicBlock basicBlock = new BasicBlock(IrNameController.getBlockName());
        IrNameController.setCurrentBlock(basicBlock);
        GenerationMain.preTraverse(rootAst);
        BasicBlock lastBlock = IrNameController.getCurrentBlock();
        if (lastBlock.isEmpty() || !(lastBlock.getLastInstr() instanceof RetInstr)) {
            if (returnType.isInt32()) {
                new RetInstr(new Constant("0", new VarType(32)));
            } else {
                new RetInstr(null);
            }
        }
        SymbolTable.destroyStackSymbolTable();
        return null;
    }

    //Lexer_part
    private Value genIrAssignChecker(AstNode sonAst) {
        AstNode rootAst = sonAst.getParent();
        if (rootAst.getChildList().get(2).getGrammarType().equals("GETINTTK")) {
            return llvmGenUtils.genIrGetIntChecker(rootAst);
        } else {
            Value lval = llvmGenUtils.genAssignIr(rootAst.getChildList().get(0));
            Value exp = genIrAnalysis(rootAst.getChildList().get(2));
            return new StoreInstr(exp, lval);
        }
    }
}
