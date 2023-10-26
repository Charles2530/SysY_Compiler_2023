package iostream;

import frontend.semantic.symtable.SymbolTable;
import frontend.syntax.AstRecursion;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

public class ErrorController {
    private static ArrayList<ErrorToken> errorlist = new ArrayList<>();
    private static BufferedWriter errorBufferedWriter;
    private static boolean isDebugMode;
    private static boolean detailMode;

    public static void setBufferedWriter(BufferedWriter errorBufferedWriter) {
        ErrorController.errorBufferedWriter = errorBufferedWriter;
    }

    public static void setIsDebugMode(boolean isDebugMode) {
        ErrorController.isDebugMode = isDebugMode;
    }

    public static void setDetailMode(boolean detailMode) {
        ErrorController.detailMode = detailMode;
    }

    public static void addError(ErrorToken error) {
        errorlist.add(error);
    }

    public static void printErrors() throws IOException {
        if (isDebugMode) {
            errorlist.sort(Comparator.comparingInt(ErrorToken::getLineNum));
            int lineNum = -1;
            for (ErrorToken error : errorlist) {
                if (error.getLineNum() == lineNum) {
                    continue;
                }
                lineNum = error.getLineNum();
                printError(error);
            }
            printSymbolTable();
        }
    }

    public static boolean hasError() {
        return isDebugMode && !errorlist.isEmpty();
    }

    private static void printError(ErrorToken error) throws IOException {
        switch (error.getErrorCategoryCode()) {
            case "a":
                printIllegalTokenError(error.getLineNum());
                break;
            case "b":
                printRedefinitionError(error.getLineNum());
                break;
            case "c":
                printUndefinedNameError(error.getLineNum());
                break;
            case "d":
                printFuncNumUnmatchError(error.getLineNum());
                break;
            case "e":
                printFuncTypeUnmatchError(error.getLineNum());
                break;
            case "f":
                printUnmatchReturnError(error.getLineNum());
                break;
            case "g":
                printMissingReturnError(error.getLineNum());
                break;
            case "h":
                printAssignTypeConsError(error.getLineNum());
                break;
            case "i":
                printMissSemicnError(error.getLineNum());
                break;
            case "j":
                printMissRparentError(error.getLineNum());
                break;
            case "k":
                printMissRbrackError(error.getLineNum());
                break;
            case "l":
                printUnmatchTokenNumError(error.getLineNum());
                break;
            case "m":
                printUnmatchLoopError(error.getLineNum());
                break;
            default:
                break;
        }
    }

    // a. 非法符号
    private static void printIllegalTokenError(int lineNum) throws IOException {
        if (isDebugMode) {
            if (detailMode) {
                errorBufferedWriter.write("Error: IllegalTokenError: " + lineNum);
            } else {
                errorBufferedWriter.write(lineNum + " a");
            }
            errorBufferedWriter.newLine();
        }
    }

    // b. 名字重定义
    private static void printRedefinitionError(int lineNum) throws IOException {
        if (isDebugMode) {
            if (detailMode) {
                errorBufferedWriter.write("Error: RedefinitionError: " + lineNum);
            } else {
                errorBufferedWriter.write(lineNum + " b");
            }
            errorBufferedWriter.newLine();
        }
    }

    // c. 未定义的名字
    private static void printUndefinedNameError(int lineNum) throws IOException {
        if (isDebugMode) {
            if (detailMode) {
                errorBufferedWriter.write("Error: UndefinedNameError: " + lineNum);
            } else {
                errorBufferedWriter.write(lineNum + " c");
            }
            errorBufferedWriter.newLine();
        }
    }

    // d. 函数参数个数不匹配
    private static void printFuncNumUnmatchError(int lineNum) throws IOException {
        if (isDebugMode) {
            if (detailMode) {
                errorBufferedWriter.write("Error: FuncNumUnmatchError: " + lineNum);
            } else {
                errorBufferedWriter.write(lineNum + " d");
            }
            errorBufferedWriter.newLine();
        }
    }

    // e. 函数参数类型不匹配
    private static void printFuncTypeUnmatchError(int lineNum) throws IOException {
        if (isDebugMode) {
            if (detailMode) {
                errorBufferedWriter.write("Error: FuncTypeUnmatchError: " + lineNum);
            } else {
                errorBufferedWriter.write(lineNum + " e");
            }
            errorBufferedWriter.newLine();
        }
    }

    // f. 无返回值的函数存在不匹配的return语句
    private static void printUnmatchReturnError(int lineNum) throws IOException {
        if (isDebugMode) {
            if (detailMode) {
                errorBufferedWriter.write("Error: UnmatchReturnError: " + lineNum);
            } else {
                errorBufferedWriter.write(lineNum + " f");
            }
            errorBufferedWriter.newLine();
        }
    }

