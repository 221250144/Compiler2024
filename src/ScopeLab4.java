import org.bytedeco.llvm.LLVM.LLVMValueRef;

import java.util.ArrayList;
import java.util.HashMap;

public class ScopeLab4 {
    public ScopeLab4 parent;
    private HashMap<String, LLVMValueRef> defInfos = new HashMap<>();

    public void put(String name, LLVMValueRef value) {
        defInfos.put(name, value);
    }
    public LLVMValueRef find(String name) {
        if (defInfos.containsKey(name)) {
            return defInfos.get(name);
        } else if (parent != null) {
            return parent.find(name);
        } else {
            return null;
        }
    }
}
