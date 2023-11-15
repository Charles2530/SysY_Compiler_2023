import backend.generation.AssemblyGeneration;
import backend.simplify.BackEndOptimizerUnit;
import frontend.generation.lexer.LexicalAnalysis;
import frontend.generation.semantic.SemanticAnalysis;
import frontend.generation.syntax.SyntaxAnalysis;
import frontend.simplify.FrontEndOptimizerUnit;
import iostream.Config;
import iostream.structure.ErrorController;
import iostream.structure.OutputController;
import midend.generation.GenerationMain;
import midend.simplify.MidEndOptimizerUnit;

import java.io.IOException;

/**
 * 编译器主程序
 */
public class Compiler {
    public static void main(String[] args) throws IOException {
        Config.compilerInit();
        // 词法分析
        String line;
        int lineNum = 0;
        LexicalAnalysis lexicalAnalysis = new LexicalAnalysis();
        while ((line = Config.getFileInputStream().readLine()) != null) {
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
        if (Config.isGenerationMode() && !ErrorController.hasError()) {
            // 生成中间代码
            GenerationMain generationMain = new GenerationMain(syntaxAnalysis.getAst());
            generationMain.generate();
            // 优化中间代码
            if (Config.isIsOptimize()) {
                // 前端优化
                FrontEndOptimizerUnit frontEndOptimizerUnit =
                        new FrontEndOptimizerUnit(GenerationMain.getModule());
                frontEndOptimizerUnit.optimize();
                // 中端优化
                MidEndOptimizerUnit midEndOptimizerUnit =
                        new MidEndOptimizerUnit(GenerationMain.getModule());
                midEndOptimizerUnit.optimize();
                // 重新生成中间代码
                OutputController.generationOptimizerPrint(GenerationMain.getModule().toString());
                // 后端优化
                if (Config.isIsAssemblyOutput()) {
                    BackEndOptimizerUnit backEndOptimizerUnit =
                            new BackEndOptimizerUnit(GenerationMain.getModule());
                    backEndOptimizerUnit.optimize();
                }
            }
            // 生成汇编代码
            if (Config.isIsAssemblyOutput()) {
                AssemblyGeneration assemblyGeneration =
                        new AssemblyGeneration(GenerationMain.getModule());
                assemblyGeneration.generate();
            }
        }
        Config.compilerEnd();
    }
}