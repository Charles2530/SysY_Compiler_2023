package frontend.generation.lexer;

import iostream.structure.ErrorController;
import iostream.structure.ErrorToken;

import java.io.IOException;
import java.util.ArrayList;

public class LexicalWordCheck {
    /**
     * charSet 用于存储可以通过单个字符判断的为所属词法的符号
     * cmpSet 用于存储判断为比较符号的符号，需要与后续连接=区分，故属于通过两个字符判断的符号
     * legalSym 用于存储合法的字符串,用于正则表达式匹配
     * word 用于存储当前正在读取的单词
     * isComment 用于判断是否处于多行注释中
     */
    private final String charSet = "+-*%;,()[]{}";
    private final String cmpSet = "<>=!";
    private final String legalSym = "\"([ !()*+,\\-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "\\[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~]|(%d)|(\\\\n))*\"";

    private String word = "";
    private Boolean isComment = false;

    public void initial() {
        this.word = "";
    }

    /**
     * 拆分读入的一行字符串，将其分解为单词集合
     */
    public ArrayList<String> split(String line, int lineNum) throws IOException {
        ArrayList<String> words = new ArrayList<>();
        for (int i = 0; i < line.length(); i++) {
            if (isComment) {
                if (i + 1 < line.length() && line.charAt(i) == '*' && line.charAt(i + 1) == '/') {
                    isComment = false;
                    i++;
                }
                continue;
            }
            char c = line.charAt(i);
            if (charSet.contains(String.valueOf(c))) {
                addWordToWords(words);
                words.add(String.valueOf(c));
            } else if (cmpSet.contains(String.valueOf(c))) {
                addWordToWords(words);
                if (i + 1 < line.length() && line.charAt(i + 1) == '=') {
                    words.add(c + "=");
                    i++;
                } else {
                    words.add(String.valueOf(c));
                }
            } else if (Character.isLetterOrDigit(c) || c == '_') {
                word += c;
            } else if (Character.isSpaceChar(c) || c == '\t') {
                addWordToWords(words);
            } else if (c == '\"') {
                addWordToWords(words);
                word += line.charAt(i++);
                while (i < line.length() && line.charAt(i) != '\"') {
                    word += line.charAt(i);
                    i++;
                }
                word += line.charAt(i);
                if (this.checkIllegalSym(word)) {
                    ErrorController.addError(new ErrorToken("a", lineNum));
                }
                addWordToWords(words);
            } else if (c == '&' || c == '|') {
                if (i + 1 < line.length() && line.charAt(i + 1) == c) {
                    addWordToWords(words);
                    words.add(c + String.valueOf(line.charAt(i + 1)));
                    i++;
                }
            } else if (c == '/') {
                addWordToWords(words);
                if (i + 1 < line.length() && line.charAt(i + 1) == '/') {
                    break;
                } else if (i + 1 < line.length() && line.charAt(i + 1) == '*') {
                    i++;
                    isComment = true;
                } else {
                    words.add(String.valueOf(c));
                }
            } else {
                ErrorController.printLexicalWordCheckPrintError(lineNum, String.valueOf(c));
            }
        }
        addWordToWords(words);
        return words;
    }

    /**
     * 将当前读到的单词添加到单词集合中
     */
    private void addWordToWords(ArrayList<String> words) {
        if (!word.isEmpty()) {
            words.add(word);
            word = "";
        }
    }

    /**
     * 检查当前的单词是否为非法字符
     */
    private boolean checkIllegalSym(String word) {
        return !word.matches(legalSym);
    }
}
