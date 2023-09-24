package Syntax;

import LLVM.ErrorController;

public class Definer {

    public static boolean IsConstDecl() {
        if (getPreSym().equals("CONSTTK")) {
            return true;
        }
        return false;
    }

    public static void ConstDecl(AstNode blockNode) {
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
                if (IsConstDef()) {
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
            ErrorController.DefinerPrintError();
        }
    }

    private static boolean IsConstDef() {
        if (getPreSym().equals("IDENFR")) {
            return true;
        }
        return false;
    }

    private static void ConstDef(AstNode constDeclNode) {
        AstNode identNode = new AstNode("<Ident>");
        constDeclNode.addChild(identNode);
        AstNode idenfrNode = new AstNode("IDENFR");
        identNode.addChild(idenfrNode);
        AstRecursion.nextSym();
        //适配数组的文法
        while (getPreSym().equals("LBRACK")) {
            AstNode lbrackNode = new AstNode("LBRACK");
            constDeclNode.addChild(lbrackNode);
            AstRecursion.nextSym();
            if (IsConstExp()) {
                ConstExp(constDeclNode);
            }
            if (getPreSym().equals("RBRACK")) {
                AstNode rbrackNode = new AstNode("RBRACK");
                constDeclNode.addChild(rbrackNode);
                AstRecursion.nextSym();
            } else {
                ErrorController.DefinerPrintError();
            }
        }
        if (getPreSym().equals("ASSIGN")) {
            AstNode assignNode = new AstNode("ASSIGN");
            constDeclNode.addChild(assignNode);
            AstRecursion.nextSym();
            if (getPreSym().equals("INTCON")) {
                AstNode intconNode = new AstNode("INTCON");
                constDeclNode.addChild(intconNode);
                AstRecursion.nextSym();
            } else {
                ErrorController.DefinerPrintError();
            }
        } else {
            ErrorController.DefinerPrintError();
        }
    }

    public static void ConstExp(AstNode constDeclNode) {
        AstNode constExpNode = new AstNode("<ConstExp>");
        constDeclNode.addChild(constExpNode);
        if (IsAddExp()) {
            AddExp(constExpNode);
        } else {
            ErrorController.DefinerPrintError();
        }
    }

    private static void AddExp(AstNode constExpNode) {
        AstNode addExpNode = new AstNode("<AddExp>");
        constExpNode.addChild(addExpNode);
        if (IsMulExp()) {
            MulExp(constExpNode);
        } else if (IsAddExp()) {
            MulExp(constExpNode);
            if (getPreSym().equals("PLUS")) {
                AstNode plusNode = new AstNode("PLUS");
                constExpNode.addChild(plusNode);
                AstRecursion.nextSym();
                if (IsAddExp()) {
                    AddExp(constExpNode);
                } else {
                    ErrorController.DefinerPrintError();
                }
            } else if (getPreSym().equals("MINU")) {
                AstNode minuNode = new AstNode("MINU");
                constExpNode.addChild(minuNode);
                AstRecursion.nextSym();
                if (IsAddExp()) {
                    AddExp(constExpNode);
                } else {
                    ErrorController.DefinerPrintError();
                }
            }
        } else {
            ErrorController.DefinerPrintError();
        }
    }

    private static void MulExp(AstNode constExpNode) {
        AstNode mulExpNode = new AstNode("<MulExp>");
        constExpNode.addChild(mulExpNode);
        if (IsUnaryExp()) {
            UnaryExp(constExpNode);
        } else if (IsMulExp()) {
            UnaryExp(constExpNode);
            //这里存在左递归，需要修改
            if (getPreSym().equals("MULT")) {
                AstNode multNode = new AstNode("MULT");
                constExpNode.addChild(multNode);
                AstRecursion.nextSym();
                if (IsUnaryExp()) {
                    UnaryExp(constExpNode);
                } else {
                    ErrorController.DefinerPrintError();
                }
            } else if (getPreSym().equals("DIV")) {
                AstNode divNode = new AstNode("DIV");
                constExpNode.addChild(divNode);
                AstRecursion.nextSym();
                if (IsUnaryExp()) {
                    UnaryExp(constExpNode);
                } else {
                    ErrorController.DefinerPrintError();
                }
            } else if (getPreSym().equals("MOD")) {
                AstNode modNode = new AstNode("MOD");
                constExpNode.addChild(modNode);
                AstRecursion.nextSym();
                if (IsUnaryExp()) {
                    UnaryExp(constExpNode);
                } else {
                    ErrorController.DefinerPrintError();
                }
            }
        } else {
            ErrorController.DefinerPrintError();
        }
    }

