package frontend.generation.lexer;

import iostream.ErrorController;
import iostream.OutputController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class LexicalAnalysis {
    /**
     * reservedWords 用于存储保留字
     * symTokens 用于存储词法分析的结果
     * lexicalWordCheck 用于进行词法分析
     */
    private final HashMap<String, String> reservedWords = new HashMap<>();
    private final ArrayList<SymToken> symTokens = new ArrayList<>();
    private final LexicalWordCheck lexicalWordCheck = new LexicalWordCheck();

    public ArrayList<SymToken> getSymTokens() {
        return symTokens;
    }

    public LexicalAnalysis() {
        this.initial();
        lexicalWordCheck.initial();
    }

    /**
     * reservedWords 初始化
     */
    public void initial() {
        reservedWords.put("main", "MAINTK");
        reservedWords.put("const", "CONSTTK");
        reservedWords.put("int", "INTTK");
        reservedWords.put("break", "BREAKTK");
        reservedWords.put("continue", "CONTINUETK");
        reservedWords.put("if", "IFTK");
        reservedWords.put("else", "ELSETK");
        reservedWords.put("for", "FORTK");
        reservedWords.put("getint", "GETINTTK");
        reservedWords.put("printf", "PRINTFTK");
        reservedWords.put("return", "RETURNTK");
        reservedWords.put("void", "VOIDTK");
        reservedWords.put("+", "PLUS");
        reservedWords.put("-", "MINU");
        reservedWords.put("*", "MULT");
        reservedWords.put("%", "MOD");
        reservedWords.put(";", "SEMICN");
        reservedWords.put(",", "COMMA");
        reservedWords.put("(", "LPARENT");
        reservedWords.put(")", "RPARENT");
        reservedWords.put("[", "LBRACK");
        reservedWords.put("]", "RBRACK");
        reservedWords.put("{", "LBRACE");
        reservedWords.put("}", "RBRACE");
        reservedWords.put("==", "EQL");
        reservedWords.put(">=", "GEQ");
        reservedWords.put("<=", "LEQ");
        reservedWords.put("!=", "NEQ");
        reservedWords.put(">", "GRE");
        reservedWords.put("<", "LSS");
        reservedWords.put("=", "ASSIGN");
        reservedWords.put("!", "NOT");
        reservedWords.put("&&", "AND");
        reservedWords.put("||", "OR");
        reservedWords.put("/", "DIV");
    }

    /**
     * 词法分析主程序
     */
    public void analysis(String line, int lineNum) throws IOException {
        // 将每一个字符串进行拆分成可分析的词法词
        ArrayList<String> words = lexicalWordCheck.split(line, lineNum);
        // 将对应的词法词进行输出
        this.output(words, lineNum);
    }

    /**
     * 将分析的结果添加到 symTokens 中，并输出词法分析结果
     */
    private void output(ArrayList<String> words, int lineNum) {
        for (String word : words) {
            try {
                String reservedWord = this.getReservedWord(word);
                if (reservedWord != null) {
                    SymToken symToken = new SymToken(reservedWord, word, lineNum);
                    symTokens.add(symToken);
                    OutputController.lexicalAnalysisPrint(symToken);
                } else {
                    ErrorController.printLexicalAnalysisPrintError(lineNum, word);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据词法词获取保留字
     */
    private String getReservedWord(String word) {
        if (reservedWords.containsKey(word)) {
            return reservedWords.get(word);
        } else if (word.matches("[0-9]+")) {
            return "INTCON";
        } else if (word.matches("\".*?\"")) {
            return "STRCON";
        } else if (word.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
            return "IDENFR";
        }
        return null;
    }
}
