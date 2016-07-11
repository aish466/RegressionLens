package regressionlensv1;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.List;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.event.AxisChangeListener;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.function.Function2D;
import org.jfree.data.function.LineFunction2D;
import org.jfree.data.function.PolynomialFunction2D;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.statistics.Regression;
import org.jfree.data.xy.*;

/**
 * Scatter Plot class
 *
 * @author Lin Shao
 *
 */
public class ScatterPlot extends JPanel implements ChartMouseListener, MouseListener, AxisChangeListener {

    private String title;
    private String path = "./scatterPlots/Clustering/";
    private JFreeChart chart;
    private ChartPanel chartPanel;
    private XYSeriesCollection dataset = new XYSeriesCollection();;
    private String xAxisName;
    private String yAxisName;
    private int selectionXstart;
    private int selectionYstart;
    private int selectionXend;
    private int selectionYend;
    private ArrayList<Double> xData;
    private ArrayList<Double> yData;
    
    /**
     * Creates a new scatter plot.
     *
     * @param title the frame title.
     * @param x the first dimension.
     * @param y the second dimension.
     * @param data dataset.
     */
    public ScatterPlot(final String title, String x, String y, double[][] data, String destPath) {

        super();
        xAxisName = x;
        yAxisName = y;
        path = destPath;

        this.dataset = setData(data);
        //addData();
        chart = createChart(0);
        storeScatterPlot(chart, title);
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(250, 125));
        chartPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        chartPanel.setDomainZoomable(false);
        chartPanel.setRangeZoomable(false);
        chartPanel.setName(title);
        chartPanel.addMouseListener(this);

        setLayout(new BorderLayout());
        this.add(chartPanel, BorderLayout.NORTH);
    }

    public ScatterPlot(final String title, String x, String y, ArrayList<Double> xData, ArrayList<Double> yData, String destPath) {

        super();
        xAxisName = x;
        yAxisName = y;
        path = destPath;

        this.dataset = setData(xData, yData);
        this.xData = xData;
        this.yData = yData;
//        addData();
        chart = createChart(0);
        
        XYPlot plot = (XYPlot) chart.getPlot();
        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
        
        xAxis.setLowerBound(getMinValue(xData));
        xAxis.setUpperBound(getMaxValue(xData));
        yAxis.setLowerBound(getMinValue(yData));
        yAxis.setUpperBound(getMaxValue(yData));
        
//        ArrayList<Point> myList = new ArrayList<Point>();
//        
//        myList.add(new Point(12, 15));
//        
//        myList.get(0).getX();
//        
//        storeScatterPlot(chart, title);
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 400));
        chartPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        chartPanel.setDomainZoomable(false);
        chartPanel.setRangeZoomable(false);
        chartPanel.setName(title);
        chartPanel.addMouseListener(this);
        
//        this works
//        JPanel panel = new JPanel();
//        panel.add(chartPanel);
//        add(panel);
        
        
//          setLayout(new BorderLayout());
//          this.add(chartPanel, BorderLayout.CENTER);
//        add(new JLabel("Hello"), BorderLayout.NORTH);
        
    }
    
    //LINE WITH SCATTERPLOT
        public ScatterPlot(final String title, String x, String y, ArrayList<Double> xData, ArrayList<Double> yData, String destPath, double x1, double y1, double x2, double y2, ArrayList<Double> rectCoord, ArrayList<Double> regLineCoord) {

        super();
        xAxisName = x;
        yAxisName = y;
        this.xData = xData;
        this.yData = yData;
        path = destPath;
        
         // scatter plot
         XYSeries series = new XYSeries("Scatter Plot");
         int l = xData.size();
         for(int i = 0 ; i < l ; i++){
             series.add(xData.get(i), yData.get(i));
         }
         dataset.addSeries(series);
         
         // line plot
         XYSeries lineData = new XYSeries("Line Plot");
         lineData.add(x1, y1);
         lineData.add(x2, y2);
         dataset.addSeries(lineData);
         
         // Saved Regressions
         int numberOfSavedRegressions = rectCoord.size() / 5;
         savedRegressions(rectCoord, regLineCoord, numberOfSavedRegressions);
        chart = createChart(numberOfSavedRegressions);
        
        XYPlot plot = (XYPlot) chart.getPlot();
        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
        
        xAxis.setLowerBound(getMinValue(xData));
        xAxis.setUpperBound(getMaxValue(xData));
        yAxis.setLowerBound(getMinValue(yData));
        yAxis.setUpperBound(getMaxValue(yData));
        
//        ArrayList<Point> myList = new ArrayList<Point>();
//        
//        myList.add(new Point(12, 15));
//        
//        myList.get(0).getX();
//        
//        storeScatterPlot(chart, title);
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 400));
        chartPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        chartPanel.setDomainZoomable(false);
        chartPanel.setRangeZoomable(false);
        chartPanel.setName(title);
        chartPanel.addMouseListener(this);
        
