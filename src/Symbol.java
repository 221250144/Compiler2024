public class Symbol {
    String name;
    String position;
    int begin;
    int end;
    boolean global;

    public Symbol(String name, String position, int begin, int end, boolean global) {
        this.name = name;
        this.position = position;
        this.begin = begin;
        this.end = end;
        this.global = global;
    }
}
