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
        return List.of("delft.Splitting");
    }

    @Override
    public List<MetaTest> metaTests() {
        return List.of(
                MetaTest.withLineReplacement(1, "finds bug where true is returned if input has odd length", 23, 24,
                        """
                        if (nums == null || nums.length == 0)
                            return false;
                        
                        if (nums.length % 2 == 1)
                            return true;
                        """),
                MetaTest.withLineReplacement(1, "finds bug where false is returned if input has odd length", 23, 24,
                        """
                        if (nums == null || nums.length == 0)
                            return false;
                        
                        if (nums.length % 2 == 1)
                            return false;
                        """),
                MetaTest.withStringReplacement(1, "finds bug where null check is wrong",
                        """
                        if (nums == null || nums.length <= 1)
                                    return false;
                        """,
                        """
                        if (nums == null) return true;
                        
                        if (nums.length <= 1)
                            return false;
                        """),
                MetaTest.withLineReplacement(2, "finds bug where method returns where sum is even", 28, 34,
                        "return (sum % 2 == 0);"),
                MetaTest.withStringReplacement(1, "finds bug where only one loop iteration is performed",
                        "half > 0;",
                        "i < 1;"),
                MetaTest.withLineReplacement(2, "finds bug where the split always happens in the middle", 25, 35,
                        """
                        return sum(nums, 0, nums.length / 2) == sum(nums, nums.length / 2, nums.length)
                                || sum(nums, 0, nums.length / 2 + 1) == sum(nums, nums.length / 2 + 1, nums.length);
                        }
                        
                        private static int sum(int[] nums, int lowerBound, int upperBound) {
                            int result = 0;
                            for (int i = lowerBound; i < upperBound; i++)
                                result += nums[i];
                            return result;
                        }
                        """),
                MetaTest.withLineReplacement(1, "finds bug where method only works for input of length 2",
                        23, 34,
                        "return (nums != null && nums.length == 2 && nums[0] == nums[1]);"),
                MetaTest.withLineReplacement(1, "finds bug where wrong result is returned for input of length 1", 23, 24,
                        """
                        if (nums == null || nums.length == 0)
                            return false;
                        
                        if (nums.length == 1)
                            return true;
                        """),
                MetaTest.withLineReplacement(1, "finds bug where wrong result is returned when input is empty",
                        23, 24,
                        """
                        if (nums == null || nums.length == 1)
                            return false;
                        
                        if (nums.length == 0)
                            return true;
                        """)
        );
    }
}