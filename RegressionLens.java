/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package regressionlensv1;
import static com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Int;
import static com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.String;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import static java.time.Clock.system;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.ui.Layer;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;
/**
 *
 * @author amahajan
 */
public class RegressionLens extends JFrame {
    
    String path = "O:\\Aishwarya\\";
    
    //GUI Components
    JLabel label, labelRectangleCord, labelPoints, labelLinearRegression;
    JTextArea textAreaRectangleCord, textAreaPoints, textAreaLinearRegression;
    JButton buttonDisplayScatterplot, buttonSaveRegression, buttonRemoveAll, buttonReset, buttonRemoveRegression;
    JPanel thePanel, pnlNorth, pnlWest, pnlEast, pnlSouth, pnlCenter;
    JRadioButton linearRegressionJRadioButton, quadraticRegressionJRadioButton;
    JComboBox xVariable, yVariable;
    String infoOnComponent = "";
    ChartPanel panel;
    JList regressionColors;
    DefaultListModel defListModel = new DefaultListModel();
    JScrollPane scroller;
    
    //Scatterplot data, points in rectangle
    ArrayList<Double> xData = new ArrayList<>();
    ArrayList<Double> yData = new ArrayList<>();
    ArrayList<Double> xSelected = new ArrayList<>();
    ArrayList<Double> ySelected = new ArrayList<>();
    Regression reg = new Regression();
    Rengine re;
    double lowerBoundX, lowerBoundY, upperBoundX, upperBoundY;
    
    //to calculate linear regression
    double[] maxPoint = {0,0};
    double[] minPoint = {0,0};
    double[] linearRegPointsStart = {0,0};
    double[] linearRegPointsEnd = {0,0};
    
    //to calculate quadratic regression
    double quadRegPointsStartX = 0;
    double quadRegPointsEndX = 0;
    double quadRegConst = 0;
    double quadRegCoeffX = 0;
    double quadRegCoeffX2 = 0;
    double[] xQuadraticIntersectionPoints = {0,0};
    
    //Marker Co-ordinates
    private Marker marker;
    private Marker markerY;
    private Double markerStart = Double.NaN;
    private Double markerEnd = Double.NaN;
    private Double markerStartY = Double.NaN;
    private Double markerEndY = Double.NaN;
    
    //Saved regressions
    ArrayList<Double> rectangleCoord = new ArrayList<>();
    ArrayList<Double> regressionLineCoord = new ArrayList<>(); //stores both linear and quadratic info
    
    //Rectangle colours
    String[] colors = {"Light Blue", "Purple", "Orange", "Green", "Dark Blue", "Gray", "Brown"};
    
    //for list
    int pos = -1;
    
    Intersection lines = new Intersection();
    ScatterPlot sp;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        if (!Rengine.versionCheck()) {
	    System.err.println("** Version mismatch - Java files don't match library version.");
	    System.exit(1);
	}
        System.out.println("Creating Rengine (with arguments)");
		// 1) we pass the arguments from the command line
		// 2) we won't use the main loop at first, we'll start it later
		//    (that's the "false" as second argument)
		// 3) the callbacks are implemented by the TextConsole class above
	Rengine reTemp;
        reTemp = new Rengine(args, false, new TextConsole());
        System.out.println("Rengine created, waiting for R");
		// the engine creates R is a new thread, so we should wait until it's ready
        if (!reTemp.waitForR()) {
            System.out.println("Cannot load R");
            return;
        }
        
