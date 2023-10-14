package midend.generation.value.construction.user;

import midend.generation.utils.IrNameController;
import midend.generation.utils.IrType;
import midend.generation.utils.irtype.StructType;
import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.Param;
import midend.generation.value.construction.User;

import java.util.ArrayList;

public class Function extends User {
    private final IrType returnType;
    private final ArrayList<BasicBlock> basicBlocks;
    private final ArrayList<Param> params;

    public Function(String name, IrType returnType) {
        super(new StructType("function"), name);
        this.returnType = returnType;
        this.basicBlocks = new ArrayList<>();
        this.params = new ArrayList<>();
        IrNameController.addFunction(this);
    }

    public IrType getReturnType() {
        return returnType;
    }

    public void addBasicBlock(BasicBlock basicBlock) {
        basicBlocks.add(basicBlock);
    }

    public void addParam(Param param) {
        params.add(param);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("define dso_local ").append(returnType.toString())
                .append(" ").append(name).append("(");
        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).toString());
            if (i != params.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(") {\n");
        for (BasicBlock basicBlock : basicBlocks) {
            sb.append(basicBlock.toString()).append("\n");
        }
        sb.append("}");
        return sb.toString();
    }
}
