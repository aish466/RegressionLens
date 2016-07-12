/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package regressionlensv1;

import java.lang.Math;
/**
 *
 * @author amahajan
 */
 
public class Intersection {
    
    public void getPoint(double slope, double intercept, double lowerBoundX, double upperBoundX, double[] minPoint, double[] maxPoint){
        minPoint[0] = lowerBoundX;
        minPoint[1] = (slope * lowerBoundX) + intercept;
        maxPoint[0] = upperBoundX;
        maxPoint[1] = (slope * upperBoundX) + intercept;
    }
    
    
    public boolean linesIntersect(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4){
      // Return false if either of the lines have zero length
      if (x1 == x2 && y1 == y2 ||
            x3 == x4 && y3 == y4){
         return false;
      }
      // Fastest method, based on Franklin Antonio's "Faster Line Segment Intersection" topic "in Graphics Gems III" book (http://www.graphicsgems.org/)
      double ax = x2-x1;
      double ay = y2-y1;
      double bx = x3-x4;
      double by = y3-y4;
      double cx = x1-x3;
      double cy = y1-y3;

      double alphaNumerator = by*cx - bx*cy;
      double commonDenominator = ay*bx - ax*by;
      if (commonDenominator > 0){
         if (alphaNumerator < 0 || alphaNumerator > commonDenominator){
            return false;
         }
      }else if (commonDenominator < 0){
         if (alphaNumerator > 0 || alphaNumerator < commonDenominator){
            return false;
         }
      }
      double betaNumerator = ax*cy - ay*cx;
      if (commonDenominator > 0){
         if (betaNumerator < 0 || betaNumerator > commonDenominator){
            return false;
         }
      }else if (commonDenominator < 0){
         if (betaNumerator > 0 || betaNumerator < commonDenominator){
            return false;
         }
      }
      if (commonDenominator == 0){
         // This code wasn't in Franklin Antonio's method. It was added by Keith Woodward.
         // The lines are parallel.
         // Check if they're collinear.
         double y3LessY1 = y3-y1;
         double collinearityTestForP3 = x1*(y2-y3) + x2*(y3LessY1) + x3*(y1-y2);   // see http://mathworld.wolfram.com/Collinear.html
         // If p3 is collinear with p1 and p2 then p4 will also be collinear, since p1-p2 is parallel with p3-p4
         if (collinearityTestForP3 == 0){
            // The lines are collinear. Now check if they overlap.
            if (x1 >= x3 && x1 <= x4 || x1 <= x3 && x1 >= x4 ||
                  x2 >= x3 && x2 <= x4 || x2 <= x3 && x2 >= x4 ||
                  x3 >= x1 && x3 <= x2 || x3 <= x1 && x3 >= x2){
               if (y1 >= y3 && y1 <= y4 || y1 <= y3 && y1 >= y4 ||
                     y2 >= y3 && y2 <= y4 || y2 <= y3 && y2 >= y4 ||
                     y3 >= y1 && y3 <= y2 || y3 <= y1 && y3 >= y2){
                  return true;
               }
            }
         }
         return false;
      }
      return true;
   }
   // End of Line intersection 
   
       
    /**
     *Returns the point of intersection of 2 lines
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param x3
     * @param y3
     * @param x4
     * @param y4
     * @return
     */
    public double[] getLineLineIntersection(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
      double[] point = new double[2];
      double det1And2 = det(x1, y1, x2, y2);
      double det3And4 = det(x3, y3, x4, y4);
      double x1LessX2 = x1 - x2;
      double y1LessY2 = y1 - y2;
      double x3LessX4 = x3 - x4;
      double y3LessY4 = y3 - y4;
      double det1Less2And3Less4 = det(x1LessX2, y1LessY2, x3LessX4, y3LessY4);
      if (det1Less2And3Less4 == 0){
         // the denominator is zero so the lines are parallel and there's either no solution (or multiple solutions if the lines overlap) so return null.
         return null;
      }
      double x = (det(det1And2, x1LessX2,
            det3And4, x3LessX4) /
            det1Less2And3Less4);
      double y = (det(det1And2, y1LessY2,
            det3And4, y3LessY4) /
            det1Less2And3Less4);
      point[0] = x ;
      point[1] = y ;

      return point;
   }
    //End of get line intersection
    
