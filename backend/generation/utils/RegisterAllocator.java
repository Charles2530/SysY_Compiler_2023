package backend.generation.utils;

import backend.generation.mips.Register;
import midend.generation.value.Value;
import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.user.Instr;
import midend.generation.value.instr.basis.ZextInstr;
import midend.generation.value.instr.optimizer.PhiInstr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * RegisterAllocator 是一个寄存器分配器
 * 主要是用于代码优化后的寄存器分配
 */
public class RegisterAllocator {
    /**
     * registers 是寄存器的列表,存储了MIPS中可以使用的寄存器
     * var2reg 是一个value和寄存器的映射
     * reg2var 是一个寄存器和value的映射
     */
    private static ArrayList<Register> registers;
    private static HashMap<Value, Register> var2reg;
    private static HashMap<Register, Value> reg2var;

    /**
     * init 方法用于初始化寄存器分配器
     * 这里没有区分临时寄存器和全局寄存器，均作为临时寄存器使用
     */
    public static void init() {
        RegisterAllocator.registers = new ArrayList<>();
        for (Register value : Register.values()) {
            if (value.ordinal() >= Register.T0.ordinal() &&
                    value.ordinal() <= Register.T9.ordinal()) {
                registers.add(value);
            }
        }
    }

    public static void setRegisterHashMap(HashMap<Value, Register> var2reg) {
        RegisterAllocator.var2reg = var2reg;
    }

    public static void setValueHashMap(HashMap<Register, Value> reg2var) {
        RegisterAllocator.reg2var = reg2var;
    }

    public static HashMap<Value, Register> getRegisterHashMap() {
        return var2reg;
    }

    /**
     * blockAllocate 方法用于分配寄存器给基本块
     * 因为key的最后一次使用是在value中,所以我们需要定义lastUseMap
     * 该函数执行逻辑如下：
     * 1.遍历一边所有指令，记录每个变量在该基本块里最后一次被使用的位置
     * 2.再遍历一遍所有指令,执行releaseReg方法
     * 3.遍历其直接支配的节点,调用映射函数reflection
     * 4.将该基本块定义的变量对应的寄存器释放
     * 5.将“后继不再使用但是是从indBasicBlock传过来”的变量对应的寄存器映射恢复回来
     * 也就是在used中，但是不是在当前基本块定义的变量
     */
    public static void blockAllocate(BasicBlock entry) {
        HashMap<Value, Value> lastUseMap = new HashMap<>();
        HashSet<Value> defined = new HashSet<>();
        HashSet<Value> used = new HashSet<>();
        entry.getInstrArrayList().forEach(instr -> instr.getOperands().forEach(
                operand -> lastUseMap.put(operand, instr)));
        entry.getInstrArrayList().forEach(instr -> releaseReg(entry, instr, lastUseMap,
                var2reg, reg2var, defined, used));
        entry.getBlockDominateChildList().forEach(RegisterAllocator::reflection);
        for (Value value : defined) {
            if (var2reg.containsKey(value)) {
                reg2var.remove(var2reg.get(value));
            }
        }
        for (Value value : used) {
            if (var2reg.containsKey(value) && !defined.contains(value)) {
                reg2var.put(var2reg.get(value), value);
            }
        }
    }

    /**
     * releaseReg 方法用于释放寄存器
     * 1.如果该指令的某个operand是该基本块内的最后一次使用，
     * 并且该基本块的out中没有这个operand,那么我们可以暂时
     * 释放这个变量所占用的寄存器（释放reg2var，但不改变var2reg）
     * 2.如果该指令属于定义语句，并且不是创建数组的alloc指令，
     * 我们需要为该变量分配寄存器
     */
    private static void releaseReg(BasicBlock entry, Instr instr, HashMap<Value, Value> lastUseMap,
                                   HashMap<Value, Register> var2reg,
                                   HashMap<Register, Value> reg2var,
                                   HashSet<Value> defined, HashSet<Value> used) {
        if (!(instr instanceof PhiInstr)) {
            for (Value operand : instr.getOperands()) {
                if (lastUseMap.get(operand).equals(instr) && var2reg.containsKey(operand) &&
                        !entry.getOutBasicBlockHashSet().contains(operand)) {
                    reg2var.remove(var2reg.get(operand));
                    used.add(operand);
                }
            }
        }
        if (instr.isValid() && !(instr instanceof ZextInstr)) {
            defined.add(instr);
            Register reg = allocRegFor();
            if (reg != null) {
                if (reg2var.containsKey(reg)) {
                    var2reg.remove(reg2var.get(reg));
                }
                reg2var.put(reg, instr);
                var2reg.put(instr, reg);
            }
        }
    }

    /**
     * allocRegFor 方法用于分配寄存器，通过遍历寄存器列表，找到一个未被分配的寄存器
     */
    private static Register allocRegFor() {
        Set<Register> allocated = reg2var.keySet();
        for (Register reg : registers) {
            if (!allocated.contains(reg)) {
                return reg;
            }
        }
        return registers.iterator().next();
    }

    /**
     * reflection 映射方法执行逻辑如下：
     * 1.如果当前个寄存器所对应的变量在其child中不会被使用到(即in中不包含该变量)
     * 可以记录这个映射关系到buffer中，并释放该寄存器，当为child（包括child的child）
     * 分配完寄存器后，再将buffer中的映射关系恢复，以免影响其兄弟节点的寄存器分配
     * 2.之后为其child调用该函数实现递归调用
     * 3.最后在回溯过程中将buffer中存储的被删除的映射关系恢复
     */
    private static void reflection(BasicBlock child) {
        HashMap<Register, Value> buffer = new HashMap<>();
        for (Register register : reg2var.keySet()) {
            if (!child.getInBasicBlockHashSet().contains(reg2var.get(register))) {
                buffer.put(register, reg2var.get(register));
            }
        }
        buffer.keySet().forEach(register -> reg2var.remove(register));
        blockAllocate(child);
        buffer.keySet().forEach(register -> reg2var.put(register, buffer.get(register)));
    }
}
