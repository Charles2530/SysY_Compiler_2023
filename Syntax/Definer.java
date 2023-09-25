package Syntax;

import LLVM.ErrorController;

public class Definer {

    public static boolean IsConstDecl() {
        return getPreSym().equals("CONSTTK");
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
        return getPreSym().equals("IDENFR");
    }

    private static void ConstDef(AstNode constDeclNode) {
        AstNode idenfrNode = new AstNode("IDENFR");
        constDeclNode.addChild(idenfrNode);
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
        MulExp(addExpNode);
        if (getPreSym().equals("PLUS")) {
            AstNode plusNode = new AstNode("PLUS");
            addExpNode.addChild(plusNode);
            AstRecursion.nextSym();
            AddExp(addExpNode);
        } else if (getPreSym().equals("MINU")) {
            AstNode minuNode = new AstNode("MINU");
            addExpNode.addChild(minuNode);
            AstRecursion.nextSym();
            AddExp(addExpNode);
        }
    }

    private static void MulExp(AstNode constExpNode) {
        AstNode mulExpNode = new AstNode("<MulExp>");
        constExpNode.addChild(mulExpNode);
        UnaryExp(mulExpNode);
        if (getPreSym().equals("MULT")) {
            AstNode multNode = new AstNode("MULT");
            mulExpNode.addChild(multNode);
            AstRecursion.nextSym();
            MulExp(mulExpNode);
        } else if (getPreSym().equals("DIV")) {
            AstNode divNode = new AstNode("DIV");
            mulExpNode.addChild(divNode);
            AstRecursion.nextSym();
            MulExp(mulExpNode);
        } else if (getPreSym().equals("MOD")) {
            AstNode modNode = new AstNode("MOD");
            mulExpNode.addChild(modNode);
            AstRecursion.nextSym();
            MulExp(mulExpNode);
        }
    }

