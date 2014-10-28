package de.hs8.ditop.datastructures;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.hs8.graphics.FPoint;


import java.awt.Shape;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hen on 10/27/14.
 */
public class Topic {

    public String topicName;
    // @JsonIgnore

    public FPoint centerPos = new FPoint();
    public double recommendedRadius = 1;

    public Map<String, Double> characteristicness = new HashMap<String, Double>();
    //	public Map<String, SubCollectionDescription> containedIn = new HashMap<String, SubCollectionDescription>();

    @JsonIgnore
    public Shape unionShape;

    public int inSetBitvector;
    public float disValue;
    public float characteristicValue = -1;
    public boolean isShared = false;

    // @JsonIgnore
    public List<Term> terms = new ArrayList<Term>();

    public Topic(final String groupName) {
        super();
        this.topicName = groupName;
    }

    @Override
    public String toString() {
        return "Topic [groupName=" + topicName + ", characteristicness="
                + characteristicness + ", inSetBitvector=" + inSetBitvector
                + ", disValue=" + disValue + ", characteristicValue="
                + characteristicValue + ", terms=" + terms + "]";
    }

    public FPoint getCenterPos() {
        return centerPos;
    }

    public void setCenterPos(final FPoint centerPos) {
        this.centerPos = centerPos;
    }

    public Shape getUnionShape() {
        return unionShape;
    }

    public void setUnionShape(final Shape unionShape) {
        this.unionShape = unionShape;
    }

    public int getInSetBitvector() {
        return inSetBitvector;
    }

    public void setInSetBitvector(final int inSetBitvector) {
        this.inSetBitvector = inSetBitvector;
    }

    public void cleanup() {
        characteristicness.clear();
        inSetBitvector = 0;
        disValue = 0;
        characteristicValue = -1;
        isShared = false;

    }

}

