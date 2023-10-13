package generation.utils;

import generation.utils.irtype.ArrayType;
import generation.utils.irtype.PointerType;
import generation.utils.irtype.VarType;
import generation.value.construction.Module;
import generation.value.construction.BasicBlock;
import generation.value.construction.procedure.Loop;
import generation.value.construction.user.Function;

import java.util.HashMap;
import java.util.Stack;

public class IrNameController {
    private static HashMap<IrPrefix, String> irPrefixHashMap;
    private static Integer blockNameIndex;
    private static Integer paramNameIndex;
    private static Integer stringLiteralNameIndex;

    private static HashMap<Function, Integer> localVarNameIndexHashMap;

    private static Module currentModule;
    private static BasicBlock currentBasicBlock;
    private static Function currentFunction;
    private static Stack<Loop> loopProcedure;

    public static void init() {
        preFixInit();
        cntInit();
        IrNameController.currentModule = new Module();
        IrNameController.currentBasicBlock = null;
        IrNameController.currentFunction = null;
        IrNameController.loopProcedure = new Stack<>();
    }

    private static void cntInit() {
        IrNameController.blockNameIndex = 0;
        IrNameController.paramNameIndex = 0;
        IrNameController.stringLiteralNameIndex = 0;
        IrNameController.localVarNameIndexHashMap = new HashMap<>();
    }

    private static void preFixInit() {
        IrNameController.irPrefixHashMap = new HashMap<>();
        IrNameController.irPrefixHashMap.put(IrPrefix.BB_NAME, "block_label_");
        IrNameController.irPrefixHashMap.put(IrPrefix.FUNC_NAME, "@f_");
        IrNameController.irPrefixHashMap.put(IrPrefix.GLOBAL_VAR_NAME, "@");
        IrNameController.irPrefixHashMap.put(IrPrefix.LOCAL_VAR_NAME, "%v");
        IrNameController.irPrefixHashMap.put(IrPrefix.PARAM_NAME, "%a");
        IrNameController.irPrefixHashMap.put(IrPrefix.STRING_LITERAL_NAME, "@s");
    }

    public static String getPrefix(IrPrefix prefix) {
        return irPrefixHashMap.get(prefix);
    }

    public static String getLocalVarName() {
        int localVarNameIndex = localVarNameIndexHashMap.get(currentFunction);
        localVarNameIndexHashMap.put(currentFunction, localVarNameIndex + 1);
        return IrNameController.getPrefix(IrPrefix.LOCAL_VAR_NAME) + localVarNameIndex;
    }

    public static String getBlockName() {
        return IrNameController.getPrefix(IrPrefix.BB_NAME) + blockNameIndex++;
    }

    public static String getGlobalVarName(String varName) {
        return IrNameController.getPrefix(IrPrefix.GLOBAL_VAR_NAME) + varName;
    }

    public static String getParamName() {
        return IrNameController.getPrefix(IrPrefix.PARAM_NAME) + paramNameIndex++;
    }

    public static String getStringLiteralName() {
        return IrNameController.getPrefix(IrPrefix.STRING_LITERAL_NAME) + stringLiteralNameIndex++;
    }

    public static String getFuncName(String funcName) {
        if (funcName.equals("main")) {
            return "@main";
        }
        return IrNameController.getPrefix(IrPrefix.FUNC_NAME) + funcName;
    }

    public static void setCurrentFunc(Function function) {
        IrNameController.currentFunction = function;
        localVarNameIndexHashMap.put(function, 0);
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

    public static String getStringLiteral(String name, String content) {
        PointerType type = new PointerType(new ArrayType(content.length() + 1, new VarType(8)));
        return name + " = constant " + type.getTarget() + " c\"" + content + "\\00\"";
    }
}
