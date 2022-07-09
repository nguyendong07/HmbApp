/**
 * PreprocessAndComputeFeatures Project
 * Created by
 * Phan Doan Cuong
 * Android Dev
 * VMGames Company
 * cuongphank58@gmail.com
 * 0989906612
 * on 02/03/2017.
 */
package com.example.myapplication;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;


public class FeaturesStatistic {

    //private  int WINDOWN_LENGTH_IN_SECOND ;
    private  double WINDOWN_LENGTH_IN_SECOND ;

    private static final String X = "x";
    private static final String Y = "y";
    private static final String Z = "z";
    private double[] x;
    private double[] y;
    private double[] z;

    private double[] x1;
    private double[] y1;
    private double[] z1;

    private double[] timestamp;

    private DescriptiveStatistics statisticX;
    private DescriptiveStatistics statisticY;
    private DescriptiveStatistics statisticZ;

    //Covariance
    private Covariance covariance;

    private FastFourierTransformer fastFourierTransformer;

    public FeaturesStatistic(double[] x, double[] y, double[] z, double wdlenght, double[] timestamp, int READING_PER_SECOND) {

        this.x = x;
        this.y = y;
        this.z = z;
        this.timestamp = timestamp;
        this.WINDOWN_LENGTH_IN_SECOND =wdlenght;
        int FFTlength[] = {16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384};
        int windowsize= FFTlength[(int)wdlenght] ;
        double[] x1 = new double[windowsize];
        double[] y1 = new double[windowsize];
        double[] z1 = new double[windowsize];
        for(int k= 0; k<x.length; k++)
        {
            x1[k]=x[k];
        }

        for(int k= 0; k<y.length; k++)
        {
            y1[k]=y[k];
        }

        for(int k= 0; k<z.length; k++)
        {
            z1[k]=z[k];
        }

        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        statisticX = new DescriptiveStatistics(x);
        statisticY = new DescriptiveStatistics(y);
        statisticZ = new DescriptiveStatistics(z);
        covariance = new Covariance();
        fastFourierTransformer = new FastFourierTransformer(DftNormalization.STANDARD);
    }


    public FeaturesStatistic(double[] x, double[] y, double[] z, double wdlenght, double[] timestamp) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.timestamp = timestamp;
        this.WINDOWN_LENGTH_IN_SECOND =wdlenght;
        int FFTlength[] = {16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384};
        int windowsize= FFTlength[(int)wdlenght] ;
        double[] x1 = new double[windowsize];
        double[] y1 = new double[windowsize];
        double[] z1 = new double[windowsize];
        for(int k= 0; k<x.length; k++)
        {
            x1[k]=x[k];
        }

        for(int k= 0; k<y.length; k++)
        {
            y1[k]=y[k];
        }

