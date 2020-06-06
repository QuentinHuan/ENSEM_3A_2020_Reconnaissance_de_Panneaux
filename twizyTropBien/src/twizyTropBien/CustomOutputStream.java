package twizyTropBien;

import org.eclipse.swt.widgets.Label;
import java.io.IOException;
import java.io.OutputStream;


public class CustomOutputStream extends OutputStream {
    private Label textArea;

    public CustomOutputStream(Label console) {
        this.textArea = console;
    }

    @Override
    public void write(int b) throws IOException {
        // redirects data to the text area
        textArea.setText(textArea.getText()+String.valueOf((char)b));
        // scrolls the text area to the end of data
       // textArea.setCaretPosition(textArea.getText().length());
        // keeps the textArea up to date
       // textArea.update(textArea.getGraphics());
    }
}