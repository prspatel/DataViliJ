
import dataprocessors.TSDProcessor;
import org.junit.Test;
import ui.AppUI;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import static org.junit.Assert.*;


public class Testing {

    @Test
    public void processString() throws Exception {
        TSDProcessor processor = new TSDProcessor();
        String test1 = "@a\ta\t2,2";
        processor.processString(test1);

    }
    @Test(expected = Exception.class)
    public void processString2() throws Exception {
        TSDProcessor processor = new TSDProcessor();
        String test1 = "@a\t2,2";
        processor.processString(test1);
    }
    @Test
    public void writeToFile() throws IOException {
        String text = "a\ta\t2,2\nb\tb\t2,2";
        FileWriter writer = new FileWriter("test.txt");
        writer.write(text);
        writer.close();
        String entireFileText = new Scanner(new File("test.txt"))
                .useDelimiter("\\A").next();
        assertEquals(text,entireFileText);
    }

    @Test (expected = NumberFormatException.class)
    public void testingCase3() throws NumberFormatException {
        String c ="x";
        AppUI.testingCase3(c,c,c);
    }
    @Test
    public void testingCase3q()   {
        String c ="3";
        AppUI.testingCase3(c,c,c);
    }
}