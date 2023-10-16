package midend.generation.utils;

public enum IrPrefix {
    BB_NAME("block_label_"),//基本块名
    FUNC_NAME("@f_"),//函数名
    GLOBAL_VAR_NAME("@g_"),//全局变量名
    LOCAL_VAR_NAME("%v_"),//局部变量名
    PARAM_NAME("%a_"),//参数名
    STRING_LITERAL_NAME("@s_");//字符串字面量名

    private final String prefix;

    IrPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String toString() {
        return prefix;
    }
}
