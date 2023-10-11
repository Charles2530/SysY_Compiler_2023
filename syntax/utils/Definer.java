package syntax.utils;

import generation.utils.ErrorController;
import generation.utils.ErrorToken;
import syntax.AstNode;
import syntax.AstRecursion;

import java.io.IOException;

public class Definer {
    public static void genConstDecl(AstNode blockNode) throws IOException {
        AstNode constDeclNode = new AstNode("<ConstDecl>");
        blockNode.addChild(constDeclNode);
        AstNode constTkNode = new AstNode("CONSTTK");
        constDeclNode.addChild(constTkNode);
        AstRecursion.nextSym();
        if (getPreSym().equals("INTTK")) {
            AstNode intNode = new AstNode("<BType>");
            constDeclNode.addChild(intNode);
            AstNode intTkNode = new AstNode("INTTK");
            intNode.addChild(intTkNode);
            AstRecursion.nextSym();
            while (true) {
                if (Judge.isConstDef()) {
                    genConstDef(constDeclNode);
                } else {
                    ErrorController.printDefinerPrintError();
                }
                if (getPreSym().equals("COMMA")) {
                    AstNode commaNode = new AstNode("COMMA");
                    constDeclNode.addChild(commaNode);
                    AstRecursion.nextSym();
                } else {
                    break;
                }
            }
        } else {
            ErrorController.printDefinerPrintError();
        }
        if (getPreSym().equals("SEMICN")) {
            AstNode semicnNode = new AstNode("SEMICN");
            constDeclNode.addChild(semicnNode);
            AstRecursion.nextSym();
        } else {
            ErrorController.addError(new ErrorToken("i",
                    AstRecursion.getPreviousNoTerminalAst().getSpan().getEndLine()));
        }
    }

    private static void genConstDef(AstNode constDeclNode) throws IOException {
        AstNode constDefNode = new AstNode("<ConstDef>");
        constDeclNode.addChild(constDefNode);
        if (Judge.isIdent()) {
            AstNode idenfrNode = new AstNode("IDENFR");
            constDefNode.addChild(idenfrNode);
            AstRecursion.nextSym();
        } else {
            ErrorController.printDefinerPrintError();
        }
        while (getPreSym().equals("LBRACK")) {
            AstNode lbrackNode = new AstNode("LBRACK");
            constDefNode.addChild(lbrackNode);
            AstRecursion.nextSym();
            if (Judge.isConstExp()) {
                genConstExp(constDefNode);
            }
            if (getPreSym().equals("RBRACK")) {
                AstNode rbrackNode = new AstNode("RBRACK");
                constDefNode.addChild(rbrackNode);
                AstRecursion.nextSym();
            } else {
                ErrorController.addError(new ErrorToken("k",
                        AstRecursion.getPreviousNoTerminalAst().getSpan().getEndLine()));
            }
        }
        if (getPreSym().equals("ASSIGN")) {
            AstNode assignNode = new AstNode("ASSIGN");
            constDefNode.addChild(assignNode);
            AstRecursion.nextSym();
            if (Judge.isConstInitVal()) {
                genConstInitVal(constDefNode);
            } else {
                ErrorController.printDefinerPrintError();
            }
        } else {
            ErrorController.printDefinerPrintError();
        }
    }

    private static void genConstInitVal(AstNode constDeclNode) throws IOException {
        AstNode constInitValNode = new AstNode("<ConstInitVal>");
        constDeclNode.addChild(constInitValNode);
        if (Judge.isConstExp()) {
            genConstExp(constInitValNode);
        } else if (getPreSym().equals("LBRACE")) {
            AstNode lbraceNode = new AstNode("LBRACE");
            constInitValNode.addChild(lbraceNode);
            AstRecursion.nextSym();
            if (Judge.isConstInitVal()) {
                genConstInitVal(constInitValNode);
            }
            while (getPreSym().equals("COMMA")) {
                AstNode commaNode = new AstNode("COMMA");
                constInitValNode.addChild(commaNode);
                AstRecursion.nextSym();
                if (Judge.isConstInitVal()) {
                    genConstInitVal(constInitValNode);
                } else {
                    ErrorController.printDefinerPrintError();
                }
            }
            if (getPreSym().equals("RBRACE")) {
                AstNode rbraceNode = new AstNode("RBRACE");
                constInitValNode.addChild(rbraceNode);
                AstRecursion.nextSym();
            } else {
                ErrorController.printDefinerPrintError();
            }
        } else {
            ErrorController.printDefinerPrintError();
        }
    }

