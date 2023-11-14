package midend.generation.utils;

/**
 * IrPrefix 是 LLVM IR 中的前缀成分，
 * 主要用于生成llvm形式中间代码变量的前缀
 */
public enum IrPrefix {
    /**
     * BB_NAME 是基本块名的前缀
     * FUNC_NAME 是函数名的前缀
     * GLOBAL_VAR_NAME 是全局变量名的前缀
     * LOCAL_VAR_NAME 是局部变量名的前缀
     * PARAM_NAME 是参数名的前缀
     * STRING_LITERAL_NAME 是字符串字面量名的前缀
     */
    BB_NAME("block_label_"),
    FUNC_NAME("@f_"),
    GLOBAL_VAR_NAME("@g_"),
    LOCAL_VAR_NAME("%v_"),
    PARAM_NAME("%a_"),
    STRING_LITERAL_NAME("@s_");
    /**
     * prefix 是该 IrPrefix 的前缀
     */
    private final String prefix;

    IrPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String toString() {
        return prefix;
    }
}
