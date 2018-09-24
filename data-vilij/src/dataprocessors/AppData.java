package dataprocessors;

import actions.AppActions;
import ui.AppUI;
import vilij.components.DataComponent;
import vilij.components.ErrorDialog;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import static settings.AppPropertyTypes.*;


/**
 * This is the concrete application-specific implementation of the data component defined by the Vilij framework.
 *
 * @author Ritwik Banerjee
 * @see DataComponent
 */
public class AppData implements DataComponent {


    public TSDProcessor getProcessor() {
        return processor;
    }

    private   TSDProcessor        processor;
    private   ApplicationTemplate applicationTemplate;

    public AppData(ApplicationTemplate applicationTemplate) {
        this.processor = new TSDProcessor();
        this.applicationTemplate = applicationTemplate;
    }

    public ArrayList<String> getLineslist() {
        return lineslist;
    }


    private ArrayList<String> lineslist= new ArrayList<>();
    @Override
    public void loadData(Path dataFilePath) {
        // TODO: NOT A PART OF HW 1
        processor.clear();
        File file = new File(dataFilePath.toString());

        try{
            String entireFileText = new Scanner(file)
                    .useDelimiter("\\A").next();
            String[] lines = entireFileText.split("\n");
            for(int i=0; i < lines.length; i++) {
                lineslist.add(lines[i]);
            }
            String ten="";
            if(lineslist.size()>10){
                ErrorDialog.getDialog().show("Large file", "Loaded data consists of "+lineslist.size()+" showing only the first 10 in the textarea");
                for (int i =0;i<10;i++){
                    if(i==9){
                        ten += lineslist.remove(0);

                    }
                    else
                        ten += lineslist.remove(0)+ "\n";

                }
            }
            else{
                ten=entireFileText;
                lineslist.clear();
            }
            ((AppUI)(applicationTemplate.getUIComponent())).setTextArea(ten);

            loadData(entireFileText);
            ((AppUI)applicationTemplate.getUIComponent()).getDone().setDisable(true);


        }catch(Exception e){
            ErrorDialog.getDialog().show("Error", "Incorrect file type/File not found/empty file");
        }
    }

    public void loadData(String dataString) {
        // TODO for homework 1
        try {

            boolean k = ((AppActions)(applicationTemplate.getActionComponent())).checkLine(dataString);
            if(k== true) {
                processor.processString(dataString);
                displayData();
                ((AppUI)applicationTemplate.getUIComponent()).getScrnshotButton().setDisable(false);
                ((AppUI)applicationTemplate.getUIComponent()).getAcTextArea().setDisable(true);
                ((AppUI)applicationTemplate.getUIComponent()).algoVbox();
            }
        } catch (Exception e) {
            PropertyManager manager = applicationTemplate.manager;
            ErrorDialog d = ErrorDialog.getDialog();
            d.setWidth(800);
            d.setHeight(200);
            d.show(manager.getPropertyValue(INVALID_TEXTAREA_TITLE.name()), e.getMessage());

        }

    }

    @Override
    public void saveData(Path dataFilePath) {
        // TODO: NOT A PART OF HW 1
        try {
            FileWriter writer = new FileWriter(dataFilePath.toString());
            writer.write(((AppUI) (applicationTemplate.getUIComponent())).getTextArea());
            writer.close();

        } catch (IOException e) {
            PropertyManager manager = applicationTemplate.manager;
            ErrorDialog.getDialog().show(manager.getPropertyValue(SAVE_UNSAVED_WORK_TITLE.name()), manager.getPropertyValue(SAVE_UNSAVED_WORK.name()));
        }
    }

    @Override
    public void clear() {
        processor.clear();
    }

    public  void displayData() {
        processor.toChartData(((AppUI) applicationTemplate.getUIComponent()).getChart());
    }

}
