package de.hs8.graphics;

//  Copyright by Marc Spicker
// GPL License -- http://www.gnu.org/licenses/gpl.html
//
// based on the publication:
//
// @article {CGF:CGF3106,
// author = {Strobelt, H. and Spicker, M. and Stoffel, A. and Keim, D. and Deussen, O.},
// title = {Rolled-out Wordles: A Heuristic Method for Overlap Removal of 2D Data Representatives},
// journal = {Computer Graphics Forum},
// volume = {31},
// number = {3pt3},
// publisher = {Blackwell Publishing Ltd},
// issn = {1467-8659},
// url = {http://dx.doi.org/10.1111/j.1467-8659.2012.03106.x},
// doi = {10.1111/j.1467-8659.2012.03106.x},
// pages = {1135--1144},
// keywords = {I.3.3 [Computer Graphics]: Picture/Image Generation��������Line and curve generation},
// year = {2012},
// }
//


import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RWordle {

    /**
     * RWordle-L algorithm.
     *
     * @param input
     *            List of shapes to be layouted.
     * @return List of layouted shapes.
     */
    public static List<Shape> generateLayoutLinear(final List<Shape> input) {
        Collections.sort(input, new Comparator<Shape>() {

            @Override
            public int compare(final Shape o1, final Shape o2) {
                return Double.compare(o1.getBounds2D().getCenterX(), o2
                        .getBounds2D().getCenterX());
            }
        });

        final List<Shape> layouted = new ArrayList<Shape>();
        for (final Shape cur : input) {
            double t = 3.0;
            // spiral depending on the size of the object
            final double minSide = Math.min(cur.getBounds2D().getWidth(), cur
                    .getBounds2D().getHeight());
            final double spiralFactor = minSide / 17.0;
            final double spiralStep = minSide / 10.0;
            while (true) {
                final double tx = Math.sin(t) * t * spiralFactor;
                final double ty = Math.cos(t) * t * spiralFactor;
                final AffineTransform at = new AffineTransform();
                at.translate(tx, ty);
                final Area curArea = new Area(cur);
                // transformed object
                final Area transformedArea = curArea.createTransformedArea(at);
                if (!hasOverlap(layouted, transformedArea)) {
                    // found placement
                    layouted.add(transformedArea);
                    break;
                }
                t += spiralStep / t;
            }
        }
        return layouted;
    }

    public static List<Shape> generateLayoutCircular(final List<Shape> input) {
        return generateLayoutCircular(input, null);
    }

    /**
     * RWordle-C algorithm.
     *
     * @param input
     *            List of shapes to be layouted.
     * @return List of layouted shapes.
     */
    public static List<Shape> generateLayoutCircular(final List<Shape> input, final List<Shape> invalid) {
        // calculate center
        double sumX = 0;
        double sumY = 0;
        int count = 0;
        for (final Shape s : input) {
            final Rectangle2D r = s.getBounds2D();
            sumX += r.getCenterX();
            sumY += r.getCenterY();
            ++count;
        }
        final Point2D center = new Point2D.Double(sumX / count, sumY / count);

//		Collections.sort(input, new Comparator<Shape>() {
//
//			@Override
//			public int compare(final Shape o1, final Shape o2) {
//				final Rectangle2D r1 = o1.getBounds2D();
//				final Rectangle2D r2 = o2.getBounds2D();
//				final Point2D c1 = new Point2D.Double(r1.getCenterX(), r1
//						.getCenterY());
//				final Point2D c2 = new Point2D.Double(r2.getCenterX(), r2
//						.getCenterY());
//				return Double.compare(calcEuclideanDistance(c1, center),
//						calcEuclideanDistance(c2, center));
//			}
//		});

        final List<Shape> layouted = new ArrayList<Shape>();
        for (final Shape cur : input) {
            double t = 3.0;
            // spiral depending on the size of the object
            final double minSide = Math.min(cur.getBounds2D().getWidth(), cur
                    .getBounds2D().getHeight());
            final double spiralFactor = minSide / 17.0; // was 17
            final double spiralStep = minSide / 10.0; // was 10
            while (true) {
                final double tx = Math.sin(t) * t * spiralFactor;
                final double ty = Math.cos(t) * t * spiralFactor;
                final AffineTransform at = new AffineTransform();
                at.translate(tx, ty);
                final Area curArea = new Area(cur);
                // transformed object
                final Area transformedArea = curArea.createTransformedArea(at);
                if (!hasOverlap(layouted, transformedArea)) {
					/* hen: add outerConstraint !!! */
                    if (invalid==null || !hasOverlap(invalid, transformedArea)){
                        // found placement
                        layouted.add(transformedArea);
                        break;
                    }
                }
                t += spiralStep / t;
            }
        }
        return layouted;
    }

    private static boolean hasOverlap(final List<Shape> alreadyLayouted,
                                      final Shape current) {
        for (final Shape s : alreadyLayouted) {
            if (hasOverlap(s, current)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasOverlap(final Shape s1, final Shape s2) {
        final Area a1 = new Area(s1);
        final Area a2 = new Area(s2);
        a1.intersect(a2);
        return !a1.isEmpty();
    }

    private static double calcEuclideanDistance(final Point2D p1,
                                                final Point2D p2) {
        return Math.sqrt((p1.getX() - p2.getX()) * (p1.getX() - p2.getX())
                + (p1.getY() - p2.getY()) * (p1.getY() - p2.getY()));
    }
}