        for(int k= 0; k<z.length; k++)
        {
            z1[k]=z[k];
        }

        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        statisticX = new DescriptiveStatistics(x);
        statisticY = new DescriptiveStatistics(y);
        statisticZ = new DescriptiveStatistics(z);
        covariance = new Covariance();
        fastFourierTransformer = new FastFourierTransformer(DftNormalization.STANDARD);
    }

    private Complex[] getFFT(int length, String type){
        if (type.equals(X)){
            Complex[] xFFT = new Complex[x1.length];
            for(int i = 0; i < x1.length; i++){
                xFFT[i] = new Complex(x1[i]);
            }
            xFFT = fastFourierTransformer.transform(xFFT,TransformType.FORWARD);
            return xFFT;
        }
        else if (type.equals(Y)){
            Complex[] yFFT = new Complex[y1.length];
            for(int i = 0; i < y1.length; i++){
                yFFT[i] = new Complex(y1[i]);
            }
            yFFT = fastFourierTransformer.transform(yFFT,TransformType.FORWARD);
            return yFFT;
        }
        else {
            Complex[] zFFT = new Complex[z1.length];
            for(int i = 0; i < z1.length; i++){
                zFFT[i] = new Complex(z1[i]);
            }
            zFFT = fastFourierTransformer.transform(zFFT,TransformType.FORWARD);
            return zFFT;
        }
    }

    //tinh trung binh 1 khoang ;

    public double getXFFTEnergy(){
        int length = x1.length;
        int khoang=length/10;
        Complex[] xFFT = getFFT(length,X);
        double sumXFFEnergy = 0;
        for (int i = (length/4 -khoang) ; i < (length/4+khoang); i++){
            sumXFFEnergy += xFFT[i].getReal()*xFFT[i].getReal();
        }
        return sumXFFEnergy/(khoang*2);
    }

    public double getYFFTEnergy(){
        int length = y1.length;
        int khoang=length/10;
        Complex[] yFFT = getFFT(length,Y);
        double sumYFFEnergy = 0;
        for (int i = (length/4 -khoang) ; i < (length/4+khoang); i++){
            sumYFFEnergy += yFFT[i].getReal()*yFFT[i].getReal();
        }
        return sumYFFEnergy/(khoang*2);
    }

    public double getZFFTEnergy(){
        int length = z1.length;
        int khoang=length/10;
        Complex[] zFFT = getFFT(length,Z);
        double sumZFFEnergy = 0;
        for (int i = (length/4 -khoang) ; i < (length/4+khoang); i++){
            sumZFFEnergy += zFFT[i].getReal()*zFFT[i].getReal();
        }
        return sumZFFEnergy/(khoang*2);
    }

    public double getXFFTEntropy(){
        int length = x1.length;
        Complex[] xFFT = getFFT(length,X);
        double sumXFFTComponents = 0;
        for (int i = 0; i < length; i++){
            sumXFFTComponents += xFFT[i].getReal();

        }
        //   System.out.print("\nGia tri sumXFFTComponents la:"+ sumXFFTComponents);

        double sumXFFTEntropy = 0;
        double pi;
        for (int i = 0; i < length; i++){

            pi = xFFT[i].getReal()/sumXFFTComponents;
            if(pi==0){
                sumXFFTEntropy = sumXFFTEntropy +0 ;
            }else{
                sumXFFTEntropy = sumXFFTEntropy + pi * Math.log(Math.abs(pi));
            }
        }
        //  System.out.print("\nGia tri Entropy la:"+ sumXFFTEntropy);
        return -(sumXFFTEntropy/length);
    }

    public double getYFFTEntropy(){
        int length = y1.length;
        Complex[] yFFT = getFFT(length,Y);
        double sumYFFTComponents = 0;
        for (int i = 0; i < length; i++){
            sumYFFTComponents += yFFT[i].getReal();
        }

        double sumYFFTEntropy = 0;
        double pi;
        for (int i = 0; i < length; i++){
            pi = yFFT[i].getReal()/sumYFFTComponents;

            sumYFFTEntropy += pi*Math.log(Math.abs(pi));
        }
        return -(sumYFFTEntropy/length);
    }

    public double getZFFTEntropy(){
        int length = z1.length;

        Complex[] zFFT = getFFT(length,Z);
        double sumZFFTComponents = 0;
        for (int i = 0; i < length; i++){
            sumZFFTComponents += zFFT[i].getReal();
        }

        double sumZFFTEntropy = 0;
        double pi;
        for (int i = 0; i < length; i++){
            pi = zFFT[i].getReal()/sumZFFTComponents;
            sumZFFTEntropy += pi*Math.log(Math.abs(pi));
        }
        return -(sumZFFTEntropy/length);
    }

    public double getWindownARA(){
        int length = x.length;
        double sumSQRT = 0.0;
        for (int i = 0; i < length; i++){
            sumSQRT += Math.sqrt((x[i] * x[i]) + (y[i] * y[i]) + (z[i] * z[i]));
        }
        return sumSQRT/length;
    }

    public double getStandardDeviationX(){
        return statisticX.getStandardDeviation();
    }
    public double getStandardDeviationY(){
        return statisticY.getStandardDeviation();
    }
    public double getStandardDeviationZ(){
        return statisticZ.getStandardDeviation();
    }

    public double getDistributionX(){
        return statisticX.getMax() - statisticX.getMin();
    }
    public double getDistributionY(){
        return statisticY.getMax() - statisticY.getMin();
    }
    public double getDistributionZ(){
        return statisticZ.getMax() - statisticZ.getMin();
    }

    public double getMeanX(){
        return statisticX.getMean();
    }
    public double getMeanY(){
        return statisticY.getMean();
    }
    public double getMeanZ(){
        return statisticZ.getMean();
    }

    public double getVarianceX(){
        return statisticX.getVariance();
    }
    public double getVarianceY(){
        return statisticY.getVariance();
    }
    public double getVarianceZ(){
        return statisticZ.getVariance();
    }

    public double getCovarianceXY(){
        return covariance.covariance(x,y);
    }

    public double getCovarianceYZ(){
        return covariance.covariance(y,z);
    }

    public double getCovarianceZX(){
        return covariance.covariance(z,x);
    }

    public double getZeroCrossRateX(){
        int numZC=0;
        int size=x.length;

        for (int i=0; i<size-1; i++){
            //if((x[i]>=0 && x[i+1]<0) || (x[i]<0 && x[i+1]>=0)){
            if((x[i]*x[i+1]<=0)){
                numZC++;
            }
        }
        //return (double)numZC/WINDOWN_LENGTH_IN_SECOND;
        return (double)numZC/2;
    }

    public double getZeroCrossRateY(){
        int numZC=0;
        int size=y.length;

        for (int i=0; i<size-1; i++){
            //if((y[i]>=0 && y[i+1]<0) || (y[i]<0 && y[i+1]>=0)){
            if((y[i]*y[i+1]<=0)){
                numZC++;
            }
        }
        return (double)numZC/2;
    }

    public double getZeroCrossRateZ(){
        int numZC=0;
        int size=z.length;

        for (int i=0; i<size-1; i++){
            if((z[i]*z[i+1]<=0)){
                numZC++;
            }
        }
        return (double)numZC/2;
    }

    public double getMeanXYZ(){
        double[] xyz = new double[x.length];
        int lenght = xyz.length;
        for (int i = 0; i < lenght; i++){
            xyz[i] = x[i] + y[i] + z[i];
        }
        return new DescriptiveStatistics(xyz).getMean();
    }

    // New feature 20170802 adding 8 feature
    public double getPARX(){
        //DOCME18
        double PAR =0;
        double avg = statisticX.getMean();
        double m = statisticX.getMax();
        if (avg !=0 ){
            PAR = m/avg;

        }
        return PAR;

    }
    //
    public double getPARY(){
        //DOCME18
        double PAR =0;
        double avg = statisticY.getMean();
        double m = statisticY.getMax();
        if (avg !=0 ){
            PAR = m/avg;

        }
        return PAR;

    }
    //
    public double getPARZ(){
        //DOCME18
        double PAR =0;
        double avg = statisticZ.getMean();
        double m = statisticZ.getMax();
        if (avg !=0 ){
            PAR = m/avg;

        }
        return PAR;

    }


    public double getSMA() {
        //DOCME: function (28)

        double total = 0;
        int windowsize = x.length;
        for (int i = 2; i < windowsize; i++) {

            double firstArg = Math.abs(x[i-1]) + Math.abs(x[i])
                    + Math.abs(y[i-1]) + Math.abs(y[i])
                    + Math.abs(z[i-1]) + Math.abs(z[i]);
            double secondArg = timestamp[i] - timestamp[i-1];
            total = total + firstArg*secondArg;
        }

        return ((1 * total) / (2*windowsize));

    }
    public double getSMAX() {
        //DOCME: function (28)

        double total = 0;
        int windowsize = x.length;
        //    System.out.print("\nCua so la: "+windowsize);
        for (int i = 2; i < windowsize; i++) {
            double firstArg = Math.abs(x[i-1]) + Math.abs(x[i]);
            double secondArg = timestamp[i] - timestamp[i-1];
            total = total + firstArg*secondArg ;

        }

        return ((1 * total) / (2*windowsize));

    }
    public double getSMAY() {
        //DOCME: function (28)

        double total = 0;
        int windowsize = x.length;
        for (int i = 1; i < windowsize; i++) {

            double firstArg =  Math.abs(y[i-1]) + Math.abs(y[i]);
            double secondArg = timestamp[i] - timestamp[i-1];
            total = total + firstArg*secondArg ;
        }
        return ((1 * total) / (2*windowsize));
    }
    public double getSMAZ() {
        //DOCME: function (28)

        double total = 0;
        int windowsize = x.length;
        for (int i = 2; i < windowsize; i++) {

            double firstArg = Math.abs(z[i-1]) + Math.abs(z[i]);
            double secondArg = timestamp[i] - timestamp[i-1];
            total = total +  firstArg * secondArg;
        }
        return ((1 * total) / (2*windowsize));
    }

    public double getDSVM() {
        //DOCME: function (33)
        int windowsize = x.length;

        double total = 0;
        for (int i = 2; i < windowsize; i++) {

            double ax = Math.abs(x[i] - x[i-1]);
            double ay = Math.abs(y[i] - y[i-1]);
            double az = Math.abs(z[i] - z[i-1]);

            total += ax + ay + az;
        }

        return (total / windowsize);
    }

    // calculate Hjorth parameter values Activity Mobility and Complexity
    public double getACTIVITY(String Kind){// return acvitity value depend on x , y or z
        int windowsize = x.length;
        String _Kind = Kind;
        double A =0;
        double d=0;
        double tmp = 0;
        for (int i = 0; i<windowsize -1 ;i ++){
            switch(_Kind){

                case "X":
                    tmp =  x[i+1]-x[i];
                    break;
                case "Y":
                    tmp =  y[i+1]-y[i];
                    break;
                case "Z":
                    tmp =  z[i+1]-z[i];
                    break;
                case "P":
                    tmp =  phiAngle(i+1)-phiAngle(i);
                    break;
                case "T":
                    tmp =  thetaAngle(i+1)-thetaAngle(i);
                    break;
                default:
                    break;
            }

            d = d +  Math.pow(tmp, 2);

        }
        A = d/(windowsize -1);
        return A;

    }

    // Mobility function
    public double getMOBILITY(String Kind){// return acvitity value depend on x , y or z
        int windowsize = x.length;
        String _Kind = Kind;
        double M =0;
        double m=0;
        double tmp = 0;
        for (int i = 0; i<windowsize -2 ;i ++){
            switch(_Kind){

                case "X":
                    tmp =  x[i+2] + x[i];
                    break;
                case "Y":
                    tmp =  y[i+2] + y[i];
                    break;
                case "Z":
                    tmp =  z[i+2] + z[i];
                    break;
                case "P":
                    tmp =  phiAngle(i+2) + phiAngle(i);
                    break;
                case "T":
                    tmp =  thetaAngle(i+2) + thetaAngle(i);
                    break;
                default:
                    break;
            }

            m = m + Math.pow(tmp,2);

        }

        m= m/(windowsize-2);
        double ac = getACTIVITY(_Kind);
        if(ac !=0){
            M = Math.sqrt(m/ac);
        }

        return M;

    }

    // Get Complexity function
    public double getCOMPLEXITY(String Kind){// return acvitity value depend on X, Y or Z
        int windowsize = x.length;
        String _Kind = Kind;
        double M =0;
        double m=0;
        double tmp = 0;
        for (int i = 0; i<windowsize -3 ;i ++){
            switch(_Kind){

                case "X":
                    tmp =  x[i+3] -x[i+2] + x[i+1]-x[i];
                    break;
                case "Y":
                    tmp =  x[i+3] -x[i+2] + x[i+1]-x[i];
                    break;
                case "Z":
                    tmp =  x[i+3] -x[i+2] + x[i+1]-x[i];
                    break;
                case "P":
                    tmp =  phiAngle(i+3) -phiAngle(i+2) + phiAngle(i+1)-phiAngle(i);
                    break;
                case "T":
                    tmp =  thetaAngle(i+3) -thetaAngle(i+2) + thetaAngle(i+1)-thetaAngle(i);
                    break;
                default:
                    break;
            }

            m = m + Math.pow(tmp,2);

        }

        m= m/(windowsize-3);
        double mb = getMOBILITY(_Kind);
        if(mb !=0){
            M = Math.sqrt(m/mb);
        }

        return M;

    }
    //************************The values of angle************************************//

    public double phiAngle(int i){ // return value of phi at k

        double phi = 0;

        if(Math.sqrt(Math.pow(x[i], 2)+ Math.pow(z[i],2 ))!=0){
            phi = Math.atan(y[i] / Math.sqrt(Math.pow(x[i], 2)+ Math.pow(z[i],2 )));
        }

        if(phi != 0){
            phi = 1/phi;
        }
        //System.out.print("\nGia tri Phi la: "+phi);
        return phi;
    }

    // value of theta

    public double thetaAngle(int i){ // return value of phi at k

        double theta = 0;
        if(z[i]!=0){
            //theta = Math.tan(y[i] / z[i]);
            theta = Math.atan(-x[i] / z[i]);
        }

        if(theta != 0){
            theta = 1/theta;
        }

        return theta;
    }
