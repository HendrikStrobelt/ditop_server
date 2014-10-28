package de.hs8.ditop.helper;

import de.hs8.ditop.datastructures.Term;
import de.hs8.ditop.datastructures.Topic;
import de.hs8.graphics.FPoint;
import de.hs8.graphics.FontManager;
import de.hs8.graphics.RWordle;
import setvis.ch.ConvexHull;
import setvis.shape.AbstractShapeGenerator;
import setvis.shape.BezierShapeGenerator;
import setvis.shape.ShapeSimplifier;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

/**
 * Created by hen on 10/27/14.
 */
public class TopicCoinGenerator {
    public class SetVisGroup {

        public int bitValue;

        public int countID;

        public int color;

        public String label;

    }

    private final FontManager fontManager;
    static private String cloudFont = "Arial";

    protected Map<String, Topic> topicMap = new HashMap<String, Topic>();
    protected TreeMap<String, SetVisGroup> allSetNamesAndID = new TreeMap<String, SetVisGroup>();
    protected Map<Integer, List<Topic>> termGroupSetsMap = new TreeMap<Integer, List<Topic>>();

    private FPoint[] templatePositions;
    private final HashMap<Integer, Topic> topicTerms;
    private final String dataSetPrefix;
    public Map<String, Double> maxValueMap = new HashMap<String, Double>();


    public TopicCoinGenerator(final String dataSetPrefix,
                            final HashMap<Integer, Topic> topicTerms) {
        super();
        this.dataSetPrefix = dataSetPrefix;
        this.topicTerms = topicTerms;

        final FontRenderContext frc = new FontRenderContext(
                new AffineTransform(), false, false);

        fontManager = new FontManager(frc, cloudFont, 10, 14, 20);
        loadAndPrepareData();

    }

    public TreeMap<String, SetVisGroup> getAllSetNamesAndID() {
        return allSetNamesAndID;
    }

    /**
     * load all the Files here..
     */
    protected void loadAndPrepareData() {

        final List<Topic> discriminiativeTopics = new ArrayList<Topic>();
        final List<Topic> sharedTopics = new ArrayList<Topic>();

		/*
		 * assign new names to topics for D3 compatibility and sort into
		 * discriminative and shared Lists
		 */
        final Set<Map.Entry<Integer, Topic>> topicTermEntries = topicTerms
                .entrySet();
        for (final Map.Entry<Integer, Topic> topicEntry : topicTermEntries) {
            final Topic topic = topicEntry.getValue();
            topic.topicName = dataSetPrefix + topicEntry.getKey();

            if (topic.isShared)
                sharedTopics.add(topic);
            else if (topic.disValue > 0)
                discriminiativeTopics.add(topic);

        }
        //		System.out.println(sharedTopics);



		/* get all containers .... */
        final TreeSet<String> allSetNames = new TreeSet<String>();
        for (final Topic termGroup : discriminiativeTopics) {
            allSetNames.addAll(termGroup.characteristicness.keySet());
        }
        for (final Topic termGroup : sharedTopics) {
            allSetNames.addAll(termGroup.characteristicness.keySet());
        }


        if (allSetNames.size() < 1)
            allSetNames.add("All");

		/* ...and assign a bit to them */
        int bitValue = 1;
        int log2Value = 0;
        for (final String setName : allSetNames) {
            final SetVisGroup setVisGroup = new SetVisGroup();
            setVisGroup.label = setName;
            setVisGroup.bitValue = bitValue;
            setVisGroup.countID = log2Value;

            allSetNamesAndID.put(setName, setVisGroup);
            bitValue *= 2;
            log2Value++;
        }

		/* assign all containers to shared groups and add to topicList */
        for (final Topic shGroup : sharedTopics) {
//            for (final String setName : allSetNames) {
//                shGroup.characteristicness.put(setName, 0.5d);
//            }
//            shGroup.characteristicValue = 0.5f;
            discriminiativeTopics.add(shGroup);
        }

        // normalize over all items
        TermSizeAssigner.assignAll(discriminiativeTopics,
                TermSizeAssigner.TSAMethod.NORMALIZEALL, maxValueMap);



		/* finally add all TG to the tgMap */
        int id = 1;
        for (final Topic topic : discriminiativeTopics) {

            // generate Bitvetcor for set containment
            int inSetBitvector = 0;
            final Set<String> containers = topic.characteristicness.keySet();
            for (final String c : containers) {
                inSetBitvector += allSetNamesAndID.get(c).bitValue;
            }
            topic.setInSetBitvector(inSetBitvector);
            topicMap.put(topic.topicName, topic);

			/*
			 * and add to termgroupset
			 */
            List<Topic> list = termGroupSetsMap.get(inSetBitvector);
            if (list == null) {
                list = new ArrayList<Topic>();
                termGroupSetsMap.put(inSetBitvector, list);
            }
            list.add(topic);

            id++;

        }

    }

