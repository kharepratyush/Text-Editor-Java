// SimpleEditor.java
// An example showing several DefaultEditorKit features. This class is designed
// to be easily extended for additional functionality.
//
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.util.Hashtable;

public class SimpleEditor extends JFrame {

  private Action openAction = new OpenAction();
  private Action saveAction = new SaveAction();

  public static JTextArea lines;
  private static JTextComponent textComp;
  private Hashtable actionHash = new Hashtable();


  private boolean fileSaved = false;

  public static void main(String[] args) {
    SimpleEditor editor = new SimpleEditor();
    editor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);



    
    JScrollPane scroll = new JScrollPane (textComp, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    scroll.setRowHeaderView(lines);

editor.add(scroll);
    editor.setVisible(true);
  }

  // Create an editor.
  public SimpleEditor() {
    super("AziEditor");
    textComp = createTextComponent();
    makeActionsPretty();

    Container content = getContentPane();
    content.add(textComp, BorderLayout.SOUTH);
    content.add(createToolBar(), BorderLayout.NORTH);
    setJMenuBar(createMenuBar());
    setSize(768,512);
  }

  // Create the JTextComponent subclass.
  protected JTextComponent createTextComponent()  {
    JTextArea ta = new JTextArea();
    //ta.setLineWrap(true);
    Font font = new Font("Serif", Font.ITALIC, 20);
    //Font font = new Font("Verdana", Font.BOLD, 16);
	ta.setFont(font);
	ta.setBackground(Color.DARK_GRAY);
    ta.setForeground(Color.WHITE);
	//ta.setForeground(Color.BLUE);
	ta.setLineWrap(true);
	ta.setWrapStyleWord(true);


	


	lines = new JTextArea("1");
	lines.setFont(font);
	lines.setBackground(Color.GRAY);
	lines.setForeground(Color.BLACK);
	lines.setEditable(false);
 
	ta.getDocument().addDocumentListener(new DocumentListener(){
			public String getText(){
				int caretPosition = ta.getDocument().getLength();
				Element root = ta.getDocument().getDefaultRootElement();
				String text = "1  " + System.getProperty("line.separator");
				for(int i = 2; i < root.getElementIndex( caretPosition ) + 2; i++){
					text += i +"  " +System.getProperty("line.separator");
				}
				return text;
			}
			@Override
			public void changedUpdate(DocumentEvent de) {
				lines.setText(getText());
			}
 
			@Override
			public void insertUpdate(DocumentEvent de) {
				lines.setText(getText());
			}
 
			@Override
			public void removeUpdate(DocumentEvent de) {
				lines.setText(getText());
			}
 
		});
 
		




    return ta;
  }

  // Add icons and friendly names to actions we care about.
  protected void makeActionsPretty() {
    Action a;
    a = textComp.getActionMap().get(DefaultEditorKit.cutAction);
    a.putValue(Action.SMALL_ICON, new ImageIcon("support/cut.gif"));
    a.putValue(Action.NAME, "Cut");

    a = textComp.getActionMap().get(DefaultEditorKit.copyAction);
    a.putValue(Action.SMALL_ICON, new ImageIcon("support/copy.gif"));
    a.putValue(Action.NAME, "Copy");

    a = textComp.getActionMap().get(DefaultEditorKit.pasteAction);
    a.putValue(Action.SMALL_ICON, new ImageIcon("support/paste.gif"));
    a.putValue(Action.NAME, "Paste");

    a = textComp.getActionMap().get(DefaultEditorKit.selectAllAction);
    a.putValue(Action.NAME, "Select All");
  }

  // Create a simple JToolBar with some buttons.
  protected JToolBar createToolBar() {
    JToolBar bar = new JToolBar();

    // Add simple actions for opening & saving.
    bar.add(getOpenAction()).setText("");
    bar.add(getSaveAction()).setText("");
    bar.addSeparator();

    // Add cut/copy/paste buttons.
    bar.add(textComp.getActionMap().get(DefaultEditorKit.cutAction)).setText("");
    bar.add(textComp.getActionMap().get(
              DefaultEditorKit.copyAction)).setText("");
    bar.add(textComp.getActionMap().get(
              DefaultEditorKit.pasteAction)).setText("");
    return bar;
  }