// Ig phi

    public double igPhi(int i ){// prepared  for getIgPhi function
        double iphi =0;

        if((phiAngle(i)+ phiAngle(i-1))* (timestamp[i]-timestamp[i-1])!=0){
            iphi =  1/(2* (phiAngle(i)+ phiAngle(i-1))* (timestamp[i]-timestamp[i-1]));
        }

        //System.out.print("\nGia tri iphi la: "+ iphi);
        return iphi;
    }

    //
    public double igTheta(int i ){// prepared  for getIgTheta function
        double iTheta =0;

        if((thetaAngle(i)+ thetaAngle(i-1))* (timestamp[i]-timestamp[i-1])!=0){
            iTheta = 1/(2* (thetaAngle(i)+ thetaAngle(i-1))* (timestamp[i]-timestamp[i-1]));
        }

        return iTheta;
    }

    // Mean phi DOCME 14
    public double getMeanPhi(){
        double mphi =0;
        int wsize = x.length;
        for (int i = 1; i<wsize;i++){
            mphi = mphi + phiAngle(i);

        }
        return mphi/wsize;
    }
    // Mean theta DOCME 14
    public double getMeanTheta(){

        double mtheta =0;
        int wsize = x.length;
        for (int i = 1; i<wsize;i++){
            mtheta = mtheta + thetaAngle(i);

        }
        return mtheta/wsize;
    }
