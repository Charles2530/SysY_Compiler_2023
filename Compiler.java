import LLVM.LLvmMain;
import Lexer.LexicalAnalysis;
import Syntax.SyntaxAnalysis;

import java.io.*;

public class Compiler {
    private static final boolean IsDebugMode = false;
    private static final boolean IsLexerOutput = false;
    private static final boolean IsParserOutput = true;

    public static void main(String[] args) throws IOException {
        // 将文件进行重定向
        BufferedReader fileInputStream = new BufferedReader(new FileReader("testfile.txt"));
        BufferedWriter lexerOutputStream = IsLexerOutput ? new BufferedWriter(
                new FileWriter("LexerOutput.txt", false)) : null;
        BufferedWriter parserOutputStream = IsParserOutput ? new BufferedWriter(
                new FileWriter("output.txt", false)) : null;
        String line;
        int lineNum = 0;
        // 词法分析初始化
        LexicalAnalysis lexicalAnalysis = new LexicalAnalysis(
                lexerOutputStream, IsDebugMode, IsLexerOutput);
        while ((line = fileInputStream.readLine()) != null) {
            // 对每一行进行词法分析
            lineNum++;
            lexicalAnalysis.analysis(line, lineNum);
        }
        // 语法分析初始化
        SyntaxAnalysis syntaxAnalysis = new SyntaxAnalysis(parserOutputStream,
                lexicalAnalysis.getSymTokens(), IsDebugMode, IsParserOutput);
        syntaxAnalysis.analysis();
        // 生成中间代码
        LLvmMain llvmMain = new LLvmMain(syntaxAnalysis.getAst(), IsDebugMode);
        llvmMain.generate();
        // 关闭文件流
        fileInputStream.close();
        if (IsLexerOutput) {
            lexerOutputStream.close();
        }
        if (IsParserOutput) {
            parserOutputStream.close();
        }
    }
}