    private static void UnaryExp(AstNode constExpNode) {
        if (IsPrimaryExp()) {
            PrimaryExp(constExpNode);
        } else if (IsIdent()) {
            Ident(constExpNode);
        } else if (IsUnaryOp()) {
            UnaryOp(constExpNode);
            if (IsUnaryExp()) {
                UnaryExp(constExpNode);
            } else {
                ErrorController.DefinerPrintError();
            }
        } else {
            ErrorController.DefinerPrintError();
        }
    }

    private static void UnaryOp(AstNode constExpNode) {
        if (getPreSym().equals("PLUS")) {
            AstNode plusNode = new AstNode("PLUS");
            constExpNode.addChild(plusNode);
            AstRecursion.nextSym();
        } else if (getPreSym().equals("MINU")) {
            AstNode minuNode = new AstNode("MINU");
            constExpNode.addChild(minuNode);
            AstRecursion.nextSym();
        } else if (getPreSym().equals("NOT")) {
            AstNode modNode = new AstNode("NOT");
            constExpNode.addChild(modNode);
            AstRecursion.nextSym();
        } else {
            ErrorController.DefinerPrintError();
        }
    }

    public static void Ident(AstNode constExpNode) {
        AstNode identNode = new AstNode("<Ident>");
        constExpNode.addChild(identNode);
        AstNode idenfrNode = new AstNode("IDENFR");
        identNode.addChild(idenfrNode);
        AstRecursion.nextSym();
        if (getPreSym().equals("LBRACK")) {
            AstNode lbrackNode = new AstNode("LBRACK");
            constExpNode.addChild(lbrackNode);
            AstRecursion.nextSym();
            if (IsFuncRParams()) {
                FuncRParams(constExpNode);
            }
            if (getPreSym().equals("RBRACK")) {
                AstNode rbrackNode = new AstNode("RBRACK");
                constExpNode.addChild(rbrackNode);
                AstRecursion.nextSym();
            } else {
                ErrorController.DefinerPrintError();
            }
        }
    }

