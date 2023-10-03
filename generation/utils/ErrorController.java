package generation.utils;

import semantic.symbolTable.Symbol;
import syntax.AstRecursion;

import java.io.BufferedWriter;
import java.io.IOException;

public class ErrorController {
    private String errorCategoryCode;// 错误类别码
    private int lineNum;  // 词法分析器的行号
    private static BufferedWriter errorBufferedWriter;
    private static boolean isDebugMode;

    public ErrorController(String errorCategoryCode, int lineNum) throws IOException {
        this.errorCategoryCode = errorCategoryCode;
        this.lineNum = lineNum;
        printError();
    }

    public static void setBufferedWriter(BufferedWriter errorBufferedWriter) {
        ErrorController.errorBufferedWriter = errorBufferedWriter;
    }

    public static void setIsDebugMode(boolean isDebugMode) {
        ErrorController.isDebugMode = isDebugMode;
    }

    public static void LexicalWordCheckPrintError(int lineNum, String c) throws IOException {
        if (isDebugMode) {
            errorBufferedWriter.write("error in " + lineNum +
                    " line,word:" + c + " can not be recognized");
            errorBufferedWriter.newLine();
        }
    }

    public static void SyntaxAnalysisError(String grammarType) throws IOException {
        if (isDebugMode) {
            errorBufferedWriter.write("grammarType:" + grammarType);
            errorBufferedWriter.newLine();
        }
    }

    public static void SymbolError(Symbol symbol) {
        if (isDebugMode) {
            System.out.println("symbol:" + symbol);
        }
    }

    private void printError() throws IOException {
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

    private void MissRbrackError(int lineNum) throws IOException {
        if (isDebugMode) {
            errorBufferedWriter.write("Error: MissRbrackError: " + lineNum);
            errorBufferedWriter.newLine();
        }
    }

    private void MissRparentError(int lineNum) throws IOException {
        if (isDebugMode) {
            errorBufferedWriter.write("Error: MissRparentError: " + lineNum);
            errorBufferedWriter.newLine();
        }
    }

    private void MissSemicnError(int lineNum) throws IOException {
        if (isDebugMode) {
            errorBufferedWriter.write("Error: MissSemicnError: " + lineNum);
            errorBufferedWriter.newLine();
        }
    }

    private void IllegalTokenError(int lineNum) throws IOException {
        if (isDebugMode) {
            errorBufferedWriter.write("Error: IllegalTokenError: " + lineNum);
            errorBufferedWriter.newLine();
        }
    }

    public static void DeclPrintError() throws IOException {
        if (isDebugMode) {
            errorBufferedWriter.write("Error: Decl: "
                    + AstRecursion.getPreSymToken().getLineNum() +
                    ": preSymToken is " + AstRecursion.getPreSymToken().getWord());
            errorBufferedWriter.newLine();
        }
    }

    public static void FuncDefPrintError() throws IOException {
        if (isDebugMode) {
            errorBufferedWriter.write("Error: FuncDef: "
                    + AstRecursion.getPreSymToken().getLineNum() +
                    ": preSymToken is " + AstRecursion.getPreSymToken().getWord());
            errorBufferedWriter.newLine();
        }
    }

    public static void MainFuncDefPrintError() throws IOException {
        if (isDebugMode) {
            errorBufferedWriter.write("Error: MainFuncDef: "
                    + AstRecursion.getPreSymToken().getLineNum() +
                    ": preSymToken is " + AstRecursion.getPreSymToken().getWord());
            errorBufferedWriter.newLine();
        }
    }

    public static void DefinerPrintError() throws IOException {
        if (isDebugMode) {
            errorBufferedWriter.write("Error: Definer: BlockItem: "
                    + AstRecursion.getPreSymToken().getLineNum());
            errorBufferedWriter.newLine();
        }
    }

    public static void LexicalAnalysisPrintError(int lineNum, String word) throws IOException {
        if (isDebugMode) {
            errorBufferedWriter.write("error in " + lineNum + " line,word:"
                    + word + " is not a reserved word");
            errorBufferedWriter.newLine();
        }
    }
}
