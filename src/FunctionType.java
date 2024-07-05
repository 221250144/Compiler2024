import java.util.ArrayList;

public class FunctionType extends Type {
    public Type retTy;
    public ArrayList<Type> paramsType;

    public FunctionType(Type retTy, ArrayList<Type> paramsType) {
        this.retTy = retTy;
        this.paramsType = paramsType;
    }
}
