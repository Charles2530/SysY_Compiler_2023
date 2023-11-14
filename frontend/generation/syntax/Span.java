package frontend.generation.syntax;

public class Span {
    /**
     * Span 用于记录某个终结符或非终结符所处的行号范围
     * startLine 为起始行号
     * endLine 为结束行号
     */
    private int startLine;
    private int endLine;

    public Span(int startLine, int endLine) {
        this.startLine = startLine;
        this.endLine = endLine;
    }

    public int getStartLine() {
        return startLine;
    }

    public int getEndLine() {
        return endLine;
    }

    public void setStartLine(int startLine) {
        this.startLine = startLine;
    }

    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }
}
