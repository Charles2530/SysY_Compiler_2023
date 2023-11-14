package backend.generation.utils;

import backend.generation.mips.asm.Assembly;
import backend.generation.mips.Segment;

/**
 * AssemblyData 是一个汇编数据
 * 主要用于汇编代码的生成
 */
public class AssemblyData {
    /**
     * textSegment 是.text段
     * dataSegment 是.data段
     */
    private static Segment textSegment;
    private static Segment dataSegment;

    public AssemblyData() {
        AssemblyData.textSegment = new Segment();
        AssemblyData.dataSegment = new Segment();
    }

    /**
     * addTextAssembly 方法用于向.text段添加汇编代码
     */
    public static void addTextAssembly(Assembly assembly) {
        textSegment.addAssembly(assembly);
    }

    /**
     * addDataAssembly 方法用于向.data段添加汇编代码
     */
    public static void addDataAssembly(Assembly assembly) {
        dataSegment.addAssembly(assembly);
    }

    @Override
    public String toString() {
        return ".data\n" + dataSegment + "\n\n\n.text\n" + textSegment;
    }
}
