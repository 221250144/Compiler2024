import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.*;
import org.bytedeco.javacpp.BytePointer;

import java.io.IOException;

import static org.bytedeco.llvm.global.LLVM.LLVMDisposeMessage;
import static org.bytedeco.llvm.global.LLVM.LLVMPrintModuleToFile;

public class Main {
    public static final BytePointer error = new BytePointer();
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("input path is required");
            return;
        }
        String source = args[0];
        CharStream input = CharStreams.fromFileName(source);
        SysYLexer sysYLexer = new SysYLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(sysYLexer);
        SysYParser sysYParser = new SysYParser(tokens);
        sysYParser.removeErrorListeners();
        myParserErrorListener myParserErrorListener = new myParserErrorListener();
        sysYParser.addErrorListener(myParserErrorListener);

        ParseTree tree = sysYParser.program();

        if (!myParserErrorListener.hasError) {
            myVisitorLab4 visitor = new myVisitorLab4();
            visitor.visit(tree);

            if (LLVMPrintModuleToFile(visitor.module, "/home/cz/schoolwork/compiler/Lab/tests/test1.ll", error) != 0) {    // module是你自定义的LLVMModuleRef对象
                LLVMDisposeMessage(error);
            }

            RISCVCodeGenerator codeGenerator = new RISCVCodeGenerator(visitor.module, args[1]);
            codeGenerator.generateRISCVCode();
        }

    }
}