import generation.GenerationMain;
import iostream.ErrorController;
import iostream.OutputController;
import lexer.LexicalAnalysis;
import semantic.SemanticAnalysis;
import syntax.SyntaxAnalysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class Compiler {
    private static boolean fileInputMode = true;
    private static boolean detailMode = false;
    private static boolean IsCalcMode = false;
    private static boolean IsDebugMode = false;
    private static boolean IsLexerOutput = false;
    private static boolean IsParserOutput = false;
    private static boolean IsGenerationOutput = true;

    public static void main(String[] args) throws IOException {
        // 将文件进行重定向
        BufferedReader fileInputStream = fileInputMode ? new BufferedReader(
                new FileReader("testfile.txt")) :
                new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter errorOutputStream = IsDebugMode ? new BufferedWriter(
                new FileWriter("error.txt", false)) : null;
        BufferedWriter lexerOutputStream = IsLexerOutput ? new BufferedWriter(
                new FileWriter("output.txt", false)) : null;
        BufferedWriter parserOutputStream = IsParserOutput ? new BufferedWriter(
                new FileWriter("output.txt", false)) : null;
        BufferedWriter generationOutputStream = IsGenerationOutput ? new BufferedWriter(
                new FileWriter("llvm_ir.txt", false)) : null;
        // 错误处理初始化
        ErrorController.setBufferedWriter(errorOutputStream);
        ErrorController.setIsDebugMode(IsDebugMode);
        ErrorController.setDetailMode(detailMode);
        OutputController.setIsCalcMode(IsCalcMode);
        // 输出处理初始化
        OutputController.setBufferedLexerWriter(lexerOutputStream);
        OutputController.setBufferedParserWriter(parserOutputStream);
        OutputController.setBufferedGenerationWriter(generationOutputStream);
        OutputController.setLexerOutput(IsLexerOutput);
        OutputController.setParserOutput(IsParserOutput);
        OutputController.setGenerationOutput(IsGenerationOutput);
        // 词法分析
        String line;
        int lineNum = 0;
        LexicalAnalysis lexicalAnalysis = new LexicalAnalysis();
        while ((line = fileInputStream.readLine()) != null) {
            lineNum++;
            lexicalAnalysis.analysis(line, lineNum);
        }
        // 语法分析
        SyntaxAnalysis syntaxAnalysis = new SyntaxAnalysis(
                lexicalAnalysis.getSymTokens());
        syntaxAnalysis.analysis();
        // 语义分析
        SemanticAnalysis semanticAnalysis = new SemanticAnalysis(syntaxAnalysis.getAst());
        semanticAnalysis.analysis();
        // 生成中间代码
        GenerationMain generationMain = new GenerationMain(syntaxAnalysis.getAst());
        generationMain.generate();
        // 关闭文件流
        fileInputStream.close();
        if (IsDebugMode) {
            errorOutputStream.close();
        }
        if (IsLexerOutput) {
            lexerOutputStream.close();
        }
        if (IsParserOutput) {
            parserOutputStream.close();
        }
        if (IsGenerationOutput) {
            generationOutputStream.close();
        }
    }
}