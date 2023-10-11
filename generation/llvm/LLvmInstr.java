package generation.llvm;

public class LLvmInstr {
    //LLVM IR
    public static String genLlvmInstrAdd(String result, String ty, String op1, String op2) {
        return result + " = add " + ty + " " + op1 + ", " + op2;
    }

    public static String genLlvmInstrSub(String result, String ty, String op1, String op2) {
        return result + " = sub " + ty + " " + op1 + ", " + op2;
    }

    public static String genLlvmInstrMul(String result, String ty, String op1, String op2) {
        return result + " = mul " + ty + " " + op1 + ", " + op2;
    }

    public static String genLlvmInstrSdiv(String result, String ty, String op1, String op2) {
        return result + " = sdiv " + ty + " " + op1 + ", " + op2;
    }

    public static String genLlvmInstrIcmp(String result, String cond,
                                          String ty, String op1, String op2) {
        return result + " = icmp " + cond + " " + ty + " " + op1 + ", " + op2;
    }

    public static String genLlvmInstrAnd(String result, String ty, String op1, String op2) {
        return result + " = and " + ty + " " + op1 + ", " + op2;
    }

    public static String genLlvmInstrOr(String result, String ty, String op1, String op2) {
        return result + " = or " + ty + " " + op1 + ", " + op2;
    }

    public static String genLlvmInstrCall(String result, String retAttrs, String ty
            , String fnptrval, String functionArgs) {
        return result + " = call " + retAttrs + " " + ty + " "
                + fnptrval + "(" + functionArgs + ")";
    }

    public static String genLlvmInstrAlloca(String result, String type) {
        return result + " = alloca " + type;
    }

    public static String genLlvmInstrLoad(String result, String ty, String pointer) {
        return result + " = load " + ty + ", " + ty + "* " + pointer;
    }

    public static String genLlvmInstrStore(String ty, String value, String pointer) {
        return "store " + ty + " " + value + ", " + ty + "* " + pointer;
    }

    public static String genLlvmInstrGetElementPtr(String result, String ty, String idx) {
        return result + " = getelementptr " + ty + ", " + ty + "* " + idx;
    }

    public static String genLlvmInstrPhi(String result, String fastMathFlags,
                                         String ty, String val0, String label0) {
        return result + " = phi " + fastMathFlags + " " + ty + " " + val0 + ", " + label0;
    }

    public static String genLlvmInstrZextTo(String result, String ty, String value, String ty2) {
        return result + " = zext " + ty + " " + value + " to " + ty2;
    }

    public static String genLlvmInstrTruncTo(String result, String ty, String value, String ty2) {
        return result + " = trunc " + ty + " " + value + " to " + ty2;
    }

    public static String genLlvmInstrBrCond(String cond, String iftrue, String iffalse) {
        return "br i1 " + cond + ", label " + iftrue + ", label " + iffalse;
    }

    public static String genLlvmInstrBrLabel(String dest) {
        return "br label " + dest;
    }
}
