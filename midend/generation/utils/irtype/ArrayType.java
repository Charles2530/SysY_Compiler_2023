package midend.generation.utils.irtype;

import midend.generation.utils.IrType;

import java.util.ArrayList;

/**
 * ArrayType 是 LLVM IR 中的数组类型，
 * 主要用于生成数组类型
 * 继承自IrType
 */
public class ArrayType extends IrType {
    /**
     * eleSpace 是该 ArrayType 的元素空间维度信息，记录了每一维的元素数量
     * eleType 是该 ArrayType 的中存储的元素类型
     */
    private final ArrayList<Integer> eleSpace;
    private final IrType eleType;

    public ArrayType(ArrayList<Integer> eleSpace, IrType eleType) {
        this.eleSpace = eleSpace;
        this.eleType = eleType;
    }

    /**
     * calcSpaceTot 是该 ArrayType 的元素空间维度信息的计算函数
     */
    public Integer calcSpaceTot() {
        Integer tot = 1;
        for (Integer i : eleSpace) {
            tot *= i;
        }
        return tot;
    }

    public ArrayList<Integer> getEleSpace() {
        return eleSpace;
    }

    @Override
    public String toString() {
        return (eleSpace.size() == 1) ? "[" + eleSpace.get(0) + " x " + eleType + "]"
                : "[" + eleSpace.get(0) + " x " + "[" + eleSpace.get(1) + " x " + eleType + "]]";
    }
}
