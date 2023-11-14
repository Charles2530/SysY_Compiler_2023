package midend.simplify.controller.datastruct;

import midend.generation.value.Value;
import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.Constant;
import midend.generation.value.construction.user.Function;
import midend.generation.value.construction.user.GlobalVar;
import midend.generation.value.construction.user.Instr;
import midend.generation.value.instr.optimizer.PhiInstr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class FunctionClone {
    private final HashMap<Value, Value> copyMap;
    private final HashSet<BasicBlock> visited;
    private final ArrayList<PhiInstr> phiList;

    public FunctionClone() {
        this.copyMap = new HashMap<>();
        this.visited = new HashSet<>();
        this.phiList = new ArrayList<>();
    }

    public Function copyFunc(Function response) {
        this.clear();
        Function copyFunction = new Function(
                response.getName() + "_COPY", response.getReturnType());
        for (int i = 0; i < response.getParams().size(); i++) {
            this.putValue(response.getParams().get(i), copyFunction.getParams().get(i));
        }
        for (BasicBlock basicBlock : response.getBasicBlocks()) {
            BasicBlock copyBlock = new BasicBlock(basicBlock.getName() + "_COPY");
            copyFunction.addBasicBlock(copyBlock);
            this.putValue(basicBlock, copyBlock);
        }
        copyBlocks(response.getBasicBlocks().get(0));
        for (PhiInstr phiInstr : phiList) {
            for (int i = 0; i < phiInstr.getOperands().size(); i++) {
                Value operand = phiInstr.getOperands().get(i);
                PhiInstr reflectPhi = ((PhiInstr) getValue(phiInstr));
                reflectPhi.getOperands().set(i, getValue(operand));
                getValue(operand).addUseDefChain(reflectPhi);
            }
        }
        return copyFunction;
    }

    private void copyBlocks(BasicBlock basicBlock) {
        for (Instr instr : basicBlock.getInstrArrayList()) {
            putValue(instr, instr.copy(this));
        }
        for (BasicBlock outBasicBlock : basicBlock.getBlockOutBasicBlock()) {
            if (!visited.contains(outBasicBlock)) {
                visited.add(outBasicBlock);
                copyBlocks(outBasicBlock);
            }
        }
    }

    private void clear() {
        this.copyMap.clear();
        this.visited.clear();
        this.phiList.clear();
    }

    public Value getValue(Value source) {
        return (source instanceof GlobalVar || source instanceof Constant
                || source instanceof Function) ? source : copyMap.get(source);
    }

    public void putValue(Value source, Value copy) {
        copyMap.put(source, copy);
    }
}
