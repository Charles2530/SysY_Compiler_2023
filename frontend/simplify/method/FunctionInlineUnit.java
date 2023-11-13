package frontend.simplify.method;

import midend.generation.value.construction.Module;
import midend.generation.value.construction.user.Function;
import midend.generation.value.instr.basis.CallInstr;

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
    }
}
