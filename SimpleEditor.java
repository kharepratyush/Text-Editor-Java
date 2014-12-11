import javax.swing.*;
import javax.swing.event.*;
import java.awt.*; 
import java.awt.event.*;
import java.util.Vector;
import java.io.*;


//   A menu-based simple text editor. 



public class SimpleEditor extends JFrame implements ActionListener, DocumentListener
{
    
    private static final long serialVersionUID = 2L;
    private JMenuBar mBar = new JMenuBar();  // Create the menu bar
    private JMenu fileMenu, editMenu, cutsMenu; // Menu references
    private JMenuItem cutItem, copyItem, pasteItem, selectItem;
    private JMenuItem quitItem, openItem;    // File items
    private JMenuItem saveItem, saveAsItem, saveExitItem; // more File items
    private JTextArea display = new JTextArea();  
    private String scratchPad = "";       // Scratch pad for cut/paste
    private Vector<String> recentCuts = new Vector<String>();

    private File currentFile = new File("Untitled");
    private String startDir; // the directory form where the editor was started
    private JFileChooser openFileChooser, saveFileChooser;

    // Keeps track of if file has been changed since last change
    private boolean fileSaved = true;

    //private JTextField statusLine = new JTextField(getContentPane().getWidth());
    private JTextField statusLine = new JTextField(10);


    private JMenu preferencesMenu, textColorMenu;
    private JMenuItem fontItem, tabItem;
    private JMenuItem [] colorItems;
    private String [] fontValues = {"10", "12", "14", "16", "18", "20", "22", "24", "26", "28" ,"30"};
    private Font defaultFont;
    private int defaultFontSize = 10;;
    private int defaultTabSize;
    private String [] colorValues = {"red", "green", "blue", "black",
                                            "orange", "cyan", "magenta"};

        
    
    //  SimpleEditor() constructor sets the layout for the GUI
    //   and calls methods to initialize the menus.
    
