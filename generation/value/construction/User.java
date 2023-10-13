package generation.value.construction;

import generation.value.Value;

import java.util.ArrayList;

public class User extends Value {
    protected ArrayList<Value> operands;

    public User(String name) {
        super(name);
        this.operands = new ArrayList<>();
    }
}
