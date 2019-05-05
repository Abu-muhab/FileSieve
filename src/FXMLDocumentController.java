/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.ResourceBundle;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

/**
 *
 * @author Abdulmalik Abubakar
 */
public class FXMLDocumentController implements Initializable {
    
    private Label label;
    @FXML
    private JFXTextField searchField;
    @FXML
    private JFXTextField destinationField;
    @FXML
    private JFXTextField keyField;
    @FXML
    private JFXButton copyButton;
    @FXML
    private JFXButton moveButton;
    @FXML
    private JFXRadioButton currentDirectoryBox;
    @FXML
    private ToggleGroup radioGroup;
    @FXML
    private JFXRadioButton subDirectoryBox;
    @FXML
    private JFXProgressBar progressBar;
    @FXML
    private AnchorPane achorPane;
    @FXML
    private Pane pane;
    
    static double xOffset;
    static double yOffset;
    @FXML
    private ImageView exit;
    @FXML
    private ImageView minimise;
    
 
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void moveButtonClicked(ActionEvent event) {
        copyButton.setDisable(true);
        moveButton.setDisable(true);
        progressBar.setVisible(true);
        File source=new File(searchField.getText());
        File destination=new File(destinationField.getText());
        boolean checkSubDir=false;
        if(subDirectoryBox.isSelected())
        {
            checkSubDir=true;
        }
        progressBar.setVisible(true);
        MoveTask moveTask=new MoveTask(source,destinationField.getText(),keyField.getText(),checkSubDir,progressBar,moveButton);
        progressBar.progressProperty().bind(moveTask.progressProperty());
        new Thread(moveTask).start();
        
        
    }

    @FXML
    private void keyTyped(KeyEvent event) {
        File sourceDirectory=new File(searchField.getText());
        File destinationDirectory=new File(destinationField.getText());
        String extension=keyField.getText();
        
        if(sourceDirectory.exists()&&destinationDirectory.exists()&&!extension.equals("")&&sourceDirectory.isDirectory()&&destinationDirectory.isDirectory())
        {
            moveButton.setDisable(false);
            copyButton.setDisable(false);
        }
        else
        {
            moveButton.setDisable(true);
            copyButton.setDisable(true);
        }
    }

    @FXML
    private void paneDragged(MouseEvent event) {
        FileSieve_v_2.primaryStage.setX(event.getScreenX()+xOffset);
        FileSieve_v_2.primaryStage.setY(event.getScreenY()+yOffset);
        
    }

    @FXML
    private void panePressed(MouseEvent event) {
        xOffset=FileSieve_v_2.primaryStage.getX()-event.getScreenX();
        yOffset=FileSieve_v_2.primaryStage.getY()-event.getScreenY();
    }

    @FXML
    private void exitButtonClicked(MouseEvent event) {
        FileSieve_v_2.primaryStage.close();
        System.exit(0);
    }

    @FXML
    private void minimiseButtonClicked(MouseEvent event) {
        FileSieve_v_2.primaryStage.setIconified(true);
    }

    @FXML
    private void copyButtonClicked(ActionEvent event) {
        copyButton.setDisable(true);
        moveButton.setDisable(true);
        CopyTask task=new CopyTask(searchField,destinationField,keyField,subDirectoryBox,progressBar,copyButton);
        progressBar.setVisible(true);
        progressBar.progressProperty().bind(task.progressProperty());
        new Thread(task).start();
    }
    
}
class CopyTask extends Task
{
    JFXRadioButton subDirectoryBox;
    JFXTextField searchField;
    JFXTextField destinationField;
    JFXTextField keyWordField;
    String source;
    String destination;
    String keyWord;
    public static  int fileCount;
    public static  int fileProcessed;
    JFXProgressBar bar;
    JFXButton button;
    File sourceDirectory;
        
    public Void call()
    {
        sourceDirectory=new File(source);
        copy(sourceDirectory);
        bar.setVisible(false);
        button.setDisable(false);
        fileCount=0;
        fileProcessed=0;
        return null;
    }
    