  // Create a JMenuBar with file & edit menus.
  protected JMenuBar createMenuBar() {
    JMenuBar menubar = new JMenuBar();
    JMenu file = new JMenu("File");
    JMenu edit = new JMenu("Edit");
    menubar.add(file);
    menubar.add(edit);

    file.add(getOpenAction());
    file.add(getSaveAction());
    file.add(new ExitAction());
    edit.add(textComp.getActionMap().get(DefaultEditorKit.cutAction));
    edit.add(textComp.getActionMap().get(DefaultEditorKit.copyAction));
    edit.add(textComp.getActionMap().get(DefaultEditorKit.pasteAction));
    edit.add(textComp.getActionMap().get(DefaultEditorKit.selectAllAction));
    return menubar;
  }

  // Subclass can override to use a different open action.
  protected Action getOpenAction() { return openAction; }

  // Subclass can override to use a different save action.
  protected Action getSaveAction() { return saveAction; }

  protected JTextComponent getTextComponent() { return textComp; }

  // ********** ACTION INNER CLASSES ********** //

  // A very simple exit action
  
  // An action that opens an existing file
  class OpenAction extends AbstractAction {
    public OpenAction() { 
      super("Open", new ImageIcon("support/open.gif")); 
    }

    // Query user for a filename and attempt to open and read the file into the
    // text component.
    public void actionPerformed(ActionEvent ev) {
      JFileChooser chooser = new JFileChooser();
      if (chooser.showOpenDialog(SimpleEditor.this) !=
          JFileChooser.APPROVE_OPTION)
        return;
      File file = chooser.getSelectedFile();
      if (file == null)
        return;

      FileReader reader = null;
      try {
        reader = new FileReader(file);
        textComp.read(reader, null);
      }
      catch (IOException ex) {
        JOptionPane.showMessageDialog(SimpleEditor.this,
        "File Not Found", "ERROR", JOptionPane.ERROR_MESSAGE);
      }
      finally {
        if (reader != null) {
          try {
            reader.close();
          } catch (IOException x) {}
        }
      }
    }
  }

  // An action that saves the document to a file
  class SaveAction extends AbstractAction {
    public SaveAction() {
      super("Save", new ImageIcon("support/save.gif"));
    }

    // Query user for a filename and attempt to open and write the text
    // componentâ€™s content to the file.
    public void actionPerformed(ActionEvent ev) {
      JFileChooser chooser = new JFileChooser();
      fileSaved=true;
      if (chooser.showSaveDialog(SimpleEditor.this) !=
          JFileChooser.APPROVE_OPTION)
        return;
      File file = chooser.getSelectedFile();
      if (file == null)
        return;

      FileWriter writer = null;
      try {
        writer = new FileWriter(file);
        textComp.write(writer);
      }
      catch (IOException ex) {
        JOptionPane.showMessageDialog(SimpleEditor.this,
        "File Not Saved", "ERROR", JOptionPane.ERROR_MESSAGE);
      }
      finally {
        if (writer != null) {
          try {
            writer.close();
          } catch (IOException x) {}
        }
      }
    }
  }
  public  int writeFile()
  {
  	JFileChooser chooser = new JFileChooser();
      if (chooser.showSaveDialog(SimpleEditor.this) !=
          JFileChooser.APPROVE_OPTION)
        return 1;
    File file = chooser.getSelectedFile();
      if (file == null)
        return 0;

      FileWriter writer = null;
      try {
        writer = new FileWriter(file);
        textComp.write(writer);
      }
      catch (IOException ex) {
        JOptionPane.showMessageDialog(SimpleEditor.this,
        "File Not Saved", "ERROR", JOptionPane.ERROR_MESSAGE);
      }
      finally {
        if (writer != null) {
          try {
            writer.close();
          } catch (IOException x) {}
        }
  }
  return 0;
}
public class ExitAction extends AbstractAction {
    public ExitAction() { super("Exit"); }
    public void actionPerformed(ActionEvent ev) {  
    		//if(fileSaved)	System.exit(0);
			int returnValue = JOptionPane.showConfirmDialog(null, "Save File?","Save File?", JOptionPane.YES_NO_OPTION);
			if (returnValue == JOptionPane.YES_OPTION)
			{	int r= writeFile();
				if(r==1)
					return;
				System.exit(0);
			}
			else  if (returnValue == JOptionPane.NO_OPTION)
			{	System.exit(0);
			}
		
		
	}
  }
  

 }
