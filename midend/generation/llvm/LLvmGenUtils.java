package midend.generation.llvm;

import frontend.generation.semantic.symtable.SymbolTable;
import frontend.generation.semantic.symtable.symbol.VarSymbol;
import frontend.generation.semantic.symtable.symbol.varsymbol.IntSymbol;
import frontend.generation.syntax.AstNode;
import iostream.declare.GetIntDeclare;
import midend.generation.utils.IrNameController;
import midend.generation.utils.irtype.VarType;
import midend.generation.value.Value;
import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.Constant;
import midend.generation.value.construction.user.Instr;
import midend.generation.value.instr.basis.BrInstr;
import midend.generation.value.instr.basis.CalcInstr;
import midend.generation.value.instr.basis.GetEleInstr;
import midend.generation.value.instr.basis.IcmpInstr;
import midend.generation.value.instr.basis.LoadInstr;
import midend.generation.value.instr.basis.StoreInstr;

import java.util.ArrayList;

/**
 * LLvmGenUtils 用于生成 LLVM IR 的工具类，主要解析了
 * Exp下的各种不需要加入AST递归过程分析的子节点，生成对
 * 应的 LLVM IR
 */
public class LLvmGenUtils {
    private final LLvmGenIR llvmGenIR;

    public LLvmGenUtils(LLvmGenIR llvmGenIR) {
        this.llvmGenIR = llvmGenIR;
    }

    /**
     * Cond -> LOrExp
     */
    public void genCondIr(AstNode rootAst, BasicBlock thenBlock, BasicBlock elseBlock) {
        if (rootAst.getChildList().get(0).getGrammarType().equals("<LOrExp>")) {
            genOrIr(rootAst.getChildList().get(0), thenBlock, elseBlock);
        }
    }

    /**
     * LorExp -> LAndExp | LOrExp '||' LAndExp
     */
    public void genOrIr(AstNode child, BasicBlock thenBlock, BasicBlock elseBlock) {
        for (AstNode node : child.getChildList()) {
            if (node.getGrammarType().equals("<LAndExp>")) {
                if ((child.getParent().getChildList().size() > 1 || child.getChildList().size() > 1)
                        && child.getParent().getGrammarType().equals("<LOrExp>")) {
                    BasicBlock nextBlock = new BasicBlock(IrNameController.getBlockName());
                    genAndIr(node, thenBlock, nextBlock);
                    IrNameController.setCurrentBlock(nextBlock);
                } else {
                    genAndIr(node, thenBlock, elseBlock);
                }
            } else if (node.getGrammarType().equals("<LOrExp>")) {
                genOrIr(node, thenBlock, elseBlock);
            }
        }
    }

    /**
     * LAndExp -> EqExp | LAndExp '&&' EqExp
     */
    public void genAndIr(AstNode child, BasicBlock thenBlock, BasicBlock elseBlock) {
        for (AstNode node : child.getChildList()) {
            if (node.getGrammarType().equals("<EqExp>")) {
                if ((child.getParent().getChildList().size() > 1 || child.getChildList().size() > 1)
                        && child.getParent().getGrammarType().equals("<LAndExp>")) {
                    BasicBlock nextBlock = new BasicBlock(IrNameController.getBlockName());
                    genEqIr(node, nextBlock, elseBlock);
                    IrNameController.setCurrentBlock(nextBlock);
                } else {
                    genEqIr(node, thenBlock, elseBlock);
                }
            } else if (node.getGrammarType().equals("<LAndExp>")) {
                genAndIr(node, thenBlock, elseBlock);
            }
        }
    }

    /**
     * EqExp -> RelExp | EqExp ('=='|'!=') RelExp
     */
    public void genEqIr(AstNode node, BasicBlock thenBlock, BasicBlock elseBlock) {
        Value cond = llvmGenIR.genIrAnalysis(node);
        if (cond.getType().isInt32()) {
            cond = new IcmpInstr(IrNameController.getLocalVarName(),
                    "ne", cond, new Constant("0", new VarType(32)));
        }
        new BrInstr(cond, thenBlock, elseBlock);
    }

