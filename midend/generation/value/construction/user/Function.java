package midend.generation.value.construction.user;

import backend.mips.Register;
import backend.mips.asm.textsegment.structure.Label;
import backend.utils.AssemblyUnit;
import backend.utils.RegisterUtils;
import midend.generation.utils.IrNameController;
import midend.generation.utils.IrType;
import midend.generation.utils.irtype.StructType;
import midend.generation.value.Value;
import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.Param;
import midend.generation.value.construction.User;

import java.util.ArrayList;
import java.util.HashMap;

public class Function extends User {
    private final IrType returnType;
    private final ArrayList<BasicBlock> basicBlocks;
    private final ArrayList<Param> params;
    private final HashMap<Value, Register> registerHashMap;

    public Function(String name, IrType returnType) {
        super(new StructType("function"), name);
        this.returnType = returnType;
        this.basicBlocks = new ArrayList<>();
        this.params = new ArrayList<>();
        this.registerHashMap = null;
        IrNameController.addFunction(this);
    }

    public IrType getReturnType() {
        return returnType;
    }

    public void addBasicBlock(BasicBlock basicBlock) {
        basicBlocks.add(basicBlock);
    }

    public ArrayList<BasicBlock> getBasicBlocks() {
        return basicBlocks;
    }

    public void addParam(Param param) {
        params.add(param);
    }

    public HashMap<Value, Register> getRegisterHashMap() {
        return registerHashMap;
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
        sb.append(") {\n\n");
        for (int i = 0; i < basicBlocks.size(); i++) {
            sb.append(basicBlocks.get(i).toString());
            if (i != basicBlocks.size() - 1) {
                sb.append("\n\n");
            }
        }
        sb.append("\n}");
        return sb.toString();
    }

    @Override
    public void generateAssembly() {
        new Label(name.substring(1));
        AssemblyUnit.resetFunctionConfig(this);
        for (int i = 0; i < params.size(); i++) {
            if (i < 3) {
                AssemblyUnit.getRegisterController().allocRegister(params.get(i),
                        Register.regTransform(Register.A0.ordinal() + i + 1));
            }
            RegisterUtils.moveValueOffset(params.get(i));
        }
        for (BasicBlock basicBlock : basicBlocks) {
            basicBlock.generateAssembly();
        }
    }

    public void simplifyBlock() {
        for (BasicBlock basicBlock : basicBlocks) {
            basicBlock.simplifyBlock();
        }
    }
}
