package com.example.myapplication;

import java.util.ArrayList;
import java.util.List;

public class PowerFeatures { //Data acc in a window size

    long time;
    List<SimpleAccelData> accs = new ArrayList<SimpleAccelData>();

    public List<Double> av = new ArrayList<Double>();

    int start_range = 8;
    int end_range = 20;

    public double hE, vE, V;
    public float vMobility;
    public float hMobility ;

    public float vComplexity;
    public float hComplexity ;

    public double R =0;
    public int peaks =0;
    public double S = 0;

    public double[] fftFeatures = new double[5];

    public double[] energyFeatures = new double[10];

    public double[] fftSerial = new double[16384];

    static final int MAX_STATUS = 3;
    private double[] gravity = new double[MAX_STATUS]; //M windows for avg gravity

    double mobility;
    double complexity;
    double dispose_f;

    public double[] real, img;

    public void setTime(long t){
        time = t;
    }

    public long getTime(){
        return time;
    }

    public void setAcc(List<SimpleAccelData> list){
        accs = list;
    }
    public String toString(){
        String rs= "";
        for (SimpleAccelData acc : accs) {
            rs += acc.toString()+"\n";
        }
        //return accs.toString();

        return rs;
    }

    public ArrayList<Double> getGravity(){

        ArrayList<Double> arrs = new ArrayList<Double>();

        for(int i =0; i< gravity.length; i++){
            arrs.add(i, gravity[i]);

        }

        return arrs;


    }

    public void setGravity(ArrayList<Double> _gravity){

        for (int i =1; i< _gravity.size(); i++){
            gravity[i-1]= _gravity.get(i);
            if (i>=MAX_STATUS) break;
        }


    }

    public void setGravityDefault(){

        for (int i =0; i< MAX_STATUS; i++){
            gravity[0] = 0;
        }

    }

    public double getMobility(){
        return energyFeatures[1];
    }

    public double getComplexity(){
        return energyFeatures[2];
    }

    public double getActivity(){
        return energyFeatures[0];
    }

    public double getEnergy(){
        return energyFeatures[3];
    }


    public boolean analysisRMS(){

        int len = 0;
        len = accs.size();
        //System.out.println("LEN = " + len);
        // calculate gravity for this window
        double total_g = 0.0;
        double g =0.0;

        //new features

        //	System.out.println(accs.get(0));
        for(int i =0; i<len; i++){
            SimpleAccelData acc = accs.get(i);
            //System.out.println(accs.get(i).toString());
            g = Math.sqrt(Math.pow(acc.getX(),2)+
                    Math.pow(acc.getY(),2) + Math.pow(acc.getY(),2));

            total_g += g;
        }
        g =  (len>0)?(total_g /len):0;

        //gravity[MAX_STATUS-1] = g;

        //g= avgG();

        //System.out.println("g=" + g);

        //calculate mobility
        List<Double> arms = new ArrayList<Double>();
        List<Double> d_arms = new ArrayList<Double>();

        double total_arms = 0.0;
        double M =0.0;
        double C =0.0;

        //arm

        for(int i =0; i<len; i++){
            SimpleAccelData acc = accs.get(i);
            //System.out.println(accs.get(i).toString());
            double arm = Math.sqrt(Math.pow(acc.getX(),2)+
                    Math.pow(acc.getY(),2) + Math.pow(acc.getZ(),2)) - g;

            arms.add(arm);
            total_arms += arm;
        }
        //double avg = (len>0)?total_arms/len:0;


        //calculate Activity

        double d_a =0;
        double a_1 = (len>0)?arms.get(0):0;

        for(int i =1; i<len; i++){
            double a = arms.get(i);
            d_a = a-a_1;
            d_arms.add(d_a);
            total_arms += d_a*d_a;
            a_1 = a;

        }

        len = d_arms.size();

        double A= 0;
        A= (len>1)?total_arms/(len-1):0;

        //calculate mobility, complexity

        //len = d_arms.size();
        double total_arms1 = 0;
        double total_arms2 = 0;
        double total_arms3 = 0;

        for(int i =1; i<len; i++){
            total_arms1 += Math.pow(d_arms.get(i)-d_arms.get(i-1),2);

            if(i<len-1)
                total_arms2 += Math.pow(d_arms.get(i)-d_arms.get(i-1),2);

            if(i<len-2)
                total_arms3 += Math.pow(d_arms.get(i)-d_arms.get(i-1),2);

        }

        M = (len>2)?((total_arms2*(len-1)/total_arms1)/(len-2)):0;

        C = (len>3)?((total_arms3*(len-2)/total_arms2)/(len-3)):0;

        //Energy

        for(int i =0 ; i<len; i++){
            total_arms += Math.pow(arms.get(i), 2);

        }

        double E = 0;
        E = (len>0)?total_arms/len:0;

        int index = 0;
        energyFeatures[index++] = A;
        energyFeatures[index++] = M;
        energyFeatures[index++] = C;
        energyFeatures[index++] = E;
        energyFeatures[index++] = (double)(M/A);

        //Filter step 2 a1=-1.56; a2= 0.6; parameter for 50Hz
		/*
		double a1,a2,b0,b1,b2 = 0;
		b1=0;
		b0=1;
		b2=1;
		a1=-1.8370;
		a2= 0.8493;
		*/


        double a1,a2,b0,b1,b2 = 0;
        b1=0;
        b0=1;
        b2=1;
        a1=-1.56;
        a2= 0.6;


        arms = filter(arms);


        //calculate mobility
        arms = new ArrayList<Double>();
        d_arms = new ArrayList<Double>();

        total_arms = 0.0;
        M =0.0;
        C =0.0;

        //arm

        for(int i =0; i<len; i++){
            SimpleAccelData acc = accs.get(i);
            //System.out.println(accs.get(i).toString());
            double arm = Math.sqrt(Math.pow(acc.getX(),2)+
                    Math.pow(acc.getY(),2) + Math.pow(acc.getZ(),2)) - g;
            arms.add(arm);
            total_arms += arm;
        }
        //double avg = (len>0)?total_arms/len:0;


        //calculate Activity

        d_a =0;
        a_1 = (len>0)?arms.get(0):0;

        for(int i =1; i<len; i++){
            double a = arms.get(i);
            d_a = a-a_1;
            d_arms.add(d_a);
            total_arms += d_a*d_a;
            a_1 = a;

        }

        len = d_arms.size();

        A = 0;
        A = (len>1)?total_arms/(len-1):0;

        //calculate mobility, complexity

        //len = d_arms.size();
        total_arms1 = 0;
        total_arms2 = 0;
        total_arms3 = 0;

        for(int i =1; i<len; i++){
            total_arms1 += Math.pow(d_arms.get(i)-d_arms.get(i-1),2);

            if(i<len-1)
                total_arms2 += Math.pow(d_arms.get(i)-d_arms.get(i-1),2);

            if(i<len-2)
                total_arms3 += Math.pow(d_arms.get(i)-d_arms.get(i-1),2);

        }

        M = (len>2)?((total_arms2*(len-1)/total_arms1)/(len-2)):0;

        C = (len>3)?((total_arms3*(len-2)/total_arms2)/(len-3)):0;

        //Energy

        for(int i =0 ; i<len; i++){
            total_arms += Math.pow(arms.get(i), 2);

        }


        E = (len>0)?total_arms/len:0;

        //int index = 0;
        energyFeatures[index++] = A;
        energyFeatures[index++] = M;
        energyFeatures[index++] = C;
        energyFeatures[index++] = E;
        energyFeatures[index++] = (double)(M/A);

        return true;

    }