    /**
     * LVal -> Ident { '[' Exp ']'}
     * 用于获取左值对应的LLVM IR代码，获得左值的地址
     */
    public Value genAssignIr(AstNode rootAst) {
        ArrayList<Value> values = new ArrayList<>();
        for (AstNode child : rootAst.getChildList()) {
            if (child.getGrammarType().equals("<Exp>")) {
                values.add(llvmGenIR.genIrAnalysis(child));
            }
        }
        IntSymbol intSymbol = (IntSymbol) SymbolTable.getSymByName(rootAst.getChildList()
                .get(0).getSymToken().getWord());
        if (intSymbol != null) {
            Integer dim = intSymbol.getDim();
            ArrayList<Integer> space = intSymbol.getSpace();
            if (dim.equals(0)) {
                return intSymbol.getValue();
            } else if (dim.equals(1)) {
                return new GetEleInstr(IrNameController.getLocalVarName(),
                        intSymbol.getValue(), values.get(0));
            } else {
                return new GetEleInstr(IrNameController.getLocalVarName(), intSymbol.getValue(),
                        new CalcInstr(IrNameController.getLocalVarName(), "add",
                                new CalcInstr(IrNameController.getLocalVarName(), "mul",
                                        new Constant(String.valueOf(space.get(1)), new VarType(32)),
                                        values.get(0)), values.get(1)));
            }
        }
        return null;
    }

    /**
     * LVal -> Ident { '[' Exp ']'}
     * 用于创建左值对应的LLVM IR代码，获得左值的值
     */
    public Value genLValIr(AstNode rootAst) {
        int expNum = 0;
        ArrayList<Value> values = new ArrayList<>();
        for (AstNode child : rootAst.getChildList()) {
            if (child.getGrammarType().equals("<Exp>")) {
                values.add(llvmGenIR.genIrAnalysis(child));
                expNum++;
            }
        }
        VarSymbol varSymbol = ((VarSymbol) SymbolTable.getSymByName(
                rootAst.getChildList().get(0).getSymToken().getWord()));
        return genVarSymbolValueIr(values, expNum, varSymbol.getDim(),
                varSymbol.getSpace(), varSymbol.getValue());
    }

    /**
     * genVarSymbolValueIr 用于数值对应的LLVM IR代码，
     * 提取了genIntValueIr和genConstValueIr的公共部分
     * 形成了VarSymbol的一类处理方法
     */
    private Value genVarSymbolValueIr(ArrayList<Value> values, int expNum,
                                      Integer dim, ArrayList<Integer> space, Value value) {
        if (dim.equals(0)) {
            return new LoadInstr(IrNameController.getLocalVarName(), value);
        } else if (dim.equals(1)) {
            return (expNum == 0) ? new GetEleInstr(IrNameController.getLocalVarName(),
                    value, new Constant("0", new VarType(32))) :
                    new LoadInstr(IrNameController.getLocalVarName(),
                            new GetEleInstr(IrNameController.getLocalVarName(),
                                    value, values.get(0)));
        } else {
            if (expNum == 0) {
                return new GetEleInstr(IrNameController.getLocalVarName(),
                        value,
                        new Constant("0", new VarType(32)));
            } else if (expNum == 1) {
                Instr instr = new CalcInstr(IrNameController.getLocalVarName(), "mul",
                        new Constant(String.valueOf(space.get(1)), new VarType(32)), values.get(0));
                return new GetEleInstr(IrNameController.getLocalVarName(),
                        value, instr);
            } else {
                Instr instr = new CalcInstr(IrNameController.getLocalVarName(), "add",
                        new CalcInstr(IrNameController.getLocalVarName(), "mul",
                                new Constant(String.valueOf(space.get(1)), new VarType(32)),
                                values.get(0)), values.get(1));
                return new LoadInstr(IrNameController.getLocalVarName(),
                        new GetEleInstr(IrNameController.getLocalVarName(), value, instr));
            }
        }
    }

    /**
     * Stmt -> LVal '=' 'getint' '(' ')'';'
     */
    public Value genIrGetIntChecker(AstNode rootAst) {
        Value pointer = genAssignIr(rootAst.getChildList().get(0));
        GetIntDeclare getIntDeclare = new GetIntDeclare(IrNameController.getLocalVarName(),
                "call");
        return new StoreInstr(getIntDeclare, pointer);
    }
}
