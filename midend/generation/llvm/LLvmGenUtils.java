package midend.generation.llvm;

import iostream.declare.GetIntDeclare;
import midend.generation.utils.IrNameController;
import midend.generation.utils.irtype.VarType;
import midend.generation.value.Value;
import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.Constant;
import midend.generation.value.construction.user.Instr;
import frontend.semantic.symtable.Symbol;
import frontend.semantic.symtable.SymbolTable;
import frontend.semantic.symtable.symbol.varsymbol.ConstSymbol;
import frontend.semantic.symtable.symbol.varsymbol.IntSymbol;
import frontend.syntax.AstNode;
import midend.generation.value.instr.basis.BrInstr;
import midend.generation.value.instr.basis.CalcInstr;
import midend.generation.value.instr.basis.GetEleInstr;
import midend.generation.value.instr.basis.LoadInstr;
import midend.generation.value.instr.basis.StoreInstr;

import java.util.ArrayList;

public class LLvmGenUtils {
    private LLvmGenIR llvmGenIR;

    public LLvmGenUtils(LLvmGenIR llvmGenIR) {
        this.llvmGenIR = llvmGenIR;
    }

    public void genCondIr(AstNode rootAst, BasicBlock thenBlock, BasicBlock elseBlock) {
        if (rootAst == null) {
            return;
        }
        if (rootAst.getChildList().get(0).getGrammarType().equals("<LOrExp>")) {
            genOrIr(rootAst.getChildList().get(0), thenBlock, elseBlock);
        }
    }

    public void genOrIr(AstNode astNode, BasicBlock thenBlock, BasicBlock elseBlock) {
        for (AstNode child : astNode.getChildList()) {
            if (child.getGrammarType().equals("<LAndExp>")) {
                if (astNode.getChildList().indexOf(child) == astNode.getChildList().size() - 1) {
                    genAndIr(child, thenBlock, elseBlock);
                } else {
                    BasicBlock nextBlock = new BasicBlock(IrNameController.getBlockName());
                    genAndIr(child, thenBlock, nextBlock);
                    IrNameController.setCurrentBlock(nextBlock);
                }
            }
        }
    }

    public void genAndIr(AstNode child, BasicBlock thenBlock, BasicBlock elseBlock) {
        for (AstNode node : child.getChildList()) {
            if (node.getGrammarType().equals("<EqExp>")) {
                if (child.getChildList().indexOf(node) == child.getChildList().size() - 1) {
                    Value cond = llvmGenIR.genIrAnalysis(node);
                    new BrInstr(IrNameController.getLocalVarName(),
                            "br", cond, thenBlock, elseBlock);
                } else {
                    BasicBlock nextBlock = new BasicBlock(IrNameController.getBlockName());
                    Value cond = llvmGenIR.genIrAnalysis(node);
                    new BrInstr(IrNameController.getLocalVarName(),
                            "br", cond, thenBlock, elseBlock);
                    IrNameController.setCurrentBlock(nextBlock);
                }
            }
        }
    }

    public Value genAssignIr(AstNode rootAst) {
        ArrayList<Value> values = new ArrayList<>();
        for (AstNode child : rootAst.getChildList()) {
            if (child.getGrammarType().equals("<Exp>")) {
                values.add(llvmGenIR.genIrAnalysis(child));
            }
        }
        IntSymbol intSymbol = (IntSymbol) SymbolTable.getSymByName(rootAst.getChildList()
                .get(0).getSymToken().getWord());
        Integer dim = intSymbol.getDim();
        ArrayList<Integer> space = intSymbol.getSpace();
        if (dim.equals(0)) {
            return intSymbol.getValue();
        } else if (dim.equals(1)) {
            return new GetEleInstr(IrNameController.getLocalVarName(),
                    "getelementptr", intSymbol.getValue(), values.get(0));
        } else {
            Instr instr = new CalcInstr(IrNameController.getLocalVarName(), "mul",
                    new Constant(String.valueOf(space.get(0)), new VarType(32)), values.get(0));
            instr = new CalcInstr(IrNameController.getLocalVarName(), "add", instr, values.get(1));
            return new GetEleInstr(IrNameController.getLocalVarName(),
                    "getelementptr", intSymbol.getValue(), instr);
        }
    }

