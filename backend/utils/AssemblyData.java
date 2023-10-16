package backend.utils;

import backend.mips.Assembly;
import backend.mips.Segment;

public class AssemblyData {
    private Segment textSegment;
    private Segment dataSegment;

    public AssemblyData() {
        this.textSegment = new Segment();
        this.dataSegment = new Segment();
    }

    public void addTextAssembly(Assembly assembly) {
        textSegment.addAssembly(assembly);
    }

    public void addDataAssembly(Assembly assembly) {
        dataSegment.addAssembly(assembly);
    }

    @Override
    public String toString() {
        return ".data\n" +
                dataSegment +
                "\n\n\n.text\n" +
                textSegment;
    }
}
