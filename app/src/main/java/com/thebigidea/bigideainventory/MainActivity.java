package com.thebigidea.bigideainventory;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "OCVSample::Activity";
    private CameraBridgeViewBase _cameraBridgeViewBase;
    private Mat matLines;// = inputFrame.rgba();
    private Mat lines;
    private Mat matRGBA;
    private boolean pauseCamera = false;

    private BaseLoaderCallback _baseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    // Load ndk built module, as specified in moduleName in build.gradle
                    // after opencv initialization
                    System.loadLibrary("native-lib");
                    _cameraBridgeViewBase.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        // Permissions for Android 6+
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.CAMERA},
                1);

        _cameraBridgeViewBase = (CameraBridgeViewBase) findViewById(R.id.main_surface);
        _cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        _cameraBridgeViewBase.setCvCameraViewListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        disableCamera();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, _baseLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            _baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void onDestroy() {
        super.onDestroy();
        disableCamera();
    }

    public void disableCamera() {
        if (_cameraBridgeViewBase != null)
            _cameraBridgeViewBase.disableView();
    }

    public void enableCamera() {
        if (_cameraBridgeViewBase != null)
            _cameraBridgeViewBase.enableView();
    }

    public void onCameraViewStarted(int width, int height) {
    }

    public void onCameraViewStopped() {
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat matGray = inputFrame.gray();
        matRGBA = inputFrame.rgba();
        Mat intermediateMatLines = inputFrame.rgba();
        Mat matBlur = new Mat();
        Mat matAdap = new Mat();
        lines = new Mat();
        double rho = 1;
        double theta = Math.PI/180;
        int threshold = 20;
        double minLineSize = 20;
        double lineGap = 10;

        //if (pauseCamera) return matLines;
        //salt(matGray.getNativeObjAddr(), 2000);

        Imgproc.blur(matGray, matBlur, new Size(3.d, 3.d));
        Imgproc.adaptiveThreshold(matBlur, matAdap, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 15, 4);

        Imgproc.HoughLinesP(matAdap, lines, rho, theta, threshold, minLineSize, lineGap);
        //Imgproc.HoughLines(matAdap, lines, rho, theta, threshold); 


        int counter = 0;
        for (int x = 0; x < lines.rows(); x++)
        {
            double[] vec = lines.get(x, 0);
            double x1 = vec[0],
                    y1 = vec[1],
                    x2 = vec[2],
                    y2 = vec[3];
            Point start = new Point(x1, y1);
            Point end = new Point(x2, y2);
            double dx = x1 - x2;
            double dy = y1 - y2;

            double dist = Math.sqrt (dx*dx + dy*dy);

            if(dist>300.d)  // show those lines that have length greater than 300
                Imgproc.line(intermediateMatLines, start, end, new Scalar(0,0, 255, 255),2);// here initimg is the original image.

            counter++;
        }
/*
        setContentView(R.layout.activity_main);
        TextView textView = (TextView) findViewById(R.id.numLines);
        textView.setText(counter);
*/
        matLines = intermediateMatLines;

        return matLines;
    }

    public void sendMessage(View view) {
/*
        Intent intent = new Intent(this, ConfirmImage.class);
        Bundle extras = new Bundle();
        Bitmap bmp = Bitmap.createBitmap(lines.width(), lines.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(lines,bmp);
        extras.putParcelable("imageLines", bmp);
        intent.putExtra("imageLines", extras);
        startActivity(intent);

        if (pauseCamera) {
            pauseCamera = false;
            disableCamera();
        }
        else {
            pauseCamera = true;
            enableCamera();
        }
*/

        File directory = this.getDir("imageDir", this.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.jpg");

        FileOutputStream fos = null;

        Bitmap bmp = Bitmap.createBitmap(matLines.width(), matLines.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(matRGBA,bmp);

        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
                Intent intent = new Intent(this, ConfirmImage.class);
                intent.putExtra("message", directory.getAbsolutePath());
                startActivity(intent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //Intent intent = new Intent(this, ConfirmImage.class);
        //startActivity(intent);
    }

    /** Create a File for saving an image or video */
/*
    private  File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(context.getFilesDir(), filename);
        //File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
        //        + "/Android/data/"
        //        + getApplicationContext().getPackageName()
        //        + "/Files");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName="MI_"+ timeStamp +".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    private void storeImage(Bitmap image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Log.d(TAG,
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
    }
*/
    public native void salt(long matAddrGray, int nbrElem);
}

