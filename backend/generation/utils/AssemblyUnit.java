package backend.generation.utils;

import backend.generation.mips.Register;
import iostream.structure.ErrorController;
import midend.generation.value.Value;
import midend.generation.value.construction.user.Function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * AssemblyUnit 是一个汇编单元
 * 主要用于汇编代码的生成，控制了整个转汇编的过程
 */
public class AssemblyUnit {
    /**
     * currentOffset 是当前的栈指针偏移量
     * registerController 是寄存器控制器
     * stackOffsetHashMap 是一个value和偏移量的映射
     */
    private static Integer currentOffset;
    private static RegisterController registerController;
    private static HashMap<Value, Integer> stackOffsetHashMap;

    /**
     * init 方法用于初始化汇编单元
     */
    public void init() {
        AssemblyUnit.currentOffset = 0;
        AssemblyUnit.registerController = new RegisterController();
    }

    /**
     * moveCurrentOffset 方法用于移动当前的栈指针偏移量
     */
    public static void moveCurrentOffset(int offset) {
        AssemblyUnit.currentOffset += offset;
        if (AssemblyUnit.currentOffset > 0) {
            ErrorController.printStackOverflowError();
        }
    }

    /**
     * resetFunctionConfig 方法用于重置函数的配置
     */
    public static void resetFunctionConfig(Function function) {
        AssemblyUnit.currentOffset = 0;
        AssemblyUnit.stackOffsetHashMap = new HashMap<>();
        registerController.setValueRegisterHashMap(function.getRegisterHashMap());
    }

    public static RegisterController getRegisterController() {
        return registerController;
    }

    public static Integer getCurrentOffset() {
        return currentOffset;
    }

    public static void addOffset(Value value, int offset) {
        stackOffsetHashMap.put(value, offset);
    }

    public static Integer getOffset(Value value) {
        return stackOffsetHashMap.get(value);
    }

    public static ArrayList<Register> getAllocatedRegister() {
        return (registerController.getRegisterHashMap() == null) ? new ArrayList<>() :
                new ArrayList<>(new HashSet<>(registerController.getRegisterHashMap().values()));
    }
}
