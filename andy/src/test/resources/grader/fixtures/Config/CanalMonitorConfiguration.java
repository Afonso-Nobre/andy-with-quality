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
            put("mutation", 0.15f);
            put("meta", 0.75f);
            put("codechecks", 0.0f);
        }};
    }

    @Override
    public List<String> classesUnderTest() {
        return List.of("delft.CanalMonitor");
    }

    @Override
    public int numberOfMutationsToConsider() {
        return 9;
    }

    @Override
    public List<MetaTest> metaTests() {
        return List.of(

            // nullReadings — detects removal of null check
            MetaTest.withLineReplacement(
                    "finds bug where null check is missing",
                    24, 25,
                    ""
            ),

            // emptyArray — detects removal of the length < 1 guard
            MetaTest.withStringReplacement(
                    "finds bug where empty array is not rejected",
                    "readings.length < 1 || readings.length > 24",
                    "readings.length > 24"
            ),

            // arrayTooLong — detects removal of the length > 24 guard
            MetaTest.withStringReplacement(
                    "finds bug where oversized array is not rejected",
                    "readings.length < 1 || readings.length > 24",
                    "readings.length < 1"
            ),

            // warningLevelZero — detects removal of the warningLevel validation
            MetaTest.withLineReplacement(
                    "finds bug where warningLevel validation is missing",
                    30, 33,
                    ""
            ),

            // negativeReading — detects removal of negative element check
            MetaTest.withLineReplacement(
                    "finds bug where negative readings are not rejected",
                    39, 41,
                    ""
            ),

            // arrayLengthOne — detects off-by-one: min length allows 0
            MetaTest.withStringReplacement(
                    "finds bug where array of length 0 is incorrectly accepted (off-by-one on minimum length)",
                    "readings.length < 1",
                    "readings.length < 0"
            ),

            // arrayLengthTwentyFour — detects off-by-one: max length rejects 24
            MetaTest.withStringReplacement(
                    "finds bug where array of length 24 is incorrectly rejected (off-by-one on maximum length)",
                    "readings.length > 24",
                    "readings.length > 23"
            ),

            // warningLevelOne — detects off-by-one: warningLevel=1 incorrectly rejected
            MetaTest.withStringReplacement(
                    "finds bug where warningLevel of 1 is incorrectly rejected (off-by-one on minimum warningLevel)",
                    "warningLevel < 1",
                    "warningLevel < 2"
            ),

            // readingAtWarningLevel — detects >= instead of > when counting high readings
            MetaTest.withStringReplacement(
                    "finds bug where a reading equal to warningLevel is incorrectly counted as high",
                    "if (readings[i] > warningLevel)",
                    "if (readings[i] >= warningLevel)"
            ),

            // readingOneAboveWarningLevel — detects removal of h++ (high readings never counted)
            MetaTest.withStringReplacement(
                    "finds bug where high readings are never counted",
                    "h++",
                    "h = 0"
            ),

            // readingAtCriticalThreshold — detects >= instead of > for critical check
            MetaTest.withStringReplacement(
                    "finds bug where a reading equal to warningLevel + 50 is incorrectly classified as critical",
                    "if (readings[i] > warningLevel + 50)",
                    "if (readings[i] >= warningLevel + 50)"
            ),

            // readingOneAboveCriticalThreshold — detects critical path returning 2 instead of 3
            MetaTest.withLineReplacement(
                    "finds bug where critical reading returns 2 instead of 3",
                    43, 43,
                    "return 2;"
            ),

            // ratioExactlyHalfFloodSeason — detects >= 0.5 instead of > 0.5 in flood branch
            MetaTest.withStringReplacement(
                    "finds bug where r = 0.5 is incorrectly classified as high risk in flood season",
                    "isFloodSeason && r > 0.5",
                    "isFloodSeason && r >= 0.5"
            ),

            // ratioJustAboveHalfFloodSeason — detects flood-season branch being dropped
            MetaTest.withStringReplacement(
                    "finds bug where flood season threshold is not applied (only non-flood threshold used)",
                    "(isFloodSeason && r > 0.5) || (!isFloodSeason && r > 0.75)",
                    "!isFloodSeason && r > 0.75"
            ),

            // ratioExactly75NonFlood — detects >= 0.75 instead of > 0.75 in non-flood branch
            MetaTest.withStringReplacement(
                    "finds bug where r = 0.75 is incorrectly classified as high risk outside flood season",
                    "!isFloodSeason && r > 0.75",
                    "!isFloodSeason && r >= 0.75"
            ),

            // ratioJustAbove75NonFlood — detects non-flood branch being dropped
            MetaTest.withStringReplacement(
                    "finds bug where non-flood season threshold is not applied (only flood threshold used)",
                    "(isFloodSeason && r > 0.5) || (!isFloodSeason && r > 0.75)",
                    "isFloodSeason && r > 0.5"
            ),

            // ratioZero — detects always returning 1 instead of 0 when no high readings
            MetaTest.withStringReplacement(
                    "finds bug where 0 (normal) is never returned — always returns at least 1",
                    "return r > 0 ? 1 : 0;",
                    "return 1;"
            ),

            // ratioJustAboveZero — detects always returning 0 instead of 1 for elevated
            MetaTest.withStringReplacement(
                    "finds bug where 1 (elevated) is never returned — always returns 0 when below high-risk threshold",
                    "return r > 0 ? 1 : 0;",
                    "return 0;"
            )
        );
    }

}