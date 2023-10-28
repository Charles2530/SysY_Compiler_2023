package backend.generation.utils;

import backend.generation.mips.Register;
import backend.generation.mips.asm.textsegment.complex.LaAsm;
import backend.generation.mips.asm.textsegment.complex.LiAsm;
import backend.generation.mips.asm.textsegment.complex.MoveAsm;
import backend.generation.mips.asm.textsegment.mipsinstr.ItypeAsm;
import backend.generation.mips.asm.textsegment.mipsinstr.MemTypeAsm;
import backend.generation.mips.asm.textsegment.mipsinstr.RtypeAsm;
import midend.generation.value.Value;
import midend.generation.value.construction.Constant;
import midend.generation.value.construction.Param;
import midend.generation.value.construction.user.GlobalVar;
import midend.simplify.value.Undefined;

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
        if (operand instanceof Constant || operand instanceof Undefined) {
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
        if (operand instanceof Constant || operand instanceof Undefined) {
            new ItypeAsm("addi", target, pointerReg, Integer.parseInt(operand.getName()) * 4);
        } else {
            register = loadRegisterValue(operand, instead, register);
            new ItypeAsm("sll", instead, register, 2);
            new RtypeAsm("add", target, instead, pointerReg);
        }
        return register;
    }

    public static Register allocParamReg(Value para, Register paraReg,
                                         int currentOffset, ArrayList<Register> allocatedRegs) {
        if (para instanceof Constant || para instanceof Undefined) {
            new LiAsm(paraReg, Integer.parseInt(para.getName()));
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
        if (para instanceof Constant || para instanceof Undefined) {
            new LiAsm(register, Integer.parseInt(para.getName()));
            new MemTypeAsm("sw", null, register,
                    Register.SP, currentOffset - (allocatedRegs.size() + 2 + paraNum) * 4);
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
                Register.SP, currentOffset - (allocatedRegs.size() + 2 + paraNum) * 4);
        return register;
    }
}
