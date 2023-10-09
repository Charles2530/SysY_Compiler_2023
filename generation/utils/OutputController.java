package generation.utils;

import lexer.SymToken;
import syntax.AstNode;

import java.io.BufferedWriter;
import java.io.IOException;

public class OutputController {
    private static BufferedWriter lexerOutputStream;
    private static boolean IsLexerOutput;
    private static BufferedWriter parserOutputStream;
    private static boolean IsParserOutput;
    private static BufferedWriter generationOutputStream;
    private static boolean IsGenerationOutput;

    public static void LexicalAnalysisPrint(SymToken symToken) throws IOException {
        if (IsLexerOutput) {
            lexerOutputStream.write(symToken.getReservedWord() + " " + symToken.getWord());
            lexerOutputStream.newLine();
        }
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

    public static void setLexerOutput(boolean isLexerOutput) {
        OutputController.IsLexerOutput = isLexerOutput;
    }

    public static void setParserOutput(boolean isParserOutput) {
        OutputController.IsParserOutput = isParserOutput;
    }

    public static void setGenerationOutput(boolean isGenerationOutput) {
        OutputController.IsGenerationOutput = isGenerationOutput;
    }

    public static void SyntaxAnalysisPrintTerminal(AstNode rootAst) throws IOException {
        if (IsParserOutput) {
            try {
                parserOutputStream.write(rootAst.getSymToken().getReservedWord()
                        + " " + rootAst.getSymToken().getWord());
                parserOutputStream.newLine();
            } catch (Exception e) {
                System.err.println(rootAst.getGrammarType());
                e.printStackTrace();
            }
        }
    }

    public static void SyntaxAnalysisPrintNoTerminal(AstNode rootAst) throws IOException {
        if (IsParserOutput) {
            try {
                parserOutputStream.write(rootAst.getGrammarType());
                parserOutputStream.newLine();
            } catch (Exception e) {
                System.err.println(rootAst.getSymToken().getWord());
                e.printStackTrace();
            }
        }
    }

    public static void GenerationPrint(String str) throws IOException {
        if (IsGenerationOutput) {
            generationOutputStream.write(str);
            generationOutputStream.newLine();
        }
    }

}
