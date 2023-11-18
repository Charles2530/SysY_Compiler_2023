package backend.simplify.method;

import backend.generation.mips.Register;
import backend.generation.utils.RegisterAllocator;
import midend.generation.utils.IrNameController;
import midend.generation.utils.irtype.VarType;
import midend.generation.value.Value;
import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.Constant;
import midend.generation.value.construction.Module;
import midend.generation.value.construction.user.Function;
import midend.generation.value.construction.user.Instr;
import midend.generation.value.instr.basis.BrInstr;
import midend.generation.value.instr.basis.JumpInstr;
import midend.generation.value.instr.optimizer.MoveInstr;
import midend.generation.value.instr.optimizer.ParallelCopy;
import midend.generation.value.instr.optimizer.PhiInstr;
import midend.simplify.controller.datastruct.ControlFlowGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * PhiEliminationUnit 是执行PhiElimination的单元
 * 主要用于PhiElimination
 * 便于形成MIPS汇编
 */

public class PhiEliminationUnit {
    /**
     * run 方法用于执行PhiElimination
     */
    public static void run(Module module) {
        RegisterAllocator.init();
        module.getFunctions().forEach(Function::regAllocate);
        module.getFunctions().forEach(Function::phiEliminate);
    }

    /**
     * putParallelCopy 方法用于在基本块中插入ParallelCopy指令
     * 具体插入在跳转语句之前
     */
    public static void putParallelCopy(ParallelCopy parallelCopy, BasicBlock indbasicBlock) {
        indbasicBlock.insertInstr(indbasicBlock.getInstrArrayList().size() - 1, parallelCopy);
    }

    /**
     * insertParallelCopy 方法用于在indBasicBlock和present之间插入ParallelCopy指令
     * 具体而言即将ParallelCopy插入targetBlock,之后修改跳转关系即可
     */
    public static void insertParallelCopy(ParallelCopy parallelCopy,
                                          BasicBlock indbasicBlock, BasicBlock present) {
        BasicBlock targetBlock = new BasicBlock(IrNameController.getBlockName());
        Function function = indbasicBlock.getBelongingFunc();
        function.addBasicBlock(targetBlock, function.getBasicBlocks().indexOf(present));
        targetBlock.addInstr(parallelCopy);
        if (indbasicBlock.getLastInstr() instanceof BrInstr brInstr) {
            targetBlock.addInstr(new JumpInstr(present));
            if (present.equals(brInstr.getThenBlock())) {
                brInstr.setThenBlock(targetBlock);
            } else {
                brInstr.setElseBlock(targetBlock);
            }
        }
        ControlFlowGraph.insertBlockIntoGraph(indbasicBlock, present, targetBlock);
    }

    /**
     * removePhiInstr 方法用于移除Phi指令
     * 该函数执行逻辑如下：
     * 1. 遍历block所有Phi，每个phi中option放到对应的各个ParallelCopy中
     * （options本身就是按照前驱的顺序排列的）
     * 2.如果option是未定义的，那么我们不把他加入对应copy中
     * 3.最后删除phi指令
     */
    public static void removePhiInstr(ArrayList<Instr> instrArrayList,
                                      ArrayList<ParallelCopy> pcList) {
        Iterator<Instr> iterator = instrArrayList.iterator();
        while (iterator.hasNext()) {
            Instr instr = iterator.next();
            if (instr instanceof PhiInstr phiInstr) {
                phiInstr.generateCopyList(pcList);
                iterator.remove();
                phiInstr.dropOperands();
            }
        }
    }

    /**
     * getMoveAsm 方法用于获取Move指令
     * 这里首先创建初始move序列
     * 之后解决循环赋值的问题和共享寄存器问题后
     * 将临时的move序列加入到初始move序列中
     */
    public static ArrayList<MoveInstr> getMoveAsm(ParallelCopy pc) {
        Function function = pc.getBelongingBlock().getBelongingFunc();
        ArrayList<MoveInstr> moveList = PhiEliminationUnit.
                initialMoveInstrList(pc.getFrom(), pc.getTo(), function);
        ArrayList<MoveInstr> extraMoveList = PhiEliminationUnit.genTempMoveList(
                function, function.getRegisterHashMap(), moveList);
        extraMoveList.forEach((x) -> moveList.add(0, x));
        return moveList;
    }

