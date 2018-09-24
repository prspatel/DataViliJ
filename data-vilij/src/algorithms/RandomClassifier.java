package algorithms;


import dataprocessors.AppData;
import dataprocessors.DataSet;
import dataprocessors.TSDProcessor;
import javafx.application.Platform;
import javafx.scene.chart.XYChart;
import javafx.scene.shape.Rectangle;
import ui.AppUI;
import vilij.templates.ApplicationTemplate;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Ritwik Banerjee
 */
public class RandomClassifier extends Classifier {

    private static final Random RAND = new Random();
    private ApplicationTemplate applicationTemplate;
    @SuppressWarnings("FieldCanBeLocal")
    // this mock classifier doesn't actually use the data, but a real classifier will
    private DataSet dataset;

    private final int maxIterations;
    private final int updateInterval;
    private double minY,maxY;
    XYChart.Data tata1;
    XYChart.Data tata2;

    // currently, this value does not change after instantiation
    private final AtomicBoolean tocontinue;

    @Override
    public int getMaxIterations() {
        return maxIterations;
    }

    @Override
    public int getUpdateInterval() {
        return updateInterval;
    }

    @Override
    public boolean tocontinue() {
        return tocontinue.get();
    }

    public RandomClassifier(DataSet dataset,
                            int maxIterations,
                            int updateInterval,
                            boolean tocontinue) {
        this.dataset = dataset;
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.tocontinue = new AtomicBoolean(tocontinue);
    }

    public RandomClassifier(DataSet dataset,
                            int maxIterations,
                            int updateInterval,
                            boolean tocontinue, ApplicationTemplate applicationTemplate) {
        this.dataset = dataset;
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.tocontinue = new AtomicBoolean(tocontinue);
        this.applicationTemplate = applicationTemplate;
        this.k=1;
        Platform.runLater(this::createLine);
        //((AppUI)applicationTemplate.getUIComponent()).getChart().getData().add(series);
    }
    int k;
    @Override
    public void run() {
        k=0;
        for (int i = 1; i <= maxIterations && tocontinue(); i++) {

            int xCoefficient =  new Long(-1 * Math.round((2 * RAND.nextDouble() - 1) * 10)).intValue();
            int yCoefficient = 10;
            int constant     = RAND.nextInt(11);

            if (i % updateInterval == 0) {
                output = Arrays.asList(xCoefficient, yCoefficient, constant);
                Platform.runLater(this::setYvalues);
                System.out.printf("Iteration number %d: ", i); //
                flush();
            }
            if (i > maxIterations * .6 && RAND.nextDouble() < 0.05) {
                System.out.printf("Iteration number %d: ", i);
                Platform.runLater(this::setYvalues);
                flush();
                break;
            }
            try {
                Thread.sleep(350);
            } catch (InterruptedException e) {
            }

        }
        while(tocontinue()==false&&k< maxIterations){

            int xCoefficient =  new Long(-1 * Math.round((2 * RAND.nextDouble() - 1) * 10)).intValue();
            int yCoefficient = 10;
            int constant     = RAND.nextInt(11);

            if (k % updateInterval == 0) {
                output = Arrays.asList(xCoefficient, yCoefficient, constant);
                ((AppUI)applicationTemplate.getUIComponent()).getScrnshotButton().setDisable(true);
                Platform.runLater(this::setYvalues);
                System.out.printf("Iteration number %d: ", k); //
                flush();
            }
            if (k > maxIterations * .6 && RAND.nextDouble() < 0.05) {
                System.out.printf("Iteration number %d: ", k);
                ((AppUI)applicationTemplate.getUIComponent()).getScrnshotButton().setDisable(true);
                Platform.runLater(this::setYvalues);
                flush();
            }
            try {
                ((AppUI)applicationTemplate.getUIComponent()).getScrnshotButton().setDisable(false);
                ((AppUI)applicationTemplate.getUIComponent()).getRun().setDisable(false);
                Thread.sleep(Integer.MAX_VALUE);
            } catch (InterruptedException e) {

            }


            k++;
        }
        ((AppUI)applicationTemplate.getUIComponent()).getScrnshotButton().setDisable(false);
        ((AppUI)applicationTemplate.getUIComponent()).setRunning(false);
        ((AppUI)applicationTemplate.getUIComponent()).getRun().setDisable(false);
    }

    // for internal viewing only
    protected void flush() {
        System.out.printf("%d\t%d\t%d%n", output.get(0), output.get(1), output.get(2));
    }

    /** A placeholder main method to just make sure this code runs smoothly */
    public static void main(String... args) throws IOException {
        DataSet          dataset    = DataSet.fromTSDFile(Paths.get("/path/to/some-data.tsd"));
        RandomClassifier classifier = new RandomClassifier(dataset, 100, 5, true);
        classifier.run(); // no multithreading yet
    }

    public static XYChart.Series<Number, Number> getSeries() {
        return series;
    }

    public static XYChart.Series<Number, Number> series = new XYChart.Series<>();
    public void createLine(){
        clear();
        TSDProcessor processor = ((AppData)applicationTemplate.getDataComponent()).getProcessor();
        tata1 = new XYChart.Data<>(processor.getMinX(),minY);
        tata2 = new XYChart.Data<>(processor.getMaxX(),maxY);
        series.setName("Line");
        Rectangle rectangle = new Rectangle(0,0);
        rectangle.setVisible(false);
        Rectangle rectangle1 = new Rectangle(0,0);
        rectangle1.setVisible(false);
        tata1.setNode(rectangle);
        tata2.setNode(rectangle1);
        series.getData().addAll(tata1,tata2);
        ((AppUI)applicationTemplate.getUIComponent()).getChart().getData().add(series);
    }
    public void setYvalues(){
        TSDProcessor processor = ((AppData)applicationTemplate.getDataComponent()).getProcessor();
        maxY = ((-output.get(2)-(output.get(0)*processor.getMaxX())/output.get(1)));
        minY = ((-output.get(2)-(output.get(0)*processor.getMinX())/output.get(1)));
        tata2.setYValue(maxY);tata1.setYValue(minY);
    }
    public void clear(){
        ((AppUI)applicationTemplate.getUIComponent()).getChart().getData().remove(series);
    }
}
