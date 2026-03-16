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
            put("coverage", 0.25f);
            put("mutation", 0.25f);
            put("meta", 0.5f);
            put("codechecks", 0.0f);
        }};
    }

    @Override
    public List<String> classesUnderTest() {
        return List.of("delft.DelftStringUtilities");
    }

    @Override
    public int numberOfMutationsToConsider() {
        return 16;
    }

    @Override
    public List<MetaTest> metaTests() {
        return List.of(
                MetaTest.withStringReplacement("finds bug where at most one replacement is made",
                        "--max == 0",
                        "true"),
                MetaTest.withStringReplacement("finds bug where all occurrences are always replaced",
                        """
                        if (--max == 0) {
                            break;
                        }""",
                        ""),
                MetaTest.withLineReplacement("finds bug where input is returned unchanged",
                        32, 57,
                        "return text;"),
                MetaTest.insertAt("finds bug where case is always ignored", 35,
                        "ignoreCase = true;"
                ),
                MetaTest.insertAt("finds bug where case is never ignored", 35,
                        "ignoreCase = false;"
                ),
                MetaTest.withStringReplacement("finds bug where starting position is determined by end + replacement.length() + 1",
                        "start = end + replLength;",
                        "start = end + replacement.length() + 1;"),
                MetaTest.insertAt("finds bug where check for empty searchString is broken", 32,
                        """
                        if (isEmpty(searchString)) {
                            return replacement;
                        }
                        """),
                MetaTest.insertAt("finds bug where check for empty text is broken", 32,
                        """
                        if (isEmpty(text)) {
                            return replacement;
                        }
                        """),
                MetaTest.insertAt("finds bug where a null replacement is considered the same as an empty replacement", 32,
                        """
                        if (replacement == null) {
                            replacement = "";
                        }
                        """),
                MetaTest.insertAt("finds bug where max cannot be 0", 32,
                        """
                        if (max == 0) {
                            max = 1;
                        }
                        """)
        );
    }
}