    public static void genVarDecl(AstNode blockNode) throws IOException {
        AstNode varDeclNode = new AstNode("<VarDecl>");
        blockNode.addChild(varDeclNode);
        AstNode intNode = new AstNode("<BType>");
        varDeclNode.addChild(intNode);
        AstNode intTkNode = new AstNode("INTTK");
        intNode.addChild(intTkNode);
        AstRecursion.nextSym();
        while (true) {
            if (Judge.isVarDef()) {
                genVarDef(varDeclNode);
            } else {
                ErrorController.printDefinerPrintError();
            }
            if (getPreSym().equals("COMMA")) {
                AstNode commaNode = new AstNode("COMMA");
                varDeclNode.addChild(commaNode);
                AstRecursion.nextSym();
            } else {
                break;
            }
        }
        if (getPreSym().equals("SEMICN")) {
            AstNode semicnNode = new AstNode("SEMICN");
            varDeclNode.addChild(semicnNode);
            AstRecursion.nextSym();
        } else {
            ErrorController.addError(new ErrorToken("i",
                    AstRecursion.getPreviousNoTerminalAst().getSpan().getEndLine()));
        }
    }

    private static void genVarDef(AstNode varDeclNode) throws IOException {
        AstNode vardefNode = new AstNode("<VarDef>");
        varDeclNode.addChild(vardefNode);
        if (Judge.isIdent()) {
            AstNode idenfrNode = new AstNode("IDENFR");
            vardefNode.addChild(idenfrNode);
            AstRecursion.nextSym();
        } else {
            ErrorController.printDefinerPrintError();
        }
        while (getPreSym().equals("LBRACK")) {
            AstNode lbrackNode = new AstNode("LBRACK");
            vardefNode.addChild(lbrackNode);
            AstRecursion.nextSym();
            if (Judge.isConstExp()) {
                genConstExp(vardefNode);
            }
            if (getPreSym().equals("RBRACK")) {
                AstNode rbrackNode = new AstNode("RBRACK");
                vardefNode.addChild(rbrackNode);
                AstRecursion.nextSym();
            } else {
                ErrorController.addError(new ErrorToken("k",
                        AstRecursion.getPreviousNoTerminalAst().getSpan().getEndLine()));
            }
        }
        if (getPreSym().equals("ASSIGN")) {
            AstNode assignNode = new AstNode("ASSIGN");
            vardefNode.addChild(assignNode);
            AstRecursion.nextSym();
            if (Judge.isInitval()) {
                genInitVal(vardefNode);
            } else {
                ErrorController.printDefinerPrintError();
            }
        }
    }

    private static void genInitVal(AstNode vardefNode) throws IOException {
        AstNode initValNode = new AstNode("<InitVal>");
        vardefNode.addChild(initValNode);
        if (Judge.isExp()) {
            genExp(initValNode);
        } else if (getPreSym().equals("LBRACE")) {
            AstNode lbraceNode = new AstNode("LBRACE");
            initValNode.addChild(lbraceNode);
            AstRecursion.nextSym();
            if (Judge.isInitval()) {
                genInitVal(initValNode);
            }
            while (getPreSym().equals("COMMA")) {
                AstNode commaNode = new AstNode("COMMA");
                initValNode.addChild(commaNode);
                AstRecursion.nextSym();
                if (Judge.isInitval()) {
                    genInitVal(initValNode);
                } else {
                    ErrorController.printDefinerPrintError();
                }
            }
            if (getPreSym().equals("RBRACE")) {
                AstNode rbraceNode = new AstNode("LBRACE");
                initValNode.addChild(rbraceNode);
                AstRecursion.nextSym();
            } else {
                ErrorController.printDefinerPrintError();
            }
        }
    }

