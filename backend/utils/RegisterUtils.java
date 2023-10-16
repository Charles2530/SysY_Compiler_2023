package backend.utils;

import backend.mips.Register;
import backend.mips.asm.datasegment.complex.LaAsm;
import backend.mips.asm.datasegment.complex.LiAsm;
import backend.mips.asm.datasegment.complex.MoveAsm;
import backend.mips.asm.datasegment.mipsinstr.ItypeAsm;
import backend.mips.asm.datasegment.mipsinstr.MemTypeAsm;
import backend.mips.asm.datasegment.mipsinstr.RtypeAsm;
import midend.generation.value.Value;
import midend.generation.value.construction.Constant;
import midend.generation.value.construction.Param;
import midend.generation.value.construction.user.GlobalVar;

import java.util.ArrayList;

public class RegisterUtils {
    public static void loadRegVal(Value operand, Register reg, Register instead) {
        if (operand instanceof Constant) {
            new LiAsm(instead, Integer.parseInt(operand.getName()));
        } else if (reg == null) {
            reg = instead;
            Integer offset = AssemblyUnit.getOffset(operand);
            if (offset == null) {
                AssemblyUnit.moveCurrentOffset(-4);
                offset = AssemblyUnit.getCurrentOffset();
                AssemblyUnit.addOffset(operand, offset);
            }
            new MemTypeAsm("lw", null, reg, Register.SP, offset);
        }
    }

    public static void extractedReg(Value para, Register paraReg,
                                    int currentOffset, ArrayList<Register> allocatedRegs) {
        if (para instanceof Constant) {
            new LiAsm(paraReg, ((Constant) para).getVal());
        } else if (AssemblyUnit.getRegisterController().getRegister(para) != null) {
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
    }

    public static void extractedMem(Value para, Register paraReg, int currentOffset,
                                    ArrayList<Register> allocatedRegs, int paraNum) {
        if (para instanceof Constant) {
            new LiAsm(paraReg, ((Constant) para).getVal());
        } else if (AssemblyUnit.getRegisterController().getRegister(para) != null) {
            Register sourceReg = AssemblyUnit.getRegisterController().getRegister(para);
            if (para instanceof Param) {
                new MemTypeAsm("lw", null, paraReg,
                        Register.SP, currentOffset - (allocatedRegs.indexOf(sourceReg) + 1) * 4);
            } else {
                paraReg = sourceReg;
            }
        } else {
            new MemTypeAsm("lw", null, paraReg, Register.SP, AssemblyUnit.getOffset(para));
        }
        new MemTypeAsm("sw", null, paraReg,
                Register.SP, currentOffset - allocatedRegs.size() * 4 - 8 - 4 * paraNum);
    }

    public static void extractedPointer(Value operand, Register pointerReg, Register instead) {
        if (operand instanceof GlobalVar) {
            new LaAsm(instead, operand.getName().substring(1));
        } else if (pointerReg == null) {
            pointerReg = instead;
            new MemTypeAsm("lw", null, pointerReg, Register.SP, AssemblyUnit.getOffset(operand));
        }
    }

    public static void extractedOffset(Value operand, Register instead,
                                       Register target, Register pointerReg, Register offsetReg) {
        if (operand instanceof Constant) {
            new ItypeAsm("addi", target, pointerReg, ((Constant) operand).getVal() * 4);
        } else {
            if (offsetReg == null) {
                offsetReg = instead;
                Integer offset = AssemblyUnit.getOffset(operand);
                if (offset == null) {
                    AssemblyUnit.moveCurrentOffset(-4);
                    offset = AssemblyUnit.getCurrentOffset();
                    AssemblyUnit.addOffset(operand, offset);
                }
                new MemTypeAsm("lw", null, offsetReg, Register.SP, AssemblyUnit.getOffset(operand));
            }
            new ItypeAsm("sll", instead, offsetReg, 2);
            new RtypeAsm("addu", target, instead, pointerReg);
        }
    }
}
