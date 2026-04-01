package weblab.quality;

import integration.BaseMetaTestsTest;
import nl.tudelft.cse1110.andy.config.DirectoryConfiguration;
import nl.tudelft.cse1110.andy.execution.Context.Context;
import nl.tudelft.cse1110.andy.execution.mode.Action;
import nl.tudelft.cse1110.andy.execution.mode.Mode;
import nl.tudelft.cse1110.andy.execution.mode.ModeActionSelector;
import nl.tudelft.cse1110.andy.result.Result;
import nl.tudelft.cse1110.andy.writer.ResultWriter;
import nl.tudelft.cse1110.andy.writer.standard.CodeSnippetGenerator;
import nl.tudelft.cse1110.andy.writer.standard.RandomAsciiArtGenerator;
import nl.tudelft.cse1110.andy.writer.standard.VersionInformation;
import nl.tudelft.cse1110.andy.writer.weblab.WebLabResultWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Stream;

import static nl.tudelft.cse1110.andy.utils.FilesUtils.concatenateDirectories;
import static nl.tudelft.cse1110.andy.utils.FilesUtils.readFile;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OverviewQualityResultsWebLab extends BaseMetaTestsTest {
    protected Context ctx = mock(Context.class);
    protected VersionInformation versionInformation = new VersionInformation("testVersion", "testBuildTimestamp", "testCommitId");
    protected RandomAsciiArtGenerator asciiArtGenerator = mock(RandomAsciiArtGenerator.class);
    protected CodeSnippetGenerator codeSnippetGenerator = mock(CodeSnippetGenerator.class);
    protected ResultWriter writer;

    protected ResultWriter buildWriter() {
        return new WebLabResultWriter(versionInformation, asciiArtGenerator, codeSnippetGenerator);
    }

    @TempDir
    protected Path reportDir;

    @BeforeEach
    void setupMocks() throws FileNotFoundException {
        DirectoryConfiguration dirs = new DirectoryConfiguration(null, reportDir.toString());
        when(ctx.getDirectoryConfiguration()).thenReturn(dirs);
        ModeActionSelector mas = new ModeActionSelector(Mode.PRACTICE, Action.QUALITY);
        when(ctx.getModeActionSelector()).thenReturn(mas);
        when(asciiArtGenerator.getRandomAsciiArt()).thenReturn("random ascii art");
        when(codeSnippetGenerator.generateCodeSnippetFromSolution(any(), anyInt())).thenReturn("arbitrary code snippet");
    }

    @BeforeEach
    void createWriter() {
        this.writer = buildWriter();
    }

    protected String generatedResult() {
        return readFile(new File(concatenateDirectories(reportDir.toString(), "stdout.txt")));
    }

    protected String generatedXml() {
        File xmlFile = new File(concatenateDirectories(reportDir.toString(), "results.xml"));
        if (!xmlFile.exists()) {
            // Try to find where it actually landed
            System.out.println("XML not found in reportDir. Contents of reportDir:");
            Arrays.stream(reportDir.toFile().listFiles()).forEach(f -> System.out.println(f.getAbsolutePath()));
            return "NOT FOUND";
        }
        return readFile(xmlFile);
    }

    /**
     * For an overview of the results. To be used in production only.
     */
    @ParameterizedTest
    @MethodSource("domainTestingTestSuites")
    void metaTestQuality(
            String libraryFile,
            String solutionFile,
            String configurationFile
    ) {
        Result result = runWithQuality(libraryFile, solutionFile, configurationFile);

        writer.write(ctx, result);

        System.out.println(generatedResult());       // stdout
        System.out.println(generatedXml());          // results.xml
    }

    static Stream<Arguments> domainTestingTestSuites() {
        return Stream.of(
                Arguments.of("NumberUtilsAddLibrary", "NumberUtilsAddOfficialSolution", "NumberUtilsAddConfiguration"),
                Arguments.of("IsEqualCollectionLibrary", "IsEqualCollectionOfficialSolution", "IsEqualCollectionConfiguration"),
                Arguments.of("AutoAssignStudentsLibrary", "AutoAssignStudentsOfficialSolution", "AutoAssignStudentsConfiguration")
        );
    }
}
