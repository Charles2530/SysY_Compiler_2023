package midend.generation.value.instr.basis;

import backend.mips.Register;
import backend.mips.asm.datasegment.complex.MoveAsm;
import backend.mips.asm.datasegment.mipsinstr.ItypeAsm;
import backend.mips.asm.datasegment.mipsinstr.JtypeAsm;
import backend.mips.asm.datasegment.mipsinstr.MemTypeAsm;
import backend.utils.AssemblyUnit;
import backend.utils.RegisterUtils;
import midend.generation.value.Value;
import midend.generation.value.construction.user.Function;
import midend.generation.value.construction.user.Instr;

import java.util.ArrayList;

public class CallInstr extends Instr {
    public CallInstr(String name, Function targetFunc, ArrayList<Value> paramList) {
        super(targetFunc.getReturnType(), name, "call");
        addOperand(targetFunc);
        for (Value param : paramList) {
            addOperand(param);
        }
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
        int paraNum = 0;
        for (Value para : operands.subList(1, operands.size())) {
            paraNum++;
            if (paraNum <= 3 && AssemblyUnit.getRegisterController().getRegisterHashMap() != null) {
                RegisterUtils.extractedReg(para, Register.regTransform(
                        Register.A0.ordinal() + paraNum), currentOffset, allocatedRegs);
            } else {
                RegisterUtils.extractedMem(para, Register.K0,
                        currentOffset, allocatedRegs, paraNum);
            }
        }
        new ItypeAsm("addi", Register.SP, Register.SP, currentOffset - registerOffset - 8);
        new JtypeAsm("jal", operands.get(0).getName().substring(2));
        new MemTypeAsm("lw", null, Register.SP, Register.SP, 0);
        new MemTypeAsm("lw", null, Register.RA, Register.SP, 4);
        for (int offset = 0; offset < allocatedRegs.size(); offset++) {
            new MemTypeAsm("lw", null, allocatedRegs.get(offset),
                    Register.SP, currentOffset - (offset + 1) * 4);
        }
        if (AssemblyUnit.getRegisterController().getRegister(this) == null) {
            AssemblyUnit.moveCurrentOffset(-4);
            AssemblyUnit.addOffset(this, AssemblyUnit.getCurrentOffset());
            new MemTypeAsm("sw", null, Register.V0, Register.SP, AssemblyUnit.getCurrentOffset());
        } else {
            new MoveAsm(AssemblyUnit.getRegisterController().getRegister(this), Register.V0);
        }
    }
}
