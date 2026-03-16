package delft;

import nl.tudelft.cse1110.andy.config.MetaTest;
import nl.tudelft.cse1110.andy.config.RunConfiguration;
import nl.tudelft.cse1110.andy.execution.mode.Mode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Configuration extends RunConfiguration {
    @Override
    public Mode mode() {
        return Mode.GRADING;
    }

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
        return List.of("delft.CodeSnippetUtils");
    }


    @Override
    public List<MetaTest> metaTests() {
        return List.of(
                MetaTest.withStringReplacement("finds bug where method does not work if line number is near beginning of file",
                        "Math.max(0, lineNumberZeroIndexed - SURROUNDING_LINES)",
                        "lineNumberZeroIndexed - SURROUNDING_LINES"),
                MetaTest.withStringReplacement("finds off by one error at end of file",
                        "Math.min(lines.size() - 1, lineNumberZeroIndexed + SURROUNDING_LINES)",
                        "Math.min(lines.size(), lineNumberZeroIndexed + SURROUNDING_LINES)"),
                MetaTest.withStringReplacement(2, "finds bug where method does not preserve relative indentation",
                        ".map(x -> x.isBlank() ? \"\" : x.substring(spacesToTrim))",
                        ".map(x -> x.isBlank() ? \"\" : x.substring(getNumberOfLeadingSpaces(x)))"),
                MetaTest.withStringReplacement(3, "finds bug where method does not trim indentation correctly with shorter blank/whitespace lines in snippet",
                        """
                        .filter(s -> !s.isBlank())
                        .mapToInt(CodeSnippetUtils::getNumberOfLeadingSpaces)
                        """,
                        """
                        .mapToInt(s -> s.isBlank() ? s.length() : getNumberOfLeadingSpaces(s))
                        """),
                MetaTest.withStringReplacement("finds bug where arrows are placed on all lines",
                        "String s = i == relativeLineNumber ?",
                        "String s = true ?")
        );
    }
}