//        this works
//        JPanel panel = new JPanel();
//        panel.add(chartPanel);
//        add(panel);
        
        
//          setLayout(new BorderLayout());
//          this.add(chartPanel, BorderLayout.CENTER);
//        add(new JLabel("Hello"), BorderLayout.NORTH);
        
    }
        
        //QUADRATIC CURVE WITH SCATTERPLOT
        public ScatterPlot(final String title, String x, String y, ArrayList<Double> xData, ArrayList<Double> yData, String destPath, double startX, double endX, double a, double b, double c, ArrayList<Double> rectCoord, ArrayList<Double> regLineCoord) {

        super();
        xAxisName = x;
        yAxisName = y;
        this.xData = xData;
        this.yData = yData;
        path = destPath;
        
         // scatter plot
         XYSeries series = new XYSeries("Scatter Plot");
         int l = xData.size();
         for(int i = 0 ; i < l ; i++){
             series.add(xData.get(i), yData.get(i));
         }
         dataset.addSeries(series);
         
         // Quadratic plot
         XYSeries quadData = new XYSeries("Quad Plot") {};
         double[] array = {c, b, a};
        Function2D p = new PolynomialFunction2D(array);
        
        if(startX < endX){
        quadData =  DatasetUtilities.sampleFunction2DToSeries(
            p, startX, endX, 50, "y = " + a + "x² +" + b + "x +" + c);
         dataset.addSeries(quadData);
        }
        else{
        quadData =  DatasetUtilities.sampleFunction2DToSeries(
            p, endX, startX, 50, "y = " + a + "x² +" + b + "x +" + c);
         dataset.addSeries(quadData);
        }
         
         // Saved Regressions
         int numberOfSavedRegressions = rectCoord.size() / 5;
         savedRegressions(rectCoord, regLineCoord, numberOfSavedRegressions);
//        addData();
        chart = createChart(numberOfSavedRegressions);
        
        XYPlot plot = (XYPlot) chart.getPlot();
        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
        
        xAxis.setLowerBound(getMinValue(xData));
        xAxis.setUpperBound(getMaxValue(xData));
        yAxis.setLowerBound(getMinValue(yData));
        yAxis.setUpperBound(getMaxValue(yData));
        
//        ArrayList<Point> myList = new ArrayList<Point>();
//        
//        myList.add(new Point(12, 15));
//        
//        myList.get(0).getX();
//        
//        storeScatterPlot(chart, title);
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 400));
        chartPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        chartPanel.setDomainZoomable(false);
        chartPanel.setRangeZoomable(false);
        chartPanel.setName(title);
        chartPanel.addMouseListener(this);
        
