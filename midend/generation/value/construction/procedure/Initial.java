package midend.generation.value.construction.procedure;

import midend.generation.utils.IrType;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Initial {
    private final IrType type;
    private final ArrayList<Integer> initValue;
    private final ArrayList<Integer> space;
    private final Integer offset;

    public Initial(IrType type, ArrayList<Integer> initValue,
                   ArrayList<Integer> space, Integer offset) {
        this.type = type;
        this.initValue = initValue;
        this.space = space;
        this.offset = offset;
    }

    public ArrayList<Integer> getInitValue() {
        return initValue;
    }

    public IrType getType() {
        return type;
    }

    @Override
    public String toString() {
        if (initValue == null || initValue.isEmpty()) {
            return (type.isInt32()) ? type + " 0" : type + " zeroinitializer";
        } else {
            if (type.isInt32()) {
                return type + " " + initValue.get(0);
            } else {
                if (space.size() == 1) {
                    return type + " [" + initValue.stream().map(number -> "i32 " + number)
                            .collect(Collectors.joining(", ")) + "]";
                } else {
                    StringBuilder sb = new StringBuilder();
                    if (offset == 0) {
                        sb.append(type).append(" zeroinitializer");
                    } else {
                        sb.append(type).append(" [");
                        for (int i = 0; i < space.get(0); i++) {
                            if (i * space.get(0) < offset) {
                                sb.append("[").append(space.get(1)).append(" x i32]").append("[");
                                for (int j = 0; j < space.get(1); j++) {
                                    sb.append("i32 ").append(initValue.get(i * space.get(0) + j));
                                    if (j != space.get(1) - 1) {
                                        sb.append(", ");
                                    }
                                }
                                sb.append("]");
                            } else {
                                sb.append("[").append(space.get(1)).
                                        append(" x i32] ").append("zeroinitializer");
                            }
                            if (i != space.get(0) - 1) {
                                sb.append(", ");
                            }
                        }
                        sb.append("]");
                    }
                    return sb.toString();
                }
            }
        }
    }
}
