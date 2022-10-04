package com.example.myapplication;


import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;


public class MainActivity extends AppCompatActivity implements SensorEventListener {
    static final int READ_BLOCK_SIZE = 100;
    private LineChart mChart;
    private Thread thread;
    private boolean plotData = true;
    File myInternalFile;
    private  static MainActivity instance;
    private Handler mHander = new Handler();
    EditText textmsg;
    private  static final  String TAG = "MainActivity";
    private SensorManager sensorManager;
    private Sensor sensor;
    private Sensor accelerometer;
    private static boolean started;
    private static long timeInMillis;
    private static int result_predict;
    private static double result_sub1;
    private static String result_sub1_string;
    private static String final_result;
    private float[] acc_3 = new float[4];
    private double TIME_SITE=1.35;
    private static double OVERLAP_SIZE=1.08;
    public static double WINDOW_TIME_SITE;
    private static int 	dataCount;
    private static int count = 0;
    private static int size_w = 128;
    private static double over_lap = 0.8;
    private static final double ALPHA = 0.1d;
    private static int next_windows;
    private static float[] record = new float[4];
    private static ArrayList<SimpleAccelData> rawAccelDatas =  new ArrayList<SimpleAccelData>();
    final Handler handler = new Handler();
    ArrayList<Double> XList = new ArrayList<Double>();
    ArrayList<Double> YList = new ArrayList<Double>();
    ArrayList<Double> ZList = new ArrayList<Double>();
    ArrayList<Double> TList = new ArrayList<Double>();
    FileOutputStream fos = null;
    ArrayList<Double> TestList = new ArrayList<Double>();
    private static int turn = 0;
    ArrayList<Double> X = new ArrayList<Double>();
    ArrayList<Double> Y = new ArrayList<Double>();
    ArrayList<Double> Z = new ArrayList<Double>();
    ArrayList<Double> T = new ArrayList<Double>();
    static double[] endSignal = new double[] { 0d, 0d, 0d };

    static PowerFeatures powerFeatures = new PowerFeatures();
    ArrayList<Double> Features = new ArrayList<Double>();
    Button button;
    ArrayList<Double> Feature_New = new ArrayList<Double>();
    ArrayList<double[]> RawData = new ArrayList<double[]>();
    ArrayList<AccelData> RawData_New = new ArrayList<AccelData>();
    ArrayList<double[]> arr_1 = new ArrayList<double[]>();
    ArrayList<double[]> arr_2 = new ArrayList<double[]>();

    private static  double[] arr_x;
    private static  double[] arr_y;
    private static  double[] arr_z;
    private static  double[] arr_t;