    private static void FuncRParams(AstNode constExpNode) {
        AstNode funcRParamsNode = new AstNode("<FuncRParams>");
        constExpNode.addChild(funcRParamsNode);
        while (IsExp()) {
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

    private static boolean IsExp() {
        return IsAddExp();
    }

    private static void Exp(AstNode funcRParamsNode) {
        AstNode expNode = new AstNode("<Exp>");
        funcRParamsNode.addChild(expNode);
        if (IsAddExp()) {
            AddExp(expNode);
        } else {
            ErrorController.DefinerPrintError();
        }
    }

    private static boolean IsFuncRParams() {
        return IsExp();
    }

    private static void PrimaryExp(AstNode constExpNode) {
        AstNode primaryExpNode = new AstNode("<PrimaryExp>");
        constExpNode.addChild(primaryExpNode);
        if (getPreSym().equals("LPARENT")) {
            AstNode lparentNode = new AstNode("LPARENT");
            primaryExpNode.addChild(lparentNode);
            AstRecursion.nextSym();
            if (IsExp()) {
                Exp(primaryExpNode);
            } else {
                ErrorController.DefinerPrintError();
            }
            if (getPreSym().equals("RPARENT")) {
                AstNode rparentNode = new AstNode("RPARENT");
                primaryExpNode.addChild(rparentNode);
                AstRecursion.nextSym();
            } else {
                ErrorController.DefinerPrintError();
            }
        } else if (IsLVal()) {
            LVal(primaryExpNode);
        } else if (IsNumber()) {
            NumberCall(primaryExpNode);
        } else {
            ErrorController.DefinerPrintError();
        }
    }

    private static void NumberCall(AstNode primaryExpNode) {
        AstNode numberNode = new AstNode("<Number>");
        primaryExpNode.addChild(numberNode);
        AstNode intconNode = new AstNode("INTCON");
        numberNode.addChild(intconNode);
        AstRecursion.nextSym();
    }

    private static boolean IsNumber() {
        return getPreSym().equals("INTCON");
    }

    private static void LVal(AstNode primaryExpNode) {
        AstNode lValNode = new AstNode("<LVal>");
        primaryExpNode.addChild(lValNode);
        if (IsIdent()) {
            Ident(lValNode);
            while (getPreSym().equals("LBRACK")) {
                AstNode lbrackNode = new AstNode("LBRACK");
                lValNode.addChild(lbrackNode);
                AstRecursion.nextSym();
                if (IsExp()) {
                    Exp(lValNode);
                } else {
                    ErrorController.DefinerPrintError();
                }
                if (getPreSym().equals("RBRACK")) {
                    AstNode rbrackNode = new AstNode("RBRACK");
                    lValNode.addChild(rbrackNode);
                    AstRecursion.nextSym();
                } else {
                    ErrorController.DefinerPrintError();
                }
            }
        } else {
            ErrorController.DefinerPrintError();
        }
    }

    private static boolean IsLVal() {
        return IsIdent();
    }

    private static boolean IsUnaryExp() {
        return IsPrimaryExp() || IsUnaryOp() || IsIdent();
    }

    public static boolean IsIdent() {
        return getPreSym().equals("IDENFR");
    }

    private static boolean IsUnaryOp() {
        return getPreSym().equals("PLUS") || getPreSym().equals("MINU")
                || getPreSym().equals("NOT");
    }

    private static boolean IsPrimaryExp() {
        return getPreSym().equals("LPARENT") || IsLVal() || IsNumber();
    }

    private static boolean IsMulExp() {
        return IsUnaryExp() || IsMulExp();
    }

    private static boolean IsAddExp() {
        return IsMulExp() || IsAddExp();
    }

    public static boolean IsConstExp() {
        return IsAddExp();
    }

    public static boolean IsVarDecl() {
        if (getPreSym().equals("INTTK")) {
            return true;
        }
        return false;
    }

    public static void VarDecl(AstNode blockNode) {
        AstNode varDeclNode = new AstNode("<VarDecl>");
        blockNode.addChild(varDeclNode);
        AstNode intNode = new AstNode("<BType>");
        varDeclNode.addChild(intNode);
        AstNode intTkNode = new AstNode("INTTK");
        intNode.addChild(intTkNode);
        AstRecursion.nextSym();
        while (true) {
            if (IsVarDef()) {
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
            ErrorController.DefinerPrintError();
        }
    }

    private static boolean IsVarDef() {
        if (getPreSym().equals("IDENFR")) {
            return true;
        }
        return false;
    }

    private static void VarDef(AstNode varDeclNode) {
        AstNode identNode = new AstNode("<Ident>");
        varDeclNode.addChild(identNode);
        AstNode idenfrNode = new AstNode("IDENFR");
        identNode.addChild(idenfrNode);
        AstRecursion.nextSym();
        while (getPreSym().equals("LBRACK")) {
            AstNode lbrackNode = new AstNode("LBRACK");
            varDeclNode.addChild(lbrackNode);
            AstRecursion.nextSym();
            if (IsConstExp()) {
                ConstExp(varDeclNode);
            }
            if (getPreSym().equals("RBRACK")) {
                AstNode rbrackNode = new AstNode("RBRACK");
                varDeclNode.addChild(rbrackNode);
                AstRecursion.nextSym();
            } else {
                ErrorController.DefinerPrintError();
            }
        }
    }

    public static boolean IsStmt() {
        return IsLVal() || IsExp() || IsBlock() || IsReservedForStmt();
    }

    private static boolean IsReservedForStmt() {
        return getPreSym().equals("SEMICN") || getPreSym().equals("IFTK") ||
                getPreSym().equals("FORTK") || getPreSym().equals("BREAKTK") ||
                getPreSym().equals("CONTINUETK") || getPreSym().equals("RETURNTK") ||
                getPreSym().equals("PRINTFTK");
    }

    public static void Stmt(AstNode blockNode) {
        if (IsLVal()) {
            LVal(blockNode);
            if (getPreSym().equals("ASSIGN")) {
                AstNode assignNode = new AstNode("ASSIGN");
                blockNode.addChild(assignNode);
                AstRecursion.nextSym();
                if (IsExp()) {
                    Exp(blockNode);
                } else if (getPreSym().equals("GETINTTK")) {
                    AstNode getinttkNode = new AstNode("GETINTTK");
                    blockNode.addChild(getinttkNode);
                    AstRecursion.nextSym();
                    if (getPreSym().equals("LPARENT")) {
                        AstNode lparentNode = new AstNode("LPARENT");
                        blockNode.addChild(lparentNode);
                        AstRecursion.nextSym();
                        if (getPreSym().equals("RPARENT")) {
                            AstNode rparentNode = new AstNode("RPARENT");
                            blockNode.addChild(rparentNode);
                            AstRecursion.nextSym();
                        } else {
                            ErrorController.DefinerPrintError();
                        }
                    } else {
                        ErrorController.DefinerPrintError();
                    }
                } else {
                    ErrorController.DefinerPrintError();
                }
                if (getPreSym().equals("SEMICN")) {
                    AstNode semicnNode = new AstNode("SEMICN");
                    blockNode.addChild(semicnNode);
                    AstRecursion.nextSym();
                } else {
                    ErrorController.DefinerPrintError();
                }
            } else {
                ErrorController.DefinerPrintError();
            }
        } else if (IsExp()) {
            Exp(blockNode);
            if (getPreSym().equals("SEMICN")) {
                AstNode semicnNode = new AstNode("SEMICN");
                blockNode.addChild(semicnNode);
                AstRecursion.nextSym();
            } else {
                ErrorController.DefinerPrintError();
            }
        } else if (IsBlock()) {
            Block(blockNode);
        } else if (getPreSym().equals("IFTK")) {
            IfStmt(blockNode);
        } else if (getPreSym().equals("FORTK")) {
            ForStmt(blockNode);
        } else if (getPreSym().equals("BREAKTK")) {
            BreakStmt(blockNode);
        } else if (getPreSym().equals("CONTINUETK")) {
            ContinueStmt(blockNode);
        } else if (getPreSym().equals("RETURNTK")) {
            ReturnStmt(blockNode);
        } else if (getPreSym().equals("PRINTFTK")) {
            PrintfStmt(blockNode);
        }
    }

    private static void PrintfStmt(AstNode blockNode) {
        AstNode printfStmtNode = new AstNode("PRINTFTK");
        blockNode.addChild(printfStmtNode);
        if (getPreSym().equals("LPARENT")) {
            AstNode lparentNode = new AstNode("LPARENT");
            printfStmtNode.addChild(lparentNode);
            AstRecursion.nextSym();
            if (getPreSym().equals("FORMATTK")) {
                AstNode formatNode = new AstNode("FORMATTK");
                printfStmtNode.addChild(formatNode);
                AstRecursion.nextSym();
                while (getPreSym().equals("COMMA")) {
                    AstNode commaNode = new AstNode("COMMA");
                    printfStmtNode.addChild(commaNode);
                    AstRecursion.nextSym();
                    if (IsExp()) {
                        Exp(printfStmtNode);
                    } else {
                        ErrorController.DefinerPrintError();
                    }
                }
            } else {
                ErrorController.DefinerPrintError();
            }
            if (getPreSym().equals("RPARENT")) {
                AstNode rparentNode = new AstNode("RPARENT");
                printfStmtNode.addChild(rparentNode);
                AstRecursion.nextSym();
            } else {
                ErrorController.DefinerPrintError();
            }
            if (getPreSym().equals("SEMICN")) {
                AstNode semicnNode = new AstNode("SEMICN");
                printfStmtNode.addChild(semicnNode);
                AstRecursion.nextSym();
            } else {
                ErrorController.DefinerPrintError();
            }
        }
    }

    private static void ReturnStmt(AstNode blockNode) {
        AstNode returnStmtNode = new AstNode("RETURNTK");
        blockNode.addChild(returnStmtNode);
        AstRecursion.nextSym();
        if (IsExp()) {
            Exp(returnStmtNode);
        }
        if (getPreSym().equals("SEMICN")) {
            AstNode semicnNode = new AstNode("SEMICN");
            returnStmtNode.addChild(semicnNode);
            AstRecursion.nextSym();
        } else {
            ErrorController.DefinerPrintError();
        }
    }

    private static void ContinueStmt(AstNode blockNode) {
        AstNode continueStmtNode = new AstNode("CONTINUETK");
        blockNode.addChild(continueStmtNode);
        AstRecursion.nextSym();
        if (getPreSym().equals("SEMICN")) {
            AstNode semicnNode = new AstNode("SEMICN");
            continueStmtNode.addChild(semicnNode);
            AstRecursion.nextSym();
        } else {
            ErrorController.DefinerPrintError();
        }
    }

    private static void BreakStmt(AstNode blockNode) {
        AstNode breakStmtNode = new AstNode("BREAKTK");
        blockNode.addChild(breakStmtNode);
        AstRecursion.nextSym();
        if (getPreSym().equals("SEMICN")) {
            AstNode semicnNode = new AstNode("SEMICN");
            breakStmtNode.addChild(semicnNode);
            AstRecursion.nextSym();
        } else {
            ErrorController.DefinerPrintError();
        }
    }

    private static void ForStmt(AstNode blockNode) {
        AstNode forStmtNode = new AstNode("FORTK");
        blockNode.addChild(forStmtNode);
        AstRecursion.nextSym();
        if (getPreSym().equals("LPARENT")) {
            AstNode lparentNode = new AstNode("LPARENT");
            forStmtNode.addChild(lparentNode);
            AstRecursion.nextSym();
            if (IsForStmtVal()) {
                ForStmtVal(forStmtNode);
            }
            if (getPreSym().equals("SEMICN")) {
                AstNode semicnNode = new AstNode("SEMICN");
                forStmtNode.addChild(semicnNode);
                AstRecursion.nextSym();
            } else {
                ErrorController.DefinerPrintError();
            }
            if (IsCond()) {
                Cond(forStmtNode);
            }
            if (getPreSym().equals("SEMICN")) {
                AstNode semicnNode = new AstNode("SEMICN");
                forStmtNode.addChild(semicnNode);
                AstRecursion.nextSym();
            } else {
                ErrorController.DefinerPrintError();
            }
            if (IsForStmtVal()) {
                ForStmtVal(forStmtNode);
            }
            if (getPreSym().equals("RPARENT")) {
                AstNode rparentNode = new AstNode("RPARENT");
                forStmtNode.addChild(rparentNode);
                AstRecursion.nextSym();
            } else {
                ErrorController.DefinerPrintError();
            }
            if (IsStmt()) {
                Stmt(forStmtNode);
            } else {
                ErrorController.DefinerPrintError();
            }
        } else {
            ErrorController.DefinerPrintError();
        }
    }

    private static boolean IsCond() {
        return IsLOrExp();
    }

    private static boolean IsLOrExp() {
        return IsLAndExp();
    }

    private static void Cond(AstNode forStmtNode) {
        AstNode condNode = new AstNode("<Cond>");
        forStmtNode.addChild(condNode);
        if (IsLOrExp()) {
            LOrExp(condNode);
        } else {
            ErrorController.DefinerPrintError();
        }
    }

    private static void LOrExp(AstNode condNode) {
        AstNode lOrExpNode = new AstNode("<LOrExp>");
        condNode.addChild(lOrExpNode);
        if (IsLOrExp()) {
            LOrExp(lOrExpNode);
            if (getPreSym().equals("OR")) {
                AstNode orNode = new AstNode("OR");
                lOrExpNode.addChild(orNode);
                AstRecursion.nextSym();
                if (IsLAndExp()) {
                    LAndExp(lOrExpNode);
                } else {
                    ErrorController.DefinerPrintError();
                }
            }
        } else if (IsLAndExp()) {
            LAndExp(lOrExpNode);
        } else {
            ErrorController.DefinerPrintError();
        }
    }

    private static void LAndExp(AstNode lOrExpNode) {
        AstNode lAndExpNode = new AstNode("<LAndExp>");
        lOrExpNode.addChild(lAndExpNode);
        if (IsLAndExp()) {
            LAndExp(lAndExpNode);
            if (getPreSym().equals("AND")) {
                AstNode andNode = new AstNode("AND");
                lAndExpNode.addChild(andNode);
                AstRecursion.nextSym();
                if (IsEquExp()) {
                    EquExp(lAndExpNode);
                } else {
                    ErrorController.DefinerPrintError();
                }
            }
        } else if (IsEquExp()) {
            EquExp(lAndExpNode);
        } else {
            ErrorController.DefinerPrintError();
        }
    }

    private static void EquExp(AstNode lAndExpNode) {
        AstNode equExpNode = new AstNode("<EquExp>");
        lAndExpNode.addChild(equExpNode);
        if (IsEquExp()) {
            EquExp(equExpNode);
            if (getPreSym().equals("EQL")) {
                AstNode eqlNode = new AstNode("EQL");
                equExpNode.addChild(eqlNode);
                AstRecursion.nextSym();
                if (IsRelExp()) {
                    RelExp(equExpNode);
                } else {
                    ErrorController.DefinerPrintError();
                }
            } else if (getPreSym().equals("NEQ")) {
                AstNode neqNode = new AstNode("NEQ");
                equExpNode.addChild(neqNode);
                AstRecursion.nextSym();
                if (IsRelExp()) {
                    RelExp(equExpNode);
                } else {
                    ErrorController.DefinerPrintError();
                }
            }
        } else if (IsRelExp()) {
            RelExp(equExpNode);
        } else {
            ErrorController.DefinerPrintError();
        }
    }

    private static void RelExp(AstNode equExpNode) {
        AstNode relExpNode = new AstNode("<RelExp>");
        equExpNode.addChild(relExpNode);
        if (IsRelExp()) {
            RelExp(relExpNode);
            if (getPreSym().equals("LSS")) {
                AstNode lssNode = new AstNode("LSS");
                relExpNode.addChild(lssNode);
                AstRecursion.nextSym();
                if (IsAddExp()) {
                    AddExp(relExpNode);
                } else {
                    ErrorController.DefinerPrintError();
                }
            } else if (getPreSym().equals("LEQ")) {
                AstNode leqNode = new AstNode("LEQ");
                relExpNode.addChild(leqNode);
                AstRecursion.nextSym();
                if (IsAddExp()) {
                    AddExp(relExpNode);
                } else {
                    ErrorController.DefinerPrintError();
                }
            } else if (getPreSym().equals("GRE")) {
                AstNode greNode = new AstNode("GRE");
                relExpNode.addChild(greNode);
                AstRecursion.nextSym();
                if (IsAddExp()) {
                    AddExp(relExpNode);
                } else {
                    ErrorController.DefinerPrintError();
                }
            } else if (getPreSym().equals("GEQ")) {
                AstNode geqNode = new AstNode("GEQ");
                relExpNode.addChild(geqNode);
                AstRecursion.nextSym();
                if (IsAddExp()) {
                    AddExp(relExpNode);
                } else {
                    ErrorController.DefinerPrintError();
                }
            }
        } else if (IsAddExp()) {
            AddExp(relExpNode);
        } else {
            ErrorController.DefinerPrintError();
        }
    }

    private static boolean IsRelExp() {
        return IsAddExp();
    }

    private static boolean IsEquExp() {
        return IsRelExp();
    }

    private static boolean IsLAndExp() {
        return IsEquExp();
    }

    private static boolean IsForStmtVal() {
        return IsLVal() || IsExp();
    }

    private static void ForStmtVal(AstNode forStmtNode) {
        if (IsLVal()) {
            LVal(forStmtNode);
            if (getPreSym().equals("ASSIGN")) {
                AstNode assignNode = new AstNode("ASSIGN");
                forStmtNode.addChild(assignNode);
                AstRecursion.nextSym();
                if (IsExp()) {
                    Exp(forStmtNode);
                } else {
                    ErrorController.DefinerPrintError();
                }
            } else {
                ErrorController.DefinerPrintError();
            }
            if (getPreSym().equals("COMMA")) {
                AstNode commaNode = new AstNode("COMMA");
                forStmtNode.addChild(commaNode);
                AstRecursion.nextSym();
                if (IsExp()) {
                    Exp(forStmtNode);
                } else {
                    ErrorController.DefinerPrintError();
                }
            }
        } else {
            ErrorController.DefinerPrintError();
        }
    }

    private static void IfStmt(AstNode blockNode) {
        AstNode ifStmtNode = new AstNode("IFTK");
        blockNode.addChild(ifStmtNode);
        AstRecursion.nextSym();
        if (getPreSym().equals("LPARENT")) {
            AstNode lparentNode = new AstNode("LPARENT");
            ifStmtNode.addChild(lparentNode);
            AstRecursion.nextSym();
            if (IsCond()) {
                Cond(ifStmtNode);
            } else {
                ErrorController.DefinerPrintError();
            }
            if (getPreSym().equals("RPARENT")) {
                AstNode rparentNode = new AstNode("RPARENT");
                ifStmtNode.addChild(rparentNode);
                AstRecursion.nextSym();
            } else {
                ErrorController.DefinerPrintError();
            }
            if (IsStmt()) {
                Stmt(ifStmtNode);
            } else {
                ErrorController.DefinerPrintError();
            }
            if (getPreSym().equals("ELSETK")) {
                AstNode elseTkNode = new AstNode("ELSETK");
                ifStmtNode.addChild(elseTkNode);
                AstRecursion.nextSym();
                if (IsStmt()) {
                    Stmt(ifStmtNode);
                } else {
                    ErrorController.DefinerPrintError();
                }
            }
        } else {
            ErrorController.DefinerPrintError();
        }
    }

    public static boolean IsBlockItem() {
        return IsConstDecl() || IsVarDecl() || IsStmt();
    }

    public static void BlockItem(AstNode blockNode) {
        if (IsConstDecl()) {
            ConstDecl(blockNode);
        } else if (IsVarDecl()) {
            VarDecl(blockNode);
        } else if (IsStmt()) {
            Stmt(blockNode);
        } else {
            ErrorController.DefinerPrintError();
        }
    }

    private static String getPreSym() {
        return AstRecursion.getPreSymToken().getReservedWord();
    }

    public static boolean IsBlock() {
        return getPreSym().equals("LBRACE");
    }

    public static void Block(AstNode parent) {
        AstNode blockNode = new AstNode("<Block>");
        parent.addChild(blockNode);
        if (getPreSym().equals("LBRACE")) {
            AstNode lbraceNode = new AstNode("LBRACE");
            blockNode.addChild(lbraceNode);
            AstRecursion.nextSym();
            while (Definer.IsBlockItem()) {
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
}