    public void createTopicCoins() {
        final ArrayList<Topic> termGroups = new ArrayList<Topic>(
                topicMap.values());
        for (final Topic tg : termGroups) {

            final List<Term> terms = tg.terms;
            final List<Shape> shapeList = new ArrayList<Shape>();

            for (final Term term : terms) {
                Shape shape = fontManager.getShape(term.getText(),
                        term.getSize());
                // bleeding the BB
                final Rectangle2D bounds2d = shape.getBounds2D();
                shape = new Rectangle2D.Float(
                        (float) (-1.5f - bounds2d.getWidth() * .5),
                        (float) (-.5f - bounds2d.getHeight() * .5),
                        (float) bounds2d.getWidth() + 3,
                        (float) bounds2d.getHeight() + 1);
                term.setSize(fontManager.getAWTFont(term.getSize()).getSize());
                term.setShape(shape);
                shapeList.add(shape);
            }

            final List<Shape> layoutCircular = RWordle
                    .generateLayoutCircular(shapeList);
            Iterator<Term> termIt = terms.iterator();
            final Area area = new Area();
            for (final Shape shape : layoutCircular) {
                final Term next = termIt.next();
                next.setxPos((float) shape.getBounds2D().getCenterX());
                next.setyPos((float) shape.getBounds2D().getCenterY());
                next.setShape(shape);
                area.add((Area) shape);
            }
            // area.transform(AffineTransform.getTranslateInstance(random(400),
            // random(400)));
            tg.setUnionShape(area);

			/*
			 * Translate everything relative to center of area.
			 */
            final double offsetX = area.getBounds2D().getCenterX();
            final double offsetY = area.getBounds2D().getCenterY();
            termIt = terms.iterator();
            while (termIt.hasNext()) {
                final Term term = termIt.next();
                term.setxPos((float) (term.getxPos() - offsetX));
                term.setyPos((float) (term.getyPos() - offsetY));
            }

            // tg.centerPos.setLocation(random(400), random(400));
            // topicMap.put(tg.groupName, tg);

        }

    }

    public void createDiTopPositions() {
        createTemplatedWordle();
        removeCloudOverlaps();
        recenterGroups();
    }

