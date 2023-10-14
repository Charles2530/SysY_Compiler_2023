package generation.value.construction;

import generation.utils.IrNameController;
import generation.utils.irtype.ArrayType;
import generation.utils.irtype.PointerType;
import generation.utils.irtype.VarType;
import generation.value.Value;

public class FormatString extends Value {
    private String string;

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
