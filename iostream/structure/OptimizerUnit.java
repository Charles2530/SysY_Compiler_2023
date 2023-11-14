package iostream.structure;

/**
 * OptimizerUnit 是优化器的基类
 */
public class OptimizerUnit {
    /**
     * isOptimizer 是用于判断是否开启优化器
     * 若开启则会自动将生成的Function，BasicBlock,
     * Instr,Param,FormatString,GlobalVar添加
     * 到对应的module结构各成分对应父类中
     */
    private static boolean isOptimizer;

    public static boolean isIsOptimizer() {
        return isOptimizer;
    }

    public static void setIsOptimizer(boolean isOptimizer) {
        OptimizerUnit.isOptimizer = isOptimizer;
    }

    public void optimize() {
    }
}
