package llvm;

import syntax.AstRecursion;
import syntax.SyntaxAnalysis;

public class ErrorController {
    private String errorCategoryCode;// 错误类别码
    private int lineNum;  // 词法分析器的行号

    public ErrorController(String errorCategoryCode, int lineNum) {
        this.errorCategoryCode = errorCategoryCode;
        this.lineNum = lineNum;
        printError(errorCategoryCode, lineNum);
    }

    private void printError(String errorCategoryCode, int lineNum) {
        switch (errorCategoryCode) {
            case "a":
                IllegalTokenError(lineNum);
                break;
            case "i":
                MissSemicnError(lineNum);
                break;
            case "j":
                MissRparentError(lineNum);
                break;
            case "k":
                MissRbrackError(lineNum);
                break;
            default:
                break;
        }
    }

    private void MissRbrackError(int lineNum) {
        if (SyntaxAnalysis.getDebugMode()) {
            System.err.println("Error: MissRbrackError: " + lineNum);
        }
    }

    private void MissRparentError(int lineNum) {
        if (SyntaxAnalysis.getDebugMode()) {
            System.err.println("Error: MissRparentError: " + lineNum);
        }
    }

    private void MissSemicnError(int lineNum) {
        if (SyntaxAnalysis.getDebugMode()) {
            System.err.println("Error: MissSemicnError: " + lineNum);
        }
    }

    private void IllegalTokenError(int lineNum) {
        if (SyntaxAnalysis.getDebugMode()) {
            System.err.println("Error: IllegalTokenError: " + lineNum);
        }
    }

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
