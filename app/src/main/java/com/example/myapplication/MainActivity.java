package com.example.myapplication;


import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
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

import java.io.InputStream;
import java.util.ArrayList;
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
    private Integer TIME_SITE=2;
    public static int WINDOW_TIME_SITE;
    private static int 	dataCount;
    private int count = 0;
    float ALPHA = 0.1f;
    private static int OVERLAP_SIZE=1;
    private static int next_windows;
    private static float[] record = new float[4];
    final Handler handler = new Handler();
    ArrayList<Double> XList = new ArrayList<Double>();
    ArrayList<Double> YList = new ArrayList<Double>();
    ArrayList<Double> ZList = new ArrayList<Double>();
    ArrayList<Double> TList = new ArrayList<Double>();

    ArrayList<Double> TestList = new ArrayList<Double>();

    ArrayList<Double> X = new ArrayList<Double>();
    ArrayList<Double> Y = new ArrayList<Double>();
    ArrayList<Double> Z = new ArrayList<Double>();
    ArrayList<Double> T = new ArrayList<Double>();


    ArrayList<Double> Features = new ArrayList<Double>();

    ArrayList<Double> Feature_New = new ArrayList<Double>();
    ArrayList<double[]> RawData = new ArrayList<double[]>();
    ArrayList<AccelData> RawData_New = new ArrayList<AccelData>();
    ArrayList<double[]> arr_1 = new ArrayList<double[]>();
    ArrayList<double[]> arr_2 = new ArrayList<double[]>();

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
            System.out.println("Model" + classifier);
            inputStream.close();
        }
        catch (Exception e) {
            System.out.println("Error in rf predict" + e);
        }
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }

        Button button = (Button) findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                button.setText("Đang thu dữ liệu");
