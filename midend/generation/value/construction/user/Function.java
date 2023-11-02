package midend.generation.value.construction.user;

import backend.generation.mips.Register;
import backend.generation.mips.asm.textsegment.structure.Label;
import backend.generation.utils.AssemblyUnit;
import backend.generation.utils.RegisterAllocator;
import backend.generation.utils.RegisterUtils;
import iostream.DebugDetailController;
import iostream.OptimizerUnit;
import midend.generation.utils.IrNameController;
import midend.generation.utils.IrType;
import midend.generation.utils.irtype.StructType;
import midend.generation.value.Value;
import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.Param;
import midend.generation.value.construction.User;
import midend.simplify.controller.LivenessAnalysisController;
import midend.simplify.controller.datastruct.ControlFlowGraph;
import midend.simplify.controller.datastruct.DominatorTree;
import midend.simplify.method.GlobalVariableNumberingUnit;
import midend.simplify.method.Mem2RegUnit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Function extends User {
    private final IrType returnType;
    private final ArrayList<BasicBlock> basicBlocks;
    private final ArrayList<Param> params;
    private HashMap<Value, Register> registerHashMap;
    private Boolean isImproved;

    public Function(String name, IrType returnType) {
        super(new StructType("function"), name);
        this.returnType = returnType;
        this.basicBlocks = new ArrayList<>();
        this.params = new ArrayList<>();
        this.registerHashMap = null;
        this.isImproved = null;
        if (!OptimizerUnit.isIsOptimizer()) {
            IrNameController.addFunction(this);
        }
    }

    public IrType getReturnType() {
        return returnType;
    }

    public void addBasicBlock(BasicBlock basicBlock, int... idx) {
        if (idx.length == 1) {
            basicBlocks.add(idx[0], basicBlock);
        } else {
            basicBlocks.add(basicBlock);
        }
        basicBlock.setBelongingFunc(this);
    }

    public ArrayList<BasicBlock> getBasicBlocks() {
        return basicBlocks;
    }

    public void addParam(Param param) {
        params.add(param);
        param.setBelongingFunc(this);
    }

    public HashMap<Value, Register> getRegisterHashMap() {
        return registerHashMap;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("define dso_local ").append(returnType.toString())
                .append(" ").append(name).append("(");
        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).toString());
            if (i != params.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(") {\n\n");
        for (int i = 0; i < basicBlocks.size(); i++) {
            sb.append(basicBlocks.get(i).toString());
            if (i != basicBlocks.size() - 1) {
                sb.append("\n\n");
            }
        }
        sb.append("\n}");
        return sb.toString();
    }

    @Override
    public void generateAssembly() {
        new Label(name.substring(1));
        AssemblyUnit.resetFunctionConfig(this);
        for (int i = 0; i < params.size(); i++) {
            if (i < 3) {
                AssemblyUnit.getRegisterController().allocRegister(params.get(i),
                        Register.regTransform(Register.A0.ordinal() + i + 1));
            }
            RegisterUtils.moveValueOffset(params.get(i));
        }
        basicBlocks.forEach(BasicBlock::generateAssembly);
    }

    public void simplifyBlock() {
        basicBlocks.forEach(BasicBlock::simplifyBlock);
    }

    public void insertPhiProcess() {
        Mem2RegUnit.initialBasicBlock = basicBlocks.get(0);
        basicBlocks.forEach(BasicBlock::insertPhiProcess);
    }

    public void deadCodeElimination() {
        basicBlocks.forEach(BasicBlock::deadCodeElimination);
    }

    public void searchBlockDominateSet() {
        BasicBlock entry = basicBlocks.get(0);
        for (BasicBlock basicBlock : basicBlocks) {
            HashSet<BasicBlock> reachedSet = new HashSet<>();
            DominatorTree.dfsDominate(entry, basicBlock, reachedSet);
            ArrayList<BasicBlock> domList = new ArrayList<>();
            for (BasicBlock bb : basicBlocks) {
                if (!reachedSet.contains(bb)) {
                    domList.add(bb);
                }
            }
            DominatorTree.addBlockDominateSet(basicBlock, domList);
        }
    }

    public void searchBlockDominanceFrontier() {
        for (Map.Entry<BasicBlock, ArrayList<BasicBlock>> entry :
                ControlFlowGraph.getFunctionOutBasicBlock(this).entrySet()) {
            BasicBlock from = entry.getKey();
            ArrayList<BasicBlock> outBasicBlocks = entry.getValue();
            for (BasicBlock to : outBasicBlocks) {
                BasicBlock runner = from;
                while (!DominatorTree.getBlockDominateSet(runner).contains(to)
                        || runner.equals(to)) {
                    DominatorTree.addBlockDominanceFrontier(runner, to);
                    runner = DominatorTree.getBlockDominateParent(runner);
                }
            }
        }
    }

    public void uniqueInstr() {
        HashMap<String, Instr> hashMap = new HashMap<>();
        GlobalVariableNumberingUnit.uniqueInstr(basicBlocks.get(0), hashMap);
        GlobalVariableNumberingUnit.addHashMap(this, hashMap);
    }

    public boolean isImprovable() {
        boolean flag = true;
        if (isImproved != null) {
            return isImproved;
        }
        for (Param param : params) {
            if (param.getType().isPointer()) {
                flag = false;
                break;
            }
        }
        for (BasicBlock basicBlock : basicBlocks) {
            if (!(flag = basicBlock.isImprovable(flag))) {
                break;
            }
        }
        isImproved = flag;
        return flag;
    }

    public void analysisActiveness() {
        HashMap<BasicBlock, HashSet<Value>> inMap = new HashMap<>();
        HashMap<BasicBlock, HashSet<Value>> outMap = new HashMap<>();
        LivenessAnalysisController.addInFunctionHashMap(this, inMap);
        LivenessAnalysisController.addOutFunctionHashMap(this, outMap);
        for (BasicBlock basicBlock : basicBlocks) {
            inMap.put(basicBlock, new HashSet<>());
            outMap.put(basicBlock, new HashSet<>());
        }
        basicBlocks.forEach(BasicBlock::analysisActiveness);
        LivenessAnalysisController.calculateInOut(this);
    }

    public void regAllocate() {
        BasicBlock entry = basicBlocks.get(0);
        registerHashMap = new HashMap<>();
//        HashMap<Value, Register> registerHashMap = new HashMap<>();
        HashMap<Register, Value> valueHashMap = new HashMap<>();
        RegisterAllocator.blockAllocate(entry, registerHashMap, valueHashMap);
//        this.registerHashMap = registerHashMap;
        DebugDetailController.printRegisterValueReflection(registerHashMap);
    }

    public void phiEliminate() {
        ArrayList<BasicBlock> copy = new ArrayList<>(basicBlocks);
        copy.forEach(BasicBlock::transformPhiInstrToParallelCopy);
        basicBlocks.forEach(BasicBlock::transformParallelCopyToMoveAsm);
    }

}
