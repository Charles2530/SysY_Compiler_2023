import backend.AssemblyGeneration;
import frontend.lexer.LexicalAnalysis;
import frontend.semantic.SemanticAnalysis;
import frontend.syntax.SyntaxAnalysis;
import iostream.ErrorController;
import iostream.OutputController;
import midend.generation.GenerationMain;
import midend.simplify.OptimizerUnit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class Compiler {
    private static boolean fileInputMode = true;
    private static boolean detailMode = false;
    private static boolean generationMode = true;
    private static boolean IsCalcMode = false;
    private static boolean IsDebugMode = false;
    private static boolean IsLexerOutput = false;
    private static boolean IsParserOutput = false;
    private static boolean IsGenerationOutput = true;
    private static boolean IsOptimize = false;
    private static boolean IsAssemblyOutput = false;
    private static BufferedReader fileInputStream = null;
    private static BufferedWriter errorOutputStream = null;
    private static BufferedWriter lexerOutputStream = null;
    private static BufferedWriter parserOutputStream = null;
    private static BufferedWriter generationOutputStream = null;
    private static BufferedWriter assemblyOutputStream = null;

    public static void main(String[] args) throws IOException {
        compilerInit();
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
        if (generationMode) {
            // 生成中间代码
            GenerationMain generationMain = new GenerationMain(syntaxAnalysis.getAst());
            generationMain.generate();
            // 优化中间代码
            if (IsOptimize) {
                OptimizerUnit optimizerUnit = new OptimizerUnit(GenerationMain.getModule());
                optimizerUnit.optimize();
            }
            // 生成汇编代码
            AssemblyGeneration assemblyGeneration =
                    new AssemblyGeneration(GenerationMain.getModule());
            assemblyGeneration.generate();
        }
        compilerEnd();
    }

    private static void compilerInit() throws IOException {
        // 将文件进行重定向
        fileInputStream = fileInputMode ? new BufferedReader(
                new FileReader("testfile.txt")) :
                new BufferedReader(new InputStreamReader(System.in));
        errorOutputStream = IsDebugMode ? new BufferedWriter(
                new FileWriter("error.txt", false)) : null;
        lexerOutputStream = IsLexerOutput ? new BufferedWriter(
                new FileWriter("output.txt", false)) : null;
        parserOutputStream = IsParserOutput ? new BufferedWriter(
                new FileWriter("output.txt", false)) : null;
        generationOutputStream = IsGenerationOutput ? new BufferedWriter(
                new FileWriter("llvm_ir.txt", false)) : null;
        assemblyOutputStream = IsAssemblyOutput ? new BufferedWriter(
                new FileWriter("mips.txt", false)) : null;
        // 错误处理初始化
        ErrorController.setBufferedWriter(errorOutputStream);
        ErrorController.setIsDebugMode(IsDebugMode);
        ErrorController.setDetailMode(detailMode);
        OutputController.setIsCalcMode(IsCalcMode);
        // 输出处理初始化
        OutputController.setBufferedLexerWriter(lexerOutputStream);
        OutputController.setBufferedParserWriter(parserOutputStream);
        OutputController.setBufferedGenerationWriter(generationOutputStream);
        OutputController.setBufferedAssemblyWriter(assemblyOutputStream);
        OutputController.setLexerOutput(IsLexerOutput);
        OutputController.setParserOutput(IsParserOutput);
        OutputController.setGenerationOutput(IsGenerationOutput);
        OutputController.setAssemblyOutput(IsAssemblyOutput);
    }

    private static void compilerEnd() throws IOException {
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
        if (IsAssemblyOutput) {
            assemblyOutputStream.close();
        }
    }
}