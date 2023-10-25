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
        if (initValue == null) {
            return label + ": .word " + value;
        } else {
            int offsetTot = Integer.parseInt(value) - initValue.size();
            StringBuilder res = new StringBuilder(label + ": .word ");
            for (int i = initValue.size() - 1; i >= 0; i--) {
                res.append(initValue.get(i));
                if (i != 0) {
                    res.append(", ");
                }
            }
            for (int i = 0; i < offsetTot; i++) {
                res.append("0");
                if (i != offsetTot - 1) {
                    res.append(", ");
                }
            }
            return res.toString();
        }
    }
}
