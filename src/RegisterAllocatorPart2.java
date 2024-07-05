import org.bytedeco.llvm.LLVM.LLVMBasicBlockRef;
import org.bytedeco.llvm.LLVM.LLVMModuleRef;
import org.bytedeco.llvm.LLVM.LLVMValueRef;

import java.util.ArrayList;
import java.util.List;

import static org.bytedeco.llvm.global.LLVM.*;

class RegisterAllocatorPart2 extends RegisterAllocator {

    public RegisterAllocatorPart2(LLVMModuleRef module) {
        super(module);
    }

    @Override
    public String allocateRegisters(LLVMValueRef inst) {
            StringBuilder result = new StringBuilder();
            int opcode = LLVMGetInstructionOpcode(inst);

            if (opcode == LLVMAlloca) {
                return "";
            } else if (opcode == LLVMStore) {

            } else if (opcode == LLVMLoad) {

            } else if (opcode == LLVMAdd || opcode == LLVMMul || opcode == LLVMSDiv || opcode == LLVMSub || opcode == LLVMSRem) {

            } else if (opcode == LLVMICmp) {

            }


            return result.toString();
        }

    @Override
    public int getStackSize() {
        // 遍历 main 函数中的指令，统计等号运算符数量
        int stackSize = 0;
        LLVMValueRef func = LLVMGetNamedFunction(module, "main");
        for (LLVMBasicBlockRef bb = LLVMGetFirstBasicBlock(func); bb != null; bb = LLVMGetNextBasicBlock(bb)) {
            for (LLVMValueRef inst = LLVMGetFirstInstruction(bb); inst != null; inst = LLVMGetNextInstruction(inst)) {
                String name = LLVMGetValueName(inst).getString();
                if (name != null && !name.isEmpty()) {
                    stackSize += 4;
                }
            }
        }
        if (stackSize % 16 != 0) {
            stackSize += 16 - stackSize % 16;
        }
        return stackSize;
    }
}
