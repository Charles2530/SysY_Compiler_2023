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
     * 该函数的执行逻辑如下:
     * 1. 如果该函数已经被访问过,则直接返回该函数的副作用
     * 2. 如果该函数没有被访问过,则将该函数标记为已访问
     * 3. 之后我们需要看该函数调用的所有函数，如果它调用的
     * 函数本身存在副作用或者它调用的函数没有被访问过且
     * 该函数调用的函数存在副作用，则该函数存在副作用
     */
    private static boolean processAnalysis(Function function) {
        boolean sideEffect = false;
        addFunctionVisited(function, true);
        if (getFunctionProcessed(function)) {
            sideEffect = function.getSideEffect();
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
                    sideEffect = true;
                }
            }
        }
        addFunctionVisited(function, false);
        function.setSideEffect(sideEffect);
        addFunctionProcessed(function, true);
        return sideEffect;
    }

    /**
     * buildCallGraph 方法用于构建函数调用图
     */
    private static void buildCallGraph(Module module) {
        module.getFunctions().forEach(Function::sideEffectBuildGraph);
    }

    /**
     * init 方法用于初始化副作用处理模块
     * 该函数借用了在函数内联中的函数调用图创建函数
     * 之后将其填入每个function的responses中以完成初始化
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
