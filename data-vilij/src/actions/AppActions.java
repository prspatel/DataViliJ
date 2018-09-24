package actions;

import dataprocessors.AppData;
import dataprocessors.DataSet;
import dataprocessors.TSDProcessor;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.Chart;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import ui.AppUI;
import vilij.components.ActionComponent;
import vilij.components.ConfirmationDialog;
import vilij.components.ErrorDialog;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import static settings.AppPropertyTypes.*;

/**
 * This is the concrete implementation of the action handlers required by the application.
 *
 * @author Ritwik Banerjee
 */
public final class AppActions implements ActionComponent {

    /** The application to which this class of actions belongs. */
    private ApplicationTemplate applicationTemplate;

    public Path getDataFilePath() {
        return dataFilePath;
    }

    /** Path to the data file currently active. */
    Path dataFilePath;
    private File selectedFile;


    public AppActions(ApplicationTemplate applicationTemplate) {
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    public void handleNewRequest() {
        // TODO for homework 1
        ((AppUI)applicationTemplate.getUIComponent()).setHasNewText(true);
        if(((AppUI)applicationTemplate.getUIComponent()).getTextArea().equals("")){
            ((AppUI)applicationTemplate.getUIComponent()).createVBox();
        }
        else {
            try {
                promptToSave();
                selectedFile = null;
            } catch (IOException e) {
                PropertyManager manager = applicationTemplate.manager;
                ErrorDialog.getDialog().show(manager.getPropertyValue(SAVE_UNSAVED_WORK_TITLE.name()), manager.getPropertyValue(SAVE_UNSAVED_WORK.name()));
            }
        }
    }
    public void checkData() throws Exception{
        TSDProcessor k = new TSDProcessor();
        k.processString(((AppUI)(applicationTemplate.getUIComponent())).getTextArea());
    }

    @Override
    public void handleSaveRequest() {
        // TODO: NOT A PART OF HW 1

        PropertyManager manager=applicationTemplate.manager;

        try{
            checkData();
            boolean k = checkLine(((AppUI) (applicationTemplate.getUIComponent())).getTextArea());

            if (k == true) {
                if (selectedFile == null) {
                        FileChooser fileChooser = new FileChooser();
                        fileChooser.getExtensionFilters().add(
                                new FileChooser.ExtensionFilter("Tab-Separated Data File(.*.tsd)", "*.tsd"));
                        selectedFile = fileChooser.showSaveDialog(ConfirmationDialog.getDialog());
                }
                if (selectedFile != null) {
                        dataFilePath= Paths.get(selectedFile.getAbsolutePath());
                        ((AppData)(applicationTemplate.getDataComponent())).saveData(dataFilePath);

                }
                ((AppUI) (applicationTemplate.getUIComponent())).getSaveButton().setDisable(true);
            }
        }catch(Exception e){

            ErrorDialog.getDialog().show(manager.getPropertyValue(INVALID_TEXTAREA_TITLE.name()),e.getMessage());

        }

    }

    @Override
    public void handleLoadRequest() {
        // TODO: NOT A PART OF HW 1
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Tab-Separated Data File(.*.tsd)", "*.tsd"));

        selectedFile = fileChooser.showOpenDialog(applicationTemplate.getUIComponent().getPrimaryWindow());
        if (selectedFile != null) {
            ((AppData)(applicationTemplate.getDataComponent())).getLineslist().clear();
            ((AppUI)(applicationTemplate.getUIComponent())).getChart().getData().clear();
            ((AppData) applicationTemplate.getDataComponent()).clear();
            ((AppUI)(applicationTemplate.getUIComponent())).createVBox();
            dataFilePath= Paths.get(selectedFile.getAbsolutePath());
            ((AppData)(applicationTemplate.getDataComponent())).loadData(dataFilePath);
            try {
                ((AppUI)applicationTemplate.getUIComponent()).setDataset(DataSet.fromTSDFile(dataFilePath));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
   // if (/**algoname = randomClassfiier*/){ Thread thread = new Thread(Class.forName(/**/).getConstructor(/**/).newInstance()), thread.start();}

    @Override
    public void handleExitRequest() {
        // TODO for homework 1
        try {
            if (((AppUI) applicationTemplate.getUIComponent()).getThread().isAlive()) {
                ErrorDialog.getDialog().show(" exit", "Algorithm is running!! Though exiting");
                applicationTemplate.getUIComponent().getPrimaryWindow().close();

            }
            else {
                if (((AppUI) applicationTemplate.getUIComponent()).isHasNewText() && ((AppUI) applicationTemplate.getUIComponent()).getTextArea().length() != 0) {
                    try {
                        promptToSave();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                applicationTemplate.getUIComponent().getPrimaryWindow().close();
            }
        }catch(NullPointerException e){
            applicationTemplate.getUIComponent().getPrimaryWindow().close();
        }

    }

    @Override
    public void handlePrintRequest() {
        // TODO: NOT A PART OF HW 1
    }
     public void handleScreenshotRequest() throws IOException {
        // TODO: NOT A PART OF HW 1
        Chart chart = ((AppUI)applicationTemplate.getUIComponent()).getChart();
        try{
            WritableImage scrnimage = chart.snapshot(new SnapshotParameters(),null);
            File file = new File("chartscreenshot.png");
            ImageIO.write(SwingFXUtils.fromFXImage(scrnimage,null), "png", file);
        }catch(Exception e){

        }
    }

    /**
     * This helper method verifies that the user really wants to save their unsaved work, which they might not want to
     * do. The user will be presented with three options:
     * <ol>
     * <li><code>yes</code>, indicating that the user wants to save the work and continue with the action,</li>
     * <li><code>no</code>, indicating that the user wants to continue with the action without saving the work, and</li>
     * <li><code>cancel</code>, to indicate that the user does not want to continue with the action, but also does not
     * want to save the work at this point.</li>
     * </ol>
     *
     * @return <code>false</code> if the user presses the <i>cancel</i>, and <code>true</code> otherwise.
     */
    private boolean promptToSave() throws IOException {
        // TODO for homework 1
        // TODO remove the placeholder line below after you have implemented this method
        PropertyManager manager = applicationTemplate.manager;
        ConfirmationDialog.getDialog().show(manager.getPropertyValue(SAVE_UNSAVED_WORK_TITLE.name()), manager.getPropertyValue(SAVE_UNSAVED_WORK.name()));
        if(ConfirmationDialog.getDialog().getSelectedOption().equals(ConfirmationDialog.Option.YES)){
            try{
                checkData();
                try {
                    if(selectedFile==null) {
                        FileChooser fileChooser = new FileChooser();
                        fileChooser.getExtensionFilters().add(
                                new FileChooser.ExtensionFilter("Tab-Separated Data File(.*.tsd)", "*.tsd"));
                        selectedFile = fileChooser.showSaveDialog(ConfirmationDialog.getDialog());
                    }
                    if (selectedFile != null) {
                        FileWriter writer = new FileWriter(selectedFile);
                        writer.write(((AppUI) (applicationTemplate.getUIComponent())).getTextArea());
                        writer.close();
                    }
                    ((AppUI)(applicationTemplate.getUIComponent())).getSaveButton().setDisable(true);
                } catch (IOException e) {
                    ErrorDialog.getDialog().show(manager.getPropertyValue(SAVE_UNSAVED_WORK_TITLE.name()), manager.getPropertyValue(SAVE_UNSAVED_WORK.name()));

                }
            }catch(Exception e){
                ErrorDialog.getDialog().show(manager.getPropertyValue(INVALID_SAVE_TITLE.name()),manager.getPropertyValue(INVALID_SAVE.name()));

            }

        }
        else if (ConfirmationDialog.getDialog().getSelectedOption().equals(ConfirmationDialog.Option.NO)){
                applicationTemplate.getUIComponent().clear();
                ((AppUI)(applicationTemplate.getUIComponent())).setText("");
                ((AppUI)(applicationTemplate.getUIComponent())).temppane.getChildren().clear();
            }

        //applicationTemplate.getDialog(Dialog.DialogType.CONFIRMATION).show("Save", "do u you wanna save your work?     ");

        return true;
    }

    public HashSet getK2() {
        return k2;
    }

    // hash set do not add similar data no point of exception if not thrown.
    HashSet k2 ;

    public boolean checkLine(String textArea){
        String[] lines = textArea.split("\n");

        HashSet k= new HashSet();
        int i=0;
        boolean flag= true;
        for (i = 0; i < lines.length; i++) {
            String[] elements = lines[i].split("\t");
            k.add(elements[0]);
            if(k.size()<i+1){
                flag = false;
                break;
            }
        }
        if(flag == false){
            PropertyManager manager = applicationTemplate.manager;
            ErrorDialog.getDialog().show(manager.getPropertyValue(IDENTICAL_name.name()), manager.getPropertyValue(INDENTICAL_NAME_MSG.name())+(i+1)+"");
            return false;
        }
        k2 = new HashSet();

        for (i = 0; i < lines.length; i++) {
            String[] elements = lines[i].split("\t");
            k2.add(elements[1]);
        }
        try{
            ((AppUI) applicationTemplate.getUIComponent()).setMeta(lines.length, k2, Paths.get(selectedFile.getAbsolutePath()).toString());
        }
        catch(NullPointerException e) {
            ((AppUI) applicationTemplate.getUIComponent()).setMeta(lines.length, k2, " ");
        }

        return true;
    }
}
