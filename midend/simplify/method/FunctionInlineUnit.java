package midend.simplify.method;

import midend.generation.utils.IrNameController;
import midend.generation.utils.IrType;
import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.Module;
import midend.generation.value.construction.Param;
import midend.generation.value.construction.user.Function;
import midend.generation.value.construction.user.Instr;
import midend.generation.value.instr.basis.AllocaInstr;
import midend.generation.value.instr.basis.CallInstr;
import midend.generation.value.instr.basis.JumpInstr;
import midend.generation.value.instr.basis.LoadInstr;
import midend.generation.value.instr.basis.RetInstr;
import midend.generation.value.instr.basis.StoreInstr;
import midend.generation.value.instr.optimizer.PhiInstr;
import midend.simplify.controller.datastruct.FunctionClone;
import midend.simplify.controller.datastruct.Use;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * FunctionInlineUnit 是函数内联单元，
 * 主要用于函数内联
 */
public class FunctionInlineUnit {
    /**
     * module 是LLVM IR生成的顶级模块
     * fixedPoint 是函数内联的迭代标志，因为函数内联是迭代进行的，所以有一个不动点设计
     * callers 是该 FunctionInlineUnit 的调用者哈希表
     * responses 是该 FunctionInlineUnit 的响应者哈希表
     * inlineFunctionsList 是该 FunctionInlineUnit 的内联函数列表
     * isInlineAble 是判断当前函数是否可以内联的标志
     */
    private static Module module;
    private static boolean fixedPoint;
    private static HashMap<Function, ArrayList<Function>> callers;
    private static HashMap<Function, ArrayList<Function>> responses;
    private static ArrayList<Function> inlineFunctionsList;
    private static boolean isInlineAble;

    /**
     * run 方法用于运行函数内联单元，是函数内联的主函数
     */
    public static void run(Module module) {
        FunctionInlineUnit.module = module;
        FunctionInlineUnit.init();
        FunctionInlineUnit.inlineAnalysis();
    }

    /**
     * init 方法用于初始化函数内联单元
     */
    private static void init() {
        FunctionInlineUnit.fixedPoint = false;
        FunctionInlineUnit.isInlineAble = true;
        FunctionInlineUnit.callers = new HashMap<>();
        FunctionInlineUnit.responses = new HashMap<>();
        for (Function function : module.getFunctions()) {
            FunctionInlineUnit.callers.put(function, new ArrayList<>());
            FunctionInlineUnit.responses.put(function, new ArrayList<>());
        }
        FunctionInlineUnit.inlineFunctionsList = new ArrayList<>();
    }

    /**
     * inlineAnalysis 方法用于进行函数内联分析
     * 当前函数是否可以被内联，评价标准是内联函数不能递归，不能调用其他函数
     * 函数内联分析主要分为以下几个步骤：
     * 1.建立函数调用图
     * 2.深度优先搜索函数调用图，判断是否有递归
     * 3.内联函数并更新函数调用图
     * 4.删除无用函数
     * 5.重复1-4步骤，直到不动点稳定
     */
    private static void inlineAnalysis() {
        FunctionInlineUnit.fixedPoint = true;
        while (FunctionInlineUnit.fixedPoint) {
            FunctionInlineUnit.fixedPoint = false;
            FunctionInlineUnit.buildFuncCallGraph();
            module.getFunctions().forEach(Function::dfsCaller);
            FunctionInlineUnit.inlineFunctionsList.forEach(Function::inlineFunction);
            FunctionInlineUnit.inlineFunctionsList.clear();
            FunctionInlineUnit.buildFuncCallGraph();
            FunctionInlineUnit.removeUselessFunction();
        }
    }

    /**
     * buildFuncCallGraph 方法用于建立函数调用图
     */
    private static void buildFuncCallGraph() {
        FunctionInlineUnit.clear();
        module.getFunctions().forEach(Function::buildFuncCallGraph);
    }

    /**
     * clear 方法用于清空函数调用图,便于重新构建
     */
    private static void clear() {
        FunctionInlineUnit.callers = new HashMap<>();
        FunctionInlineUnit.responses = new HashMap<>();
        for (Function function : module.getFunctions()) {
            FunctionInlineUnit.callers.put(function, new ArrayList<>());
            FunctionInlineUnit.responses.put(function, new ArrayList<>());
        }
    }

