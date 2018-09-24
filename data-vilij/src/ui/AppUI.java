package ui;

import actions.AppActions;
import algorithms.Clusterer;
import algorithms.KMeansClusterer;
import algorithms.RandomClassifier;
import com.sun.org.apache.xerces.internal.util.SAXLocatorWrapper;
import dataprocessors.AppData;

import dataprocessors.DataSet;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import vilij.components.ErrorDialog;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;
import vilij.templates.UITemplate;


import javax.xml.crypto.Data;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;

import static settings.AppPropertyTypes.*;
import static vilij.settings.PropertyTypes.*;

/**
 * This is the application's user interface implementation.
 *
 * @author Ritwik Banerjee
 */
public final class AppUI extends UITemplate {

    /** The application to which this class of actions belongs. */
    ApplicationTemplate applicationTemplate;
    private static final String SEPARATOR = "/";
    @SuppressWarnings("FieldCanBeLocal")
    private Button                       scrnshotButton; // toolbar button to take a screenshot of the data
    private Button                       settingsButton;
    private LineChart<Number, Number> chart;          // the chart where data will be displayed
    private String chartcssPath;
    String sametext ="";
    private TextArea                     textArea = new TextArea();       // text area for new data input
    private boolean                      hasNewText;     // whether or not the text area has any new data since last display
    private CheckBox clustering = new CheckBox("Clustering");
    private CheckBox classifying = new CheckBox("Classifying");
    ArrayList<Button> buttons = new ArrayList<>(10);
    private RandomClassifier randomClassifier;
    private  VBox algoV= new VBox();
    public VBox temppane;
    private     Label metaData = new Label();
    private ToggleButton done = new ToggleButton("Done");
    Image image1;
    ImageView selectedImage;
    private String[] classConList  = {"1","1","false"};
    private String[] clusConList  = {"1","1","false","1"};
    private Button back = new Button("Back <<");
    private Thread thread;
    private Button run = new Button("Run");
    private ArrayList<CheckBox> radioList = new ArrayList<>(10);
    private boolean isRunning=false;

    public void setDataset(DataSet dataset) {
        this.dataset = dataset;
    }

    private DataSet dataset = new DataSet();




    public void setHasNewText(boolean hasNewText) {
        this.hasNewText = hasNewText;
    }

    public Button getRun() {
        return run;
    }

    public ToggleButton getDone() {
        return done;
    }
    public Button getScrnshotButton() {
        return scrnshotButton;
    }
    public TextArea getAcTextArea() {
        return textArea;
    }

    public String getTextArea() {
        return textArea.getText();
    }

    public void setText(String text) {
        this.sametext = text;
    }

    public void setTextArea(String textset) {
         textArea.setText(textset);

    }
    public LineChart<Number, Number> getChart() { return chart; }