    protected void createTemplatedWordle() {
        templatePositions = new FPoint[0];
        FPoint[] labelPositions = new FPoint[0];
        final int[] labelAlignment;

        Shape[] templateShapes;

        ArrayList<Integer> singleSetsOrder;
        ArrayList<Integer> overlapSetsOrder;
        int centerSet;
        final double scaleVis = 20 + Math.sqrt(topicMap.size()) * 65;

        switch (allSetNamesAndID.size()) {
            case 3:
            case 2:
            case 0:
                templatePositions = new FPoint[8];
                templatePositions[1] = new FPoint(0, 1);
                templatePositions[2] = new FPoint(.866, -.5);
                templatePositions[3] = new FPoint(.866, .5); // between 1 and 2
                templatePositions[4] = new FPoint(-.866, -.5);
                templatePositions[5] = new FPoint(-.866, .5); // between 1 and 4
                templatePositions[6] = new FPoint(0, -1); // between 2 and 4
                templatePositions[7] = new FPoint(0, 0); // central

                labelPositions = new FPoint[8];
                labelPositions[1] = new FPoint(0, .5);
                labelPositions[2] = new FPoint(.6, -1.1);
                labelPositions[3] = new FPoint(.866, .5); // between 1 and 2
                labelPositions[4] = new FPoint(-.6, -1.1);
                labelPositions[5] = new FPoint(-.866, .5); // between 1 and 4
                labelPositions[6] = new FPoint(0, -1); // between 2 and 4
                labelPositions[7] = new FPoint(0, 0); // central

                // labelAlignment = new int[8];
                // labelAlignment[1] = CENTER;
                // labelAlignment[2] = LEFT;
                // labelAlignment[3] = CENTER;
                // labelAlignment[4] = RIGHT;
                // labelAlignment[5] = CENTER;
                // labelAlignment[6] = CENTER;
                // labelAlignment[7] = CENTER;
                // labelAlignment[0] = CENTER;

                templateShapes = new Shape[3];
                templateShapes[0] = new Arc2D.Double(-10 * scaleVis,
                        -10 * scaleVis, 20 * scaleVis, 20 * scaleVis, -150, 120,
                        Arc2D.PIE);
                templateShapes[1] = new Arc2D.Double(-10 * scaleVis,
                        -10 * scaleVis, 20 * scaleVis, 20 * scaleVis, -30, 120,
                        Arc2D.PIE);
                templateShapes[2] = new Arc2D.Double(-10 * scaleVis,
                        -10 * scaleVis, 20 * scaleVis, 20 * scaleVis, 90, 120,
                        Arc2D.PIE);

                singleSetsOrder = new ArrayList<Integer>(3);
                singleSetsOrder.add(1);
                singleSetsOrder.add(2);
                singleSetsOrder.add(4);

                overlapSetsOrder = new ArrayList<Integer>();
                overlapSetsOrder.add(3);
                overlapSetsOrder.add(6);
                overlapSetsOrder.add(5);

                centerSet = 7;

                break;
            //		case 2:
            //			templatePositions = new FPoint[4];
            //			templatePositions[1] = new FPoint(1, 1);
            //			templatePositions[2] = new FPoint(-1, -1);
            //			templatePositions[3] = new FPoint(0, 0); // between 1 and 2
            //
            //			labelPositions = new FPoint[4];
            //			labelPositions[1] = new FPoint(.5, .5);
            //			labelPositions[2] = new FPoint(-.5, -.5);
            //			labelPositions[3] = new FPoint(0, 0); // between 1 and 2
            //
            //			// labelAlignment = new int[4];
            //			// labelAlignment[1] = CENTER;
            //			// labelAlignment[2] = CENTER;
            //			// labelAlignment[3] = CENTER;
            //
            //			templateShapes = new Shape[2];
            //			templateShapes[0] = new Arc2D.Double(-10 * scaleVis,
            //					-10 * scaleVis, 20 * scaleVis, 20 * scaleVis, -45, 180,
            //					Arc2D.PIE);
            //			templateShapes[1] = new Arc2D.Double(-10 * scaleVis,
            //					-10 * scaleVis, 20 * scaleVis, 20 * scaleVis, 135, 180,
            //					Arc2D.PIE);
            //
            //			singleSetsOrder = new ArrayList<Integer>();
            //			singleSetsOrder.add(1);
            //			singleSetsOrder.add(2);
            //
            //			overlapSetsOrder = new ArrayList<Integer>();
            //			overlapSetsOrder.add(3);
            //
            //			centerSet = 3;
            //
            //			break;
            //
            case 1:
                templatePositions = new FPoint[2];
                templatePositions[1] = new FPoint(0, 0);

            default:
                break;
        }

		/*
		 * be sure to have a scaled version of positons !!
		 */
        for (final FPoint point : templatePositions) {
            if (point != null)
                point.scale((float) scaleVis);
        }

        for (final FPoint point : labelPositions) {
            if (point != null)
                point.scale((float) scaleVis);
        }

        /**
         * AND APPLY !!!!
         */

        final Map<String, FPoint> map = new TreeMap<String, FPoint>(); // position
        // Map
        final Set<Map.Entry<String, Topic>> tgEntries = topicMap.entrySet();

        for (final Map.Entry<String, Topic> entry : tgEntries) {
            System.out.println(entry.getValue());
        }
        for (final Map.Entry<String, Topic> entry : tgEntries) {
            final Topic tg = entry.getValue();

            final int bitvector = tg.getInSetBitvector();

            final Point2D point2d = templatePositions[bitvector];

            final FPoint fun = new FPoint(point2d.getX(), point2d.getY());

            map.put(entry.getKey(), fun);

        }

        // Normalizer.normalize(map, map, true, topicMap.size()*30, 0);

        final Set<String> keySet = map.keySet();
        for (final String key : keySet) {
            final FPoint fPoint = map.get(key);
            final Topic termGroup = topicMap.get(key);
            termGroup.setCenterPos(fPoint);
            final Shape unionShape = termGroup.getUnionShape();

            final Shape newUnion = new Area(unionShape)
                    .createTransformedArea(AffineTransform
                            .getTranslateInstance(fPoint.x
                                            - unionShape.getBounds2D().getCenterX(),
                                    fPoint.y
                                            - unionShape.getBounds2D()
                                            .getCenterY()));

            termGroup.setUnionShape(newUnion);
        }

    }

