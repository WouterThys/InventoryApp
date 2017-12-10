package com.waldo.inventory.gui.components;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.*;
import javax.swing.text.DefaultEditorKit.CopyAction;
import javax.swing.text.DefaultEditorKit.CutAction;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Random;

import static com.waldo.inventory.gui.Application.imageResource;

public class ITextEditor extends JPanel {

    private ITextPane editor;
    private UndoManager undoManager;
    private String pictureButtonName;

    enum BulletActionType {INSERT, REMOVE};
    enum NumbersActionType {INSERT, REMOVE};
    enum UndoActionType {UNDO, REDO};

    // This flag checks true if the caret position within a bulleted para
    // is at the first text position after the bullet (bullet char + space).
    // Also see EditorCaretListener and BulletParaKeyListener.
    private boolean startPosPlusBullet;

    // This flag checks true if the caret position within a numbered para
    // is at the first text position after the number (number + dot + space).
    // Also see EditorCaretListener and NumbersParaKeyListener.
    private boolean startPosPlusNum;


    private static final char BULLET_CHAR = '\u2022';
    private static final String BULLET_STR = new String(new char [] {BULLET_CHAR});
    private static final String BULLET_STR_WITH_SPACE = BULLET_STR + " ";
    private static final int BULLET_LENGTH = BULLET_STR_WITH_SPACE.length();
    private static final String NUMBERS_ATTR = "NUMBERS";
    private static final String ELEM = AbstractDocument.ElementNameAttribute;
    private static final String COMP = StyleConstants.ComponentElementName;

    private AbstractAction undoAction;
    private AbstractAction redoAction;
    private AbstractAction saveAction;

    private AbstractAction copyAction;
    private AbstractAction cutAction;
    private AbstractAction pasteAction;

    private AbstractAction boldAction;
    private AbstractAction italicAction;
    private AbstractAction underlineAction;

    private AbstractAction colorAction;
    private AbstractAction insertPictureAction;

    private AbstractAction bulletInsertButton;
    private AbstractAction numbersInsertButton;

    public ITextEditor() {
        editor = new ITextPane();
        JScrollPane editorScrollPane = new JScrollPane(editor);

        editor.setDocument(getNewDocument());
        editor.addKeyListener(new BulletParaKeyListener());
        editor.addKeyListener(new NumbersParaKeyListener());
        editor.addCaretListener(new EditorCaretListener());

        undoManager = new UndoManager();
        EditButtonActionListener editButtonActionListener = new EditButtonActionListener();

        undoAction = new AbstractAction("Undo", imageResource.readImage("TextEdit.Undo")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onUndo();
            }
        };
        redoAction = new AbstractAction("Redo", imageResource.readImage("TextEdit.Redo")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRedo();
            }
        };

        copyAction = new CopyAction();
        copyAction.putValue(AbstractAction.SMALL_ICON, imageResource.readImage("TextEdit.Copy"));
        pasteAction = new DefaultEditorKit.PasteAction();
        pasteAction.putValue(AbstractAction.SMALL_ICON, imageResource.readImage("TextEdit.Paste"));
        cutAction = new CutAction();
        cutAction.putValue(AbstractAction.SMALL_ICON, imageResource.readImage("TextEdit.Cut"));

        boldAction = new StyledEditorKit.BoldAction();
        boldAction.putValue(AbstractAction.SMALL_ICON, imageResource.readImage("TextEdit.Bold"));
        italicAction = new StyledEditorKit.ItalicAction();
        italicAction.putValue(AbstractAction.SMALL_ICON, imageResource.readImage("TextEdit.Italic"));
        underlineAction = new StyledEditorKit.UnderlineAction();
        underlineAction.putValue(AbstractAction.SMALL_ICON, imageResource.readImage("TextEdit.Underline"));

        colorAction = new AbstractAction("Color", imageResource.readImage("TextEdit.Color")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onColor();
            }
        };
        insertPictureAction = new AbstractAction("Picture", imageResource.readImage("TextEdit.Picture")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onInsertPicture();
            }
        };

        bulletInsertButton = new AbstractAction("Bullets", imageResource.readImage("TextEdit.ListBullets")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onBulletAction(BulletActionType.INSERT);
            }
        };

        numbersInsertButton = new AbstractAction("Numbers", imageResource.readImage("TextEdit.ListNumbers")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onInsertNumbers(NumbersActionType.INSERT);
            }
        };


        JToolBar undoRedoTb = getToolBar(JToolBar.HORIZONTAL);
        layoutButton(undoRedoTb.add(undoAction), "Undo");
        layoutButton(undoRedoTb.add(redoAction), "Redo");

        JToolBar copyPasteCutTb = getToolBar(JToolBar.HORIZONTAL);
        layoutButton(copyPasteCutTb.add(copyAction), "Copy");
        layoutButton(copyPasteCutTb.add(pasteAction), "Paste");
        layoutButton(copyPasteCutTb.add(cutAction), "Cut");

        JToolBar boldItalicUnderlineTb = getToolBar(JToolBar.HORIZONTAL);
        layoutButton(boldItalicUnderlineTb.add(boldAction), "Bold", editButtonActionListener);
        layoutButton(boldItalicUnderlineTb.add(italicAction), "Italic", editButtonActionListener);
        layoutButton(boldItalicUnderlineTb.add(underlineAction), "Underline", editButtonActionListener);

        JToolBar colorPictureTb = getToolBar(JToolBar.HORIZONTAL);
        layoutButton(colorPictureTb.add(colorAction), "Text color");
        layoutButton(colorPictureTb.add(insertPictureAction), "Insert image");

        JToolBar listTb = getToolBar(JToolBar.HORIZONTAL);
        layoutButton(listTb.add(bulletInsertButton), "Bullet points");
        layoutButton(listTb.add(numbersInsertButton), "Numbers");

        JToolBar toolBar = new JToolBar(JToolBar.HORIZONTAL);
        toolBar.setFloatable(false);
        toolBar.add(undoRedoTb);
        toolBar.addSeparator();
        toolBar.add(copyPasteCutTb);
        toolBar.addSeparator();
        toolBar.add(boldItalicUnderlineTb);
        toolBar.addSeparator();
        toolBar.add(listTb);
        toolBar.addSeparator();
        toolBar.add(colorPictureTb);
        toolBar.setBorder(BorderFactory.createEmptyBorder(2,10,2,10));

        setLayout(new BorderLayout());
        add(toolBar, BorderLayout.NORTH);
        add(editorScrollPane, BorderLayout.CENTER);

