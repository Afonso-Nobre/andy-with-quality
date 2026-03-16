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
            put("coverage", 0.2f);
            put("mutation", 0.3f);
            put("meta", 0.5f);
            put("codechecks", 0.0f);
        }};
    }

    @Override
    public List<String> classesUnderTest() {
        return List.of("delft.DelftCaseUtilities");
    }

    @Override
    public List<MetaTest> metaTests() {
        return List.of(
                MetaTest.withStringReplacement("finds bug where method does not check for null",
                        "if (str == null || str.isEmpty())",
                        "if (str.isEmpty())"),

                MetaTest.withLineReplacement("finds bug where null is returned if input is empty",
                        36, 36,
                        "return null;"),

                MetaTest.withStringReplacement("finds bug where method does not split by space unless explicitly specified",
                        "delimiterHashSet.add(Character.codePointAt(new char[]{' '}, 0));",
                        ""),

                MetaTest.withLineReplacement("finds bug where method only splits by space", 87, 89,
                        ""),

                MetaTest.withLineReplacement("finds bug where method does not split by space if other delimiters are specified",
                        83, 86,
                        """
                        if (delimiters == null || delimiters.length == 0) {
                            delimiterHashSet.add(Character.codePointAt(new char[]{' '}, 0));
                            return delimiterHashSet;
                        }
                        """),

                MetaTest.withStringReplacement("finds bug where delimiters parameter cannot be null",
                        "if (delimiters == null || delimiters.length == 0) {",
                        "if (delimiters.length == 0) {"),

                MetaTest.withLineReplacement("finds bug where only the first delimiter is used",
                        87, 89,
                        "delimiterHashSet.add(Character.codePointAt(delimiters, 0));"),

                MetaTest.withStringReplacement("finds bug where input is not converted to lowercase",
                        "str = str.toLowerCase();",
                        ""),

                MetaTest.withLineReplacement("finds bug where first letter is always capitalised", 43, 46,
                        "boolean capitalizeNext = true;"),

                MetaTest.withLineReplacement("finds bug where first letter is never capitalised", 43, 46,
                        "boolean capitalizeNext = false;")
        );
    }

}