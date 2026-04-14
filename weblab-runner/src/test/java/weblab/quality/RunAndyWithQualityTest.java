package weblab.quality;

import integration.BaseMetaTestsTest;
import nl.tudelft.cse1110.andy.AndyOnWebLab;
import nl.tudelft.cse1110.andy.execution.mode.Action;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Stream;

import static java.nio.file.Files.createTempDirectory;
import static nl.tudelft.cse1110.andy.utils.FilesUtils.*;

public class RunAndyWithQualityTest extends BaseMetaTestsTest {

    /**
     * For an overview of the results via AndyOnWebLab. To be used in production only.
     */
    @ParameterizedTest
    @MethodSource("domainTestingTestSuites")
    void metaTestQuality(
            String libraryFile,
            String solutionFile,
            String configurationFile
    ) {
        // Prepare the work directory the same way BaseMetaTestsTest / Andy expects it
        copyFiles(libraryFile, solutionFile);
        copyConfigurationFile(configurationFile);

        // Invoke exactly what AndyOnWebLab.main() does, but without System.exit
        String[] args = {
                Action.QUALITY.name(),
                workDir.toString(),
                reportDir.toString()
        };
        AndyOnWebLab.main(args);

        System.out.println(generatedResult()); // stdout
        System.out.println(generatedXml()); // results.xml
    }

    static Stream<Arguments> domainTestingTestSuites() {
        return Stream.of(
                Arguments.of("NumberUtilsAddLibrary", "NumberUtilsAddOfficialSolution", "NumberUtilsAddConfiguration"),
                Arguments.of("IsEqualCollectionLibrary", "IsEqualCollectionOfficialSolution", "IsEqualCollectionConfiguration"),
                Arguments.of("AutoAssignStudentsLibrary", "AutoAssignStudentsOfficialSolution", "AutoAssignStudentsConfiguration")
        );
    }

    protected String generatedResult() {
        return readFile(new File(concatenateDirectories(reportDir.toString(), "stdout.txt")));
    }

    protected String generatedXml() {
        File xmlFile = new File(concatenateDirectories(reportDir.toString(), "results.xml"));
        if (!xmlFile.exists()) {
            System.out.println("XML not found in reportDir. Contents of reportDir:");
            Arrays.stream(reportDir.listFiles())
                    .forEach(f -> System.out.println(f.getAbsolutePath()));
            return "NOT FOUND";
        }
        return readFile(xmlFile);
    }
}
