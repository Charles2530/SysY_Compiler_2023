package midend.simplify.controller;

import iostream.structure.DebugDetailController;
import midend.generation.value.construction.Module;
import midend.generation.value.construction.user.Function;
import midend.simplify.method.FunctionInlineUnit;

import java.util.HashMap;

/**
 * SideEffectAnalysisController 是副作用分析的控制器
 * 用于分析函数的副作用
 */
public class SideEffectAnalysisController {
    /**
     * module 是LLVM IR生成的顶级模块
     * functionProcessedHashMap 是函数处理哈希表,表示需要被处理的 func
     * functionVisitedHashMap 是函数访问哈希表,表示已经被访问的 func
     */
    private static Module module;
    private static HashMap<Function, Boolean> functionProcessedHashMap;
    private static HashMap<Function, Boolean> functionVisitedHashMap;

    /**
     * analysis 方法用于分析函数的副作用,是副作用分析的主函数
     */
    public static void analysis(Module module) {
        SideEffectAnalysisController.module = module;
        SideEffectAnalysisController.init();
        SideEffectAnalysisController.buildCallGraph(module);
        Function mainFunction = module.getFunctions().get(module.getFunctions().size() - 1);
        SideEffectAnalysisController.processAnalysis(mainFunction);
        mainFunction.setSideEffect(true);
        DebugDetailController.printSideEffectAnalysis(module);
    }

    /**
     * processAnalysis 方法用于处理函数的副作用
     */
    private static boolean processAnalysis(Function function) {
        boolean hasSideEffect = false;
        addFunctionVisited(function, false);
        if (getFunctionProcessed(function)) {
            hasSideEffect = function.getSideEffect();
            for (Function response : function.getResponses()) {
                if (!getFunctionVisited(response) && !getFunctionProcessed(response)) {
                    processAnalysis(response);
                }
            }
        } else {
            for (Function response : function.getResponses()) {
                if (response.getSideEffect() ||
                        (!getFunctionVisited(response) && !getFunctionProcessed(response)
                                && processAnalysis(response))) {
                    hasSideEffect = true;
                }
            }
        }
        addFunctionVisited(function, false);
        function.setSideEffect(hasSideEffect);
        addFunctionProcessed(function, true);
        return hasSideEffect;
    }

    /**
     * buildCallGraph 方法用于构建函数调用图
     */
    private static void buildCallGraph(Module module) {
        module.getFunctions().forEach(Function::sideEffectBuildGraph);
    }

    /**
     * init 方法用于初始化副作用处理模块
     */
    private static void init() {
        SideEffectAnalysisController.functionProcessedHashMap = new HashMap<>();
        SideEffectAnalysisController.functionVisitedHashMap = new HashMap<>();
        FunctionInlineUnit.buildFuncCallGraph();
        module.getFunctions().forEach(Function::initResponse);
    }

    public static void addFunctionProcessed(Function function, Boolean bool) {
        SideEffectAnalysisController.functionProcessedHashMap.put(function, bool);
    }

    public static void addFunctionVisited(Function function, Boolean bool) {
        SideEffectAnalysisController.functionVisitedHashMap.put(function, bool);
    }

    public static boolean getFunctionProcessed(Function function) {
        return SideEffectAnalysisController.functionProcessedHashMap.get(function);
    }

    public static boolean getFunctionVisited(Function function) {
        return SideEffectAnalysisController.functionVisitedHashMap.get(function);
    }
}