    /**
     * Rolled Out Wordle overlap removal
     */
    protected void removeCloudOverlaps() {

        final List<Shape> groupShapes = new ArrayList<Shape>();

        final Set<String> keySet = topicMap.keySet();

        Shape newUnion;
        Topic termGroup;
        double h;
        double w;

        // PolygonShapeGenerator shapeGenerator = new PolygonShapeGenerator(new
        // ConvexHull());

        // new ShapeSimplifier(
        // new BSplineShapeGenerator(new ShapeSimplifier(
        // new PolygonShapeGenerator(new BubbleSet()))));
        final AbstractShapeGenerator shapeGenerator = new ShapeSimplifier(
                new BezierShapeGenerator(new ConvexHull()));

        shapeGenerator.setRadius(10);
        final Rectangle2D[] emptyRectangleSet = new Rectangle2D[] {};
        double recRadius;

        for (final String key : keySet) {
            termGroup = topicMap.get(key);
            newUnion = termGroup.getUnionShape();

            final Rectangle2D bounds2d = newUnion.getBounds2D();
            w = bounds2d.getWidth();
            h = bounds2d.getHeight();

			/*
			 * generate bleeded ConvexHull over terms
			 */
            final List<Term> terms = termGroup.terms;
            final FPoint centerPos = termGroup.getCenterPos();

            final Rectangle2D[] allShapes = new Rectangle2D[terms.size()];
            final AffineTransform termGroupCenterTranslation = AffineTransform
                    .getTranslateInstance(centerPos.x, centerPos.y);

            int c = 0;
            for (final Term term : terms) {
                allShapes[c] = termGroupCenterTranslation
                        .createTransformedShape(term.getShape()).getBounds2D();
                c++;
            }

            newUnion = shapeGenerator.createShapeFor(allShapes,
                    emptyRectangleSet);

            // newUnion = AffineTransform.getScaleInstance((w+10.)/w,
            // (h+7.)/h).createTransformedShape(newUnion);

            try {
                final Shape myShape = termGroupCenterTranslation
                        .createInverse().createTransformedShape(newUnion);

                termGroup.setUnionShape(myShape);

                final Rectangle2D myShapeBB = myShape.getBounds2D();
                recRadius = Math.sqrt(myShapeBB.getWidth()
                        * myShapeBB.getWidth() + myShapeBB.getHeight()
                        * myShapeBB.getHeight());
                termGroup.recommendedRadius = recRadius / 2d;
            } catch (final NoninvertibleTransformException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            groupShapes.add(newUnion);

        }

        final List<Shape> layoutCircular = RWordle
                .generateLayoutCircular(groupShapes);

        final Iterator<Shape> iterator = layoutCircular.iterator();
        float xPos, yPos;
        final Shape unionShape;

        for (final String key : keySet) {
            final Shape shape = iterator.next();
            final Topic tgNext = topicMap.get(key);
            xPos = (float) shape.getBounds2D().getCenterX();
            yPos = (float) shape.getBounds2D().getCenterY();
            tgNext.setCenterPos(new FPoint(xPos, yPos));

            // tgNext.setUnionShape(shape);

        }

    }

    private void recenterGroups() {

        final Set<Map.Entry<Integer, List<Topic>>> entrySet = termGroupSetsMap
                .entrySet();
        for (final Map.Entry<Integer, List<Topic>> entry : entrySet) {
            final FPoint newCenter = templatePositions[entry.getKey()];

			/*
			 * generate union BB and determine Center
			 */
            final List<Topic> termgroups = entry.getValue();
            Area unionShape = null;
            Shape tgShape;
            for (final Topic tg : termgroups) {
                tgShape = AffineTransform.getTranslateInstance(tg.centerPos.x,
                        tg.centerPos.y).createTransformedShape(
                        tg.getUnionShape());

                // tgShape = tg.getUnionShape();
                if (unionShape == null)
                    unionShape = new Area(tgShape);
                else
                    unionShape.add(new Area(tgShape));
            }

            if (unionShape != null) {
				/*
				 * generate Affine Transformation
				 */

                final Rectangle2D bounds2d = unionShape.getBounds2D();
                final FPoint oldCenter = new FPoint(bounds2d.getCenterX(),
                        bounds2d.getCenterY());

                final FPoint diff = newCenter.min(oldCenter);

                // System.out.println(diff);

                final AffineTransform translateToNewCenter = AffineTransform
                        .getTranslateInstance(diff.x, diff.y);

				/*
				 * and apply it...
				 */
                for (final Topic tg : termgroups) {
                    // tg.setUnionShape(translateToNewCenter
                    // .createTransformedShape(tg.getUnionShape()));
                    tg.centerPos.addOwn(diff);
                }

            }

        }

    }

	/*
	 * ======================= -- GETTER & SETTER --=======================
	 */

    public Map<String, Topic> getTopicMap() {
        return topicMap;
    }

    public void setTopicMap(final Map<String, Topic> topicMap) {
        this.topicMap = topicMap;
    }


}
