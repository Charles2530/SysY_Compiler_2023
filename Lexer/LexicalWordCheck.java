package Lexer;

import java.util.ArrayList;

public class LexicalWordCheck {
    private final String charSet = "+-*%;,()[]{}";
    private final String cmpSet = "<>=!";
    private final String legalSym = "\"([ !()*+,\\-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "\\[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~]|(%d)|(\\\\n))*\"";

    private String word = "";
    private Boolean isComment = false;
    private boolean IsDebugMode;

    public void initial(boolean isDebugMode) {
        this.word = "";
        this.IsDebugMode = isDebugMode;
    }

    public ArrayList<String> split(String line, int lineNum) {
        ArrayList<String> words = new ArrayList<>();
        for (int i = 0; i < line.length(); i++) {
            if (isComment) {
                //注释
                if (i + 1 < line.length() && line.charAt(i) == '*' && line.charAt(i + 1) == '/') {
                    isComment = false;
                    i++;
                }
                continue;
            }
            char c = line.charAt(i);
            if (charSet.contains(String.valueOf(c))) {
                //判断是否为保留字
                if (!word.isEmpty()) {
                    words.add(word);
                    word = "";
                }
                words.add(String.valueOf(c));
            } else if (cmpSet.contains(String.valueOf(c))) {
                //判断是否为>=,<=,==,!=
                if (!word.isEmpty()) {
                    words.add(word);
                    word = "";
                }
                if (i + 1 < line.length() && line.charAt(i + 1) == '=') {
                    words.add(c + "=");
                    i++;
                } else {
                    words.add(String.valueOf(c));
                }
            } else if (Character.isLetterOrDigit(c) || c == '_') {
                //判断是否为数字或者字母或者下划线
                word += c;
            } else if (Character.isSpaceChar(c)) {
                //判断是否为空格
                if (!word.isEmpty()) {
                    words.add(word);
                    word = "";
                }
            } else if (c == '\"') {
                //判断是否为字符串
                if (!word.isEmpty()) {
                    words.add(word);
                    word = "";
                }
                word += line.charAt(i++);
                while (i < line.length() && line.charAt(i) != '\"') {
                    word += line.charAt(i);
                    i++;
                }
                word += line.charAt(i);
                if (this.checkIllegalSym(word) && IsDebugMode) {
                    //判断是否为非法字符串
                    System.err.println("error in " + lineNum +
                            " line,word:" + word + " is illegal");
                    System.exit(0);
                } else {
                    words.add(word);
                }
                word = "";
            } else if (i + 1 < line.length() && (c == '&' && line.charAt(i + 1) == '&'
                    || c == '|' && line.charAt(i + 1) == '|')) {
                //判断是否为&&,||
                if (!word.isEmpty()) {
                    words.add(word);
                    word = "";
                }
                words.add(c + String.valueOf(line.charAt(i + 1)));
                i++;
            } else if (c == '/') {
                //判断是否为除法
                if (!word.isEmpty()) {
                    words.add(word);
                    word = "";
                }
                if (i + 1 < line.length() && line.charAt(i + 1) == '/') {
                    //判断是否为单行注释
                    break;
                } else if (i + 1 < line.length() && line.charAt(i + 1) == '*') {
                    //判断是否为多行注释
                    i += 2;
                    isComment = true;
                    while (i < line.length() && !(line.charAt(i) == '*'
                            && line.charAt(i + 1) == '/')) {
                        i++;
                    }
                    if (i + 1 < line.length() && line.charAt(i) == '*'
                            && line.charAt(i + 1) == '/') {
                        isComment = false;
                        i++;
                    }
                } else {
                    words.add(String.valueOf(c));
                }
            } else {
                //出现错误
                if (IsDebugMode) {
                    System.err.println("error in " + lineNum +
                            " line,word:" + c + " can not be recognized");
                    System.exit(0);
                }
            }
        }
        if (!word.isEmpty()) {
            words.add(word);
            word = "";
        }
        return words;
    }

    private boolean checkIllegalSym(String word) {
        return !word.matches(legalSym);
    }
}
