package comparison;

public class Context extends Selector {
	
	public Context(ComplexProperty property, String object) {
		super(property, object);
		computeCount();
	}

}
