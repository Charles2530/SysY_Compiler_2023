package backend.generation.utils;

import backend.generation.mips.Register;
import midend.generation.value.Value;

import java.util.HashMap;

/**
 * RegisterController 是一个寄存器控制器
 * 主要用于寄存器的分配
 */
public class RegisterController {
    /**
     * valueRegisterHashMap 是一个value和寄存器的映射
     */
    private static HashMap<Value, Register> valueRegisterHashMap;

    public RegisterController() {
        RegisterController.valueRegisterHashMap = null;
    }

    /**
     * getRegister 方法用于获取value对应的寄存器
     */
    public Register getRegister(Value value) {
        return (valueRegisterHashMap == null) ? null :
                valueRegisterHashMap.get(value);
    }

    /**
     * allocRegister 方法用于分配寄存器
     */
    public void allocRegister(Value value, Register register) {
        if (valueRegisterHashMap == null) {
            return;
        }
        valueRegisterHashMap.put(value, register);
    }

    public void setValueRegisterHashMap(HashMap<Value, Register> valueRegisterHashMap) {
        RegisterController.valueRegisterHashMap = valueRegisterHashMap;
    }

    public HashMap<Value, Register> getRegisterHashMap() {
        return valueRegisterHashMap;
    }
}
