package frontend.generation.lexer;

import iostream.ErrorController;
import iostream.structure.ErrorToken;

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
            } else if (i + 1 < line.length() && (c == '&' && line.charAt(i + 1) == '&'
                    || c == '|' && line.charAt(i + 1) == '|')) {
                addWordToWords(words);
                words.add(c + String.valueOf(line.charAt(i + 1)));
                i++;
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

    private void addWordToWords(ArrayList<String> words) {
        if (!word.isEmpty()) {
            words.add(word);
            word = "";
        }
    }

    private boolean checkIllegalSym(String word) {
        return !word.matches(legalSym);
    }
}