    public static void genBlock(AstNode parent) throws IOException {
        AstNode blockNode = new AstNode("<Block>");
        parent.addChild(blockNode);
        if (getPreSym().equals("LBRACE")) {
            AstNode lbraceNode = new AstNode("LBRACE");
            blockNode.addChild(lbraceNode);
            AstRecursion.nextSym();
            while (Judge.isBlockItem()) {
                Definer.genBlockItem(blockNode);
            }
            if (getPreSym().equals("RBRACE")) {
                AstNode rbraceNode = new AstNode("RBRACE");
                blockNode.addChild(rbraceNode);
                AstRecursion.nextSym();
            } else {
                ErrorController.printDefinerPrintError();
            }
        } else {
            ErrorController.printDefinerPrintError();
        }
    }

    public static void genBlockItem(AstNode blockNode) throws IOException {
        AstNode blockItemNode = new AstNode("<BlockItem>");
        blockNode.addChild(blockItemNode);
        if (Judge.isConstDecl()) {
            AstNode declNode = new AstNode("<Decl>");
            blockItemNode.addChild(declNode);
            genConstDecl(declNode);
        } else if (Judge.isVarDecl()) {
            AstNode declNode = new AstNode("<Decl>");
            blockItemNode.addChild(declNode);
            genVarDecl(declNode);
        } else if (Judge.isStmt()) {
            genStmt(blockItemNode);
        } else {
            ErrorController.printDefinerPrintError();
        }
    }

    public static void genStmt(AstNode blockNode) throws IOException {
        AstNode stmtNode = new AstNode("<Stmt>");
        blockNode.addChild(stmtNode);
        if (Judge.isLVal()) {
            genLVal(stmtNode);
            if (getPreSym().equals("ASSIGN")) {
                AstNode assignNode = new AstNode("ASSIGN");
                stmtNode.addChild(assignNode);
                AstRecursion.nextSym();
                if (Judge.isExp()) {
                    genExp(stmtNode);
                } else if (getPreSym().equals("GETINTTK")) {
                    AstNode getinttkNode = new AstNode("GETINTTK");
                    stmtNode.addChild(getinttkNode);
                    AstRecursion.nextSym();
                    if (getPreSym().equals("LPARENT")) {
                        AstNode lparentNode = new AstNode("LPARENT");
                        stmtNode.addChild(lparentNode);
                        AstRecursion.nextSym();
                        if (getPreSym().equals("RPARENT")) {
                            AstNode rparentNode = new AstNode("RPARENT");
                            stmtNode.addChild(rparentNode);
                            AstRecursion.nextSym();
                        } else {
                            ErrorController.addError(new ErrorToken("j",
                                    AstRecursion.getPreviousNoTerminalAst()
                                            .getSpan().getEndLine()));
                        }
                    } else {
                        ErrorController.printDefinerPrintError();
                    }
                } else {
                    ErrorController.printDefinerPrintError();
                }
                if (getPreSym().equals("SEMICN")) {
                    AstNode semicnNode = new AstNode("SEMICN");
                    stmtNode.addChild(semicnNode);
                    AstRecursion.nextSym();
                } else {
                    ErrorController.addError(new ErrorToken("i",
                            AstRecursion.getPreviousNoTerminalAst()
                                    .getSpan().getEndLine()));
                }
            } else {
                ErrorController.printDefinerPrintError();
            }
        } else if (Judge.isExp() || getPreSym().equals("SEMICN")) {
            if (Judge.isExp()) {
                genExp(stmtNode);
            }
            if (getPreSym().equals("SEMICN")) {
                AstNode semicnNode = new AstNode("SEMICN");
                stmtNode.addChild(semicnNode);
                AstRecursion.nextSym();
            } else {
                ErrorController.addError(new ErrorToken("i",
                        AstRecursion.getPreviousNoTerminalAst().getSpan().getEndLine()));
            }
        } else if (Judge.isBlock()) {
            genBlock(stmtNode);
        } else if (getPreSym().equals("IFTK")) {
            genIfStmt(stmtNode);
        } else if (getPreSym().equals("FORTK")) {
            genForStmt(stmtNode);
        } else if (getPreSym().equals("BREAKTK")) {
            genBreakStmt(stmtNode);
        } else if (getPreSym().equals("CONTINUETK")) {
            genContinueStmt(stmtNode);
        } else if (getPreSym().equals("RETURNTK")) {
            genReturnStmt(stmtNode);
        } else if (getPreSym().equals("PRINTFTK")) {
            genPrintfStmt(stmtNode);
        }
    }

