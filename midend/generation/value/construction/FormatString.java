package midend.generation.value.construction;

import backend.generation.mips.asm.datasegment.AsciizAsm;
import iostream.structure.OptimizerUnit;
import midend.generation.utils.IrNameController;
import midend.generation.utils.irtype.ArrayType;
import midend.generation.utils.irtype.PointerType;
import midend.generation.utils.irtype.VarType;
import midend.generation.value.Value;

import java.util.ArrayList;

/**
 * FormatString 是 LLVM IR 中的字符串常量成分，
 * 继承于Value，主要用于生成字符串常量
 */
public class FormatString extends Value {
    /**
     * string 是该 FormatString 的源字符串
     */
    private final String string;

    public FormatString(String name, String string, ArrayList<Integer> arrayList) {
        super(new PointerType(new ArrayType(arrayList, new VarType(8))), name);
        this.string = string;
        if (!OptimizerUnit.isOptimizer()) {
            IrNameController.addFormatString(this);
        }
    }

    /**
     * getPointer 方法用于获取指向该 FormatString 的指针
     */
    public PointerType getPointer() {
        return (PointerType) type;
    }

    public String getString() {
        return string;
    }

    @Override
    public String toString() {
        return name + " = constant " + ((PointerType) type).getTarget() +
                " c\"" + string + "\\00\"";
    }

    @Override
    public void generateAssembly() {
        new AsciizAsm(name.substring(1), string);
    }
}
