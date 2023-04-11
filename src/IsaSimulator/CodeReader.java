package IsaSimulator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class CodeReader {
    private Path path;
    private List<String> codeLines;

    public CodeReader(String path) {
        this.path = Paths.get(path);

    }

    public void readCode() throws IOException {

        this.codeLines = Files.readAllLines(this.path);
    }

    public List<String> getCodeLines() {
        return this.codeLines;
    }


}