// The variance of phi angle

    public double getVariancePhi(){
        double vphi =0;
        double tmp = 0;
        int wsize = x.length;
        for (int i = 0; i<wsize;i++){
            tmp = tmp + Math.pow(phiAngle(i) + getMeanPhi(), 2) ;
        }
        vphi = tmp/wsize;
        return vphi;
    }
    // The variance of theta angle
    public double getVarianceTheta(){
        double vtheta =0;
        double tmp = 0;
        int wsize = x.length;
        for (int i = 0; i<wsize;i++){
            tmp = tmp + Math.pow(thetaAngle(i) + getMeanTheta(), 2) ;
        }
        vtheta = tmp/wsize;
        return vtheta;
    }
    // the velocity angle changing value base on integrated
    public double getIgPhi(){ //
        double igphi =0;
        double tmp = 0;
        int wsize = x.length;
        for (int i = 1; i<wsize; i++){
            tmp = tmp + igPhi(i);

        }
        igphi = tmp;
        return igphi;
    }

    //
    public double getIgTheta(){ //
        double igtheta =0;
        double tmp = 0;
        int wsize = x.length;
        for (int i = 1; i<wsize; i++){
            tmp = tmp + igTheta(i);
        }
        igtheta = tmp;
        return igtheta;

    }

    //**************************End of angle**********************************//

    // get the max of array for statistic
    public double getMax(double[] a) {
        //
        int size= a.length;

        double max = 0;
        for (int i = 1; i < size; i++) {
            if(a[i]>max){
                max = a[i];
            }

        }

        return max;
    }
    //
    // get the min of array
    public double getMin(double[] a) {
        //
        int size= a.length;

        double min = 500000;
        for (int i = 1; i < size; i++) {
            if(a[i]<min){
                min = a[i];
            }

        }

        return min;
    }


}
