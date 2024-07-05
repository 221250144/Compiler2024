import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.llvm.LLVM.*;
import org.bytedeco.llvm.global.LLVM;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

import static org.bytedeco.llvm.global.LLVM.*;


public class myVisitorLab4 extends SysYParserBaseVisitor {
    //创建module
    public LLVMModuleRef module = LLVMModuleCreateWithName("module");

    //初始化IRBuilder，后续将使用这个builder去生成LLVM IR
    LLVMBuilderRef builder = LLVMCreateBuilder();

    //考虑到我们的语言中仅存在int一个基本类型，可以通过下面的语句为LLVM的int型重命名方便以后使用
    LLVMTypeRef i32Type = LLVMInt32Type();
    // lab5里面出现了void类型
    LLVMTypeRef voidType = LLVMVoidType();

    ScopeLab4 curScope = new ScopeLab4();
    ArrayList<LLVMValueRef> paramsLocal = new ArrayList<LLVMValueRef>(0);
    ArrayList<String> paramsName = new ArrayList<>(0);
    LLVMValueRef zero = LLVMConstInt(i32Type, 0, 0);
    LLVMValueRef trueValue = LLVMConstInt(i32Type, 1, 0);
    LLVMValueRef curFunc = new LLVMValueRef();
    int num = 0;
    Stack<loopInfo> loopStack = new Stack<loopInfo>();

    public myVisitorLab4() {
        //初始化LLVM
        LLVMInitializeCore(LLVMGetGlobalPassRegistry());
        LLVMLinkInMCJIT();
        LLVMInitializeNativeAsmPrinter();
        LLVMInitializeNativeAsmParser();
        LLVMInitializeNativeTarget();
    }

    @Override
    public Void visitFuncDef(SysYParser.FuncDefContext ctx) {
        LLVMTypeRef returnType;
        if (ctx.funcType().getText().equals("void")) {
            returnType = voidType;
        } else {
            returnType = i32Type;
        }

        int paramCount = 0;
        paramsLocal.clear();
        paramsName.clear();

        if (ctx.funcFParams() != null) {
            paramCount = ctx.funcFParams().funcFParam().size();
        }
        PointerPointer<Pointer> params = new PointerPointer<>(paramCount);
        for (int i = 0; i < paramCount; i++) {
            params.put(i, i32Type);
        }

        LLVMTypeRef functionType = LLVMFunctionType(returnType, params, paramCount, 0);
        LLVMValueRef function = LLVMAddFunction(module, ctx.funcName().getText(), functionType);
        curScope.put(ctx.funcName().getText(), function);
        curFunc = function;
        LLVMBasicBlockRef entry = LLVMAppendBasicBlock(function, ctx.funcName().getText() + "Entry");
        LLVMPositionBuilderAtEnd(builder, entry);

        if (ctx.funcFParams() != null) {
            visitFuncFParams(ctx.funcFParams());
        }

        visit(ctx.block());

        if (ctx.funcType().getText().equals("void")) {
            LLVMBuildRetVoid(builder);
        }

        return null;
    }

    @Override
    public Object visitFuncFParams(SysYParser.FuncFParamsContext ctx) {
        int size = ctx.funcFParam().size();
        num = 0;
        for (int i = 0; i < size; i++) {
            visitFuncFParam(ctx.funcFParam(i));
            num++;

//            String name = ctx.funcFParam(i).getText();
//            LLVMValueRef local = LLVMBuildAlloca(builder, i32Type, name);
//            LLVMBuildStore(builder, zero, local);
//
//            paramsLocal.add(local);
//            paramsName.add(name);
        }
        return null;
    }

    @Override
    public Object visitFuncFParam(SysYParser.FuncFParamContext ctx) {
        String name = ctx.IDENT().getText();
        LLVMValueRef local = LLVMBuildAlloca(builder, i32Type, name);
        LLVMBuildStore(builder, LLVMGetParam(curFunc, num), local);

        paramsLocal.add(local);
        paramsName.add(name);
        return null;
    }

    @Override
    public defInfo visitBlock(SysYParser.BlockContext ctx) {
        //新一层作用域
        ScopeLab4 newScope = new ScopeLab4(); // 添加新作用域
        newScope.parent = curScope; // 设置新作用域的父作用域

        for (int i = 0; i < paramsName.size(); i++) {
            newScope.put(paramsName.get(i), paramsLocal.get(i));
        }

        curScope = newScope; // 更新当前作用域

        ctx.blockItem().forEach(this::visit); // 依次visit block中的节点
        curScope = curScope.parent; // 作用域回退

        return null;
    }

