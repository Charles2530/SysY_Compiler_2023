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
import midend.simplify.controller.ControlFlowGraphController;
import midend.simplify.controller.datastruct.Use;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;

/**
 * Mem2RegUnit 是执行mem2reg的单元
 * 主要用于mem2reg
 */
public class Mem2RegUnit {
    /**
     * module 是LLVM IR生成的顶级模块
     * cfGraphController 是该 Mem2RegUnit 的控制流图
     * initialBasicBlock 存储了每个函数的第一个基本块，
     * 便于在不同函数中传递该参数
     * useInstrArrayList 存储了所有使用该alloca指令的load指令，以及phi指令
     * defInstrArrayList 存储了所有使用该alloca指令的store指令，以及phi指令
     * useBasicBlockArrayList 存储了所有使用alloca指令的基本块
     * defBasicBlockArrayList 存储了所有定义alloca指令的基本块
     * stack 是一个栈，用于存储phi指令的值
     * currentAllocaInstr 是当前的alloca指令
     */
    private static Module module;
    private static ControlFlowGraphController cfGraphController;
    private static BasicBlock initialBasicBlock;
    private static ArrayList<Instr> useInstrArrayList;
    private static ArrayList<Instr> defInstrArrayList;
    private static ArrayList<BasicBlock> useBasicBlockArrayList;
    private static ArrayList<BasicBlock> defBasicBlockArrayList;
    private static Stack<Value> stack;
    private static Instr currentAllocaInstr;

    /**
     * setInitialBasicBlock 方法用于设置InitialBasicBlock的值
     */
    public static void setInitialBasicBlock(BasicBlock initialBasicBlock) {
        Mem2RegUnit.initialBasicBlock = initialBasicBlock;
    }

    public static BasicBlock getInitialBasicBlock() {
        return initialBasicBlock;
    }

    /**
     * run 方法用于执行mem2reg
     * 是执行Mem2Reg的主函数
     */
    public static void run(Module module) {
        Mem2RegUnit.module = module;
        module.simplifyBlock();
        Mem2RegUnit.init();
        module.getFunctions().forEach(Function::insertPhiProcess);
    }

    /**
     * init 方法用于初始化Mem2RegUnit
     * 主要需要构建控制流图CFG
     */
    private static void init() {
        Mem2RegUnit.cfGraphController = new ControlFlowGraphController(module);
        Mem2RegUnit.cfGraphController.build();
    }

    /**
     * reConfig 方法用于重新配置某个Instr对应的
     * useInstrArrayList，defInstrArrayList，
     * useBasicBlockArrayList，defBasicBlockArrayList
     * 并获得对应当前Instr的相应集合
     */
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

    /**
     * insertPhi 方法用于插入Phi指令
     * 主要用于在CFG中插入Phi指令
     */
    public static void insertPhi() {
        HashSet<BasicBlock> f = new HashSet<>();
        Stack<BasicBlock> w = new Stack<>();
        Mem2RegUnit.defBasicBlockArrayList.forEach(w::push);
        while (!w.isEmpty()) {
            BasicBlock x = w.pop();
            for (BasicBlock y : x.getBlockDominanceFrontier()) {
                if (!f.contains(y)) {
                    f.add(y);
                    Instr phiInstr = new PhiInstr(IrNameController.getLocalVarName(
                            y.getBelongingFunc()), y.getBlockIndBasicBlock());
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

    /**
     * dfsVarRename 方法用于深度优先搜索基本块
     * 并对基本块中的变量进行重命名
     */
    public static void dfsVarRename(BasicBlock presentBlock) {
        int cnt = removeUnnecessaryInstr(presentBlock);
        for (BasicBlock basicBlock : presentBlock.getBlockOutBasicBlock()) {
            Instr instr = basicBlock.getInstrArrayList().get(0);
            if (instr instanceof PhiInstr phiInstr && useInstrArrayList.contains(phiInstr)) {
                phiInstr.modifyValue(((stack.isEmpty()) ?
                        new Constant("0", new VarType(32), false) : stack.peek()), presentBlock);
            }
        }
        presentBlock.getBlockDominateChildList().forEach(Mem2RegUnit::dfsVarRename);
        for (int i = 0; i < cnt; i++) {
            stack.pop();
        }
    }

    /**
     * removeUnnecessaryInstr 方法用于删除不必要的指令
     * 主要用于删除不必要的相关的alloca, store,load指令
     */
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
