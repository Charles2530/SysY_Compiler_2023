package midend.generation.value.instr.optimizer;

import midend.generation.utils.IrNameController;
import midend.generation.utils.irtype.VarType;
import midend.generation.value.Value;
import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.Constant;
import midend.generation.value.construction.user.Instr;
import midend.simplify.controller.datastruct.FunctionClone;

import java.util.ArrayList;

/**
 * PhiInstr 用于生成 LLVM IR 中的 phi 指令,
 * 继承于 Instr,主要用于Mem2Reg环节代码优化使用
 */
public class PhiInstr extends Instr {
    /**
     * indBasicBlock 是 phi 指令在数据流分析中的前序基本块集合
     */
    private final ArrayList<BasicBlock> indBasicBlock;

    public PhiInstr(String name, ArrayList<BasicBlock> indBasicBlock, int... cnt) {
        super(new VarType(32), name, "phi");
        this.indBasicBlock = new ArrayList<>(indBasicBlock);
        int size = (cnt.length == 0) ? this.indBasicBlock.size() : cnt[0];
        addOperand(null, size);
        while (this.indBasicBlock.size() < size) {
            this.indBasicBlock.add(null);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" = ").append(instrType).append(" ").append(type).append(" ");
        for (int i = 0; i < indBasicBlock.size(); i++) {
            sb.append("[ ").append(operands.get(i).getName()).append(", %")
                    .append(indBasicBlock.get(i).getName()).append(" ]");
            if (i != operands.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    @Override
    public Value copy(FunctionClone functionClone) {
        BasicBlock copyBlock = (BasicBlock) functionClone.getValue(this.getBelongingBlock());
        ArrayList<BasicBlock> copyIndBasicBlock = new ArrayList<>();
        for (BasicBlock basicBlock : this.indBasicBlock) {
            copyIndBasicBlock.add((BasicBlock) functionClone.getValue(basicBlock));
        }
        Instr instr = new PhiInstr(
                IrNameController.getLocalVarName(functionClone.getCaller()) + "_Inline",
                copyIndBasicBlock);
        copyBlock.addInstr(instr);
        return instr;
    }

    /**
     * modifyValue() 用于修改 PhiInstr 中的操作数并添加使用定义链
     */
    public void modifyValue(Value value, BasicBlock initialBasicBlock) {
        operands.set(indBasicBlock.indexOf(initialBasicBlock), value);
        value.addUseDefChain(this);
    }

    /**
     * generateCopyList() 用于将PhiInstr转化为ParallelCopy的集合，便于转为MIPS汇编
     * 该函数执行逻辑如下：
     * 1.获得phi所有的options
     * 2.将每个option都插入到对应的ParallelCopy中
     */
    public void generateCopyList(ArrayList<ParallelCopy> pcList) {
        for (int i = 0; i < operands.size(); i++) {
            Value operand = operands.get(i);
            if (!(operand instanceof Constant constant && !constant.isDefined())) {
                pcList.get(i).addParallelCopy(operand, this);
            }
        }
    }

    /**
     * reducePhi() 用于消除冗余的PhiInstr，比如说所有的 input 都相等的情况
     *
     * @param flag 用于判断是否需要消除非指令的PhiInstr
     */
    public boolean reducePhi(boolean flag) {
        if (!getUseDefChain().isEmpty()) {
            Value val = operands.get(0);
            for (int i = 1; i < operands.size(); i++) {
                if (operands.get(i) != val) {
                    return false;
                }
            }
            if (!flag && val instanceof Instr) {
                return false;
            }
            replaceAllUse(val);
        }
        dropOperands();
        return true;
    }

    /**
     * inlineReturnValue() 用于将返回值内联到PhiInstr中
     * 主要用于函数内联
     */
    public void inlineReturnValue(Value retValue, BasicBlock belongingBlock) {
        int mark;
        for (mark = 0; mark < operands.size(); mark++) {
            if (operands.get(mark) == null) {
                break;
            }
        }
        if (mark < operands.size()) {
            operands.set(mark, retValue);
            indBasicBlock.set(mark, belongingBlock);
        } else {
            operands.add(retValue);
            indBasicBlock.add(belongingBlock);
        }
        retValue.addUseDefChain(this);
        belongingBlock.addUseDefChain(this);
    }
}
