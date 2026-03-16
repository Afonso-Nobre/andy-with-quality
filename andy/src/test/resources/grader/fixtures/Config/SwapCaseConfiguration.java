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
        return List.of("delft.DelftWordUtilities");
    }

    @Override
    public int numberOfMutationsToConsider() {
        return 10;
    }

    @Override
    public List<MetaTest> metaTests() {
        return List.of(
                MetaTest.withStringReplacement("finds bug where last character is not considered",
                        "buffer.length",
                        "buffer.length - 1"),
                MetaTest.withStringReplacement("finds bug where first character is not considered",
                        "i = 0",
                        "i = 1"),
                MetaTest.withStringReplacement("finds bug where first if-statement is negated",
                        "DelftWordUtilities.isEmpty(str)",
                        "!DelftWordUtilities.isEmpty(str)"),
                MetaTest.withStringReplacement("finds bug where second if-statement is negated",
                        "Character.isUpperCase(ch)",
                        "!Character.isUpperCase(ch)"),
                MetaTest.withStringReplacement("finds bug where third if-statement is negated",
                        "Character.isLowerCase(ch)",
                        "!Character.isLowerCase(ch)"),
                MetaTest.withStringReplacement("finds bug where case is only swapped for uppercase characters",
                        """
                        else if (Character.isLowerCase(ch)) {
                            buffer[i] = Character.toUpperCase(ch);
                        }""",
                        ""),
                MetaTest.withStringReplacement("finds bug where case is only swapped for lowercase characters",
                        """
                        if (Character.isUpperCase(ch)) {
                            buffer[i] = Character.toLowerCase(ch);
                        } else if (Character.isLowerCase(ch)) {
                            buffer[i] = Character.toUpperCase(ch);
                        }
                        """,
                        """
                        if (Character.isLowerCase(ch)) {
                            buffer[i] = Character.toUpperCase(ch);
                        }
                        """),
                MetaTest.withStringReplacement("finds bug where function does not swap uppercase characters",
                        """
                        if (Character.isUpperCase(ch)) {
                            buffer[i] = Character.toLowerCase(ch);
                        """,
                        """
                        if (Character.isUpperCase(ch)) {
                            buffer[i] = Character.toUpperCase(ch);
                        """),
                MetaTest.withStringReplacement("finds bug where function does not swap lowercase characters",
                        """
                        else if (Character.isLowerCase(ch)) {
                            buffer[i] = Character.toUpperCase(ch);
                        }
                        """,
                        """
                        else if (Character.isLowerCase(ch)) {
                            buffer[i] = Character.toLowerCase(ch);
                        }
                        """)
        );
    }

}
