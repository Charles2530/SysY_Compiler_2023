package frontend.generation.lexer;

import iostream.ErrorController;
import iostream.ErrorToken;

import java.io.IOException;
import java.util.ArrayList;

public class LexicalWordCheck {
    private final String charSet = "+-*%;,()[]{}";
    private final String cmpSet = "<>=!";
    private final String legalSym = "\"([ !()*+,\\-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "\\[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~]|(%d)|(\\\\n))*\"";

    private String word = "";
    private Boolean isComment = false;

    public void initial() {
        this.word = "";
    }

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
                if (!word.isEmpty()) {
                    words.add(word);
                    word = "";
                }
                words.add(String.valueOf(c));
            } else if (cmpSet.contains(String.valueOf(c))) {
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
                word += c;
            } else if (Character.isSpaceChar(c)) {
                if (!word.isEmpty()) {
                    words.add(word);
                    word = "";
                }
            } else if (c == '\"') {
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
                if (this.checkIllegalSym(word)) {
                    ErrorController.addError(new ErrorToken("a", lineNum));
                }
                words.add(word);
                word = "";
            } else if (i + 1 < line.length() && (c == '&' && line.charAt(i + 1) == '&'
                    || c == '|' && line.charAt(i + 1) == '|')) {
                if (!word.isEmpty()) {
                    words.add(word);
                    word = "";
                }
                words.add(c + String.valueOf(line.charAt(i + 1)));
                i++;
            } else if (c == '/') {
                if (!word.isEmpty()) {
                    words.add(word);
                    word = "";
                }
                if (i + 1 < line.length() && line.charAt(i + 1) == '/') {
                    break;
                } else if (i + 1 < line.length() && line.charAt(i + 1) == '*') {
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
                ErrorController.printLexicalWordCheckPrintError(lineNum, String.valueOf(c));
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
