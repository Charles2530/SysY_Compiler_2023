package midend.simplify.method;

import midend.generation.utils.IrNameController;
import midend.generation.value.Value;
import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.Module;
import midend.generation.value.construction.user.Function;
import midend.generation.value.construction.user.Instr;
import midend.generation.value.instr.basis.LoadInstr;
import midend.generation.value.instr.basis.StoreInstr;
import midend.generation.value.instr.optimizer.PhiInstr;
import midend.simplify.controller.ControlFlowGraphController;
import midend.simplify.controller.datastruct.ControlFlowGraph;
import midend.simplify.controller.datastruct.DominatorTree;
import midend.simplify.controller.datastruct.Use;
import midend.simplify.value.Undefined;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;

public class Mem2RegUnit {
    private static Module module;
    private static ControlFlowGraphController cfGraphController;
    private static BasicBlock initialBasicBlock;
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
        Mem2RegUnit.init();
        Mem2RegUnit.insertPhiProcess();
    }

    private static void insertPhiProcess() {
        for (Function function : module.getFunctions()) {
            Mem2RegUnit.initialBasicBlock = function.getBasicBlocks().get(0);
            function.insertPhiProcess();
        }
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
        for (BasicBlock basicBlock : Mem2RegUnit.defBasicBlockArrayList) {
            w.push(basicBlock);
        }
        while (!w.isEmpty()) {
            BasicBlock x = w.pop();
            for (BasicBlock y : DominatorTree.getBlockDominanceFrontier(x)) {
                if (!f.contains(y)) {
                    f.add(y);
                    Instr phiInstr = new PhiInstr(IrNameController.getLocalVarName(
                            y.getBelongingFunc()), ControlFlowGraph.getBlockIndBasicBlock(y));
                    y.addInstrToFirst(phiInstr);
                    phiInstr.setBelongingBlock(y);
                    useInstrArrayList.add(phiInstr);
                    defInstrArrayList.add(phiInstr);
                    if (!defBasicBlockArrayList.contains(y)) {
                        w.push(y);
                    }
                }
            }
        }
    }

    public static void varRename(BasicBlock initialBasicBlock) {
        int cnt = 0;
        Iterator<Instr> iter = initialBasicBlock.getInstrArrayList().iterator();
        while (iter.hasNext()) {
            Instr instr = iter.next();
            if (instr instanceof LoadInstr && useInstrArrayList.contains(instr)) {
                instr.replaceAllUse(((stack.isEmpty()) ? new Undefined() : stack.peek()));
                iter.remove();
            } else if (instr instanceof StoreInstr storeInstr &&
                    defInstrArrayList.contains(instr)) {
                cnt++;
                stack.push(storeInstr.getOperands().get(0));
                iter.remove();
            } else if (instr instanceof PhiInstr && defInstrArrayList.contains(instr)) {
                cnt++;
                stack.push(instr);
            } else if (instr.equals(Mem2RegUnit.currentAllocaInstr)) {
                iter.remove();
            }
        }
        for (BasicBlock basicBlock : ControlFlowGraph.getBlockOutBasicBlock(initialBasicBlock)) {
            Instr instr = basicBlock.getInstrArrayList().get(0);
            if (instr instanceof PhiInstr phiInstr && useInstrArrayList.contains(instr)) {
                phiInstr.modifyValue(((stack.isEmpty()) ?
                        new Undefined() : stack.peek()), initialBasicBlock);
            }
        }
        for (BasicBlock child : DominatorTree.getBlockDominateChildList(initialBasicBlock)) {
            Mem2RegUnit.varRename(child);
        }
        for (int i = 0; i < cnt; i++) {
            stack.pop();
        }
    }
}
