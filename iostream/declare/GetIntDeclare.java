package iostream.declare;

import backend.generation.mips.Register;
import backend.generation.mips.asm.textsegment.complex.LiAsm;
import backend.generation.mips.asm.textsegment.complex.MoveAsm;
import backend.generation.mips.asm.textsegment.mipsinstr.SyscallAsm;
import backend.generation.utils.AssemblyUnit;
import backend.generation.utils.RegisterUtils;
import midend.generation.utils.IrNameController;
import midend.generation.utils.irtype.VarType;
import iostream.structure.IoStreamGeneration;
import midend.generation.value.Value;
import midend.generation.value.construction.BasicBlock;
import midend.simplify.controller.datastruct.FunctionClone;

/**
 * GetIntDeclare 是从控制台读取一个整数的声明
 * 是生成LLVM IR的函数声明
 * 继承自 IoStreamGeneration
 */
public class GetIntDeclare extends IoStreamGeneration {
    public GetIntDeclare(String name, String instrType) {
        super(name, instrType, new VarType(32));
    }

    public static String getDeclare() {
        return "declare i32 @getint() ";
    }

    @Override
    public String toString() {
        return name + " = call i32 @getint()";
    }

    @Override
    public void generateAssembly() {
        super.generateAssembly();
        // 首先我们需要使用 syscall 指令读取一个整数
        new LiAsm(Register.V0, 5);
        new SyscallAsm();
        // 然后我们需要将读取到的整数加载到寄存器中
        Register reg = AssemblyUnit.getRegisterController().getRegister(this);
        // 如果寄存器已经被分配了，那么我们就需要将其移动到 V0 中
        if (reg != null) {
            new MoveAsm(reg, Register.V0);
        } else {
            RegisterUtils.reAllocReg(this, Register.V0);
        }
    }

    @Override
    public Value copy(FunctionClone functionClone) {
        BasicBlock copyBlock = (BasicBlock) functionClone.getValue(this.getBelongingBlock());
        GetIntDeclare getIntDeclare = new GetIntDeclare(
                IrNameController.getLocalVarName(functionClone.getCaller()) + "_Inline",
                this.getInstrType());
        copyBlock.addInstr(getIntDeclare);
        return getIntDeclare;
    }
}
