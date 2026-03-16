package unit.quality;

import nl.tudelft.cse1110.andy.execution.metatest.MetaTestReport;
import nl.tudelft.cse1110.andy.result.QualityResult;
import nl.tudelft.cse1110.andy.result.TestFailureInfo;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CohesionTest {

    @Test
    public void testTriggersOneMetaTestTest() {
        QualityResult qualityResult = new QualityResult(1);
        qualityResult.setUnitTests(Map.of("testID", "test"));

        TestFailureInfo failure = new TestFailureInfo("test", "error message");
        MetaTestReport metaTestReport = new MetaTestReport(1, 0, 1, List.of(failure));
        metaTestReport.setName("meta-test");
        qualityResult.considerMetaTest(metaTestReport);

        assertEquals(1, qualityResult.countCohesiveTests());
    }

    @Test
    public void testTriggersNoMetaTestTest() {
        QualityResult qualityResult = new QualityResult(1);
        qualityResult.setUnitTests(Map.of("testID", "test"));

        MetaTestReport metaTestReport = new MetaTestReport(1, 1, 1, List.of());
        metaTestReport.setName("meta-test");
        qualityResult.considerMetaTest(metaTestReport);

        assertEquals(0, qualityResult.countCohesiveTests());
    }

    @Test
    public void sameTestTriggersTwoMetaTestsTest() {
        QualityResult qualityResult = new QualityResult(1);
        qualityResult.setUnitTests(Map.of("testID", "test"));

        TestFailureInfo failure1 = new TestFailureInfo("test", "error message");
        TestFailureInfo failure2 = new TestFailureInfo("test", "some other error message");
        MetaTestReport metaTestReport1 = new MetaTestReport(1, 1, 1, List.of(failure1));
        metaTestReport1.setName("meta-test1");
        MetaTestReport metaTestReport2 = new MetaTestReport(1, 1, 1, List.of(failure2));
        metaTestReport2.setName("meta-test2");
        qualityResult.considerMetaTest(metaTestReport1);
        qualityResult.considerMetaTest(metaTestReport2);

        assertEquals(0, qualityResult.countCohesiveTests());
    }

    @Test
    public void differentTestsTriggerDifferentMetaTestsTest() {
        QualityResult qualityResult = new QualityResult(2);
        qualityResult.setUnitTests(Map.of("test1ID", "test1", "test2ID", "test2"));

        TestFailureInfo failure1 = new TestFailureInfo("test1", "error message");
        TestFailureInfo failure2 = new TestFailureInfo("test2", "some other error message");
        MetaTestReport metaTestReport1 = new MetaTestReport(2, 1, 2, List.of(failure1));
        metaTestReport1.setName("meta-test1");
        MetaTestReport metaTestReport2 = new MetaTestReport(2, 1, 2, List.of(failure2));
        metaTestReport2.setName("meta-test2");
        qualityResult.considerMetaTest(metaTestReport1);
        qualityResult.considerMetaTest(metaTestReport2);

        assertEquals(2, qualityResult.countCohesiveTests());
    }

    @Test
    public void differentTestsTriggerSameMetaTestsTest() {
        QualityResult qualityResult = new QualityResult(2);
        qualityResult.setUnitTests(Map.of("test1ID", "test1", "test2ID", "test2"));

        TestFailureInfo failure1 = new TestFailureInfo("test1", "error message");
        TestFailureInfo failure2 = new TestFailureInfo("test2", "some other error message");
        MetaTestReport metaTestReport = new MetaTestReport(2, 0, 2, List.of(failure1, failure2));
        metaTestReport.setName("meta-test");
        qualityResult.considerMetaTest(metaTestReport);

        assertEquals(2, qualityResult.countCohesiveTests());
    }

    @Test
    public void noTestsTest() {
        QualityResult qualityResult = new QualityResult(0);
        qualityResult.setUnitTests(Map.of());

        MetaTestReport metaTestReport = new MetaTestReport(0, 0, 0, List.of());
        metaTestReport.setName("meta-test");
        qualityResult.considerMetaTest(metaTestReport);

        assertEquals(0, qualityResult.countCohesiveTests());
    }

    @Test
    public void noMetaTestsTest() {
        QualityResult qualityResult = new QualityResult(1);
        qualityResult.setUnitTests(Map.of("testID", "test"));

        assertEquals(0, qualityResult.countCohesiveTests());
    }
}
