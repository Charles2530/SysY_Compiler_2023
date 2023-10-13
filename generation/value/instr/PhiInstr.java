package generation.value.instr;

import generation.utils.irtype.VarType;
import generation.value.construction.BasicBlock;
import generation.value.construction.user.Instr;

import java.util.ArrayList;

public class PhiInstr extends Instr {
    private ArrayList<BasicBlock> preBlockList;

    public PhiInstr(String name, String instrType, ArrayList<BasicBlock> preBlockList) {
        super(new VarType(32), name, instrType);
        this.preBlockList = preBlockList;
        addOperand(null, preBlockList.size());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" = phi ").append(instrType).append(" ");
        for (int i = 0; i < operands.size(); i++) {
            sb.append("[ ").append(operands.get(i).getName()).append(", %")
                    .append(preBlockList.get(i).getName()).append(" ]");
            if (i != operands.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}