//        this works
//        JPanel panel = new JPanel();
//        panel.add(chartPanel);
//        add(panel);
        
        
//          setLayout(new BorderLayout());
//          this.add(chartPanel, BorderLayout.CENTER);
//        add(new JLabel("Hello"), BorderLayout.NORTH);
        
    }
        
    //SAVE A REGRESSION WITH SCATTERPLOT
        public ScatterPlot(final String title, String x, String y, ArrayList<Double> xData, ArrayList<Double> yData, String destPath, ArrayList<Double> rectCoord, ArrayList<Double> regLineCoord) {

        super();
        xAxisName = x;
        yAxisName = y;
        this.xData = xData;
        this.yData = yData;
        path = destPath;
        
         // scatter plot
         XYSeries series = new XYSeries("Scatter Plot");
         int l = xData.size();
         for(int i = 0 ; i < l ; i++){
             series.add(xData.get(i), yData.get(i));
         }
         dataset.addSeries(series);
         
         // line plot
         XYSeries lineData = new XYSeries("Line Plot");
         lineData.add(regLineCoord.get(0), regLineCoord.get(1));
         lineData.add(regLineCoord.get(2), regLineCoord.get(3));
         dataset.addSeries(lineData);
         
         // Saved Regressions
         int numberOfSavedRegressions = rectCoord.size() / 5;
         savedRegressions(rectCoord, regLineCoord, numberOfSavedRegressions);
         
//        addData();
        chart = createChart(numberOfSavedRegressions);
        
        XYPlot plot = (XYPlot) chart.getPlot();
        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
        
        xAxis.setLowerBound(getMinValue(xData));
        xAxis.setUpperBound(getMaxValue(xData));
        yAxis.setLowerBound(getMinValue(yData));
        yAxis.setUpperBound(getMaxValue(yData));
        
//        ArrayList<Point> myList = new ArrayList<Point>();
//        
//        myList.add(new Point(12, 15));
//        
//        myList.get(0).getX();
//        
//        storeScatterPlot(chart, title);
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 400));
        chartPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        chartPanel.setDomainZoomable(false);
        chartPanel.setRangeZoomable(false);
        chartPanel.setName(title);
        chartPanel.addMouseListener(this);
        
