package comparison;

import java.util.Objects;

public class ComplexProperty {
	
	private String uri;
	private boolean inverse = false;
	
	public ComplexProperty(String uri, boolean inverse) {
		this.uri = uri;
		this.inverse = inverse;
	}

	@Override
	public String toString() {
		return "ComplexProperty [uri=" + uri + ", inverse=" + inverse + "]";
	}

	public String getUri() {
		return uri;
	}

	public boolean isInverse() {
		return inverse;
	}

	public String getFilter(String object) {
		if (inverse)
			return " <" + object + "> <" + uri + "> ?s ";
		else
			return " ?s <" + uri + "> <" + object + "> ";
	}

	@Override
	public int hashCode() {
		return Objects.hash(uri, inverse);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ComplexProperty) {
			ComplexProperty cp = (ComplexProperty) obj;
			return uri.equals(cp.uri) && inverse == cp.inverse;
		}
		return false;
	}
	
	
}