    public Value genValueIr(AstNode rootAst) {
        int expNum = 0;
        ArrayList<Value> values = new ArrayList<>();
        for (AstNode child : rootAst.getChildList()) {
            if (child.getGrammarType().equals("<Exp>")) {
                values.add(llvmGenIR.genIrAnalysis(child));
                expNum++;
            }
        }
        Symbol symbol = SymbolTable.getSymByName(
                rootAst.getChildList().get(0).getSymToken().getWord());
        return (symbol instanceof IntSymbol) ? genVarValueIr((IntSymbol) symbol, values, expNum) :
                genConstValueIr((ConstSymbol) symbol, values, expNum);
    }

    private Value genConstValueIr(ConstSymbol constSymbol, ArrayList<Value> values, int expNum) {
        Integer dim = constSymbol.getDim();
        ArrayList<Integer> space = constSymbol.getSpace();
        if (dim.equals(0)) {
            return new LoadInstr(IrNameController.getLocalVarName(),
                    "load", constSymbol.getValue());
        } else if (dim.equals(1)) {
            return (expNum == 0) ? new GetEleInstr(IrNameController.getLocalVarName(),
                    "getelementptr", constSymbol.getValue(),
                    new Constant("0", new VarType(32))) :
                    new LoadInstr(IrNameController.getLocalVarName(), "load",
                            new GetEleInstr(IrNameController.getLocalVarName(),
                                    "getelementptr", constSymbol.getValue(), values.get(0)));
        } else {
            if (expNum == 0) {
                return new GetEleInstr(IrNameController.getLocalVarName(),
                        "getelementptr", constSymbol.getValue(),
                        new Constant("0", new VarType(32)));
            } else if (expNum == 1) {
                Instr instr = new CalcInstr(IrNameController.getLocalVarName(), "mul",
                        new Constant(String.valueOf(space.get(1)), new VarType(32)), values.get(0));
                return new GetEleInstr(IrNameController.getLocalVarName(),
                        "getelementptr", constSymbol.getValue(), instr);
            } else {
                Instr instr = new CalcInstr(IrNameController.getLocalVarName(), "mul",
                        new Constant(String.valueOf(space.get(1)), new VarType(32)), values.get(0));
                instr = new CalcInstr(IrNameController.getLocalVarName(),
                        "add", instr, values.get(1));
                instr = new GetEleInstr(IrNameController.getLocalVarName(),
                        "getelementptr", constSymbol.getValue(), instr);
                return new LoadInstr(IrNameController.getLocalVarName(), "load", instr);
            }
        }
    }

    private Value genVarValueIr(IntSymbol intSymbol, ArrayList<Value> values, int expNum) {
        Integer dim = intSymbol.getDim();
        ArrayList<Integer> space = intSymbol.getSpace();
        if (dim.equals(0)) {
            return new LoadInstr(IrNameController.getLocalVarName(), "load", intSymbol.getValue());
        } else if (dim.equals(1)) {
            return (expNum == 0) ? new GetEleInstr(IrNameController.getLocalVarName(),
                    "getelementptr", intSymbol.getValue(),
                    new Constant("0", new VarType(32))) :
                    new LoadInstr(IrNameController.getLocalVarName(), "load",
                            new GetEleInstr(IrNameController.getLocalVarName(),
                                    "getelementptr", intSymbol.getValue(), values.get(0)));
        } else {
            if (expNum == 0) {
                return new GetEleInstr(IrNameController.getLocalVarName(),
                        "getelementptr", intSymbol.getValue(), new Constant("0", new VarType(32)));
            } else if (expNum == 1) {
                Instr instr = new CalcInstr(IrNameController.getLocalVarName(), "mul",
                        new Constant(String.valueOf(space.get(1)), new VarType(32)), values.get(0));
                return new GetEleInstr(IrNameController.getLocalVarName(),
                        "getelementptr", intSymbol.getValue(), instr);
            } else {
                Instr instr = new CalcInstr(IrNameController.getLocalVarName(), "mul",
                        new Constant(String.valueOf(space.get(1)), new VarType(32)), values.get(0));
                instr = new CalcInstr(IrNameController.getLocalVarName(),
                        "add", instr, values.get(1));
                instr = new GetEleInstr(IrNameController.getLocalVarName(),
                        "getelementptr", intSymbol.getValue(), instr);
                return new LoadInstr(IrNameController.getLocalVarName(), "load", instr);
            }
        }
    }

    public Value genIrGetIntChecker(AstNode rootAst) {
        Value pointer = genAssignIr(rootAst.getChildList().get(0));
        GetIntDeclare getIntDeclare = new GetIntDeclare(IrNameController.getLocalVarName(),
                "call");
        return new StoreInstr(IrNameController.getLocalVarName(),
                "store", getIntDeclare, pointer);
    }
}