   protected static double det(double a, double b, double c, double d) {
      return a * d - b * c;
   }
   
    // quadratic equation : y = ax2 + bx + c
    // lines : x = x1, x2 ; y = y1, y2 
    public void getQuadraticCurveLineIntersection(double a, double b, double c, double x1, double x2, double y1, double y2, double[] xIntersectionPoints){
        int i = 0;
        double x;
        double y;
        double xFirst;
        double xSecond;
        double discriminant;
        int check = 0;
        
        //For x = x1
        y = a*x1*x1 + b*x1 + c;
        System.out.println("y = " + y + "i = " + i);
        if((y > y1) && (y < y2)){
            xIntersectionPoints[i] = x1;
            if(i == 1)
                return;
            i++;
        }
        
        //For x = x2
        y = a*x2*x2 + b*x2 + c;
        System.out.println("y = " + y + "i = " + i);
        if((y > y1) && (y < y2)){
            xIntersectionPoints[i] = x2;
            if(i == 1)
                return;
            i++;
        }
        
        //For y = y1
        // ax2 + bx + (c - y1) = 0
        discriminant = b*b - 4*a*(c - y1);
        
        System.out.println("D = " + discriminant + "i = " + i);
        
        if(discriminant > 0){
            xFirst = (-b + Math.sqrt(b*b - 4*a*(c-y1)))/(2 * a);
            if((xFirst > x1) && (xFirst < x2)){
                xIntersectionPoints[i] = xFirst;
                if(i == 1)
                    return;
                i++;
                check = 1;
            }
            
            xSecond = (-b - Math.sqrt(b*b - 4*a*(c-y1)))/(2 * a);
            if((xSecond > x1) && (xSecond < x2)){
                if(check == 1){
                    xIntersectionPoints[0] = xFirst;
                    xIntersectionPoints[1] = xSecond;
                    return;
                }
                xIntersectionPoints[i] = xSecond;
                if(i == 1)
                    return;
                i++;
            }
            check = 0;
        }
        else if(discriminant == 0){
            x = (-b + Math.sqrt(b*b - 4*a*(c-y1)))/(2 * a);
            if(x> x1 && x<x2){
                xIntersectionPoints[i] = x;
                if(i == 1)
                    return;
                i++;
            }
        }
        
        //For y = y2
        // ay2 + by + (c - x2) = 0
        discriminant = b*b - 4*a*(c - y2);
        
        System.out.println("D = " + discriminant + "i = " + i);
        if(discriminant > 0){
            xFirst = (-b + Math.sqrt(b*b - 4*a*(c-y2)))/(2 * a);
            if((xFirst > x1) && (xFirst < x2)){
                xIntersectionPoints[i] = xFirst;
                if(i == 1)
                    return;
                i++;
                check = 1;
            }
            
            xSecond = (-b - Math.sqrt(b*b - 4*a*(c-y2)))/(2 * a);
            if((xSecond > x1) && (xSecond < x2)){
                if(check == 1){
                    xIntersectionPoints[0] = xFirst;
                    xIntersectionPoints[1] = xSecond;
                    return;
                }
                xIntersectionPoints[i] = xSecond;
                if(i == 1)
                    return;
                i++;
            }
            check = 0;
        }
        else if(discriminant == 0){
            x = (-b + Math.sqrt(b*b - 4*a*(c-y2)))/(2 * a);
            if(x> x1 && x<x2){
                xIntersectionPoints[i] = x;
                if(i == 1)
                    return;
            }
        }
    }
    //End of get quadratic line intersection
}
