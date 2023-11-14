package midend.generation.value.construction.procedure;

import midend.generation.utils.IrType;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Initial 是 LLVM IR 中的初始化器成分，
 * 主要用于生成初始化器
 */
public class Initial {
    /**
     * type 是该 Initial 的类型
     * initValue 是该 Initial 的初始值
     * space 是该 Initial 的空间维度信息
     * offset 是该 Initial 的偏移量，对应存在的初始值的数量
     */
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

    public Integer getOffset() {
        return offset;
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
                    return getDoubleDimArrayInitial();
                }
            }
        }
    }

    /**
     * getDoubleDimArrayInitial 方法用于生成二维数组的字符串形式
     * 是从toString中分离出来的专门处理二维数组的方法
     */
    private String getDoubleDimArrayInitial() {
        StringBuilder sb = new StringBuilder();
        if (offset == 0) {
            sb.append(type).append(" zeroinitializer");
        } else {
            sb.append(type).append(" [");
            for (int i = 0; i < space.get(0); i++) {
                if (i * space.get(1) < offset) {
                    sb.append("[").append(space.get(1)).append(" x i32]").append("[");
                    for (int j = 0; j < space.get(1); j++) {
                        sb.append("i32 ").append(initValue.get(i * space.get(1) + j));
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
