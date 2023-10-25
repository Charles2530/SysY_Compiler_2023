package backend.mips.asm.datasegment;

import backend.mips.asm.DataAssembly;

import java.util.ArrayList;

public class WordAsm extends DataAssembly {
    private final ArrayList<Integer> initValue;

    public WordAsm(String label, String value, ArrayList<Integer> initValue) {
        super(label, value);
        this.initValue = initValue;
    }

    @Override
    public String toString() {
        if (!value.equals("array")) {
            return label + ": .word " + value;
        } else {
            StringBuilder res = new StringBuilder(label + ": .word ");
            for (Integer integer : initValue) {
                res.append(integer);
                if (!integer.equals(initValue.get(initValue.size() - 1))) {
                    res.append(", ");
                }
            }
            return res.toString();
        }
    }
}
