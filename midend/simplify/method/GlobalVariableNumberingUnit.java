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
import midend.simplify.controller.LivenessAnalysisController;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * GlobalVariableNumberingUnit 是全局变量编号单元，
 * 主要用于全局变量编号
 */
public class GlobalVariableNumberingUnit {
    /**
     * GlobalVariableNumberingHashMap 是该 GlobalVariableNumberingUnit 的全局变量编号哈希表
     * 该哈希表用于记录全局变量编号
     * 其中，key为函数，value为该函数的全局变量编号哈希表
     * value哈希表的key为全局变量编号哈希值，value为对应的指令
     * livenessAnalysisController 是该 GlobalVariableNumberingUnit 的活跃变量分析控制器
     * 该控制器用于进行活跃变量分析
     */
    private static HashMap<Function, HashMap<String, Instr>> GlobalVariableNumberingHashMap;
    private static LivenessAnalysisController livenessAnalysisController;

    /**
     * run 方法用于运行全局变量编号，是GVN的主函数
     */
    public static void run(Module module) {
        GlobalVariableNumberingUnit.GlobalVariableNumberingHashMap = new HashMap<>();
        module.getFunctions().forEach(Function::uniqueInstr);
        GlobalVariableNumberingUnit.livenessAnalysisController =
                new LivenessAnalysisController(module);
        GlobalVariableNumberingUnit.livenessAnalysisController.analysis();
    }

    /**
     * addHashMap 方法用于添加全局变量编号哈希表
     */
    public static void addHashMap(Function function, HashMap<String, Instr> hashMap) {
        GlobalVariableNumberingUnit.GlobalVariableNumberingHashMap.put(function, hashMap);
    }

    public static HashMap<String, Instr> getFunctionGlobalVariableNumberingHashMap(
            Function function) {
        return GlobalVariableNumberingUnit.GlobalVariableNumberingHashMap.get(function);
    }

    /**
     * uniqueInstr 方法用于找出可以优化的指令
     * 遍历一遍Instr，找出所有可优化的alu, gep, icmp和func
     * 如果GVN中已存在，直接用该值即可, 否则插入到GVN中
     */
    public static void uniqueInstr(BasicBlock entry, HashMap<String, Instr> hashMap) {
        ConstantFoldingController.foldingCalcInstr(entry);
        HashSet<Instr> vis = new HashSet<>();
        Iterator<Instr> iterator = entry.getInstrArrayList().iterator();
        while (iterator.hasNext()) {
            Instr instr = iterator.next();
            if (instr instanceof CalcInstr || instr instanceof IcmpInstr ||
                    instr instanceof GetEleInstr || (instr instanceof CallInstr callInstr &&
                    ((Function) callInstr.getOperands().get(0)).isImprovable())) {
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
        entry.getBlockDominateChildList()
                .forEach(child -> uniqueInstr(child, hashMap));
        for (Instr instr : vis) {
            hashMap.remove(instr.getGlobalVariableNumberingHash());
        }
    }
}
