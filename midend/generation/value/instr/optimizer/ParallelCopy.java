package midend.generation.value.instr.optimizer;

import midend.generation.utils.irtype.VarType;
import midend.generation.value.Value;
import midend.generation.value.construction.user.Instr;

import java.util.ArrayList;

/**
 * ParallelCopy 用于生成 LLVM IR 中的 parallelCopy 指令,
 * 继承于 Instr,主要用于消除PhiInstr中使用，存储临时变量,
 * 便于后续转为MoveInstr
 */
public class ParallelCopy extends Instr {
    /**
     * from 是 parallelCopy 指令的源操作数集合，用于存储消除PhiInstr产生的临时变量
     * to 是 parallelCopy 指令的目的操作数集合，用于存储消除PhiInstr产生的临时变量
     */
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
