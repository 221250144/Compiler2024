import java.util.ArrayList;
import java.util.List;

public class Scope {
    public Scope parent;
    public ArrayList<defInfo> defInfos = new ArrayList<>();

    public void put(defInfo def) {
        defInfos.add(def);
    }

    public defInfo find(String name) {
        if (defInfos == null) {
            return null;
        }
        for (int i = defInfos.size() - 1; i >= 0; i--) {
            if (defInfos.get(i).name.equals(name)) {
                return defInfos.get(i);
            }
        }
        return null;
    }

    public defInfo findAll(String name) {
        if (defInfos == null) {
            return null;
        }
        for (int i = defInfos.size() - 1; i >= 0; i--) {
            if (defInfos.get(i).name.equals(name)) {
                return defInfos.get(i);
            }
        }
        if (parent != null) {
            return parent.findAll(name);
        }
        return null;
    }
}
