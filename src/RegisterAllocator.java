import org.bytedeco.llvm.LLVM.LLVMModuleRef;
import org.bytedeco.llvm.LLVM.LLVMValueRef;

abstract class RegisterAllocator {
    protected LLVMModuleRef module;

    public RegisterAllocator(LLVMModuleRef module) {
        this.module = module;
    }

    public abstract String allocateRegisters(LLVMValueRef inst);
    public abstract int getStackSize();
}