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

import java.util.ArrayList;

/**
 * RegisterUtils 是一个工具类
 * 主要用于生成汇编代码时的寄存器分配
 */
public class RegisterUtils {
    /**
     * moveValueOffset 方法用于移动栈指针并添加
     * 对应的偏移量和value的映射
     */
    public static Integer moveValueOffset(Value value) {
        Integer valueOffset;
        // 移动栈指针并在偏移量和value的映射中更新映射
        AssemblyUnit.moveCurrentOffset(-4);
        valueOffset = AssemblyUnit.getCurrentOffset();
        AssemblyUnit.addOffset(value, valueOffset);
        return valueOffset;
    }

    /**
     * allocReg 方法用于申请分配寄存器
     * 函数执行逻辑如下:
     * 1. 首先将栈指针向下移动4个字节，用来分配寄存器
     * 2. 然后将value的偏移量和寄存器的映射添加到寄存器控制器中
     */
    public static void allocReg(Value value, Register target) {
        moveValueOffset(value);
        new MemTypeAsm("sw", null, target, Register.SP, AssemblyUnit.getCurrentOffset());
    }

    /**
     * reAllocReg 方法用于重新分配寄存器
     * 如果value已经分配了寄存器，则不分配
     */
    public static void reAllocReg(Value value, Register target) {
        if (AssemblyUnit.getRegisterController().getRegister(value) == null) {
            allocReg(value, target);
        }
    }

    /**
     * memAllocReg 方法用于在内存中申请分配寄存器
     */
    public static void memAllocReg(Value value, Register target) {
        if (AssemblyUnit.getRegisterController().getRegister(value) == null) {
            Integer offset = AssemblyUnit.getOffset(value);
            if (offset == null) {
                allocReg(value, target);
            } else {
                // 由于调用这个函数对应的是move指令，所以即使存在对应的offset我们也需要更新该处offset对应的value
                new MemTypeAsm("sw", null, target, Register.SP, offset);
            }
        }
    }

    /**
     * loadRegisterValue 方法用于加载寄存器的值
     */
    public static Register loadRegisterValue(Value operand, Register instead, Register reg) {
        Register register = reg;
        // 如果没有对应的寄存器，则使用默认寄存器
        if (register == null) {
            register = instead;
            // 尝试在栈中查找操作数
            Integer offset = AssemblyUnit.getOffset(operand);
            // 如果没有找到，则移动栈指针并添加对应的偏移量和value的映射
            offset = (offset == null) ? moveValueOffset(operand) : offset;
            new MemTypeAsm("lw", null, register, Register.SP, offset);
        }
        return register;
    }

    /**
     * loadVariableValue 方法用于加载变量的值
     */
    public static Register loadVariableValue(Value operand, Register reg, Register instead) {
        Register register = reg;
        // 如果操作数是Constant，则直接使用li指令
        if (operand instanceof Constant) {
            new LiAsm(instead, ((Constant) operand).getVal());
            return instead;
        }
        // 否则则加载到寄存器中
        register = loadRegisterValue(operand, instead, register);
        return register;
    }

    /**
     * loadPointerValue 方法用于加载指针的值
     */
    public static Register loadPointerValue(Value operand, Register pointerReg, Register instead) {
        Register register = pointerReg;
        // 如果操作数是全局变量，则直接使用la指令
        if (operand instanceof GlobalVar) {
            new LaAsm(instead, operand.getName().substring(1));
            return instead;
        }
        // 否则则加载到寄存器中
        register = loadRegisterValue(operand, instead, register);
        return register;
    }

    /**
     * loadMemoryOffset 方法用于加载内存偏移量
     */
    public static Register loadMemoryOffset(Value operand, Register instead, Register target,
                                            Register pointerReg, Register offsetReg) {
        Register register = offsetReg;
        if (operand instanceof Constant) {
            new ItypeAsm("addi", target, pointerReg, ((Constant) operand).getVal() * 4);
        } else {
            register = loadRegisterValue(operand, instead, register);
            new ItypeAsm("sll", instead, register, 2);
            new RtypeAsm("addu", target, instead, pointerReg);
        }
        return register;
    }

    /**
     * allocParamReg 方法用于在函数参数中分配寄存器
     */
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

    /**
     * allocParamMem 方法用于在函数参数中分配内存
     */
    public static Register allocParamMem(Value para, Register paraReg, int currentOffset,
                                         ArrayList<Register> allocatedRegs, int paraNum) {
        Register register = paraReg;
        if (para instanceof Constant) {
            new LiAsm(register, ((Constant) para).getVal());
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
