package iostream.structure;

public class ErrorToken {
    private final String errorCategoryCode;
    private final int lineNum;

    public ErrorToken(String errorCategoryCode, int lineNum) {
        this.errorCategoryCode = errorCategoryCode;
        this.lineNum = lineNum;
    }

    public String getErrorCategoryCode() {
        return errorCategoryCode;
    }

    public int getLineNum() {
        return lineNum;
    }
}
