package midend.simplify.controller.datastruct;

import midend.generation.utils.IrNameController;
import midend.generation.value.Value;
import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.Constant;
import midend.generation.value.construction.Param;
import midend.generation.value.construction.user.Function;
import midend.generation.value.construction.user.GlobalVar;
import midend.generation.value.construction.user.Instr;
import midend.generation.value.instr.optimizer.PhiInstr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * FunctionClone 是函数克隆的数据结构，
 * 主要用于函数克隆，是函数内联的辅助数据结构
 */
public class FunctionClone {
    /**
     * copyMap 是克隆的映射表，
     * visited 是克隆的基本块的集合，表示已经访问过的基本块
     * phiList 是克隆的phi指令的集合
     * caller 是克隆的函数的调用者,即克隆后将要被内联的函数
     */
    private final HashMap<Value, Value> copyMap;
    private final HashSet<BasicBlock> visited;
    private final ArrayList<PhiInstr> phiList;
    private final Function caller;

    public FunctionClone(Function caller) {
        this.copyMap = new HashMap<>();
        this.visited = new HashSet<>();
        this.phiList = new ArrayList<>();
        this.caller = caller;
    }

    public Function getCaller() {
        return caller;
    }

    /**
     * copyFunc 方法用于克隆函数
     */
    public Function copyFunc(Function response) {
        this.clear();
        Function copyFunction = new Function(
                response.getName() + "_Inline", response.getReturnType());
        ControlFlowGraph.addFunctionIndBasicBlock(copyFunction, new HashMap<>());
        ControlFlowGraph.addFunctionOutBasicBlock(copyFunction, new HashMap<>());
        for (int i = 0; i < response.getParams().size(); i++) {
            Param param = response.getParams().get(i).copy();
            copyFunction.addParam(param);
            this.putValue(response.getParams().get(i), param);
        }
        for (BasicBlock basicBlock : response.getBasicBlocks()) {
            BasicBlock copyBlock = new BasicBlock(
                    IrNameController.getBlockName(getCaller()) + "_Inline");
            copyFunction.addBasicBlock(copyBlock);
            this.putValue(basicBlock, copyBlock);
        }
        copyBlocks(response.getBasicBlocks().get(0));
        for (PhiInstr phiInstr : phiList) {
            for (int i = 0; i < phiInstr.getOperands().size(); i++) {
                Value operand = phiInstr.getOperands().get(i);
                BasicBlock basicBlock = phiInstr.getIndBasicBlock().get(i);
                PhiInstr reflectPhi = ((PhiInstr) getValue(phiInstr));
                reflectPhi.getOperands().set(i, getValue(operand));
                reflectPhi.getIndBasicBlock().set(i, (BasicBlock) getValue(basicBlock));
                getValue(operand).addUseDefChain(reflectPhi);
                getValue(basicBlock).addUseDefChain(reflectPhi);
            }
        }
        return copyFunction;
    }

    /**
     * copyBlocks 方法用于克隆基本块集合
     */
    private void copyBlocks(BasicBlock basicBlock) {
        for (Instr instr : basicBlock.getInstrArrayList()) {
            Value copyInstr = instr.copy(this);
            putValue(instr, copyInstr);
            if (instr instanceof PhiInstr phiInstr) {
                phiList.add(phiInstr);
            }
        }
        for (BasicBlock outBasicBlock : basicBlock.getBlockOutBasicBlock()) {
            if (!visited.contains(outBasicBlock)) {
                visited.add(outBasicBlock);
                copyBlocks(outBasicBlock);
            }
        }
    }

    /**
     * clear 方法用于清空数据结构，
     * 每次在进入一个新的函数时，都需要清空数据结构
     */
    private void clear() {
        this.copyMap.clear();
        this.visited.clear();
        this.phiList.clear();
    }

    /**
     * getValue 方法用于获取克隆的值
     * 这里当源值为全局变量、常量或函数时，直接返回源值
     */
    public Value getValue(Value source) {
        return (source instanceof GlobalVar || source instanceof Constant
                || source instanceof Function) ? source : copyMap.get(source);
    }

    public void putValue(Value source, Value copy) {
        copyMap.put(source, copy);
    }
}
