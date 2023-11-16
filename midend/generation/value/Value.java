package midend.generation.value;

import midend.generation.utils.IrType;
import midend.generation.value.construction.User;
import midend.simplify.controller.datastruct.Use;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * LLVM IR 有一切皆Value的概念，所以LLVM IR生成的所有成分均
 * 可以继承于Value类
 */
public class Value {
    /**
     * type 是该 Value 的类型
     * name 是该 Value 的名字
     * useDefChain 是该 Value 的使用定义链
     */
    protected IrType type;

    protected String name;
    protected ArrayList<Use> useDefChain;

    public Value(IrType type, String name) {
        this.type = type;
        this.name = name;
        this.useDefChain = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public IrType getType() {
        return type;
    }

    public ArrayList<Use> getUseDefChain() {
        return useDefChain;
    }

    public void generateAssembly() {
    }

    /**
     * addUseDefChain() 用于在使用定义链中添加一个新的使用
     */
    public void addUseDefChain(User user) {
        useDefChain.add(new Use(user, this));
    }

    /**
     * deleteUseDefChain() 用于在使用定义链中删除一个使用
     */
    public void deleteUseDefChain(User user) {
        useDefChain.removeIf(value -> value.getUser().equals(user));
    }

    /**
     * replaceUseDefChain() 用于在使用定义链中替换一个使用
     */
    public void replaceUseDefChain(Value origin, Value present, User user) {
        origin.deleteUseDefChain(user);
        present.addUseDefChain(user);
    }

    /**
     * getUses() 用于获取该 Value 的所有使用中的所有 User
     */
    public ArrayList<User> getUsers() {
        return getUseDefChain().stream().map(Use::getUser)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * replaceAllUse() 用于替换所有使用当前Value的User的该Value值为新的Value
     */
    public void replaceAllUse(Value value) {
        getUsers().forEach(user -> user.replaceOperand(this, value));
    }

}
