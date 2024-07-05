import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SymbolTable {
    static final ArrayList<Symbol> symbols = new ArrayList<>();

    static public void addSymbol(Symbol symbol) {
        symbols.add(symbol);
    }

    static public void changeSymbol(Symbol symbol) {
        for (Symbol s : symbols) {
            if (s.name.equals(symbol.name)) {
                s.position = symbol.position;
                s.begin = symbol.begin;
                s.end = symbol.end;
                break;
            }
        }
    }

    static public Symbol getSymbol(String name, boolean global) {
        for (Symbol symbol : symbols) {
            if (symbol.name.equals(name) && symbol.global == global) {
                return symbol;
            }
        }
        return null;
    }

    static public Symbol findSymbolByPosition(String position) {
        for (Symbol symbol : symbols) {
            if (symbol.position.equals(position)) {
                return symbol;
            }
        }
        return null;
    }

    static public void printSymbols() {
        for (Symbol symbol : symbols) {
            System.out.println(symbol.name + " " + symbol.position + " " + symbol.begin + " " + symbol.end);
        }
    }

    // 获取最大活跃变量数
    static public int getMaxActiveVariables() {
        List<Event> events = new ArrayList<>();
        for (Symbol symbol : symbols) {
            events.add(new Event(symbol.begin, true)); // 开始事件
            events.add(new Event(symbol.end, false));  // 结束事件
        }

        // 按时间排序事件
        Collections.sort(events);

        int maxActive = 0;
        int currentActive = 0;

        // 遍历排序后的事件列表
        for (Event event : events) {
            if (event.isStart) {
                currentActive++; // 开始事件，活跃变量数加一
                if (currentActive > maxActive) {
                    maxActive = currentActive; // 更新最大活跃变量数
                }
            } else {
                currentActive--; // 结束事件，活跃变量数减一
            }
        }

        return maxActive; // 返回最大活跃变量数
    }
}