package backend.generation.utils;

import backend.generation.mips.Register;
import midend.generation.value.Value;
import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.user.Instr;
import midend.generation.value.instr.basis.ZextInstr;
import midend.generation.value.instr.optimizer.PhiInstr;
import midend.simplify.controller.LivenessAnalysisController;
import midend.simplify.controller.datastruct.DominatorTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class RegisterAllocator {
    private static ArrayList<Register> registers;
    private static HashMap<Value, Register> var2reg;
    private static HashMap<Register, Value> reg2var;

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

    public static void blockAllocate(BasicBlock entry) {
        HashMap<Value, Value> lastUseMap = new HashMap<>();
        HashSet<Value> defined = new HashSet<>();
        HashSet<Value> used = new HashSet<>();
        entry.getInstrArrayList().forEach(instr -> instr.getOperands().forEach(
                operand -> lastUseMap.put(operand, instr)));
        entry.getInstrArrayList().forEach(instr -> releaseReg(entry, instr, lastUseMap,
                var2reg, reg2var, defined, used));
        DominatorTree.getBlockDominateChildList(entry).forEach(RegisterAllocator::reflection);
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

    private static void releaseReg(BasicBlock entry, Instr instr, HashMap<Value, Value> lastUseMap,
                                   HashMap<Value, Register> var2reg,
                                   HashMap<Register, Value> reg2var,
                                   HashSet<Value> defined, HashSet<Value> used) {
        if (!(instr instanceof PhiInstr)) {
            for (Value operand : instr.getOperands()) {
                if (lastUseMap.get(operand).equals(instr) && var2reg.containsKey(operand) &&
                        !LivenessAnalysisController.
                                getOutBasicBlockHashSet(entry).contains(operand)) {
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

    private static Register allocRegFor() {
        Set<Register> allocated = reg2var.keySet();
        for (Register reg : registers) {
            if (!allocated.contains(reg)) {
                return reg;
            }
        }
        return registers.iterator().next();
    }

    private static void reflection(BasicBlock child) {
        HashMap<Register, Value> buffer = new HashMap<>();
        for (Register register : reg2var.keySet()) {
            if (!LivenessAnalysisController.getInBasicBlockHashSet(child)
                    .contains(reg2var.get(register))) {
                buffer.put(register, reg2var.get(register));
            }
        }
        buffer.keySet().forEach(register -> reg2var.remove(register));
        blockAllocate(child);
        buffer.keySet().forEach(register -> reg2var.put(register, buffer.get(register)));
    }
}
