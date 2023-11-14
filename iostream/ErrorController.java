package iostream;

import frontend.generation.semantic.symtable.SymbolTable;
import frontend.generation.syntax.AstRecursion;
import iostream.structure.ErrorToken;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * ErrorController 是错误处理的控制类
 * 用于输出错误信息
 */
public class ErrorController {
    /**
     * errorlist 是错误列表
     * errorBufferedWriter 是错误输出流
     * isDebugMode 是用于判断是否开启错误处理
     * detailMode 是用于判断是否开启详细错误处理
     */
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

    /**
     * addError 是用于添加错误的函数
     */
    public static void addError(ErrorToken error) {
        errorlist.add(error);
    }

    /**
     * printErrors 是用于输出错误的函数
     * 对于每行仅输出第一个错误
     */
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

    /**
     * hasError 是用于判断是否有错误的函数
     * 这里可以用于判断此次语法分析是否存在错误，如果出错则
     * 不会进入中间代码生成程序
     */
    public static boolean hasError() {
        return isDebugMode && !errorlist.isEmpty();
    }

    /**
     * printError 是用于输出错误的函数
     * 根据错误类型输出不同的错误
     *
     * @param error 错误标识符
     */
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

    /**
     * printIllegalTokenError 是用于输出非法符号的错误(a)
     */
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

    /**
     * printRedefinitionError 是用于输出名字重定义的错误(b)
     */
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

    /**
     * printUndefinedNameError 是用于输出未定义名字的错误(c)
     */
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

    /**
     * printFuncNumUnmatchError 是用于输出函数参数个数不匹配的错误(d)
     */
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

    /**
     * printFuncTypeUnmatchError 是用于输出函数参数类型不匹配的错误(e)
     */
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

    /**
     * printUnmatchReturnError 是用于输出无返回值的函数存在不匹配的return语句的错误(f)
     */
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

    /**
     * printMissingReturnError 是用于输出有返回值的函数缺少return语句或存在不匹配的return语句的错误(g)
     */
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

    /**
     * printAssignTypeConsError 是用于输出不能改变常量的值的错误(h)
     */
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

    /**
     * printMissRbrackError 是用于输出语句缺少右中括号的错误(k)
     */
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

    /**
     * printMissRparentError 是用于输出语句缺少右小括号的错误(j)
     */
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

    /**
     * printMissSemicnError 是用于输出语句缺少分号的错误(i)
     */
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

    /**
     * printUnmatchTokenNumError 是用于输出语句缺少右大括号的错误(l)
     */
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

    /**
     * printUnmatchLoopError 是用于输出非循环语句中出现break或continue语句的错误(m)
     */
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

    /**
     * printLexicalWordCheckPrintError 是用于输出词法分析检查word错误的函数
     */
    public static void printLexicalWordCheckPrintError(int lineNum, String c) throws IOException {
        if (isDebugMode && detailMode) {
            errorBufferedWriter.write("error in " + lineNum +
                    " line,word:" + c + " can not be recognized");
            errorBufferedWriter.newLine();
        }
    }

    /**
     * printSyntaxAnalysisError 是用于输出语法分析错误的函数
     */
    public static void printSyntaxAnalysisError(String grammarType) throws IOException {
        if (isDebugMode && detailMode) {
            errorBufferedWriter.write("grammarType:" + grammarType);
            errorBufferedWriter.newLine();
        }
    }

    /**
     * printDeclPrintError 是用于输出声明语句错误的函数
     */
    public static void printDeclPrintError() throws IOException {
        if (isDebugMode && detailMode) {
            errorBufferedWriter.write("Error: Decl: "
                    + AstRecursion.getPreSymToken().getLineNum() +
                    ": preSymToken is " + AstRecursion.getPreSymToken().getWord());
            errorBufferedWriter.newLine();
        }
    }

    /**
     * printFuncDefPrintError 是用于输出函数定义错误的函数
     */
    public static void printFuncDefPrintError() throws IOException {
        if (isDebugMode && detailMode) {
            errorBufferedWriter.write("Error: FuncDef: "
                    + AstRecursion.getPreSymToken().getLineNum() +
                    ": preSymToken is " + AstRecursion.getPreSymToken().getWord());
            errorBufferedWriter.newLine();
        }
    }

    /**
     * printMainFuncDefPrintError 是用于输出主函数定义错误的函数
     */
    public static void printMainFuncDefPrintError() throws IOException {
        if (isDebugMode && detailMode) {
            errorBufferedWriter.write("Error: MainFuncDef: "
                    + AstRecursion.getPreSymToken().getLineNum() +
                    ": preSymToken is " + AstRecursion.getPreSymToken().getWord());
            errorBufferedWriter.newLine();
        }
    }

    /**
     * printDefinerPrintError 是用于输出Definer定义类错误的函数
     */
    public static void printDefinerPrintError() throws IOException {
        if (isDebugMode && detailMode) {
            errorBufferedWriter.write("Error: Definer: BlockItem: "
                    + AstRecursion.getPreSymToken().getLineNum());
            errorBufferedWriter.newLine();
        }
    }

    /**
     * printLexicalAnalysisPrintError 是用于输出词法分析错误的函数
     */
    public static void printLexicalAnalysisPrintError(int lineNum, String word) throws IOException {
        if (isDebugMode && detailMode) {
            errorBufferedWriter.write("error in " + lineNum + " line,word:"
                    + word + " is not a reserved word");
            errorBufferedWriter.newLine();
        }
    }

    /**
     * printSymbolTable 是用于输出符号表的函数
     */
    public static void printSymbolTable() {
        if (isDebugMode && detailMode) {
            SymbolTable.printSymbolTable();
        }
    }

    /**
     * printStackOverflowError 是用于输出栈溢出错误的函数
     */
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
