package de.hs8.graphics;

/**
 * Created by hen on 10/27/14.
 */
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.hs8.ditop.helper.PointSerializer;

import java.awt.geom.Point2D;

@JsonSerialize(using = PointSerializer.class)
public class FPoint extends Point2D.Float {

    private static final long serialVersionUID = 1879022349002922569L;

    public FPoint() {
        // nothing special to do here
    }

    public FPoint(final float arg0, final float arg1) {
        super(arg0, arg1);
    }

    public FPoint(final double arg0, final double arg1) {
        super((float) arg0, (float) arg1);
    }

    public FPoint(final FPoint orig) {
        super(orig.x, orig.y);
    }

    public FPoint(final Point2D.Float orig) {
        super(orig.x, orig.y);
    }

    public void scaleAdd(final float scale, final FPoint p1, final FPoint p2) {
        final float newx = scale * p1.x + p2.x;
        final float newy = scale * p1.y + p2.y;
        setLocation(newx, newy);
    }

    public void scale(final float scale) {
        setLocation(x * scale, y * scale);
    }

    public void move(final float moveX, final float moveY) {
        setLocation(x + moveX, y + moveY);
    }

    public void scaleMove(final float scale,
                          final float moveX, final float moveY) {
        setLocation(x * scale + moveX, y * scale + moveY);
    }

    public FPoint min(final FPoint o) {
        return new FPoint(x - o.x, y - o.y);
    }

    public void addOwn(final FPoint o) {
        setLocation(x + o.x, y + o.y);
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public void normalize() {
        final float length = length();
        setLocation(x / length, y / length);
    }

    @Override
    public String toString() {
        return "fp [x=" + x + ", y=" + y + "]";
    }

}

