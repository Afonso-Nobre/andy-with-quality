package delft;

import nl.tudelft.cse1110.andy.codechecker.checks.*;
import nl.tudelft.cse1110.andy.codechecker.engine.CheckScript;
import nl.tudelft.cse1110.andy.codechecker.engine.SingleCheck;
import nl.tudelft.cse1110.andy.config.MetaTest;
import nl.tudelft.cse1110.andy.config.RunConfiguration;
import nl.tudelft.cse1110.andy.execution.mode.Mode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Configuration extends RunConfiguration {

    @Override
    public Mode mode() {
        return Mode.PRACTICE;
    }

    @Override
    public List<String> classesUnderTest() {
        return List.of("delft.SeatFinder");
    }

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
    public List<MetaTest> metaTests() {
        return List.of(
                MetaTest.insertAt("finds bug where no exception is thrown if `prices` is null",
                        32, "if (prices == null) return -1;"),
                MetaTest.insertAt("finds bug where no exception is thrown if `taken` is null",
                        32, "if (taken == null) return -1;"),
                MetaTest.insertAt("finds bug where no exception is thrown if `prices` and `taken` have different lengths",
                        35, "if (prices.length != taken.length) return 0;"),
                MetaTest.insertAt("finds bug where no exception is thrown for numberOfSeats == 0",
                        38, "if (numberOfSeats == 0) return 0;"),
                MetaTest.insertAt("finds bug where no exception is thrown for numberOfSeats < 0",
                        38, "if (numberOfSeats < 0) return 0;"),
                MetaTest.withLineReplacement("finds bug where seats array is not sorted",
                        42, 46, "int[] seats = IntStream.range(0, prices.length).toArray();"),
                MetaTest.withLineReplacement("finds bug where loop continues when numberOfTickets == numberOfSeats",
                        57, 57, "continue;"),
                MetaTest.withStringReplacement("finds bug where seats are never considered taken",
                        "!taken[seat]", "true"),
                MetaTest.withStringReplacement("finds bug where discount is only applied when total price equals 100",
                        "totalPrice > 100.00",
                        "totalPrice == 100.00"),
                MetaTest.withLineReplacement("finds bug where discount is never applied",
                        62, 62, "")
        );
    }
}