    @Override
    public LLVMValueRef visitONLY_EXP(SysYParser.ONLY_EXPContext ctx) {
        return (LLVMValueRef) visit(ctx.exp());
    }

    @Override
    public LLVMValueRef visitAND_COND(SysYParser.AND_CONDContext ctx) {
        LLVMBasicBlockRef currentBlock = LLVMGetInsertBlock(builder);

        // Create basic blocks for the short-circuit evaluation
        LLVMBasicBlockRef condTrueBlock = LLVMAppendBasicBlock(curFunc, "condTrue");
        LLVMBasicBlockRef endBlock = LLVMAppendBasicBlock(curFunc, "end");

        // Evaluate the first condition
        LLVMValueRef leftCond = (LLVMValueRef) visit(ctx.cond(0));
        LLVMValueRef leftCondBool = LLVMBuildICmp(builder, LLVMIntNE, leftCond, zero, "leftCondBool");

        // Branch based on the first condition
        LLVMBuildCondBr(builder, leftCondBool, condTrueBlock, endBlock);

        // In the true block, evaluate the second condition
        LLVMPositionBuilderAtEnd(builder, condTrueBlock);
        LLVMValueRef rightCond = (LLVMValueRef) visit(ctx.cond(1));
        LLVMValueRef rightCondBool = LLVMBuildICmp(builder, LLVMIntNE, rightCond, zero, "rightCondBool");
        LLVMBuildBr(builder, endBlock);

        // End block to merge results
        LLVMPositionBuilderAtEnd(builder, endBlock);
        LLVMValueRef phi = LLVMBuildPhi(builder, LLVMInt1Type(), "andResult");

        // Ensure the correct order of predecessors and values
        PointerPointer<LLVMValueRef> values = new PointerPointer<>(2);
        values.put(0, LLVMConstInt(i32Type, 0, 0));
        values.put(1, rightCondBool);

        PointerPointer<LLVMBasicBlockRef> blocks = new PointerPointer<>(2);
        blocks.put(0, currentBlock);
        blocks.put(1, condTrueBlock);
        LLVMAddIncoming(phi, values, blocks, 2);

        // Return the result as an i32 value
        LLVMValueRef result = LLVMBuildZExt(builder, phi, i32Type, "result");
        return result;
    }



    @Override
    public LLVMValueRef visitOR_COND(SysYParser.OR_CONDContext ctx) {
        LLVMBasicBlockRef currentBlock = LLVMGetInsertBlock(builder);

        // Create basic blocks for the short-circuit evaluation
        LLVMBasicBlockRef condFalseBlock = LLVMAppendBasicBlock(curFunc, "condFalse");
        LLVMBasicBlockRef endBlock = LLVMAppendBasicBlock(curFunc, "end");

        // Evaluate the first condition
        LLVMValueRef leftCond = (LLVMValueRef) visit(ctx.cond(0));
        LLVMValueRef leftCondBool = LLVMBuildICmp(builder, LLVMIntNE, leftCond, zero, "leftCondBool");

        // Branch based on the first condition
        LLVMBuildCondBr(builder, leftCondBool, endBlock, condFalseBlock);

        // In the false block, evaluate the second condition
        LLVMPositionBuilderAtEnd(builder, condFalseBlock);
        LLVMValueRef rightCond = (LLVMValueRef) visit(ctx.cond(1));
        LLVMValueRef rightCondBool = LLVMBuildICmp(builder, LLVMIntNE, rightCond, zero, "rightCondBool");
        LLVMBuildBr(builder, endBlock);

        // End block to merge results
        LLVMPositionBuilderAtEnd(builder, endBlock);
        LLVMValueRef phi = LLVMBuildPhi(builder, LLVMInt1Type(), "orResult");

        // Ensure the correct order of predecessors and values
        PointerPointer<LLVMValueRef> values = new PointerPointer<>(2);
                values.put(0, LLVMConstInt(LLVMInt1Type(), 1, 0));
                values.put(1, rightCondBool);
        PointerPointer<LLVMBasicBlockRef> blocks = new PointerPointer<>(2);
                blocks.put(0, currentBlock);
                blocks.put(1, condFalseBlock);
        LLVMAddIncoming(phi, values, blocks, 2);

        // Return the result as an i32 value
        LLVMValueRef result = LLVMBuildZExt(builder, phi, i32Type, "result");
        return result;
    }


