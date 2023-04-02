package filezipper;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import javax.swing.*;

public class FileZipper extends JFrame
{
    public FileZipper()
    {
        this.setTitle("Zipper");
        this.setBounds(275, 300, 250, 250);
        this.setJMenuBar(menuBar);
        
        JMenu menuFile = menuBar.add(new JMenu("File"));
       
        Action actionAdd  = new Action("Add", "Add new entry to archive", "ctrl D", new ImageIcon("add.png"));
        Action actionRemove  = new Action("Remove", "Remove selected entries from the archive", "ctrl U", new ImageIcon("remove.png"));
        Action actionZip  = new Action("Zip", "Zip", "ctrl Z");
        
        JMenuItem menuAdd = menuFile.add(actionAdd);
        JMenuItem menuRemove = menuFile.add(actionRemove);
        JMenuItem menuZip = menuFile.add(actionZip);
        JScrollPane scroll = new JScrollPane(list);
        bAdd = new JButton(actionAdd);
        bRemove = new JButton(actionRemove);
        bZip = new JButton(actionZip);
        
        list.setBorder(BorderFactory.createEtchedBorder());
        GroupLayout layout = new GroupLayout(this.getContentPane());
        
        
        layout.setAutoCreateContainerGaps(true);
        layout.setAutoCreateGaps(true);
        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                     .addComponent(scroll, 100, 150, Short.MAX_VALUE)
                     .addContainerGap(0, Short.MAX_VALUE)
                     .addGroup(
                     layout.createParallelGroup().addComponent(bAdd).addComponent(bRemove).addComponent(bZip)
                     
                     )
                     );
        
        layout.setVerticalGroup(
                layout.createParallelGroup()
                     .addComponent(scroll, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                     .addGroup(layout.createSequentialGroup().addComponent(bAdd).addComponent(bRemove).addGap(5, 40, Short.MAX_VALUE).addComponent(bZip))
                
                );
        
        this.getContentPane().setLayout(layout);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        this.pack();
    }
    private DefaultListModel listModel = new DefaultListModel()
    {
      @Override
      public void addElement(Object obj) 
      {
          list.add(obj);
          super.addElement(((File)obj).getName());
      }  
      @Override
      public Object get(int index) 
      {
        return list.get(index);
      }
      
      @Override
      public Object remove(int index)
      {
          list.remove(index);
          return super.remove(index);
      }
      ArrayList list = new ArrayList();
    };
    private JList list = new JList(listModel);
    private JButton bAdd;
    private JButton bRemove;
    private JButton bZip;
    private JMenuBar menuBar = new JMenuBar();
    private JFileChooser chooser = new JFileChooser();
    public static void main(String[] args) 
    {
        new FileZipper().setVisible(true);
    }
    
    private class Action extends AbstractAction
    {
         public Action(String name, String description, String hotKey)
         {
             this.putValue(Action.NAME, name);
             this.putValue(Action.SHORT_DESCRIPTION, description);
             this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(hotKey));
         }
         public Action(String name, String description, String hotKey, Icon icon)
         {
             this(name, description, hotKey);
             this.putValue(Action.SMALL_ICON, icon);
         }
        public void actionPerformed(ActionEvent e) 
        {
            if (e.getActionCommand().equals("Add"))
                addEntriesToArchive();
            else if (e.getActionCommand().equals("Remove"))
                removeEntriesFromList();
            else if (e.getActionCommand().equals("Zip"))
                System.out.println("Zip");
        }
    
        private void addEntriesToArchive()
        {
            chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            chooser.setMultiSelectionEnabled(true);
            
            int tmp = chooser.showDialog(rootPane, "Add to archieve");
            
            if (tmp == JFileChooser.APPROVE_OPTION)
            {
                File[] paths =  chooser.getSelectedFiles();
                
                for (int i = 0; i < paths.length; i++)
                   if(!isEntryRepeated(paths[i].getPath())) 
                    listModel.addElement(paths[i]);
            }
        }
        private boolean isEntryRepeated(String testEntry)
        {
            for (int i = 0; i < listModel.getSize(); i++)
                if (((File)listModel.get(i)).getPath().equals(testEntry) )
                    return true;
            return false;
        }
        private void removeEntriesFromList()
        {
            int[] tmp = list.getSelectedIndices();
            
            for (int i = 0; i < tmp.length; i++)
                listModel.remove(tmp[i]-i);
        }
    }
}
