package backend.utils;

import backend.mips.Register;
import midend.generation.value.Value;

import java.util.HashMap;

public class RegisterController {
    private static HashMap<Value, Register> valueRegisterHashMap;

    public RegisterController() {
        RegisterController.valueRegisterHashMap = null;
    }

    public Register getRegister(Value value) {
        return (valueRegisterHashMap == null) ? null :
                valueRegisterHashMap.get(value);
    }

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
