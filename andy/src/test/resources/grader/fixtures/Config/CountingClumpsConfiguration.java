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
        return List.of("delft.Clumps");
    }

    @Override
    public List<MetaTest> metaTests() {
        return List.of(
                MetaTest.withLineReplacement("finds bug where method always finds clumps", 23, 36,
                        "return nums.length / 2;"),
                MetaTest.withLineReplacement("finds bug where method always returns zero", 20, 36,
                        "return 0;"),
                MetaTest.withLineReplacement("finds bug where method checks in pairs", 24, 35,
                        """
                        for (int i = 0; i < nums.length; i+=2) {
                            if (i + 1 < nums.length && nums[i] == nums[i+1]) {
                                count += 1;
                            }
                        }
                        """),
                MetaTest.withLineReplacement("finds bug where method does not support more than two per clump", 25, 35,
                        """
                        for (int i = 1; i < nums.length; i++) {
                            if (nums[i] == prev) {
                                count += 1;
                            }
                            if (nums[i] != prev) {
                                prev = nums[i];
                            }
                        }
                        """),
                MetaTest.withStringReplacement("finds bug where method does not support multiple clumps",
                        "return count;",
                        "return count > 1 ? 1 : count;"),
                MetaTest.withStringReplacement("finds bug where check for empty is missing",
                        "if (nums == null || nums.length == 0)",
                        "if (nums == null)"),
                MetaTest.withStringReplacement("finds bug where check for null is missing",
                        "if (nums == null || nums.length == 0)",
                        "if (nums.length == 0)"),
                MetaTest.withLineReplacement("finds bug where method only checks the first two elements", 20, 36,
                        """
                        if (nums != null && nums.length >= 2 && nums[0] == nums[1]) {
                            return 1;
                        }
                        else {
                            return 0;
                        }
                        """),
                MetaTest.withLineReplacement("finds bug where method only checks the last two elements", 20, 36,
                        """
                        if (nums != null && nums.length >= 2 && nums[nums.length - 2] == nums[nums.length - 1]) {
                            return 1;
                        }
                        else {
                            return 0;
                        }
                        """),
                MetaTest.withLineReplacement("finds bug where elements are skipped after clumps", 34, 34,
                        """
                        }
                        if (inClump) {
                            i++;
                        }
                        """),
                MetaTest.withLineReplacement("finds bug where first element is skipped", 20, 26,
                        """
                        if (nums == null || nums.length <= 1) {
                            return 0;
                        }
                        int count = 0;
                        int prev = nums[1];
                        boolean inClump = false;
                        for (int i = 2; i < nums.length; i++) {
                        """),
                MetaTest.withStringReplacement("finds bug where last element is skipped",
                        "i < nums.length;",
                        "i < nums.length - 1;"),
                MetaTest.withStringReplacement("finds bug where wrong result is returned for one element",
                        "int count = 0;",
                        """
                        if (nums.length == 1) {
                            return 1;
                        }
                        int count = 0;
                        """)
        );
    }
}