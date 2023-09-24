package LLVM;

import Syntax.AstRecursion;
import Syntax.SyntaxAnalysis;

public class ErrorController {
    public static void DeclPrintError() {
        if (SyntaxAnalysis.getDebugMode()) {
            System.err.println("Error: Decl: "
                    + AstRecursion.getPreSymToken().getLineNum() +
                    ": preSymToken is " + AstRecursion.getPreSymToken().getWord());
        }
    }

    public static void FuncDefPrintError() {
        if (SyntaxAnalysis.getDebugMode()) {
            System.err.println("Error: FuncDef: "
                    + AstRecursion.getPreSymToken().getLineNum() +
                    ": preSymToken is " + AstRecursion.getPreSymToken().getWord());
        }
    }

    public static void MainFuncDefPrintError() {
        if (SyntaxAnalysis.getDebugMode()) {
            System.err.println("Error: MainFuncDef: "
                    + AstRecursion.getPreSymToken().getLineNum() +
                    ": preSymToken is " + AstRecursion.getPreSymToken().getWord());
        }
    }

    public static void DefinerPrintError() {
        if (SyntaxAnalysis.getDebugMode()) {
            System.err.println("Error: Definer: BlockItem: "
                    + AstRecursion.getPreSymToken().getLineNum());
        }
    }
}