//        Dimension dim = getMinimumSize();
//        dim.height = 300;
//        setPreferredSize(dim);

        editor.requestFocusInWindow();
    }

    private JToolBar getToolBar(int dir) {
        JToolBar toolBar = new JToolBar(dir);
        toolBar.setFloatable(false);
        toolBar.setBorder(BorderFactory.createEmptyBorder(1,1,1,1));
        return toolBar;
    }

    private void layoutButton(JButton button, String text) {
        button.setToolTipText(text);
//        button.setText(text);
//        button.setVerticalTextPosition(SwingConstants.CENTER);
//        button.setHorizontalTextPosition(SwingConstants.LEFT);
    }

    private void layoutButton(JButton button, String text, ActionListener actionListener) {
        layoutButton(button, text);
        button.addActionListener(actionListener);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        copyAction.setEnabled(enabled);
        cutAction.setEnabled(enabled);
        pasteAction.setEnabled(enabled);
        boldAction.setEnabled(enabled);
        italicAction.setEnabled(enabled);
        underlineAction.setEnabled(enabled);
        colorAction.setEnabled(enabled);
        insertPictureAction.setEnabled(enabled);
        undoAction.setEnabled(enabled);
        redoAction.setEnabled(enabled);
        bulletInsertButton.setEnabled(enabled);
        numbersInsertButton.setEnabled(enabled);
        editor.setEnabled(enabled);
    }

    private StyledDocument getNewDocument() {
        StyledDocument doc = new DefaultStyledDocument();
        doc.addUndoableEditListener(new UndoEditListener());
        return doc;
    }

    private class EditButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            editor.requestFocusInWindow();
        }
    }

    private class UndoEditListener implements UndoableEditListener {
        @Override
        public void undoableEditHappened(UndoableEditEvent e) {
            undoManager.addEdit(e.getEdit()); // remember the edit
        }
    }

    private void onColor() {
        Color newColor = JColorChooser.showDialog(ITextEditor.this, "Choose a color", Color.BLACK);
        if (newColor == null) {
            editor.requestFocusInWindow();
            return;
        }

        SimpleAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setForeground(attr, newColor);
        editor.setCharacterAttributes(attr, false);
        editor.requestFocusInWindow();
    }

    private void onUndo() {
        if (! undoManager.canUndo()) {
            editor.requestFocusInWindow();
            return; // no edits to undo
        }
        undoManager.undo();
        editor.requestFocusInWindow();
    }

    private void onRedo() {
        if (!undoManager.canRedo()) {
            editor.requestFocusInWindow();
            return; // no edits to redo
        }
        undoManager.redo();
        editor.requestFocusInWindow();
    }

    private void onInsertPicture() {
        File pictureFile = choosePictureFile();

        if (pictureFile == null) {

            editor.requestFocusInWindow();
            return;
        }

        ImageIcon icon = new ImageIcon(pictureFile.toString());
        JButton picButton = new JButton(icon);
        picButton.setBorder(new LineBorder(Color.WHITE));
        picButton.setMargin(new Insets(0,0,0,0));
        picButton.setAlignmentY(.9f);
        picButton.setAlignmentX(.9f);
        picButton.addFocusListener(new PictureFocusListener());
        picButton.setName("PICTURE_ID_" + new Random().nextInt());
        editor.insertComponent(picButton);
        editor.requestFocusInWindow();
    }

    private File choosePictureFile() {

        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "PNG, JPG & GIF Images", "png", "jpg", "gif");
        chooser.setFileFilter(filter);

        if (chooser.showOpenDialog(ITextEditor.this) == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        else {
            return null;
        }
    }



    private class PictureFocusListener implements FocusListener {

        @Override
        public void focusGained(FocusEvent e) {

            JButton button = (JButton) e.getComponent();
            button.setBorder(new LineBorder(Color.GRAY));
            pictureButtonName = button.getName();
        }

        @Override
        public void focusLost(FocusEvent e) {
            ((JButton) e.getComponent()).setBorder(new LineBorder(Color.WHITE));
        }
    }

    private class PictureDeleteActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            StyledDocument doc = getEditorDocument();
            ElementIterator iterator = new ElementIterator(doc);
            Element element;

            while ((element = iterator.next()) != null) {

                AttributeSet attrs = element.getAttributes();

                if (attrs.containsAttribute(ELEM, COMP)) {

                    JButton button = (JButton) StyleConstants.getComponent(attrs);

                    if (button.getName().equals(pictureButtonName)) {

                        try {
                            doc.remove(element.getStartOffset(), 1); // length = 1
                        }
                        catch (BadLocationException ex_) {

                            throw new RuntimeException(ex_);
                        }
                    }
                }
            }

            editor.requestFocusInWindow();
            pictureButtonName = null;
        }
    } // PictureDeleteActionListener

    private StyledDocument getEditorDocument() {
        return (DefaultStyledDocument) editor.getDocument();
    }


    private void onBulletAction(BulletActionType bulletActionType) {
        String selectedText = editor.getSelectedText();

        if ((selectedText == null) || (selectedText.trim().isEmpty())) {

            editor.requestFocusInWindow();
            return;
        }

        StyledDocument doc = getEditorDocument();
        Element paraEle = doc.getParagraphElement(editor.getSelectionStart());
        int paraEleStart = paraEle.getStartOffset();
        int paraEleEnd = 0;

        BULLETS_PARA_LOOP:
        do {
            paraEle = doc.getParagraphElement(paraEleStart);
            paraEleEnd = paraEle.getEndOffset();

            if ((paraEleEnd - paraEleStart) <= 1) { // empty line/para

                paraEleStart = paraEleEnd;
                paraEle = doc.getParagraphElement(paraEleStart);
                continue BULLETS_PARA_LOOP;
            }

            switch (bulletActionType) {

                case INSERT:
                    if ((! isBulletedPara(paraEleStart)) &&
                            (! isNumberedPara(paraEleStart))) {

                        insertBullet(paraEleStart, paraEleStart);
                    }

                    break; // switch

                case REMOVE:
                    if (isBulletedPara(paraEleStart)) {

                        removeBullet(paraEleStart, BULLET_LENGTH);
                    }
            }

            // Get the updated para element details after bulleting
            paraEle = doc.getParagraphElement(paraEleStart);
            paraEleEnd = paraEle.getEndOffset();

            paraEleStart = paraEleEnd;

        } while (paraEleEnd <= editor.getSelectionEnd());
        // BULLETS_PARA_LOOP

        editor.requestFocusInWindow();
    }

    /*
     * Action listener class for bullet insert and remove button actions.
     */
    private class BulletActionListener implements ActionListener {

        private BulletActionType bulletActionType;

        public BulletActionListener(BulletActionType actionType) {

            bulletActionType = actionType;
        }

        /*
         * Common routine for insert and remove bullet actions. This routine
         * loops thru the selected text and inserts or removes a bullet.
         * - For insert action: inserts a bullet at the beginning of each para
         * of selected text. The paras already bulleted or numbered are ignored.
         * - For remove bullet action: removes the bullet in case a para is
         * bulleted for the selected text.
         */
        @Override
        public void actionPerformed(ActionEvent e) {

            String selectedText = editor.getSelectedText();

            if ((selectedText == null) || (selectedText.trim().isEmpty())) {

                editor.requestFocusInWindow();
                return;
            }

            StyledDocument doc = getEditorDocument();
            Element paraEle = doc.getParagraphElement(editor.getSelectionStart());
            int paraEleStart = paraEle.getStartOffset();
            int paraEleEnd = 0;

            BULLETS_PARA_LOOP:
            do {
                paraEle = doc.getParagraphElement(paraEleStart);
                paraEleEnd = paraEle.getEndOffset();

                if ((paraEleEnd - paraEleStart) <= 1) { // empty line/para

                    paraEleStart = paraEleEnd;
                    paraEle = doc.getParagraphElement(paraEleStart);
                    continue BULLETS_PARA_LOOP;
                }

                switch (bulletActionType) {

                    case INSERT:
                        if ((! isBulletedPara(paraEleStart)) &&
                                (! isNumberedPara(paraEleStart))) {

                            insertBullet(paraEleStart, paraEleStart);
                        }

                        break; // switch

                    case REMOVE:
                        if (isBulletedPara(paraEleStart)) {

                            removeBullet(paraEleStart, BULLET_LENGTH);
                        }
                }

                // Get the updated para element details after bulleting
                paraEle = doc.getParagraphElement(paraEleStart);
                paraEleEnd = paraEle.getEndOffset();

                paraEleStart = paraEleEnd;

            } while (paraEleEnd <= editor.getSelectionEnd());
            // BULLETS_PARA_LOOP

            editor.requestFocusInWindow();
        }
    }

    private boolean isBulletedPara(int paraEleStart) {

        if (getParaFirstCharacter(paraEleStart) == BULLET_CHAR) {

            return true;
        }

        return false;
    }

    private char getParaFirstCharacter(int paraEleStart) {

        String firstChar = "";

        try {
            firstChar = editor.getText(paraEleStart, 1);
        }
        catch (BadLocationException ex) {

            throw new RuntimeException(ex);
        }

        return firstChar.charAt(0);
    }

    private boolean isNumberedPara(int paraEleStart) {

        AttributeSet attrSet = getParaStartAttributes(paraEleStart);
        Integer paraNum = (Integer) attrSet.getAttribute(NUMBERS_ATTR);

        if ((paraNum == null) || (! isFirstCharNumber(paraEleStart))) {

            return false;
        }

        return true;
    }

    private boolean isFirstCharNumber(int paraEleStart) {

        if (Character.isDigit(getParaFirstCharacter(paraEleStart))) {

            return true;
        }

        return false;
    }

    /*
     * The insert bullet routine; inserts the bullet in the editor document. This
     * routine is used from the insert action (ActionListener) as well as bullet
     * para key press actions (keyPressed or keyReleased methods of KeyListener).
     *
     * The parameter insertPos is the position at which the bullet is to be
     * inserted. The parameter attributesPos is the position from which the bullet
     * is to get its attributes (like color, font, size, etc.). The two parameter
     * values are derived differently for bullet insert and bullet para Enter
     * key press actions.
     *
     * Bullet insert action: the insertPos and attributesPos is the same,
     * the paraEleStart.
     * Enter key press: the insertPos is the current caret position of keyReleased(),
     * and the attributesPos is the previous paraEleStart position from
     * keyPressed() method.
     */
    private void insertBullet(int insertPos, int attributesPos) {

        try {
            getEditorDocument().insertString(insertPos,
                    BULLET_STR_WITH_SPACE,
                    getParaStartAttributes(attributesPos));
        }
        catch(BadLocationException ex) {

            throw new RuntimeException(ex);
        }
    }

    private AttributeSet getParaStartAttributes(int pos) {

        StyledDocument doc = (DefaultStyledDocument) editor.getDocument();
        Element	charEle = doc.getCharacterElement(pos);
        return charEle.getAttributes();
    }

    /*
     * The remove bullet routine; removes the bullet in the editor document. This
     * routine is used from the delete action (ActionListener) as well as bullet
     * para key press actions (keyPressed or keyRemoved methods of KeyListener).
     * The keys include the Enter, Backspace, Delete keys.
     *
     * The parameter removePos is the start position and the length is the length
     * of text to be removed. Length of characters removed is: BULLET_LENGTH
     * or +1 (includes carriage return folowing the BULLET_LENGTH). The two
     * parameter values are derived differently for bullet remove and bullet
     * para key press actions.
     *
     * Bullet remove action: removePos is paraEleStart and the BULLET_LENGTH.
     * Delete key press: removePos is current caret pos of keyPressed() and
     * the BULLET_LENGTH.
     * Backspace key press: removePos is paraEleStart of keyPressed() and
     * the length is BULLET_LENGTH.
     * Enter key press: removePos is previous paraEleStart of keyPressed() and
     * the length is BULLET_LENGTH + 1 (+1 includes CR).
     */
    private void removeBullet(int removePos, int length) {

        try {
            getEditorDocument().remove(removePos, length);
        }
        catch(BadLocationException ex) {

            throw new RuntimeException(ex);
        }
    }

    /*
     * Key listener class for key press and release actions within a bulleted
     * para. The keys include Enter, Backspace, Delete and Left. The Enter press
     * is implemented with both the keyPressed and keyReleased methods. The Delete,
     * Backspace and Left key press is implemented within the keyPressed.
     */
    public class BulletParaKeyListener implements KeyListener {

        // These two variables are derived in the keyPressed and are used in
        // keyReleased method.
        private String prevParaText_;
        private int prevParaEleStart_;

        // Identifies if a key is pressed in a bulleted para.
        // This is required to distinguish from the numbered para.
        private boolean bulletedPara_;


        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {

            bulletedPara_ = false;
            int pos = editor.getCaretPosition();

            if (! isBulletedParaForPos(pos)) {

                return;
            }

            Element paraEle = getEditorDocument().getParagraphElement(pos);
            int paraEleStart = paraEle.getStartOffset();

            switch (e.getKeyCode()) {

                case KeyEvent.VK_LEFT: // same as that of VK_KP_LEFT
                case KeyEvent.VK_KP_LEFT: int newPos = pos - (BULLET_LENGTH + 1);
                    doLeftArrowKeyRoutine(newPos, startPosPlusBullet);
                    break;
                case KeyEvent.VK_DELETE: doDeleteKeyRoutine(paraEle, pos);
                    break;
                case KeyEvent.VK_BACK_SPACE: doBackspaceKeyRoutine(paraEle);
                    break;
                case KeyEvent.VK_ENTER: getPrevParaDetails(pos);
            }

        } // keyPressed()

        private boolean isBulletedParaForPos(int caretPos) {

            Element paraEle = getEditorDocument().getParagraphElement(caretPos);

            if (isBulletedPara(paraEle.getStartOffset())) {

                return true;
            }

            return false;
        }

        // This method is used with Enter key press routine.
        // Two instance variable values are derived here and are used
        // in the keyReleased() method: prevParaEleStart_ and prevParaText_
        private void getPrevParaDetails(int pos) {

            pos =  pos - 1;

            if (isBulletedParaForPos(pos)) {

                bulletedPara_ = true;
                Element paraEle = getEditorDocument().getParagraphElement(pos);
                prevParaEleStart_ = paraEle.getStartOffset();
                prevParaText_ =
                        getPrevParaText(prevParaEleStart_, paraEle.getEndOffset());
            }
        }

        // Delete key press routine within bulleted para.
        private void doDeleteKeyRoutine(Element paraEle, int pos) {

            int paraEleEnd = paraEle.getEndOffset();

            if (paraEleEnd > getEditorDocument().getLength()) {

                return; // no next para, end of document text
            }

            if (pos == (paraEleEnd - 1)) { // last char of para; -1 is for CR

                if (isBulletedParaForPos(paraEleEnd + 1)) {

                    // following para is bulleted, remove
                    removeBullet(pos, BULLET_LENGTH);
                }
                // else, not a bulleted para
                // delete happens normally (one char)
            }
        }

        // Backspace key press routine within a bulleted para.
        // Also, see EditorCaretListener.
        private void doBackspaceKeyRoutine(Element paraEle) {

            // In case the position of cursor at the backspace is just
            // before the bullet (that is BULLET_LENGTH).
            if (startPosPlusBullet) {

                removeBullet(paraEle.getStartOffset(), BULLET_LENGTH);
                startPosPlusBullet = false;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {

            if (! bulletedPara_) {

                return;
            }

            switch (e.getKeyCode()) {

                case KeyEvent.VK_ENTER: doEnterKeyRoutine();
                    break;
            }
        }

        // Enter key press routine within a bulleted para.
        // Also, see keyPressed().
        private void doEnterKeyRoutine() {

            String prevParaText = prevParaText_;
            int prevParaEleStart = prevParaEleStart_;

            // Check if prev para with bullet has text
            if (prevParaText.length() < 4) {

                // Para has bullet and no text, remove bullet+CR from para
                removeBullet(prevParaEleStart, (BULLET_LENGTH + 1));
                editor.setCaretPosition(prevParaEleStart);
                return;
            }
            // Prev para with bullet and text

            // Insert bullet for next para (current position), and
            // prev para attributes are used for this bullet.
            insertBullet(editor.getCaretPosition(), prevParaEleStart);
        }

    } // BulletParaKeyListener

    private String getPrevParaText(int prevParaEleStart, int prevParaEleEnd) {

        String prevParaText = "";

        try {
            prevParaText = getEditorDocument().getText(prevParaEleStart,
                    (prevParaEleEnd -  prevParaEleStart));
        }
        catch(BadLocationException ex) {

            throw new RuntimeException(ex);
        }

        return prevParaText;
    }

    /*
     * Left arrow key press routine within a bulleted and numbered paras.
     * Moves the cursor when caret is at position startPosPlusBullet or at
     * startPosPlusNum for bullets or numbers respectively.
     * Also see EditorCaretListener.
     *
     * The parameter startTextPos indicates if startPosPlusBullet or
     * startPosPlusNum. pos is the present caret postion.
     */
    private void doLeftArrowKeyRoutine(int pos, boolean startTextPos) {

        if (! startTextPos) {

            return;
        }

        // Check if this is start of document
        Element paraEle =
                getEditorDocument().getParagraphElement(editor.getCaretPosition());
        int newPos = (paraEle.getStartOffset() == 0) ? 0 : pos;

        // Position the caret in an EDT, otherwise the caret is
        // positioned at one less position than intended.
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {

                editor.setCaretPosition(newPos);
            }
        });
    }

    /*
     * This listener is used with the bulleted and numbered para actions.
     * The bulleted item's bullet is made of bullet + space. The cursor (caret)
     * is not allowed to position at the bullet para's first position and at
     * the space after the bullet. This listener controls the cursor position
     * in such cases; the cursor jumps/moves to the after bullet+space position
     * (indicated by startPosPlusBullet boolean instance variable).
     *
     * Also, the backspace and left-arrow key usage requires startPosPlusBullet
     * to perform the routines (see doLeftArrowKeyRoutine() and BulletParaKeyListener).
     *
     * This is also similar for numbered paras (see startPosPlusNum and
     * NumbersParaKeyListener).
     */
    private class EditorCaretListener implements CaretListener {

        @Override
        public void caretUpdate(CaretEvent e) {

            startPosPlusBullet = false;
            startPosPlusNum = false;
            Element paraEle =
                    getEditorDocument().getParagraphElement(editor.getCaretPosition());
            int paraEleStart = paraEle.getStartOffset();

            if (isBulletedPara(paraEleStart)) {

                if (e.getDot() == (paraEleStart + BULLET_LENGTH)) {

                    startPosPlusBullet = true;
                }
                else if (e.getDot() < (paraEleStart + BULLET_LENGTH)) {

                    editor.setCaretPosition(paraEleStart + BULLET_LENGTH);
                }
                else {
                    // continue
                }
            }
            else if (isNumberedPara(paraEleStart)) {

                int numLen = getNumberLength(paraEleStart);

                if (e.getDot() < (paraEleStart + numLen)) {

                    editor.setCaretPosition(paraEleStart + numLen);
                }
                else if (e.getDot() == (paraEleStart + numLen)) {

                    startPosPlusNum = true;
                }
                else {
                    // continue
                }
            }
            else {
                // not a bulleted or numbered para
            }
        }
    }

    /*
     * Returns the numbered para's number length. This length includes
     * the number + dot + space. For example, the text "12. A Numbered para..."
     * has the number length of 4.
     */
    private int getNumberLength(int paraEleStart) {

        Integer num = getParaNumber(paraEleStart);
        int len = num.toString().length() + 2; // 2 = dot + space after number
        return len;
    }

    private Integer getParaNumber(int paraEleStart) {

        AttributeSet attrSet = getParaStartAttributes(paraEleStart);
        Integer paraNum = (Integer) attrSet.getAttribute(NUMBERS_ATTR);
        return paraNum;
    }

    private int n;
    private void onInsertNumbers(NumbersActionType numbersActionType) {
        StyledDocument doc = getEditorDocument();
        String selectedText = editor.getSelectedText();

        if ((selectedText == null) || (selectedText.trim().isEmpty())) {

            editor.requestFocusInWindow();
            return;
        }

        Element paraEle = doc.getParagraphElement(editor.getSelectionStart());
        int paraEleStart = paraEle.getStartOffset();
        int paraEleEnd = 0;
        boolean firstPara = true;

        NUMBERS_PARA_LOOP:
        do {
            paraEle = doc.getParagraphElement(paraEleStart);
            paraEleEnd = paraEle.getEndOffset();

            if ((paraEleEnd - paraEleStart) <= 1) { // empty line

                if (firstPara) {

                    firstPara = false;
                    n = 0;
                }

                paraEleStart = paraEleEnd;
                paraEle = doc.getParagraphElement(paraEleStart);
                continue NUMBERS_PARA_LOOP;
            }

            switch (numbersActionType) {

                case INSERT:

                    if (isBulletedPara(paraEleStart)) {

                        break; // switch
                    }

                    if (firstPara) {

                        firstPara = false;
                        n = 0;
                    }

                    if (isNumberedPara(paraEleStart)) {

                        // remove any existing number
                        removeNumber(paraEleStart, getNumberLength(paraEleStart));
                    }

                    if (! isNumberedPara(paraEleStart)) {

                        Integer nextN = new Integer(++n);
                        insertNumber(paraEleStart, paraEleStart, nextN);
                    }

                    break; // switch

                case REMOVE:

                    if (isNumberedPara(paraEleStart)) {

                        removeNumber(paraEleStart, getNumberLength(paraEleStart));
                    }
            }

            // Get the updated para element details after numbering
            paraEle = doc.getParagraphElement(paraEleStart);
            paraEleEnd = paraEle.getEndOffset();

            paraEleStart = paraEleEnd;

        } while (paraEleEnd <= editor.getSelectionEnd());
        // NUMBERS_PARA_LOOP

        editor.requestFocusInWindow();
    }

    /*
     * Action listener class for number insert and remove button actions.
     */
    private class NumbersActionListener implements ActionListener {

        private NumbersActionType numbersActionType;
        private int n;

        public NumbersActionListener(NumbersActionType actionType) {

            numbersActionType = actionType;
        }

        /*
         * Common routine for insert and remove numbers actions. This routine
         * loops thru the selected text and inserts or removes a number.
         * - For insert action: inserts a number at the beginning of each para
         * of selected text. The paras already bulleted or numbered are ignored.
         *  Note that the numbering always starts from 1.
         * - For remove action: removes the number in case a para is numbered
         * for the selected text.
         */
        @Override
        public void actionPerformed(ActionEvent e) {

            StyledDocument doc = getEditorDocument();
            String selectedText = editor.getSelectedText();

            if ((selectedText == null) || (selectedText.trim().isEmpty())) {

                editor.requestFocusInWindow();
                return;
            }

            Element paraEle = doc.getParagraphElement(editor.getSelectionStart());
            int paraEleStart = paraEle.getStartOffset();
            int paraEleEnd = 0;
            boolean firstPara = true;

            NUMBERS_PARA_LOOP:
            do {
                paraEle = doc.getParagraphElement(paraEleStart);
                paraEleEnd = paraEle.getEndOffset();

                if ((paraEleEnd - paraEleStart) <= 1) { // empty line

                    if (firstPara) {

                        firstPara = false;
                        n = 0;
                    }

                    paraEleStart = paraEleEnd;
                    paraEle = doc.getParagraphElement(paraEleStart);
                    continue NUMBERS_PARA_LOOP;
                }

                switch (numbersActionType) {

                    case INSERT:

                        if (isBulletedPara(paraEleStart)) {

                            break; // switch
                        }

                        if (firstPara) {

                            firstPara = false;
                            n = 0;
                        }

                        if (isNumberedPara(paraEleStart)) {

                            // remove any existing number
                            removeNumber(paraEleStart, getNumberLength(paraEleStart));
                        }

                        if (! isNumberedPara(paraEleStart)) {

                            Integer nextN = new Integer(++n);
                            insertNumber(paraEleStart, paraEleStart, nextN);
                        }

                        break; // switch

                    case REMOVE:

                        if (isNumberedPara(paraEleStart)) {

                            removeNumber(paraEleStart, getNumberLength(paraEleStart));
                        }
                }

                // Get the updated para element details after numbering
                paraEle = doc.getParagraphElement(paraEleStart);
                paraEleEnd = paraEle.getEndOffset();

                paraEleStart = paraEleEnd;

            } while (paraEleEnd <= editor.getSelectionEnd());
            // NUMBERS_PARA_LOOP

            editor.requestFocusInWindow();
        }
    }

    /*
     * The insert number routine; inserts the number in the editor document. This
     * routine is used from the insert action (ActionListener) as well as number
     * para key press actions (keyPressed or keyReleased methods of KeyListener).
     *
     * The parameter insertPos is the position at which the number is to be
     * inserted. The parameter attributesPos is the position from which the number
     * is to get its attributes (like color, font, size, etc.). The two parameter
     * values are derived differently for the insert and the number para key press
     * actions. The patameter num is the number being inserted.
     *
     * Number insert action: the insertPos and attributesPos is the same,
     * the paraEleStart.
     * Enter key press: the insertPos is the current caret position of keyReleased(),
     * and the attributesPos is the previous paraEleStart position from
     * keyPressed() method.
     */
    private void insertNumber(int insertPos, int attributesPos, Integer num) {

        try {
            getEditorDocument().insertString(insertPos,
                    getNumberString(num),
                    getNumbersAttributes(attributesPos, num));
        }
        catch(BadLocationException ex) {

            throw new RuntimeException(ex);
        }
    }

    private String getNumberString(Integer nextNumber) {

        return new String(nextNumber.toString() + "." + " ");
    }

    private AttributeSet getNumbersAttributes(int paraEleStart, Integer number) {

        AttributeSet attrs1 = getParaStartAttributes(paraEleStart);
        SimpleAttributeSet attrs2 = new SimpleAttributeSet(attrs1);
        attrs2.addAttribute(NUMBERS_ATTR, number);
        return attrs2;
    }

    /*
     * The remove number routine; removes the number in the editor document. This
     * routine is used from the delete action (ActionListener) as well as the number
     * para key press actions (keyPressed or keyRemoved methods of KeyListener).
     * The keys include the Enter, Backspace, Delete keys.
     *
     * The parameter removePos is the start position and the length is the length
     * of text to be removed. Length of characters removed is derived from the
     * method getNumberLength() or +1 (includes carriage return folowing the
     * number length). The two parameter values are derived differently for
     * number remove action and number para key press actions.
     *
     * Number remove action: removePos is paraEleStart and the length from
     * the method getNumberLength().
     * Delete key press: removePos is current caret pos of keyPressed() and
     * the length from the method getNumberLength().
     * Backspace key press: removePos is paraEleStart of keyPressed() and
     * the length from the method getNumberLength().
     * Enter key press: removePos is previous paraEleStart of keyPressed() and
     * the length from the method getNumberLength() + 1 (+1 includes CR).
     */
    private void removeNumber(int removePos, int length) {

        try {
            getEditorDocument().remove(removePos, length);
        }
        catch(BadLocationException ex) {

            throw new RuntimeException(ex);
        }
    }

    /*
     * Key listener class for key press and release actions within a numbered
     * para. The keys include Enter, Backspace, Delete and Left. The Enter press
     * is implemented with both the keyPressed and keyReleased methods. The Delete,
     * Backspace and Left key press is implemented within the keyPressed.
     *
     * This also includes key press actions (backspace, enter and delete) for
     * the text selected within the numbered paras.
     */
    public class NumbersParaKeyListener implements KeyListener {

        // These two variables are derived in the keyPressed and are used in
        // keyReleased method.
        private String prevParaText_;
        private int prevParaEleStart_;

        // Identifies if a key is pressed in a numbered para.
        // This is required to distinguish from the bulleted para.
        private boolean numberedPara_;


        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {

            String selectedText = editor.getSelectedText();

            if ((selectedText == null) || (selectedText.trim().isEmpty())) {

                // continue, processing key press without any selected text
            }
            else {
                // text is selected within numbered para and a key is pressed
                doReplaceSelectionRoutine();
                return;
            }

            numberedPara_ = false;
            int pos = editor.getCaretPosition();

            if (! isNumberedParaForPos(pos)) {

                return;
            }

            Element paraEle = getEditorDocument().getParagraphElement(pos);
            int paraEleStart = paraEle.getStartOffset();

            switch (e.getKeyCode()) {

                case KeyEvent.VK_LEFT: // same as that of VK_KP_LEFT
                case KeyEvent.VK_KP_LEFT: int newPos = pos -
                        (getNumberLength(paraEleStart) + 1);
                    doLeftArrowKeyRoutine(newPos, startPosPlusNum);
                    break;
                case KeyEvent.VK_DELETE: doDeleteKeyRoutine(paraEle, pos);
                    break;
                case KeyEvent.VK_BACK_SPACE: doBackspaceKeyRoutine(paraEle);
                    break;
                case KeyEvent.VK_ENTER: getPrevParaDetails(pos);
                    break;
            }

        } // keyPressed()

        private boolean isNumberedParaForPos(int caretPos) {

            Element paraEle = getEditorDocument().getParagraphElement(caretPos);

            if (isNumberedPara(paraEle.getStartOffset())) {

                return true;
            }

            return false;
        }

        /*
         * Routine for processing selected text with numbered paras
         * after pressing Enter, Backspace or Delete keys, and the
         * paste insert replacing the selected text.
         */
        private void doReplaceSelectionRoutine() {

            // Get selection start and end para details.
            // Check if there are numbered paras at top and bottom
            // of the selection. Re-number if needed i.e., when selection
            // is replaced in the middle of numbered paras or at the top
            // items of the numbered paras.

            StyledDocument doc = getEditorDocument();
            Element topParaEle = doc.getParagraphElement(editor.getSelectionStart());
            Element bottomParaEle = doc.getParagraphElement(editor.getSelectionEnd());

            int bottomParaEleStart = bottomParaEle.getStartOffset();
            int bottomParaEleEnd = bottomParaEle.getEndOffset();

            // No numbered text at bottom, no processing required -or-
            // no next para after selection end (end of document text).
            if ((! isNumberedPara(bottomParaEleStart)) ||
                    (bottomParaEleEnd > doc.getLength())) {

                return;
            }

            // Check if para following the selection end is numbered or not.
            Element paraEle = doc.getParagraphElement(bottomParaEleEnd + 1);
            int paraEleStart = paraEle.getStartOffset();

            if (! isNumberedPara(paraEleStart)) {

                return;
            }

            // Process re-numbering

            Integer numTop = getParaNumber(topParaEle.getStartOffset());

            if (numTop != null) {

                // There are numbered items above the removed para, and
                // there are numbered items following the removed para;
                // bottom numbers start from numTop + 1.
                doNewNumbers(paraEleStart, numTop);
            }
            else {
                // numTop == null
                // There are no numbered items above the removed para, and
                // there are numbered items following the removed para;
                // bottom numbers start from 1.
                doNewNumbers(paraEleStart, 0);
            }

        } // doReplaceSelectionRoutine()

        /*
         * Common routine to arrive at new numbers and replace the previous
         * ones after the following actions within numbered para list:
         * - Enter, Delete, Backspace key press.
         * - Delete, Backspace and paste-insert selected text.
         */
        private void doNewNumbers(int nextParaEleStart, Integer newNum) {

            StyledDocument doc = getEditorDocument();
            Element nextParaEle = doc.getParagraphElement(nextParaEleStart);
            boolean nextParaIsNumbered = true;

            NUMBERED_PARA_LOOP:
            while (nextParaIsNumbered) {

                Integer oldNum = getParaNumber(nextParaEleStart);
                newNum++;
                replaceNumbers(nextParaEleStart, oldNum, newNum);

                nextParaIsNumbered = false;

                // Get following para details after number is replaced for a para

                int nextParaEleEnd = nextParaEle.getEndOffset();
                int nextParaPos = nextParaEleEnd + 1;

                if (nextParaPos > doc.getLength()) {

                    break NUMBERED_PARA_LOOP; // no next para, end of document text
                }

                nextParaEle = doc.getParagraphElement(nextParaPos);
                nextParaEleStart = nextParaEle.getStartOffset();
                nextParaIsNumbered = isNumberedPara(nextParaEleStart);
            }
            // NUMBERED_PARA_LOOP

        } // doNewNumbers()

        private void replaceNumbers(int nextParaEleStart, Integer prevNum,
                                    Integer newNum) {

            try {
                ((DefaultStyledDocument) getEditorDocument()).replace(
                        nextParaEleStart,
                        getNumberString(prevNum).length(),
                        getNumberString(newNum),
                        getNumbersAttributes(nextParaEleStart, newNum));
            }
            catch(BadLocationException ex) {

                throw new RuntimeException(ex);
            }
        }

        // Delete key press routine within a numbered para.
        private void doDeleteKeyRoutine(Element paraEle, int pos) {

            int paraEleEnd = paraEle.getEndOffset();

            if (paraEleEnd > getEditorDocument().getLength()) {

                return; // no next para, end of document text
            }

            if (pos == (paraEleEnd - 1)) { // last char of para; -1 is for CR

                Element nextParaEle =
                        getEditorDocument().getParagraphElement(paraEleEnd + 1);
                int nextParaEleStart = nextParaEle.getStartOffset();

                if (isNumberedPara(nextParaEleStart)) {

                    removeNumber(pos, getNumberLength(nextParaEleStart));
                    doReNumberingForDeleteKey(paraEleEnd + 1);
                }
                // else, not a numbered para
                // delete happens normally (one char)
            }
        }

        private void doReNumberingForDeleteKey(int delParaPos) {

            // Get para element details where delete key is pressed
            StyledDocument doc = getEditorDocument();
            Element paraEle = doc.getParagraphElement(delParaPos);
            int paraEleStart = paraEle.getStartOffset();
            int paraEleEnd = paraEle.getEndOffset();

            // Get bottom para element details
            Element bottomParaEle = doc.getParagraphElement(paraEleEnd + 1);
            int bottomParaEleStart = bottomParaEle .getStartOffset();

            // In case bottom para is not numbered or end of document,
            // no re-numbering is required.
            if ((paraEleEnd > doc.getLength()) ||
                    (! isNumberedPara(bottomParaEleStart))) {

                return;
            }

            Integer n = getParaNumber(paraEleStart);
            doNewNumbers(bottomParaEleStart, n);
        }

        // Backspace key press routine within a numbered para.
        // Also, see EditorCaretListener.
        private void doBackspaceKeyRoutine(Element paraEle) {

            // In case the position of cursor at the backspace is just after
            // the number: remove the number and re-number the following ones.
            if (startPosPlusNum) {

                int startOffset = paraEle.getStartOffset();
                removeNumber(startOffset, getNumberLength(startOffset));
                doReNumberingForBackspaceKey(paraEle, startOffset);
                startPosPlusNum = false;
            }
        }

        private void doReNumberingForBackspaceKey(Element paraEle, int paraEleStart) {

            // Get bottom para element and check if numbered.
            StyledDocument doc = getEditorDocument();
            Element bottomParaEle = doc.getParagraphElement(paraEle.getEndOffset() + 1);
            int bottomParaEleStart = bottomParaEle.getStartOffset();

            if (! isNumberedPara(bottomParaEleStart)) {

                return; // there are no numbers following this para, and
                // no re-numbering required.
            }

            // Get top para element and number

            Integer numTop = null;

            if (paraEleStart == 0) {

                // beginning of document, no top para exists
                // before the document start; numTop = null
            }
            else {
                Element topParaEle = doc.getParagraphElement(paraEleStart - 1);
                numTop = getParaNumber(topParaEle.getStartOffset());
            }

            if (numTop == null) {

                // There are no numbered items above the removed para, and
                // there are numbered items following the removed para;
                // bottom numbers start from 1.
                doNewNumbers(bottomParaEleStart, 0);
            }
            else {
                // numTop != null
                // There are numbered items above the removed para, and
                // there are numbered items following the removed para;
                // bottom numbers start from numTop + 1.
                doNewNumbers(bottomParaEleStart, numTop);
            }
        }

        // This method is used with Enter key press routine.
        // Two instance variable values are derived here and are used
        // in the keyReleased() method: prevParaEleStart_ and prevParaText_
        private void getPrevParaDetails(int pos) {

            pos =  pos - 1;

            if (isNumberedParaForPos(pos)) {

                numberedPara_ = true;
                Element paraEle = getEditorDocument().getParagraphElement(pos);
                prevParaEleStart_ = paraEle.getStartOffset();
                prevParaText_ =
                        getPrevParaText(prevParaEleStart_, paraEle.getEndOffset());
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {

            if (! numberedPara_) {

                return;
            }

            switch (e.getKeyCode()) {

                case KeyEvent.VK_ENTER: doEnterKeyRoutine();
                    break;
            }
        }

        // Enter key press routine within a numbered para.
        // Also, see keyPressed().
        private void doEnterKeyRoutine() {

            String prevParaText = prevParaText_;
            int prevParaEleStart = prevParaEleStart_;
            int len = getNumberLength(prevParaEleStart) + 1; // +1 for CR

            // Check if prev para with numbers has text
            if (prevParaText.length() == len) {

                // Para has numbers and no text, remove number from para
                removeNumber(prevParaEleStart, len);
                editor.setCaretPosition(prevParaEleStart);
                return;
            }
            // Prev para with number and text,
            // insert number for new para (current position)
            Integer num = getParaNumber(prevParaEleStart);
            num++;
            insertNumber(editor.getCaretPosition(), prevParaEleStart, num);

            // After insert, check for numbered paras following the newly
            // inserted numberd para; and re-number those paras.

            // Get newly inserted number para details
            StyledDocument doc = getEditorDocument();
            Element newParaEle = doc.getParagraphElement(editor.getCaretPosition());
            int newParaEleEnd = newParaEle.getEndOffset();

            if (newParaEleEnd > doc.getLength()) {

                return; // no next para, end of document text
            }

            // Get next para (following the newly inserted one) and
            // re-number para only if already numered.
            Element nextParaEle = doc.getParagraphElement(newParaEleEnd + 1);
            int nextParaEleStart = nextParaEle.getStartOffset();

            if (isNumberedPara(nextParaEleStart)) {

                doNewNumbers(nextParaEleStart, num);
            }

        } // doEnterKeyRoutine()

    } // NumbersParaKeyListener

    public DefaultStyledDocument getStyledDocument() {
        return (DefaultStyledDocument) getEditorDocument();
    }

    public void setDocument(File file) {
        if (file != null) {
            readFile(file);
        } else {
            editor.setText("");
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

        editor.setDocument(doc);
        doc.addUndoableEditListener(new UndoEditListener());
        //applyFocusListenerToPictures(doc);
    }

}
