package generation.value.construction;

import generation.utils.IrNameController;
import generation.utils.irtype.StructType;
import generation.value.Value;
import generation.value.construction.user.Function;
import generation.value.construction.user.Instr;

import java.util.ArrayList;

public class BasicBlock extends Value {
    private ArrayList<Instr> instrArrayList;

    private Function belongingFunc;

    public BasicBlock(String name) {
        super(new StructType("basicblock"), name);
        this.instrArrayList = new ArrayList<>();
        IrNameController.addBasicBlock(this);
    }

    public void addInstr(Instr instr) {
        instrArrayList.add(instr);
    }

    public boolean isEmpty() {
        return instrArrayList.isEmpty();
    }

    public Instr getLastInstr() {
        return instrArrayList.get(instrArrayList.size() - 1);
    }

    public Function getBelongingFunc() {
        return belongingFunc;
    }

    public void setBelongingFunc(Function belongingFunc) {
        this.belongingFunc = belongingFunc;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(":\n\t");
        for (int i = 0; i < instrArrayList.size(); i++) {
            sb.append(instrArrayList.get(i).toString());
            if (i != instrArrayList.size() - 1) {
                sb.append("\n\t");
            }
        }
        return sb.toString();
    }
}
