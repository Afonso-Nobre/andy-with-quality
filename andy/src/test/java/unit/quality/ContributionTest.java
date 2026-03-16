package unit.quality;

import nl.tudelft.cse1110.andy.execution.metatest.MetaTestReport;
import nl.tudelft.cse1110.andy.result.QualityResult;
import nl.tudelft.cse1110.andy.result.TestFailureInfo;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ContributionTest {

    @Test
    public void testNoTestsContributeWhenAllMapsEmpty() {
        QualityResult qualityResult = new QualityResult(3);
        qualityResult.setUnitTests(Map.of("id1", "test1", "id2", "test2", "id3", "test3"));
        // no meta tests, coverage, or mutations set
        assertEquals(0, qualityResult.countContributingTests());
    }

    @Test
    public void testContributesViaMetaTestOnly() {
        QualityResult qualityResult = new QualityResult(1);
        qualityResult.setUnitTests(Map.of("id1", "test1"));

        TestFailureInfo failure = new TestFailureInfo("test1", "error");
        MetaTestReport report = new MetaTestReport(1, 1, 1, List.of(failure));
        report.setName("meta-test1");
        qualityResult.considerMetaTest(report);

        assertEquals(1, qualityResult.countContributingTests());
    }

    @Test
    public void testContributesViaCoverageOnly() {
        QualityResult qualityResult = new QualityResult(1);
        qualityResult.setUnitTests(Map.of("id1", "test1"));
        qualityResult.setCoveragePerTest(Map.of("test1", Set.of(10, 11, 12)));

        assertEquals(1, qualityResult.countContributingTests());
    }

    @Test
    public void testContributesViaMutationOnly() {
        QualityResult qualityResult = new QualityResult(1);
        qualityResult.setUnitTests(Map.of("id1", "test1"));
        qualityResult.setMutationsKilledPerTest(Map.of("test1", Set.of(1, 2)));

        assertEquals(1, qualityResult.countContributingTests());
    }

    @Test
    public void testSameTestContributesViaAllThree() {
        QualityResult qualityResult = new QualityResult(1);
        qualityResult.setUnitTests(Map.of("id1", "test1"));

        TestFailureInfo failure = new TestFailureInfo("test1", "error");
        MetaTestReport report = new MetaTestReport(1, 1, 1, List.of(failure));
        report.setName("meta-test1");
        qualityResult.considerMetaTest(report);
        qualityResult.setCoveragePerTest(Map.of("test1", Set.of(10)));
        qualityResult.setMutationsKilledPerTest(Map.of("test1", Set.of(1)));

        // still counts as 1 contributing test, not 3
        assertEquals(1, qualityResult.countContributingTests());
    }

    @Test
    public void testMultipleTestsEachContributingDifferently() {
        QualityResult qualityResult = new QualityResult(3);
        qualityResult.setUnitTests(Map.of("id1", "test1", "id2", "test2", "id3", "test3"));

        TestFailureInfo failure = new TestFailureInfo("test1", "error");
        MetaTestReport report = new MetaTestReport(1, 1, 1, List.of(failure));
        report.setName("meta-test1");
        qualityResult.considerMetaTest(report);
        qualityResult.setCoveragePerTest(Map.of("test2", Set.of(10)));
        qualityResult.setMutationsKilledPerTest(Map.of("test3", Set.of(1)));

        assertEquals(3, qualityResult.countContributingTests());
    }

    @Test
    public void testTestWithEmptySetsDoesNotContribute() {
        QualityResult qualityResult = new QualityResult(1);
        qualityResult.setUnitTests(Map.of("id1", "test1"));
        qualityResult.setCoveragePerTest(Map.of("test1", Set.of()));
        qualityResult.setMutationsKilledPerTest(Map.of("test1", Set.of()));

        assertEquals(0, qualityResult.countContributingTests());
    }
}
