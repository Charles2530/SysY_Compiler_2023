package midend.simplify.controller.datastruct;

import midend.generation.value.Value;
import midend.generation.value.construction.User;

public class Use {
    private final User user;
    private final Value value;

    public Use(User user, Value value) {
        this.user = user;
        this.value = value;
    }

    public User getUser() {
        return user;
    }
}
