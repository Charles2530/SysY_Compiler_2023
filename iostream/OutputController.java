package iostream;

import backend.generation.utils.AssemblyData;
import frontend.lexer.SymToken;
import frontend.syntax.AstNode;

import java.io.BufferedWriter;
import java.io.IOException;

public class OutputController {
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

    public static void lexicalAnalysisPrint(SymToken symToken) throws IOException {
        if (IsLexerOutput) {
            lexerOutputStream.write(symToken.getReservedWord() + " " + symToken.getWord());
            lexerOutputStream.newLine();
        }
    }

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
