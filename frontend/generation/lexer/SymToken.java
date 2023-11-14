package frontend.generation.lexer;

public class SymToken {
    /**
     * reservedWord 用于存储保留字
     * word 用于存储词法分析的结果
     * lineNum 用于存储当前单词所在的行数
     */
    private final String reservedWord;
    private final String word;
    private final int lineNum;

    public SymToken(String reservedWord, String word, int lineNum) {
        this.reservedWord = reservedWord;
        this.word = word;
        this.lineNum = lineNum;
    }

    public String getReservedWord() {
        return reservedWord;
    }

    public String getWord() {
        return word;
    }

    public int getLineNum() {
        return lineNum;
    }

}