    @Override
    public LLVMValueRef visitONE(SysYParser.ONEContext ctx) {
        int op = 0;
        if (ctx.LT() != null) { // 小于
            op = LLVMIntSLT;
        } else if (ctx.LE() != null) { // 小于等于
            op = LLVMIntSLE;
        } else if (ctx.GT() != null) { // 大于
            op = LLVMIntSGT;
        } else if (ctx.GE() != null) { // 大于等于
            op = LLVMIntSGE;
        } else if (ctx.EQ() != null) { // 等于
            op = LLVMIntEQ;
        } else if (ctx.NEQ() != null) { // 不等于
            op = LLVMIntNE;
        }
        LLVMValueRef left = (LLVMValueRef) visit(ctx.cond(0));
        LLVMValueRef right = (LLVMValueRef) visit(ctx.cond(1));

        LLVMValueRef condition = LLVMBuildICmp(builder, op, left, right, ctx.getText());
        LLVMValueRef result = LLVMBuildZExt(builder, condition, i32Type, "boolToInt");
        return result;
    }

    @Override
    public Object visitWHILE(SysYParser.WHILEContext ctx) {
        LLVMBasicBlockRef conditionBlock = LLVMAppendBasicBlock(curFunc, "whileCondition");
        LLVMBasicBlockRef bodyBlock = LLVMAppendBasicBlock(curFunc, "whileBody");
        LLVMBasicBlockRef endBlock = LLVMAppendBasicBlock(curFunc, "whileEnd");

        // 跳转到条件判断块
        LLVMBuildBr(builder, conditionBlock);

        // 处理条件判断块
        LLVMPositionBuilderAtEnd(builder, conditionBlock);
        LLVMValueRef condition = (LLVMValueRef) visit(ctx.cond());

        condition = LLVMBuildICmp(builder, LLVMIntNE, condition, zero, "whilecond");

        LLVMBuildCondBr(builder, condition, bodyBlock, endBlock);
        // 保存当前循环信息到栈中
        loopStack.push(new loopInfo(conditionBlock, endBlock));

        // 处理循环体块
        LLVMPositionBuilderAtEnd(builder, bodyBlock);
        visit(ctx.stmt());
        LLVMBuildBr(builder, conditionBlock); // 循环体结束后跳回条件判断

        // 处理结束块
        LLVMPositionBuilderAtEnd(builder, endBlock);

        // 循环处理结束后从栈中弹出
        loopStack.pop();

        return null;
    }

    @Override
    public Object visitBREAK(SysYParser.BREAKContext ctx) {
        if (!loopStack.isEmpty()) {
            loopInfo loopInfo = loopStack.peek();
            LLVMBuildBr(builder, loopInfo.endBlock); // 跳转到循环结束块
        }
        return null;
    }

    @Override
    public Object visitCONTINUE(SysYParser.CONTINUEContext ctx) {
        if (!loopStack.isEmpty()) {
            loopInfo loopInfo = loopStack.peek();
            LLVMBuildBr(builder, loopInfo.conditionBlock); // 跳转到条件判断块
        }
        return null;
    }


    @Override
    public Object visitIF_ELSE(SysYParser.IF_ELSEContext ctx) {
        LLVMBasicBlockRef ifTrue = LLVMAppendBasicBlock(curFunc, "ifTrue");
        LLVMBasicBlockRef ifFalse = LLVMAppendBasicBlock(curFunc, "ifFalse");
        LLVMBasicBlockRef next = LLVMAppendBasicBlock(curFunc, "next");

        LLVMValueRef condition = (LLVMValueRef) visit(ctx.cond());

        // 转换条件值为 i1 类型
        condition = LLVMBuildICmp(builder, LLVMIntNE, condition, zero, "ifcond");

        // 根据条件值选择跳转到 ifTrue 还是 ifFalse
        LLVMBuildCondBr(builder, condition, ifTrue, ifFalse);

        // 填充 ifTrue 基本块
        LLVMPositionBuilderAtEnd(builder, ifTrue);
        visit(ctx.stmt(0)); // 访问 if 语句
        LLVMBuildBr(builder, next); // 跳转到 next 基本块

        // 填充 ifFalse 基本块
        LLVMPositionBuilderAtEnd(builder, ifFalse);
        if (ctx.stmt(1) != null) { // 检查是否存在 else 语句
            visit(ctx.stmt(1)); // 访问 else 语句
        }
        LLVMBuildBr(builder, next); // 跳转到 next 基本块

        // 将生成指令的位置移动到 next 基本块
        LLVMPositionBuilderAtEnd(builder, next);

        return null; // 这里返回 null，表示没有返回值
    }


