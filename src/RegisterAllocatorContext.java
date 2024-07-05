import org.bytedeco.llvm.LLVM.LLVMValueRef;

class RegisterAllocatorContext {
    private RegisterAllocator allocator;

    public void setAllocator(RegisterAllocator allocator) {
        this.allocator = allocator;
    }

    public String allocateRegisters(LLVMValueRef inst) {
        if (allocator != null) {
            return allocator.allocateRegisters(inst);
        } else {
            System.out.println("No allocator strategy set.");
            return null;
        }
    }

    public int getStackSize() {
        if (allocator != null) {
            return allocator.getStackSize();
        } else {
            System.out.println("No allocator strategy set.");
            return -1;
        }
    }
}