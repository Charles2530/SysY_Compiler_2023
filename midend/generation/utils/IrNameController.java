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

/**
 * IrNameController 是 LLVM IR 中的命名控制器，
 * 主要用于生成命名
 */
public class IrNameController {
    /**
     * blockNameIndexHashMap 是该 IrNameController 的块名索引哈希表
     * paramNameIndex 是该 IrNameController 的参数名索引
     * stringLiteralNameIndex 是该 IrNameController 的字符串字面量名索引
     * localVarNameIndexHashMap 是该 IrNameController 的局部变量名索引哈希表
     */
    private static HashMap<Function, Integer> blockNameIndexHashMap;
    private static Integer paramNameIndex;
    private static Integer stringLiteralNameIndex;
    private static HashMap<Function, Integer> localVarNameIndexHashMap;
    /**
     * currentModule 是该 IrNameController 的当前模块
     * currentBasicBlock 是该 IrNameController 的当前基本块
     * currentFunction 是该 IrNameController 的当前函数
     * loopProcedure 是该 IrNameController 的循环过程,
     * 采用一个Stack来存储，每次进入循环时push，退出循环时pop
     */
    private static Module currentModule;
    private static BasicBlock currentBasicBlock;
    private static Function currentFunction;
    private static Stack<Loop> loopProcedure;

    /**
     * 初始化命名控制器
     */
    public static void init(Module module) {
        cntInit();
        IrNameController.currentModule = module;
        IrNameController.currentBasicBlock = null;
        IrNameController.currentFunction = null;
        IrNameController.loopProcedure = new Stack<>();
    }

    /**
     * cntInit 是该 IrNameController 的计数器初始化函数
     * 该函数用于初始化该 IrNameController 的计数器
     */
    private static void cntInit() {
        IrNameController.blockNameIndexHashMap = new HashMap<>();
        IrNameController.paramNameIndex = 0;
        IrNameController.stringLiteralNameIndex = 0;
        IrNameController.localVarNameIndexHashMap = new HashMap<>();
    }

    /**
     * getLocalVarName 方法用于获取局部变量名
     *
     * @param function 是该局部变量所属的函数，可缺省
     *                 如果缺省，则默认为当前函数
     */
    public static String getLocalVarName(Function... function) {
        Function presentFunction = ((function.length == 0) ? currentFunction : function[0]);
        int localVarNameIndex = localVarNameIndexHashMap.get(presentFunction);
        localVarNameIndexHashMap.put(presentFunction, localVarNameIndex + 1);
        return IrPrefix.LOCAL_VAR_NAME.toString() + localVarNameIndex;
    }

    /**
     * getBlockName 方法用于获取块名
     *
     * @param function 是该块所属的函数，可缺省
     *                 如果缺省，则默认为当前函数
     */
    public static String getBlockName(Function... function) {
        Function presentFunction = ((function.length == 0) ? currentFunction : function[0]);
        blockNameIndexHashMap.computeIfAbsent(presentFunction, k -> 0);
        int blockNameIndex = blockNameIndexHashMap.get(presentFunction);
        blockNameIndexHashMap.put(presentFunction, blockNameIndex + 1);
        String funcName = presentFunction.getName().equals("@main") ? "main" :
                presentFunction.getName().substring(3);
        return funcName + "_" + IrPrefix.BB_NAME + blockNameIndex;
    }

    /**
     * getGlobalVarName 方法用于获取全局变量名
     */
    public static String getGlobalVarName(String varName) {
        return IrPrefix.GLOBAL_VAR_NAME + varName;
    }

    /**
     * getParamName 方法用于获取参数名
     */
    public static String getParamName() {
        return IrPrefix.PARAM_NAME.toString() + paramNameIndex++;
    }

    /**
     * getStringLiteralName 方法用于获取字符串字面量名
     */
    public static String getStringLiteralName() {
        return IrPrefix.STRING_LITERAL_NAME.toString() + stringLiteralNameIndex++;
    }

    /**
     * getFuncName 方法用于获取函数名
     *
     * @param funcName 函数名
     *                 如果是 main 函数，则返回 @main
     *                 否则返回 @f_funcName
     */
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
    }

    public static void addParam(Param param) {
        currentFunction.addParam(param);
    }

    public static void addInstr(Instr instr) {
        currentBasicBlock.addInstr(instr);
    }

    public static void addFormatString(FormatString formatString) {
        currentModule.addStringLiteral(formatString);
    }
}