//        this works
//        JPanel panel = new JPanel();
//        panel.add(chartPanel);
//        add(panel);
        
        
//          setLayout(new BorderLayout());
//          this.add(chartPanel, BorderLayout.CENTER);
//        add(new JLabel("Hello"), BorderLayout.NORTH);
        
    }
        
    public void savedRegressions(ArrayList<Double> rectCoord, ArrayList<Double> regLineCoord, int numberOfSavedRegressions){
        for(int i = 0; i < numberOfSavedRegressions; i++){
             XYSeries rectData = new XYSeries("Rectangle Plot" + i, false);
             rectData.add(rectCoord.get((5*i)+0), rectCoord.get((5*i)+1));
             rectData.add(rectCoord.get((5*i)+0), rectCoord.get((5*i)+3));
             rectData.add(rectCoord.get((5*i)+2), rectCoord.get((5*i)+3));
             rectData.add(rectCoord.get((5*i)+2), rectCoord.get((5*i)+1));
             rectData.add(rectCoord.get((5*i)+0), rectCoord.get((5*i)+1));
             dataset.addSeries(rectData);
             
             if(rectCoord.get((5*i)+4)== 0.0){
                XYSeries regLineData = new XYSeries("Regression Plot" + i);
                regLineData.add(regLineCoord.get((5*i)+0), regLineCoord.get((5*i)+1));
                regLineData.add(regLineCoord.get((5*i)+2), regLineCoord.get((5*i)+3));
                dataset.addSeries(regLineData);
             }
             else{
                 XYSeries quadData1 = new XYSeries("Regression Plot" + i) {};
                 double[] array1 = {regLineCoord.get((5*i)+2), regLineCoord.get((5*i)+3), regLineCoord.get((5*i)+4)};
                 Function2D p1 = new PolynomialFunction2D(array1);
                if(regLineCoord.get((5*i)+0) < regLineCoord.get((5*i)+1)){
                quadData1 =  DatasetUtilities.sampleFunction2DToSeries(
                    p1, regLineCoord.get((5*i)+0), regLineCoord.get((5*i)+1), 50, "y = " + regLineCoord.get((5*i)+2) + "x² {-20…20} +" + regLineCoord.get((5*i)+3) + "x{-20…20} +" + regLineCoord.get((5*i)+4));
                 dataset.addSeries(quadData1);
                }
                else{
                quadData1 =  DatasetUtilities.sampleFunction2DToSeries(
                    p1, regLineCoord.get((5*i)+1), regLineCoord.get((5*i)+0), 50, "y = " + regLineCoord.get((5*i)+2) + "x² {-20…20} +" + regLineCoord.get((5*i)+3) + "x{-20…20} +" + regLineCoord.get((5*i)+4));
                 dataset.addSeries(quadData1);
                }
             }
         }
    
    
    
    }
    
    public JFreeChart getChart(){
    return chart;
    }
    
    /**
     * Creates a new scatter plot without storing the plot.
     *
     * @param title the frame title.
     * @param x the first dimension.
     * @param y the second dimension.
     * @param data dataset.
     */
    public ScatterPlot(final String title, String x, String y, ArrayList<Double> xData, ArrayList<Double> yData) {
        super();
        xAxisName = x;
        yAxisName = y;
        this.xData = xData;
        this.yData = yData;

        this.dataset = setData(xData, yData);
        chart = createChart(0);
        
        XYPlot plot = (XYPlot) chart.getPlot();
        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
        xAxis.setLowerBound(getLowerBoundX());
        xAxis.setUpperBound(getUpperBoundX());
        yAxis.setLowerBound(getLowerBoundY());
        yAxis.setUpperBound(getUpperBoundY());
        
        double[] coeffs = Regression.getOLSRegression(dataset, 0);
	LineFunction2D linefunction2d = new LineFunction2D(coeffs[0], coeffs[1]);
	XYDataset series2 = DatasetUtilities.sampleFunction2D(linefunction2d, 10, 50, 5, "Linear Regression Line");
	plot.setDataset(2, series2); 
        
        storeScatterPlot(chart, title);
        chartPanel = new ChartPanel(chart);
//        chartPanel.setSize(250, 125);
        chartPanel.setPreferredSize(new Dimension(180, 100));
        chartPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        chartPanel.setDomainZoomable(false);
        chartPanel.setRangeZoomable(false);
        chartPanel.setName(title);
        chartPanel.addMouseListener(this);

        setLayout(new BorderLayout());
        add(chartPanel, BorderLayout.NORTH);
    }
    

    private JFreeChart createChart(int numberOfSavedRegressions) {
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Test",
                "X",
                "Y",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                true);
        XYPlot plot = (XYPlot) chart.getPlot();
        chart.removeLegend();
        plot.setBackgroundPaint(Color.white);
        plot.setOutlineVisible(false);

        plot.getRangeAxis().addChangeListener(this);
        plot.getDomainAxis().addChangeListener(this);


        Shape shape = new Ellipse2D.Double(-2.0, -2.0, 4.0, 4.0);
        XYLineAndShapeRenderer renderer1 = (XYLineAndShapeRenderer) plot.getRenderer();
        renderer1.setBaseShapesVisible(true);
        renderer1.setSeriesShape(0, shape);
        renderer1.setSeriesPaint(0, Color.red);
        renderer1.setSeriesLinesVisible(0, false);
        renderer1.setBaseShape(shape);
        renderer1.setBasePaint(Color.red);
        renderer1.setBaseLinesVisible(false);
        
        //stuff added
        renderer1.setSeriesLinesVisible(0, false);
        renderer1.setSeriesShapesVisible(0, true);

        // "1" is the line plot
        renderer1.setSeriesLinesVisible(1, true);
        renderer1.setSeriesShapesVisible(1, false);
        renderer1.setSeriesPaint(1, Color.black);
        
        for(int i = 1; i <= numberOfSavedRegressions; i++){
            //For rectangle
            renderer1.setSeriesLinesVisible((2*i), true);
            renderer1.setSeriesShapesVisible((2*i), false);
//            renderer1.setSeriesPaint((2*i), Color.blue);
            
            //For reg line
            renderer1.setSeriesLinesVisible(((2*i)+1), true);
            renderer1.setSeriesShapesVisible(((2*i)+1), false);
            renderer1.setSeriesPaint(((2*i)+1), Color.black);
        }
        
        Color[] colorarray = new Color[12];
        colorarray[0] = new Color(141,211,199); //light blue
        colorarray[1] = new Color(190,186,218); //purple
        colorarray[2] = new Color(253,180,98); //orange
        colorarray[3] = new Color(179,222,105); //green
        colorarray[4] = new Color(128,177,211); //dark blue
        colorarray[5] = new Color(189,189,189); //grey 
        colorarray[6] = new Color(166,54,3); //brown
        
        // light blue, purple, orange, green, dark blue, grey, brown
        
        renderer1.setSeriesPaint((2), colorarray[0]);
        renderer1.setSeriesPaint((4), colorarray[1]);
        renderer1.setSeriesPaint((6), colorarray[2]);
        renderer1.setSeriesPaint((8), colorarray[3]);
        renderer1.setSeriesPaint((10), colorarray[4]);
        renderer1.setSeriesPaint((12), colorarray[5]);
        renderer1.setSeriesPaint((14), colorarray[6]);
        
        plot.setRenderer(renderer1);
        //STUFF ADDED END
        
        return chart;
    }
    
    public void getPointsSelected(ArrayList<Double> xSelected, ArrayList<Double> ySelected, Double markerStart, Double markerStartY, Double markerEnd, Double markerEndY, JTextArea textAreaPoints){
            System.out.println("In get points!!!!!");
            xSelected.clear();
            ySelected.clear();
            for(int i= 0; i < xData.size(); i++){
                if((markerStart < markerEnd)&&(markerStartY < markerEndY))
                {
                    if((xData.get(i) > markerStart)&&(xData.get(i) < markerEnd)&&(yData.get(i) > markerStartY)&&(yData.get(i) < markerEndY))
                    {
                        xSelected.add(xData.get(i));
                        ySelected.add(yData.get(i));
                    }
                }
                else if((markerStart < markerEnd)&&(markerStartY > markerEndY))
                {
                    if((xData.get(i) > markerStart)&&(xData.get(i) < markerEnd)&&(yData.get(i) < markerStartY)&&(yData.get(i) > markerEndY))
                    {
                        xSelected.add(xData.get(i));
                        ySelected.add(yData.get(i));
                    }
                
                }
                else if((markerStart > markerEnd)&&(markerStartY < markerEndY))
                {
                    if((xData.get(i) < markerStart)&&(xData.get(i) > markerEnd)&&(yData.get(i) > markerStartY)&&(yData.get(i) < markerEndY))
                    {
                        xSelected.add(xData.get(i));
                        ySelected.add(yData.get(i));
                    }
                
                }
                else
                {
                    if((xData.get(i) < markerStart)&&(xData.get(i) > markerEnd)&&(yData.get(i) < markerStartY)&&(yData.get(i) > markerEndY))
                    {
                        xSelected.add(xData.get(i));
                        ySelected.add(yData.get(i));
                    }
                
                }
            }
            //END OF FOR LOOP
            
            textAreaPoints.setText("");
            for(int i= 0; i < xSelected.size(); i++){
                textAreaPoints.append("x : ");
                textAreaPoints.append(Double.toString(xSelected.get(i)));
                textAreaPoints.append("\ty : ");
                textAreaPoints.append(Double.toString(ySelected.get(i)));
                textAreaPoints.append("\n");
            }
    }
    //END OF GET POINTS SELECTED

    /**
     * Creates a sample dataset.
     *
     * @return dataset collection.
     */
    public XYSeriesCollection setData(double scatterData[][]) {
        XYSeries data = new XYSeries("");
        if (scatterData.length > 0) {
            for (int i = 0; i < scatterData.length; i++) {
                final double x = scatterData[i][0];
                final double y = scatterData[i][1];
                data.add(x, y);
            }
        }
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(data);
        return dataset;
    }

    /**
     *
     * @param x
     * @param y
     *
     * @return dataset collection.
     */
    public XYSeriesCollection setData(ArrayList<Double> x, ArrayList<Double> y) {
        int length = x.size();
        if (x.size() != y.size()) {
            if (x.size() > y.size()) {
                length = y.size();
            }
        }

        XYSeries data = new XYSeries("");
        for (int i = 0; i < length; i++) {
            if (x.get(i) != null && y.get(i) != null) {
                final double xTmp = x.get(i);
                final double yTmp = y.get(i);
                data.add(xTmp, yTmp);
            }
        }
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(data);
        return dataset;
    }
    
