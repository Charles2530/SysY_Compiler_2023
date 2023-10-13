package generation.value.construction;

public class Instr extends User {
    protected String instrType;
    private BasicBlock parent;

    public Instr(String name, String instrType) {
        super(name);
        this.instrType = instrType;
    }
}
