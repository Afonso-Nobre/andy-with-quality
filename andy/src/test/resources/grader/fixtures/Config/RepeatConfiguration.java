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
            put("mutation", 0.3f);
            put("meta", 0.6f);
            put("codechecks", 0.0f);
        }};
    }

    @Override
    public List<String> classesUnderTest() {
        return List.of("delft.DelftStringUtilities");
    }

    @Override
    public int numberOfMutationsToConsider() {
        return 22;
    }

    @Override
    public List<MetaTest> metaTests() {
        return List.of(
                MetaTest.withStringReplacement("finds bug where method returns empty if str == null",
                        "return null;",
                        "return EMPTY;"),
                MetaTest.withStringReplacement("finds bug where method returns str if repeat <= 0",
                        "return EMPTY;",
                        "return str;"),
                MetaTest.withStringReplacement("finds bug where method returns empty if (repeat == 1 || inputLength == 0)",
                        "return str;",
                        "return EMPTY;"),
                MetaTest.withLineReplacement("finds bug where method always repeats only the first character",
                        57, 76,
                        "return repeat(str.charAt(0), repeat);"),
                MetaTest.withStringReplacement("finds bug where method returns str if input length is 1",
                        "return repeat(str.charAt(0), repeat);",
                        "return str;"),
                MetaTest.withLineReplacement("finds bug where ch0 and ch1 are swapped",
                        66, 67,
                        """
                        output2[i] = ch1;
                        output2[i + 1] = ch0;"""
                ),
                MetaTest.withStringReplacement("finds bug where string is repeated one time less",
                        "i < repeat",
                        "i < repeat - 1"),
                MetaTest.insertAt("finds bug where method returns null if the string is empty", 47,
                        """
                        if (str.isEmpty()) {
                            return null;
                        }""")
        );
    }

}

