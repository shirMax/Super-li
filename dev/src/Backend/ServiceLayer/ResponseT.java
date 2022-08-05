package Backend.ServiceLayer;

public class ResponseT<T> extends Response{

    public T Value;
    //todo change this with shir , when the value is of type String the code calls this method and thinks that error occurred
    public ResponseT(String msg,boolean errorOccurred)
    {
        super(msg);
        this.Value = null;
    }

    public ResponseT(T value)
    {
        super(null);
        this.Value = value;
    }

    public T getValue() {
        return Value;
    }
}


