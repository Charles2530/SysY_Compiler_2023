package midend.generation.value.construction.procedure;

import midend.generation.utils.IrType;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Initial {
    private final IrType type;
    private ArrayList<Integer> initValue;

    public Initial(IrType type, ArrayList<Integer> initValue) {
        this.type = type;
        this.initValue = initValue;
    }

    public IrType getType() {
        return type;
    }

    @Override
    public String toString() {
        if (initValue == null || initValue.isEmpty()) {
            return (type.isInt32()) ? type + " 0" : type + " zeroinitializer";
        } else {
            return (type.isInt32()) ? type + " " + initValue.get(0) :
                    type + " [" + initValue.stream().map(number -> "i32 " + number)
                            .collect(Collectors.joining(", ")) + "]";
        }
    }

    public ArrayList<Integer> getInitValue() {
        return initValue;
    }
}
