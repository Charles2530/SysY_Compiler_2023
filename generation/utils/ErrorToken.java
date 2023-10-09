package generation.utils;

import java.io.IOException;

public class ErrorToken {
    private String errorCategoryCode;// 错误类别码
    private int lineNum;  // 词法分析器的行号

    public ErrorToken(String errorCategoryCode, int lineNum) throws IOException {
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
