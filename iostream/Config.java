package iostream;

import iostream.structure.DebugDetailController;
import iostream.structure.ErrorController;
import iostream.structure.OutputController;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Config 是用于配置的类
 * 配置了各类文件流及输出模式等开关设置
 */
public class Config {
    /**
     * initSettings 方法用于初始化设置
     * 主要对各类接口设置
     */
    private static void initSettings() {
        Config.setFileInputMode(true);
        Config.setDetailMode(false);
        Config.setGenerationMode(true);
        Config.setIsCalcMode(false);
        Config.setIsDebugMode(true);
        Config.setIsLexerOutput(true);
        Config.setIsParserOutput(true);
    }

    /**
     * Compiler设置参数
     * fileInputMode 是否从文件输入
     * detailMode 是否输出错误输出的详细信息
     * generationMode 是否生成中间代码
     * IsCalcMode 是否计算全局变量表达式的值
     * IsDebugMode 是否开启错误处理模式
     * IsLexerOutput 是否输出词法分析结果
     * IsParserOutput 是否输出语法分析结果
     * IsGenerationOutput 是否输出中间代码
     * IsOptimize 是否开启优化器
     * IsGenerationOptimizerOutput 是否输出优化后的中间代码
     * IsAssemblyOutput 是否输出汇编代码
     * IsDebugDetailOutput 是否输出优化过程的详细信息
     */
    private static boolean fileInputMode;
    private static boolean detailMode;
    private static boolean generationMode;
    private static boolean IsCalcMode;
    private static boolean IsDebugMode;
    private static boolean IsLexerOutput;
    private static boolean IsParserOutput;
    private static boolean IsGenerationOutput;
    private static boolean IsOptimize;
    private static boolean IsGenerationOptimizerOutput;
    private static boolean IsAssemblyOutput;
    private static boolean IsDebugDetailOutput;
    private static BufferedReader fileInputStream;
    private static BufferedWriter errorOutputStream;
    private static BufferedWriter lexerOutputStream;
    private static BufferedWriter parserOutputStream;
    private static BufferedWriter generationOutputStream;
    private static BufferedWriter generationOptimizerOutputStream;
    private static BufferedWriter assemblyOutputStream;
    private static BufferedWriter debugDetailOutputStream;

    /**
     * 编译器初始化程序设置，定义文件输出路径
     * 及优化器初始化设置
     */
    public static void compilerInit() throws IOException {
        Config.initSettings();
        // 优化器设置初始化
        OptimizerUnit.initOptimizerSetting();
        // 将文件进行重定向
        Config.setFileInputStream(Config.isFileInputMode() ? new BufferedReader(
                new FileReader("testfile.txt")) :
                new BufferedReader(new InputStreamReader(System.in)));
        Config.setErrorOutputStream(Config.isIsDebugMode() ? new BufferedWriter(
                new FileWriter("error.txt", false)) : null);
        Config.setLexerOutputStream(Config.isIsLexerOutput() ? new BufferedWriter(
                new FileWriter("lexer_output.txt", false)) : null);
        Config.setParserOutputStream(Config.isIsParserOutput() ? new BufferedWriter(
                new FileWriter("parser_output.txt", false)) : null);
        Config.setGenerationOutputStream(Config.isIsGenerationOutput() ? new BufferedWriter(
                new FileWriter("llvm_ir.txt", false)) : null);
        Config.setGenerationOptimizerOutputStream(
                Config.isIsGenerationOptimizerOutput() ? new BufferedWriter(
                        new FileWriter("llvm_ir.txt", false)) : null);
        Config.setAssemblyOutputStream(Config.isIsAssemblyOutput() ? new BufferedWriter(
                new FileWriter("mips.txt", false)) : null);
        Config.setDebugDetailOutputStream(Config.isIsDebugDetailOutput() ? new BufferedWriter(
                new FileWriter("debug_detail.txt", false)) : null);
        // 错误处理初始化
        ErrorController.setBufferedWriter(Config.getErrorOutputStream());
        ErrorController.setIsDebugMode(Config.isIsDebugMode());
        ErrorController.setDetailMode(Config.isDetailMode());
        OutputController.setIsCalcMode(Config.isIsCalcMode());
        // 输出处理初始化
        OutputController.setBufferedLexerWriter(Config.getLexerOutputStream());
        OutputController.setBufferedParserWriter(Config.getParserOutputStream());
        OutputController.setBufferedGenerationWriter(Config.getGenerationOutputStream());
        OutputController.setBufferedGenerationOptimizerWriter(
                Config.getGenerationOptimizerOutputStream());
        OutputController.setBufferedAssemblyWriter(Config.getAssemblyOutputStream());
        OutputController.setLexerOutput(Config.isIsLexerOutput());
        OutputController.setParserOutput(Config.isIsParserOutput());
        OutputController.setGenerationOutput(Config.isIsGenerationOutput());
        OutputController.setGenerationOptimizerOutput(Config.isIsGenerationOptimizerOutput());
        OutputController.setAssemblyOutput(Config.isIsAssemblyOutput());
        // 具体信息输出初始化
        DebugDetailController.setBufferedWriter(Config.getDebugDetailOutputStream());
        DebugDetailController.setIsDebugDetailOutput(Config.isIsDebugDetailOutput());
    }

