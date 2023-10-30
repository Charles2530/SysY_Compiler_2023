package midend.generation.value.instr.optimizer;

import midend.generation.utils.irtype.VarType;
import midend.generation.value.Value;
import midend.generation.value.construction.user.Instr;

import java.util.ArrayList;

public class ParallelCopy extends Instr {
    private final ArrayList<Value> from;
    private final ArrayList<Value> to;

    public ParallelCopy(String name) {
        super(new VarType(0), name, "parallelCopy");
        this.from = new ArrayList<>();
        this.to = new ArrayList<>();
    }

    public void addParallelCopy(Value from, Value to) {
        this.from.add(from);
        this.to.add(to);
    }

    public ArrayList<Value> getFrom() {
        return from;
    }

    public ArrayList<Value> getTo() {
        return to;
    }
}
