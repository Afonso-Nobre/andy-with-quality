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
            put("mutation", 0.4f);
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
                MetaTest.withStringReplacement("finds bug where check for null str is missing",
                        "if (str == null || isEmpty(open) || isEmpty(close)) {",
                        "if (isEmpty(open) || isEmpty(close)) {"),

                MetaTest.withStringReplacement("finds bug where check for null open is missing",
                        "if (str == null || isEmpty(open) || isEmpty(close)) {",
                        "if (str == null || open.isEmpty() || isEmpty(close)) {"),

                MetaTest.withStringReplacement("finds bug where check for empty open is missing",
                        "if (str == null || isEmpty(open) || isEmpty(close)) {",
                        "if (str == null || open == null || isEmpty(close)) {"),

                MetaTest.withStringReplacement("finds bug where check for null close is missing",
                        "if (str == null || isEmpty(open) || isEmpty(close)) {",
                        "if (str == null || isEmpty(open) || close.isEmpty()) {"),

                MetaTest.withStringReplacement("finds bug where check for empty close is missing",
                        "if (str == null || isEmpty(open) || isEmpty(close)) {",
                        "if (str == null || isEmpty(open) || close == null) {"),

                MetaTest.withLineReplacement("finds bug where null is returned for empty input", 40, 40,
                        "return null;"),

                MetaTest.withStringReplacement("finds bug where first character is skipped",
                        "int pos = 0;",
                        "int pos = 1;"),

                MetaTest.withLineReplacement("finds bug where empty list is returned when there are no matches",
                        59, 61, ""),

                MetaTest.withLineReplacement("finds bug where only the first match is returned", 62, 62,
                        "return new String[] { list.get(0) };"),

                MetaTest.withStringReplacement("finds bug where method skips a character after finding a match",
                        "pos = end + closeLen;",
                        "pos = end + closeLen + 1;"),

                MetaTest.withLineReplacement("finds bug where method does not work with open string longer than 1 character", 34, 37,
                        """
                        public static String[] substringsBetween(final String str, String open, final String close) {
                            if (str == null || isEmpty(open) || isEmpty(close)) {
                                return null;
                            }
                            open = open.substring(1, open.length());
                        """),

                MetaTest.withLineReplacement("finds bug where method does not work with close string longer than 1 character", 34, 37,
                        """
                        public static String[] substringsBetween(final String str, final String open, String close) {
                            if (str == null || isEmpty(open) || isEmpty(close)) {
                                return null;
                            }
                            close = close.substring(0, open.length() - 1);
                        """),
                MetaTest.withLineReplacement("finds bug where matching happens greedily instead of lazily",
                        42, 58,
                        """
                        // Re-implementation of the method using a regular expression.
                        // With (.*?), this mutant does the same as the original method.
                        // With (.*), the matching happens greedily.
                        // For example, if str=="ababa", open=="a", close=="a",
                        // lazy matching would return substring "b",
                        // and greedy matching would return substring "bab".
                        String regex = Pattern.quote(open) + "(.*)" + Pattern.quote(close);
                        Matcher matcher = Pattern.compile(regex).matcher(str);
        
                        List<String> list = new ArrayList<>();
        
                        while (matcher.find()) {
                                list.add(matcher.group(1));
                        }""")
        );
    }

}