package midend.generation.value.instr.basis;

import backend.generation.mips.Register;
import backend.generation.mips.asm.textsegment.complex.MoveAsm;
import backend.generation.mips.asm.textsegment.mipsinstr.ItypeAsm;
import backend.generation.mips.asm.textsegment.mipsinstr.JtypeAsm;
import backend.generation.mips.asm.textsegment.mipsinstr.MemTypeAsm;
import backend.generation.utils.AssemblyUnit;
import backend.generation.utils.RegisterUtils;
import midend.generation.utils.IrNameController;
import midend.generation.value.Value;
import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.user.Function;
import midend.generation.value.construction.user.Instr;
import midend.simplify.controller.datastruct.FunctionClone;

import java.util.ArrayList;

/**
 * CallInstr 用于生成 LLVM IR 中的 call 指令,
 * 继承于 Instr,主要用于生成函数调用指令
 * <result> = call [ret attrs] <ty> <fnptrval>(<function args>)
 */
public class CallInstr extends Instr {
    /**
     * target 是 call 指令的目标函数
     */
    private final Function target;

    public CallInstr(String name, Function targetFunc, ArrayList<Value> paramList) {
        super(targetFunc.getReturnType(), name, "call");
        addOperand(targetFunc);
        this.target = targetFunc;
        paramList.forEach(this::addOperand);
    }

    public Function getTarget() {
        return target;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (type.isVoid()) {
            sb.append("call void ");
        } else {
            sb.append(name).append(" = call ").append(type).append(" ");
        }
        sb.append(operands.get(0).getName()).append("(");
        for (int i = 1; i < operands.size(); i++) {
            sb.append(operands.get(i).getType()).append(" ").append(operands.get(i).getName());
            if (i != operands.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public void generateAssembly() {
        super.generateAssembly();
        ArrayList<Register> allocatedRegs = AssemblyUnit.getAllocatedRegister();
        int registerOffset = 0;
        int currentOffset = AssemblyUnit.getCurrentOffset();
        for (Register register : allocatedRegs) {
            registerOffset += 4;
            new MemTypeAsm("sw", null, register, Register.SP, currentOffset - registerOffset);
        }
        new MemTypeAsm("sw", null, Register.SP, Register.SP, currentOffset - registerOffset - 4);
        new MemTypeAsm("sw", null, Register.RA, Register.SP, currentOffset - registerOffset - 8);
        for (int paraNum = 1; paraNum < operands.size(); paraNum++) {
            Value para = operands.get(paraNum);
            if (paraNum <= 3 && AssemblyUnit.getRegisterController().getRegisterHashMap() != null) {
                RegisterUtils.allocParamReg(para, Register.regTransform(
                        Register.A0.ordinal() + paraNum), currentOffset, allocatedRegs);
            } else {
                RegisterUtils.allocParamMem(para, Register.K0,
                        currentOffset, allocatedRegs, paraNum);
            }
        }
        new ItypeAsm("addi", Register.SP, Register.SP, currentOffset - registerOffset - 8);
        new JtypeAsm("jal", operands.get(0).getName().substring(1));
        new MemTypeAsm("lw", null, Register.RA, Register.SP, 0);
        new MemTypeAsm("lw", null, Register.SP, Register.SP, 4);
        for (int offset = 0; offset < allocatedRegs.size(); offset++) {
            new MemTypeAsm("lw", null, allocatedRegs.get(offset),
                    Register.SP, currentOffset - (offset + 1) * 4);
        }
        if (AssemblyUnit.getRegisterController().getRegister(this) == null) {
            RegisterUtils.allocReg(this, Register.V0);
        } else {
            new MoveAsm(AssemblyUnit.getRegisterController().getRegister(this), Register.V0);
        }
    }

    @Override
    public String getGlobalVariableNumberingHash() {
        StringBuilder sb = new StringBuilder();
        sb.append(operands.get(0).getName()).append("(");
        for (int i = 1; i < operands.size(); i++) {
            sb.append(operands.get(i).getName());
            if (i != operands.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public Value copy(FunctionClone functionClone) {
        BasicBlock copyBlock = (BasicBlock) functionClone.getValue(this.getBelongingBlock());
        ArrayList<Value> copyParamList = new ArrayList<>();
        for (int i = 1; i < this.getOperands().size(); i++) {
            copyParamList.add(functionClone.getValue(this.getOperands().get(i)));
        }
        Function copyTarget = (Function) functionClone.getValue(this.getOperands().get(0));
        Instr instr = new CallInstr(
                IrNameController.getLocalVarName(functionClone.getCaller()) + "_Inline",
                copyTarget, copyParamList);
        copyBlock.addInstr(instr);
        return instr;
    }

    public boolean isPure() {
        if (this.getUsers().isEmpty() || target.getSideEffect()) {
            return false;
        }
        for (Value value : operands) {
            if (value instanceof GetEleInstr || value instanceof LoadInstr ||
                    value.getType().isPointer()) {
                return false;
            }
        }
        return true;
    }
}