    public CopyTask(JFXTextField searchField,JFXTextField destinationField,JFXTextField keyWordField,JFXRadioButton subDirectoryBox,JFXProgressBar bar,JFXButton button)
    {
        this.subDirectoryBox=subDirectoryBox;
        this.searchField=searchField;
        this.destinationField=destinationField;
        this.keyWordField=keyWordField;
        source=this.searchField.getText();
        destination=this.destinationField.getText();
        keyWord=this.keyWordField.getText();
        this.bar=bar;
        this.button=button;
    }
    
    public void copy(File sourceDirectory)
    {
        {
            File[] filesInSourceDirectory=sourceDirectory.listFiles();
            fileCount=filesInSourceDirectory.length;
            for(File f: filesInSourceDirectory)
            {
                if(!f.isDirectory())
                {
                    fileProcessed++;
                    if(f.getName().toLowerCase().contains(keyWord.toLowerCase()))
                    {
                        String pathA=f.getParentFile().getPath()+"\\"+f.getName();
                        String pathB=destination+"\\"+f.getName();
                        Path sourcePath=Paths.get(pathA);
                        Path destinationPath=Paths.get(pathB);
                        try
                        {
                            Files.copy(sourcePath,destinationPath,REPLACE_EXISTING);
                        }
                        catch(Exception e)
                        {
                        
                        }
                        updateProgress(fileProcessed,fileCount);
                    }
                   
                }
                else
                {
                    fileCount--;
                    if(subDirectoryBox.isSelected())
                    {
                        File[] subDirectoryFiles=f.listFiles();
                        fileCount=fileCount+subDirectoryFiles.length;
                        copy(f);
                    }
                    
                }
            }
        }
        
    }
}

class MoveTask extends Task
{
    static double fileCount=0;
    static double fileProcessed=0;
    File source;
    String destination;
    String ee;
    boolean checkSubDir;
    boolean done=false;
    JFXProgressBar pb;
    JFXButton moveButton;
    File next_=null;
    public Void call()
    {
        move(source);
        try
        {
           Thread.sleep(1000);
        }
        catch(Exception e)
        {
          
        }
        pb.setVisible(false);
        moveButton.setDisable(false);
        fileCount=0;
        fileProcessed=0;
        return null;
    }
    public MoveTask(File source,String destination,String ee,boolean checkSubDir,JFXProgressBar pb,JFXButton moveButton)
    {
        this.source=source;
        this.destination=destination;
        this.ee=ee;
        this.checkSubDir=checkSubDir;
        this.pb=pb;
        this.moveButton=moveButton;
    }
    public void move(File source)
    {
        
       moveButton.setDisable(true);
       int directoryCount=0;
       File[] files=source.listFiles();
       fileCount=files.length;
       for(File f: files)
       {
           if(f.isDirectory())
           {
               directoryCount++;
           }
       }
       fileCount=fileCount-directoryCount;
       for(File x: files)
       {
           
           if(!x.isDirectory())
           {
               String name=x.getName();
               name=name.toLowerCase();
               ee=ee.toLowerCase();
               
               if(name.contains(ee))
               {
                   fileProcessed++;
                   x.renameTo(new File(destination+"\\"+name));
                   updateProgress(fileProcessed,fileCount);
                   updateProgress(fileProcessed,fileCount);
                   try
                   {
                       Thread.sleep(7);
                   }
                   catch(Exception e)
                   {
                       
                   }
               }
           }
           else
           {
               
               if(checkSubDir)
               {
                   /*
                   int directoryCountTemp=0;
                   File[] temp=x.listFiles();
                   fileCount+=temp.length;
                   for(File k: temp)
                   {
                       if(k.isDirectory())
                       {
                           directoryCountTemp++;
                       }
                   }
                   fileCount=fileCount-directoryCountTemp;
                   updateProgress(1,100);
                   move(x);
                           */
                   
                   
                   
                   
                   
                    fileCount--;
                    if(checkSubDir)
                    {
                        File[] subDirectoryFiles=x.listFiles();
                        fileCount=fileCount+subDirectoryFiles.length;
                        move(x);
                    }
               }
           }
          
       }
       /*
       done=true;
       fileCount=100;
       fileProcessed=100;
       updateProgress(fileProcessed,fileCount);
       fileCount=0;
       fileProcessed=0;   
               */
    }
}
