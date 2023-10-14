package generation.value.construction.procedure;

import generation.utils.IrType;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Initial {
    private IrType type;
    private ArrayList<Integer> initValue;

    public Initial(IrType type, ArrayList<Integer> initValue) {
        this.type = type;
        this.initValue = initValue;
        if (initValue == null) {
            this.initValue = new ArrayList<>();
        }
    }

    public IrType getType() {
        return type;
    }

    @Override
    public String toString() {
        if (initValue == null) {
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
