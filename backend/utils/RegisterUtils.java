package backend.utils;

import backend.mips.Register;
import backend.mips.asm.textsegment.complex.LaAsm;
import backend.mips.asm.textsegment.complex.LiAsm;
import backend.mips.asm.textsegment.complex.MoveAsm;
import backend.mips.asm.textsegment.mipsinstr.ItypeAsm;
import backend.mips.asm.textsegment.mipsinstr.MemTypeAsm;
import backend.mips.asm.textsegment.mipsinstr.RtypeAsm;
import midend.generation.value.Value;
import midend.generation.value.construction.Constant;
import midend.generation.value.construction.Param;
import midend.generation.value.construction.user.GlobalVar;

import java.util.ArrayList;

public class RegisterUtils {
    public static Integer moveValueOffset(Value value) {
        Integer valueOffset;
        AssemblyUnit.moveCurrentOffset(-4);
        valueOffset = AssemblyUnit.getCurrentOffset();
        AssemblyUnit.addOffset(value, valueOffset);
        return valueOffset;
    }

    public static void allocReg(Value value, Register target) {
        moveValueOffset(value);
        new MemTypeAsm("sw", null, target, Register.SP, AssemblyUnit.getCurrentOffset());
    }

    public static void reAllocReg(Value value, Register target) {
        if (AssemblyUnit.getRegisterController().getRegister(value) == null) {
            allocReg(value, target);
        }
    }

    public static Register loadRegisterValue(Value operand, Register instead, Register reg) {
        Register register = reg;
        if (register == null) {
            register = instead;
            Integer offset = AssemblyUnit.getOffset(operand);
            offset = (offset == null) ? moveValueOffset(operand) : offset;
            new MemTypeAsm("lw", null, register, Register.SP, offset);
        }
        return register;
    }

    public static Register loadVariableValue(Value operand, Register reg, Register instead) {
        Register register = reg;
        if (operand instanceof Constant) {
            new LiAsm(instead, Integer.parseInt(operand.getName()));
            return instead;
        }
        register = loadRegisterValue(operand, instead, register);
        return register;
    }

    public static Register loadPointerValue(Value operand, Register pointerReg, Register instead) {
        Register register = pointerReg;
        if (operand instanceof GlobalVar) {
            new LaAsm(instead, operand.getName().substring(1));
            return instead;
        }
        register = loadRegisterValue(operand, instead, register);
        return register;
    }

    public static Register loadMemoryOffset(Value operand, Register instead, Register target,
                                            Register pointerReg, Register offsetReg) {
        Register register = offsetReg;
        if (operand instanceof Constant) {
            new ItypeAsm("addi", target, pointerReg, ((Constant) operand).getVal() * 4);
        } else {
            register = loadRegisterValue(operand, instead, register);
            new ItypeAsm("sll", instead, register, 2);
            new RtypeAsm("add", target, instead, pointerReg);
        }
        return register;
    }

    public static Register allocParamReg(Value para, Register paraReg,
                                         int currentOffset, ArrayList<Register> allocatedRegs) {
        if (para instanceof Constant) {
            new LiAsm(paraReg, ((Constant) para).getVal());
            return paraReg;
        }
        if (AssemblyUnit.getRegisterController().getRegister(para) != null) {
            Register sourceReg = AssemblyUnit.getRegisterController().getRegister(para);
            if (para instanceof Param) {
                new MemTypeAsm("lw", null, paraReg,
                        Register.SP, currentOffset - (allocatedRegs.indexOf(sourceReg) + 1) * 4);
            } else {
                new MoveAsm(paraReg, AssemblyUnit.getRegisterController().getRegister(para));
            }
        } else {
            new MemTypeAsm("lw", null, paraReg, Register.SP, AssemblyUnit.getOffset(para));
        }
        return paraReg;
    }

    public static Register allocParamMem(Value para, Register paraReg, int currentOffset,
                                         ArrayList<Register> allocatedRegs, int paraNum) {
        Register register = paraReg;
        if (para instanceof Constant) {
            new LiAsm(register, ((Constant) para).getVal());
            new MemTypeAsm("sw", null, register,
                    Register.SP, currentOffset - allocatedRegs.size() * 4 - 8 - 4 * paraNum);
            return register;
        }
        if (AssemblyUnit.getRegisterController().getRegister(para) != null) {
            Register sourceReg = AssemblyUnit.getRegisterController().getRegister(para);
            if (para instanceof Param) {
                new MemTypeAsm("lw", null, register,
                        Register.SP, currentOffset - (allocatedRegs.indexOf(sourceReg) + 1) * 4);
            } else {
                register = sourceReg;
            }
        } else {
            new MemTypeAsm("lw", null, register, Register.SP, AssemblyUnit.getOffset(para));
        }
        new MemTypeAsm("sw", null, register,
                Register.SP, currentOffset - allocatedRegs.size() * 4 - 8 - 4 * paraNum);
        return register;
    }
}
