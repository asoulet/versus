package comparison;

public class Context extends Selector {

    public Context(ComplexProperty property, String object) throws InterruptedException {
        super(property, object);
        computeCount();
    }

}
