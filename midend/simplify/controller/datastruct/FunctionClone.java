package midend.simplify.controller.datastruct;

import midend.generation.value.Value;
import midend.generation.value.construction.user.Function;

import java.util.HashMap;

public class FunctionClone {
    private HashMap<Value, Value> copyMap;

    public Function copyFunc(Function response) {
        initial();
        return null;
    }

    private void initial() {
        copyMap = new HashMap<>();
    }
}
