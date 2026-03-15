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
            put("mutation", 0.2f);
            put("meta", 0.7f);
            put("codechecks", 0.0f);
        }};
    }

    @Override
    public List<String> classesUnderTest() {
        return List.of("delft.ArrayUtils");
    }

    @Override
    public List<MetaTest> metaTests() {
        return List.of(
                MetaTest.withLineReplacement("finds bug where false is returned for null input",
                        19, 21,
                        """
                        if (array == null) {
                            return false;
                        }
                        if (array.length < 2) {
                            return true;
                        }
                        """),
                MetaTest.withStringReplacement("finds bug where check for length < 2 is missing",
                        " || array.length < 2",
                        ""),
                MetaTest.withStringReplacement("finds bug where last element is ignored",
                        "i < n",
                        "i < n - 1"),
                MetaTest.withLineReplacement("finds bug where only the sorting of the last two elements is considered",
                        24, 31,
                        """
                        boolean result = true;
                        for (int i = 1; i < n; i++) {
                            final int current = array[i];
                            result = (previous <= current);
                            previous = current;
                        }
                        return result;"""),
                MetaTest.withLineReplacement("finds bug where method throws exception if array has one element",
                        19, 31,
                        """
                        if (array == null || array.length == 0) {
                            return true;
                        }
                        int previous = array[0];
                        int current = array[1];
                        final int n = array.length;
                        for (int i = 2; i < n; i++) {
                            if (previous > current) {
                                return false;
                            }
                            previous = current;
                            current = array[i];
                        }
                        if (previous > current) {
                            return false;
                        }
                        return true;"""),
                MetaTest.withStringReplacement("finds bug where condition is changed to previous >= current",
                        "previous > current",
                        "previous >= current"),
                MetaTest.withLineReplacement("finds bug where true is returned even when the input is unsorted",
                        27, 27,
                        "return true;"),
                MetaTest.withLineReplacement("finds bug where false is returned for a sorted list of length >= 2",
                        31, 31,
                        "return false;"
                )
        );
    }

}
