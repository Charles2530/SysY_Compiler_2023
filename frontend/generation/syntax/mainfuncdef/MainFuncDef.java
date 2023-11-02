package frontend.generation.syntax.mainfuncdef;

import iostream.ErrorController;
import iostream.structure.ErrorToken;
import frontend.generation.syntax.AstNode;
import frontend.generation.syntax.AstRecursion;
import frontend.generation.syntax.utils.Definer;
import frontend.generation.syntax.utils.Judge;

import java.io.IOException;

public class MainFuncDef {
    private final AstNode rootAst;

    public MainFuncDef(AstNode rootAst) throws IOException {
        this.rootAst = rootAst;
        this.analysis();
    }

    private void analysis() throws IOException {
        AstNode mainFuncDefNode = new AstNode("<MainFuncDef>");
        rootAst.addChild(mainFuncDefNode);
        if (getPreSym().equals("INTTK")) {
            AstNode intNode = new AstNode("INTTK");
            mainFuncDefNode.addChild(intNode);
            AstRecursion.nextSym();
            if (getPreSym().equals("MAINTK")) {
                AstNode mainNode = new AstNode("MAINTK");
                mainFuncDefNode.addChild(mainNode);
                AstRecursion.nextSym();
                if (getPreSym().equals("LPARENT")) {
                    AstNode lparentNode = new AstNode("LPARENT");
                    mainFuncDefNode.addChild(lparentNode);
                    AstRecursion.nextSym();
                    if (getPreSym().equals("RPARENT")) {
                        AstNode rparentNode = new AstNode("RPARENT");
                        mainFuncDefNode.addChild(rparentNode);
                        AstRecursion.nextSym();
                    } else {
                        ErrorController.addError(new ErrorToken("j",
                                AstRecursion.getPreviousNoTerminalAst()
                                        .getSpan().getEndLine()));
                    }
                    if (Judge.isBlock()) {
                        Definer.genBlock(mainFuncDefNode);
                    } else {
                        ErrorController.printMainFuncDefPrintError();
                    }
                } else {
                    ErrorController.printMainFuncDefPrintError();
                }
            } else {
                ErrorController.printMainFuncDefPrintError();
            }
        } else {
            ErrorController.printMainFuncDefPrintError();
        }
    }

    private String getPreSym() {
        return AstRecursion.getPreSymToken().getReservedWord();
    }
}
