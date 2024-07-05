//import org.antlr.v4.runtime.Token;
//import org.antlr.v4.runtime.tree.TerminalNode;
//
//import java.util.List;
//import java.util.Set;
//import java.util.Stack;
//
//public class highLightListener extends SysYParserBaseListener{
//    String printStyle = "\u001B[0m";
//    Token lastToken = null;
//    boolean inDecl = false;
//    boolean inNotBLOCK = false;
//    int inBlock = 0;
//    boolean inFuncDef = false;
//    List<String> rainbowBrackets = List.of("\u001B[91m", "\u001B[92m", "\u001B[93m", "\u001B[94m", "\u001B[95m", "\u001B[96m");
//    int bracketNum = 0;
//    boolean veryFirst = true;
//    boolean spaceAfterFlag = false;
//    boolean spaceBeforeFlag = false;
//    boolean elseFlag = false;
//    boolean ifFlag = false;
//    boolean whileFlag = false;
//    boolean returnDirFlag = false;
//    boolean onceFlag = false;
//    boolean invalidL_BRACE = false;
//    boolean printSpace = false;
//    Stack<String> bracketStack = new Stack<String>();
//
//    int tabNum = 0;
//    int enterWHILE = 0;
//    boolean newLineBefore = false;
//    boolean tag = false;
//    boolean notDealWithElse = false;
//    int plusIF = 0;
//    Stack<String> ifOrWhile = new Stack<String>();
//
//    Set<String> keyWords = Set.of("CONST", "INT", "VOID", "IF", "ELSE", "WHILE", "BREAK", "CONTINUE", "RETURN");
//    Set<String> someKeyWords = Set.of("CONST", "INT", "VOID", "IF", "ELSE", "WHILE", "RETURN");
//    Set<String> operators = Set.of("PLUS", "MINUS", "MUL", "DIV", "MOD", "ASSIGN", "EQ", "NEQ", "LT", "GT", "LE", "GE", "NOT", "AND", "OR", "COMMA", "SEMICOLON");
//    Set<String> brackets = Set.of("L_PAREN", "R_PAREN", "L_BRACE", "R_BRACE", "L_BRACKT", "R_BRACKT");
//    Set<String> doubleOp = Set.of("PLUS", "MINUS", "MUL", "DIV", "MOD", "ASSIGN", "EQ", "NEQ", "LT", "GT", "LE", "GE", "AND", "OR");
//    @Override
//    public void enterFuncDef(SysYParser.FuncDefContext ctx) { // 进入函数定义
//        System.out.print("\u001B[0m");
//        inFuncDef = true;
//        if (!veryFirst) {
//            System.out.println();
//            newLineBefore = true;
//        }
//    }
//    @Override
//    public void exitFuncDef(SysYParser.FuncDefContext ctx) { // 退出函数定义
//        inFuncDef = false;
//    }
//    @Override
//    public void enterFuncName(SysYParser.FuncNameContext ctx) { // 进入函数名
//        printStyle = "\u001B[93m";
//    }
//    @Override
//    public void enterBlock(SysYParser.BlockContext ctx) { // 进入块
//        if (SysYParser.ruleNames[ctx.getParent().getRuleIndex()].equals("funcDef")) {
//
//        } else if (SysYParser.ruleNames[ctx.getParent().getRuleIndex()].equals("stmt") && (elseFlag || ifFlag || whileFlag)) {
//            if (whileFlag && ifOrWhile.peek().equals("WHILE")) {
//                enterWHILE--;
//                tabNum--;
//                whileFlag = false;
//            }
//            if (ifFlag && ifOrWhile.peek().equals("IF")) {
//                tabNum--;
//                ifFlag = false;
//            }
//            if (elseFlag) {
//                tabNum--;
//                elseFlag = false;
//            }
//
//        } else if (SysYParser.ruleNames[ctx.getParent().getRuleIndex()].equals("stmt")) {
//            newLineBefore = true;
//        }
//        inBlock++;
//        tabNum++;
//    }
//    @Override
//    public void exitBlock(SysYParser.BlockContext ctx) { // 退出块
//        inBlock--;
//    }
//    @Override
//    public void enterNOT_BLOCK(SysYParser.NOT_BLOCKContext ctx) { // 进入非块的语句
//        printStyle = "\u001B[97m";
//        inNotBLOCK = true;
//        newLineBefore = true;
//    }
//    @Override
//    public void exitNOT_BLOCK(SysYParser.NOT_BLOCKContext ctx) { // 退出非块的语句
//        inNotBLOCK = false;
//    }
//    @Override
//    public void enterRERURNDIR(SysYParser.RERURNDIRContext ctx) { // 进入return语句
//        printStyle = "\u001B[97m";
//        inNotBLOCK = true;
//        newLineBefore = true;
//        returnDirFlag = true;
//    }
//    @Override
//    public void exitRERURNDIR(SysYParser.RERURNDIRContext ctx) { // 退出return语句
//        inNotBLOCK = false;
//        returnDirFlag = false;
//    }
//
//    @Override
//    public void enterIF_ELSE(SysYParser.IF_ELSEContext ctx) { // 进入if-else语句
//        printStyle = "\u001B[97m";
//        inNotBLOCK = true;
//        if (elseFlag) {
//            tabNum--;
//        }
//        if (!elseFlag && !veryFirst) {
//            newLineBefore = true;
//        } else {
//            elseFlag = false;
//        }
//    }
//
//    @Override
//    public void exitIF_ELSE(SysYParser.IF_ELSEContext ctx) { // 退出if-else语句
//        inNotBLOCK = false;
//        if (plusIF > 0) {
//            tabNum--;
//            plusIF--;
//        }
//        if (ifFlag) {
//            tabNum--;
//            ifFlag = false;
//        }
//    }
//
//    @Override
//    public void enterWHILE(SysYParser.WHILEContext ctx) { // 进入while语句
//        printStyle = "\u001B[97m";
//        inNotBLOCK = true;
//        newLineBefore = true;
//        enterWHILE++;
//    }
//    @Override
//    public void exitWHILE(SysYParser.WHILEContext ctx) { // 退出while语句
//        inNotBLOCK = false;
//        if (enterWHILE > 0) {
//            tabNum--;
//            enterWHILE--;
//        }
//    }
//
//
//    @Override
//    public void enterDecl(SysYParser.DeclContext ctx) { // 进入声明， 需要打印下划线
//        printStyle = "\u001B[95m";
//        if (!veryFirst) {
//            newLineBefore = true;
//        }
//        inDecl = true;
//    }
//    @Override
//    public void exitDecl(SysYParser.DeclContext ctx) { // 退出声明， 需要取消下划线
//        inDecl = false;
//    }
//
//    @Override
//    public void enterUnaryOp(SysYParser.UnaryOpContext ctx) {
//        onceFlag = true;
//    }
//    @Override
//    public void exitUnaryOp(SysYParser.UnaryOpContext ctx) {
//        onceFlag = false;
//    }
//
//    @Override
//    public void visitTerminal(TerminalNode node) {
//        Token token = node.getSymbol();
//        String type = SysYLexer.VOCABULARY.getSymbolicName(token.getType());
//
//        if (notDealWithElse && (type.equals("IF") || type.equals("L_BRACE"))) {
//            notDealWithElse = false;
//            System.out.print("\u001B[0m");
//            System.out.print(" ");
//        } else if (notDealWithElse) {
//            notDealWithElse = false;
//        }
//
//        if (elseFlag && type.equals("SEMICOLON")) {
//            elseFlag = false;
//            tag = true;
//        } else if (ifFlag && type.equals("SEMICOLON") && ifOrWhile.peek().equals("IF")) {
//            ifFlag = false;
//            ifOrWhile.pop();
//            tag = true;
//        } else if (whileFlag && type.equals("SEMICOLON") && ifOrWhile.peek().equals("WHILE")) {
//            whileFlag = false;
//            ifOrWhile.pop();
//            enterWHILE--;
//            tag = true;
//        }
//
//        if (type.equals("EOF")) {
//            return;
//        }
//        if (keyWords.contains(type)) {
//
//            if (type.equals("ELSE")) {
//                newLineBefore = true;
//                elseFlag = true;
//                tabNum++;
//            } else if (type.equals("IF")) {
//                if (ifFlag) {
//                    plusIF++;
//                }
//                ifFlag = true;
//                tabNum++;
//                ifOrWhile.push("IF");
//            } else if (type.equals("WHILE")) {
//                whileFlag = true;
//                tabNum++;
//                ifOrWhile.push("WHILE");
//            }
//
//            printStyle = "\u001B[96m";
//        } else if (operators.contains(type)) {
//            printStyle = "\u001B[91m";
//        } else if (type.equals("INTEGER_CONST")) {
//            printStyle = "\u001B[35m";
//        } else if (brackets.contains(type)) {
//            String last = bracketStack.empty() ? "" : bracketStack.peek();
//            if (last.equals("L_PAREN") && type.equals("R_PAREN") || last.equals("L_BRACE") && type.equals("R_BRACE") || last.equals("L_BRACKT") && type.equals("R_BRACKT")) {
//                bracketStack.pop();
//                bracketNum--;
//                printStyle = rainbowBrackets.get(bracketNum % 6);
//            } else {
//                bracketStack.push(type);
//                printStyle = rainbowBrackets.get(bracketNum % 6);
//                bracketNum++;
//            }
//
//            if (type.equals("R_BRACE")) {
//                if (!inDecl) {
//                    newLineBefore = true;
//                }
//                if (inBlock > 0) {
//                    if (!inDecl) {
//                        tabNum--;
//                    }
//                }
//            }
//
//        } else {
//
//        }
//
//        if (someKeyWords.contains(type) && !returnDirFlag) {
//            spaceAfterFlag = true;
//        } else if (doubleOp.contains(type) && !onceFlag) {
//            spaceBeforeFlag = true;
//            spaceAfterFlag = true;
//        } else if (type.equals("COMMA")) {
//            spaceAfterFlag = true;
//        } else if (type.equals("L_BRACE")) {
//            if (!invalidL_BRACE) {
//                if (lastToken != null) {
//                    if (lastToken.getType() != 27) {
//                        spaceBeforeFlag = true;
//                    }
//                }
//            } else {
//                invalidL_BRACE = false;
//            }
//        }
//
//        if (spaceBeforeFlag && !newLineBefore) {
//            if (!printSpace) {
//                System.out.print("\u001B[0m");
//                System.out.print(" ");
//            }
//            spaceBeforeFlag = false;
//        } else if (spaceBeforeFlag) {
//            spaceBeforeFlag = false;
//        }
//        printSpace = false;
//
//        if (newLineBefore) {
//            System.out.print("\u001B[0m");
//            System.out.println();
//            if (type.equals("L_BRACE")) {
//                System.out.print("    ".repeat(Math.max(tabNum - 1, 0)));
//            } else if (type.equals("ELSE") || type.equals("IF") || type.equals("WHILE")) {
//                System.out.print("    ".repeat(Math.max(tabNum - 1, 0)));
//            } else {
//                System.out.print("    ".repeat(Math.max(tabNum, 0)));
//            }
//            newLineBefore = false;
//        }
//
//        if (inDecl) { // 如果在声明中， 需要打印下划线
//            printStyle += "\u001B[4m";
//        }
//        System.out.print(printStyle + token.getText());
//
//        if (spaceAfterFlag) {
//            if (type.equals("ELSE")) {
//                notDealWithElse = true;
//                spaceAfterFlag = false;
//                printSpace = true;
//            } else {
//                System.out.print("\u001B[0m");
//                System.out.print(" ");
//                spaceAfterFlag = false;
//                printSpace = true;
//            }
//        }
//
//        if (!inDecl && !inNotBLOCK) {
//            printStyle = "\u001B[0m";
//        } else if (inDecl) {
//            printStyle = "\u001B[95m";
//        } else {
//            printStyle = "\u001B[97m";
//        }
//
//        veryFirst = false;
//        lastToken = token;
//
//        if (tag) {
//            tabNum--;
//            tag = false;
//        }
//    }
//}
