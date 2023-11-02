package midend.simplify.method;

import midend.generation.utils.IrNameController;
import midend.generation.utils.irtype.VarType;
import midend.generation.value.Value;
import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.Constant;
import midend.generation.value.construction.Module;
import midend.generation.value.construction.user.Function;
import midend.generation.value.construction.user.Instr;
import midend.generation.value.instr.basis.LoadInstr;
import midend.generation.value.instr.basis.StoreInstr;
import midend.generation.value.instr.optimizer.PhiInstr;
import midend.simplify.controller.LivenessAnalysisController;
import midend.simplify.controller.ControlFlowGraphController;
import midend.simplify.controller.datastruct.ControlFlowGraph;
import midend.simplify.controller.datastruct.DominatorTree;
import midend.simplify.controller.datastruct.Use;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;

public class Mem2RegUnit {
    private static Module module;
    private static ControlFlowGraphController cfGraphController;
    public static BasicBlock initialBasicBlock;
    private static ArrayList<Instr> useInstrArrayList;
    private static ArrayList<Instr> defInstrArrayList;
    private static ArrayList<BasicBlock> useBasicBlockArrayList;
    private static ArrayList<BasicBlock> defBasicBlockArrayList;
    private static Stack<Value> stack;
    private static Instr currentAllocaInstr;

    public static BasicBlock getInitialBasicBlock() {
        return initialBasicBlock;
    }

    private static void init() {
        Mem2RegUnit.cfGraphController = new ControlFlowGraphController(module);
        Mem2RegUnit.cfGraphController.build();
    }

    public static void run(Module module) {
        Mem2RegUnit.module = module;
        module.simplifyBlock();
        Mem2RegUnit.init();
        module.getFunctions().forEach(Function::insertPhiProcess);
    }

    public static void reConfig(Instr instr) {
        Mem2RegUnit.useInstrArrayList = new ArrayList<>();
        Mem2RegUnit.defInstrArrayList = new ArrayList<>();
        Mem2RegUnit.useBasicBlockArrayList = new ArrayList<>();
        Mem2RegUnit.defBasicBlockArrayList = new ArrayList<>();
        Mem2RegUnit.stack = new Stack<>();
        Mem2RegUnit.currentAllocaInstr = instr;
        for (Use use : instr.getUseDefChain()) {
            Instr user = (Instr) use.getUser();
            if (user instanceof LoadInstr loadInstr
                    && loadInstr.getBelongingBlock().isExist()) {
                useInstrArrayList.add(loadInstr);
                if (!defBasicBlockArrayList.contains(loadInstr.getBelongingBlock())) {
                    useBasicBlockArrayList.add(loadInstr.getBelongingBlock());
                }
            } else if (user instanceof StoreInstr storeInstr
                    && storeInstr.getBelongingBlock().isExist()) {
                defInstrArrayList.add(storeInstr);
                if (!defBasicBlockArrayList.contains(storeInstr.getBelongingBlock())) {
                    defBasicBlockArrayList.add(storeInstr.getBelongingBlock());
                }
            }
        }
    }

    public static void insertPhi() {
        HashSet<BasicBlock> f = new HashSet<>();
        Stack<BasicBlock> w = new Stack<>();
        Mem2RegUnit.defBasicBlockArrayList.forEach(w::push);
        while (!w.isEmpty()) {
            BasicBlock x = w.pop();
            for (BasicBlock y : DominatorTree.getBlockDominanceFrontier(x)) {
                if (!f.contains(y)) {
                    f.add(y);
                    Instr phiInstr = new PhiInstr(IrNameController.getLocalVarName(
                            y.getBelongingFunc()), ControlFlowGraph.getBlockIndBasicBlock(y));
                    y.insertInstr(0, phiInstr);
                    useInstrArrayList.add(phiInstr);
                    defInstrArrayList.add(phiInstr);
                    if (!defBasicBlockArrayList.contains(y)) {
                        w.push(y);
                    }
                }
            }
        }
    }

    public static void dfsVarRename(BasicBlock presentBlock) {
        int cnt = removeUnnecessaryInstr(presentBlock);
        for (BasicBlock basicBlock : ControlFlowGraph.getBlockOutBasicBlock(presentBlock)) {
            Instr instr = basicBlock.getInstrArrayList().get(0);
            if (instr instanceof PhiInstr phiInstr && useInstrArrayList.contains(phiInstr)) {
                phiInstr.modifyValue(((stack.isEmpty()) ?
                        new Constant("0", new VarType(32), false) : stack.peek()), presentBlock);
            }
        }
        DominatorTree.getBlockDominateChildList(presentBlock).forEach(Mem2RegUnit::dfsVarRename);
        for (int i = 0; i < cnt; i++) {
            stack.pop();
        }
    }

    private static int removeUnnecessaryInstr(BasicBlock presentBlock) {
        int instrNum = 0;
        Iterator<Instr> iter = presentBlock.getInstrArrayList().iterator();
        while (iter.hasNext()) {
            Instr instr = iter.next();
            if (instr instanceof StoreInstr storeInstr &&
                    defInstrArrayList.contains(storeInstr)) {
                instrNum++;
                stack.push(storeInstr.getOperands().get(0));
                iter.remove();
            } else if (instr instanceof LoadInstr loadInstr &&
                    useInstrArrayList.contains(loadInstr)) {
                loadInstr.replaceAllUse(((stack.isEmpty()) ?
                        new Constant("0", new VarType(32), false) : stack.peek()));
                iter.remove();
            } else if (instr instanceof PhiInstr phiInstr && defInstrArrayList.contains(phiInstr)) {
                instrNum++;
                stack.push(phiInstr);
            } else if (instr.equals(Mem2RegUnit.currentAllocaInstr)) {
                iter.remove();
            }
        }
        return instrNum;
    }
}
