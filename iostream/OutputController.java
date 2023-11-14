package iostream;

import backend.generation.utils.AssemblyData;
import frontend.generation.lexer.SymToken;
import frontend.generation.syntax.AstNode;

import java.io.BufferedWriter;

/**
 * OutputController 是一个用于输出的控制类
 */
public class OutputController {
    /**
     * IsCalcMode 用于控制是否为计算模式(打开后会计算全局区变量的值)
     * IsLexerOutput 用于控制是否输出词法分析的结果
     * IsParserOutput 用于控制是否输出语法分析的结果
     * IsGenerationOutput 用于控制是否输出生成的LLVM IR
     * IsGenerationOptimizerOutput 用于控制是否输出优化后的LLVM IR
     * IsAssemblyOutput 用于控制是否输出生成的MIPS汇编
     * lexerOutputStream 用于输出词法分析的结果的输出流
     * parserOutputStream 用于输出语法分析的结果的输出流
     * generationOutputStream 用于输出生成的LLVM IR的输出流
     * generationOptimizerOutputStream 用于输出优化后的LLVM IR的输出流
     * assemblyOutputStream 用于输出生成的MIPS汇编的输出流
     */
    private static boolean IsCalcMode;
    private static BufferedWriter lexerOutputStream;
    private static boolean IsLexerOutput;
    private static BufferedWriter parserOutputStream;
    private static boolean IsParserOutput;
    private static BufferedWriter generationOutputStream;
    private static boolean IsGenerationOutput;
    private static BufferedWriter generationOptimizerOutputStream;
    private static boolean IsGenerationOptimizerOutput;

    private static BufferedWriter assemblyOutputStream;
    private static boolean IsAssemblyOutput;

    public static void setIsCalcMode(boolean isCalcMode) {
        IsCalcMode = isCalcMode;
    }

    public static boolean getIsCalcMode() {
        return IsCalcMode;
    }

    public static void setBufferedLexerWriter(BufferedWriter lexerOutputStream) {
        OutputController.lexerOutputStream = lexerOutputStream;
    }

    public static void setBufferedParserWriter(BufferedWriter parserOutputStream) {
        OutputController.parserOutputStream = parserOutputStream;
    }

    public static void setBufferedGenerationWriter(BufferedWriter generationOutputStream) {
        OutputController.generationOutputStream = generationOutputStream;
    }

    public static void setBufferedGenerationOptimizerWriter(
            BufferedWriter generationOptimizerOutputStream) {
        OutputController.generationOptimizerOutputStream = generationOptimizerOutputStream;
    }

    public static void setBufferedAssemblyWriter(BufferedWriter assemblyOutputStream) {
        OutputController.assemblyOutputStream = assemblyOutputStream;
    }

    public static void setLexerOutput(boolean isLexerOutput) {
        OutputController.IsLexerOutput = isLexerOutput;
    }

    public static void setParserOutput(boolean isParserOutput) {
        OutputController.IsParserOutput = isParserOutput;
    }

    public static void setGenerationOutput(boolean isGenerationOutput) {
        OutputController.IsGenerationOutput = isGenerationOutput;
    }

    public static void setGenerationOptimizerOutput(boolean isGenerationOptimizerOutput) {
        OutputController.IsGenerationOptimizerOutput = isGenerationOptimizerOutput;
    }

    public static void setAssemblyOutput(boolean isAssemblyOutput) {
        OutputController.IsAssemblyOutput = isAssemblyOutput;
    }

    /**
     * lexicalAnalysisPrint 是一个用于输出词法分析结果的函数
     */
    public static void lexicalAnalysisPrint(SymToken symToken) {
        if (IsLexerOutput) {
            try {
                lexerOutputStream.write(symToken.getReservedWord() + " " + symToken.getWord());
                lexerOutputStream.newLine();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * syntaxAnalysisPrintTerminal 是一个用于输出语法分析终结符的函数
     */
    public static void syntaxAnalysisPrintTerminal(AstNode rootAst) {
        if (IsParserOutput) {
            try {
                parserOutputStream.write(rootAst.getSymToken().getReservedWord()
                        + " " + rootAst.getSymToken().getWord());
                parserOutputStream.newLine();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * syntaxAnalysisPrintNoTerminal 是一个用于输出语法分析非终结符的函数
     */
    public static void syntaxAnalysisPrintNoTerminal(AstNode rootAst) {
        if (IsParserOutput) {
            try {
                parserOutputStream.write(rootAst.getGrammarType());
                parserOutputStream.newLine();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * generationPrint 是一个用于输出生成的LLVM IR的函数
     */
    public static void generationPrint(String str) {
        if (IsGenerationOutput) {
            try {
                generationOutputStream.write(str);
                generationOutputStream.newLine();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * generationOptimizerPrint 是一个用于输出优化后的LLVM IR的函数
     */
    public static void generationOptimizerPrint(String str) {
        if (IsGenerationOptimizerOutput) {
            try {
                generationOptimizerOutputStream.write(str);
                generationOptimizerOutputStream.newLine();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * assemblyPrint 是一个用于输出生成的MIPS汇编的函数
     */
    public static void assemblyPrint(AssemblyData assemblyData) {
        if (IsAssemblyOutput) {
            try {
                assemblyOutputStream.write(assemblyData.toString());
                assemblyOutputStream.newLine();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
