package midend.simplify.controller;

import midend.generation.utils.irtype.VarType;
import midend.generation.value.Value;
import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.Constant;
import midend.generation.value.construction.user.Instr;
import midend.generation.value.instr.basis.CalcInstr;

import java.util.Iterator;

public class ConstantFoldingController {
    public static void foldingCalcInstr(BasicBlock entry) {
        Iterator<Instr> iter = entry.getInstrArrayList().iterator();
        while (iter.hasNext()) {
            Instr instr = iter.next();
            if (instr instanceof CalcInstr calcInstr) {
                Value instead = (calcInstr.getConstantNum().equals(0)) ?
                        ConstantFoldingController.simplifyNoConstant(calcInstr) :
                        (calcInstr.getConstantNum().equals(1)) ?
                                ConstantFoldingController.simplifySingle(calcInstr) :
                                ConstantFoldingController.simplifyDouble(calcInstr);
                if (instead != null) {
                    calcInstr.replaceAllUse(instead);
                    iter.remove();
                }
            }
        }
    }

    private static Value simplifyNoConstant(CalcInstr calcInstr) {
        if (calcInstr.getOperands().get(0).equals(calcInstr.getOperands().get(1))) {
            if (calcInstr.getInstrType().matches("sub|srem")) {
                return new Constant("0", new VarType(32));
            } else if (calcInstr.getInstrType().matches("sdiv")) {
                return new Constant("1", new VarType(32));
            }
        }
        return null;
    }

    private static Value simplifySingle(CalcInstr calcInstr) {
        if (calcInstr.getOperands().get(0) instanceof Constant constant) {
            int val = constant.getVal();
            if (val == 0) {
                if (calcInstr.getInstrType().matches("add")) {
                    return calcInstr.getOperands().get(1);
                } else if (calcInstr.getInstrType().matches("mul")) {
                    return new Constant("0", new VarType(32));
                }
            } else if (val == 1) {
                if (calcInstr.getInstrType().matches("mul")) {
                    return calcInstr.getOperands().get(1);
                }
            }
        } else {
            int val = ((Constant) calcInstr.getOperands().get(1)).getVal();
            if (val == 0) {
                if (calcInstr.getInstrType().matches("add|sub")) {
                    return calcInstr.getOperands().get(0);
                } else if (calcInstr.getInstrType().matches("mul")) {
                    return new Constant("0", new VarType(32));
                }
            } else if (val == 1) {
                if (calcInstr.getInstrType().matches("mul|sdiv")) {
                    return calcInstr.getOperands().get(0);
                } else if (calcInstr.getInstrType().matches("srem")) {
                    return new Constant("0", new VarType(32));
                }
            }
        }
        return null;
    }

    private static Value simplifyDouble(CalcInstr calcInstr) {
        int op1 = ((Constant) calcInstr.getOperands().get(0)).getVal();
        int op2 = ((Constant) calcInstr.getOperands().get(1)).getVal();
        op2 = (calcInstr.getInstrType().matches("sdiv|srem") && op2 == 0) ? 1 : op2;
        return switch (calcInstr.getInstrType()) {
            case "add" -> new Constant(String.valueOf(op1 + op2), new VarType(32));
            case "sub" -> new Constant(String.valueOf(op1 - op2), new VarType(32));
            case "mul" -> new Constant(String.valueOf(op1 * op2), new VarType(32));
            case "sdiv" -> new Constant(String.valueOf(op1 / op2), new VarType(32));
            case "srem" -> new Constant(String.valueOf(op1 % op2), new VarType(32));
            case "and" -> new Constant(String.valueOf(op1 & op2), new VarType(32));
            case "or" -> new Constant(String.valueOf(op1 | op2), new VarType(32));
            default -> null;
        };
    }
}
