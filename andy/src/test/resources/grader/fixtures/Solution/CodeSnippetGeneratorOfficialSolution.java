package delft;

import java.util.*;
import java.util.stream.*;
import org.assertj.core.data.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class CodeSnippetUtilsTest {

    @Test
    void middleOfFile() {
        String actual = CodeSnippetUtils.generateCodeSnippet(List.of(
                "a",
                "b",
                "c",
                "d",
                "e",
                "f",
                "g",
                "h"
        ), 5);
        assertThat(actual).isEqualTo(
                "    c\n" +
                        "    d\n" +
                        "--> e\n" +
                        "    f\n" +
                        "    g"
        );
    }

    @Test
    void beginningOfFile() {
        String actual = CodeSnippetUtils.generateCodeSnippet(List.of(
                "a",
                "b",
                "c",
                "d",
                "e",
                "f",
                "g",
                "h"
        ), 2);
        assertThat(actual).isEqualTo(
                "    a\n" +
                        "--> b\n" +
                        "    c\n" +
                        "    d"
        );
    }

    @Test
    void firstLine() {
        String actual = CodeSnippetUtils.generateCodeSnippet(List.of(
                "a",
                "b",
                "c",
                "d",
                "e",
                "f",
                "g",
                "h"
        ), 1);
        assertThat(actual).isEqualTo(
                "--> a\n" +
                        "    b\n" +
                        "    c"
        );
    }

    @Test
    void endOfFile() {
        String actual = CodeSnippetUtils.generateCodeSnippet(List.of(
                "a",
                "b",
                "c",
                "d",
                "e",
                "f",
                "g",
                "h"
        ), 7);
        assertThat(actual).isEqualTo(
                "    e\n" +
                        "    f\n" +
                        "--> g\n" +
                        "    h"
        );
    }

    @Test
    void lastLine() {
        String actual = CodeSnippetUtils.generateCodeSnippet(List.of(
                "a",
                "b",
                "c",
                "d",
                "e",
                "f",
                "g",
                "h"
        ), 8);
        assertThat(actual).isEqualTo(
                "    f\n" +
                        "    g\n" +
                        "--> h"
        );
    }

    @Test
    void indentation() {
        String actual = CodeSnippetUtils.generateCodeSnippet(List.of(
                "       a",
                "      b",
                "      if(c) {",
                "          d",
                "      }",
                "   f",
                "g",
                "                h"
        ), 3);
        assertThat(actual).isEqualTo(
                "     a\n" +
                        "    b\n" +
                        "--> if(c) {\n" +
                        "        d\n" +
                        "    }"
        );
    }

    @Test
    void indentationMixedWithBlankLines() {
        String actual = CodeSnippetUtils.generateCodeSnippet(List.of(
                "       a",
                "",
                "      if(c) {",
                "  ",
                "          d",
                "      }",
                "   f",
                "g",
                "                h"
        ), 3);
        assertThat(actual).isEqualTo(
                "     a\n" +
                        "\n" +
                        "--> if(c) {\n" +
                        "\n" +
                        "        d"
        );
    }

    @Test
    void fileBoundaryWithIndentation() {
        String actual = CodeSnippetUtils.generateCodeSnippet(List.of(
                "  b",
                "  if(c) {",
                "      d",
                "  }",
                "f",
                "g"
        ), 2);
        assertThat(actual).isEqualTo(
                "    b\n" +
                        "--> if(c) {\n" +
                        "        d\n" +
                        "    }"
        );
    }

}
