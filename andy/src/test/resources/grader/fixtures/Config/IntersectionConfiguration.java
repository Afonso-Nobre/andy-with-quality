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
        return List.of("delft.ListUtils");
    }

    @Override
    public int numberOfMutationsToConsider() {
        return 2;
    }

    @Override
    public List<MetaTest> metaTests() {
        return List.of(
                MetaTest.insertAt("finds bug where method returns empty list when list1 is null", 29,
                        "if (list1 == null) return result;"
                ),
                MetaTest.insertAt("finds bug where method returns empty list when list2 is null", 29,
                        "if (list2 == null) return result;"
                ),
                MetaTest.withStringReplacement("finds bug where method returns wrong elements",
                        "if (hashSet.contains(e))",
                        "if (!hashSet.contains(e))"
                ),
                MetaTest.insertAt("finds bug where method returns null when there are no elements in the intersection", 42,
                        "if (result.isEmpty()) return null;"
                ),
                MetaTest.withStringReplacement("finds bug where method does not work correctly when one of the lists is empty",
                        "if (hashSet.contains(e))",
                        "if (hashSet.isEmpty() || hashSet.contains(e))"
                ),
                MetaTest.withStringReplacement("finds bug where method adds repeated elements to result multiple times",
                        "hashSet.remove(e);",
                        ""
                ),
                MetaTest.withStringReplacement("finds bug where method always returns smaller",
                        "return result;",
                        "return new ArrayList<>(smaller);"),
                MetaTest.insertAt("finds bug where method finds only the first element in the intersection", 40,
                        "break;"
                ),
                MetaTest.withLineReplacement("finds bug where method compares only elements at the same index", 29, 41,
                        """
                        for (int i = 0; i < list1.size() && i < list2.size(); i++) {
                            if (list1.get(i).equals(list2.get(i))) {
                                result.add(list1.get(i));
                            }
                        }
                        """
                )
        );
    }

}