package dataprocessors;

import com.sun.javafx.scene.paint.GradientUtils;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.shape.Rectangle;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * The data files used by this data visualization applications follow a tab-separated format, where each data point is
 * named, labeled, and has a specific location in the 2-dimensional X-Y plane. This class handles the parsing and
 * processing of such data. It also handles exporting the data to a 2-D plot.
 * <p>
 * A sample file in this format has been provided in the application's <code>resources/data</code> folder.
 *
 * @author Ritwik Banerjee
 * @see XYChart
 */
public final class TSDProcessor {

    public static class InvalidDataNameException extends Exception {

        private static final String NAME_ERROR_MSG = "All data instance names must start with the @ character.";

        public InvalidDataNameException(String name) {
            super(String.format("Invalid name '%s'." + NAME_ERROR_MSG, name));
        }
    }

    private  static Map<String, String>  dataLabels;
    private  static Map<String, Point2D> dataPoints;
    HashMap<Point2D, String> names;
    public TSDProcessor() {
        dataLabels = new HashMap<>();
        dataPoints = new HashMap<>();
        names = new HashMap<>();
    }

    public HashMap<Point2D, String> getNames(){
        return names;
    }

    /**
     * Processes the data and populated two {@link Map} objects with the data.
     *
     * @param tsdString the input data provided as a single {@link String}
     * @throws Exception if the input string does not follow the <code>.tsd</code> data format
     */
    public  void processString(String tsdString) throws Exception {
        AtomicBoolean hadAnError   = new AtomicBoolean(false);
        StringBuilder errorMessage = new StringBuilder();
        AtomicInteger counter=new AtomicInteger(0);
        Stream.of(tsdString.split("\n"))
              .map(line -> Arrays.asList(line.split("\t")))
              .forEach(list -> {
                  try {

                      if(!hadAnError.get()) {
                          counter.set(counter.get() + 1);
                          String name = checkedname(list.get(0));
                          String label = list.get(1);
                          String[] pair = list.get(2).split(",");
                          Point2D point = new Point2D(Double.parseDouble(pair[0]), Double.parseDouble(pair[1]));
                          dataLabels.put(name, label);
                          dataPoints.put(name, point);
                          names.put(point,name);
                      }
                  } catch (Exception e) {
                      if(e.getClass().getSimpleName().equals(ArrayIndexOutOfBoundsException.class.getSimpleName())){
                          errorMessage.setLength(0);
                          errorMessage.append("Error at line : "+(counter.get()));
                          hadAnError.set(true);
                      }
                      else {
                          errorMessage.setLength(0);
                          errorMessage.append(e.getClass().getSimpleName()).append(": ").append("Error at line : " + (counter.get())+" " + e.getMessage());
                          hadAnError.set(true);
                      }
                  }
              });
        if (errorMessage.length() > 0)
            throw new Exception(errorMessage.toString());
    }

    public double getMinX() {
        return minX;
    }

    public double getMaxX() {
        return maxX;
    }

    /**
     * Exports the data to the specified 2-D chart.
     *
     * @param chart the specified chart
     */
    double minX=0, maxX=0, sumY=0, average=0;
    int counter=0;
    public void toChartData(XYChart<Number, Number> chart) {
        Set<String> labels = new HashSet<>(dataLabels.values());
        for (String label : labels) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(label);
            dataLabels.entrySet().stream().filter(entry -> entry.getValue().equals(label)).forEach(entry -> {
                Point2D point = dataPoints.get(entry.getKey());
                series.getData().add(new XYChart.Data<>(point.getX(), point.getY()));
                sumY += point.getY();
                counter++;
                if(minX==0){
                    minX=point.getX();
                }
                if(point.getX()>maxX){
                    maxX=point.getX();
                }
                if(point.getX()<minX){
                    minX=point.getX();
                }

            });
            chart.getData().add(series);
            series.getNode().setStyle("-fx-stroke: transparent");
            for(XYChart.Series<Number, Number> s : chart.getData()){
               for(XYChart.Data<Number, Number> dat2: series.getData()) {
                    Point2D point = new Point2D((double) dat2.getXValue(),(double) dat2.getYValue());
                    String name = names.get(point);
                    Tooltip toolTip = new Tooltip();
                    toolTip.setText(name);
                    Tooltip.install(dat2.getNode(), toolTip);
                    dat2.getNode().setCursor(Cursor.CLOSED_HAND);

               }
            }
        }
        average=sumY/counter;
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        XYChart.Data tata1 = new XYChart.Data<>(minX,average);
        XYChart.Data tata2 = new XYChart.Data<>(maxX,average);
        series.setName("Average");
        Rectangle rectangle = new Rectangle(0,0);
        rectangle.setVisible(false);
        Rectangle rectangle1 = new Rectangle(0,0);
        rectangle1.setVisible(false);
        tata1.setNode(rectangle);
        tata2.setNode(rectangle1);
        series.getData().addAll(tata1,tata2);
//        chart.getData().add(series);
    }

    public void clear() {
        dataPoints.clear();
        dataLabels.clear();
        minX=0;
        maxX=0;
        sumY=0;
        counter=0;
        average=0;
        names.clear();
    }

    private static String checkedname(String name) throws InvalidDataNameException {
        if (!name.startsWith("@"))
            throw new InvalidDataNameException(name);
        return name;
    }
}
