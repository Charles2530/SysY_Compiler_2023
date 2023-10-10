import generation.utils.ErrorController;
import generation.GenerationMain;
import generation.utils.OutputController;
import lexer.LexicalAnalysis;
import semantic.SemanticAnalysis;
import syntax.SyntaxAnalysis;

import java.io.*;

public class Compiler {
    private static final boolean IsDebugMode = true;
    private static boolean detailMode = false;
    private static final boolean IsLexerOutput = false;
    private static final boolean IsParserOutput = true;
    private static final boolean IsGenerationOutput = false;

    public static void main(String[] args) throws IOException {
        // 将文件进行重定向
        BufferedReader fileInputStream = new BufferedReader(new FileReader("testfile.txt"));
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
            // 对每一行进行词法分析
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