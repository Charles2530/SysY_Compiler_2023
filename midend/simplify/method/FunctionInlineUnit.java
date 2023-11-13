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

public class FunctionInlineUnit {
    private static Module module;
    private static boolean fixedPoint;
    private static HashMap<Function, ArrayList<Function>> callers;
    private static HashMap<Function, ArrayList<Function>> responses;
    private static ArrayList<Function> inlineFunctionsList;
    private static boolean isInlineAble;

    public static void run(Module module) {
        FunctionInlineUnit.module = module;
        FunctionInlineUnit.init();
        FunctionInlineUnit.inlineAnalysis();
    }

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

    private static void buildFuncCallGraph() {
        FunctionInlineUnit.callers.clear();
        FunctionInlineUnit.responses.clear();
        module.getFunctions().forEach(Function::buildFuncCallGraph);
    }

    public static void addInlineFunction(Function function) {
        FunctionInlineUnit.inlineFunctionsList.add(function);
    }

    public static void addCaller(Function caller, Function response) {
        FunctionInlineUnit.callers.get(caller).add(response);
    }

    public static ArrayList<Function> getCaller(Function caller) {
        return FunctionInlineUnit.callers.get(caller);
    }

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

    public static boolean hasRecursion(Function function) {
        return FunctionInlineUnit.callers.get(function).contains(function);
    }

    private static void removeUselessFunction() {
        module.getFunctions().forEach(Function::removeUselessFunction);
    }

    public static void replaceFunctions(CallInstr callInstr) {
        Function response = callInstr.getTarget();
        BasicBlock basicBlock = callInstr.getBelongingBlock();
        Function function = basicBlock.getBelongingFunc();
        BasicBlock inlineBlock = new BasicBlock(IrNameController.getBlockName(function));
        function.getBasicBlocks().add(function.getBasicBlocks().indexOf(basicBlock), inlineBlock);
        FunctionInlineUnit.splitBlock(callInstr, basicBlock, inlineBlock);
        FunctionInlineUnit.cloneBlock(callInstr, basicBlock, inlineBlock, response);
    }

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
        ArrayList<RetInstr> retInstrs = new ArrayList<>();
        int cnt = 0;
        for (BasicBlock block : copyFunc.getBasicBlocks()) {
            for (Instr instr : block.getInstrArrayList()) {
                if (instr instanceof RetInstr retInstr) {
                    retInstrs.add(retInstr);
                    cnt++;
                }
            }
        }
        FunctionInlineUnit.dealWithRetValue(retInstrs, cnt, inlineBlock, response.getReturnType());
        for (BasicBlock block : copyFunc.getBasicBlocks()) {
            block.getBelongingFunc().getBasicBlocks().remove(block);
            function.addBasicBlock(inlineBlock,
                    block.getBelongingFunc().getBasicBlocks().indexOf(block) - 1);
        }
        callInstr.dropOperands();
        callInstr.getBelongingBlock().getInstrArrayList().remove(callInstr);
        /*TODO:need change*/

    }
    /*TODO:partly finish*/

    private static void dealWithRetValue(
            ArrayList<RetInstr> retInstrs, int cnt, BasicBlock inlineBlock, IrType returnType) {
        if (returnType.isInt32()) {
            return;
        } else if (returnType.isVoid()) {
            return;
        }
    }

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