    /**
     * initialMoveInstrList 方法用于获得Move指令集合
     */
    private static ArrayList<MoveInstr> initialMoveInstrList(
            ArrayList<Value> fromValueList, ArrayList<Value> toValueList, Function function) {
        ArrayList<MoveInstr> moveList = new ArrayList<>();
        for (int i = 0; i < fromValueList.size(); i++) {
            moveList.add(new MoveInstr(IrNameController
                    .getLocalVarName(function), fromValueList.get(i), toValueList.get(i)));
        }
        return moveList;
    }

    /**
     * genTempMoveList 方法用于生成临时的Move指令集合
     * 主要是解决循环和共享寄存器的问题
     */
    private static ArrayList<MoveInstr> genTempMoveList(
            Function function, HashMap<Value, Register> valueRegisterHashMap,
            ArrayList<MoveInstr> moveList) {
        ArrayList<MoveInstr> extra = PhiEliminationUnit.genLoopMoveList(function, moveList);
        extra.addAll(genSharedRegMoveList(function, valueRegisterHashMap, moveList));
        return extra;
    }

    /**
     * genLoopMoveList 方法用于生成循环的Move指令集合
     * 主要是解决循环的问题
     * 函数执行逻辑如下：
     * 1.检查该指令之后的所有指令，如果value同时是某一个move的src，那么存在循环赋值的问题
     * 2.如果出现了循环赋值的情况，我们需要增加中间变量，即将所有使用value作为src的move指令，
     * 将改为使用midValue作为src，最后在moveList的开头插入新的move
     */
    private static ArrayList<MoveInstr> genLoopMoveList(
            Function function, ArrayList<MoveInstr> moveList) {
        ArrayList<MoveInstr> extra = new ArrayList<>();
        HashSet<Value> vis = new HashSet<>();
        for (int i = 0; i < moveList.size(); i++) {
            Value value = moveList.get(i).getTo();
            if (!(value instanceof Constant || vis.contains(value))) {
                vis.add(value);
                boolean isLoop = false;
                for (int j = i + 1; j < moveList.size(); j++) {
                    if (moveList.get(j).getFrom().equals(value)) {
                        isLoop = true;
                        break;
                    }
                }
                if (isLoop) {
                    Value midValue = new Value(new VarType(32), value.getName() + "_tmp");
                    for (MoveInstr moveInstr : moveList) {
                        if (moveInstr.getFrom().equals(value)) {
                            moveInstr.setFrom(midValue);
                        }
                    }
                    extra.add(0, new MoveInstr(
                            IrNameController.getLocalVarName(function), value, midValue));
                }
            }
        }
        return extra;
    }

    /**
     * genSharedRegMoveList 方法用于生成共享寄存器的Move指令集合
     * 主要是解决共享寄存器的问题
     * 函数执行逻辑如下：
     * 1.检查该指令之前的所有指令，
     * 如果value对应的reg同时是某一个move的dst的reg，那么存在寄存器冲突的问题
     * 2.如果出现了寄存器冲突的情况，我们需要增加中间变量，将所有使用value作为src的move指令，
     * 将改为使用midValue作为src，在moveList的开头插入新的move
     */
    private static ArrayList<MoveInstr> genSharedRegMoveList(Function function, HashMap<Value,
            Register> valueRegisterHashMap, ArrayList<MoveInstr> moveList) {
        ArrayList<MoveInstr> extra = new ArrayList<>();
        HashSet<Value> vis = new HashSet<>();
        for (int i = moveList.size() - 1; i > 0; i--) {
            Value value = moveList.get(i).getFrom();
            if (!(value instanceof Constant || vis.contains(value))) {
                vis.add(value);
                boolean isSharedReg = false;
                for (int j = 0; j < i; j++) {
                    if (valueRegisterHashMap.get(value) != null && valueRegisterHashMap.get(value)
                            .equals(valueRegisterHashMap.get(moveList.get(j).getTo()))) {
                        isSharedReg = true;
                        break;
                    }
                }
                if (isSharedReg) {
                    Value midValue = new Value(new VarType(32), value.getName() + "_tmp");
                    for (MoveInstr moveInstr : moveList) {
                        if (moveInstr.getFrom().equals(value)) {
                            moveInstr.setFrom(midValue);
                        }
                    }
                    extra.add(0, new MoveInstr(
                            IrNameController.getLocalVarName(function), value, midValue));
                }
            }
        }
        return extra;
    }
}
