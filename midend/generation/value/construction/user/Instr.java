package midend.generation.value.construction.user;

import backend.generation.mips.asm.textsegment.structure.Comment;
import midend.simplify.controller.datastruct.FunctionClone;
import midend.simplify.method.FunctionInlineUnit;
import iostream.declare.GetIntDeclare;
import iostream.structure.IoStreamGeneration;
import iostream.structure.OptimizerUnit;
import midend.generation.utils.IrNameController;
import midend.generation.utils.IrType;
import midend.generation.value.Value;
import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.Param;
import midend.generation.value.construction.User;
import midend.generation.value.instr.basis.AllocaInstr;
import midend.generation.value.instr.basis.CalcInstr;
import midend.generation.value.instr.basis.CallInstr;
import midend.generation.value.instr.basis.GetEleInstr;
import midend.generation.value.instr.basis.IcmpInstr;
import midend.generation.value.instr.basis.LoadInstr;
import midend.generation.value.instr.basis.ZextInstr;
import midend.generation.value.instr.optimizer.PhiInstr;
import midend.simplify.method.Mem2RegUnit;

public class Instr extends User {
    protected String instrType;
    private BasicBlock parent;

    public Instr(IrType type, String name, String instrType) {
        super(type, name);
        this.instrType = instrType;
        if (!OptimizerUnit.isIsOptimizer()) {
            IrNameController.addInstr(this);
        }
    }

    public String getInstrType() {
        return instrType;
    }

    public void setBelongingBlock(BasicBlock currentBasicBlock) {
        this.parent = currentBasicBlock;
    }

    public BasicBlock getBelongingBlock() {
        return parent;
    }

    @Override
    public void generateAssembly() {
        new Comment(this.toString());
    }

    public void insertPhiProcess() {
        Mem2RegUnit.reConfig(this);
        Mem2RegUnit.insertPhi();
    }

    public boolean isDead() {
        return this.isValid() && !(this instanceof CallInstr) &&
                !(this instanceof IoStreamGeneration) && this.getUseDefChain().isEmpty();
    }

    public boolean isValid() {
        boolean valid = this instanceof AllocaInstr || this instanceof CalcInstr ||
                (this instanceof CallInstr callInstr && !callInstr.getType().isVoid()) ||
                this instanceof GetIntDeclare;
        return valid || this instanceof PhiInstr ||
                this instanceof GetEleInstr || this instanceof IcmpInstr ||
                this instanceof LoadInstr || this instanceof ZextInstr;
    }

    public String getGlobalVariableNumberingHash() {
        return null;
    }

    public void addPhiToUse() {
        if (this instanceof PhiInstr phiInstr) {
            for (Value operand : phiInstr.getOperands()) {
                if (operand instanceof Instr || operand instanceof GlobalVar
                        || operand instanceof Param) {
                    this.getBelongingBlock().getUseBasicBlockHashSet().add(operand);
                }
            }
        }
    }

    public void genUseDefAnalysis() {
        for (Value operand : this.getOperands()) {
            if (!this.getBelongingBlock().getDefBasicBlockHashSet().contains(operand) &&
                    (operand instanceof Instr || operand instanceof GlobalVar
                            || operand instanceof Param)) {
                this.getBelongingBlock().getUseBasicBlockHashSet().add(operand);
            }
        }
        if (!this.getBelongingBlock().getUseBasicBlockHashSet().contains(this) && this.isValid()) {
            this.getBelongingBlock().getDefBasicBlockHashSet().add(this);
        }
    }

    public void buildFuncCallGraph() {
        if (this instanceof CallInstr callInstr) {
            Function response = callInstr.getBelongingBlock().getBelongingFunc();
            Function curFunc = this.getBelongingBlock().getBelongingFunc();
            if (!curFunc.isBuildin()) {
                if (!FunctionInlineUnit.getCaller(curFunc).contains(response)) {
                    FunctionInlineUnit.addCaller(curFunc, response);
                }
                if (!FunctionInlineUnit.getResponse(response).contains(curFunc)) {
                    FunctionInlineUnit.addResponse(response, curFunc);
                }
            }
        }
    }

    public Value copy(FunctionClone functionClone) {
        return null;
    }
}
