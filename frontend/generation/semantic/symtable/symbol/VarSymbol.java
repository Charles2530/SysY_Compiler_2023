package frontend.generation.semantic.symtable.symbol;

import frontend.generation.semantic.symtable.Symbol;
import midend.generation.utils.IrType;
import midend.generation.utils.irtype.ArrayType;
import midend.generation.utils.irtype.VarType;
import midend.generation.value.Value;
import midend.generation.value.construction.procedure.Initial;

import java.util.ArrayList;

/**
 * VarSymbol 是符号表中的数值表项,继承于Symbol
 */
public class VarSymbol extends Symbol {
    /**
     * dim 是变量的维数
     * initValue 是变量的初始化值
     * space 是变量的维度信息,存储了每一维的大小
     * spaceTot 是变量的总大小(如果是数组则为数组的总大小)
     * value 主要用于生成LLVM IR代码时该符号表项对应的Value表示
     * totalOffset 是由于存在变量初始值导致变量的总偏移量，
     * 也可以理解为当前初始化数组中存在的元素个数
     */
    protected final int dim;
    protected final ArrayList<Integer> initValue;
    protected final ArrayList<Integer> space;
    protected Integer spaceTot;

    protected Value value;
    protected Integer totalOffset;

    public VarSymbol(String symbolName, SymType symbolType, int dim,
                     ArrayList<Integer> initValue, ArrayList<Integer> space) {
        super(symbolName, symbolType);
        this.dim = dim;
        this.initValue = (initValue == null) ? new ArrayList<>() : initValue;
        this.space = space;
        this.spaceTot = 1;
        this.totalOffset = (initValue == null) ? 0 : initValue.size();
        // array init
        if (dim > 0) {
            int size = 1;
            for (int i = 0; i < dim; i++) {
                size *= space.get(i);
            }
            this.spaceTot = size;
            int res = size - totalOffset;
            for (int i = 0; i < res; i++) {
                this.initValue.add(0);
            }
        }
    }

    public Integer getDim() {
        return dim;
    }

    /**
     * getConstValue 是用于获取变量的初始化值的函数,由于变量的初始化值
     * 可能是一个数组,故需要传入一个idx数组来表示当前所需要的初始化值,
     * 并用idx的各维度信息来计算出对应的初始化值
     */
    public Integer getConstValue(int... idx) {
        if (initValue == null || initValue.isEmpty()) {
            return 0;
        }
        if (idx.length == 0) {
            return initValue.get(0);
        } else if (idx.length == 1) {
            return initValue.get(idx[0]);
        } else {
            if (idx[0] == 0) {
                return initValue.get(idx[1]);
            }
            return initValue.get(idx[0] * space.get(0) + idx[1]);
        }
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public ArrayList<Integer> getSpace() {
        return space;
    }

    /**
     * 用于生成LLVM IR中的Initial成分
     */
    public Initial getInitial() {
        IrType type = (dim == 0) ? new VarType(32) : new ArrayType(space, new VarType(32));
        return new Initial(type, initValue, space, totalOffset);
    }
}
