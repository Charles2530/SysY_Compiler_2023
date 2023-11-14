package iostream.structure;

/**
 * ErrorToken 是错误token的记录单元
 * 用于在错误处理时，记录错误的类型和行号
 */
public class ErrorToken {
    /**
     * errorCategoryCode 是错误类型的代码
     * lineNum 是错误所在行号
     */
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