    @Override
    public Object visitRETURN(SysYParser.RETURNContext ctx) {
//        LLVMValueRef result = (LLVMValueRef) visit(ctx.getChild(1));

        LLVMValueRef result = new LLVMValueRef();
        if (ctx.exp() != null) {
            result = (LLVMValueRef) visit(ctx.exp());
            LLVMBuildRet(builder, /*result:LLVMValueRef*/result);
        } else {
            LLVMBuildRetVoid(builder);
        }
//        exit(0);

        return null;
    }

    @Override
    public LLVMValueRef visitFUNCCALL(SysYParser.FUNCCALLContext ctx) {
        LLVMValueRef function = curScope.find(ctx.funcName().getText());
//        LLVMTypeRef returnType = LLVMGetTypeKind(LLVMGetReturnType(LLVMGetReturnType(LLVMTypeOf(function))));
        int returnType = LLVMGetTypeKind(LLVMGetReturnType(LLVMGetReturnType(LLVMTypeOf(function))));
        int size = 0;
        if (ctx.funcRParams() != null) {
            size = ctx.funcRParams().param().size();
        }
        PointerPointer<LLVMValueRef> params = new PointerPointer<>(size);
        if (ctx.funcRParams() != null) {
            params = visitFuncRParams(ctx.funcRParams());
        }

        LLVMValueRef tmp = new LLVMValueRef();
        if (returnType == 0) {
            tmp = LLVMBuildCall(builder, function, params, size, "");
        } else if (returnType == 8) {
            tmp = LLVMBuildCall(builder, function, params, size, ctx.funcName().getText());
        }
        return tmp;
    }

    @Override
    public PointerPointer<LLVMValueRef> visitFuncRParams(SysYParser.FuncRParamsContext ctx) {
        int size = ctx.param().size();
        PointerPointer<LLVMValueRef> params = new PointerPointer<>(size);
        for (int i = 0; i < size; i++) {
//            LLVMValueRef tmp = visitParam(ctx.param(i));
//            System.out.println(LLVMPrintValueToString(tmp).getString());
            params.put(i, visitParam(ctx.param(i)));
        }
        return params;
    }

    @Override
    public LLVMValueRef visitParam(SysYParser.ParamContext ctx) {
        return (LLVMValueRef)(visit(ctx.exp()));
    }

    @Override
    public Object visitConstDef(SysYParser.ConstDefContext ctx) {
        if (curScope.parent == null) {
            // 全局变量
            LLVMValueRef value = (LLVMValueRef) visit(ctx.constInitVal());
            LLVMValueRef global = LLVMAddGlobal(module, i32Type, ctx.IDENT().getText());
            LLVMSetInitializer(global, value);
            curScope.put(ctx.IDENT().getText(), global);
        } else {
            // 局部变量
            LLVMValueRef value = (LLVMValueRef) visit(ctx.constInitVal());
            LLVMValueRef local = LLVMBuildAlloca(builder, i32Type, ctx.IDENT().getText());
            LLVMBuildStore(builder, value, local);
            curScope.put(ctx.IDENT().getText(), local);
        }
        return null;
    }

    @Override
    public Object visitVarDef(SysYParser.VarDefContext ctx) {
        LLVMValueRef value = new LLVMValueRef();
        if (ctx.initVal() != null) {
            value = (LLVMValueRef) visit(ctx.initVal());
        } else {
            value = zero;
        }
        if (curScope.parent == null) {
            // 全局变量
//            LLVMValueRef value = (LLVMValueRef) visit(ctx.initVal());
            LLVMValueRef global = LLVMAddGlobal(module, i32Type, ctx.IDENT().getText());
            LLVMSetInitializer(global, value);
            curScope.put(ctx.IDENT().getText(), global);
        } else {
            // 局部变量
            LLVMValueRef local = LLVMBuildAlloca(builder, i32Type, ctx.IDENT().getText());
            LLVMBuildStore(builder, value, local);
            curScope.put(ctx.IDENT().getText(), local);
        }
        return null;
    }