    private static void genIfStmt(AstNode blockNode) throws IOException {
        AstNode ifStmtNode = new AstNode("IFTK");
        blockNode.addChild(ifStmtNode);
        AstRecursion.nextSym();
        if (getPreSym().equals("LPARENT")) {
            AstNode lparentNode = new AstNode("LPARENT");
            blockNode.addChild(lparentNode);
            AstRecursion.nextSym();
            if (Judge.isCond()) {
                genCond(blockNode);
            } else {
                ErrorController.printDefinerPrintError();
            }
            if (getPreSym().equals("RPARENT")) {
                AstNode rparentNode = new AstNode("RPARENT");
                blockNode.addChild(rparentNode);
                AstRecursion.nextSym();
            } else {
                ErrorController.addError(new ErrorToken("j",
                        AstRecursion.getPreviousNoTerminalAst().getSpan().getEndLine()));
            }
            if (Judge.isStmt()) {
                genStmt(blockNode);
            } else {
                ErrorController.printDefinerPrintError();
            }
            if (getPreSym().equals("ELSETK")) {
                AstNode elseTkNode = new AstNode("ELSETK");
                blockNode.addChild(elseTkNode);
                AstRecursion.nextSym();
                if (Judge.isStmt()) {
                    genStmt(blockNode);
                } else {
                    ErrorController.printDefinerPrintError();
                }
            }
        } else {
            ErrorController.printDefinerPrintError();
        }
    }

    private static void genForStmt(AstNode blockNode) throws IOException {
        AstNode forStmtNode = new AstNode("FORTK");
        blockNode.addChild(forStmtNode);
        AstRecursion.nextSym();
        if (getPreSym().equals("LPARENT")) {
            AstNode lparentNode = new AstNode("LPARENT");
            blockNode.addChild(lparentNode);
            AstRecursion.nextSym();
            if (Judge.isForStmtVal()) {
                genForStmtVal(blockNode);
            }
            if (getPreSym().equals("SEMICN")) {
                AstNode semicnNode = new AstNode("SEMICN");
                blockNode.addChild(semicnNode);
                AstRecursion.nextSym();
            } else {
                ErrorController.addError(new ErrorToken("i",
                        AstRecursion.getPreviousNoTerminalAst().getSpan().getEndLine()));
            }
            if (Judge.isCond()) {
                genCond(blockNode);
            }
            if (getPreSym().equals("SEMICN")) {
                AstNode semicnNode = new AstNode("SEMICN");
                blockNode.addChild(semicnNode);
                AstRecursion.nextSym();
            } else {
                ErrorController.addError(new ErrorToken("i",
                        AstRecursion.getPreviousNoTerminalAst().getSpan().getEndLine()));
            }
            if (Judge.isForStmtVal()) {
                genForStmtVal(blockNode);
            }
            if (getPreSym().equals("RPARENT")) {
                AstNode rparentNode = new AstNode("RPARENT");
                blockNode.addChild(rparentNode);
                AstRecursion.nextSym();
            } else {
                ErrorController.addError(new ErrorToken("j",
                        AstRecursion.getPreviousNoTerminalAst().getSpan().getEndLine()));
            }
            if (Judge.isStmt()) {
                genStmt(blockNode);
            } else {
                ErrorController.printDefinerPrintError();
            }
        } else {
            ErrorController.printDefinerPrintError();
        }
    }