//    public XYSeriesCollection addData() {
//        
//        ArrayList<Double> x = new ArrayList<Double>();
//        x.add(11.38);
//        x.add(12.56);
//        x.add(14.3);
//        
//        ArrayList<Double> y = new ArrayList<Double>();
//        y.add(2.38);
//        y.add(2.56);
//        y.add(2.39);
//        
////        int length = x.size();
////        if (x.size() != y.size()) {
////            if (x.size() > y.size()) {
////                length = y.size();
////            }
////        }
//        
//        
//
//        XYSeries data = new XYSeries("");
//        for (int i = 0; i < x.size(); i++) {
//            if (x.get(i) != null && y.get(i) != null) {
//                final double xTmp = x.get(i);
//                final double yTmp = y.get(i);
//                data.add(xTmp, yTmp);
//            }
//        }
//        
//        this.dataset.addSeries(data);
//        return this.dataset;
//    }

    /**
     * Stores the Scatter Plot
     */
    public void storeScatterPlot(JFreeChart chart, String name) {
        if (name.contains(":")) {
            name = name.replace(":", "_");
        }
        File file = new File(path + name + ".png");

        try {
            ChartUtilities.saveChartAsPNG(file, chart, 250, 125);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double getLowerBoundX() {
        XYPlot plot = (XYPlot) chart.getPlot();
        return plot.getDomainAxis().getLowerBound();
    }

    public double getUpperBoundX() {
        XYPlot plot = (XYPlot) chart.getPlot();
        return plot.getDomainAxis().getUpperBound();
    }

    public double getLowerBoundY() {
        XYPlot plot = (XYPlot) chart.getPlot();
        return plot.getRangeAxis().getLowerBound();
    }

    public double getUpperBoundY() {
        XYPlot plot = (XYPlot) chart.getPlot();
        return plot.getRangeAxis().getUpperBound();
    }

    public double[] getAxisBounds() {
        double[] bounds = new double[4];
        bounds[0] = getLowerBoundX();
        bounds[1] = getUpperBoundX();
        bounds[2] = getLowerBoundY();
        bounds[3] = getUpperBoundY();

        return bounds;
    }
    
    public double getMaxValue(ArrayList<Double> data){
        double result = Double.NEGATIVE_INFINITY;
        for(Double d : data){
            if(d != null){
                result = (d > result) ? d : result;
            }            
        }
        return result;
    }
    
    public double getMinValue(ArrayList<Double> data){
        double result = Double.POSITIVE_INFINITY;
        for(Double d : data){
            if(d != null){
                result = (d < result) ? d : result;
            }            
        }
        return result;
    }

    @Override
    public void axisChanged(AxisChangeEvent event) {
        if (event.getAxis().equals(chart.getXYPlot().getRangeAxis())) {
            double rangeLow = chart.getXYPlot().getRangeAxis().getLowerBound();
            double rangeUp = chart.getXYPlot().getRangeAxis().getUpperBound();

            selectionYstart = (int) Math.round(rangeUp);
            selectionYend = (int) Math.round(rangeLow);
//            System.out.println("RangeAxis new range from " + rangeLow + " to " + rangeUp);
        } else {
            if (event.getAxis().equals(chart.getXYPlot().getDomainAxis())) {
                double domainLow = chart.getXYPlot().getDomainAxis().getLowerBound();
                double domainUp = chart.getXYPlot().getDomainAxis().getUpperBound();

                selectionXstart = (int) Math.round(domainLow);
                selectionXend = (int) Math.round(domainUp);
//                System.out.println("DomainAxis new range from " + domainLow + " to " + domainUp);
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void chartMouseClicked(ChartMouseEvent cme) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void chartMouseMoved(ChartMouseEvent cme) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
