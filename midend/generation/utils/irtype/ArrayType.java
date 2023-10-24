package midend.generation.utils.irtype;

import midend.generation.utils.IrType;

import java.util.ArrayList;

public class ArrayType extends IrType {
    private final ArrayList<Integer> eleSpace;
    private final IrType eleType;

    public ArrayType(ArrayList<Integer> eleSpace, IrType eleType) {
        this.eleSpace = eleSpace;
        this.eleType = eleType;
    }

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
