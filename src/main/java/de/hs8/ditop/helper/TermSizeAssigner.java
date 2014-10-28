package de.hs8.ditop.helper;

import de.hs8.ditop.datastructures.Term;
import de.hs8.ditop.datastructures.Topic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TermSizeAssigner {
    public static enum TSAMethod {
        NORMALIZEALL
    };

    static class MinMaxer {
        public float min = Float.MAX_VALUE;
        public float max = -Float.MAX_VALUE;

        public void update(final float x) {
            if (min > x)
                min = x;
            if (max < x)
                max = x;

        }

        public float scale() {
            return max - min;
        }

    }

    public static boolean assignAll(
            final List<Topic> groups,
            final TSAMethod method) {
        if (method == TSAMethod.NORMALIZEALL) {

            final MinMaxer weightMinMax = new MinMaxer();
            final MinMaxer discMinMax = new MinMaxer();
            final MinMaxer charMinMax = new MinMaxer();

            final HashMap<String, MinMaxer> charmapMinMax = new HashMap<String, TermSizeAssigner.MinMaxer>();

            for (final Topic termGroup : groups) {
                final List<Term> terms = termGroup.terms;
                for (final Term term : terms) {
                    weightMinMax.update(term.weight);
                }

				/* normalize discrimination as well */
                discMinMax.update(termGroup.disValue);

				/* assign and normalize characteristic */
                //				final Collection<Double> values = termGroup.characteristicness
                //						.values();

                float sum = 0;
                float count = 0;
                double val;
                MinMaxer minMaxer;
                for (final Map.Entry<String, Double> entry : termGroup.characteristicness
                        .entrySet()) {
                    val = entry.getValue();
                    sum += val;

                    minMaxer = charmapMinMax.get(entry.getKey());
                    if (minMaxer == null) {
                        minMaxer = new MinMaxer();
                        charmapMinMax.put(entry.getKey(), minMaxer);
                    }
                    minMaxer.update((float) val);

                    count++;
                }
                if (count > 1) { // averaging charactersitcis
                    sum = sum / count;
                }

                charMinMax.update(sum);

                if (sum > 0) {
                    termGroup.characteristicValue = sum;
                } else {
                    termGroup.characteristicValue = -1; // marker for non-valid
                    // value
                }

            }

            final float scale = weightMinMax.scale();
            float scaleCharacteristicValue = charMinMax.max;// -minChar;
            if (scaleCharacteristicValue == 0)
                scaleCharacteristicValue = 1; // to avoid div-by-zero

            MinMaxer actualCharMinMaxer;
            for (final Topic termGroup : groups) {
                final List<Term> terms = termGroup.terms;
                for (final Term term : terms) {
                    term.setSize((term.weight - weightMinMax.min) / scale);
                }

				/* normalize DiscValue */
                termGroup.disValue = (termGroup.disValue - discMinMax.min)
                        / (discMinMax.scale());

				/* normalize all DiscValues 0-max */
                for (final Map.Entry<String, Double> entry : termGroup.characteristicness
                        .entrySet()) {
                    actualCharMinMaxer = charmapMinMax.get(entry.getKey());
                    entry.setValue((entry.getValue() - actualCharMinMaxer.min)
                            / actualCharMinMaxer.scale());
                }

				/* normalize Char value -- negative Values remain neg. !! */
                // termGroup.characteristicValue =
                // (termGroup.characteristicValue-minChar)/scaleCharacteristicValue;
                termGroup.characteristicValue = (termGroup.characteristicValue)
                        / scaleCharacteristicValue;

            }

        }
        return true;
    }
}
