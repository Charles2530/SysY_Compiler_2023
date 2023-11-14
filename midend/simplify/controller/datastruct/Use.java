package midend.simplify.controller.datastruct;

import midend.generation.value.Value;
import midend.generation.value.construction.User;

/**
 * Use 是使用的数据结构，
 * 主要用于记录使用的数据结构
 * 便于形成Use-Def链
 */
public class Use {
    /**
     * user 是Use-Def链中的使用者
     * value 是Use-Def链中的使用值
     */
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
