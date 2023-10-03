package generation;

public class LLvmInstr {
    //LLVM IR
    public static String add(String result, String ty, String op1, String op2) {
        return result + " = add " + ty + " " + op1 + ", " + op2;
    }

    public static String sub(String result, String ty, String op1, String op2) {
        return result + " = sub " + ty + " " + op1 + ", " + op2;
    }

    public static String mul(String result, String ty, String op1, String op2) {
        return result + " = mul " + ty + " " + op1 + ", " + op2;
    }

    public static String sdiv(String result, String ty, String op1, String op2) {
        return result + " = sdiv " + ty + " " + op1 + ", " + op2;
    }

    public static String icmp(String result, String cond, String ty, String op1, String op2) {
        return result + " = icmp " + cond + " " + ty + " " + op1 + ", " + op2;
    }

    public static String and(String result, String ty, String op1, String op2) {
        return result + " = and " + ty + " " + op1 + ", " + op2;
    }

    public static String or(String result, String ty, String op1, String op2) {
        return result + " = or " + ty + " " + op1 + ", " + op2;
    }

    public static String call(String result, String retAttrs, String ty
            , String fnptrval, String functionArgs) {
        return result + " = call " + retAttrs + " " + ty + " "
                + fnptrval + "(" + functionArgs + ")";
    }

    public static String alloca(String result, String type) {
        return result + " = alloca " + type;
    }

    public static String load(String result, String ty, String pointer) {
        return result + " = load " + ty + ", " + ty + "* " + pointer;
    }

    public static String store(String ty, String value, String pointer) {
        return "store " + ty + " " + value + ", " + ty + "* " + pointer;
    }

    public static String getelementptr(String result, String ty, String idx) {
        return result + " = getelementptr " + ty + ", " + ty + "* " + idx;
    }

    public static String phi(String result, String fastMathFlags,
                             String ty, String val0, String label0) {
        return result + " = phi " + fastMathFlags + " " + ty + " " + val0 + ", " + label0;
    }

    public static String zextTo(String result, String ty, String value, String ty2) {
        return result + " = zext " + ty + " " + value + " to " + ty2;
    }

    public static String truncTo(String result, String ty, String value, String ty2) {
        return result + " = trunc " + ty + " " + value + " to " + ty2;
    }

    public static String brCond(String cond, String iftrue, String iffalse) {
        return "br i1 " + cond + ", label " + iftrue + ", label " + iffalse;
    }

    public static String brLabel(String dest) {
        return "br label " + dest;
    }
}
