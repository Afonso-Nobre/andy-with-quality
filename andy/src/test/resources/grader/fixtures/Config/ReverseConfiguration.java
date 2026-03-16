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
            put("mutation", 0.1f);
            put("meta", 0.8f);
            put("codechecks", 0.0f);
        }};
    }

    @Override
    public List<String> classesUnderTest() {
        return List.of("delft.ArrayUtils");
    }

    @Override
    public int numberOfMutationsToConsider() {
        return 6;
    }

    @Override
    public List<MetaTest> metaTests() {
        return List.of(
                MetaTest.withStringReplacement("finds bug where null check is missing",
                        """
                        if (array == null) {
                            return;
                        }
                        """,
                        ""
                ),
                MetaTest.withStringReplacement("finds bug where method always starts at index 0",
                        "int i = startIndexInclusive < 0 ? 0 : startIndexInclusive;",
                        "int i = 0;"
                ),
                MetaTest.withStringReplacement("finds bug where negative start index is not promoted",
                        "int i = startIndexInclusive < 0 ? 0 : startIndexInclusive;",
                        "int i = startIndexInclusive;"
                ),
                MetaTest.withStringReplacement("finds bug where start index is considered exclusive",
                        "int i = startIndexInclusive < 0 ? 0 : startIndexInclusive;",
                        "int i = (startIndexInclusive + 1) < 0 ? 0 : (startIndexInclusive + 1);"
                ),
                MetaTest.withStringReplacement("finds bug where index 0 is skipped",
                        "int i = startIndexInclusive < 0 ? 0 : startIndexInclusive;",
                        "int i = startIndexInclusive < 1 ? 1 : startIndexInclusive;"
                ),
                MetaTest.withStringReplacement("finds bug where method always ends at last index",
                        "int j = Math.min(array.length, endIndexExclusive) - 1;",
                        "int j = array.length - 1;"
                ),
                MetaTest.withStringReplacement("finds bug where end index greater than the length of the array is not demoted",
                        "int j = Math.min(array.length, endIndexExclusive) - 1;",
                        "int j = endIndexExclusive - 1;"
                ),
                MetaTest.withStringReplacement("finds bug where end index is considered inclusive",
                        "int j = Math.min(array.length, endIndexExclusive) - 1;",
                        "int j = Math.min(array.length - 1, endIndexExclusive);"
                ),
                MetaTest.withStringReplacement("finds bug where last index of array is skipped",
                        "int j = Math.min(array.length, endIndexExclusive) - 1;",
                        "int j = Math.min(array.length - 1, endIndexExclusive) - 1;"
                ),
                MetaTest.withLineReplacement("finds bug where start and end indices are swapped if start is greater",
                        30, 31,
                        """
                        int s = startIndexInclusive;
                        int e = endIndexExclusive;
                        if (s > e) {
                            s = endIndexExclusive;
                            e = startIndexInclusive;
                        }
                        int i = s < 0 ? 0 : s;
                        int j = Math.min(array.length, e) - 1;"""
                ),
                MetaTest.insertAt("finds bug where exception is thrown if range has one element", 32,
                        "if (i == j) throw new RuntimeException(\"killed the mutant\");"
                )
        );
    }
}
