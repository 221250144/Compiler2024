public class AsmBuilder {
    StringBuffer buffer;

    public AsmBuilder() {
        buffer = new StringBuffer();
    }

    public void append(String line) {
        buffer.append(line);
    }

    public void op2(String op, String dest, String lhs, String rhs) {
        buffer.append(String.format("  %s %s, %s, %s\n", op, dest, lhs, rhs));
    }

    public void op1(String op, String dest) {
        buffer.append(String.format("  %s %s\n", op, dest));
    }

    public String getResult() {
        return buffer.toString();
    }
}