    /**
     * addInlineFunction 方法用于添加内联函数
     */
    public static void addInlineFunction(Function function) {
        FunctionInlineUnit.inlineFunctionsList.add(function);
    }

    /**
     * addCaller 方法用于添加调用者
     */
    public static void addCaller(Function caller, Function response) {
        FunctionInlineUnit.callers.get(caller).add(response);
    }

    public static ArrayList<Function> getCaller(Function caller) {
        return FunctionInlineUnit.callers.get(caller);
    }

    /**
     * addResponse 方法用于添加响应者
     */
    public static void addResponse(Function response, Function caller) {
        FunctionInlineUnit.responses.get(response).add(caller);
    }

    public static ArrayList<Function> getResponse(Function response) {
        return FunctionInlineUnit.responses.get(response);
    }

    public static boolean isInlineAble() {
        return isInlineAble;
    }

    public static void setInlineAble(boolean isInlineAble) {
        FunctionInlineUnit.isInlineAble = isInlineAble;
    }

    public static void setFixedPoint(boolean fixedPoint) {
        FunctionInlineUnit.fixedPoint = fixedPoint;
    }

    /**
     * hasRecursion 方法用于判断函数是否递归
     */
    public static boolean hasRecursion(Function function) {
        return FunctionInlineUnit.callers.get(function).contains(function);
    }

    /**
     * removeUselessFunction 方法用于删除无用函数
     */
    private static void removeUselessFunction() {
        module.getFunctions().removeIf(Function::removeUselessFunction);
    }

    /**
     * replaceFunctions 方法用于替换函数
     */
    public static void replaceFunctions(CallInstr callInstr) {
        Function response = callInstr.getTarget();
        BasicBlock basicBlock = callInstr.getBelongingBlock();
        Function function = basicBlock.getBelongingFunc();
        BasicBlock inlineBlock = new BasicBlock(IrNameController.getBlockName(function));
        function.getBasicBlocks().add(function.getBasicBlocks().indexOf(basicBlock), inlineBlock);
        FunctionInlineUnit.splitBlock(callInstr, basicBlock, inlineBlock);
        FunctionInlineUnit.cloneBlock(callInstr, basicBlock, inlineBlock, response);
    }

    /**
     * cloneBlock 方法用于克隆基本块
     */
    private static void cloneBlock(
            CallInstr callInstr, BasicBlock basicBlock, BasicBlock inlineBlock, Function response) {
        FunctionClone functionCloner = new FunctionClone();
        Function copyFunc = functionCloner.copyFunc(response);
        Function function = basicBlock.getBelongingFunc();
        for (int i = 0; i < copyFunc.getParams().size(); i++) {
            Param param = copyFunc.getParams().get(i);
            Param callParam = (Param) callInstr.getOperands().get(i + 1);
            if (callParam.getType().isInt32()) {
                param.replaceAllUse(callParam);
            } else {
                ArrayList<Use> users = new ArrayList<>(param.getUseDefChain());
                for (Use use : users) {
                    if (use.getUser() instanceof StoreInstr storeInstr &&
                            storeInstr.getOperands().get(1) instanceof AllocaInstr allocaInstr) {
                        allocaInstr.getBelongingBlock().getInstrArrayList().remove(allocaInstr);
                        for (Use other : allocaInstr.getUseDefChain()) {
                            if (other.getUser() instanceof Instr instr) {
                                if (instr instanceof LoadInstr loadInstr) {
                                    loadInstr.replaceAllUse(callParam);
                                }
                                instr.getBelongingBlock().getInstrArrayList().remove(instr);
                            }
                        }
                        allocaInstr.dropOperands();
                    } else {
                        param.replaceAllUse(callParam);
                    }
                }
            }
        }
        JumpInstr toFunc = new JumpInstr(copyFunc.getBasicBlocks().get(0));
        toFunc.getBelongingBlock().getInstrArrayList().remove(toFunc);
        basicBlock.addInstr(toFunc);
        ArrayList<RetInstr> retList = new ArrayList<>();
        for (BasicBlock block : copyFunc.getBasicBlocks()) {
            for (Instr instr : block.getInstrArrayList()) {
                if (instr instanceof RetInstr retInstr) {
                    retList.add(retInstr);
                }
            }
        }
        FunctionInlineUnit.dealWithRetValue(retList, inlineBlock, response.getReturnType());
        for (BasicBlock block : copyFunc.getBasicBlocks()) {
            block.getBelongingFunc().getBasicBlocks().remove(block);
            function.addBasicBlock(inlineBlock,
                    block.getBelongingFunc().getBasicBlocks().indexOf(block) - 1);
        }
        callInstr.dropOperands();
        callInstr.getBelongingBlock().getInstrArrayList().remove(callInstr);
        inlineBlock.reducePhi(true);

    }

