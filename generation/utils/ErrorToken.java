package generation.utils;

public class ErrorToken {
    private String errorCategoryCode;
    private int lineNum;

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
