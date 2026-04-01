package nl.tudelft.cse1110.andy.result;

import net.jqwik.api.Tuple;
import nl.tudelft.cse1110.andy.execution.metatest.MetaTestReport;

import java.util.*;
import java.util.stream.Collectors;

public class QualityResult {
    private int score; // between 0 and 100 - see computeScore() for more details
    private int numUnitTests;
    private Map<String, String> unitTests; // uniqueId -> displayName
    private Map<String, String> displayNameToUniqueId;

    private LinkedList<MetaTestReport> metaTestReports;
    private Map<String, Set<String>> testToMetaTests; // displayName -> meta-tests

    private Map<String, Set<Integer>> coveragePerTest; // displayName -> linesCovered
    private Map<String, Set<Integer>> mutationsKilledPerTest; // displayName -> mutationId

    public QualityResult(int numUnitTests) {
        // dummy:
        this.score = 0;
        this.numUnitTests = numUnitTests;
        unitTests = new HashMap<>();
        displayNameToUniqueId = new LinkedHashMap<>();
        metaTestReports  = new LinkedList<>();
        testToMetaTests  = new LinkedHashMap<>();
        coveragePerTest  = new LinkedHashMap<>();
        mutationsKilledPerTest = new LinkedHashMap<>();
    }

    public static QualityResult build(int score) {
        return new QualityResult(score);
    }

    public static QualityResult empty() {
        return new QualityResult(0);
    }

    public int getScore() {
        return score;
    }

