package midend.generation.utils;

import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.FormatString;
import midend.generation.value.construction.Module;
import midend.generation.value.construction.Param;
import midend.generation.value.construction.procedure.Loop;
import midend.generation.value.construction.user.Function;
import midend.generation.value.construction.user.GlobalVar;
import midend.generation.value.construction.user.Instr;

import java.util.HashMap;
import java.util.Stack;

public class IrNameController {
    private static HashMap<Function, Integer> blockNameIndexHashMap;
    private static Integer paramNameIndex;
    private static Integer stringLiteralNameIndex;
    private static HashMap<Function, Integer> localVarNameIndexHashMap;
    private static Module currentModule;
    private static BasicBlock currentBasicBlock;
    private static Function currentFunction;
    private static Stack<Loop> loopProcedure;

    public static void init(Module module) {
        cntInit();
        IrNameController.currentModule = module;
        IrNameController.currentBasicBlock = null;
        IrNameController.currentFunction = null;
        IrNameController.loopProcedure = new Stack<>();
    }

    private static void cntInit() {
        IrNameController.blockNameIndexHashMap = new HashMap<>();
        IrNameController.paramNameIndex = 0;
        IrNameController.stringLiteralNameIndex = 0;
        IrNameController.localVarNameIndexHashMap = new HashMap<>();
    }

    public static String getLocalVarName(Function... function) {
        Function presentFunction = function.length == 0 ? currentFunction : function[0];
        int localVarNameIndex = localVarNameIndexHashMap.get(presentFunction);
        localVarNameIndexHashMap.put(presentFunction, localVarNameIndex + 1);
        return IrPrefix.LOCAL_VAR_NAME.toString() + localVarNameIndex;
    }

    public static String getBlockName() {
        int blockNameIndex = blockNameIndexHashMap.get(currentFunction);
        blockNameIndexHashMap.put(currentFunction, blockNameIndex + 1);
        String funcName = currentFunction.getName().equals("@main") ? "main" :
                currentFunction.getName().substring(3);
        return funcName + "_" + IrPrefix.BB_NAME + blockNameIndex;
    }

    public static String getGlobalVarName(String varName) {
        return IrPrefix.GLOBAL_VAR_NAME + varName;
    }

    public static String getParamName() {
        return IrPrefix.PARAM_NAME.toString() + paramNameIndex++;
    }

    public static String getStringLiteralName() {
        return IrPrefix.STRING_LITERAL_NAME.toString() + stringLiteralNameIndex++;
    }

    public static String getFuncName(String funcName) {
        return (funcName.equals("main")) ? "@main" : IrPrefix.FUNC_NAME + funcName;
    }

    public static void setCurrentFunc(Function function) {
        IrNameController.currentFunction = function;
        localVarNameIndexHashMap.put(function, 0);
        blockNameIndexHashMap.put(function, 0);
    }

    public static Function getCurrentFunc() {
        return currentFunction;
    }

    public static void setCurrentBlock(BasicBlock basicBlock) {
        IrNameController.currentBasicBlock = basicBlock;
    }

    public static BasicBlock getCurrentBlock() {
        return currentBasicBlock;
    }

    public static Loop getCurrentLoop() {
        return loopProcedure.peek();
    }

    public static void pushLoop(Loop loop) {
        loopProcedure.push(loop);
    }

    public static void popLoop() {
        loopProcedure.pop();
    }

    public static void addGlobalVar(GlobalVar globalVar) {
        currentModule.addGlobalVar(globalVar);
    }

    public static void addFunction(Function function) {
        currentModule.addFunction(function);
    }

    public static void addBasicBlock(BasicBlock basicBlock) {
        currentFunction.addBasicBlock(basicBlock);
        basicBlock.setBelongingFunc(currentFunction);
    }

    public static void addParam(Param param) {
        currentFunction.addParam(param);
        param.setBelongingFunc(currentFunction);
    }

    public static void addInstr(Instr instr) {
        currentBasicBlock.addInstr(instr);
        instr.setBelongingBlock(currentBasicBlock);
    }

    public static void addFormatString(FormatString formatString) {
        currentModule.addStringLiteral(formatString);
    }
}
