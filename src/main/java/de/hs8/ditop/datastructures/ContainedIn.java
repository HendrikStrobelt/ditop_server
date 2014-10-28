package de.hs8.ditop.datastructures;

public class ContainedIn {
	public String name;
	public float numberOne = 0;
	public float numberTwo = 0;

	public ContainedIn(final String name, final float numberOne) {
		super();
		this.name = name;
		this.numberOne = numberOne;
	}

	@Override
	public String toString() {
		return "ContainedIn [name=" + name + ", numberOne=" + numberOne
				+ ", numberTwo=" + numberTwo + "]";
	}

}