    private static void genBreakStmt(AstNode blockNode) throws IOException {
        AstNode breakStmtNode = new AstNode("BREAKTK");
        blockNode.addChild(breakStmtNode);
        AstRecursion.nextSym();
        if (getPreSym().equals("SEMICN")) {
            AstNode semicnNode = new AstNode("SEMICN");
            blockNode.addChild(semicnNode);
            AstRecursion.nextSym();
        } else {
            ErrorController.addError(new ErrorToken("i",
                    AstRecursion.getPreviousNoTerminalAst().getSpan().getEndLine()));
        }
    }

    private static void genContinueStmt(AstNode blockNode) throws IOException {
        AstNode continueStmtNode = new AstNode("CONTINUETK");
        blockNode.addChild(continueStmtNode);
        AstRecursion.nextSym();
        if (getPreSym().equals("SEMICN")) {
            AstNode semicnNode = new AstNode("SEMICN");
            blockNode.addChild(semicnNode);
            AstRecursion.nextSym();
        } else {
            ErrorController.addError(new ErrorToken("i",
                    AstRecursion.getPreviousNoTerminalAst().getSpan().getEndLine()));
        }
    }

    private static void genReturnStmt(AstNode blockNode) throws IOException {
        AstNode returnStmtNode = new AstNode("RETURNTK");
        blockNode.addChild(returnStmtNode);
        AstRecursion.nextSym();
        if (Judge.isExp()) {
            genExp(blockNode);
        }
        if (getPreSym().equals("SEMICN")) {
            AstNode semicnNode = new AstNode("SEMICN");
            blockNode.addChild(semicnNode);
            AstRecursion.nextSym();
        } else {
            ErrorController.addError(new ErrorToken("i",
                    AstRecursion.getPreviousNoTerminalAst().getSpan().getEndLine()));
        }
    }

    private static void genPrintfStmt(AstNode blockNode) throws IOException {
        AstNode printfStmtNode = new AstNode("PRINTFTK");
        blockNode.addChild(printfStmtNode);
        AstRecursion.nextSym();
        if (getPreSym().equals("LPARENT")) {
            AstNode lparentNode = new AstNode("LPARENT");
            blockNode.addChild(lparentNode);
            AstRecursion.nextSym();
            if (getPreSym().equals("STRCON")) {
                AstNode formatNode = new AstNode("STRCON");
                blockNode.addChild(formatNode);
                AstRecursion.nextSym();
                while (getPreSym().equals("COMMA")) {
                    AstNode commaNode = new AstNode("COMMA");
                    blockNode.addChild(commaNode);
                    AstRecursion.nextSym();
                    if (Judge.isExp()) {
                        genExp(blockNode);
                    } else {
                        ErrorController.printDefinerPrintError();
                    }
                }
            } else {
                ErrorController.printDefinerPrintError();
            }
            if (getPreSym().equals("RPARENT")) {
                AstNode rparentNode = new AstNode("RPARENT");
                blockNode.addChild(rparentNode);
                AstRecursion.nextSym();
            } else {
                ErrorController.addError(new ErrorToken("j",
                        AstRecursion.getPreviousNoTerminalAst().getSpan().getEndLine()));
            }
            if (getPreSym().equals("SEMICN")) {
                AstNode semicnNode = new AstNode("SEMICN");
                blockNode.addChild(semicnNode);
                AstRecursion.nextSym();
            } else {
                ErrorController.addError(new ErrorToken("i",
                        AstRecursion.getPreviousNoTerminalAst().getSpan().getEndLine()));
            }
        }
    }

    private static void genForStmtVal(AstNode forStmtNode) throws IOException {
        AstNode forStmtValNode = new AstNode("<ForStmt>");
        forStmtNode.addChild(forStmtValNode);
        if (Judge.isLVal()) {
            genLVal(forStmtValNode);
            if (getPreSym().equals("ASSIGN")) {
                AstNode assignNode = new AstNode("ASSIGN");
                forStmtValNode.addChild(assignNode);
                AstRecursion.nextSym();
                if (Judge.isExp()) {
                    genExp(forStmtValNode);
                } else {
                    ErrorController.printDefinerPrintError();
                }
            } else {
                ErrorController.printDefinerPrintError();
            }
        } else {
            ErrorController.printDefinerPrintError();
        }
    }

