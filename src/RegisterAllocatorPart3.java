import org.bytedeco.llvm.LLVM.LLVMBasicBlockRef;
import org.bytedeco.llvm.LLVM.LLVMModuleRef;
import org.bytedeco.llvm.LLVM.LLVMValueRef;
import org.bytedeco.llvm.global.LLVM;

import java.util.ArrayList;
import java.util.List;

import static org.bytedeco.llvm.global.LLVM.*;
import static org.bytedeco.llvm.global.LLVM.LLVMGetValueName;

class RegisterAllocatorPart3 extends RegisterAllocator {
    //    ArrayList<String> registers = new ArrayList<>(List.of("t3", "t4", "t5"));
    ArrayList<String> registers = new ArrayList<String>(List.of("t3", "t4", "t5", "t6", "a1", "a2", "a3", "a4", "a5", "a6", "a7", "s0", "s1", "s2", "s3", "s4", "s5", "s6", "s7", "s8", "s9", "s10", "s11"));
    ArrayList<String> usedRegisters = new ArrayList<String>();
    static int lineNo = 0;
    int stackPos = 0;

    public RegisterAllocatorPart3(LLVMModuleRef module) {
        super(module);
    }

    @Override
    public String allocateRegisters(LLVMValueRef inst) {
        lineNo++;
        StringBuilder result = new StringBuilder();
        int opcode = LLVMGetInstructionOpcode(inst);

        if (opcode == LLVMAlloca) {
            String name = LLVMGetValueName(inst).getString();
            Symbol symbol;
            if (LLVMIsAGlobalValue(inst) != null) {
                symbol = SymbolTable.getSymbol(name, true);
            } else {
                symbol = SymbolTable.getSymbol(name, false);
            }
            if (!registers.isEmpty()) {
                String reg = registers.get(0);
                registers.remove(0);
                usedRegisters.add(reg);
                symbol.position = reg;
                SymbolTable.changeSymbol(symbol);
            } else {
                symbol.position = String.format("%d(sp)", stackPos);
                SymbolTable.changeSymbol(symbol);
                stackPos -= 4;
            }
            return "";
        } else if (opcode == LLVMStore) {
            LLVMValueRef op1 = LLVMGetOperand(inst, 0);
            LLVMValueRef op2 = LLVMGetOperand(inst, 1);
            String name1 = LLVMGetValueName(op1).getString();
            String name2 = LLVMGetValueName(op2).getString();
            Symbol symbol1;
            Symbol symbol2;
            if (LLVMIsAGlobalValue(op1) != null) {
                symbol1 = SymbolTable.getSymbol(name1, true);
            } else {
                symbol1 = SymbolTable.getSymbol(name1, false);
            }
            if (LLVMIsAGlobalValue(op2) != null) {
                symbol2 = SymbolTable.getSymbol(name2, true);
            } else {
                symbol2 = SymbolTable.getSymbol(name2, false);
            }
            String srcReg;
            String destReg;

            if (symbol1 == null) { // 如果 name1 不在符号表中，说明是立即数
                name1 = String.valueOf(LLVMConstIntGetSExtValue(op1));
                result.append(String.format("  li t0, %s\n", name1));
                srcReg = "t0";
            } else { // 如果 name1 在符号表中，获取其位置
                if (symbol1.position.isEmpty() || symbol1.position.contains("sp")) {
                    freeRegisters();
                    if (!registers.isEmpty()) { // 如果还有空闲寄存器
                        srcReg = registers.get(0);
                        if (symbol1.position.contains("sp")) {
                            result.append(String.format("  lw %s, %s\n", srcReg, symbol1.position));
                        }
                        registers.remove(0);
                        usedRegisters.add(srcReg);

                        symbol1.position = srcReg;
                        SymbolTable.changeSymbol(symbol1);
                    } else { // 否则需要保存寄存器到栈中
                        Symbol symbol = findLastEndSymbol();
                        String reg = symbol.position;
                        symbol.position = String.format("%d(sp)", stackPos);
                        SymbolTable.changeSymbol(symbol);
                        result.append(String.format("  sw %s, %d(sp)\n", reg, stackPos));

                        if (symbol1.position.contains("sp")) {
                            result.append(String.format("  lw %s, %s\n", reg, symbol1.position));
                        }
                        srcReg = reg;
                        symbol1.position = reg;
                        SymbolTable.changeSymbol(symbol);
                        stackPos -= 4;
                    }
                } else {
                    if (symbol1.position.equals("heap")) { // 如果 name1 在堆中
                        result.append(String.format("  la t2, %s\n", name1));
                        result.append("  lw t0, 0(t2)\n");
                        srcReg = "t0";
                    } else {
                        srcReg = symbol1.position;
                    }
                }
            }

            if (symbol2.position.isEmpty() || symbol2.position.contains("sp")) {
                freeRegisters();
                result.append(String.format("  sw %s, %s\n", srcReg, symbol2.position));
//                if (!registers.isEmpty()) { // 如果还有空闲寄存器
//                    destReg = registers.get(0);
//                    if (symbol2.position.contains("sp")) {
//                        result.append(String.format("  lw %s, %s\n", destReg, symbol2.position));
//                    }
//                    registers.remove(0);
//                    usedRegisters.add(destReg);
//
//                    symbol2.position = destReg;
//                    SymbolTable.changeSymbol(symbol2);
//                } else { // 否则需要保存寄存器到栈中
//                    Symbol symbol = findLastEndSymbol();
//                    String reg = symbol.position;
//                    result.append(String.format("  sw %s, %d(sp)\n", reg, stackPos));
//                    symbol.position = String.format("%d(sp)", stackPos);
//                    SymbolTable.changeSymbol(symbol);
//
//                    if (symbol2.position.contains("sp")) {
//                        result.append(String.format("  lw %s, %s\n", reg, symbol2.position));
//                    }
//                    destReg = reg;
//                    symbol2.position = reg;
//                    SymbolTable.changeSymbol(symbol);
//                    stackPos -= 4;
//                }
//                result.append(String.format("  mv %s, %s\n", destReg, srcReg));
            } else {
                if (symbol2.position.equals("heap")) { // 如果 name2 在堆中
                    result.append(String.format("  la t2, %s\n", name2));
                    result.append(String.format("  sw %s, 0(t2)\n", srcReg));
                } else {
                    destReg = symbol2.position;
                    result.append(String.format("  mv %s, %s\n", destReg, srcReg));
                }
            }

        } else if (opcode == LLVMLoad) {
            String left = LLVMGetValueName(inst).getString();
            Symbol symbol;
            if (LLVMIsAGlobalValue(inst) != null) {
                symbol = SymbolTable.getSymbol(left, true);
            } else {
                symbol = SymbolTable.getSymbol(left, false);
            }
            String destReg;

            if (symbol.position.isEmpty() || symbol.position.contains("sp")) {
                freeRegisters();
                if (!registers.isEmpty()) {
                    String reg = registers.get(0);
                    if (symbol.position.contains("sp")) {
                        result.append(String.format("  lw %s, %s\n", reg, symbol.position));
                    }
                    registers.remove(0);
                    usedRegisters.add(reg);
                    symbol.position = reg;
                    SymbolTable.changeSymbol(symbol);
                    destReg = reg;
                } else {
                    Symbol lastEndSymbol = findLastEndSymbol();
                    String reg = lastEndSymbol.position;
                    result.append(String.format("  sw %s, %d(sp)\n", reg, stackPos));
                    lastEndSymbol.position = String.format("%d(sp)", stackPos);
                    SymbolTable.changeSymbol(lastEndSymbol);

                    if (symbol.position.contains("sp")) {
                        result.append(String.format("  lw %s, %s\n", reg, symbol.position));
                    }
                    destReg = reg;
                    symbol.position = reg;
                    SymbolTable.changeSymbol(symbol);
                    stackPos -= 4;
                }
            } else {
                destReg = symbol.position;
            }

            LLVMValueRef op1 = LLVMGetOperand(inst, 0);
            String name1 = LLVMGetValueName(op1).getString();
            Symbol symbol1;
            if (LLVMIsAGlobalValue(op1) != null) {
                symbol1 = SymbolTable.getSymbol(name1, true);
            } else {
                symbol1 = SymbolTable.getSymbol(name1, false);
            }

            if (symbol1.position.isEmpty() || symbol1.position.contains("sp")) {
                freeRegisters();
                if (symbol1.position.contains("sp")) {
                    result.append(String.format("  lw %s, %s\n", destReg, symbol1.position));
                } else if (!registers.isEmpty()) {
                    String reg = registers.get(0);
                    registers.remove(0);
                    usedRegisters.add(reg);
                    symbol1.position = reg;
                    SymbolTable.changeSymbol(symbol1);
                    result.append(String.format("  mv %s, %s\n", destReg, reg));
                } else {
                    symbol1.position = String.format("%d(sp)", stackPos);
                    SymbolTable.changeSymbol(symbol1);
                    result.append(String.format("  lw %s, %s\n", destReg, symbol1.position));
                    stackPos -= 4;
                }
            } else {
                if (symbol1.position.equals("heap")) {
                    result.append(String.format("  la t2, %s\n", name1));
                    result.append("  lw t0, 0(t2)\n");
                    result.append(String.format("  mv %s, t0\n", destReg));
                } else {
                    result.append(String.format("  mv %s, %s\n", destReg, symbol1.position));
                }
            }

        } else if (opcode == LLVMAdd || opcode == LLVMMul || opcode == LLVMSDiv || opcode == LLVMSub || opcode == LLVMSRem) {
            String left = LLVMGetValueName(inst).getString();
            Symbol symbol;
            if (LLVMIsAGlobalValue(inst) != null) {
                symbol = SymbolTable.getSymbol(left, true);
            } else {
                symbol = SymbolTable.getSymbol(left, false);
            }
            String destReg;

            if (symbol.position.isEmpty() || symbol.position.contains("sp")) {
                freeRegisters();
                if (!registers.isEmpty()) {
                    String reg = registers.get(0);
                    if (symbol.position.contains("sp")) {
                        result.append(String.format("  lw %s, %s\n", reg, symbol.position));
                    }

                    registers.remove(0);
                    usedRegisters.add(reg);
                    symbol.position = reg;
                    SymbolTable.changeSymbol(symbol);
                    destReg = reg;
                } else {
                    Symbol lastEndSymbol = findLastEndSymbol();
                    String reg = lastEndSymbol.position;
                    result.append(String.format("  sw %s, %d(sp)\n", reg, stackPos));
                    lastEndSymbol.position = String.format("%d(sp)", stackPos);
                    SymbolTable.changeSymbol(lastEndSymbol);

                    if (symbol.position.contains("sp")) {
                        result.append(String.format("  lw %s, %s\n", reg, symbol.position));
                    }
                    symbol.position = reg;
                    destReg = reg;
                    SymbolTable.changeSymbol(symbol);
                    stackPos -= 4;
                }
            } else {
                destReg = symbol.position;
            }

            LLVMValueRef op1 = LLVMGetOperand(inst, 0);
            LLVMValueRef op2 = LLVMGetOperand(inst, 1);
            String name1 = LLVMGetValueName(op1).getString();
            String name2 = LLVMGetValueName(op2).getString();
            Symbol symbol1;
            Symbol symbol2;
            if (LLVMIsAGlobalValue(op1) != null) {
                symbol1 = SymbolTable.getSymbol(name1, true);
            } else {
                symbol1 = SymbolTable.getSymbol(name1, false);
            }
            if (LLVMIsAGlobalValue(op2) != null) {
                symbol2 = SymbolTable.getSymbol(name2, true);
            } else {
                symbol2 = SymbolTable.getSymbol(name2, false);
            }

            String srcReg1 = "";
            String srcReg2 = "";

            if (symbol1 == null) { // 如果 name1 不在符号表中，说明是立即数
                name1 = String.valueOf(LLVMConstIntGetSExtValue(op1));
                result.append(String.format("  li t0, %s\n", name1));
                srcReg1 = "t0";
            } else { // 如果 name1 在符号表中，获取其位置
                if (symbol1.position.isEmpty() || symbol1.position.contains("sp")) {
                    freeRegisters();
                    if (!registers.isEmpty()) {
                        String reg = registers.get(0);
                        if (symbol1.position.contains("sp")) {
                            result.append(String.format("  lw %s, %s\n", reg, symbol1.position));
                        }

                        registers.remove(0);
                        usedRegisters.add(reg);
                        symbol1.position = reg;
                        SymbolTable.changeSymbol(symbol1);
                        srcReg1 = reg;
                    } else {
                        Symbol lastEndSymbol = findLastEndSymbol();
                        String reg = lastEndSymbol.position;
                        result.append(String.format("  sw %s, %d(sp)\n", reg, stackPos));
                        lastEndSymbol.position = String.format("%d(sp)", stackPos);
                        SymbolTable.changeSymbol(lastEndSymbol);

                        if (symbol1.position.contains("sp")) {
                            result.append(String.format("  lw %s, %s\n", reg, symbol1.position));
                        }
                        symbol1.position = reg;
                        srcReg1 = reg;
                        SymbolTable.changeSymbol(symbol1);
                        stackPos -= 4;
                    }
                } else {
                    srcReg1 = symbol1.position;
                }
            }

            if (symbol2 == null) { // 如果 name1 不在符号表中，说明是立即数
                name2 = String.valueOf(LLVMConstIntGetSExtValue(op2));
                result.append(String.format("  li t0, %s\n", name2));
                srcReg2 = "t0";
            } else { // 如果 name1 在符号表中，获取其位置
                if (symbol2.position.isEmpty() || symbol2.position.contains("sp")) {
                    freeRegisters();
                    if (!registers.isEmpty()) {
                        String reg = registers.get(0);
                        if (symbol2.position.contains("sp")) {
                            result.append(String.format("  lw %s, %s\n", reg, symbol2.position));
                        }

                        registers.remove(0);
                        usedRegisters.add(reg);
                        symbol2.position = reg;
                        SymbolTable.changeSymbol(symbol2);
                        srcReg2 = reg;
                    } else {
                        Symbol lastEndSymbol = findLastEndSymbol();
                        String reg = lastEndSymbol.position;
                        result.append(String.format("  sw %s, %d(sp)\n", reg, stackPos));
                        lastEndSymbol.position = String.format("%d(sp)", stackPos);
                        SymbolTable.changeSymbol(lastEndSymbol);

                        if (symbol2.position.contains("sp")) {
                            result.append(String.format("  lw %s, %s\n", reg, symbol2.position));
                        }
                        symbol2.position = reg;
                        srcReg2 = reg;
                        SymbolTable.changeSymbol(symbol2);
                        stackPos -= 4;
                    }
                } else {
                    srcReg2 = symbol2.position;
                }
            }
            // 获取操作符
            String op = "";
            switch (opcode) {
                case LLVMAdd:
                    op = "add";
                    break;
                case LLVMMul:
                    op = "mul";
                    break;
                case LLVMSDiv:
                    op = "div";
                    break;
                case LLVMSub:
                    op = "sub";
                    break;
                case LLVMSRem:
                    op = "rem";
                    break;
            }
            result.append(String.format("  %s %s, %s, %s\n", op, destReg, srcReg1, srcReg2));
        } else if (opcode == LLVMRet) {
            LLVMValueRef op1 = LLVMGetOperand(inst, 0);
            String name1 = LLVMGetValueName(op1).getString();
            Symbol symbol1;
            if (LLVMIsAGlobalValue(op1) != null) {
                symbol1 = SymbolTable.getSymbol(name1, true);
            } else {
                symbol1 = SymbolTable.getSymbol(name1, false);
            }
            if (symbol1 == null) {
                name1 = String.valueOf(LLVMConstIntGetSExtValue(op1));
                result.append(String.format("  li a0, %s\n", name1));
            } else if (!symbol1.position.isEmpty()) {
                if (symbol1.position.equals("heap")) {
                    result.append(String.format("  la a0, %s\n", name1));
                    result.append("  lw a0, 0(a0)\n");
                } else {
                    result.append(String.format("  mv a0, %s\n", symbol1.position));
                }
            }
        }
        return result.toString();
    }