        new RegressionLens(reTemp);
        
    }
    
    public RegressionLens(Rengine reTemp){
        
        re = reTemp;
        
        //Testing R connection
        //REXP coeff = new REXP(34, re.eval("c(1,2,4,8,16 )"));
        //System.out.println("Processed first RE Evaluation!!");
        
        this.setSize(1200,550);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setTitle("Scatterplot Regression");
        
        //Creating the panels
        thePanel = new JPanel();
        thePanel.setLayout(new BorderLayout());
        pnlNorth = new JPanel();
        pnlWest = new JPanel();
        pnlEast = new JPanel();
        pnlSouth = new JPanel();
        pnlCenter = new JPanel();
        
        pnlEast.setLayout(new BoxLayout(pnlEast, BoxLayout.PAGE_AXIS ));
        pnlWest.setLayout(new BoxLayout(pnlWest, BoxLayout.PAGE_AXIS ));
        
        //Creating and adding components for NORTH
        label = new JLabel("Regression lens");
        label.setToolTipText("Diplays Regression for Area selected");
        label.setFont (label.getFont ().deriveFont (30.0f));
        pnlNorth.add(label,BorderLayout.NORTH);
        
        
        //Creating and adding components for WEST        
        
        //Combo Box
        String[] variables = {"Dim1", "Dim2", "Dim3", "Dim4", "Dim5", "Dim6", "Dim7", "Dim8", "Dim9", "Dim10", "Dim11", "Dim12", "Dim13", "Dim14", "Dim15", "Dim16"};
        xVariable = new JComboBox(variables);
        yVariable = new JComboBox(variables);
        setMaxSize(xVariable);
        setMaxSize(yVariable);
        pnlWest.add(xVariable);
        pnlWest.add(Box.createVerticalStrut(5));
        pnlWest.add(yVariable);
        pnlWest.add(Box.createVerticalStrut(15));
        
        buttonDisplayScatterplot = new JButton("Display Scatter Plot");
        buttonDisplayScatterplot.setToolTipText("Displays the scatter plot");
        ListenForButton lForButton = new ListenForButton();
        buttonDisplayScatterplot.addActionListener(lForButton);
        pnlWest.add(buttonDisplayScatterplot);
        
        pnlWest.add(Box.createVerticalStrut(20));
        
        //Radio Buttons
        linearRegressionJRadioButton = new JRadioButton("Linear");
        quadraticRegressionJRadioButton = new JRadioButton("Quadratic");
        ButtonGroup regressionType = new ButtonGroup();
        regressionType.add(linearRegressionJRadioButton);
        regressionType.add(quadraticRegressionJRadioButton);
        JPanel regressionTypePanel = new JPanel();
        Border regressionTypeBorder = BorderFactory.createTitledBorder("Type of Regression");
        regressionTypePanel.setBorder(regressionTypeBorder);
        regressionTypePanel.add(linearRegressionJRadioButton);
        regressionTypePanel.add(quadraticRegressionJRadioButton);
        linearRegressionJRadioButton.setSelected(true);
        pnlWest.add(regressionTypePanel);
        
        pnlWest.add(Box.createVerticalStrut(20));

        buttonSaveRegression = new JButton("Save Regression");
        buttonSaveRegression.setToolTipText("Saves the linear regression selected");
        buttonSaveRegression.addActionListener(lForButton);
        pnlWest.add(buttonSaveRegression);
        
        pnlWest.add(Box.createVerticalStrut(20));
        
        regressionColors = new JList(defListModel);
        regressionColors.setVisibleRowCount(4);
        scroller = new JScrollPane(regressionColors, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        regressionColors.setFixedCellHeight(30);
        regressionColors.setFixedCellWidth(150);
        regressionColors.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        pnlWest.add(scroller);
        ListenForList lForList =  new ListenForList();
        regressionColors.addListSelectionListener(lForList);
        
        pnlWest.add(Box.createVerticalStrut(5));
        
        buttonRemoveRegression = new JButton("Remove selected regressions");
        buttonRemoveRegression.setToolTipText("Removes all the selected regressions in list from the scatterplot");
        buttonRemoveRegression.addActionListener(lForButton);
        pnlWest.add(buttonRemoveRegression);
        
        pnlWest.add(Box.createVerticalStrut(30));

        buttonRemoveAll = new JButton("Remove all regressions");
        buttonRemoveAll.setToolTipText("Removes all the regressions on the scatterplot");
        buttonRemoveAll.addActionListener(lForButton);
        pnlWest.add(buttonRemoveAll);
        
        pnlWest.add(Box.createVerticalStrut(10));
        
        buttonReset = new JButton("Reset");
        buttonReset.setToolTipText("Reset to initial state");
        buttonReset.addActionListener(lForButton);
        pnlWest.add(buttonReset);
        
        pnlWest.add(Box.createVerticalStrut(20));

        // Creating and adding components for EAST
        labelRectangleCord = new JLabel("Rectangle Co-ordinates");
        labelRectangleCord.setToolTipText("Displays the co-ordinates of the area the user has selected");
        pnlEast.add(labelRectangleCord);
        
        textAreaRectangleCord = new JTextArea(15, 15);
        textAreaRectangleCord.setPreferredSize(new Dimension(15,15));
        JScrollPane scrollbarRectangleCord = new JScrollPane(textAreaRectangleCord, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        pnlEast.add(scrollbarRectangleCord);
        
        
        pnlEast.add(Box.createVerticalGlue());
        
        labelPoints = new JLabel("Points in Rectagle");
        labelPoints.setToolTipText("Displays the scatter plot points in the area the user has selected");
        pnlEast.add(labelPoints);
        
        textAreaPoints = new JTextArea(15, 15);
        JScrollPane scrollbarPoints = new JScrollPane(textAreaPoints, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pnlEast.add(scrollbarPoints);
        
        pnlEast.add(Box.createVerticalGlue());
        
        labelLinearRegression = new JLabel("Regression Information");
        labelLinearRegression.setToolTipText("Gives information about linear regression of points in area user has selected");
        pnlEast.add(labelLinearRegression);
        
        textAreaLinearRegression = new JTextArea(15, 15);
        JScrollPane scrollbarLinearRegression = new JScrollPane(textAreaLinearRegression, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        pnlEast.add(scrollbarLinearRegression,BorderLayout.EAST);
        
        pnlEast.add(Box.createVerticalGlue());
        
        thePanel.add(pnlNorth, BorderLayout.NORTH);
        thePanel.add(pnlWest, BorderLayout.WEST);
        thePanel.add(pnlEast, BorderLayout.EAST);
        thePanel.add(pnlSouth, BorderLayout.SOUTH);
        thePanel.add(pnlCenter, BorderLayout.CENTER);
        
        
        this.add(thePanel);
        this.setVisible(true);
    
    }

    private void setMaxSize(JComboBox jc) {
        Dimension max = jc.getMaximumSize();
        Dimension pref = jc.getPreferredSize();
        max.height = pref.height;
        jc.setMaximumSize(max); 
    }
    
    /**
     *
     * @param xData
     * @param yData
     */

        // Implements ActionListener so it can react to events on components
    
    //START OF LIST LISTENER
    private class ListenForList implements ListSelectionListener{

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting() == true){
                if(!defListModel.isEmpty()){
                                    
                int index = regressionColors.getSelectedIndex();
                
                //PRINT RECT COORD
                textAreaRectangleCord.setText("Marker Start \nx = ");
                textAreaRectangleCord.append(Double.toString(rectangleCoord.get(5 * index)));
                textAreaRectangleCord.append("\ny = ");
                textAreaRectangleCord.append(Double.toString(rectangleCoord.get(5 * index + 1)));
                textAreaRectangleCord.append("\nMarker end \nx = ");
                textAreaRectangleCord.append(Double.toString(rectangleCoord.get(5 * index + 2)));
                textAreaRectangleCord.append("\ny = ");
                textAreaRectangleCord.append(Double.toString(rectangleCoord.get(5 * index + 3)));
                
                //PRINT POINTS IN RECTANGLE
                sp.getPointsSelected(xSelected, ySelected, rectangleCoord.get(5 * index), rectangleCoord.get(5 * index + 1), rectangleCoord.get(5 * index + 2), rectangleCoord.get(5 * index + 3), textAreaPoints);
            
                //PRINT REGRESSION
                if(rectangleCoord.get(5 * index + 4) == 0.0) {
                    String s = reg.getLinearRegression(xSelected, ySelected, re);
                    String[] coeff = s.split("\\s*,\\s*");
                    textAreaLinearRegression.setText("Intercept : ");
                    textAreaLinearRegression.append(coeff[0]);
                    textAreaLinearRegression.append("\nSlope : ");
                    textAreaLinearRegression.append(coeff[1]);
                }
                else{
                    String s = reg.getQuadraticRegression(ySelected, xSelected, re);
                    String[] coeff = s.split("\\s*,\\s*");
                    textAreaLinearRegression.setText("Intercept : ");
                    textAreaLinearRegression.append(coeff[0]);
                    textAreaLinearRegression.append("\nX Coeff : ");
                    textAreaLinearRegression.append(coeff[1]);
                    textAreaLinearRegression.append("\nX2 Coeff : ");
                    textAreaLinearRegression.append(coeff[2]);
                }
                
                //RESET THE SCATTERPLOT TO REMOVE PREVIOUS MARKER AND REGRESSION
                markerStart = Double.NaN;
                markerEnd = Double.NaN;
                markerStartY = Double.NaN;
                markerEndY = Double.NaN;
                sp = new ScatterPlot("Title", "x", "y", xData, yData, path, rectangleCoord, regressionLineCoord);
                pnlCenter.removeAll();
                panel = new ChartPanel(sp.getChart());
                panel.setRangeZoomable(false);
                panel.setDomainZoomable(false);
                panel.addMouseListener(new MouseMarker(panel));
                pnlCenter.add(panel);
                pnlCenter.updateUI();
                }
            }   
            //END OF VAlUE ADJUSTING
        }
    
    
    }
    
    //START OF ACTION LISTENERS
    private class ListenForButton implements ActionListener{
    @Override
        public void actionPerformed(ActionEvent e){
            
            //DISPLAY SCATTERPLOT BUTTON
            if(e.getSource() == buttonDisplayScatterplot){
                
                int xComp = xVariable.getSelectedIndex();
                int yComp = yVariable.getSelectedIndex();
    
                xData.clear();
                yData.clear();
                getScatterplotPoints(xData, yData, xComp, yComp);
                sp = new ScatterPlot("Title", "x", "y", xData, yData, path);
                
                clearMarker();
                clearText();
                
                rectangleCoord.clear();
                regressionLineCoord.clear();
                
                defListModel.clear();
                pos = -1;
                
                resetCenterPanel(sp);                

                lowerBoundX = sp.getLowerBoundX();
                lowerBoundY = sp.getLowerBoundY();
                upperBoundX = sp.getUpperBoundX();
                upperBoundY = sp.getUpperBoundY();
             
                
                Object[] xArray = new Object[xData.size()];
                xArray = xData.toArray();                
                
                Object[] yArray = new Object[yData.size()];
                yArray = yData.toArray();     
            }
            //END OF BUTTON DISPLAY SCATTERPLOT
            
            //SAVE REGRESSION BUTTON SELECTED
            if(e.getSource() == buttonSaveRegression){
                rectangleCoord.add(markerStart);
                rectangleCoord.add(markerStartY);
                rectangleCoord.add(markerEnd);
                rectangleCoord.add(markerEndY);
                
                if(linearRegressionJRadioButton.isSelected()) {
                    rectangleCoord.add(0.0);
                    regressionLineCoord.add(linearRegPointsStart[0]);
                    regressionLineCoord.add(linearRegPointsStart[1]);
                    regressionLineCoord.add(linearRegPointsEnd[0]);
                    regressionLineCoord.add(linearRegPointsEnd[1]);
                    regressionLineCoord.add(0.0);
                }
                //End of linear reg
                
                if(quadraticRegressionJRadioButton.isSelected()) {
                    rectangleCoord.add(1.0);
                    regressionLineCoord.add(xQuadraticIntersectionPoints[0]);
                    regressionLineCoord.add(xQuadraticIntersectionPoints[1]);
                    regressionLineCoord.add(quadRegConst);
                    regressionLineCoord.add(quadRegCoeffX);
                    regressionLineCoord.add(quadRegCoeffX2);
                }
                
                clearMarker();
                
                //Create new scatterplot with saved regression
                sp = new ScatterPlot("Title", "x", "y", xData, yData, path, rectangleCoord, regressionLineCoord);
                resetCenterPanel(sp);
                
                pos += 1;
                defListModel.addElement(colors[pos]);
            }
            //END OF SAVE  REGRESSION
            
            //REMOVE SELECTED REGRESSION
            if(e.getSource() == buttonRemoveRegression){
                
                clearMarker();
                clearText();
                
                //Create new scatterplot with saved regression
                if(pos == -1){
                    JOptionPane.showMessageDialog(RegressionLens.this, "There are no saved regressions to remove", "", JOptionPane.INFORMATION_MESSAGE);
                }
                
                else if(regressionColors.getSelectedIndex() == -1){
                   JOptionPane.showMessageDialog(RegressionLens.this, "Select a regression to remove", "No Regression Selected", JOptionPane.INFORMATION_MESSAGE);
                   sp = new ScatterPlot("Title", "x", "y", xData, yData, path, rectangleCoord, regressionLineCoord);
                   resetCenterPanel(sp);
                }
                
                else if(pos == 0){
                    rectangleCoord.clear();
                    regressionLineCoord.clear();
                    defListModel.clear();
                    pos = -1;
                    sp = new ScatterPlot("Title", "x", "y", xData, yData, path);
                    resetCenterPanel(sp);
                }
                else{
                    int colourSelected = regressionColors.getSelectedIndex();
                    rectangleCoord.remove((5*colourSelected));
                    rectangleCoord.remove((5*colourSelected));
                    rectangleCoord.remove((5*colourSelected));
                    rectangleCoord.remove((5*colourSelected));
                    rectangleCoord.remove((5*colourSelected));
                    regressionLineCoord.remove((5*colourSelected));
                    regressionLineCoord.remove((5*colourSelected));
                    regressionLineCoord.remove((5*colourSelected));
                    regressionLineCoord.remove((5*colourSelected));
                    regressionLineCoord.remove((5*colourSelected));
                    
                    defListModel.remove(pos);
                    pos = pos - 1;  
                    
                    sp = new ScatterPlot("Title", "x", "y", xData, yData, path, rectangleCoord, regressionLineCoord);
                    resetCenterPanel(sp);
                }
            }
            //END OF REMOVE SELECTED REGRESSION
            
            //REMOVE ALL REGRESSIONS
            if(e.getSource() == buttonRemoveAll){
                rectangleCoord.clear();
                regressionLineCoord.clear();
                
                
                clearMarker();
                clearText();
                
                //Create new scatterplot
                sp = new ScatterPlot("Title", "x", "y", xData, yData, path);
                resetCenterPanel(sp);
                
                defListModel.clear();
                
                pos = -1;
                
            }
            //END OF REMOVE ALL REGRESSIONS
            
            //RESET SCREEN
            if(e.getSource() == buttonReset){
                
                defListModel.clear();
                
                pnlCenter.removeAll();
                pnlCenter.updateUI();
                
                clearMarker();
                clearText();
                
                rectangleCoord.clear();
                regressionLineCoord.clear();
            
            }
            //END OF RESET SCREEN
        }
        
        public void resetCenterPanel(ScatterPlot sp){
        
                pnlCenter.removeAll();
                panel = new ChartPanel(sp.getChart());
                panel.setRangeZoomable(false);
                panel.setDomainZoomable(false);
                panel.addMouseListener(new MouseMarker(panel));
                pnlCenter.add(panel);
                pnlCenter.updateUI();
        
        }
        
        public void clearMarker(){
            markerStart = Double.NaN;
            markerEnd = Double.NaN;
            markerStartY = Double.NaN;
            markerEndY = Double.NaN;
        }
        
        public void clearText(){
            textAreaRectangleCord.setText("");
            textAreaPoints.setText("");
            textAreaLinearRegression.setText("");
        }
        
        public void getScatterplotPoints(ArrayList<Double> xData, ArrayList<Double> yData, int xComp, int yComp){
            BufferedReader in = null;
            try {
                in = new BufferedReader(new FileReader(new File("O:\\\\Aishwarya\\\\eurostat27.csv")));
                String next=in.readLine();
                while((next=in.readLine()) != null){
                    String[] values = next.split(";");
                    if(!(values[xComp].isEmpty() || values[yComp].isEmpty()))
                    {
                        xData.add(Double.parseDouble(values[xComp]));
                        yData.add(Double.parseDouble(values[yComp]));
                    }
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(RegressionLens.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(RegressionLens.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    in.close();
                } catch (IOException ex) {
                    Logger.getLogger(RegressionLens.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }           
    }
    // END OF ACTION LISTENERS
    
    
    //START OF MOUSE MARKER
    private final class  MouseMarker extends MouseAdapter{
        
        private final XYPlot plot;
        private final JFreeChart chart;
        private ChartPanel panel;


        public MouseMarker(ChartPanel panel) {
            this.panel = panel;
            this.chart = panel.getChart();
            this.plot = (XYPlot) chart.getPlot();
            updateMarker();
            updateMarkerY();
        }

        private void updateMarker(){
            if (marker != null){
                plot.removeDomainMarker(marker,Layer.BACKGROUND);
            }
            if (!( markerStart.isNaN() && markerEnd.isNaN())){
                if ( markerEnd > markerStart){
                    marker = new IntervalMarker(markerStart, markerEnd);
                    
                    marker.setPaint(new Color(0xDD, 0xFF, 0xDD, 0x80));
                    marker.setAlpha(0.5f);
                    plot.addDomainMarker(marker,Layer.BACKGROUND);
                }else{
                    marker = new IntervalMarker(markerEnd, markerStart);
                    
                    marker.setPaint(new Color(0xDD, 0xFF, 0xDD, 0x80));
                    marker.setAlpha(0.5f);
                    plot.addDomainMarker(marker,Layer.BACKGROUND);
                }
            }
        }
        
        private void updateMarkerY(){
            if (markerY != null){
                plot.removeRangeMarker(markerY,Layer.BACKGROUND);
            }
            if (!( markerStartY.isNaN() && markerEndY.isNaN())){
            if ( markerEndY > markerStartY){
                markerY = new IntervalMarker(markerStartY, markerEndY);
                markerY.setPaint(new Color(0xDD, 0xFF, 0xDD, 0x80));
                markerY.setAlpha(0.5f);
                plot.addRangeMarker(markerY,Layer.BACKGROUND);
            }else{
                markerY = new IntervalMarker(markerEndY, markerStartY);
                markerY.setPaint(new Color(0xDD, 0xFF, 0xDD, 0x80));
                markerY.setAlpha(0.5f);
                plot.addRangeMarker(markerY,Layer.BACKGROUND);
                }
            }
        }

        private Double getPosition(MouseEvent e){
            Point2D p = panel.translateScreenToJava2D( e.getPoint());
            Rectangle2D plotArea = panel.getScreenDataArea();
            XYPlot plot = (XYPlot) chart.getPlot();
            return plot.getDomainAxis().java2DToValue(p.getX(), plotArea, plot.getDomainAxisEdge());
        }
        
        private Double getPositionY(MouseEvent e){
            Point2D p = panel.translateScreenToJava2D( e.getPoint());
            Rectangle2D plotArea = panel.getScreenDataArea();
            XYPlot plot = (XYPlot) chart.getPlot();
            return plot.getRangeAxis().java2DToValue(p.getY(), plotArea, plot.getRangeAxisEdge());
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if(e.getModifiers() == InputEvent.BUTTON1_MASK){
            String s = "";
            String[] rEngStr = {};
            markerEnd = getPosition(e);
            markerEndY = getPositionY(e);      
            textAreaRectangleCord.append("\nMarker end \nx = ");
            textAreaRectangleCord.append(Double.toString(markerEnd));
            textAreaRectangleCord.append("\ny = ");
            textAreaRectangleCord.append(Double.toString(markerEndY));
            
            checkMarkerCoord();
            
            updateMarker();
            updateMarkerY();
            
            
            //get points in rectangle
            sp.getPointsSelected(xSelected, ySelected, markerStart, markerStartY, markerEnd, markerEndY, textAreaPoints);
            
            //LINEAR REGRESSION 
            if(linearRegressionJRadioButton.isSelected()) {
                //Find linear Regression
                s = reg.getLinearRegression(xSelected, ySelected, re);
                String[] coeff = s.split("\\s*,\\s*");
                double intercept = Double.parseDouble(coeff[0]);
                double slope = Double.parseDouble(coeff[1]);
                textAreaLinearRegression.setText("Intercept : ");
                textAreaLinearRegression.append(coeff[0]);
                textAreaLinearRegression.append("\nSlope : ");
                textAreaLinearRegression.append(coeff[1]);
            
            
            //Find intersection of regression and rectangle

                minPoint[0] = (slope * lowerBoundY) + intercept;
                minPoint[1] = lowerBoundY;
                maxPoint[0] = (slope * upperBoundX) + intercept;
                maxPoint[1] = upperBoundX;
            
            
                int j = 0;
                do{ 
                    boolean b = lines.linesIntersect(minPoint[0], minPoint[1], maxPoint[0], maxPoint[1], markerStart, markerStartY, markerStart, markerEndY);
                    if(lines.linesIntersect(minPoint[0], minPoint[1], maxPoint[0], maxPoint[1], markerStart, markerStartY, markerStart, markerEndY)){                  
                        linearRegPointsStart = lines.getLineLineIntersection(minPoint[0], minPoint[1], maxPoint[0], maxPoint[1], markerStart, markerStartY, markerStart, markerEndY);
                        j++;
                    }
                    
                    if(lines.linesIntersect(minPoint[0], minPoint[1], maxPoint[0], maxPoint[1], markerStart, markerEndY, markerEnd, markerEndY)){
                        if(j == 0){
                            linearRegPointsStart = lines.getLineLineIntersection(minPoint[0], minPoint[1], maxPoint[0], maxPoint[1], markerStart, markerEndY, markerEnd, markerEndY);
                            j++;
                        }
                        else{
                            linearRegPointsEnd = lines.getLineLineIntersection(minPoint[0], minPoint[1], maxPoint[0], maxPoint[1], markerStart, markerEndY, markerEnd, markerEndY);
                            break;
                        }
                    }
                    
                    if(lines.linesIntersect(minPoint[0], minPoint[1], maxPoint[0], maxPoint[1], markerEnd, markerEndY, markerEnd, markerStartY)){
                        if(j == 0){
                            linearRegPointsStart = lines.getLineLineIntersection(minPoint[0], minPoint[1], maxPoint[0], maxPoint[1], markerEnd, markerEndY, markerEnd, markerStartY);
                            j++;
                        }
                        else{
                            linearRegPointsEnd = lines.getLineLineIntersection(minPoint[0], minPoint[1], maxPoint[0], maxPoint[1], markerEnd, markerEndY, markerEnd, markerStartY);
                            break;
                        }
                    }
                    
                    if(lines.linesIntersect(minPoint[0], minPoint[1], maxPoint[0], maxPoint[1], markerEnd, markerStartY, markerStart, markerStartY)){
                      linearRegPointsEnd = lines.getLineLineIntersection(minPoint[0], minPoint[1], maxPoint[0], maxPoint[1], markerEnd, markerStartY, markerStart, markerStartY);
                    }
                }while(false);
            //END WHILE LOOP
            
                //Create linear regression line
                ScatterPlot sp = new ScatterPlot("Title", "x", "y", xData, yData, path, linearRegPointsStart[0], linearRegPointsStart[1], linearRegPointsEnd[0], linearRegPointsEnd[1], rectangleCoord, regressionLineCoord);
                pnlCenter.removeAll();
                panel = new ChartPanel(sp.getChart());
                panel.setRangeZoomable(false);
                panel.setDomainZoomable(false);
                panel.addMouseListener(new MouseMarker(panel));
                pnlCenter.add(panel);
                pnlCenter.updateUI();
            }
            //END OF FIND LINEAR REGRESSION
            
            //QUADRATIC REGRESSION
            if(quadraticRegressionJRadioButton.isSelected()) {
                s = reg.getQuadraticRegression(ySelected, xSelected, re);
                String[] coeff = s.split("\\s*,\\s*");
                quadRegConst = Double.parseDouble(coeff[0]);
                quadRegCoeffX = Double.parseDouble(coeff[1]);
                quadRegCoeffX2 = Double.parseDouble(coeff[2]);
                textAreaLinearRegression.setText("Intercept : ");
                textAreaLinearRegression.append(coeff[0]);
                textAreaLinearRegression.append("\nX Coeff : ");
                textAreaLinearRegression.append(coeff[1]);
                textAreaLinearRegression.append("\nX2 Coeff : ");
                textAreaLinearRegression.append(coeff[2]);
                
                lines.getQuadraticCurveLineIntersection(quadRegCoeffX2, quadRegCoeffX, quadRegConst, markerStart, markerEnd, markerStartY, markerEndY, xQuadraticIntersectionPoints); 
                
                ScatterPlot sp = new ScatterPlot("Title", "x", "y", xData, yData, path, xQuadraticIntersectionPoints[0], xQuadraticIntersectionPoints[1], quadRegCoeffX2, quadRegCoeffX, quadRegConst, rectangleCoord, regressionLineCoord);
                pnlCenter.removeAll();
                panel = new ChartPanel(sp.getChart());
                panel.setRangeZoomable(false);
                panel.setDomainZoomable(false);
                panel.addMouseListener(new MouseMarker(panel));
                pnlCenter.add(panel);
                pnlCenter.updateUI();
                
            }
            //END OF QUADRATIC REGRESSION
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if(e.getModifiers() == InputEvent.BUTTON1_MASK){
                markerStart = getPosition(e);
                markerStartY = getPositionY(e);
                textAreaRectangleCord.setText("Marker Start \nx = ");
                textAreaRectangleCord.append(Double.toString(markerStart));
                textAreaRectangleCord.append("\ny = ");
                textAreaRectangleCord.append(Double.toString(markerStartY));
            }
        }
        
        public void checkMarkerCoord(){
            double temp;
            if(markerEnd < markerStart){
                temp = markerEnd;
                markerEnd = markerStart;
                markerStart = temp;
            }
            if(markerEndY < markerStartY){
                temp = markerEndY;
                markerEndY = markerStartY;
                markerStartY = temp;
            }
        
        }
       
    }
    //END OF MOUSE MARKER
}
