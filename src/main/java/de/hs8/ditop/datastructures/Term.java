package de.hs8.ditop.datastructures;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.awt.Color;
import java.awt.Shape;


public class Term {
	String text;

	@JsonIgnore
	Shape shape;

	Color color;
	float size;
	public float weight;
	float xPos;
	float yPos;

	public float getxPos() {
		return xPos;
	}

	public void setxPos(final float xPos) {
		this.xPos = xPos;
	}

	public float getyPos() {
		return yPos;
	}

	public void setyPos(final float yPos) {
		this.yPos = yPos;
	}


    public Shape getShape() {
		return shape;
	}


	public void setShape(final Shape shape) {
		this.shape = shape;
	}

	public String getText() {
		return text;
	}

	public void setText(final String text) {
		this.text = text;
	}

	public float getSize() {
		return size;
	}

	public void setSize(final float size) {
		this.size = size;
	}

	public Term(final String text, final Shape shape, final Color color,
			final float size) {
		super();
		this.text = text;
		this.shape = shape;
		this.color = color;
		this.size = size;
	}

	public Term(final String text, final float weight) {
		super();
		this.text = text;
		this.weight = weight;
	}

	@Override
	public String toString() {
		return "Term [text=" + text + ", shape=" + shape + ", color=" + color
				+ ", size=" + size + ", weight=" + weight + ", xPos=" + xPos
				+ ", yPos=" + yPos + "]";
	}

	// @Override
	// public String toString() {
	// return "Term [text=" + text + ", weight=" + weight + "]";
	// }
	//

}