    public SimpleEditor() 
    {
        setTitle("Simple Text Editor");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        getContentPane().setLayout(new BorderLayout()); 
        getContentPane().add("Center", display);    
        getContentPane().add(
            new JScrollPane(display, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
        
        display.setLineWrap(true);
        display.getDocument().addDocumentListener(this);

        getContentPane().add("South", statusLine);
        statusLine.setEditable(false);
        this.setJMenuBar(mBar);         

        initFileMenu();                
        initEditMenu();    
        initPreferencesMenu();

        startDir = System.getProperty("user.dir");
        openFileChooser = new JFileChooser(startDir);
        saveFileChooser = new JFileChooser(startDir);
        saveFileChooser.setDialogTitle("Save As");
    } 
    
    
    //    Creates the edit menu and adds its individual menu items. 
    //    Note the each menu item is registered with an ActionListener.
     
    private void initEditMenu() 
    {
        editMenu = new JMenu("Edit");     // Create the edit menu
        mBar.add(editMenu);           //  and add it to menu bar

        cutItem = new JMenuItem ("Cut");          // Cut item
        cutItem.addActionListener(this);
        editMenu.add(cutItem);              
        
        copyItem = new JMenuItem("Copy");         // Copy item
        copyItem.addActionListener(this);
        editMenu.add(copyItem);
        
        pasteItem = new JMenuItem("Paste");       // Paste item
        pasteItem.addActionListener(this);  
        editMenu.add(pasteItem);
        
        editMenu.addSeparator();
        
        selectItem = new JMenuItem("Select All"); // Select item
        selectItem.addActionListener(this); 
        editMenu.add(selectItem);   
        /*
        editMenu.addSeparator();
        
        cutsMenu = new JMenu("Recent Cuts");      // Recent cuts submenu
        editMenu.add(cutsMenu);
        */
    } 

    
    //   Creates the preferences menu and adds its individual menu items. 
    
    private void initPreferencesMenu() 
    {
        preferencesMenu = new JMenu("Preferences"); 
        mBar.add(preferencesMenu);            //  and add it to menu bar

        fontItem = new JMenuItem ("Font Size");          // Cut item
        fontItem.addActionListener(this);
        preferencesMenu.add(fontItem);              

        tabItem = new JMenuItem ("Tab Size");          // Cut item
        tabItem.addActionListener(this);
        preferencesMenu.add(tabItem);               

    } 


    
    //    Creates the file menu and adds its individual menu items. 
    //    Note the each menu item is registered with an ActionListener.
    
    private void initFileMenu() 
    {
        fileMenu = new JMenu("File");       // Create the file menu
        mBar.add(fileMenu);                 // and add it to the menu bar
        
        openItem = new JMenuItem("Open");   // Open item
        openItem.addActionListener( this );
        fileMenu.add(openItem);

        saveItem = new JMenuItem("Save");   // Save item
        saveItem.addActionListener(this);
        fileMenu.add(saveItem); 

        saveAsItem = new JMenuItem("Save As");   // Save As item
        saveAsItem.addActionListener(this);
        fileMenu.add(saveAsItem);   

        fileMenu.addSeparator();            // Logical separator

        saveExitItem = new JMenuItem("Save-Exit");   // Save item
        saveExitItem.addActionListener(this);
        fileMenu.add(saveExitItem); 

        quitItem = new JMenuItem("Quit");   // Quit item
        quitItem.addActionListener(this);
        fileMenu.add(quitItem); 
    } 
        
    
    //    Listen for text changes to update the status line.
    
    public void changedUpdate(DocumentEvent e)
    {
        displayStatus("");
        fileSaved = false;
    }

    
    // Update status for insert
    
    public void insertUpdate(DocumentEvent e) 
    {
        displayStatus("");
        fileSaved = false;
    }

    
    // * Update status for remove

    public void removeUpdate(DocumentEvent e) 
    {
        displayStatus("");
        fileSaved = false;
    }

    
    //    Display a status line.
    
    private void displayStatus(String message)
    {
        if (display.getLineCount() >= 0)
            statusLine.setText(" "+currentFile.getName()+" "+
                display.getLineCount()+" lines "
                +display.getText().length()+" chars "+message);
    }

    
    //    Display a status line with an optional filename parameter.
       private void displayStatus(File fileobj, String message)
    {
        if (display.getLineCount() >= 0)
            statusLine.setText(" "+fileobj.getName()+" "+
                display.getLineCount()+" lines "
                +display.getText().length()+" chars "+message);
    }


      
    //       Handles the user's menu selections.

    public void actionPerformed(ActionEvent e) 
    {
        // Get the selected menu item
    
    JMenuItem m  = (JMenuItem)e.getSource();  

        if (m == fontItem)
        {
            selectAndSetFont();
        }
        else if ( m == tabItem ) 
        {
            selectAndSetTab();
        }
        else if ( m == saveExitItem ) 
        {
            writeFile();
            quitEditor();
        } 
        /*else if (isColorItem(m))
        {
            // get the text associated with the menu item
            String color = m.getActionCommand();

            Color newColor = findColor(color);
            display.setForeground(newColor);
        }*/
        else if ( m == quitItem ) 
        {
            quitEditor();
        } 
        else if (m == openItem) 
        {
            openAndReadFile();
        } 
        else if (m == saveItem) 
        {
            writeFile();
        }
        else if (m == saveAsItem) 
        {
            openAndSaveFile();
        }
        else if (m == cutItem) 
        {  
            // Cut the selected text
            scratchPad = display.getSelectedText();
            display.replaceRange("",display.getSelectionStart(),display.getSelectionEnd());
        } 
        else if (m == copyItem) 
        {  
            scratchPad = display.getSelectedText();
        } 
        else if (m == pasteItem) 
        {
            display.insert(scratchPad, display.getCaretPosition()); 
        } 
        else if ( m == selectItem ) 
        {               
            display.selectAll();                
        } 
        else
        {
            JMenuItem item = (JMenuItem)e.getSource(); 
            scratchPad = item.getActionCommand();    
        }              
    } 


    
    //    Create a dialog with user to set up font size.
    
    private void selectAndSetFont()
    {
        
        String fontSize = (String) JOptionPane.showInputDialog(null, "Select Font Size", 
                                                    "Font Size Selector", 
                                                    JOptionPane.QUESTION_MESSAGE, 
                                                    null, 
                                                    fontValues, fontValues[0]);
        if (fontSize != null)
        {
            defaultFontSize = Integer.parseInt(fontSize);
            defaultFont = new Font("Serif", Font.PLAIN, defaultFontSize );
            display.setFont(defaultFont);
        }

    }

    
    //    Create a dialog with user to set up tab size.
    
    private void selectAndSetTab()
    {
        String tabSize = (String) JOptionPane.showInputDialog(this, "Select Tab Size"); 
        if (tabSize != null)
        {
            defaultTabSize = Integer.parseInt(tabSize);
            display.setTabSize(defaultTabSize);
        }
    }


    
    //    Confirm with the user  to save modified file before quitting the editor.
    
    private void quitEditor()
    {
        if (!fileSaved)
        {
            int returnValue = JOptionPane.showConfirmDialog(this, "Save File?");
            if (returnValue == JOptionPane.YES_OPTION)
                writeFile();
            else  if (returnValue == JOptionPane.CANCEL_OPTION)
                return;
        } 
        System.exit(0);
    }


    
    private void openAndReadFile()
    {
        // if current file is modified, confirm for save
        if (!fileSaved)
        {
            int returnValue = JOptionPane.showConfirmDialog(this, 
                                                       "Save Current File?");
            if (returnValue == JOptionPane.YES_OPTION)
                writeFile();
            if (returnValue == JOptionPane.CANCEL_OPTION)
                return;
        } 
        // clear text area to open new file
        display.setText("");
        displayStatus("");
        // set up a new default file name
        currentFile = new File("Untitled");

        int returnVal = openFileChooser.showOpenDialog(getContentPane());
        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            currentFile =  openFileChooser.getSelectedFile();
            try 
            {
                BufferedReader in = new BufferedReader(
                                        new FileReader(currentFile));
                display.read(in, null);
                // Need to add the DocumentListener again since read replaces
                // the Document object.
                display.getDocument().addDocumentListener(this);
                display.setCaretPosition(display.getDocument().getLength()-1);
                displayStatus(" read");
                fileSaved = true;
            } 
            catch (FileNotFoundException e) 
            {
                try
                {
                    currentFile.createNewFile();
                    statusLine.setText(currentFile.getName()+"  [New file]");
                } 
                catch (IOException e1) 
                {
                    String message = e1.getMessage();
                    JOptionPane.showMessageDialog(this, message);
                }
            } 
            catch (IOException e)
            {
                String message = e.getMessage();
                JOptionPane.showMessageDialog(this, message);
            }
        }
    }

    private void writeFile()
    {
        try 
        {
            FileWriter out = new FileWriter(currentFile);
            display.write(out);
            out.close();
            displayStatus(" written");
            fileSaved = true;
        } 
        catch (IOException e) 
        {
            String message = e.getMessage();
            JOptionPane.showMessageDialog(this, message);
        }

    } 

    
    //    Save edited file to specified file.
    
    private void writeFile(File saveFile)
    {
        try 
        {
            FileWriter out = new FileWriter(saveFile);
            display.write(out);
            out.close();
            displayStatus(saveFile, " written");
            fileSaved = true;
        } 
        catch (IOException e) 
        {
            String message = e.getMessage();
            JOptionPane.showMessageDialog(this, message);
        }

    } 
    
    
    private void openAndSaveFile()
    {
        File saveFile;


        int returnVal = saveFileChooser.showSaveDialog(getContentPane());
        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            saveFile =  saveFileChooser.getSelectedFile();
            if (saveFile.exists())
            {
                int returnValue = JOptionPane.showConfirmDialog(this,
                        "Overwrite Existing File: "+saveFile.getName()+"?",
                        "Question", JOptionPane.YES_NO_OPTION);
                if (returnValue != JOptionPane.YES_OPTION)
                    return;
            }
            writeFile(saveFile);
        } 
    }



    public static void main(String args[]) 
    {
        // Need to make the variable f final so it can be accessed from the
        // inner class in the addWindowListener call below.
        final SimpleEditor f = new SimpleEditor(); 

        f.setSize(800, 600);  
        f.setVisible(true);
        f.addWindowListener(new WindowAdapter() {
               public void windowClosed(WindowEvent e) {f.quitEditor();}
        });
    } 
} 
