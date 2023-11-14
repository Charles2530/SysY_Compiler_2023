package midend.generation.value.instr.optimizer;

import midend.generation.utils.irtype.VarType;
import midend.generation.value.Value;
import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.Constant;
import midend.generation.value.construction.user.Instr;
import midend.simplify.controller.datastruct.FunctionClone;

import java.util.ArrayList;

public class PhiInstr extends Instr {
    private final ArrayList<BasicBlock> indBasicBlock;

    public PhiInstr(String name, ArrayList<BasicBlock> indBasicBlock, int... cnt) {
        super(new VarType(32), name, "phi");
        this.indBasicBlock = indBasicBlock;
        int size = (cnt.length == 0) ? indBasicBlock.size() : cnt[0];
        addOperand(null, size);
    }

    public ArrayList<BasicBlock> getIndBasicBlock() {
        return indBasicBlock;
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
        Instr instr = new PhiInstr(this.getName(), copyIndBasicBlock);
        copyBlock.addInstr(instr);
        return instr;
    }

    public void modifyValue(Value value, BasicBlock initialBasicBlock) {
        operands.set(indBasicBlock.indexOf(initialBasicBlock), value);
        value.addUseDefChain(this);
    }

    public void generateCopyList(ArrayList<ParallelCopy> pcList) {
        for (int i = 0; i < operands.size(); i++) {
            Value operand = operands.get(i);
            if (!(operand instanceof Constant constant && !constant.isDefined())) {
                pcList.get(i).addParallelCopy(operand, this);
            }
        }
    }

    public void reducePhi(boolean flag) {
        if (!getUseDefChain().isEmpty()) {
            Value val = operands.get(0);
            for (int i = 1; i < operands.size(); i++) {
                if (operands.get(i) != val) {
                    return;
                }
            }
            if (!flag && val instanceof Instr) {
                return;
            }
            replaceAllUse(val);
        }
        dropOperands();
        this.getBelongingBlock().getInstrArrayList().remove(this);
    }
}
