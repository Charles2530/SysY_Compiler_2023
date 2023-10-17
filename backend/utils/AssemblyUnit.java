package backend.utils;

import backend.mips.Register;
import iostream.ErrorController;
import midend.generation.value.Value;
import midend.generation.value.construction.user.Function;

import java.util.ArrayList;
import java.util.HashMap;

public class AssemblyUnit {
    private static Function currentFunction;
    private static AssemblyData assemblyData;
    private static Integer currentOffset;
    private static RegisterController registerController;
    private static HashMap<Value, Integer> stackOffsetHashMap;

    public void init(AssemblyData assemblyData) {
        AssemblyUnit.assemblyData = assemblyData;
        AssemblyUnit.currentFunction = null;
        AssemblyUnit.currentOffset = 0;
        AssemblyUnit.registerController = new RegisterController();
    }

    public static void moveCurrentOffset(int offset) {
        AssemblyUnit.currentOffset += offset;
        if (AssemblyUnit.currentOffset < 0) {
            ErrorController.printStackOverflowError();
        }
    }

    public static void resetFunctionConfig(Function function) {
        AssemblyUnit.currentFunction = function;
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
        return (registerController == null) ? new ArrayList<>() :
                new ArrayList<>(registerController.getRegisterHashMap().values());
    }

}