    private static void genExp(AstNode funcRParamsNode) throws IOException {
        AstNode expNode = new AstNode("<Exp>");
        funcRParamsNode.addChild(expNode);
        if (Judge.isAddExp()) {
            genAddExp(expNode);
        } else {
            ErrorController.printDefinerPrintError();
        }
    }

    private static void genCond(AstNode forStmtNode) throws IOException {
        AstNode condNode = new AstNode("<Cond>");
        forStmtNode.addChild(condNode);
        genLOrExp(condNode);
    }

    private static void genLVal(AstNode primaryExpNode) throws IOException {
        AstNode lvalNode = new AstNode("<LVal>");
        primaryExpNode.addChild(lvalNode);
        if (Judge.isIdent()) {
            genIdent(lvalNode);
            while (getPreSym().equals("LBRACK")) {
                AstNode lbrackNode = new AstNode("LBRACK");
                lvalNode.addChild(lbrackNode);
                AstRecursion.nextSym();
                if (Judge.isExp()) {
                    genExp(lvalNode);
                } else {
                    ErrorController.printDefinerPrintError();
                }
                if (getPreSym().equals("RBRACK")) {
                    AstNode rbrackNode = new AstNode("RBRACK");
                    lvalNode.addChild(rbrackNode);
                    AstRecursion.nextSym();
                } else {
                    ErrorController.addError(new ErrorToken("k",
                            AstRecursion.getPreviousNoTerminalAst().getSpan().getEndLine()));
                }
            }
        } else {
            ErrorController.printDefinerPrintError();
        }
    }

    private static void genPrimaryExp(AstNode constExpNode) throws IOException {
        AstNode primaryExpNode = new AstNode("<PrimaryExp>");
        constExpNode.addChild(primaryExpNode);
        if (getPreSym().equals("LPARENT")) {
            AstNode lparentNode = new AstNode("LPARENT");
            primaryExpNode.addChild(lparentNode);
            AstRecursion.nextSym();
            if (Judge.isExp()) {
                genExp(primaryExpNode);
            } else {
                ErrorController.printDefinerPrintError();
            }
            if (getPreSym().equals("RPARENT")) {
                AstNode rparentNode = new AstNode("RPARENT");
                primaryExpNode.addChild(rparentNode);
                AstRecursion.nextSym();
            } else {
                ErrorController.addError(new ErrorToken("j",
                        AstRecursion.getPreviousNoTerminalAst().getSpan().getEndLine()));
            }
        } else if (Judge.isIdent()) {
            genLVal(primaryExpNode);
        } else if (Judge.isNumber()) {
            genNumberCall(primaryExpNode);
        } else {
            ErrorController.printDefinerPrintError();
        }
    }

    private static void genNumberCall(AstNode primaryExpNode) throws IOException {
        AstNode numberNode = new AstNode("<Number>");
        primaryExpNode.addChild(numberNode);
        if (getPreSym().equals("INTCON")) {
            AstNode intconNode = new AstNode("INTCON");
            numberNode.addChild(intconNode);
            AstRecursion.nextSym();
        } else {
            ErrorController.printDefinerPrintError();
        }
    }

    private static void genUnaryExp(AstNode constExpNode) throws IOException {
        AstNode unaryExpNode = new AstNode("<UnaryExp>");
        constExpNode.addChild(unaryExpNode);
        if (Judge.isUnaryOp()) {
            genUnaryOp(unaryExpNode);
            if (Judge.isUnaryExp()) {
                genUnaryExp(unaryExpNode);
            } else {
                ErrorController.printDefinerPrintError();
            }
        } else if (Judge.isIdent() && getNextSym(1).equals("LPARENT")) {
            genIdent(unaryExpNode);
            if (getPreSym().equals("LPARENT")) {
                AstNode lparentNode = new AstNode("LPARENT");
                unaryExpNode.addChild(lparentNode);
                AstRecursion.nextSym();
                if (Judge.isFuncRParams()) {
                    genFuncRParams(unaryExpNode);
                }
                if (getPreSym().equals("RPARENT")) {
                    AstNode rparentNode = new AstNode("RPARENT");
                    unaryExpNode.addChild(rparentNode);
                    AstRecursion.nextSym();
                } else {
                    ErrorController.addError(new ErrorToken("j",
                            AstRecursion.getPreviousNoTerminalAst().getSpan().getEndLine()));
                }
            }
        } else if (Judge.isPrimaryExp()) {
            genPrimaryExp(unaryExpNode);
        } else {
            ErrorController.printDefinerPrintError();
        }
    }

