package midend.generation.value.construction;

import midend.generation.utils.IrNameController;
import midend.generation.utils.irtype.ArrayType;
import midend.generation.utils.irtype.PointerType;
import midend.generation.utils.irtype.VarType;
import midend.generation.value.Value;

public class FormatString extends Value {
    private final String string;

    public FormatString(String name, String string) {
        super(new PointerType(new ArrayType(string.length() + 1, new VarType(8))), name);
        this.string = string;
        IrNameController.addFormatString(this);
    }

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
}