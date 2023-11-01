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

public class PhiEliminationUnit {
    public static void run(Module module) {
        RegisterAllocator.init();
        module.getFunctions().forEach(Function::regAllocate);
        module.getFunctions().forEach(Function::phiEliminate);
    }

    public static void putParallelCopy(ParallelCopy parallelCopy, BasicBlock indbasicBlock) {
        indbasicBlock.insertInstr(indbasicBlock.getInstrArrayList().size() - 1, parallelCopy);
    }

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

    public static void removePhiInstr(ArrayList<Instr> instrArrayList,
                                      ArrayList<ParallelCopy> pcList) {
        Iterator<Instr> iterator = instrArrayList.iterator();
        while (iterator.hasNext()) {
            Instr instr = iterator.next();
            if (instr instanceof PhiInstr phiInstr) {
                phiInstr.generateCopyList(pcList);
                iterator.remove();
            }
        }
    }

    public static ArrayList<MoveInstr> getMoveAsm(ParallelCopy pc) {
        ArrayList<Value> fromValueList = pc.getFrom();
        ArrayList<Value> toValueList = pc.getTo();
        Function function = pc.getBelongingBlock().getBelongingFunc();
        HashMap<Value, Register> valueRegisterHashMap = function.getRegisterHashMap();
        ArrayList<MoveInstr> moveList = PhiEliminationUnit.
                initialMoveInstrList(fromValueList, toValueList, function);
        ArrayList<MoveInstr> extraMoveList =
                PhiEliminationUnit.genTempMoveList(function, valueRegisterHashMap, moveList);
        extraMoveList.forEach((x) -> moveList.add(0, x));
        return moveList;
    }

    private static ArrayList<MoveInstr> initialMoveInstrList(
            ArrayList<Value> fromValueList, ArrayList<Value> toValueList, Function function) {
        ArrayList<MoveInstr> moveList = new ArrayList<>();
        for (int i = 0; i < fromValueList.size(); i++) {
            Value fromValue = fromValueList.get(i);
            Value toValue = toValueList.get(i);
            moveList.add(new MoveInstr(IrNameController
                    .getLocalVarName(function), fromValue, toValue));
        }
        return moveList;
    }

    private static ArrayList<MoveInstr> genTempMoveList(
            Function function, HashMap<Value, Register> valueRegisterHashMap,
            ArrayList<MoveInstr> moveList) {
        ArrayList<MoveInstr> extra = PhiEliminationUnit.genLoopMoveList(function, moveList);
        extra.addAll(genSharedRegMoveList(function, valueRegisterHashMap, moveList));
        return extra;
    }

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
//                    extra.add(0, new MoveInstr(
//                            IrNameController.getLocalVarName(function), value, midValue));
                    extra.add(new MoveInstr(IrNameController.getLocalVarName(function), value, midValue));
                }
            }
        }
        return extra;
    }

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
//                    extra.add(0, new MoveInstr(
//                            IrNameController.getLocalVarName(function), value, midValue));
                    extra.add(new MoveInstr(
                            IrNameController.getLocalVarName(function), value, midValue));
                }
            }
        }
        return extra;
    }
}
