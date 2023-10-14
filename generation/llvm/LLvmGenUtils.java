package generation.llvm;

import generation.utils.IrNameController;
import generation.utils.irtype.VarType;
import generation.value.Value;
import generation.value.construction.BasicBlock;
import generation.value.construction.Constant;
import generation.value.construction.user.Instr;
import generation.value.instr.BrInstr;
import generation.value.instr.CalcInstr;
import generation.value.instr.GetEleInstr;
import generation.value.instr.LoadInstr;
import semantic.symtable.Symbol;
import semantic.symtable.SymbolTable;
import semantic.symtable.symbol.ConstSymbol;
import semantic.symtable.symbol.VarSymbol;
import syntax.AstNode;

import java.util.ArrayList;

public class LLvmGenUtils {
    private LLvmGenIR llvmGenIR;

    public LLvmGenUtils(LLvmGenIR llvmGenIR) {
        this.llvmGenIR = llvmGenIR;
    }

    public void genCondIr(AstNode rootAst, BasicBlock thenBlock, BasicBlock elseBlock) {
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
        VarSymbol varSymbol = (VarSymbol) SymbolTable.getSymByName(rootAst.getChildList()
                .get(0).getSymToken().getWord());
        Integer dim = varSymbol.getDim();
        ArrayList<Integer> space = varSymbol.getSpace();
        if (dim.equals(0)) {
            return varSymbol.getValue();
        } else if (dim.equals(1)) {
            return new GetEleInstr(IrNameController.getLocalVarName(),
                    "getelementptr", varSymbol.getValue(), values.get(0));
        } else {
            Instr instr = new CalcInstr(IrNameController.getLocalVarName(), "mul",
                    new Constant(String.valueOf(space.get(0)), new VarType(32)), values.get(0));
            instr = new CalcInstr(IrNameController.getLocalVarName(), "add", instr, values.get(1));
            return new GetEleInstr(IrNameController.getLocalVarName(),
                    "getelementptr", varSymbol.getValue(), instr);
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
        if (symbol instanceof VarSymbol) {
            return genVarValueIr((VarSymbol) symbol, values, expNum);
        } else {
            return genConstValueIr((ConstSymbol) symbol, values, expNum);
        }
    }

    private Value genConstValueIr(ConstSymbol constSymbol, ArrayList<Value> values, int expNum) {
        Integer dim = constSymbol.getDim();
        ArrayList<Integer> space = constSymbol.getSpace();
        if (dim.equals(0)) {
            return new LoadInstr(IrNameController.getLocalVarName(),
                    "load", constSymbol.getValue());
        } else if (dim.equals(1)) {
            if (expNum == 0) {
                return new GetEleInstr(IrNameController.getLocalVarName(),
                        "getelementptr", constSymbol.getValue(),
                        new Constant("0", new VarType(32)));
            } else {
                Instr instr = new GetEleInstr(IrNameController.getLocalVarName(),
                        "getelementptr", constSymbol.getValue(), values.get(0));
                return new LoadInstr(IrNameController.getLocalVarName(), "load", instr);

            }
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

    private Value genVarValueIr(VarSymbol varSymbol, ArrayList<Value> values, int expNum) {
        Integer dim = varSymbol.getDim();
        ArrayList<Integer> space = varSymbol.getSpace();
        if (dim.equals(0)) {
            return new LoadInstr(IrNameController.getLocalVarName(), "load", varSymbol.getValue());
        } else if (dim.equals(1)) {
            if (expNum == 0) {
                return new GetEleInstr(IrNameController.getLocalVarName(),
                        "getelementptr", varSymbol.getValue(), new Constant("0", new VarType(32)));
            } else {
                Instr instr = new GetEleInstr(IrNameController.getLocalVarName(),
                        "getelementptr", varSymbol.getValue(), values.get(0));
                return new LoadInstr(IrNameController.getLocalVarName(), "load", instr);
            }
        } else {
            if (expNum == 0) {
                return new GetEleInstr(IrNameController.getLocalVarName(),
                        "getelementptr", varSymbol.getValue(), new Constant("0", new VarType(32)));
            } else if (expNum == 1) {
                Instr instr = new CalcInstr(IrNameController.getLocalVarName(), "mul",
                        new Constant(String.valueOf(space.get(1)), new VarType(32)), values.get(0));
                return new GetEleInstr(IrNameController.getLocalVarName(),
                        "getelementptr", varSymbol.getValue(), instr);
            } else {
                Instr instr = new CalcInstr(IrNameController.getLocalVarName(), "mul",
                        new Constant(String.valueOf(space.get(1)), new VarType(32)), values.get(0));
                instr = new CalcInstr(IrNameController.getLocalVarName(),
                        "add", instr, values.get(1));
                instr = new GetEleInstr(IrNameController.getLocalVarName(),
                        "getelementptr", varSymbol.getValue(), instr);
                return new LoadInstr(IrNameController.getLocalVarName(), "load", instr);
            }
        }
    }
}
