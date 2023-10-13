package iostream;

import semantic.symtable.SymbolTable;
import syntax.AstRecursion;

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

    private static void printError(ErrorToken error) throws IOException {
        switch (error.getErrorCategoryCode()) {
            case "a":
                printIllegalTokenError(error.getLineNum(), "a");
                break;
            case "b":
                printRedefinitionError(error.getLineNum(), "b");
                break;
            case "c":
                printUndefinedNameError(error.getLineNum(), "c");
                break;
            case "d":
                printFuncNumUnmatchError(error.getLineNum(), "d");
                break;
            case "e":
                printFuncTypeUnmatchError(error.getLineNum(), "e");
                break;
            case "f":
                printUnmatchReturnError(error.getLineNum(), "f");
                break;
            case "g":
                printMissingReturnError(error.getLineNum(), "g");
                break;
            case "h":
                printAssignTypeConsError(error.getLineNum(), "h");
                break;
            case "i":
                printMissSemicnError(error.getLineNum(), "i");
                break;
            case "j":
                printMissRparentError(error.getLineNum(), "j");
                break;
            case "k":
                printMissRbrackError(error.getLineNum(), "k");
                break;
            case "l":
                printUnmatchTokenNumError(error.getLineNum(), "l");
                break;
            case "m":
                printUnmatchLoopError(error.getLineNum(), "m");
                break;
            default:
                break;
        }
    }

    // a. 非法符号
    private static void printIllegalTokenError(int lineNum, String type) throws IOException {
        if (isDebugMode) {
            if (detailMode) {
                errorBufferedWriter.write("Error: IllegalTokenError: " + lineNum);
            } else {
                errorBufferedWriter.write(lineNum + " " + type);
            }
            errorBufferedWriter.newLine();
        }
    }

    // b. 名字重定义
    private static void printRedefinitionError(int lineNum, String type) throws IOException {
        if (isDebugMode) {
            if (detailMode) {
                errorBufferedWriter.write("Error: RedefinitionError: " + lineNum);
            } else {
                errorBufferedWriter.write(lineNum + " " + type);
            }
            errorBufferedWriter.newLine();
        }
    }

    // c. 未定义的名字
    private static void printUndefinedNameError(int lineNum, String type) throws IOException {
        if (isDebugMode) {
            if (detailMode) {
                errorBufferedWriter.write("Error: UndefinedNameError: " + lineNum);
            } else {
                errorBufferedWriter.write(lineNum + " " + type);
            }
            errorBufferedWriter.newLine();
        }
    }

    // d. 函数参数个数不匹配
    private static void printFuncNumUnmatchError(int lineNum, String type) throws IOException {
        if (isDebugMode) {
            if (detailMode) {
                errorBufferedWriter.write("Error: FuncNumUnmatchError: " + lineNum);
            } else {
                errorBufferedWriter.write(lineNum + " " + type);
            }
            errorBufferedWriter.newLine();
        }
    }

    // e. 函数参数类型不匹配
    private static void printFuncTypeUnmatchError(int lineNum, String type) throws IOException {
        if (isDebugMode) {
            if (detailMode) {
                errorBufferedWriter.write("Error: FuncTypeUnmatchError: " + lineNum);
            } else {
                errorBufferedWriter.write(lineNum + " " + type);
            }
            errorBufferedWriter.newLine();
        }
    }

    // f. 无返回值的函数存在不匹配的return语句
    private static void printUnmatchReturnError(int lineNum, String type) throws IOException {
        if (isDebugMode) {
            if (detailMode) {
                errorBufferedWriter.write("Error: UnmatchReturnError: " + lineNum);
            } else {
                errorBufferedWriter.write(lineNum + " " + type);
            }
            errorBufferedWriter.newLine();
        }
    }

    // g. 有返回值的函数缺少return语句或存在不匹配的return语句
    private static void printMissingReturnError(int lineNum, String type) throws IOException {
        if (isDebugMode) {
            if (detailMode) {
                errorBufferedWriter.write("Error: MissingReturnError: " + lineNum);
            } else {
                errorBufferedWriter.write(lineNum + " " + type);
            }
            errorBufferedWriter.newLine();
        }
    }

    // h. 不能改变常量的值
    private static void printAssignTypeConsError(int lineNum, String type) throws IOException {
        if (isDebugMode) {
            if (detailMode) {
                errorBufferedWriter.write("Error: AssignTypeConsError: " + lineNum);
            } else {
                errorBufferedWriter.write(lineNum + " " + type);
            }
            errorBufferedWriter.newLine();
        }
    }

    //  i. 语句缺少分号
    private static void printMissRbrackError(int lineNum, String type) throws IOException {
        if (isDebugMode) {
            if (detailMode) {
                errorBufferedWriter.write("Error: MissRbrackError: " + lineNum);
            } else {
                errorBufferedWriter.write(lineNum + " " + type);
            }
            errorBufferedWriter.newLine();
        }
    }

    // j. 语句缺少右小括号
    private static void printMissRparentError(int lineNum, String type) throws IOException {
        if (isDebugMode) {
            if (detailMode) {
                errorBufferedWriter.write("Error: MissRparentError: " + lineNum);
            } else {
                errorBufferedWriter.write(lineNum + " " + type);
            }
            errorBufferedWriter.newLine();
        }
    }

    // k. 语句缺少右中括号
    private static void printMissSemicnError(int lineNum, String type) throws IOException {
        if (isDebugMode) {
            if (detailMode) {
                errorBufferedWriter.write("Error: MissSemicnError: " + lineNum);
            } else {
                errorBufferedWriter.write(lineNum + " " + type);
            }
            errorBufferedWriter.newLine();
        }
    }

    // l. 语句缺少右大括号
    private static void printUnmatchTokenNumError(int lineNum, String type) throws IOException {
        if (isDebugMode) {
            if (detailMode) {
                errorBufferedWriter.write("Error: UnmatchTokenNumError: " + lineNum);
            } else {
                errorBufferedWriter.write(lineNum + " " + type);
            }
            errorBufferedWriter.newLine();
        }
    }

    // m. 非循环语句中出现break或continue语句
    private static void printUnmatchLoopError(int lineNum, String type) throws IOException {
        if (isDebugMode) {
            if (detailMode) {
                errorBufferedWriter.write("Error: UnmatchLoopError: " + lineNum);
            } else {
                errorBufferedWriter.write(lineNum + " " + type);
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
}