    public void freeRegisters() {
        int size = usedRegisters.size();
        for (int i = 0; i < size; i++) {
            Symbol symbol = SymbolTable.findSymbolByPosition(usedRegisters.get(i));
            if (symbol != null && symbol.end < lineNo) {
                usedRegisters.remove(i);
                i--;
                size--;
                registers.add(symbol.position);
                symbol.position = "";
                SymbolTable.changeSymbol(symbol);
            }
        }
    }

    @Override
    public int getStackSize() {
        analyzeActiveVariables();
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
        stackPos = stackSize - 4;
        return stackSize;
    }

    public int analyzeActiveVariables() {
        // 实现活跃变量分析
        int line = 0;
        LLVMValueRef func = LLVMGetNamedFunction(module, "main");
        for (LLVMBasicBlockRef bb = LLVMGetFirstBasicBlock(func); bb != null; bb = LLVMGetNextBasicBlock(bb)) {
            for (LLVMValueRef inst = LLVMGetFirstInstruction(bb); inst != null; inst = LLVMGetNextInstruction(inst)) {
                int opcode = LLVMGetInstructionOpcode(inst);
                line++;
                if (opcode == LLVMAlloca) {
                    // 处理 alloca 指令，获取变量名和位置
                    String varName = LLVMGetValueName(inst).getString();
                    String position = ""; // 假设变量在寄存器中，这里简化处理

                    // 添加变量到符号表，初始出现位置为当前指令编号
                    SymbolTable.addSymbol(new Symbol(varName, position, line, line, false));
                } else {
                    String name = LLVMGetValueName(inst).getString();
                    if (name != null && !name.isEmpty()) {
                        Symbol symbol;
                        if (LLVMIsAGlobalValue(inst) != null) {
                            symbol = SymbolTable.getSymbol(name, true);
                        } else {
                            symbol = SymbolTable.getSymbol(name, false);
                        }
                        if (symbol != null) {
                            symbol.end = line;
                        } else {
                            symbol = new Symbol(name, "", line, line, false);
                        }
                        SymbolTable.addSymbol(symbol);
                    }
                    int operandNum = LLVMGetNumOperands(inst);
                    LLVMValueRef op1;
                    LLVMValueRef op2;
                    String name1;
                    String name2;
                    if (operandNum == 1) {
                        op1 = LLVMGetOperand(inst, 0);
                        name1 = LLVMGetValueName(op1).getString();
                        Symbol symbol;
                        if (LLVMIsAGlobalValue(op1) != null) {
                            symbol = SymbolTable.getSymbol(name1, true);
                        } else {
                            symbol = SymbolTable.getSymbol(name1, false);
                        }
                        if (symbol != null) {
                            symbol.end = line;
                            SymbolTable.changeSymbol(symbol);
                        }
                    } else if (operandNum == 2) {
                        op1 = LLVMGetOperand(inst, 0);
                        op2 = LLVMGetOperand(inst, 1);
                        name1 = LLVMGetValueName(op1).getString();
                        name2 = LLVMGetValueName(op2).getString();
                        Symbol symbol1;
                        Symbol symbol2;
                        if (LLVMIsAGlobalValue(op1) != null) {
                            symbol1 = SymbolTable.getSymbol(name1, true);
                        } else {
                            symbol1 = SymbolTable.getSymbol(name1, false);
                        }
                        if (LLVMIsAGlobalValue(op2) != null) {
                            symbol2 = SymbolTable.getSymbol(name2, true);
                        } else {
                            symbol2 = SymbolTable.getSymbol(name2, false);
                        }

                        if (symbol1 != null) {
                            symbol1.end = line;
                            SymbolTable.changeSymbol(symbol1);
                        }
                        if (symbol2 != null) {
                            symbol2.end = line;
                            SymbolTable.changeSymbol(symbol2);
                        }
                    }
                }
            }
        }
        return 0;
    }

    public Symbol findLastEndSymbol() {
        Symbol lastEndSymbol = null;
        for (Symbol symbol : SymbolTable.symbols) {
            if ((lastEndSymbol == null || symbol.end > lastEndSymbol.end) && usedRegisters.contains(symbol.position)) {
                lastEndSymbol = symbol;
            }
        }
        return lastEndSymbol;
    }
}