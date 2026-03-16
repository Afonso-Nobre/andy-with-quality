package delft;

import nl.tudelft.cse1110.andy.config.MetaTest;
import nl.tudelft.cse1110.andy.config.RunConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Configuration extends RunConfiguration {

    @Override
    public Map<String, Float> weights() {
        return new HashMap<>() {{
            put("coverage", 0.1f);
            put("mutation", 0.15f);
            put("meta", 0.75f);
            put("codechecks", 0.0f);
        }};
    }

    @Override
    public List<String> classesUnderTest() {
        return List.of("delft.CollectionUtils", "delft.CollectionUtils.CardinalityHelper");
    }

    @Override
    public int numberOfMutationsToConsider() {
        return 9;
    }

    @Override
    public List<MetaTest> metaTests() {
        return List.of(
                MetaTest.withLineReplacement("finds bug where true is returned when sizes are not equal", 115, 115,
                        "return true;"
                ),
                MetaTest.withLineReplacement("finds bug where count per element is always 1",
                        91, 91,
                        ""
                ),
                MetaTest.insertAt("finds bug where only the first element is considered when building the cardinality map", 93,
                        "break;"
                ),
                MetaTest.withLineReplacement("finds bug where method does not compare list and cardinality sizes (returns true when a is empty)", 114, 120,
                        "final CardinalityHelper<Object> helper = new CardinalityHelper<>(a, b);"
                ),
                MetaTest.withLineReplacement("finds bug where NullPointerException is thrown when list A contains elements not in list B", 62, 65,
                        "return count.intValue();"
                ),
                MetaTest.withStringReplacement("finds bug where method only checks whether elements exist but not their cardinalities",
                        "return count.intValue();",
                        "return 1;"
                ),
                MetaTest.withLineReplacement("finds bug where method does not check cardinalities but only whether the lists are equal", 117, 125,
                        """
                        ArrayList<?> listA = new ArrayList<>(a);
                        ArrayList<?> listB = new ArrayList<>(b);
                        for(int i = 0; i < listA.size(); i++){
                            if(!listA.get(i).equals(listB.get(i))){
                                return false;
                            }
                        }
                        """
                ),
                MetaTest.withStringReplacement("finds bug where method compares first list to itself",
                        "return getFreq(obj, cardinalityB);",
                        "return getFreq(obj, cardinalityA);"
                )
        );
    }

}