    private static void UnaryExp(AstNode constExpNode) {
        AstNode unaryExpNode = new AstNode("<UnaryExp>");
        constExpNode.addChild(unaryExpNode);
        if (IsPrimaryExp()) {
            PrimaryExp(unaryExpNode);
        } else if (IsIdent()) {
            Ident(unaryExpNode);
        } else if (IsUnaryOp()) {
            UnaryOp(unaryExpNode);
            if (IsUnaryExp()) {
                UnaryExp(unaryExpNode);
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
        AstNode idenfrNode = new AstNode("IDENFR");
        constExpNode.addChild(idenfrNode);
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
        return IsUnaryExp();
    }

    private static boolean IsAddExp() {
        return IsMulExp();
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
        AstNode vardefNode = new AstNode("<VarDef>");
        varDeclNode.addChild(vardefNode);
        AstNode idenfrNode = new AstNode("IDENFR");
        vardefNode.addChild(idenfrNode);
        AstRecursion.nextSym();
        while (getPreSym().equals("LBRACK")) {
            AstNode lbrackNode = new AstNode("LBRACK");
            vardefNode.addChild(lbrackNode);
            AstRecursion.nextSym();
            if (IsConstExp()) {
                ConstExp(vardefNode);
            }
            if (getPreSym().equals("RBRACK")) {
                AstNode rbrackNode = new AstNode("RBRACK");
                vardefNode.addChild(rbrackNode);
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
        AstNode stmtNode = new AstNode("<Stmt>");
        blockNode.addChild(stmtNode);
        if (IsLVal()) {
            LVal(stmtNode);
            if (getPreSym().equals("ASSIGN")) {
                AstNode assignNode = new AstNode("ASSIGN");
                stmtNode.addChild(assignNode);
                AstRecursion.nextSym();
                if (IsExp()) {
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
                    stmtNode.addChild(semicnNode);
                    AstRecursion.nextSym();
                } else {
                    ErrorController.DefinerPrintError();
                }
            } else {
                ErrorController.DefinerPrintError();
            }
        } else if (IsExp()) {
            Exp(stmtNode);
            if (getPreSym().equals("SEMICN")) {
                AstNode semicnNode = new AstNode("SEMICN");
                stmtNode.addChild(semicnNode);
                AstRecursion.nextSym();
            } else {
                ErrorController.DefinerPrintError();
            }
        } else if (IsBlock()) {
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

    private static void PrintfStmt(AstNode blockNode) {
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
                    if (IsExp()) {
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
                ErrorController.DefinerPrintError();
            }
            if (getPreSym().equals("SEMICN")) {
                AstNode semicnNode = new AstNode("SEMICN");
                blockNode.addChild(semicnNode);
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
            Exp(blockNode);
        }
        if (getPreSym().equals("SEMICN")) {
            AstNode semicnNode = new AstNode("SEMICN");
            blockNode.addChild(semicnNode);
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

    private static void Cond(AstNode forStmtNode) {
        AstNode condNode = new AstNode("<Cond>");
        forStmtNode.addChild(condNode);
        LOrExp(condNode);
    }

    private static void LOrExp(AstNode condNode) {
        AstNode lOrExpNode = new AstNode("<LOrExp>");
        condNode.addChild(lOrExpNode);
        LAndExp(lOrExpNode);
        if (getPreSym().equals("OR")) {
            AstNode orNode = new AstNode("OR");
            lOrExpNode.addChild(orNode);
            AstRecursion.nextSym();
            LOrExp(lOrExpNode);
        }
    }

    private static void LAndExp(AstNode lOrExpNode) {
        AstNode lAndExpNode = new AstNode("<LAndExp>");
        lOrExpNode.addChild(lAndExpNode);
        EquExp(lAndExpNode);
        if (getPreSym().equals("AND")) {
            AstNode andNode = new AstNode("AND");
            lAndExpNode.addChild(andNode);
            AstRecursion.nextSym();
            LAndExp(lAndExpNode);
        }
    }

    private static void EquExp(AstNode lAndExpNode) {
        AstNode equExpNode = new AstNode("<EquExp>");
        lAndExpNode.addChild(equExpNode);
        RelExp(equExpNode);
        if (getPreSym().equals("EQL")) {
            AstNode eqlNode = new AstNode("EQL");
            equExpNode.addChild(eqlNode);
            AstRecursion.nextSym();
            EquExp(equExpNode);
        } else if (getPreSym().equals("NEQ")) {
            AstNode neqNode = new AstNode("NEQ");
            equExpNode.addChild(neqNode);
            AstRecursion.nextSym();
            EquExp(equExpNode);
        }
    }

    private static void RelExp(AstNode equExpNode) {
        AstNode relExpNode = new AstNode("<RelExp>");
        equExpNode.addChild(relExpNode);
        AddExp(relExpNode);
        if (getPreSym().equals("LSS")) {
            AstNode lssNode = new AstNode("LSS");
            relExpNode.addChild(lssNode);
            AstRecursion.nextSym();
            RelExp(relExpNode);
        } else if (getPreSym().equals("LEQ")) {
            AstNode leqNode = new AstNode("LEQ");
            relExpNode.addChild(leqNode);
            AstRecursion.nextSym();
            RelExp(relExpNode);
        } else if (getPreSym().equals("GRE")) {
            AstNode greNode = new AstNode("GRE");
            relExpNode.addChild(greNode);
            AstRecursion.nextSym();
            RelExp(relExpNode);
        } else if (getPreSym().equals("GEQ")) {
            AstNode geqNode = new AstNode("GEQ");
            relExpNode.addChild(geqNode);
            AstRecursion.nextSym();
            RelExp(relExpNode);
        }
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

    private static boolean IsCond() {
        return IsUnaryExp();
    }

    public static boolean IsBlockItem() {
        return IsConstDecl() || IsVarDecl() || IsStmt();
    }

    public static void BlockItem(AstNode blockNode) {
        AstNode blockItemNode = new AstNode("<BlockItem>");
        blockNode.addChild(blockItemNode);
        if (IsConstDecl()) {
            AstNode declNode = new AstNode("<Decl>");
            blockItemNode.addChild(declNode);
            ConstDecl(declNode);
        } else if (IsVarDecl()) {
            AstNode declNode = new AstNode("<Decl>");
            blockItemNode.addChild(declNode);
            VarDecl(declNode);
        } else if (IsStmt()) {
            Stmt(blockItemNode);
        } else {
            ErrorController.DefinerPrintError();
        }
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

    private static String getPreSym() {
        return AstRecursion.getPreSymToken().getReservedWord();
    }
}
