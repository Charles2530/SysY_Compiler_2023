package backend.generation.mips;

import backend.generation.mips.asm.Assembly;

import java.util.ArrayList;

public class Segment {
    private final ArrayList<Assembly> assemblySegment;

    public Segment() {
        this.assemblySegment = new ArrayList<>();
    }

    public void addAssembly(Assembly assembly) {
        assemblySegment.add(assembly);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Assembly assembly : assemblySegment) {
            sb.append(assembly.toString()).append("\n");
        }
        return sb.toString();
    }
}
