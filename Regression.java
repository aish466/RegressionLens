/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package regressionlensv1;

/**
 *
 * @author amahajan
 */

import static com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.String;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RMainLoopCallbacks;
import org.rosuda.JRI.RVector;
import org.rosuda.JRI.Rengine;

/**
 *
 * @author amahajan
 */
public class Regression {
     
    public static String getLinearRegression(ArrayList<Double> xData, ArrayList<Double> yData, Rengine rEngine){
        String javaVectorX = "c(";
        String javaVectorY = "c(";

        for(int i1= 0; i1 < xData.size(); i1++){
            javaVectorX += xData.get(i1)+",";
        }
        javaVectorX = javaVectorX.replaceAll(",$", "");
        javaVectorX += ")";
        
        for(int i2= 0; i2 < yData.size(); i2++){
            javaVectorY += yData.get(i2) + ",";
        }
        
        javaVectorY = javaVectorY.replaceAll(",$", "");        
        javaVectorY += ")";
        
        REXP mod = rEngine.eval("mod = lm("+ javaVectorX +"~"+ javaVectorY + ")");
//        REXP coeff = rEngine.eval("print(mod$coefficients)");
        REXP coeff = new REXP(34, rEngine.eval("mod$coefficients"));
        //rEngine.eval("mod$coefficients");
        String S = coeff.getContent().toString();
        
        String coeffJava = S.substring(S.indexOf('(')+1, S.indexOf(')'));
        return coeffJava;
    }
    //END GET LINEAR REGRESSION
    
    //QUADRATIC REGRESSION
    //Returns y coefficients
    public static String getQuadraticRegression(ArrayList<Double> xData, ArrayList<Double> yData, Rengine rEngine){

        ArrayList<Double> ySquare = new ArrayList<>();
        
        for(Double num : yData) {
            ySquare.add(num * num);
        }
        
        String javaVectorX = "c(";
        String javaVectorY = "c(";
        String javaVectorY2 = "c(";

        for(int i1= 0; i1 < xData.size(); i1++){
            javaVectorX += xData.get(i1)+",";
        }
        javaVectorX = javaVectorX.replaceAll(",$", "");
        javaVectorX += ")";
        
        for(int i2= 0; i2 < yData.size(); i2++){
            javaVectorY += yData.get(i2) + ",";
        }
        javaVectorY = javaVectorY.replaceAll(",$", "");        
        javaVectorY += ")";
        
        for(int i3= 0; i3 < yData.size(); i3++){
            javaVectorY2 += ySquare.get(i3) + ",";
        }
        javaVectorY2 = javaVectorY2.replaceAll(",$", "");        
        javaVectorY2 += ")";
        
        
        REXP mod = rEngine.eval("mod = lm(" + javaVectorX + "~" + javaVectorY + "+" + javaVectorY2 + ")");
        //REXP Rcoeff = rEngine.eval("print(mod$coefficients)");
        REXP coeff = new REXP(34, rEngine.eval("mod$coefficients"));
        //rEngine.eval("mod$coefficients");
        String S = coeff.getContent().toString();
        
        String coeffJava = S.substring(S.indexOf('(')+1, S.indexOf(')'));
        return coeffJava;
    }
    // END OF QUADRATIC REGRESSION
}