    /**
     * dealWithRetValue 方法对内联的函数的返回值进行处理
     */
    private static void dealWithRetValue(
            ArrayList<RetInstr> retList, BasicBlock inlineBlock, IrType returnType) {
        if (returnType.isInt32()) {
            PhiInstr phiInstr = new PhiInstr(
                    IrNameController.getLocalVarName(inlineBlock.getBelongingFunc()),
                    inlineBlock.getBlockIndBasicBlock());
            for (RetInstr retInstr : retList) {
                phiInstr.getOperands().add(retInstr.getRetValue());
                phiInstr.getIndBasicBlock().add(retInstr.getBelongingBlock());
                retInstr.getBelongingBlock().getBlockOutBasicBlock().remove(inlineBlock);
                inlineBlock.getBlockIndBasicBlock().remove(retInstr.getBelongingBlock());
                JumpInstr jumpInstr = new JumpInstr(inlineBlock);
                retInstr.getBelongingBlock().insertInstr(
                        retInstr.getBelongingBlock().getInstrArrayList().indexOf(retInstr) - 1,
                        jumpInstr);
                retInstr.dropOperands();
                retInstr.getBelongingBlock().getInstrArrayList().remove(retInstr);
            }
        } else if (returnType.isVoid()) {
            for (RetInstr retInstr : retList) {
                retInstr.getBelongingBlock().getBlockOutBasicBlock().remove(inlineBlock);
                inlineBlock.getBlockIndBasicBlock().remove(retInstr.getBelongingBlock());
                JumpInstr jumpInstr = new JumpInstr(inlineBlock);
                retInstr.getBelongingBlock().insertInstr(
                        retInstr.getBelongingBlock().getInstrArrayList().indexOf(retInstr) - 1,
                        jumpInstr);
                retInstr.dropOperands();
                retInstr.getBelongingBlock().getInstrArrayList().remove(retInstr);
            }
        }
    }

    /**
     * splitBlock 方法用于分割基本块，这里是为了将 call 指令所在的块分割成两半
     * 在当前块（也就是 call 在的那个块）之后再建一个块，用于存放 call 之后的指令
     */
    private static void splitBlock(
            CallInstr callInstr, BasicBlock basicBlock, BasicBlock inlineBlock) {
        boolean backInst = false;
        for (Instr instr : basicBlock.getInstrArrayList()) {
            if (!backInst && instr.equals(callInstr)) {
                backInst = true;
                continue;
            }
            if (backInst) {
                basicBlock.getInstrArrayList().remove(instr);
                inlineBlock.addInstr(instr);
            }
        }
        for (BasicBlock outBasicBlock : basicBlock.getBlockOutBasicBlock()) {
            for (Instr instr : outBasicBlock.getInstrArrayList()) {
                if (instr instanceof PhiInstr phiInstr &&
                        phiInstr.getOperands().contains(basicBlock)) {
                    instr.getOperands().set(instr.getOperands().indexOf(basicBlock), inlineBlock);
                    inlineBlock.replaceUseDefChain(basicBlock, inlineBlock, instr);
                }
            }
        }
        inlineBlock.getBlockOutBasicBlock().addAll(basicBlock.getBlockOutBasicBlock());
        for (BasicBlock outBasicBlock : inlineBlock.getBlockOutBasicBlock()) {
            ArrayList<BasicBlock> indBlocks =
                    new ArrayList<>(outBasicBlock.getBlockIndBasicBlock());
            for (BasicBlock indBlock : indBlocks) {
                if (indBlock.equals(basicBlock)) {
                    outBasicBlock.getBlockIndBasicBlock().remove(basicBlock);
                    outBasicBlock.getBlockIndBasicBlock().add(inlineBlock);
                }
            }
        }
        basicBlock.getBlockOutBasicBlock().clear();
    }
}