    @Override
    public Object visitASSIGN(SysYParser.ASSIGNContext ctx) {
        LLVMValueRef value = (LLVMValueRef) visit(ctx.exp());
        LLVMValueRef lVal = visitLVal(ctx.lVal());
        LLVMBuildStore(builder, value, lVal);
        return null;
    }

    @Override
    public Object visitRESERVE(SysYParser.RESERVEContext ctx) {
        LLVMValueRef valueRef = visitLVal(ctx.lVal());
        if (valueRef != null) {
//            System.out.println("reserve: " + ctx.lVal().getText());
            return LLVMBuildLoad(builder, valueRef, "loadtmp");
        } else {
            return null;
        }
    }

    @Override
    public LLVMValueRef visitLVal(SysYParser.LValContext ctx) {
        String name = ctx.IDENT().getText();
        return curScope.find(name);
    }

    @Override
    public LLVMValueRef visitNUMBER(SysYParser.NUMBERContext ctx) {
        SysYParser.NumberContext token = ctx.number();
        String text = token.getText();
        if (text.matches("0[xX][0-9a-fA-F]+")) {
            text = text.substring(2);
            text = String.valueOf(Integer.parseInt(text, 16));
        } else if (text.matches("0[0-7]+")) {
            text = text.substring(1);
            text = String.valueOf(Integer.parseInt(text, 8));
        }
        int value = Integer.parseInt(text);
        return LLVMConstInt(i32Type, value, 0);
    }

    @Override
    public LLVMValueRef visitSINGLE(SysYParser.SINGLEContext ctx) {
        char op = ctx.getChild(0).getText().charAt(0);
        switch (op) {
            case '+':
                return LLVMBuildAdd(builder, LLVMConstInt(i32Type, 0, 0), (LLVMValueRef)visit(ctx.exp()), "addtmp");
            case '-':
                return LLVMBuildSub(builder, LLVMConstInt(i32Type, 0, 0), (LLVMValueRef)visit(ctx.exp()), "subtmp");
            case '!':
                LLVMValueRef tmp_ = (LLVMValueRef)visit(ctx.exp());
                // 生成icmp
                tmp_ = LLVMBuildICmp(builder, LLVMIntNE, LLVMConstInt(i32Type, 0, 0), tmp_, "tmp_");
                // 生成xor
                tmp_ = LLVMBuildXor(builder, tmp_, LLVMConstInt(LLVMInt1Type(), 1, 0), "tmp_");
                // 生成zext
                tmp_ = LLVMBuildZExt(builder, tmp_, i32Type, "tmp_");
                return tmp_;
            default:
                return null;
        }
    }

    @Override
    public LLVMValueRef visitPLUSEXP(SysYParser.PLUSEXPContext ctx) {
        char op = ctx.getChild(1).getText().charAt(0);
        LLVMValueRef left = (LLVMValueRef) visit(ctx.getChild(0));
        LLVMValueRef right = (LLVMValueRef) visit(ctx.getChild(2));
        switch (op) {
            case '+':
                return LLVMBuildAdd(builder, left, right, "addtmp");
            case '-':
                return LLVMBuildSub(builder, left, right, "subtmp");
            default:
                return null;
        }
    }

    @Override
    public LLVMValueRef visitREEXP(SysYParser.REEXPContext ctx) {
        return (LLVMValueRef) visit(ctx.exp());
    }

    @Override
    public LLVMValueRef visitMULEXP(SysYParser.MULEXPContext ctx) {
        char op = ctx.getChild(1).getText().charAt(0);
        LLVMValueRef left = (LLVMValueRef) visit(ctx.getChild(0));
        LLVMValueRef right = (LLVMValueRef) visit(ctx.getChild(2));
        switch (op) {
            case '*':
                return LLVMBuildMul(builder, left, right, "multmp");
            case '/':
                return LLVMBuildSDiv(builder, left, right, "divtmp");
            case '%':
                return LLVMBuildSRem(builder, left, right, "modtmp");
            default:
                return null;
        }
    }
}
