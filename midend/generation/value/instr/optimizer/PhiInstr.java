package midend.generation.value.instr.optimizer;

import midend.generation.utils.irtype.VarType;
import midend.generation.value.Value;
import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.Constant;
import midend.generation.value.construction.user.Instr;

import java.util.ArrayList;

public class PhiInstr extends Instr {
    private final ArrayList<BasicBlock> indBasicBlock;

    public PhiInstr(String name, ArrayList<BasicBlock> indBasicBlock) {
        super(new VarType(32), name, "phi");
        this.indBasicBlock = indBasicBlock;
        addOperand(null, indBasicBlock.size());
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

    public void modifyValue(Value value, BasicBlock initialBasicBlock) {
        operands.set(indBasicBlock.indexOf(initialBasicBlock), value);
        value.addUseDefChain(this);
    }

    public void generateCopyList(ArrayList<ParallelCopy> pcList) {
        for (int i = 0; i < operands.size(); i++) {
            Value operand = operands.get(i);
            if (!(operand instanceof Constant constant && !constant.isDefined())) {
                pcList.get(i).addParallelCopy(this, operand);
            }
        }
    }
}
