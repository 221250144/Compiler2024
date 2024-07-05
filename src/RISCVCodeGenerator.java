import org.bytedeco.llvm.LLVM.*;
import org.bytedeco.llvm.global.LLVM;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import static org.bytedeco.llvm.global.LLVM.*;

public class RISCVCodeGenerator {
    LLVMModuleRef module;
    AsmBuilder asmBuilder = new AsmBuilder();
    String filePath;
    RegisterAllocatorContext context = new RegisterAllocatorContext();

    public RISCVCodeGenerator(LLVMModuleRef module, String filePath) {
        this.module = module;
        this.filePath = filePath;
    }

    public void generateRISCVCode() {
        // 使用 Part2 策略
        context.setAllocator(new RegisterAllocatorPart3(module));
        int stackSize = context.getStackSize();

        asmBuilder.append(" .data\n");

        // 处理全局变量
        for (LLVMValueRef global = LLVMGetFirstGlobal(module); global != null; global = LLVMGetNextGlobal(global)) {
            processGlobal(global);
        }

        asmBuilder.append(" .text\n");

        // 处理 main 函数
        for (LLVMValueRef func = LLVMGetFirstFunction(module); func != null; func = LLVMGetNextFunction(func)) {
            String funcName = LLVMGetValueName(func).getString();
            if (LLVMIsDeclaration(func) == 0 && "main".equals(funcName)) {  // 仅处理 main 函数
                asmBuilder.append(String.format(" .globl %s\n", funcName));
                asmBuilder.append(String.format("%s:\n", funcName));
                asmBuilder.append(String.format("  addi sp, sp, -%d\n", stackSize));  // prologue

                for (LLVMBasicBlockRef bb = LLVMGetFirstBasicBlock(func); bb != null; bb = LLVMGetNextBasicBlock(bb)) {
                    String bbName = LLVMGetBasicBlockName(bb).getString();
                    asmBuilder.append(String.format("%s:\n", bbName));

                    for (LLVMValueRef inst = LLVMGetFirstInstruction(bb); inst != null; inst = LLVMGetNextInstruction(inst)) {
                        asmBuilder.append(context.allocateRegisters(inst));
                    }
                }

                asmBuilder.append(String.format("  addi sp, sp, %d\n", stackSize));  // epilogue
                asmBuilder.append("  li a7, 93\n");
                asmBuilder.append("  ecall\n");
            }
        }

//         你可以将生成的 RISC-V 代码写入test1.riscv，以便后续使用
         try (PrintWriter writer = new PrintWriter(filePath)) {
             writer.println(asmBuilder.getResult());
         } catch (FileNotFoundException e) {
             e.printStackTrace();
         }
    }

    private void processGlobal(LLVMValueRef global) {
        String globalName = LLVMGetValueName(global).getString();
        LLVMValueRef init = LLVMGetInitializer(global);
        if (init != null) {
            int initValue = (int) LLVMConstIntGetSExtValue(init);
            asmBuilder.append(String.format("%s:\n", globalName));
            asmBuilder.append(String.format("  .word %d\n", initValue));
            Symbol symbol = new Symbol(globalName, "heap", 0, 0, true);
            SymbolTable.addSymbol(symbol);
        }
    }
}
