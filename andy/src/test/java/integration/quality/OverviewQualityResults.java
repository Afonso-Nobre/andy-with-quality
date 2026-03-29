package integration.quality;

import integration.BaseMetaTestsTest;
import nl.tudelft.cse1110.andy.config.DirectoryConfiguration;
import nl.tudelft.cse1110.andy.execution.Context.Context;
import nl.tudelft.cse1110.andy.execution.mode.Action;
import nl.tudelft.cse1110.andy.execution.mode.Mode;
import nl.tudelft.cse1110.andy.execution.mode.ModeActionSelector;
import nl.tudelft.cse1110.andy.writer.ResultWriter;
import nl.tudelft.cse1110.andy.writer.standard.CodeSnippetGenerator;
import nl.tudelft.cse1110.andy.writer.standard.RandomAsciiArtGenerator;
import nl.tudelft.cse1110.andy.writer.standard.StandardResultWriter;
import nl.tudelft.cse1110.andy.writer.standard.VersionInformation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import nl.tudelft.cse1110.andy.result.Result;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.stream.Stream;

import static nl.tudelft.cse1110.andy.utils.FilesUtils.concatenateDirectories;
import static nl.tudelft.cse1110.andy.utils.FilesUtils.readFile;
import static org.mockito.Mockito.*;

/*
This is not a test class. It is a script to run some solutions and capture the output using the writer
 */
public class OverviewQualityResults extends BaseMetaTestsTest {
    protected Context ctx = mock(Context.class);
    protected VersionInformation versionInformation = new VersionInformation("testVersion", "testBuildTimestamp", "testCommitId");
    protected RandomAsciiArtGenerator asciiArtGenerator = mock(RandomAsciiArtGenerator.class);
    protected CodeSnippetGenerator codeSnippetGenerator = mock(CodeSnippetGenerator.class);
    protected ResultWriter writer;

    protected ResultWriter buildWriter() {
        return new StandardResultWriter(versionInformation, asciiArtGenerator, codeSnippetGenerator);
    }

    @TempDir
    protected Path reportDir;

    @BeforeEach
    void setupMocks() throws FileNotFoundException {
        DirectoryConfiguration dirs = new DirectoryConfiguration(null, reportDir.toString());
        when(ctx.getDirectoryConfiguration()).thenReturn(dirs);
        ModeActionSelector mas = new ModeActionSelector(Mode.PRACTICE, Action.FULL_WITH_HINTS);
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
        Result result = run(libraryFile, solutionFile, configurationFile);

        writer.write(ctx, result);

        String output = generatedResult();

        System.out.println(output);
    }

    static Stream<Arguments> domainTestingTestSuites() {
        return Stream.of(
                // Arguments.of("NumberUtilsAddLibrary", "NumberUtilsAddOfficialSolution", "NumberUtilsAddConfiguration")
                // ## Week 1
                // Arguments.of("ContainsAnyLibrary", "ContainsAnyOfficialSolution", "ContainsAnyConfiguration")
                // Arguments.of("LastIndexOfLibrary", "LastIndexOfOfficialSolution", "LastIndexOfConfiguration")
                // Arguments.of("IsSortedLibrary", "IsSortedOfficialSolution", "IsSortedConfiguration")
                // ## Week 2
                // Arguments.of("SwapCaseLibrary", "SwapCaseOfficialSolution", "SwapCaseConfiguration")
                // Arguments.of("RepeatLibrary", "RepeatOfficialSolution", "RepeatConfiguration")
                // Arguments.of("IntersectionLibrary", "IntersectionOfficialSolution", "IntersectionConfiguration")
                // Arguments.of("GetCheapestPriceLibrary", "GetCheapestPriceOfficialSolution", "GetCheapestPriceConfiguration")
                // Arguments.of("IsEqualCollectionLibrary", "IsEqualCollectionOfficialSolution", "IsEqualCollectionConfiguration")
                Arguments.of("AutoAssignStudentsLibrary", "AutoAssignStudentsOfficialSolution", "AutoAssignStudentsConfiguration")
                // ## Week 3
                // Arguments.of("CountingClumpsLibrary", "CountingClumpsOfficialSolution", "CountingClumpsConfiguration")
                // Arguments.of("ReplaceLibrary", "ReplaceOfficialSolution", "ReplaceConfiguration")
                // Arguments.of("ToCamelCaseLibrary", "ToCamelCaseOfficialSolution", "ToCamelCaseConfiguration")
                // ## Week 4
                // Arguments.of("BalancingArraysLibrary", "BalancingArraysOfficialSolution", "BalancingArraysConfiguration")
                // ## Week 5
                // Arguments.of("CodeSnippetGeneratorLibrary", "CodeSnippetGeneratorOfficialSolution", "CodeSnippetGeneratorConfiguration")
                // ## Week 6
                // Arguments.of("SubstringsBetweenLibrary", "SubstringsBetweenOfficialSolution", "SubstringsBetweenConfiguration")
                // ## Week 7
                // Arguments.of("ReverseLibrary", "ReverseOfficialSolution", "ReverseConfiguration")
                // ## Week 8
                // Arguments.of("ZigZagLibrary", "ZigZagOfficialSolution", "ZigZagConfiguration")
        );
    }
}
