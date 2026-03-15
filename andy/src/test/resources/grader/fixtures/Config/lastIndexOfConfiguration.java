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
            put("coverage", 0.3f);
            put("mutation", 0.0f);
            put("meta", 0.7f);
            put("codechecks", 0.0f);
        }};
    }

    @Override
    public List<String> classesUnderTest() {
        return List.of("delft.ArrayUtils");
    }

    @Override
    public boolean skipPitest() {
        return true;
    }

    @Override
    public List<MetaTest> metaTests() {
        return List.of(
                MetaTest.insertAt("finds bug where negative startIndex throws exception",
                        37,
                        """
                        if (startIndex < 0) {
                            throw new IllegalArgumentException("startIndex must be non-negative.");
                        }
                        """),
                MetaTest.withLineReplacement("finds bug where a startIndex higher than the array length isn't caught",
                        42, 44,
                        """
                        }
                        """),
                MetaTest.insertAt("finds bug where an array containing only the value to find does not return the correct index",
                        40,
                        """
                        if(array.length == 1){
                            return INDEX_NOT_FOUND;
                        }
                        """),
                MetaTest.insertAt("finds bug where a single element array, not containing the value to find, does not return index -1",
                        40,
                        """
                        if(array.length == 1){
                            return array[0];
                        }
                        """),
                MetaTest.insertAt("finds bug where the value to find can only be at startIndex",
                        49,
                        """
                        else{
                            return INDEX_NOT_FOUND;
                        }
                        """),
                MetaTest.withLineReplacement("finds bug where the value to find cannot be at startIndex",
                        45, 45,
                        """
                        for (int i = startIndex - 1; i >= 0; i--) {
                        """),
                MetaTest.withLineReplacement("finds bug where invalid index positions are not considered",
                        40, 44,
                        ""
                ),
                MetaTest.withStringReplacement("finds bug where the index is of the last value that is not the value to find",
                        "valueToFind == array[i]", "valueToFind != array[i]"),
                MetaTest.withStringReplacement("finds bug where the index of the first occurrence of the value is returned",
                        "int i = startIndex; i >= 0; i--", "int i = 0; i <= startIndex; i++"),
                MetaTest.withLineReplacement("finds bug where the first value in the array is not checked",
                        45, 45,
                        """
                        for (int i = startIndex; i >= 1; i--) {
                        """),
                MetaTest.withLineReplacement("finds bug where startIndex is ignored",
                        45, 45,
                        """
                        for (int i = array.length; i >= 0; i--) {
                        """),
                MetaTest.withStringReplacement("finds bug where -1 is returned even when value is found",
                        "return i;", "return INDEX_NOT_FOUND;")
        );
    }
}
