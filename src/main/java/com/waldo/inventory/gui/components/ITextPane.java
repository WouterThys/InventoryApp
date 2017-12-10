package com.waldo.inventory.gui.components;

import javax.swing.*;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;
import java.io.*;

public class ITextPane extends JTextPane {


    public ITextPane() {
        super();
    }

    public void setFile(File file) {
        if (file != null && file.exists()) {
            readFile(file);
        } else {
            setText("");
        }
    }

    private void readFile(File file) {
        StyledDocument doc;
        try (InputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            doc = (DefaultStyledDocument) ois.readObject();
        }
        catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Input file was not found!");
            return;
        }
        catch (ClassNotFoundException | IOException ex) {
            throw new RuntimeException(ex);
        }

        setDocument(doc);
    }

}