    // g. 有返回值的函数缺少return语句或存在不匹配的return语句
    private static void printMissingReturnError(int lineNum) throws IOException {
        if (isDebugMode) {
            if (detailMode) {
                errorBufferedWriter.write("Error: MissingReturnError: " + lineNum);
            } else {
                errorBufferedWriter.write(lineNum + " g");
            }
            errorBufferedWriter.newLine();
        }
    }

    // h. 不能改变常量的值
    private static void printAssignTypeConsError(int lineNum) throws IOException {
        if (isDebugMode) {
            if (detailMode) {
                errorBufferedWriter.write("Error: AssignTypeConsError: " + lineNum);
            } else {
                errorBufferedWriter.write(lineNum + " h");
            }
            errorBufferedWriter.newLine();
        }
    }

    //  i. 语句缺少分号
    private static void printMissRbrackError(int lineNum) throws IOException {
        if (isDebugMode) {
            if (detailMode) {
                errorBufferedWriter.write("Error: MissRbrackError: " + lineNum);
            } else {
                errorBufferedWriter.write(lineNum + " k");
            }
            errorBufferedWriter.newLine();
        }
    }

    // j. 语句缺少右小括号
    private static void printMissRparentError(int lineNum) throws IOException {
        if (isDebugMode) {
            if (detailMode) {
                errorBufferedWriter.write("Error: MissRparentError: " + lineNum);
            } else {
                errorBufferedWriter.write(lineNum + " j");
            }
            errorBufferedWriter.newLine();
        }
    }

    // k. 语句缺少右中括号
    private static void printMissSemicnError(int lineNum) throws IOException {
        if (isDebugMode) {
            if (detailMode) {
                errorBufferedWriter.write("Error: MissSemicnError: " + lineNum);
            } else {
                errorBufferedWriter.write(lineNum + " i");
            }
            errorBufferedWriter.newLine();
        }
    }

    // l. 语句缺少右大括号
    private static void printUnmatchTokenNumError(int lineNum) throws IOException {
        if (isDebugMode) {
            if (detailMode) {
                errorBufferedWriter.write("Error: UnmatchTokenNumError: " + lineNum);
            } else {
                errorBufferedWriter.write(lineNum + " l");
            }
            errorBufferedWriter.newLine();
        }
    }

    // m. 非循环语句中出现break或continue语句
    private static void printUnmatchLoopError(int lineNum) throws IOException {
        if (isDebugMode) {
            if (detailMode) {
                errorBufferedWriter.write("Error: UnmatchLoopError: " + lineNum);
            } else {
                errorBufferedWriter.write(lineNum + " m");
            }
            errorBufferedWriter.newLine();
        }
    }

    public static void printLexicalWordCheckPrintError(int lineNum, String c) throws IOException {
        if (isDebugMode && detailMode) {
            errorBufferedWriter.write("error in " + lineNum +
                    " line,word:" + c + " can not be recognized");
            errorBufferedWriter.newLine();
        }
    }

    public static void printSyntaxAnalysisError(String grammarType) throws IOException {
        if (isDebugMode && detailMode) {
            errorBufferedWriter.write("grammarType:" + grammarType);
            errorBufferedWriter.newLine();
        }
    }

    public static void printDeclPrintError() throws IOException {
        if (isDebugMode && detailMode) {
            errorBufferedWriter.write("Error: Decl: "
                    + AstRecursion.getPreSymToken().getLineNum() +
                    ": preSymToken is " + AstRecursion.getPreSymToken().getWord());
            errorBufferedWriter.newLine();
        }
    }

    public static void printFuncDefPrintError() throws IOException {
        if (isDebugMode && detailMode) {
            errorBufferedWriter.write("Error: FuncDef: "
                    + AstRecursion.getPreSymToken().getLineNum() +
                    ": preSymToken is " + AstRecursion.getPreSymToken().getWord());
            errorBufferedWriter.newLine();
        }
    }

    public static void printMainFuncDefPrintError() throws IOException {
        if (isDebugMode && detailMode) {
            errorBufferedWriter.write("Error: MainFuncDef: "
                    + AstRecursion.getPreSymToken().getLineNum() +
                    ": preSymToken is " + AstRecursion.getPreSymToken().getWord());
            errorBufferedWriter.newLine();
        }
    }

    public static void printDefinerPrintError() throws IOException {
        if (isDebugMode && detailMode) {
            errorBufferedWriter.write("Error: Definer: BlockItem: "
                    + AstRecursion.getPreSymToken().getLineNum());
            errorBufferedWriter.newLine();
        }
    }

    public static void printLexicalAnalysisPrintError(int lineNum, String word) throws IOException {
        if (isDebugMode && detailMode) {
            errorBufferedWriter.write("error in " + lineNum + " line,word:"
                    + word + " is not a reserved word");
            errorBufferedWriter.newLine();
        }
    }

    public static void printSymbolTable() {
        if (isDebugMode && detailMode) {
            SymbolTable.printSymbolTable();
        }
    }

    public static void printStackOverflowError() {
        if (isDebugMode && detailMode) {
            try {
                errorBufferedWriter.write("Stack pointer overflow");
                errorBufferedWriter.newLine();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
