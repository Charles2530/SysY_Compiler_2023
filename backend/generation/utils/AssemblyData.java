package backend.generation.utils;

import backend.generation.mips.asm.Assembly;
import backend.generation.mips.Segment;

public class AssemblyData {
    private static Segment textSegment;
    private static Segment dataSegment;

    public AssemblyData() {
        AssemblyData.textSegment = new Segment();
        AssemblyData.dataSegment = new Segment();
    }

    public static void addTextAssembly(Assembly assembly) {
        textSegment.addAssembly(assembly);
    }

    public static void addDataAssembly(Assembly assembly) {
        dataSegment.addAssembly(assembly);
    }

    @Override
    public String toString() {
        return ".data\n" + dataSegment + "\n\n\n.text\n" + textSegment;
    }
}
