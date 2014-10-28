package de.hs8.ditop.helper;

import java.awt.geom.Point2D;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class PointSerializer extends StdSerializer<Point2D> {

	public PointSerializer() {
		super(Point2D.class);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void serialize(final Point2D p, final JsonGenerator jg,
			final SerializerProvider arg2) throws IOException,
			JsonGenerationException {
		jg.writeStartObject();
		jg.writeNumberField("x", p.getX());
		jg.writeNumberField("y", p.getY());
		jg.writeEndObject();
		// TODO Auto-generated method stub

	}

}
