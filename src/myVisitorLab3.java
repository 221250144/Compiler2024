//import java.util.ArrayList;
//import java.util.Objects;
//import java.util.Stack;
//
//public class myVisitorLab3 extends SysYParserBaseVisitor<defInfo> {
//    private Scope curScope = new Scope(); // 当前作用域
//    ArrayList<Type> paramsTyList = new ArrayList<>();
//    ArrayList<String> paramsNameList = new ArrayList<>();
//    String FuncNameTmp = "";
//    int blockDepth = 0;
//
//    @Override
//    public defInfo visitFuncDef(SysYParser.FuncDefContext ctx) {
//        paramsTyList.clear();
//        paramsNameList.clear();
//        String funcName = ctx.funcName().getText();
//        FuncNameTmp = funcName;
//        if (curScope.find(funcName) != null) { // curScope为当前的作用域
//            OutputHelper.printSemanticError(ErrorType.REDEF_FUNC, ctx.funcName().IDENT().getSymbol().getLine(),
//                    ctx.funcName().getText());
//            return null;
//        }
//
//        Type retType = new VoidType(); // 返回值类型
//        String typeStr = ctx.getChild(0).getText();
//        if (typeStr.equals("int"))
//            retType = new IntType();     // 返回值类型为int32
//
//        if (ctx.funcFParams() != null) { // 如有入参，处理形参，添加形参信息等
//            visit(ctx.funcFParams());
//        }
//
//        FunctionType functionType = new FunctionType(retType, new ArrayList<>(paramsTyList));
//        //顶层作用域中压入此函数
//        curScope.put(new defInfo(funcName, functionType));
//
//        visit(ctx.block());
//        return null;
//    }
//
//    @Override
//    public defInfo visitFuncFParams(SysYParser.FuncFParamsContext ctx) {
//        for (int i = 0; i < ctx.funcFParam().size(); i++) {
//            visit(ctx.funcFParam(i));
//        }
//        return null;
//    }
//
//    @Override
//    public defInfo visitFuncFParam(SysYParser.FuncFParamContext ctx) {
//        Type paramType;
//        if (!ctx.L_BRACKT().isEmpty()) {
//            paramType = new ArrayType(1);
//        } else {
//            paramType = new IntType();
//        }
//        String name = ctx.IDENT().getText();
//        if (paramsNameList.contains(name)) {
//            OutputHelper.printSemanticError(ErrorType.REDEF_VAR, ctx.IDENT().getSymbol().getLine(), name);
//        } else {
//            paramsNameList.add(name);
//            paramsTyList.add(paramType);
//        }
//        return null;
//    }
//
//    @Override
//    public defInfo visitRETURN (SysYParser.RETURNContext ctx) {
//        defInfo curFunc = curScope.findAll(FuncNameTmp);
//        if (ctx.exp() == null) { // 无返回值，即返回void
//            if (((FunctionType)curFunc.type).retTy instanceof IntType) {
//                if (blockDepth < 2) {
//                    OutputHelper.printSemanticError(ErrorType.TYPEERR_RETURN, ctx.RETURN().getSymbol().getLine(), FuncNameTmp);
//                }
//            }
//        } else { // 有返回值,也有可能是void
//            defInfo retInfo = visit(ctx.exp());
//            if (retInfo == null) {
//                if (blockDepth < 2) {
//                    OutputHelper.printSemanticError(ErrorType.TYPEERR_RETURN, ctx.RETURN().getSymbol().getLine(), FuncNameTmp);
//                }
//            } else if (retInfo.type instanceof FunctionType) {
//                if (blockDepth < 2) {
//                    OutputHelper.printSemanticError(ErrorType.TYPEERR_RETURN, ctx.RETURN().getSymbol().getLine(), FuncNameTmp);
//                }
//            } else if (((FunctionType)curFunc.type).retTy instanceof IntType && !(retInfo.type instanceof IntType) || ((FunctionType)curFunc.type).retTy instanceof VoidType && !(retInfo.type instanceof VoidType)){
//                if (blockDepth < 2) {
//                    OutputHelper.printSemanticError(ErrorType.TYPEERR_RETURN, ctx.RETURN().getSymbol().getLine(), FuncNameTmp);
//                }
//            }
//        }
//        return null;
//    }
//
//    @Override
//    public defInfo visitBlock(SysYParser.BlockContext ctx) {
//        //新一层作用域
////        System.out.println(ctx.getText());
//        blockDepth++;
//        Scope newScope = new Scope(); // 添加新作用域
//        newScope.parent = curScope; // 设置新作用域的父作用域
//        for (int i = 0; i < paramsTyList.size(); i++) {
//            newScope.put(new defInfo(paramsNameList.get(i), paramsTyList.get(i)));
//        }
//        curScope = newScope; // 更新当前作用域
//
//        ctx.blockItem().forEach(this::visit); // 依次visit block中的节点
//        curScope = curScope.parent; // 作用域回退
//        blockDepth--;
//
//        return null;
//    }
//
//
//    @Override
//    public defInfo visitVarDecl(SysYParser.VarDeclContext ctx) {
//        for (int i = 0; i < ctx.varDef().size(); i ++) {
//            visit(ctx.varDef(i)); // 依次visit def，即依次visit c=4 和 d=5
//        }
//        // return super.visitVarDecl(ctx);
//        return null;
//    }
//
//    @Override
//    public defInfo visitConstDecl(SysYParser.ConstDeclContext ctx) {
//        for (int i = 0; i < ctx.constDef().size(); i ++) {
//            visit(ctx.constDef(i)); // 依次visit def，即依次visit c=4 和 d=5
//        }
//        // return super.visitConstDecl(ctx);
//        return null;
//    }
//
//
//    @Override
//    public defInfo visitVarDef(SysYParser.VarDefContext ctx) {
//        String varName = ctx.IDENT().getText();
//        defInfo def = curScope.find(varName);
//        if (def != null) {
//            OutputHelper.printSemanticError(ErrorType.REDEF_VAR, ctx.IDENT().getSymbol().getLine(), varName);
//            return null;
//        }
//
//        if (ctx.constExp().isEmpty()) {     //非数组
//            if (ctx.ASSIGN() != null) {     // 包含定义语句
////                System.out.println(ctx.initVal().getText());
//                defInfo initValInfo = visitInitVal(ctx.initVal()); // 访问定义语句右侧的表达式，如c=4右侧的4
//                if (initValInfo == null) {
//                    OutputHelper.printSemanticError(ErrorType.TYPEERR_ASSIGN, ctx.ASSIGN().getSymbol().getLine(), ctx.ASSIGN().getText());
//                } else if (initValInfo.type instanceof ArrayType) {
//                    OutputHelper.printSemanticError(ErrorType.TYPEERR_ASSIGN, ctx.ASSIGN().getSymbol().getLine(), ctx.ASSIGN().getText());
//                } else if (initValInfo.type instanceof FunctionType) {
//                    OutputHelper.printSemanticError(ErrorType.TYPEERR_ASSIGN, ctx.ASSIGN().getSymbol().getLine(), ctx.ASSIGN().getText());
//                }
//            }
//            curScope.put(new defInfo(varName, new IntType()));
//        } else { // 数组
//            ArrayType arrType = new ArrayType(ctx.constExp().size());
//            if (ctx.initVal() != null) {
////                System.out.println(ctx.initVal().getText());
//                defInfo initValInfo = visitInitVal(ctx.initVal()); // 访问定义语句右侧的表达式，如c=4右侧的4
//                if (initValInfo == null) {
//                    OutputHelper.printSemanticError(ErrorType.TYPEERR_ASSIGN, ctx.ASSIGN().getSymbol().getLine(), ctx.ASSIGN().getText());
//                } else if (initValInfo.type instanceof IntType || initValInfo.type instanceof FunctionType ||((ArrayType) initValInfo.type).depth != arrType.depth) {
//                    OutputHelper.printSemanticError(ErrorType.TYPEERR_ASSIGN, ctx.ASSIGN().getSymbol().getLine(), ctx.ASSIGN().getText());
//                }
//            }
//            curScope.put(new defInfo(varName, arrType));
//        }
//        return null;
//    }
//
//    @Override
//    public defInfo visitConstDef (SysYParser.ConstDefContext ctx) {
//        String varName = ctx.IDENT().getText();
//        defInfo def = curScope.find(varName);
//        if (def != null) {
//            OutputHelper.printSemanticError(ErrorType.REDEF_VAR, ctx.IDENT().getSymbol().getLine(), varName);
//            return null;
//        }
//
//        if (ctx.constExp().isEmpty()) {     //非数组
//            if (ctx.ASSIGN() != null) {     // 包含定义语句
//                defInfo constInitVal = visitConstInitVal(ctx.constInitVal()); // 访问定义语句右侧的表达式，如c=4右侧的4
//                if (constInitVal == null) {
//                    OutputHelper.printSemanticError(ErrorType.TYPEERR_ASSIGN, ctx.ASSIGN().getSymbol().getLine(), ctx.ASSIGN().getText());
//                } else if (constInitVal.type instanceof ArrayType) {
//                    OutputHelper.printSemanticError(ErrorType.TYPEERR_ASSIGN, ctx.ASSIGN().getSymbol().getLine(), ctx.ASSIGN().getText());
//                }
//            }
//            curScope.put(new defInfo(varName, new IntType()));
//        } else { // 数组
//            ArrayType arrType = new ArrayType(ctx.constExp().size());
//            if (ctx.constInitVal() != null) {
//                defInfo initValInfo = visitConstInitVal(ctx.constInitVal()); // 访问定义语句右侧的表达式，如c=4右侧的4
//                if (initValInfo == null) {
//                    OutputHelper.printSemanticError(ErrorType.TYPEERR_ASSIGN, ctx.ASSIGN().getSymbol().getLine(), ctx.ASSIGN().getText());
//                } else if (initValInfo.type instanceof IntType || ((ArrayType) initValInfo.type).depth != arrType.depth) {
//                    OutputHelper.printSemanticError(ErrorType.TYPEERR_ASSIGN, ctx.ASSIGN().getSymbol().getLine(), ctx.ASSIGN().getText());
//                }
//            }
//            curScope.put(new defInfo(varName, arrType));
//        }
//        return null;
//    }
//
//    @Override
//    public defInfo visitLVal(SysYParser.LValContext ctx) {
//        String name = ctx.IDENT().getText();
//        defInfo def = curScope.findAll(name);
//        if (def == null) {
//            OutputHelper.printSemanticError(ErrorType.UNDEF_VAR, ctx.IDENT().getSymbol().getLine(), name);
//            return null;
//        } else {
//            if (!(def.type instanceof ArrayType) && !ctx.L_BRACKT().isEmpty()) {
//                OutputHelper.printSemanticError(ErrorType.USEINDEX_NOTARRAY, ctx.IDENT().getSymbol().getLine(), name);
//                return null;
//            } else {
//                if (def.type instanceof ArrayType) {
//                    int curDepth = getDepth(ctx.getText());
//                    int depth = ((ArrayType) def.type).depth - curDepth;
//                    if (depth == 0) {
//                        return new defInfo("", new IntType());
//                    } else {
//                        if (depth < 0) {
//                            OutputHelper.printSemanticError(ErrorType.USEINDEX_NOTARRAY, ctx.IDENT().getSymbol().getLine(), name);
//                            return new defInfo("", new ArrayType(depth));
//                        } else {
//                            return new defInfo("", new ArrayType(depth));
//                        }
//                    }
//                } else if (def.type instanceof FunctionType) {
//                    return def;
//                } else {
//                    return new defInfo("", new IntType());
//                }
//            }
//        }
//    }
//
//    @Override
//    public defInfo visitPLUSEXP(SysYParser.PLUSEXPContext ctx) {
//        defInfo left = visit(ctx.exp(0));
//        defInfo right = visit(ctx.exp(1));
//        int line = ctx.PLUS() != null ? ctx.PLUS().getSymbol().getLine() : ctx.MINUS().getSymbol().getLine();
//        String op = ctx.PLUS() != null ? "+" : "-";
//        if (left == null || right == null) {
//            return null;
//        }
//        if (left.type instanceof IntType && right.type instanceof IntType) {
//            return new defInfo("", new IntType());
//        } else {
//            OutputHelper.printSemanticError(ErrorType.TYPEERR_OP, line, op);
//        }
//        return null;
//    }
//
//
//    @Override
//    public defInfo visitNumber(SysYParser.NumberContext ctx) {
//        return new defInfo("", new IntType());
//    }
//
//    @Override
//    public defInfo visitMULEXP(SysYParser.MULEXPContext ctx) {
//        defInfo left = visit(ctx.exp(0));
//        defInfo right = visit(ctx.exp(1));
//        int line;
//        String op;
//        if (ctx.MUL() != null) {
//            line = ctx.MUL().getSymbol().getLine();
//            op = "*";
//        } else if (ctx.DIV() != null) {
//            line = ctx.DIV().getSymbol().getLine();
//            op = "/";
//        } else {
//            line = ctx.MOD().getSymbol().getLine();
//            op = "%";
//        }
//        if (left == null || right == null) {
//            return null;
//        }
//        if (left.type instanceof IntType && right.type instanceof IntType) {
//            return new defInfo("", new IntType());
//        }
//        else {
//            OutputHelper.printSemanticError(ErrorType.TYPEERR_OP, line, op);
//        }
//        return null;
//    }
//
//    @Override
//    public defInfo visitASSIGN(SysYParser.ASSIGNContext ctx) {
//        defInfo left = visit(ctx.lVal());
//        defInfo right = visit(ctx.exp());
//        if (left == null || right == null) {
//            return null;
//        } else if (left.type instanceof IntType && right.type instanceof IntType) {
//            return new defInfo("", new IntType());
//        } else if (left.type instanceof ArrayType && right.type instanceof ArrayType) {
//            if (((ArrayType)left.type).depth == ((ArrayType)right.type).depth) {
//                return new defInfo("", new ArrayType(((ArrayType)left.type).depth));
//            } else {
//                OutputHelper.printSemanticError(ErrorType.TYPEERR_ASSIGN, ctx.ASSIGN().getSymbol().getLine(), ctx.ASSIGN().getText());
//            }
//        } else if (left.type instanceof FunctionType) {
//            OutputHelper.printSemanticError(ErrorType.ASSIGN_FUNC, ctx.ASSIGN().getSymbol().getLine(), ctx.ASSIGN().getText());
//        } else {
//            OutputHelper.printSemanticError(ErrorType.TYPEERR_ASSIGN, ctx.ASSIGN().getSymbol().getLine(), ctx.ASSIGN().getText());
//        }
//        return null;
//    }
//
//    @Override
//    public defInfo visitInitVal(SysYParser.InitValContext ctx) {
//        defInfo def;
//        if (ctx.exp() != null) {
//            def = visit(ctx.exp());
//        } else {
//            int depth = getDepth2(ctx.getText());
//            def = new defInfo("", new ArrayType(depth));
//        }
//        return def;
//    }
//
//    @Override
//    public defInfo visitConstInitVal(SysYParser.ConstInitValContext ctx) {
//        defInfo def;
//        if (ctx.constExp() != null) {
//            def = visit(ctx.constExp());
//        } else {
//            int depth = getDepth2(ctx.getText());
//            def = new defInfo("", new ArrayType(depth));
//        }
//        return def;
//    }
//
//    @Override
//    public defInfo visitFUNCCALL(SysYParser.FUNCCALLContext ctx) {
//        String funcName = ctx.funcName().IDENT().getText();
//        defInfo def = curScope.findAll(funcName);
//        if (def == null) {
//            OutputHelper.printSemanticError(ErrorType.UNDEF_FUNC, ctx.funcName().IDENT().getSymbol().getLine(), funcName);
//            return null;
//        } else if (!(def.type instanceof FunctionType)) {
//            OutputHelper.printSemanticError(ErrorType.CALLFUNC_VAR, ctx.funcName().IDENT().getSymbol().getLine(), funcName);
//            return null;
//        } else {
//            FunctionType funcType = (FunctionType) def.type;
//            ArrayList<Type> funcCallParas = new ArrayList<>();
//            if (ctx.funcRParams() != null) {
//                for (int i = 0; i < ctx.funcRParams().param().size(); i++) {
//                    defInfo defInfo = visit(ctx.funcRParams().param(i));
//                    if (defInfo == null) {
//                        OutputHelper.printSemanticError(ErrorType.PARAMSERR, ctx.funcName().IDENT().getSymbol().getLine(), funcName);
//                        return null;
//                    } else {
//                        funcCallParas.add(defInfo.type);
//                    }
//                }
//            }
//            if (funcCallParas.size() != funcType.paramsType.size()) {
//                OutputHelper.printSemanticError(ErrorType.PARAMSERR, ctx.funcName().IDENT().getSymbol().getLine(), funcName);
//                return null;
//            } else {
//                for (int i = 0; i < funcCallParas.size(); i++) {
//                    if (funcCallParas.get(i) == null) {
//                        OutputHelper.printSemanticError(ErrorType.PARAMSERR, ctx.funcName().IDENT().getSymbol().getLine(), funcName);
//                        return null;
//                    } else if (funcCallParas.get(i) instanceof IntType && funcType.paramsType.get(i) instanceof IntType) {
//                    } else if (funcCallParas.get(i) instanceof ArrayType && funcType.paramsType.get(i) instanceof ArrayType) {
//                        if (((ArrayType)funcCallParas.get(i)).depth == ((ArrayType)funcType.paramsType.get(i)).depth) {
//                        } else {
//                            OutputHelper.printSemanticError(ErrorType.PARAMSERR, ctx.funcName().IDENT().getSymbol().getLine(), funcName);
//                            return null;
//                        }
//                    } else {
//                        OutputHelper.printSemanticError(ErrorType.PARAMSERR, ctx.funcName().IDENT().getSymbol().getLine(), funcName);
//                        return null;
//                    }
//                }
//            }
//        }
//
//        return new defInfo("", ((FunctionType) def.type).retTy);
//    }
//
//    @Override
//    public defInfo visitREEXP(SysYParser.REEXPContext ctx) {
//        if (Objects.equals(ctx.getChild(0).getText(), "(")) {
//            return visit(ctx.exp());
//        }
//        return visit(ctx.exp());
//    }
//
//    @Override
//    public defInfo visitParam(SysYParser.ParamContext ctx) {
//        defInfo def = visit(ctx.exp());
//        return def;
//    }
//
//    @Override
//    public defInfo visitCond(SysYParser.CondContext ctx) {
//        if (!ctx.cond().isEmpty()) {
//            defInfo left = visit(ctx.cond(0));
//            defInfo right = visit(ctx.cond(1));
//            if (left == null || right == null) {
//                return null;
//            } else if (left.type instanceof IntType && right.type instanceof IntType) {
//                return new defInfo("", new IntType());
//            }
//        } else {
//            defInfo exp = visit(ctx.exp());
//            if (exp == null) {
//                return null;
//            }
//            if (exp.type instanceof IntType) {
//                return new defInfo("", new IntType());
//            }
////            else if (exp.type instanceof FunctionType && ((FunctionType) exp.type).retTy instanceof IntType) {
////                return new defInfo("", new IntType());
////            }
//            else {
//                return null;
//            }
//        }
//        return null;
//    }
//
//    @Override
//    public defInfo visitHAVECOND(SysYParser.HAVECONDContext ctx) {
//        for (int i = 0; i < ctx.getChildCount(); i++) {
//            if (ctx.getChild(i) instanceof SysYParser.CondContext) {
//                defInfo condInfo = visit(ctx.getChild(i));
//                int line;
//                String op;
//                if (ctx.WHILE() == null) {
//                    line = ctx.IF().getSymbol().getLine();
//                    op = "if";
//                } else {
//                    line = ctx.WHILE().getSymbol().getLine();
//                    op = "while";
//                }
//                if (condInfo == null) {
//                    OutputHelper.printSemanticError(ErrorType.TYPEERR_OP, line, op);
//                } else if (!(condInfo.type instanceof IntType)) {
//                    OutputHelper.printSemanticError(ErrorType.TYPEERR_OP, line, op);
//                }
//            } else {
//                visit(ctx.getChild(i));
//            }
//        }
//        return null;
//    }
//
//    @Override
//    public defInfo visitSINGLE(SysYParser.SINGLEContext ctx) {
//        defInfo def = visit(ctx.exp());
//        int line;
//        String op;
//        if (ctx.unaryOp().NOT() != null) {
//            line = ctx.unaryOp().NOT().getSymbol().getLine();
//            op = "!";
//        } else if (ctx.unaryOp().MINUS() != null) {
//            line = ctx.unaryOp().MINUS().getSymbol().getLine();
//            op = "-";
//        } else {
//            line = ctx.unaryOp().PLUS().getSymbol().getLine();
//            op = "+";
//        }
//        if (def == null) {
//            OutputHelper.printSemanticError(ErrorType.TYPEERR_OP, line, op);
//            return null;
//        } else if (!(def.type instanceof IntType)) {
//            OutputHelper.printSemanticError(ErrorType.TYPEERR_OP, line, op);
//            return null;
//        }
//        return new defInfo("", new IntType());
//    }
//
//
//    public int getDepth(String str) {
//        int depth = 0;
//        Stack<Character> stack = new Stack<>();
//        for (int i = 0; i < str.length(); i++) {
//            if (str.charAt(i) == '[') {
//                if (stack.empty() || stack.peek()  != '[') {
//                    depth++;
//                }
//                stack.push('[');
//            } else if (str.charAt(i) == ']') {
//                stack.pop();
//            }
//        }
//        return depth;
//    }
//
//    public int getDepth2(String str) {
//        int depth = 0;
//        Stack<Character> stack = new Stack<>();
//        for (int i = 0; i < str.length(); i++) {
//            if (str.charAt(i) == '{') {
//                if (stack.empty() || stack.peek()  == '{') {
//                    depth++;
//                }
//                stack.push('{');
//            } else if (str.charAt(i) == '}') {
//                stack.pop();
//            } else if (str.charAt(i) == ',') {
//                return depth;
//            }
//        }
//        return depth;
//    }
//}