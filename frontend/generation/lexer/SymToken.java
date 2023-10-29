package frontend.generation.lexer;

public class SymToken {
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
