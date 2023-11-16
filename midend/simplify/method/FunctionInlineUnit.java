package midend.simplify.method;

import iostream.structure.DebugDetailController;
import midend.generation.utils.IrNameController;
import midend.generation.utils.IrType;
import midend.generation.value.Value;
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
        FunctionInlineUnit.reset();
        FunctionInlineUnit.inlineFunctionsList = new ArrayList<>();
    }

    /**
     * reset 方法用于重置函数调用图,便于重新构建
     */
    private static void reset() {
        FunctionInlineUnit.callers = new HashMap<>();
        FunctionInlineUnit.responses = new HashMap<>();
        for (Function function : module.getFunctions()) {
            FunctionInlineUnit.callers.put(function, new ArrayList<>());
            FunctionInlineUnit.responses.put(function, new ArrayList<>());
        }
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
        int iterations = 0;
        while (FunctionInlineUnit.fixedPoint) {
            iterations++;
            FunctionInlineUnit.fixedPoint = false;
            FunctionInlineUnit.buildFuncCallGraph();
            module.getFunctions().forEach(Function::dfsCaller);
            FunctionInlineUnit.inlineFunctionsList.forEach(Function::inlineFunction);
            FunctionInlineUnit.inlineFunctionsList.clear();
            FunctionInlineUnit.buildFuncCallGraph();
            FunctionInlineUnit.removeUselessFunction();
            DebugDetailController.printInlineFunctionResult(iterations);
        }
    }

    /**
     * buildFuncCallGraph 方法用于建立函数调用图
     */
    private static void buildFuncCallGraph() {
        FunctionInlineUnit.reset();
        module.getFunctions().forEach(Function::buildFuncCallGraph);
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
     * 辅助完成函数内联
     * 该函数的执行逻辑如下：
     * 1.将函数调用指令所在的基本块分割成两半
     * 2.将函数调用指令所在的基本块之后再建一个块，用于存放函数调用指令之后的指令
     * 3.克隆函数调用指令所在的基本块，将克隆的基本块插入到函数调用指令所在的基本块之后
     *
     * @param callInstr 函数调用指令
     */
    public static void replaceFunctions(CallInstr callInstr) {
        Function response = callInstr.getTarget();
        BasicBlock basicBlock = callInstr.getBelongingBlock();
        Function function = basicBlock.getBelongingFunc();
        BasicBlock inlineBlock = new BasicBlock(IrNameController.getBlockName(function));
        function.addBasicBlock(inlineBlock, function.getBasicBlocks().indexOf(basicBlock) + 1);
        FunctionInlineUnit.splitBlock(callInstr, basicBlock, inlineBlock);
        FunctionInlineUnit.cloneBlock(callInstr, basicBlock, inlineBlock, response);
    }

    /**
     * cloneBlock 方法用于克隆基本块
     * 该函数执行逻辑如下:
     * 1.克隆函数调用指令所在的函数
     * 2.将克隆的函数的形参替换成函数调用指令的实参
     * 3.让basicBlock跳入克隆的函数的入口块
     * 4.收集所有的RetInstr，对其进行处理，
     * 如果返回值是int32，则将所有的RetInstr的返回值替换成phi指令
     * 5.清除重新插入并删除call指令
     */
    private static void cloneBlock(
            CallInstr callInstr, BasicBlock basicBlock, BasicBlock inlineBlock, Function response) {
        Function function = basicBlock.getBelongingFunc();
        FunctionClone functionCloner = new FunctionClone(function);
        Function copyFunc = functionCloner.copyFunc(response);
        for (int i = 0; i < copyFunc.getParams().size(); i++) {
            Param param = copyFunc.getParams().get(i);
            Value callParam = callInstr.getOperands().get(i + 1);
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
        basicBlock.addInstr(toFunc);
        //toFunc.getBelongingBlock().getInstrArrayList().removeIf(instr -> instr.equals(toFunc));
        ArrayList<RetInstr> retList = new ArrayList<>();
        int cnt = 0;
        for (BasicBlock block : copyFunc.getBasicBlocks()) {
            for (Instr instr : block.getInstrArrayList()) {
                if (instr instanceof RetInstr retInstr) {
                    retList.add(retInstr);
                    cnt++;
                }
            }
        }
        FunctionInlineUnit.dealWithRetValue(
                callInstr, retList, inlineBlock, response.getReturnType(), cnt);
        Iterator<BasicBlock> iterator = copyFunc.getBasicBlocks().iterator();
        while (iterator.hasNext()) {
            BasicBlock block = iterator.next();
            function.addBasicBlock(block,
                    inlineBlock.getBelongingFunc().getBasicBlocks().indexOf(inlineBlock));
            iterator.remove();
        }
        callInstr.dropOperands();
        callInstr.getBelongingBlock().getInstrArrayList().remove(callInstr);
        inlineBlock.reducePhi(true);

    }

    /**
     * dealWithRetValue 方法对内联的函数的返回值进行处理
     * 如果返回值是int32，则将所有的RetInstr的返回值替换成phi指令
     * 如果返回值是void，则直接删除所有的RetInstr
     */
    private static void dealWithRetValue(
            CallInstr callInstr, ArrayList<RetInstr> retList,
            BasicBlock inlineBlock, IrType returnType, int cnt) {
        if (returnType.isInt32()) {
            PhiInstr phiInstr = new PhiInstr(
                    IrNameController.getLocalVarName(inlineBlock.getBelongingFunc()),
                    inlineBlock.getBlockIndBasicBlock(), cnt);
            inlineBlock.addInstr(phiInstr, 0);
            for (RetInstr retInstr : retList) {
                phiInstr.inlineReturnValue(retInstr.getRetValue(), retInstr.getBelongingBlock());
                retInstr.getBelongingBlock().getBlockOutBasicBlock().remove(inlineBlock);
                inlineBlock.getBlockIndBasicBlock().remove(retInstr.getBelongingBlock());
                JumpInstr jumpInstr = new JumpInstr(inlineBlock);
                retInstr.getBelongingBlock().insertInstr(
                        retInstr.getBelongingBlock().getInstrArrayList().indexOf(retInstr),
                        jumpInstr);
                retInstr.dropOperands();
                retInstr.getBelongingBlock().getInstrArrayList().remove(retInstr);
            }
            callInstr.replaceAllUse(phiInstr);
        } else if (returnType.isVoid()) {
            for (RetInstr retInstr : retList) {
                retInstr.getBelongingBlock().getBlockOutBasicBlock().remove(inlineBlock);
                inlineBlock.getBlockIndBasicBlock().remove(retInstr.getBelongingBlock());
                JumpInstr jumpInstr = new JumpInstr(inlineBlock);
                retInstr.getBelongingBlock().insertInstr(
                        retInstr.getBelongingBlock().getInstrArrayList().indexOf(retInstr),
                        jumpInstr);
                retInstr.dropOperands();
                retInstr.getBelongingBlock().getInstrArrayList().remove(retInstr);
            }
        }
    }

    /**
     * splitBlock 方法用于分割基本块，这里是为了将 call 指令所在的块分割成两半
     * 在当前块（也就是 call 在的那个块）之后再建一个块，用于存放 call 之后的指令
     * 该函数的执行逻辑如下:
     * 1.遍历当前块的指令，找到 call 指令
     * 2.将 call 指令之后的指令从当前块中删除，并添加到新建的块中
     * 3.遍历当前块的后继块，找到其中的 phi 指令，并将其中的操作数中的当前块替换成新建的块
     * 4.将新建的块的后继块添加到当前块的后继块中，并将当前块从新建的块的前驱块中删除
     * 5.将当前块的后继块清空
     */
    private static void splitBlock(
            CallInstr callInstr, BasicBlock basicBlock, BasicBlock inlineBlock) {
        boolean backInst = false;
        Iterator<Instr> iterator = basicBlock.getInstrArrayList().iterator();
        while (iterator.hasNext()) {
            Instr instr = iterator.next();
            if (!backInst && instr.equals(callInstr)) {
                backInst = true;
                continue;
            }
            if (backInst) {
                iterator.remove();
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
