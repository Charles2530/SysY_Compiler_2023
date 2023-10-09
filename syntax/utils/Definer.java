package syntax.utils;

import generation.utils.ErrorController;
import generation.utils.ErrorToken;
import syntax.AstNode;
import syntax.AstRecursion;

import java.io.IOException;

public class Definer {
    public static void ConstDecl(AstNode blockNode) throws IOException {
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
                if (Judge.IsConstDef()) {
                    ConstDef(constDeclNode);
                } else {
                    ErrorController.DefinerPrintError();
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
            ErrorController.DefinerPrintError();
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

    private static void ConstDef(AstNode constDeclNode) throws IOException {
        AstNode constDefNode = new AstNode("<ConstDef>");
        constDeclNode.addChild(constDefNode);
        if (Judge.IsIdent()) {
            AstNode idenfrNode = new AstNode("IDENFR");
            constDefNode.addChild(idenfrNode);
            AstRecursion.nextSym();
        } else {
            ErrorController.DefinerPrintError();
        }
        while (getPreSym().equals("LBRACK")) {
            AstNode lbrackNode = new AstNode("LBRACK");
            constDefNode.addChild(lbrackNode);
            AstRecursion.nextSym();
            if (Judge.IsConstExp()) {
                ConstExp(constDefNode);
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
            if (Judge.IsConstInitVal()) {
                ConstInitVal(constDefNode);
            } else {
                ErrorController.DefinerPrintError();
            }
        } else {
            ErrorController.DefinerPrintError();
        }
    }

    private static void ConstInitVal(AstNode constDeclNode) throws IOException {
        AstNode constInitValNode = new AstNode("<ConstInitVal>");
        constDeclNode.addChild(constInitValNode);
        if (Judge.IsConstExp()) {
            ConstExp(constInitValNode);
        } else if (getPreSym().equals("LBRACE")) {
            AstNode lbraceNode = new AstNode("LBRACE");
            constInitValNode.addChild(lbraceNode);
            AstRecursion.nextSym();
            if (Judge.IsConstInitVal()) {
                ConstInitVal(constInitValNode);
            }
            while (getPreSym().equals("COMMA")) {
                AstNode commaNode = new AstNode("COMMA");
                constInitValNode.addChild(commaNode);
                AstRecursion.nextSym();
                if (Judge.IsConstInitVal()) {
                    ConstInitVal(constInitValNode);
                } else {
                    ErrorController.DefinerPrintError();
                }
            }
            if (getPreSym().equals("RBRACE")) {
                AstNode rbraceNode = new AstNode("RBRACE");
                constInitValNode.addChild(rbraceNode);
                AstRecursion.nextSym();
            } else {
                ErrorController.DefinerPrintError();
            }
        } else {
            ErrorController.DefinerPrintError();
        }
    }

    public static void VarDecl(AstNode blockNode) throws IOException {
        AstNode varDeclNode = new AstNode("<VarDecl>");
        blockNode.addChild(varDeclNode);
        AstNode intNode = new AstNode("<BType>");
        varDeclNode.addChild(intNode);
        AstNode intTkNode = new AstNode("INTTK");
        intNode.addChild(intTkNode);
        AstRecursion.nextSym();
        while (true) {
            if (Judge.IsVarDef()) {
                VarDef(varDeclNode);
            } else {
                ErrorController.DefinerPrintError();
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

    private static void VarDef(AstNode varDeclNode) throws IOException {
        AstNode vardefNode = new AstNode("<VarDef>");
        varDeclNode.addChild(vardefNode);
        if (Judge.IsIdent()) {
            AstNode idenfrNode = new AstNode("IDENFR");
            vardefNode.addChild(idenfrNode);
            AstRecursion.nextSym();
        } else {
            ErrorController.DefinerPrintError();
        }
        while (getPreSym().equals("LBRACK")) {
            AstNode lbrackNode = new AstNode("LBRACK");
            vardefNode.addChild(lbrackNode);
            AstRecursion.nextSym();
            if (Judge.IsConstExp()) {
                ConstExp(vardefNode);
            }
            if (getPreSym().equals("RBRACK")) {
                AstNode rbrackNode = new AstNode("RBRACK");
                vardefNode.addChild(rbrackNode);
                AstRecursion.nextSym();
            } else {
                ErrorController.addError(
                        new ErrorToken("k",
                                AstRecursion.getPreviousNoTerminalAst().getSpan().getEndLine()));
            }
        }
        if (getPreSym().equals("ASSIGN")) {
            AstNode assignNode = new AstNode("ASSIGN");
            vardefNode.addChild(assignNode);
            AstRecursion.nextSym();
            if (Judge.IsInitval()) {
                InitVal(vardefNode);
            } else {
                ErrorController.DefinerPrintError();
            }
        }
    }

    private static void InitVal(AstNode vardefNode) throws IOException {
        AstNode initValNode = new AstNode("<InitVal>");
        vardefNode.addChild(initValNode);
        if (Judge.IsExp()) {
            Exp(initValNode);
        } else if (getPreSym().equals("LBRACE")) {
            AstNode lbraceNode = new AstNode("LBRACE");
            initValNode.addChild(lbraceNode);
            AstRecursion.nextSym();
            if (Judge.IsInitval()) {
                InitVal(initValNode);
            }
            while (getPreSym().equals("COMMA")) {
                AstNode commaNode = new AstNode("COMMA");
                initValNode.addChild(commaNode);
                AstRecursion.nextSym();
                if (Judge.IsInitval()) {
                    InitVal(initValNode);
                } else {
                    ErrorController.DefinerPrintError();
                }
            }
            if (getPreSym().equals("RBRACE")) {
                AstNode rbraceNode = new AstNode("LBRACE");
                initValNode.addChild(rbraceNode);
                AstRecursion.nextSym();
            } else {
                ErrorController.DefinerPrintError();
            }
        }
    }

    public static void Block(AstNode parent) throws IOException {
        AstNode blockNode = new AstNode("<Block>");
        parent.addChild(blockNode);
        if (getPreSym().equals("LBRACE")) {
            AstNode lbraceNode = new AstNode("LBRACE");
            blockNode.addChild(lbraceNode);
            AstRecursion.nextSym();
            while (Judge.IsBlockItem()) {
                Definer.BlockItem(blockNode);
            }
            if (getPreSym().equals("RBRACE")) {
                AstNode rbraceNode = new AstNode("RBRACE");
                blockNode.addChild(rbraceNode);
                AstRecursion.nextSym();
            } else {
                ErrorController.DefinerPrintError();
            }
        } else {
            ErrorController.DefinerPrintError();
        }
    }

    public static void BlockItem(AstNode blockNode) throws IOException {
        AstNode blockItemNode = new AstNode("<BlockItem>");
        blockNode.addChild(blockItemNode);
        if (Judge.IsConstDecl()) {
            AstNode declNode = new AstNode("<Decl>");
            blockItemNode.addChild(declNode);
            ConstDecl(declNode);
        } else if (Judge.IsVarDecl()) {
            AstNode declNode = new AstNode("<Decl>");
            blockItemNode.addChild(declNode);
            VarDecl(declNode);
        } else if (Judge.IsStmt()) {
            Stmt(blockItemNode);
        } else {
            ErrorController.DefinerPrintError();
        }
    }

    public static void Stmt(AstNode blockNode) throws IOException {
        AstNode stmtNode = new AstNode("<Stmt>");
        blockNode.addChild(stmtNode);
        if (Judge.IsLVal()) {
            LVal(stmtNode);
            if (getPreSym().equals("ASSIGN")) {
                AstNode assignNode = new AstNode("ASSIGN");
                stmtNode.addChild(assignNode);
                AstRecursion.nextSym();
                if (Judge.IsExp()) {
                    Exp(stmtNode);
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
                        ErrorController.DefinerPrintError();
                    }
                } else {
                    ErrorController.DefinerPrintError();
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
                ErrorController.DefinerPrintError();
            }
        } else if (Judge.IsExp() || getPreSym().equals("SEMICN")) {
            if (Judge.IsExp()) {
                Exp(stmtNode);
            }
            if (getPreSym().equals("SEMICN")) {
                AstNode semicnNode = new AstNode("SEMICN");
                stmtNode.addChild(semicnNode);
                AstRecursion.nextSym();
            } else {
                ErrorController.addError(
                        new ErrorToken("i",
                                AstRecursion.getPreviousNoTerminalAst().getSpan().getEndLine()));
            }
        } else if (Judge.IsBlock()) {
            Block(stmtNode);
        } else if (getPreSym().equals("IFTK")) {
            IfStmt(stmtNode);
        } else if (getPreSym().equals("FORTK")) {
            ForStmt(stmtNode);
        } else if (getPreSym().equals("BREAKTK")) {
            BreakStmt(stmtNode);
        } else if (getPreSym().equals("CONTINUETK")) {
            ContinueStmt(stmtNode);
        } else if (getPreSym().equals("RETURNTK")) {
            ReturnStmt(stmtNode);
        } else if (getPreSym().equals("PRINTFTK")) {
            PrintfStmt(stmtNode);
        }
    }

    private static void IfStmt(AstNode blockNode) throws IOException {
        AstNode ifStmtNode = new AstNode("IFTK");
        blockNode.addChild(ifStmtNode);
        AstRecursion.nextSym();
        if (getPreSym().equals("LPARENT")) {
            AstNode lparentNode = new AstNode("LPARENT");
            blockNode.addChild(lparentNode);
            AstRecursion.nextSym();
            if (Judge.IsCond()) {
                Cond(blockNode);
            } else {
                ErrorController.DefinerPrintError();
            }
            if (getPreSym().equals("RPARENT")) {
                AstNode rparentNode = new AstNode("RPARENT");
                blockNode.addChild(rparentNode);
                AstRecursion.nextSym();
            } else {
                ErrorController.addError(new ErrorToken("j",
                        AstRecursion.getPreviousNoTerminalAst().getSpan().getEndLine()));
            }
            if (Judge.IsStmt()) {
                Stmt(blockNode);
            } else {
                ErrorController.DefinerPrintError();
            }
            if (getPreSym().equals("ELSETK")) {
                AstNode elseTkNode = new AstNode("ELSETK");
                blockNode.addChild(elseTkNode);
                AstRecursion.nextSym();
                if (Judge.IsStmt()) {
                    Stmt(blockNode);
                } else {
                    ErrorController.DefinerPrintError();
                }
            }
        } else {
            ErrorController.DefinerPrintError();
        }
    }

    private static void ForStmt(AstNode blockNode) throws IOException {
        AstNode forStmtNode = new AstNode("FORTK");
        blockNode.addChild(forStmtNode);
        AstRecursion.nextSym();
        if (getPreSym().equals("LPARENT")) {
            AstNode lparentNode = new AstNode("LPARENT");
            blockNode.addChild(lparentNode);
            AstRecursion.nextSym();
            if (Judge.IsForStmtVal()) {
                ForStmtVal(blockNode);
            }
            if (getPreSym().equals("SEMICN")) {
                AstNode semicnNode = new AstNode("SEMICN");
                blockNode.addChild(semicnNode);
                AstRecursion.nextSym();
            } else {
                ErrorController.addError(new ErrorToken("i",
                        AstRecursion.getPreviousNoTerminalAst().getSpan().getEndLine()));
            }
            if (Judge.IsCond()) {
                Cond(blockNode);
            }
            if (getPreSym().equals("SEMICN")) {
                AstNode semicnNode = new AstNode("SEMICN");
                blockNode.addChild(semicnNode);
                AstRecursion.nextSym();
            } else {
                ErrorController.addError(new ErrorToken("i",
                        AstRecursion.getPreviousNoTerminalAst().getSpan().getEndLine()));
            }
            if (Judge.IsForStmtVal()) {
                ForStmtVal(blockNode);
            }
            if (getPreSym().equals("RPARENT")) {
                AstNode rparentNode = new AstNode("RPARENT");
                blockNode.addChild(rparentNode);
                AstRecursion.nextSym();
            } else {
                ErrorController.addError(new ErrorToken("j",
                        AstRecursion.getPreviousNoTerminalAst().getSpan().getEndLine()));
            }
            if (Judge.IsStmt()) {
                Stmt(blockNode);
            } else {
                ErrorController.DefinerPrintError();
            }
        } else {
            ErrorController.DefinerPrintError();
        }
    }

    private static void BreakStmt(AstNode blockNode) throws IOException {
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

    private static void ContinueStmt(AstNode blockNode) throws IOException {
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

    private static void ReturnStmt(AstNode blockNode) throws IOException {
        AstNode returnStmtNode = new AstNode("RETURNTK");
        blockNode.addChild(returnStmtNode);
        AstRecursion.nextSym();
        if (Judge.IsExp()) {
            Exp(blockNode);
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

    private static void PrintfStmt(AstNode blockNode) throws IOException {
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
                    if (Judge.IsExp()) {
                        Exp(blockNode);
                    } else {
                        ErrorController.DefinerPrintError();
                    }
                }
            } else {
                ErrorController.DefinerPrintError();
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

    private static void ForStmtVal(AstNode forStmtNode) throws IOException {
        AstNode forStmtValNode = new AstNode("<ForStmt>");
        forStmtNode.addChild(forStmtValNode);
        if (Judge.IsLVal()) {
            LVal(forStmtValNode);
            if (getPreSym().equals("ASSIGN")) {
                AstNode assignNode = new AstNode("ASSIGN");
                forStmtValNode.addChild(assignNode);
                AstRecursion.nextSym();
                if (Judge.IsExp()) {
                    Exp(forStmtValNode);
                } else {
                    ErrorController.DefinerPrintError();
                }
            } else {
                ErrorController.DefinerPrintError();
            }
        } else {
            ErrorController.DefinerPrintError();
        }
    }

    private static void Exp(AstNode funcRParamsNode) throws IOException {
        AstNode expNode = new AstNode("<Exp>");
        funcRParamsNode.addChild(expNode);
        if (Judge.IsAddExp()) {
            AddExp(expNode);
        } else {
            ErrorController.DefinerPrintError();
        }
    }

    private static void Cond(AstNode forStmtNode) throws IOException {
        AstNode condNode = new AstNode("<Cond>");
        forStmtNode.addChild(condNode);
        LOrExp(condNode);
    }

    private static void LVal(AstNode primaryExpNode) throws IOException {
        AstNode lValNode = new AstNode("<LVal>");
        primaryExpNode.addChild(lValNode);
        if (Judge.IsIdent()) {
            Ident(lValNode);
            while (getPreSym().equals("LBRACK")) {
                AstNode lbrackNode = new AstNode("LBRACK");
                lValNode.addChild(lbrackNode);
                AstRecursion.nextSym();
                if (Judge.IsExp()) {
                    Exp(lValNode);
                } else {
                    ErrorController.DefinerPrintError();
                }
                if (getPreSym().equals("RBRACK")) {
                    AstNode rbrackNode = new AstNode("RBRACK");
                    lValNode.addChild(rbrackNode);
                    AstRecursion.nextSym();
                } else {
                    ErrorController.addError(new ErrorToken("k",
                            AstRecursion.getPreviousNoTerminalAst().getSpan().getEndLine()));
                }
            }
        } else {
            ErrorController.DefinerPrintError();
        }
    }

    private static void PrimaryExp(AstNode constExpNode) throws IOException {
        AstNode primaryExpNode = new AstNode("<PrimaryExp>");
        constExpNode.addChild(primaryExpNode);
        if (getPreSym().equals("LPARENT")) {
            AstNode lparentNode = new AstNode("LPARENT");
            primaryExpNode.addChild(lparentNode);
            AstRecursion.nextSym();
            if (Judge.IsExp()) {
                Exp(primaryExpNode);
            } else {
                ErrorController.DefinerPrintError();
            }
            if (getPreSym().equals("RPARENT")) {
                AstNode rparentNode = new AstNode("RPARENT");
                primaryExpNode.addChild(rparentNode);
                AstRecursion.nextSym();
            } else {
                ErrorController.addError(new ErrorToken("j",
                        AstRecursion.getPreviousNoTerminalAst().getSpan().getEndLine()));
            }
        } else if (Judge.IsIdent()) {
            LVal(primaryExpNode);
        } else if (Judge.IsNumber()) {
            NumberCall(primaryExpNode);
        } else {
            ErrorController.DefinerPrintError();
        }
    }

    private static void NumberCall(AstNode primaryExpNode) throws IOException {
        AstNode numberNode = new AstNode("<Number>");
        primaryExpNode.addChild(numberNode);
        if (getPreSym().equals("INTCON")) {
            AstNode intconNode = new AstNode("INTCON");
            numberNode.addChild(intconNode);
            AstRecursion.nextSym();
        } else {
            ErrorController.DefinerPrintError();
        }
    }

    private static void UnaryExp(AstNode constExpNode) throws IOException {
        AstNode unaryExpNode = new AstNode("<UnaryExp>");
        constExpNode.addChild(unaryExpNode);
        if (Judge.IsUnaryOp()) {
            UnaryOp(unaryExpNode);
            if (Judge.IsUnaryExp()) {
                UnaryExp(unaryExpNode);
            } else {
                ErrorController.DefinerPrintError();
            }
        } else if (Judge.IsIdent() && getNextSym(1).equals("LPARENT")) {
            Ident(unaryExpNode);
            if (getPreSym().equals("LPARENT")) {
                AstNode lparentNode = new AstNode("LPARENT");
                unaryExpNode.addChild(lparentNode);
                AstRecursion.nextSym();
                if (Judge.IsFuncRParams()) {
                    FuncRParams(unaryExpNode);
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
        } else if (Judge.IsPrimaryExp()) {
            PrimaryExp(unaryExpNode);
        } else {
            ErrorController.DefinerPrintError();
        }
    }

    private static void UnaryOp(AstNode constExpNode) throws IOException {
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
            ErrorController.DefinerPrintError();
        }
    }

    private static void FuncRParams(AstNode constExpNode) throws IOException {
        AstNode funcRParamsNode = new AstNode("<FuncRParams>");
        constExpNode.addChild(funcRParamsNode);
        while (Judge.IsExp()) {
            Exp(funcRParamsNode);
            if (getPreSym().equals("COMMA")) {
                AstNode commaNode = new AstNode("COMMA");
                funcRParamsNode.addChild(commaNode);
                AstRecursion.nextSym();
            } else {
                break;
            }
        }
    }

    private static void MulExp(AstNode constExpNode) throws IOException {
        AstNode mulExpNode = new AstNode("<MulExp>");
        UnaryExp(mulExpNode);
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
                UnaryExp(father);
            } else if (getPreSym().equals("DIV")) {
                AstNode divNode = new AstNode("DIV");
                father.addChild(divNode);
                AstRecursion.nextSym();
                UnaryExp(father);
            } else if (getPreSym().equals("MOD")) {
                AstNode modNode = new AstNode("MOD");
                father.addChild(modNode);
                AstRecursion.nextSym();
                UnaryExp(father);
            }
        }
        constExpNode.addChild(father);
    }

    private static void AddExp(AstNode constExpNode) throws IOException {
        AstNode addExpNode = new AstNode("<AddExp>");
        MulExp(addExpNode);
        AstNode father = addExpNode;
        while (getPreSym().equals("PLUS") || getPreSym().equals("MINU")) {
            AstNode extraAddExpNode = new AstNode("<AddExp>");
            extraAddExpNode.addChild(father);
            father = extraAddExpNode;
            if (getPreSym().equals("PLUS")) {
                AstNode plusNode = new AstNode("PLUS");
                father.addChild(plusNode);
                AstRecursion.nextSym();
                MulExp(father);
            } else if (getPreSym().equals("MINU")) {
                AstNode minuNode = new AstNode("MINU");
                father.addChild(minuNode);
                AstRecursion.nextSym();
                MulExp(father);
            }
        }
        constExpNode.addChild(father);
    }

    private static void RelExp(AstNode equExpNode) throws IOException {
        AstNode relExpNode = new AstNode("<RelExp>");
        AddExp(relExpNode);
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
                AddExp(father);
            } else if (getPreSym().equals("LEQ")) {
                AstNode leqNode = new AstNode("LEQ");
                father.addChild(leqNode);
                AstRecursion.nextSym();
                AddExp(father);
            } else if (getPreSym().equals("GRE")) {
                AstNode greNode = new AstNode("GRE");
                father.addChild(greNode);
                AstRecursion.nextSym();
                AddExp(father);
            } else if (getPreSym().equals("GEQ")) {
                AstNode geqNode = new AstNode("GEQ");
                father.addChild(geqNode);
                AstRecursion.nextSym();
                AddExp(father);
            }
        }
        equExpNode.addChild(father);
    }

    private static void EqExp(AstNode lAndExpNode) throws IOException {
        AstNode equExpNode = new AstNode("<EqExp>");
        RelExp(equExpNode);
        AstNode father = equExpNode;
        while (getPreSym().equals("EQL") || getPreSym().equals("NEQ")) {
            AstNode extraEqExpNode = new AstNode("<EqExp>");
            extraEqExpNode.addChild(father);
            father = extraEqExpNode;
            if (getPreSym().equals("EQL")) {
                AstNode eqlNode = new AstNode("EQL");
                father.addChild(eqlNode);
                AstRecursion.nextSym();
                RelExp(father);
            } else if (getPreSym().equals("NEQ")) {
                AstNode neqNode = new AstNode("NEQ");
                father.addChild(neqNode);
                AstRecursion.nextSym();
                RelExp(father);
            }
        }
        lAndExpNode.addChild(father);
    }

    private static void LAndExp(AstNode lOrExpNode) throws IOException {
        AstNode lAndExpNode = new AstNode("<LAndExp>");
        EqExp(lAndExpNode);
        AstNode father = lAndExpNode;
        while (getPreSym().equals("AND")) {
            AstNode extraLAndExpNode = new AstNode("<LAndExp>");
            extraLAndExpNode.addChild(father);
            father = extraLAndExpNode;
            AstNode andNode = new AstNode("AND");
            father.addChild(andNode);
            AstRecursion.nextSym();
            EqExp(father);
        }
        lOrExpNode.addChild(father);
    }

    private static void LOrExp(AstNode condNode) throws IOException {
        AstNode lOrExpNode = new AstNode("<LOrExp>");
        LAndExp(lOrExpNode);
        AstNode father = lOrExpNode;
        while (getPreSym().equals("OR")) {
            AstNode extraLorExpNode = new AstNode("<LOrExp>");
            extraLorExpNode.addChild(father);
            father = extraLorExpNode;
            AstNode orNode = new AstNode("OR");
            father.addChild(orNode);
            AstRecursion.nextSym();
            LAndExp(father);
        }
        condNode.addChild(father);
    }

    public static void ConstExp(AstNode constDeclNode) throws IOException {
        AstNode constExpNode = new AstNode("<ConstExp>");
        constDeclNode.addChild(constExpNode);
        if (Judge.IsAddExp()) {
            AddExp(constExpNode);
        } else {
            ErrorController.DefinerPrintError();
        }
    }

    public static void Ident(AstNode constExpNode) {
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
