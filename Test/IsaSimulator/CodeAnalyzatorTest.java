package IsaSimulator;

import Exceptions.LexicalException;
import Exceptions.SemanticException;
import Exceptions.SyntaxException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CodeAnalyzatorTest
{
    private void analyzeCode(String path)throws Exception
    {
        CodeReader reader=new CodeReader(path);
        reader.readCode();
        CodeAnalyzator analyzator=new CodeAnalyzator(reader.getCodeLines());
        analyzator.analyzeCode();
    }

    @Test
    public void lexicalErrorTest()
    {
        assertThrows(LexicalException.class,()->{this.analyzeCode("Test\\lexicalErrorTestInput.txt");});
    }

    @Test
    public void syntaxErrorTest()
    {
        assertThrows(SyntaxException.class,()->{this.analyzeCode("Test\\syntaxErrorTestInput.txt");});
    }

    @Test
    public void semanticErrorTest()
    {
        assertThrows(SemanticException.class,()->{this.analyzeCode("Test\\semanticErrorTestInput.txt");});
    }
}