    private static void genUnaryOp(AstNode constExpNode) throws IOException {
        AstNode unaryOpNode = new AstNode("<UnaryOp>");
        constExpNode.addChild(unaryOpNode);
        if (getPreSym().equals("PLUS")) {
            AstNode plusNode = new AstNode("PLUS");
            unaryOpNode.addChild(plusNode);
            AstRecursion.nextSym();
        } else if (getPreSym().equals("MINU")) {
            AstNode minuNode = new AstNode("MINU");
            unaryOpNode.addChild(minuNode);
            AstRecursion.nextSym();
        } else if (getPreSym().equals("NOT")) {
            AstNode modNode = new AstNode("NOT");
            unaryOpNode.addChild(modNode);
            AstRecursion.nextSym();
        } else {
            ErrorController.printDefinerPrintError();
        }
    }

    private static void genFuncRParams(AstNode constExpNode) throws IOException {
        AstNode funcRParamsNode = new AstNode("<FuncRParams>");
        constExpNode.addChild(funcRParamsNode);
        while (Judge.isExp()) {
            genExp(funcRParamsNode);
            if (getPreSym().equals("COMMA")) {
                AstNode commaNode = new AstNode("COMMA");
                funcRParamsNode.addChild(commaNode);
                AstRecursion.nextSym();
            } else {
                break;
            }
        }
    }

    private static void genMulExp(AstNode constExpNode) throws IOException {
        AstNode mulExpNode = new AstNode("<MulExp>");
        genUnaryExp(mulExpNode);
        AstNode father = mulExpNode;
        while (getPreSym().equals("MULT") || getPreSym().equals("DIV") ||
                getPreSym().equals("MOD")) {
            AstNode extraMulExpNode = new AstNode("<MulExp>");
            extraMulExpNode.addChild(father);
            father = extraMulExpNode;
            if (getPreSym().equals("MULT")) {
                AstNode multNode = new AstNode("MULT");
                father.addChild(multNode);
                AstRecursion.nextSym();
                genUnaryExp(father);
            } else if (getPreSym().equals("DIV")) {
                AstNode divNode = new AstNode("DIV");
                father.addChild(divNode);
                AstRecursion.nextSym();
                genUnaryExp(father);
            } else if (getPreSym().equals("MOD")) {
                AstNode modNode = new AstNode("MOD");
                father.addChild(modNode);
                AstRecursion.nextSym();
                genUnaryExp(father);
            }
        }
        constExpNode.addChild(father);
    }

    private static void genAddExp(AstNode constExpNode) throws IOException {
        AstNode addExpNode = new AstNode("<AddExp>");
        genMulExp(addExpNode);
        AstNode father = addExpNode;
        while (getPreSym().equals("PLUS") || getPreSym().equals("MINU")) {
            AstNode extraAddExpNode = new AstNode("<AddExp>");
            extraAddExpNode.addChild(father);
            father = extraAddExpNode;
            if (getPreSym().equals("PLUS")) {
                AstNode plusNode = new AstNode("PLUS");
                father.addChild(plusNode);
                AstRecursion.nextSym();
                genMulExp(father);
            } else if (getPreSym().equals("MINU")) {
                AstNode minuNode = new AstNode("MINU");
                father.addChild(minuNode);
                AstRecursion.nextSym();
                genMulExp(father);
            }
        }
        constExpNode.addChild(father);
    }