    public AppUI(Stage primaryStage, ApplicationTemplate applicationTemplate) {
        super(primaryStage, applicationTemplate);
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    protected void setResourcePaths(ApplicationTemplate applicationTemplate) {
        super.setResourcePaths(applicationTemplate);
    }

    @Override
    protected void setToolBar(ApplicationTemplate applicationTemplate) {
        // TODO for homework 1
        PropertyManager manager = applicationTemplate.manager;

        newButton = setToolbarButton(newiconPath, manager.getPropertyValue(NEW_TOOLTIP.name()), false);
        saveButton = setToolbarButton(saveiconPath, manager.getPropertyValue(SAVE_TOOLTIP.name()), true);
        loadButton = setToolbarButton(loadiconPath, manager.getPropertyValue(LOAD_TOOLTIP.name()), false);
        printButton = setToolbarButton(printiconPath, manager.getPropertyValue(PRINT_TOOLTIP.name()), true);
        exitButton = setToolbarButton(exiticonPath, manager.getPropertyValue(EXIT_TOOLTIP.name()), false);
        String iconsPath = SEPARATOR + String.join(SEPARATOR,
                manager.getPropertyValue(GUI_RESOURCE_PATH.name()),
                manager.getPropertyValue(ICONS_RESOURCE_PATH.name()));


        String scrnpath = String.join(SEPARATOR, iconsPath, manager.getPropertyValue(SCREENSHOT_ICON.name()));
        scrnshotButton = setToolbarButton(scrnpath,manager.getPropertyValue(SCREENSHOT_TOOLTIP.name()),true);
        toolBar = new ToolBar(newButton, saveButton, loadButton, printButton, exitButton,scrnshotButton);

    }

    @Override
    protected void setToolbarHandlers(ApplicationTemplate applicationTemplate) {
        applicationTemplate.setActionComponent(new AppActions(applicationTemplate));
        newButton.setOnAction(e -> applicationTemplate.getActionComponent().handleNewRequest());
        saveButton.setOnAction(e -> applicationTemplate.getActionComponent().handleSaveRequest());
        loadButton.setOnAction(e -> applicationTemplate.getActionComponent().handleLoadRequest());
        exitButton.setOnAction(e -> applicationTemplate.getActionComponent().handleExitRequest());
        printButton.setOnAction(e -> applicationTemplate.getActionComponent().handlePrintRequest());
        scrnshotButton.setOnAction(e ->{
            try{
                ((AppActions)applicationTemplate.getActionComponent()).handleScreenshotRequest();
            }catch(IOException ex){

            }
        });

    }

    @Override
    public void initialize() {
        layout();
        setWorkspaceActions();
    }

    @Override
    public void clear() {
        // TODO for homework 1
        textArea.clear();
        textArea.setDisable(false);
        //((AppData)(applicationTemplate.getUIComponent())).clear();
        done.setSelected(false);
        done.setDisable(false);
        clustering.setSelected(false);
        classifying.setSelected(false);
        chart.getData().clear();
        saveButton.setDisable(true);
        scrnshotButton.setDisable(true);
        classConList= new String[3];
        clusConList = new String[3];
    }

    private void layout() {
        // TODO for homework 1
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        chart = new LineChart<>(xAxis,yAxis);
        xAxis.forceZeroInRangeProperty().set(false);
        yAxis.forceZeroInRangeProperty().set(false);
        chart.setHorizontalGridLinesVisible(false);
        chart.setVerticalGridLinesVisible(false);
        PropertyManager manager= applicationTemplate.manager;
        //jk k v
        chartcssPath = SEPARATOR+ String.join(SEPARATOR,
                manager.getPropertyValue(GUI_RESOURCE_PATH1.name()),
                manager.getPropertyValue(CSS_RESOURCE_PATH1.name()),
                manager.getPropertyValue(CSS_RESOURCE_FILENAMES.name()));
        chart.getStylesheets().add(chartcssPath);
        chart.setHorizontalZeroLineVisible(false);
        chart.setVerticalZeroLineVisible(false);
        chart.setTitle("Plot");
        chart.setAnimated(false);
        workspace =  new HBox();
        temppane=new VBox();
        temppane.setPrefHeight(500);
        temppane.setPrefWidth(450);
        workspace.getChildren().add(temppane);
        workspace.getChildren().add(getChart());
        appPane.getChildren().add(workspace);
    }
    public void algoVbox(){
        algoV.getChildren().clear();
        Label algoType = new Label("Algorithm Type");
        algoV.getChildren().addAll(algoType,classifying,clustering);
        algoV.setSpacing(10);
        algoType.setFont(new Font(20));
    }
    public void createVBox(){
        temppane.getChildren().clear();
        temppane.setSpacing(20);
        Label label = new Label("Data File");
        label.setFont(new Font(20));
        label.setMaxWidth(100);
        label.setMaxHeight(70);
        label.setAlignment(Pos.CENTER);
        textArea.setMaxHeight(200);
        textArea.setMaxWidth(300);
        metaData = new Label();
        algoV = new VBox();
        metaData.setFont(new Font(10));
        HBox dispBox = new HBox();
        dispBox.getChildren().addAll(done);
        dispBox.setSpacing(10);
        temppane.getChildren().addAll(label,textArea, dispBox, metaData, algoV);
    }

    public boolean isHasNewText() {
        return hasNewText;
    }

    private void setWorkspaceActions() {
        // TODO for homework 1
        classifying.setOnAction(e ->{
            int instances = ((AppActions)applicationTemplate.getActionComponent()).getK2().size();
            if(instances==2){
                classiFig();
            }
            else{
                ErrorDialog.getDialog().show("Classification Not possibble", " data should be of 2 labels");
                classifying.setSelected(false);
            }
        });
        clustering.setOnAction(e->{
            clustFig();
        });

        textArea.textProperty().addListener(e ->{
            String[] lines = textArea.getText().split("\n");
            if(lines.length<10&&((AppData)(applicationTemplate.getDataComponent())).getLineslist().size()>0){
                textArea.appendText("\n"+((AppData)(applicationTemplate.getDataComponent())).getLineslist().remove(0));

            }
        });

        textArea.setOnKeyReleased(e ->{
            newButton.setDisable(false);
            saveButton.setDisable(false);
            if(textArea.getLength()==0){
                saveButton.setDisable(true);
                newButton.setDisable(true);
            }
        });
        done.setOnAction(e ->{
            if(textArea.getLength()!=0&& done.isSelected()){
                hasNewText = true;
                if(sametext.equals(textArea.getText())){
                    hasNewText=false;
                }
                if(hasNewText){
                    if(textArea.getLength()==0){
                        PropertyManager manager = applicationTemplate.manager;
                        chart.getData().clear();
                        ((AppData) applicationTemplate.getDataComponent()).clear();
                        ErrorDialog.getDialog().show(manager.getPropertyValue(INVALID_TEXTAREA_MSG.name()),manager.getPropertyValue(INVALID_TEXTAREA_TITLE.name()));
                    }
                    else {
                        classifying.setSelected(false);
                        clustering.setSelected(false);
                        chart.getData().clear();
                        (applicationTemplate.getDataComponent()).clear();
                        ((AppData) applicationTemplate.getDataComponent()).loadData(textArea.getText());
                        sametext = textArea.getText();
                    }
                }
                else{

                    PropertyManager manager = applicationTemplate.manager;
                    if(textArea.getLength()==0){
                        ErrorDialog.getDialog().show(manager.getPropertyValue(INVALID_TEXTAREA_MSG.name()),manager.getPropertyValue(INVALID_TEXTAREA_TITLE.name()));
                        scrnshotButton.setDisable(true);
                    }else{
                        ErrorDialog.getDialog().show(manager.getPropertyValue(SAME_DATA_TITLE.name()),manager.getPropertyValue(SAME_DATA_MSG.name()));

                    }
                }

            }
            else if(!done.isSelected()){
                textArea.setDisable(false);
            }
            else if (textArea.getLength()==0){
                ErrorDialog.getDialog().show("Error", "Cannot lock as the text Area is empty");
            }
        });

    }
    public void classiFig(){
        algoV.getChildren().clear();
        HBox temp = new HBox();
        VBox temp2 = new VBox();
        VBox temp3 = new VBox();

        Label classification =  new Label("Classification");
        classification.setFont( new Font(20));

        String s = "";

        try{
            s= new String(Files.readAllBytes(Paths.get("classifying.txt")));
        }catch(IOException e){

        }

        String[] textfile = s.split("\n");
        for (int i = 0; i <textfile.length ; i++) {
            PropertyManager manager = applicationTemplate.manager;
            String iconsPath = SEPARATOR + String.join(SEPARATOR,
                    manager.getPropertyValue(GUI_RESOURCE_PATH.name()),
                    manager.getPropertyValue(ICONS_RESOURCE_PATH.name()));

            radioList.add(i, new CheckBox(textfile[i]));
            String settPath = String.join(SEPARATOR, iconsPath, manager.getPropertyValue(SETTINGS_ICON.name()));
            image1 = new Image(settPath);

            selectedImage = new ImageView(image1);
            settingsButton = new Button(null, selectedImage);
            selectedImage.setFitHeight(18);
            selectedImage.setFitWidth(18);

            final int tempo = i;
            settingsButton.setOnAction(e2 -> {
                if (radioList.get(tempo).isSelected()) {
                    configWindow(1);
                }
            });
            temp = new HBox();
            temp.getChildren().addAll(radioList.get(i), settingsButton);
            temp3.getChildren().add(temp);
        }


        back.setOnAction(e->{
            algoVbox();
        });


        temp2.getChildren().addAll(classification,temp3,back);
        algoV.getChildren().addAll(temp2);

    }
    public void clustFig(){
        algoV.getChildren().clear();
        HBox temp;
        VBox temp3 = new VBox();
        VBox temp2 = new VBox();
        Label clusttering =  new Label("Clustering");
        clusttering.setFont( new Font(20));

        String s = "";

        try{
            s= new String(Files.readAllBytes(Paths.get("clustering.txt")));
        }catch(IOException e){

        }

        String[] textfile = s.split("\n");
        for (int i = 0; i <textfile.length ; i++) {

            PropertyManager manager = applicationTemplate.manager;
            String iconsPath = SEPARATOR + String.join(SEPARATOR,
                    manager.getPropertyValue(GUI_RESOURCE_PATH.name()),
                    manager.getPropertyValue(ICONS_RESOURCE_PATH.name()));

            String settPath = String.join(SEPARATOR, iconsPath, manager.getPropertyValue(SETTINGS_ICON.name()));
            image1 = new Image(settPath);
            radioList.add(i, new CheckBox(textfile[i]));
            selectedImage= new ImageView(image1);
            settingsButton = new Button(null, selectedImage);
            final int tempo = i;
            selectedImage.setFitHeight(18);
            selectedImage.setFitWidth(18);
            settingsButton.setOnAction(e2 -> {
                if (radioList.get(tempo).isSelected()) {
                    configWindow(2);
                }

            });
            temp=new HBox();
            temp.getChildren().addAll(radioList.get(i), settingsButton);
            temp3.getChildren().add(temp);

        }

        back.setOnAction(e->{
            algoVbox();
        });

        temp2.getChildren().addAll(clusttering,temp3,back);
        algoV.getChildren().addAll(temp2);
    }


    public Thread getThread() {
        return thread;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public void configWindow(int i){

        Label secondLabel = new Label("Configuration");
        secondLabel.setFont(new Font(20));
        Pane secondaryLayout = new VBox(10);
        HBox hBox1 = new HBox();
        Label mIterations = new Label("Max Iteration: ");
        TextField t1 = new TextField();
        TextField t2 = new TextField();
        CheckBox cRun = new CheckBox("Continuous Run?");
        hBox1.getChildren().addAll(mIterations,t1);
        HBox hBox2 = new HBox();
        Label updateInterval = new Label("Update Interval:  ");
        hBox2.getChildren().addAll(updateInterval,t2);
        HBox hBox3 = new HBox();
        HBox hBox4 = new HBox();
        Label clusters = new Label("Clusters ");
        TextField t3 = new TextField();

        hBox3.getChildren().addAll(cRun);
        if(i==1){


                t1.setText(classConList[0]);
                t2.setText(classConList[1]);
                if (classConList[2] == "false") {
                    cRun.setSelected(false);
                } else
                    cRun.setSelected(true);


        }
        else if (i==2){
            t1.setText(clusConList[0]);
            t2.setText(clusConList[1]);
            t3.setText(clusConList[3]);
            if (clusConList[2] == "false") {
                cRun.setSelected(false);
            } else
                cRun.setSelected(true);

        }

        run.setOnAction(e->{
            if(i==1) {

                if (isRunning) {
                    try {
                        scrnshotButton.setDisable(true);
                        run.setDisable(true);
                        thread.interrupt();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
                else{

                    selectedCheck(1);
                    getChart().getData().remove(RandomClassifier.getSeries());
                    try {
                        scrnshotButton.setDisable(true);
                        run.setDisable(true);
                        thread.start();
                        setRunning(true);
                    } catch (Exception el) {
                        el.printStackTrace();
                    }
                }
            }
            else{
                if(isRunning){
                    scrnshotButton.setDisable(true);
                    run.setDisable(true);
                    thread.interrupt();
                }
                else {

                    selectedCheck(2);
                    scrnshotButton.setDisable(true);
                    run.setDisable(true);
                    thread.start();
                    setRunning(true);
                }
            }
        });

        secondaryLayout.getChildren().addAll(secondLabel, hBox1,hBox2, hBox3);
        if(i==2){

            hBox4.getChildren().addAll(clusters, t3);
            secondaryLayout.getChildren().add(hBox4);
        }
        Scene secondScene = new Scene(secondaryLayout, 350, 300);
        Stage newWindow = new Stage();
        newWindow.setMaxHeight(400);
        newWindow.setMaxWidth(500);
        newWindow.setTitle("Second Stage");
        newWindow.setScene(secondScene);
        newWindow.showAndWait();
        try {
            if(i==1) {
                classConList[0]=t1.getText();
                classConList[1]=t2.getText();
                classConList[2]=cRun.isSelected()+"";
                Integer.parseInt(t1.getText());
                Integer.parseInt(t2.getText());
//                selectedCheck(1);
                if (!algoV.getChildren().contains(run)) {
                    algoV.getChildren().add(run);
                }
            }
            else{
                clusConList[0]=t1.getText();
                clusConList[1]=t2.getText();
                clusConList[3]=t3.getText();
                clusConList[2]=cRun.isSelected()+"";
                selectedCheck(2);
                KMeansClusterer tempClusterer = new KMeansClusterer(dataset, Integer.parseInt(t1.getText()), Integer.parseInt(t2.getText()), Integer.parseInt(t3.getText()), cRun.isSelected(),  applicationTemplate);
                if (!algoV.getChildren().contains(run)) {
                    algoV.getChildren().add(run);
                }
            }
        }catch(NumberFormatException e) {
            if(i==1) {
                classConList[0]=1+"";
                classConList[1]=1+"";
                classConList[2]="false";
//                selectedCheck(1);
                if (!algoV.getChildren().contains(run)) {
                    algoV.getChildren().add(run);
                }
            }
            else{
                clusConList[0]=1+"";
                clusConList[1]=1+"";
                clusConList[3]=1+"";
                clusConList[2]="false";
                selectedCheck(2);
                if (!algoV.getChildren().contains(run)) {
                    algoV.getChildren().add(run);
                }
            }
        }

    }

    public void setMeta(int instances, HashSet<String> labels, String filePath){
        String k ="";

        for (String label: labels){
            k+=label+", ";
        }
        k.substring(0, k.length()-1);
        metaData.setText( "The total number of instances are "+instances+"\n the total number of labels are "+labels.size()+"\n the labels are" +
                ": "+k+"\n the source path is "+filePath);

    }

    public void selectedCheck(int i){
        String bame ="";
        for(CheckBox c: radioList) {
            if(c.isSelected()){
                bame=c.getText();
                break;
            }
        }
        try{
            if(i==2) {
                boolean k;
                if (clusConList[2].equals("false")) {
                    k = false;
                } else {
                    k = true;
                }
                thread = new Thread((Runnable) Class.forName("algorithms." + bame.replaceAll("\r", "")).getDeclaredConstructor(DataSet.class, int.class, int.class, int.class,
                        boolean.class, ApplicationTemplate.class).newInstance(dataset, Integer.parseInt(clusConList[0]), Integer.parseInt(clusConList[1]), Integer.parseInt(clusConList[3]), k, applicationTemplate));
            }
            else{
                boolean k;
                if (classConList[2].equals("false")) {
                    k = false;
                } else {
                    k = true;
                }
                thread = new Thread((Runnable) Class.forName("algorithms." + bame.replaceAll("\r", "")).getDeclaredConstructor(DataSet.class, int.class, int.class,
                        boolean.class, ApplicationTemplate.class).newInstance(dataset, Integer.parseInt(classConList[0]), Integer.parseInt(classConList[1]), k, applicationTemplate));
            }
        }catch(Exception e){

        }

    }
    public static void testingCase3(String iterations, String maxIntervals, String clusters) throws NumberFormatException{
        int intIterations = Integer.parseInt(iterations);
        int intMax = Integer.parseInt(maxIntervals);
        int intCluster = Integer.parseInt(clusters);
    }
}
