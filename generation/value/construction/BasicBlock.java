package generation.value.construction;

import generation.utils.irtype.StructType;
import generation.value.Value;
import generation.value.construction.user.Instr;

import java.util.ArrayList;

public class BasicBlock extends Value {
    private ArrayList<Instr> instrArrayList;

    public BasicBlock(String name) {
        super(new StructType("basicblock"), name);
        this.instrArrayList = new ArrayList<>();
    }

    public boolean isEmpty() {
        return instrArrayList.isEmpty();
    }

    public Object getLastInstr() {
        return instrArrayList.get(instrArrayList.size() - 1);
    }
}
