import org.bytedeco.llvm.LLVM.LLVMBasicBlockRef;

public class loopInfo {
    LLVMBasicBlockRef conditionBlock;
    LLVMBasicBlockRef endBlock;

    public loopInfo(LLVMBasicBlockRef conditionBlock, LLVMBasicBlockRef endBlock) {
        this.conditionBlock = conditionBlock;
        this.endBlock = endBlock;
    }
}
