package midend.generation.value.construction;

import midend.generation.utils.IrType;
import midend.generation.value.Value;

import java.util.ArrayList;

/**
 * User 是所有定义使用链成分User的基类，
 * 继承于Value，主要用于生成使用定义链
 */
public class User extends Value {
    /**
     * operands 是该 User 的操作数
     */
    protected ArrayList<Value> operands;

    public User(IrType type, String name) {
        super(type, name);
        this.operands = new ArrayList<>();
    }

    /**
     * addOperand 方法用于添加操作数
     *
     * @param value 操作数 Value
     * @param cnt   操作数出现次数
     */
    public void addOperand(Value value, Integer... cnt) {
        int times = 1;
        if (cnt.length != 0) {
            times = cnt[0];
        }
        for (int i = 0; i < times; i++) {
            operands.add(value);
            if (value != null) {
                value.addUseDefChain(this);
            }
        }
    }

    /**
     * replaceOperand 方法用于替换操作数
     */
    public void replaceOperand(Value origin, Value present) {
        if (!operands.contains(origin)) {
            return;
        }
        operands.set(operands.indexOf(origin), present);
        replaceUseDefChain(origin, present, this);
    }

    /**
     * dropOperands 方法用于删除所有User中的该操作数
     */
    public void dropOperands() {
        for (Value operand : operands) {
            operand.getUsers().remove(this);
        }
    }

    public ArrayList<Value> getOperands() {
        return operands;
    }
}