//                RawData.clear();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        started = true;
//                        UpdateActivity task = new UpdateActivity();
//                        task.execute();
                    }
                }, 5000);
            }
        });
        dataCount=1;
        WINDOW_TIME_SITE=TIME_SITE*1000;
        mChart = (LineChart) findViewById(R.id.chart1);
        mChart.getDescription().setEnabled(true);
        mChart.getDescription().setText("Real Time Accelerometer");
        mChart.setTouchEnabled(false);
        mChart.setDragEnabled(false);
        mChart.setScaleEnabled(false);
        mChart.setDrawGridBackground(false);
        mChart.setPinchZoom(false);
        mChart.setBackgroundColor(Color.WHITE);
        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);
        mChart.setData(data);
        Legend l = mChart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);
        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(true);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMaximum(40f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);
        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);
        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.setDrawBorders(false);
        feedMultiple();
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
                System.out.println("" + k);
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
        System.out.println("Giá trị đầu và cuối của array con là "+ i + " " + j);
        return CopyRangeFix(arr, i, j);
    }


    // Hàm xử lý dữ liệu thô và dự đoán
    public String  ProcessAndPredictFix(int k) {
        // copy mảng
        if (RawData != null && RawData.size() != 0) {
            arr_1 = SlidingWindow(RawData,128,0.85 ,k);
            arr_2 = SlidingWindow(RawData,128,0.85 ,k+1);
            System.out.println("Độ dài array ban đầu" + RawData.size());

            ArrayList<double []> TwoSubArray = new ArrayList<double []>();
            TwoSubArray.add(Array44Fearture(arr_1));
            TwoSubArray.add(Array44Fearture(arr_2));


            // Xử lý mảng và đưa ra kết quả double

            //result_sub1 = predict44RF(TwoSubArray);

            result_sub1_string = ActionResult((int)result_sub1);

            // Chuyển kết quả sang string
            /*result_sub1_string = ActionResult((int) result_sub1);
            result_sub2_string = ActionResult((int) result_sub2);*/

            // Kết quả cuối cùng
            final_result = FinalResultOne(result_sub1_string);
            System.out.println("Hành động đoán được là " + final_result);
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
//        x = arrlist2arr(XList);
//        y = arrlist2arr(YList);
//        z = arrlist2arr(ZList);
//        t = arrlist2arr(TList);
//        Features = Get44Features(x,y,z,t);
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
        int len = 128;
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
        PowerFeatures pf = new PowerFeatures();
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
        double Energy = pf.getEnergy();
        double xFFTEnergy = fs.getXFFTEnergy();
        double yFFTEnergy = fs.getYFFTEnergy();
        double zFFTEnergy = fs.getZFFTEnergy();
        double xFFTEntropy = fs.getXFFTEntropy();
        double yFFTEntropy = fs.getYFFTEntropy();
        double zFFTEntropy = fs.getZFFTEntropy();
        double Activity = fs.getACTIVITY("");
        double Complexity = fs.getCOMPLEXITY("");
        double Mobility = fs.getMOBILITY("");
        // thuộc tính bổ sung

       /* double xActivity = fs.getACTIVITY("X");
        double yActivity = fs.getACTIVITY("Y");
        double zActivity = fs.getACTIVITY("Z");
        double pActivity = fs.getACTIVITY("P");
        double tActivity = fs.getACTIVITY("T");
        double xMobility = fs.getMOBILITY("X");
        double yMobility = fs.getMOBILITY("Y");
        double zMobility = fs.getMOBILITY("Z");
        double pMobility = fs.getMOBILITY("P");
        double tMobility = fs.getMOBILITY("T");
        double xComplexity  = fs.getCOMPLEXITY("X");
        double yComplexity  = fs.getCOMPLEXITY("Y");
        double zComplexity  = fs.getCOMPLEXITY("Z");
        double pComplexity  = fs.getCOMPLEXITY("P");
        double tComplexity  = fs.getCOMPLEXITY("T");*/

        // Thêm vào mảng tập thuộc tính
        arr.add(ARA);
        //System.out.println("Ara" + ARA);
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
        vals[0] = feature_test.get(0);
        testing_data.add(new Instance(1, vals));
        testing_data.setClassIndex(testing_data.numAttributes() - 1);
        Instance newInst = testing_data.instance(0);
        try {
            result_predict = (int) classifier.classifyInstance(newInst);
            // System.out.println("Ket qua day" + result_predict);
            total.add(result_predict);
        } catch (Exception e) {
            System.out.println("lỗi này" + e.toString());
        }



        if(total.get(0) == 0) {
            return 0;
        }
        else if (total.get(0) == 1) {
            return 1;
        }
        else if (total.get(0) == 2) {
            return 2;
        }
        else if (total.get(0) == 3) {
            return 3;
        }
        else if (total.get(0) == 4) {
            return 4;
        }
        else if (total.get(0) == 5) {
            return 5;
        }
        else if (total.get(0) == 6) {
            return 6;
        }
        else if (total.get(0) == 7) {
            return 7;
        }
        else if (total.get(0) == 8 ) {
            return 8;
        }
        else if (total.get(0) == 9 ) {
            return 9;
        }
        else if (total.get(0) == 10) {
            return 10;
        }
        else if (total.get(0) == 11) {
            return 11;
        }
        else if (total.get(0) == 12) {
            return 12;
        }
        else if (total.get(0) == 13) {
            return 13;
        }
        else if (total.get(0) == 14) {
            return 14;
        }
        else if (total.get(0) == 15) {
            return 15;
        }
        else return -1;
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
    private void addEntry(SensorEvent event) {

        LineData data = mChart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }
            data.addEntry(new Entry(set.getEntryCount(), event.values[0] + 5), 0);
            data.notifyDataChanged();

            // let the chart know it's data has changed
            mChart.notifyDataSetChanged();

            // limit the number of visible entries
            mChart.setVisibleXRangeMaximum(150);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            mChart.moveViewToX(data.getEntryCount());

        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(plotData){
            addEntry(sensorEvent);
            plotData = false;
        }
//        + (sensorEvent.timestamp - System.nanoTime()));
        if (started) {

            TestList.add((double)sensorEvent.values[0]);
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
            count++;

            if (timeInMillis - RawData_New.get(0).getTimestamp() >= WINDOW_TIME_SITE) {
                double [] x = new double[0];
                double [] y = new double[0];
                double [] z = new double[0];
                double [] t = new double[0];
                //System.out.println("diem dau" + RawData_New.get(0).getX());
                for (int k = 0; k < RawData_New.size() ; k++) {
                    X.add(RawData_New.get(k).getX());
                    Y.add(RawData_New.get(k).getY());
                    Z.add(RawData_New.get(k).getZ());
                    T.add((double)RawData_New.get(k).getTimestamp());

                }
                // in mảng để kiểm tra xem có đè dữ liệu không
                for (int k = 0; k < RawData_New.size() ; k++) {
                    System.out.println(RawData_New.get(k).getX() + "");
                }
                //               System.out.println("diem dau" + X.get(0));
                x = arrlist2arr(X);
                y = arrlist2arr(Y);
                z = arrlist2arr(Z);
                t = arrlist2arr(T);
                X.clear();
                Y.clear();
                Z.clear();
                T.clear();
                //System.out.println("x_test" + TestList.get(0));
                //System.out.println("x_that" + x[0]);
                //System.out.println("x" + x[3]);
                Feature_New = Get44Features(x,y,z,t);
                data = arrlist2arr1(Feature_New);
                result_sub1 = predict44RF(data);
                result_sub1_string = ActionResult((int)result_sub1);
                final_result = FinalResultOne(result_sub1_string);
                TextView text = (TextView) findViewById(R.id.result);
                text.setText(final_result);
                //System.out.println("Hành động đoán được là " + final_result);
                ArrayList<AccelData>  Luu_AccelSensor = new ArrayList<AccelData>();
                next_windows = (int) ((TIME_SITE-OVERLAP_SIZE)*dataCount/TIME_SITE);
                for(int i =next_windows; i<dataCount-1; i++)
                {
                    Luu_AccelSensor.add(RawData_New.get(i));
                }
                RawData_New = new ArrayList<AccelData>();
                //System.out.println("new size" + RawData_New.size());
                for(int j =0; j<Luu_AccelSensor.size(); j++) {
                    RawData_New.add(Luu_AccelSensor.get(j));
                }
                //System.out.println("X mới" + RawData_New.get(0).getX());
                //System.out.printf("Size: " + dataCount + "-" + RawData_New.size() + "/" + next_windows + "\n");
                dataCount = RawData_New.size();
            }
            acc_3 = null;
        }
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
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
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
}