    double [] data;
    public static Classifier classifier;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(MainActivity.this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        for(int i=0; i<sensors.size(); i++){
            Log.d(TAG, "onCreate: Sensor "+ i + ": " + sensors.get(i).toString());
        }

        try {
            String modelfile="acc_3_0.8_model_11_MobiAct_44F_RF_Adroid3-6-6.model";
            InputStream inputStream = null;
            AssetManager assetManager = getAssets();
            inputStream = assetManager.open(modelfile);
            classifier = (Classifier) SerializationHelper.read(inputStream);
            //System.out.println("Model" + classifier);
            inputStream.close();
        }
        catch (Exception e) {
            System.out.println("Error in rf predict" + e);
        }
        if (accelerometer != null) {
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        }



        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File directory = contextWrapper.getDir("result" , Context.MODE_PRIVATE);

        Button view_activity = (Button) findViewById(R.id.view_act);
        view_activity.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                view_activity.setText("Xem kết quả");
                //button.setText("Thu dữ liệu");
                ViewAct();
            }
        });

        button = (Button) findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ImageView rocketImage = (ImageView) findViewById(R.id.wave_animation);
                rocketImage.setBackgroundResource(R.drawable.movie);

                Drawable rocketAnimation = rocketImage.getBackground();

                if (rocketAnimation instanceof Animatable) {
                    ((Animatable)rocketAnimation).start();

                }

                myInternalFile = new File(directory,   "activity_" + turn + ".txt" );
                try {
                    fos = new FileOutputStream(myInternalFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                button.setText("Đang thu dữ liệu");
//                RawData.clear();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        started = true;
                    }
                }, 2000);

            }
        });
        dataCount=1;
        WINDOW_TIME_SITE=TIME_SITE*1000;
    }

    //AsyncTask xử lý đa luồng
    private class UpdateActivity extends AsyncTask<Integer, Integer, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected Void doInBackground(Integer... params)  {
            for (int k = 8; k < 1000; k++){
                ProcessAndPredictFix(k);
                //k = k + 1;
               // System.out.println("" + k);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }


    public ArrayList<double[]> SlidingWindow(ArrayList<double[]> arr, int window_size , double ovevlap, int k) {
        int i = 0, j = 128;
        if (k != 0) {
            i = (int) (window_size * (1-ovevlap))*k;
            j = i + window_size;
        }
       // System.out.println("Giá trị đầu và cuối của array con là "+ i + " " + j);
        return CopyRangeFix(arr, i, j);
    }

    public void ViewAct() {


        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        StringBuilder sb = new StringBuilder();
        String [] information = new String [] {};
        try {
            File directory = contextWrapper.getDir("result" , Context.MODE_PRIVATE);
            myInternalFile = new File(directory, "activity_" + turn + ".txt");
            FileInputStream fis = new FileInputStream(myInternalFile);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            try {
                String line;
                for( int k = 0; k < 100 ; k++)
                {
                    line = bufferedReader.readLine();
                    if(line != null) {
                        sb.append(line).append(" ");
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            information = sb.toString().split(" ");
            TextView ts = (TextView) findViewById(R.id.view_text_act);
            String act ="";
            for (int m = 0; m < information.length; m++) {
                act += information[m] + " ";
                if(m == information.length-1) {
                    act += information[m] + " "+"end";
                }
            }
            ts.setText(act);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Hàm xử lý dữ liệu thô và dự đoán
    public String  ProcessAndPredictFix(int k) {
        // copy mảng
        if (RawData != null && RawData.size() != 0) {
            arr_1 = SlidingWindow(RawData,128,0.85 ,k);
            arr_2 = SlidingWindow(RawData,128,0.85 ,k+1);
           // System.out.println("Độ dài array ban đầu" + RawData.size());

            ArrayList<double []> TwoSubArray = new ArrayList<double []>();
            TwoSubArray.add(Array44Fearture(arr_1));
            TwoSubArray.add(Array44Fearture(arr_2));

            // Xử lý mảng và đưa ra kết quả double


            result_sub1_string = ActionResult((int)result_sub1);
            // Chuyển kết quả sang string
            // Kết quả cuối cùng
            final_result = FinalResultOne(result_sub1_string);
           // System.out.println("Hành động đoán được là " + final_result);
            return final_result;
        }
        else return "Chưa xác định";
    }


    //Chia mảng dữ liệu, tính tập thuộc tính
    public double[] Array44Fearture(ArrayList<double[]> arr) {
        XList = SplitArrayList(arr, "X");
        YList = SplitArrayList(arr, "Y");
        ZList = SplitArrayList(arr, "Z");
        TList = SplitArrayList(arr, "T");
        data = arrlist2arr1(Features);
        return data;
    }

    public double[] arrlist2arr1(ArrayList<Double> arr) {
        int len = 44;
        double [] arr_1 = new double[len];
        for (int i = 0; i<len; i++) {
            arr_1[i] = arr.get(i);
        }
        return arr_1;
    }

    public double[] arrlist2arr(ArrayList<Double> arr) {
        int len = arr.size();
        double [] arr_1 = new double[len];
        for (int i = 0; i<len; i++) {
            arr_1[i] = arr.get(i);
        }
        return arr_1;
    }

    //copy từ 1 mảng thành mảng con
    public ArrayList<double[]> CopyRangeFix(ArrayList<double[]> arr, int i , int j){
        ArrayList<double[]> arr_copy = new ArrayList<double[]>(arr.subList(i,j));
        return arr_copy;
    }


    // Phân 1 mảng bao gồm các đối tượng gồm 3 phần tử thành 3 mảng khác nhau tương ứng với từng loại
    public ArrayList<Double> SplitArrayList(ArrayList<double[]> arr, String type) {
        ArrayList<Double> sub_arr =  new ArrayList<Double>();
        for (int i=0; i< 128; i++) {
            switch (type) {
                case "X":
                    sub_arr.add(arr.get(i)[0]);
                case "Y":
                    sub_arr.add(arr.get(i)[1]);
                case "Z":
                    sub_arr.add(arr.get(i)[2]);
                case "T":
                    sub_arr.add(arr.get(i)[3]);
            }
        }
        return sub_arr;
    };

    // Hàm trích suất thuộc tính 44 thuộc tính
    public ArrayList<Double> Get44Features(double[] X, double[] Y, double[] Z, double[] T) {
        FeaturesStatistic fs = new FeaturesStatistic(X,Y,Z,3.0,T);
        ArrayList<Double> arr = new ArrayList<Double>();
        double ARA = fs.getWindownARA();
        double MeanX = fs.getMeanX();
        double MeanY = fs.getMeanY();
        double MeanZ = fs.getMeanZ();
        double MeanXYZ = fs.getMeanXYZ();
        double VarX = fs.getVarianceX();
        double VarY = fs.getVarianceY();
        double VarZ = fs.getVarianceZ();
        double DiffX = fs.getDistributionX();
        double DiffY = fs.getDistributionY();
        double DiffZ = fs.getDistributionZ();
        double sdX = fs.getStandardDeviationX();
        double sdY = fs.getStandardDeviationY();
        double sdZ = fs.getStandardDeviationZ();
        double CovarianceXY = fs.getCovarianceXY();
        double CovarianceYZ = fs.getCovarianceYZ();
        double CovarianceZX = fs.getCovarianceZX();
        double ZeroCrossingX = fs.getZeroCrossRateX();
        double ZeroCrossingY = fs.getZeroCrossRateY();
        double ZeroCrossingZ = fs.getZeroCrossRateZ();
        double xPAR = fs.getPARX();
        double yPAR = fs.getPARY();
        double zPAR = fs.getPARZ();
        double xSMA = fs.getSMAX();
        double ySMA = fs.getSMAY();
        double zSMA = fs.getSMAZ();
        double SMA = fs.getSMA();
        double SMVD = fs.getDSVM();
        double meanPhi = fs.getMeanPhi();
        double meanTheta = fs.getMeanTheta();
        double varPhi = fs.getVariancePhi();
        double varTheta = fs.getVarianceTheta();
        double igPhi = fs.getIgPhi();
        double igTheta = fs.getIgTheta();
        double Energy = powerFeatures.getEnergy();
        double xFFTEnergy = fs.getXFFTEnergy();
        double yFFTEnergy = fs.getYFFTEnergy();
        double zFFTEnergy = fs.getZFFTEnergy();
        double xFFTEntropy = fs.getXFFTEntropy();
        double yFFTEntropy = fs.getYFFTEntropy();
        double zFFTEntropy = fs.getZFFTEntropy();
        double Activity = powerFeatures.getActivity();
        double Complexity = powerFeatures.getComplexity();
        double Mobility = powerFeatures.getMobility();
        arr.add(ARA);
        arr.add(MeanX);
        arr.add(MeanY);
        arr.add(MeanZ);
        arr.add(MeanXYZ);
        arr.add(VarX);
        arr.add(VarY);
        arr.add(VarZ);
        arr.add(DiffX);
        arr.add(DiffY);
        arr.add(DiffZ);
        arr.add(sdX);
        arr.add(sdY);
        arr.add(sdZ);
        arr.add(CovarianceXY);
        arr.add(CovarianceYZ);
        arr.add(CovarianceZX);
        arr.add(ZeroCrossingX);
        arr.add(ZeroCrossingY);
        arr.add(ZeroCrossingZ);
        arr.add(xPAR);
        arr.add(yPAR);
        arr.add(zPAR);
        arr.add(xSMA);
        arr.add(ySMA);
        arr.add(zSMA);
        arr.add(SMA);
        arr.add(SMVD);
        arr.add(meanPhi);
        arr.add(meanTheta);
        arr.add(varPhi);
        arr.add(varTheta);
        arr.add(igPhi);
        arr.add(igTheta);
        arr.add(Energy);
        arr.add(xFFTEnergy);
        arr.add(yFFTEnergy);
        arr.add(zFFTEnergy);
        arr.add(xFFTEntropy);
        arr.add(yFFTEntropy);
        arr.add(zFFTEntropy);
        arr.add(Activity);
        arr.add(Complexity);
        arr.add(Mobility);
        return arr;
    }

    public static int Count(){
        int i = 0;
        int j = 128;
        int n = 128;
        double override = 0.8;
        int length = 512;
        int count = 1;
        while (j < length){
            i = (int)(1-override)*n*count;
            j = i + n;
            count = count + 1;

        }
        return count;
    }

    public ArrayList<Double> CreateTestData(double [] arr) {
        ArrayList<Double> new_arr = new ArrayList<Double>();
        for (int i = 0; i < arr.length; i++) {
            new_arr.add(i, arr[i] );
        }
        return new_arr;
    }


    // Hàm tạo instance và dự đoán kết quả với mẫu 44 thuộc tính
    //ArrayList<Double> features
    public double predict44RF(double [] features){
        FastVector classAttributeVector = new FastVector();
        String[] classValues = {"BSC","CHU","CSI","CSO","FKL","FOL","JOG","JUM","LYI","SCH","SDL","SIT","STD","STN","STU","WAL"};
        classAttributeVector.addElement(classValues[0]);
        classAttributeVector.addElement(classValues[1]);
        classAttributeVector.addElement(classValues[2]);
        classAttributeVector.addElement(classValues[3]);
        classAttributeVector.addElement(classValues[4]);
        classAttributeVector.addElement(classValues[5]);
        classAttributeVector.addElement(classValues[6]);
        classAttributeVector.addElement(classValues[7]);
        classAttributeVector.addElement(classValues[8]);
        classAttributeVector.addElement(classValues[9]);
        classAttributeVector.addElement(classValues[10]);
        classAttributeVector.addElement(classValues[11]);
        classAttributeVector.addElement(classValues[12]);
        classAttributeVector.addElement(classValues[13]);
        classAttributeVector.addElement(classValues[14]);
        classAttributeVector.addElement(classValues[15]);
        FastVector atts = new FastVector();
        atts.addElement(new Attribute("ARA"));
        atts.addElement(new Attribute("MeanX"));
        atts.addElement(new Attribute("MeanY"));
        atts.addElement(new Attribute("MeanZ"));
        atts.addElement(new Attribute("MeanXYZ"));
        atts.addElement(new Attribute("VarX"));
        atts.addElement(new Attribute("VarY"));
        atts.addElement(new Attribute("VarZ"));
        atts.addElement(new Attribute("DiffX"));
        atts.addElement(new Attribute("DiffY"));
        atts.addElement(new Attribute("DiffZ"));
        atts.addElement(new Attribute("sdX"));
        atts.addElement(new Attribute("sdY"));
        atts.addElement(new Attribute("sdZ"));
        atts.addElement(new Attribute("CovarianceXY"));
        atts.addElement(new Attribute("CovarianceYZ"));
        atts.addElement(new Attribute("CovarianceZX"));
        atts.addElement(new Attribute("ZeroCrossingX"));
        atts.addElement(new Attribute("ZeroCrossingY"));
        atts.addElement(new Attribute("ZeroCrossingZ"));
        atts.addElement(new Attribute("xPAR"));
        atts.addElement(new Attribute("yPAR"));
        atts.addElement(new Attribute("zPAR"));
        atts.addElement(new Attribute("xSMA"));
        atts.addElement(new Attribute("ySMA"));
        atts.addElement(new Attribute("zSMA"));
        atts.addElement(new Attribute("SMA"));
        atts.addElement(new Attribute("SMVD"));
        atts.addElement(new Attribute("meanPhi"));
        atts.addElement(new Attribute("meanTheta"));
        atts.addElement(new Attribute("varPhi"));
        atts.addElement(new Attribute("varTheta"));
        atts.addElement(new Attribute("igPhi"));
        atts.addElement(new Attribute("igTheta"));
        atts.addElement(new Attribute("Energy"));
        atts.addElement(new Attribute("XFFTEnergy"));
        atts.addElement(new Attribute("yFFTEnergy"));
        atts.addElement(new Attribute("zFFTEnergy"));
        atts.addElement(new Attribute("xFFTEntropy"));
        atts.addElement(new Attribute("yFFTEntropy"));
        atts.addElement(new Attribute("zFFTEntropy"));
        atts.addElement(new Attribute("Activity"));
        atts.addElement(new Attribute("Complexity"));
        atts.addElement(new Attribute("Mobility"));
        atts.addElement(new Attribute("label", classAttributeVector));
        // 2. create Instances object
        Instances testing_data = new Instances("acc_1_0.5_model_11_UpFall_44F", atts, 0);
        double vals[] = new double[testing_data.numAttributes()];
        ArrayList<Double> feature_test = new ArrayList<Double>();
        // 2. create Instances object
        ArrayList<Integer> total = new ArrayList<Integer>();
        feature_test = CreateTestData(features);
//        System.out.println("kiem tra features" + feature_test.get(2));
        for(int f = 0; f <feature_test.size(); f++) {
            vals[f] = feature_test.get(f);
        }

        feature_test.clear();
        testing_data.add(new Instance(1, vals));
        testing_data.setClassIndex(testing_data.numAttributes() - 1);
        Instance newInst = testing_data.instance(0);
        testing_data.delete();
        try {
            result_predict = (int) classifier.classifyInstance(newInst);

            total.add(result_predict);
        } catch (Exception e) {
            System.out.println("lỗi này" + e.toString());
        }
        return result_predict;



//        if(total.get(0) == 0) {
//            return 0;
//        }
//        else if (total.get(0) == 1) {
//            return 1;
//        }
//        else if (total.get(0) == 2) {
//            return 2;
//        }
//        else if (total.get(0) == 3) {
//            return 3;
//        }
//        else if (total.get(0) == 4) {
//            return 4;
//        }
//        else if (total.get(0) == 5) {
//            return 5;
//        }
//        else if (total.get(0) == 6) {
//            return 6;
//        }
//        else if (total.get(0) == 7) {
//            return 7;
//        }
//        else if (total.get(0) == 8 ) {
//            return 8;
//        }
//        else if (total.get(0) == 9 ) {
//            return 9;
//        }
//        else if (total.get(0) == 10) {
//            return 10;
//        }
//        else if (total.get(0) == 11) {
//            return 11;
//        }
//        else if (total.get(0) == 12) {
//            return 12;
//        }
//        else if (total.get(0) == 13) {
//            return 13;
//        }
//        else if (total.get(0) == 14) {
//            return 14;
//        }
//        else if (total.get(0) == 15) {
//            return 15;
//        }
//        else return -1;
    }



    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(2f);
        set.setColor(Color.MAGENTA);
        set.setHighlightEnabled(false);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        return set;
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (thread != null) {
            thread.interrupt();
        }
        sensorManager.unregisterListener(this);

    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (started) {
//            TestList.add((double)sensorEvent.values[0]);
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                acc_3 = lowPass(sensorEvent.values.clone(), acc_3);
            }
            if (acc_3 != null) {
                timeInMillis = System.currentTimeMillis();
                AccelData accel = new AccelData(timeInMillis,
                        acc_3[0], acc_3[1],
                        acc_3[2]);

                RawData_New.add(accel);
                dataCount++;
            }
//            if (timeInMillis - RawData_New.get(0).getTimestamp() >= WINDOW_TIME_SITE)

            if (RawData_New.size()==128) {
               System.out.println(RawData_New.size());


              // System.out.println("Size cua rawdata" + RawData_New.size());
                for (int k = 0; k < RawData_New.size() ; k++) {
                    X.add(RawData_New.get(k).getX());
                    Y.add(RawData_New.get(k).getY());
                    Z.add(RawData_New.get(k).getZ());
                    T.add((double)RawData_New.get(k).getTimestamp());
                    rawAccelDatas.add(new SimpleAccelData(RawData_New.get(k).getTimestamp(),RawData_New.get(k).getX(),RawData_New.get(k).getY(),RawData_New.get(k).getZ()));
                }

                Thread th1 = new Thread() {
                    @Override
                    public void run() {
                        double[] x = new double[0];
                        double[] y = new double[0];
                        double[] z = new double[0];
                        double[] t = new double[0];
                        x = arrlist2arr(X);
                        y = arrlist2arr(Y);
                        z = arrlist2arr(Z);
                        t = arrlist2arr(T);
                        X.clear();
                        Y.clear();
                        Z.clear();
                        T.clear();
//                int [] total = new int [] {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
                        arr_x = Arrays.copyOfRange(x, 0, size_w);
                        arr_y = Arrays.copyOfRange(y, 0, size_w);
                        arr_z = Arrays.copyOfRange(z, 0, size_w);
                        arr_t = Arrays.copyOfRange(t, 0, size_w);
                        for (int j = 0; j < 1; j++) {
                            if (j == 0) {
                                arr_x = Arrays.copyOfRange(x, 0, size_w);
                                arr_y = Arrays.copyOfRange(y, 0, size_w);
                                arr_z = Arrays.copyOfRange(z, 0, size_w);
                                arr_t = Arrays.copyOfRange(t, 0, size_w);
                            } else if (j > 0) {
                                arr_x = Arrays.copyOfRange(x, (int) (j * size_w * (1 - over_lap)), (int) (j * size_w * (1 - over_lap) + size_w));
                                arr_y = Arrays.copyOfRange(y, (int) (j * size_w * (1 - over_lap)), (int) (j * size_w * (1 - over_lap) + size_w));
                                arr_z = Arrays.copyOfRange(z, (int) (j * size_w * (1 - over_lap)), (int) (j * size_w * (1 - over_lap) + size_w));
                                arr_t = Arrays.copyOfRange(t, (int) (j * size_w * (1 - over_lap)), (int) (j * size_w * (1 - over_lap) + size_w));
                            }
                    System.out.println("do dai"  + arr_x.length);
                    for (int m = 0 ; m <arr_x.length ; m++) {
                        System.out.print(arr_x[m] + " ");

                    }
                    System.out.print("end");

                            arr_x = filter(arr_x);
                            arr_y = filter(arr_y);
                            arr_z = filter(arr_z);


                            arr_x = lowFilter(arr_x, j, endSignal[0]);
                            arr_y = lowFilter(arr_y, j, endSignal[1]);
                            arr_z = lowFilter(arr_z, j, endSignal[2]);

                            if (j < 16 - 2) {
                                endSignal[0] = arr_x[0];
                                endSignal[1] = arr_y[0];
                                endSignal[2] = arr_z[0];
                            }
                            ArrayList<SimpleAccelData> windowData;

                            windowData = getRawDataWindown(rawAccelDatas, (int) (j * 128 * (1 - 0.8)), 128);
                            powerFeatures.setAcc(windowData);
                            powerFeatures.analysisRMS();


                            Feature_New = Get44Features(arr_x, arr_y, arr_z, arr_t);

                            // double[] train_wal ={476.7147436,97.31104498,454.506224,-47.15882139,504.6584476,6824.107517,18450.7015,3661.737322,322.4910043,646.9965053,184.7021588,82.60815648,135.8333593,60.51229067,2100.988289,-2481.726962,2771.176234,0.5,0,2,2.578127255,1.445068485,-0.903982518,-1.03E+12,-7.94E+12,-8.99E+11,-9.87E+12,23.06162645,0.777305005,0.779379269,2.437232391,10.74190913,3.63E-06,1.30E-04,16.00770154,1187.74859,55811.2652,753.3248983,0.280929128,-1.573327224,-0.9932935,-3.249107644,1.007160108,1.007999668};
                            data = arrlist2arr1(Feature_New);

//                    System.out.println(data.length);
//
//                    for (int ta = 0; ta < data.length; ta++) {
//                        System.out.print(data[ta] + " ");
//                    }

                            // double dif = DisWithTraining(train_wal, data);

                            result_sub1 = predict44RF(data);
                            result_sub1_string = ActionResult((int) result_sub1);
                            final_result = FinalResultOne(result_sub1_string);
                            TextView text = findViewById(R.id.result);
                            text.setText(final_result);
                            try {
                                String lineSeparator = System.getProperty("line.separator");

                                fos.write(ActionResult((int) result_sub1).getBytes(StandardCharsets.UTF_8));
                                fos.write(lineSeparator.getBytes());

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }

                    ;


//                    switch((int)result_sub1){
//                        case 0 : total[0] +=1;
//                        case 1 : total[1] +=1;
//                        case 2 : total[2] +=1;
//                        case 3 : total[3] +=1;
//                        case 4 : total[4] +=1;
//                        case 5 : total[5] +=1;
//                        case 6 : total[6] +=1;
//                        case 7 : total[7] +=1;
//                        case 8 : total[8] +=1;
//                        case 9 : total[9] +=1;
//                        case 10 : total[10] +=1;
//                        case 11 : total[11] +=1;
//                        case 12 : total[12] +=1;
//                        case 13 : total[13] +=1;
//                        case 14 : total[14] +=1;
//                    }

                };
//                int max = total[0];
//                int index = 0;
//                for (int l = 0; l < total.length; l ++) {
//                    if (total[l] > max) {
//                        max = total[l];
//                        index = l;
//                    }
//                }


                Thread th2 = new Thread(){
                    @Override
                    public void run() {
                        ArrayList<AccelData>  Luu_AccelSensor = new ArrayList<AccelData>();
                        next_windows = (int) ((TIME_SITE-OVERLAP_SIZE)*dataCount/TIME_SITE);
//                        System.out.println("điemau"+ next_windows);
//                        System.out.println("diemcuoi" + dataCount);
                        for(int i =next_windows; i<dataCount-1; i++)
                        {

                            Luu_AccelSensor.add(RawData_New.get(i));
                        }
                        RawData_New = new ArrayList<AccelData>();
                        for(int j =0; j<Luu_AccelSensor.size(); j++) {
                            RawData_New.add(Luu_AccelSensor.get(j));

                        }
                        dataCount = RawData_New.size();
                        //System.out.println("size cua array x" + RawData_New.size());
                    }
                };

                th1.start();
                th2.start();

            }
            acc_3 = null;
        }
    }
    public static double DisWithTraining(double [] arr1, double [] arr2){
        double s = 0;
        for (int  k = 0 ; k < arr1.length; k++) {
            s = s + Math.pow((arr1[k] - arr2[k]),2);
        }
        s = Math.sqrt(s)/arr1.length;
        return s;
    }

    protected float[] lowPass(float[] input, float[] output) {
        if (output == null)
            return input;

        for (int i = 0; i < input.length; i++) {
            output[i] = input[i];
        }
        return output;
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onDestroy() {
        sensorManager.unregisterListener(MainActivity.this);
        thread.interrupt();
        super.onDestroy();
    }

    private void feedMultiple() {

        if (thread != null){
            thread.interrupt();
        }

        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true){
                    plotData = true;
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public String FinalResult(String sub1 , String sub2) {
        if (sub1.equals("BSC") && sub2.equals("BSC"))
        {
            return "BSC";
        }
        else if (sub1.equals("CHU") && sub2.equals("CHU"))
        {
            return "CHU";
        }
        else if (sub1.equals("CSI") && sub2.equals("CSI"))
        {
            return "CSI";
        }
        else if (sub1.equals("CSO") && sub2.equals("CSO"))
        {
            return "CSO";
        }
        else if (sub1.equals("FKL") && sub2.equals("FKL"))
        {
            return "FKL";
        }
        else if (sub1.equals("FOL") && sub2.equals("FOL"))
        {
            return "FOL";
        }
        else if (sub1.equals("JOG") && sub2.equals("JOG"))
        {
            return "JOG";
        }
        else if (sub1.equals("JUM") && sub2.equals("JUM"))
        {
            return "JUM";
        }
        else if (sub1.equals("SCH") && sub2.equals("SCH"))
        {
            return "SCH";
        }
        else if (sub1.equals("SDL") && sub2.equals("SDL"))
        {
            return "SDL";
        }
        else if (sub1.equals("SIT") && sub2.equals("SIT"))
        {
            return "SIT";
        }
        else if (sub1.equals("STD") && sub2.equals("STD"))
        {
            return "STD";
        }
        else if (sub1.equals("STN") && sub2.equals("STN"))
        {
            return "STN";
        }
        else if (sub1.equals("STU") && sub2.equals("STU"))
        {
            return "STU";
        }
        else if (sub1.equals("WAL") && sub2.equals("WAL"))
        {
            return "WAL";
        }
        else return "Không xác định";
    }


    public String FinalResultOne(String sub1) {
        if (sub1.equals("BSC"))
        {
            return "Ngã về phía sau khi cố gắng ngồi trên ghế";
        }
        else if (sub1.equals("CHU"))
        {
            return "Chuyển từ ngồi sang đứng";
        }
        else if (sub1.equals("CSI"))
        {
            return "Bước lên ô tô";
        }
        else if (sub1.equals("CSO"))
        {
            return "Bước ra ô tô";
        }
        else if (sub1.equals("FKL"))
        {
            return "Ngã về phía trước khi đứng, tác động đầu tiên vào đầu gối";
        }
        else if (sub1.equals("FOL"))
        {
            return "Ngã Về phía trước khi đứng, sử dụng tay để giảm bớt ngã";
        }
        else if (sub1.equals("JOG"))
        {
            return "Chạy bộ";
        }
        else if (sub1.equals("JUM"))
        {
            return "Nhảy liên tục";
        }
        else if (sub1.equals("SCH"))
        {
            return "Chuyển từ tư thế đứng sang ngồi";
        }
        else if (sub1.equals("SDL"))
        {
            return "Ngã sang một bên khi đứng, uốn cong chân";
        }
        else if (sub1.equals("SIT"))
        {
            return "Ngồi trên ghế với những chuyển động nhỏ";
        }
        else if (sub1.equals("STD"))
        {
            return "Đứng với những chuyển động tinh tế";
        }
        else if (sub1.equals("STN"))
        {
            return "Xuống cầu thang";
        }
        else if (sub1.equals("STU"))
        {
            return "Lên cầu thang";
        }
        else if (sub1.equals("WAL"))
        {
            return "Đi bộ";
        }
        else return "Không xác định";
    }

    public String ActionResult(int x) {
        switch (x) {
            case 0 : return "BSC";
            case 1 : return "CHU";
            case 2 : return "CSI";
            case 3 : return "CSO";
            case 4 : return "FKL";
            case 5 : return "FOL";
            case 6 : return "JOG";
            case 7 : return "JUM";
            case 8 : return "SCH";
            case 9 : return "SDL";
            case 10 : return "SIT";
            case 11 : return "STD";
            case 12 : return "STN";
            case 13 : return "STU";
            case 14 : return "WAL";
            default : return "No activty predicted";
        }
    }

    private static ArrayList<SimpleAccelData> getRawDataWindown(ArrayList<SimpleAccelData> rawAccelDatas, int begin,
                                                                int windownLenght) {
        ArrayList<SimpleAccelData> datas = new ArrayList<>();
        for (int i = 0; i < windownLenght; i++) {
            datas.add(rawAccelDatas.get(begin + i));
        }
        return datas;
    }



    private static double[] filter(double[] arms) {
        // TODO Auto-generated method stub

        double a1, a2, b0, b1, b2 = 0;
        b1 = 1;
        b0 = 0;
        b2 = 1;
        a1 = -1.56f;
        a2 = 0.6f;
        int len = arms.length;
        double[] rs = new double[len];

        if (len > 2) {
            rs[0] = arms[0];

            rs[1] = arms[1];
        }

        for (int i = 2; i < len; i++) {
            double ar = 0;
            ar = b0 * arms[i] + b1 * arms[i - 1] + b2 * arms[i - 2] - a1 * rs[i - 1] - a2 * rs[i - 2];
            rs[i] = ar;
        }

        return rs;
    }


    private static double[] lowFilter(double[] windownX, int index, double endSignal) {
        int length = windownX.length;
        double[] windown = new double[length];
        if (index == 0) {
            windown[0] = windownX[0];
        } else {
            windown[0] = endSignal;
        }

        for (int i = 1; i < length; i++) {
            windown[i] = lowFilters(windownX[i - 1], windownX[i]);
        }
        return windown;
    }

    private static double lowFilters(double signalOutput, double signalInput) {// loc thong thap, 2.3
        return signalOutput + ALPHA * (signalInput - signalOutput);
    }
}