    /**
     * 编译器结束程序设置，关闭文件流
     */
    public static void compilerEnd() throws IOException {
        // 关闭文件流
        Config.getFileInputStream().close();
        if (Config.isIsDebugMode()) {
            Config.getErrorOutputStream().close();
        }
        if (Config.isIsLexerOutput()) {
            Config.getLexerOutputStream().close();
        }
        if (Config.isIsParserOutput()) {
            Config.getParserOutputStream().close();
        }
        if (Config.isIsGenerationOutput()) {
            Config.getGenerationOutputStream().close();
        }
        if (Config.isIsGenerationOptimizerOutput()) {
            Config.getGenerationOptimizerOutputStream().close();
        }
        if (Config.isIsAssemblyOutput()) {
            Config.getAssemblyOutputStream().close();
        }
        if (Config.isIsDebugDetailOutput()) {
            Config.getDebugDetailOutputStream().close();
        }
    }

    public static boolean isFileInputMode() {
        return fileInputMode;
    }

    public static void setFileInputMode(boolean fileInputMode) {
        Config.fileInputMode = fileInputMode;
    }

    public static boolean isDetailMode() {
        return detailMode;
    }

    public static void setDetailMode(boolean detailMode) {
        Config.detailMode = detailMode;
    }

    public static boolean isGenerationMode() {
        return generationMode;
    }

    public static void setGenerationMode(boolean generationMode) {
        Config.generationMode = generationMode;
    }

    public static boolean isIsCalcMode() {
        return IsCalcMode;
    }

    public static void setIsCalcMode(boolean isCalcMode) {
        IsCalcMode = isCalcMode;
    }

    public static boolean isIsDebugMode() {
        return IsDebugMode;
    }

    public static void setIsDebugMode(boolean isDebugMode) {
        IsDebugMode = isDebugMode;
    }

    public static boolean isIsLexerOutput() {
        return IsLexerOutput;
    }

    public static void setIsLexerOutput(boolean isLexerOutput) {
        IsLexerOutput = isLexerOutput;
    }

    public static boolean isIsParserOutput() {
        return IsParserOutput;
    }

    public static void setIsParserOutput(boolean isParserOutput) {
        IsParserOutput = isParserOutput;
    }

    public static boolean isIsGenerationOutput() {
        return IsGenerationOutput;
    }

    public static void setIsGenerationOutput(boolean isGenerationOutput) {
        IsGenerationOutput = isGenerationOutput;
    }

    public static boolean isIsOptimize() {
        return IsOptimize;
    }

    public static void setIsOptimize(boolean isOptimize) {
        IsOptimize = isOptimize;
    }

    public static boolean isIsGenerationOptimizerOutput() {
        return IsGenerationOptimizerOutput;
    }

    public static void setIsGenerationOptimizerOutput(boolean isGenerationOptimizerOutput) {
        IsGenerationOptimizerOutput = isGenerationOptimizerOutput;
    }

    public static boolean isIsAssemblyOutput() {
        return IsAssemblyOutput;
    }

    public static void setIsAssemblyOutput(boolean isAssemblyOutput) {
        IsAssemblyOutput = isAssemblyOutput;
    }

    public static boolean isIsDebugDetailOutput() {
        return IsDebugDetailOutput;
    }

    public static void setIsDebugDetailOutput(boolean isDebugDetailOutput) {
        IsDebugDetailOutput = isDebugDetailOutput;
    }

    public static BufferedReader getFileInputStream() {
        return fileInputStream;
    }

    public static void setFileInputStream(BufferedReader fileInputStream) {
        Config.fileInputStream = fileInputStream;
    }

    public static BufferedWriter getErrorOutputStream() {
        return errorOutputStream;
    }

    public static void setErrorOutputStream(BufferedWriter errorOutputStream) {
        Config.errorOutputStream = errorOutputStream;
    }

    public static BufferedWriter getLexerOutputStream() {
        return lexerOutputStream;
    }

    public static void setLexerOutputStream(BufferedWriter lexerOutputStream) {
        Config.lexerOutputStream = lexerOutputStream;
    }

    public static BufferedWriter getParserOutputStream() {
        return parserOutputStream;
    }

    public static void setParserOutputStream(BufferedWriter parserOutputStream) {
        Config.parserOutputStream = parserOutputStream;
    }

    public static BufferedWriter getGenerationOutputStream() {
        return generationOutputStream;
    }

    public static void setGenerationOutputStream(BufferedWriter generationOutputStream) {
        Config.generationOutputStream = generationOutputStream;
    }

    public static BufferedWriter getGenerationOptimizerOutputStream() {
        return generationOptimizerOutputStream;
    }

    public static void setGenerationOptimizerOutputStream(
            BufferedWriter generationOptimizerOutputStream) {
        Config.generationOptimizerOutputStream = generationOptimizerOutputStream;
    }

    public static BufferedWriter getAssemblyOutputStream() {
        return assemblyOutputStream;
    }

    public static void setAssemblyOutputStream(BufferedWriter assemblyOutputStream) {
        Config.assemblyOutputStream = assemblyOutputStream;
    }

    public static BufferedWriter getDebugDetailOutputStream() {
        return debugDetailOutputStream;
    }

    public static void setDebugDetailOutputStream(BufferedWriter debugDetailOutputStream) {
        Config.debugDetailOutputStream = debugDetailOutputStream;
    }

}