    private List<Double> filter(List<Double> arms) {
        // TODO Auto-generated method stub

        /*
         * With 50Hz: [b0, b1, b2] = [1, 0, ï¿½1] vï¿½ [a1, a2] = [-1.56, 0.6];
         */
	/*
	double a1,a2,b0,b1,b2 = 0;
		b1=0;
		b0=1;
		b2=1;
		a1=-1.8370;
		a2= 0.8493;
		*/


        double a1,a2,b0,b1,b2 = 0;
        b1=0;
        b0=1;
        b2=1;
        a1=-1.56;
        a2= 0.6;


        List<Double> rs = new ArrayList<Double> ();
        int len = arms.size();

        if(len >2){
            rs.add(0,arms.get(0));

            rs.add(1,arms.get(1));
        }

        for(int i = 2; i< arms.size(); i++){
            double ar = 0;
            ar = b0*arms.get(i) + b1*arms.get(i-1) + b2*arms.get(i-2) - a1*rs.get(i-1) - a2*rs.get(i-2);
            rs.add(i,ar);
        }

        return rs;
    }

    private double avgG() {
// TODO Auto-generated method stub
        double total = 0 ;
        for(int i =0; i<gravity.length; i++){
            total+= gravity[i];
        }


        return total/gravity.length;
    }

    public float[] do_nghieng_vecto_giatoc_bu(float angle1, float angle2,
                                              SimpleAccelData a0_t) {
        float[] result = new float[3];
        float[] A = new float[9];
        float[] B = new float[3];
        float sinX = (float) Math.sin(angle1);
        float cosX = (float) Math.cos(angle1);
        float sinY = (float) Math.sin(angle2);
        float cosY = (float) Math.cos(angle2);

        A[0] = cosY;
        A[1] = -sinX * sinY;
        A[2] = cosX * sinY;
        A[3] = 0;
        A[4] = cosX;
        A[5] = -sinX;
        A[6] = sinY;
        A[7] = sinX * cosY;
        A[8] = cosX * cosY;

        B[0] = (float)a0_t.getX();
        B[1] = (float)a0_t.getY();
        B[2] = (float)a0_t.getZ();

        result = matrixMul_33x31(A, B);
        return result;

    }

    private float[] matrixMul_33x31(float[] A, float[] B) {
        float[] result = new float[3];

        result[0] = A[0] * B[0] + A[1] * B[1] + A[2] * B[2];
        result[1] = A[3] * B[0] + A[4] * B[1] + A[5] * B[2];
        result[2] = A[6] * B[0] + A[7] * B[1] + A[8] * B[2];

        return result;
    }

}
