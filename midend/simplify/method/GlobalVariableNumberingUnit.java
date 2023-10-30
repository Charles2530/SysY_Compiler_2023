package midend.simplify.method;

import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.Module;
import midend.generation.value.construction.user.Function;
import midend.generation.value.construction.user.Instr;
import midend.generation.value.instr.basis.CalcInstr;
import midend.generation.value.instr.basis.CallInstr;
import midend.generation.value.instr.basis.GetEleInstr;
import midend.generation.value.instr.basis.IcmpInstr;
import midend.simplify.controller.ConstantFoldingController;
import midend.simplify.controller.datastruct.DominatorTree;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class GlobalVariableNumberingUnit {
    private static HashMap<Function, HashMap<String, Instr>> GlobalVariableNumberingHashMap;

    public static void run(Module module) {
        GlobalVariableNumberingUnit.GlobalVariableNumberingHashMap = new HashMap<>();
        module.getFunctions().forEach(Function::uniqueInstr);
    }

    public static void addHashMap(Function function, HashMap<String, Instr> hashMap) {
        GlobalVariableNumberingUnit.GlobalVariableNumberingHashMap.put(function, hashMap);
    }

    public static HashMap<String, Instr> getFunctionGlobalVariableNumberingHashMap(
            Function function) {
        return GlobalVariableNumberingUnit.GlobalVariableNumberingHashMap.get(function);
    }

    public static void uniqueInstr(BasicBlock entry, HashMap<String, Instr> hashMap) {
        ConstantFoldingController.foldingCalcInstr(entry);
        HashSet<Instr> vis = new HashSet<>();
        Iterator<Instr> iterator = entry.getInstrArrayList().iterator();
        while (iterator.hasNext()) {
            Instr instr = iterator.next();
            if (instr instanceof CalcInstr || instr instanceof IcmpInstr ||
                    instr instanceof GetEleInstr || instr instanceof CallInstr callInstr &&
                    ((Function) callInstr.getOperands().get(0)).isImprovable()) {
                String hash = instr.getGlobalVariableNumberingHash();
                if (hashMap.containsKey(hash)) {
                    instr.replaceAllUse(hashMap.get(hash));
                    iterator.remove();
                } else {
                    hashMap.put(hash, instr);
                    vis.add(instr);
                }
            }
        }
        DominatorTree.getBlockDominateChildList(entry)
                .forEach(child -> uniqueInstr(child, hashMap));
        for (Instr instr : vis) {
            hashMap.remove(instr.getGlobalVariableNumberingHash());
        }
    }
}
