package comparison;

public class Feature extends Selector {
	
	private int size = 1;
	
	public Feature(ComplexProperty property, String object, int size) {
		super(property, object);
		this.size = size;
	}

	public Feature(ComplexProperty property, String object) {
		super(property, object);
	}

	public int getSize() {
		return size;
	}

}