    public LinkedList<MetaTestReport> getMetaTestReports() {
        return metaTestReports;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getNumUnitTests() {
        return numUnitTests;
    }

    public void setNumUnitTests(int numUnitTests) {
        this.numUnitTests = numUnitTests;
    }

    public Map<String, String> getUnitTests() {
        return unitTests;
    }

    public void setUnitTests(Map<String, String> unitTests) {
        this.unitTests = unitTests;

        // initialize the map from displayName to uniqueId:
        for (String uniqueId :  unitTests.keySet()) {
            displayNameToUniqueId.put(unitTests.get(uniqueId), uniqueId);
        }

        // initialize other maps:
        for (String uniqueId :  unitTests.keySet()) {
            testToMetaTests.put(uniqueId, new HashSet<>());
            coveragePerTest.put(uniqueId, new HashSet<>());
            mutationsKilledPerTest.put(uniqueId, new HashSet<>());
        }
    }

    public Map<String, Set<Integer>> getCoveragePerTest() {
        return coveragePerTest;
    }

    public void setCoveragePerTest(Map<String, Set<Integer>> coveragePerTest) {
        for (String displayName :  coveragePerTest.keySet()) {
            this.coveragePerTest.put(displayName, coveragePerTest.get(displayName));
        }
    }

    public Map<String, Set<Integer>> getMutationsKilledPerTest() {
        return mutationsKilledPerTest;
    }

    public void setMutationsKilledPerTest(Map<String, Set<Integer>> mutationsKilledPerTest) {
        for (String displayName :  mutationsKilledPerTest.keySet()) {
            this.mutationsKilledPerTest.put(displayName, mutationsKilledPerTest.get(displayName));
        }
    }

    @Override
    public String toString() {
        return "QualityResult{" +
                "score=" + score +
                '}';
    }

    public void considerMetaTest(String metaTestName, MetaTestReport metaTestReport) {
        this.metaTestReports.addFirst(metaTestReport);

        for (TestFailureInfo failure : metaTestReport.getTestsTriggered()) {
            String displayName = failure.getTestCase();

            String uniqueId = displayNameToUniqueId.get(displayName);

            testToMetaTests.computeIfAbsent(uniqueId, k -> new HashSet<>());

            testToMetaTests.get(uniqueId).add(metaTestName);
        }
    }

    public int computeScore() {
        /*
        Only cohesion and isolation are used to compute the final score.
        The maximum score (100) corresponds to an average of 0.7 in both scores.
         */

        double actualScore = (countCohesiveTests() + countIsolatedTests()) / (2.0 * countTests()) * 100;

        if (actualScore >= 70.0) {
            this.score = (int) actualScore;
        } else {
            this.score = (int) (actualScore * 100/70);
        }

        return this.score;
    }

    public long countTests() {
        return numUnitTests;
    }

    /**
     * Get how many tests cover a single meta-test
     * @return the number of such tests
     */
    public long countCohesiveTests() {
        return testToMetaTests.values().stream().filter(nt -> nt.size() == 1).count();
    }

    @SuppressWarnings("checkstyle:DeclarationOrder")
    private Map<String, Set<String>> nonisolatedTests = new HashMap<>();

    /**
     * Count the number of tests that do not trigger meta-tests already covered by other tests
     * AND trigger more than one meta-test
     * @return the number of such tests
     */
    public long countIsolatedTests() {

        // tests that trigger no meta-tests
        for (String test :  testToMetaTests.keySet()) {
            if (testToMetaTests.get(test).isEmpty()) {
                String displayName = unitTests.get(test);
                nonisolatedTests.put(displayName, new HashSet<>());
            }
        }

        // tests that trigger meta-tests triggered by other tests
        for (MetaTestReport metaTestReport : metaTestReports) {
            if (metaTestReport.getTestsTriggered().size() == 1) continue;

            // all tests that trigger this meta-test collide with each other
            List<String> collidingTests = metaTestReport.getTestsTriggered().stream()
                    .map(TestFailureInfo::getTestCase)
                    .toList();

            for (String test : collidingTests) {
                nonisolatedTests.computeIfAbsent(test, t -> new HashSet<>())
                        .addAll(collidingTests.stream()
                                .filter(other -> !other.equals(test))
                                .collect(Collectors.toSet()));
            }
        }

        int count = numUnitTests;

        for (Set<String> collisions : nonisolatedTests.values()) {
            if (!collisions.isEmpty()) count--;
        }

        return count;
    }

    @SuppressWarnings("checkstyle:DeclarationOrder")
    Map<String, List<Integer>> contributingTests = new HashMap<>();

    /**
     * Count the number of tests that increase one of:
     * 1) number of meta tests triggered
     * 2) lines covered
     * 3) mutations killed
     * @return the number of such tests
     */
    public long countContributingTests() {

        // 1)
        Set<String> contributingMetaTests = contribution(testToMetaTests);
        for (String test : contributingMetaTests) {
            contributingTests.computeIfAbsent(test, t -> new ArrayList<>());
            contributingTests.get(test).add(1);
        }

        // 2)
        Set<String> contributingCoverage = contribution(coveragePerTest);
        for (String test : contributingCoverage) {
            contributingTests.computeIfAbsent(test, t -> new ArrayList<>());
            contributingTests.get(test).add(2);
        }

        // 3)
        Set<String> contributingMutation = contribution(mutationsKilledPerTest);
        for (String test : contributingMutation) {
            contributingTests.computeIfAbsent(test, t -> new ArrayList<>());
            contributingTests.get(test).add(3);
        }

        return contributingTests.size();
    }

    private <T> Set<String> contribution(Map<String, Set<T>> map) {
        Set<String> contributingTests = new HashSet<>();
        Set<T> done = new HashSet<>();
        for (String test : map.keySet()) {
            Set<T> testContribution = new HashSet<>(map.get(test));
            testContribution.removeAll(done);
            if (!testContribution.isEmpty()) {
                contributingTests.add(test);
                done.addAll(testContribution);
            }
        }
        return contributingTests;
    }

    /**
     * Used to get an overview of cohesive tests in the output
     * @return a list of cohesive and non-cohesive tests
     */
    public String listCohesiveTests() {
        StringBuilder sb = new StringBuilder("Tests that only trigger a single meta-test: \n");

        for (String uniqueId : unitTests.keySet()) {
            String displayName = unitTests.get(uniqueId);
            if (testToMetaTests.get(uniqueId) == null ||
                    testToMetaTests.get(uniqueId).size() != 1) {
                sb.append("  > " + displayName + " ✕\n");
            } else {
                sb.append("  > " + displayName + " ✓\n");
            }
        }

        return sb.toString();
    }

    /**
     * Used to get an overview of isolated tests in the output
     * @return a list of isolated and non-isolated tests
     */
    public String listIsolatedTests() {

        StringBuilder sb = new StringBuilder("Tests that do not trigger meta-tests already covered by other tests (and trigger at least one meta-test): \n");

        for (String uniqueId : unitTests.keySet()) {
            String displayName = unitTests.get(uniqueId);
            if (nonisolatedTests.containsKey(displayName)) {
                sb.append("  > " + displayName + " ✕ - ");
                Set<String> collisions =  nonisolatedTests.get(displayName);
                if (collisions.isEmpty()) {
                    sb.append("this test triggers no meta-tests");
                }
                for (String collision : collisions) {
                    sb.append(collision + "; ");
                }
                sb.append("\n");
            } else {
                sb.append("  > " + displayName + " ✓\n");
            }
        }

        return sb.toString();
    }

    /**
     * Used to get an overview of contributing tests in the output
     * @return a list of contributing and non-contributing tests
     */
    public String listContributingTests() {

        StringBuilder sb = new StringBuilder("Tests that increase a metric (meta-tests, coverage or mutation): \n");

        for (String uniqueId : unitTests.keySet()) {
            String displayName = unitTests.get(uniqueId);
            if (contributingTests.containsKey(uniqueId)) {
                sb.append("  > " + displayName + " ✓ - ");
                List<Integer> contributions =  contributingTests.get(uniqueId);
                for (int contribution : contributions) {
                    switch (contribution) {
                        case 1:
                            sb.append("meta-tests; ");
                            break;
                        case 2:
                            sb.append("coverage; ");
                            break;
                        case 3:
                            sb.append("mutation; ");
                            break;
                        default:
                    }
                }
                sb.append("\n");
            } else {
                sb.append("  > " + displayName + " ✕\n");
            }
        }

        return sb.toString();
    }
}