    private static void genRelExp(AstNode equExpNode) throws IOException {
        AstNode relExpNode = new AstNode("<RelExp>");
        genAddExp(relExpNode);
        AstNode father = relExpNode;
        while (getPreSym().equals("LSS") || getPreSym().equals("LEQ") ||
                getPreSym().equals("GRE") || getPreSym().equals("GEQ")) {
            AstNode extraRelExpNode = new AstNode("<RelExp>");
            extraRelExpNode.addChild(father);
            father = extraRelExpNode;
            if (getPreSym().equals("LSS")) {
                AstNode lssNode = new AstNode("LSS");
                father.addChild(lssNode);
                AstRecursion.nextSym();
                genAddExp(father);
            } else if (getPreSym().equals("LEQ")) {
                AstNode leqNode = new AstNode("LEQ");
                father.addChild(leqNode);
                AstRecursion.nextSym();
                genAddExp(father);
            } else if (getPreSym().equals("GRE")) {
                AstNode greNode = new AstNode("GRE");
                father.addChild(greNode);
                AstRecursion.nextSym();
                genAddExp(father);
            } else if (getPreSym().equals("GEQ")) {
                AstNode geqNode = new AstNode("GEQ");
                father.addChild(geqNode);
                AstRecursion.nextSym();
                genAddExp(father);
            }
        }
        equExpNode.addChild(father);
    }

    private static void genEqExp(AstNode astNode) throws IOException {
        AstNode equExpNode = new AstNode("<EqExp>");
        genRelExp(equExpNode);
        AstNode father = equExpNode;
        while (getPreSym().equals("EQL") || getPreSym().equals("NEQ")) {
            AstNode extraEqExpNode = new AstNode("<EqExp>");
            extraEqExpNode.addChild(father);
            father = extraEqExpNode;
            if (getPreSym().equals("EQL")) {
                AstNode eqlNode = new AstNode("EQL");
                father.addChild(eqlNode);
                AstRecursion.nextSym();
                genRelExp(father);
            } else if (getPreSym().equals("NEQ")) {
                AstNode neqNode = new AstNode("NEQ");
                father.addChild(neqNode);
                AstRecursion.nextSym();
                genRelExp(father);
            }
        }
        astNode.addChild(father);
    }

    private static void genLAndExp(AstNode astNode) throws IOException {
        AstNode landExpNode = new AstNode("<LAndExp>");
        genEqExp(landExpNode);
        AstNode father = landExpNode;
        while (getPreSym().equals("AND")) {
            AstNode extraLAndExpNode = new AstNode("<LAndExp>");
            extraLAndExpNode.addChild(father);
            father = extraLAndExpNode;
            AstNode andNode = new AstNode("AND");
            father.addChild(andNode);
            AstRecursion.nextSym();
            genEqExp(father);
        }
        astNode.addChild(father);
    }

    private static void genLOrExp(AstNode condNode) throws IOException {
        AstNode lorExpNode = new AstNode("<LOrExp>");
        genLAndExp(lorExpNode);
        AstNode father = lorExpNode;
        while (getPreSym().equals("OR")) {
            AstNode extraLorExpNode = new AstNode("<LOrExp>");
            extraLorExpNode.addChild(father);
            father = extraLorExpNode;
            AstNode orNode = new AstNode("OR");
            father.addChild(orNode);
            AstRecursion.nextSym();
            genLAndExp(father);
        }
        condNode.addChild(father);
    }

    public static void genConstExp(AstNode constDeclNode) throws IOException {
        AstNode constExpNode = new AstNode("<ConstExp>");
        constDeclNode.addChild(constExpNode);
        if (Judge.isAddExp()) {
            genAddExp(constExpNode);
        } else {
            ErrorController.printDefinerPrintError();
        }
    }

    public static void genIdent(AstNode constExpNode) {
        AstNode idenfrNode = new AstNode("IDENFR");
        constExpNode.addChild(idenfrNode);
        AstRecursion.nextSym();
    }

    private static String getPreSym() {
        return AstRecursion.getPreSymToken().getReservedWord();
    }

    public static String getNextSym(int pos) {
        return AstRecursion.getNextSymToken(pos).getReservedWord();
    }
}
