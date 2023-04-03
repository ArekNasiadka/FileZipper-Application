package filezipper;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
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
                createZipArchive();
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
        
        private void createZipArchive()
        {
            chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            chooser.setSelectedFile(new File(System.getProperty("user.dir")+File.separator+"myname.zip"));
            int tmp = chooser.showDialog(rootPane, "Compress");
            
            if (tmp == JFileChooser.APPROVE_OPTION)
            {      
       
                    byte tmpData[] = new byte[BUFFOR];
                    try
                    {
                     ZipOutputStream zOutS = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(chooser.getSelectedFile()),BUFFOR));

                     for (int i = 0; i < listModel.getSize(); i++)
                     {
                         if (!((File)listModel.get(i)).isDirectory())
                             zipp(zOutS, (File)listModel.get(i), tmpData, ((File)listModel.get(i)).getPath());
                         else
                         {
                             writePaths((File)listModel.get(i));
                             
                             for (int j = 0; j < pathsList.size(); j++)
                                 zipp(zOutS, (File)pathsList.get(j), tmpData, ((File)listModel.get(i)).getPath());
                             
                                 pathsList.removeAll(pathsList);
                         } 
                     }

                     zOutS.close();
                    }
                    catch(IOException e)
                    {
                        System.out.println(e.getMessage());
                    }
            }
        }
       private void zipp(ZipOutputStream zOutS,File filePath, byte[] tmpData, String basePath) throws IOException
       {
             BufferedInputStream inS = new BufferedInputStream(new FileInputStream(filePath), BUFFOR);

             zOutS.putNextEntry(new ZipEntry(filePath.getPath().substring(basePath.lastIndexOf(File.separator)+1)));

             int counter;
             while ((counter = inS.read(tmpData, 0, BUFFOR)) != -1)
             zOutS.write(tmpData, 0, counter);


             zOutS.closeEntry();
 
             inS.close();
       }
       public static final int BUFFOR = 1024;
       
          private void writePaths(File pathName)
    {
       String[] fileCatalogName = pathName.list();
       
       for (int i = 0; i < fileCatalogName.length; i++)
       {
           File p = new File(pathName.getPath(), fileCatalogName[i]);
          
           if (p.isFile())
               pathsList.add(p);
           
           if (p.isDirectory())
               writePaths(new File(p.getPath()));
              
          
       }
    }
      
     ArrayList pathsList = new ArrayList();
    }
    
}
