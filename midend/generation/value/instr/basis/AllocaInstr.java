package midend.generation.value.instr.basis;

import backend.generation.mips.Register;
import backend.generation.mips.asm.textsegment.mipsinstr.ItypeAsm;
import backend.generation.utils.AssemblyUnit;
import backend.generation.utils.RegisterUtils;
import midend.generation.utils.IrNameController;
import midend.generation.utils.IrType;
import midend.generation.utils.irtype.ArrayType;
import midend.generation.utils.irtype.PointerType;
import midend.generation.value.Value;
import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.user.Instr;
import midend.simplify.controller.datastruct.FunctionClone;

/**
 * AllocaInstr 用于生成 LLVM IR 中的 alloca 指令,
 * 继承于 Instr,主要用于申请内存空间
 * <result> = alloca <type>
 */
public class AllocaInstr extends Instr {
    /**
     * type 是 alloca 指令所申请的内存空间对应的变量的类型，
     * 而alloca指令本身自己的类型为该变量的指针类型
     */
    private final IrType type;

    public AllocaInstr(String name, IrType type) {
        super(new PointerType(type), name, "alloca");
        this.type = type;
    }

    @Override
    public String toString() {
        return name + " = " + instrType + " " + type;
    }

    @Override
    public void generateAssembly() {
        super.generateAssembly();
        // 在栈上分配空间，同时更新栈指针
        AssemblyUnit.moveCurrentOffset(
                (type.isArray()) ? (-4 * ((ArrayType) type).calcSpaceTot()) : -4);
        // 尝试从寄存器中获取当前alloca指令的寄存器
        Register reg = AssemblyUnit.getRegisterController().getRegister(this);
        if (reg != null) {
            // 如果存在对应的寄存器，那么我们直接将地址赋值给这个寄存器即可
            new ItypeAsm("addi", reg, Register.SP, AssemblyUnit.getCurrentOffset());
        } else {
            // 如果不存在对应的寄存器，那么我们需要重新分配一个寄存器
            // 首先使用K0寄存器保存分配空间的首地址（实际上是最低地址）
            new ItypeAsm("addi", Register.K0, Register.SP, AssemblyUnit.getCurrentOffset());
            // 然后再从栈上为这个指令开一个空间，保存刚刚新分配空间的首地址
            RegisterUtils.allocReg(this, Register.K0);
        }
    }

    @Override
    public Value copy(FunctionClone functionClone) {
        BasicBlock copyBlock = (BasicBlock) functionClone.getValue(this.getBelongingBlock());
        Instr instr = new AllocaInstr(IrNameController.getLocalVarName(
                functionClone.getCaller()) + "_Inline", this.type);
        copyBlock.addInstr(instr);
        return instr;
    }
}
