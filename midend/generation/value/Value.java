package midend.generation.value;

import midend.generation.utils.IrType;
import midend.generation.value.construction.User;
import midend.simplify.controller.datastruct.Use;

import java.util.ArrayList;

public class Value {
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

    public void generateAssembly() {
    }

    public void addUseDefChain(User user) {
        useDefChain.add(new Use(user, this));
    }

    public void deleteUseDefChain(User user) {
        useDefChain.removeIf(value -> value.getUser().equals(user));
    }

    public void replaceUseDefChain(Value origin, Value present, User user) {
        origin.deleteUseDefChain(user);
        present.addUseDefChain(user);
    }
}
