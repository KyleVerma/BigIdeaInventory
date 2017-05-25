package com.thebigidea.bigideainventory;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ConfirmImage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_image);

        Mat matGray;

        File directory = this.getDir("imageDir", this.MODE_PRIVATE);

        File thisFile = new File(directory.getAbsolutePath(), "profile.jpg");

        Bitmap bmp;

        FileInputStream inputStream = null;
        try {
            //File f=new File(di, "profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(thisFile));

            Mat matBlur = new Mat();
            Mat imgMAT = new Mat();
            Mat matAdap = new Mat();
            Mat lines = new Mat();
            Mat intermediateMatLines = new Mat();
            double rho = 1;
            double theta = Math.PI/180;
            int threshold = 20;
            double minLineSize = 20;
            double lineGap = 10;
            Utils.bitmapToMat(b, imgMAT);
            Utils.bitmapToMat(b, intermediateMatLines);
            //Utils.bit

            Imgproc.cvtColor(imgMAT, imgMAT, Imgproc.COLOR_BGR2GRAY);

            Imgproc.blur(imgMAT, matBlur, new Size(3.d, 3.d));
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

            Bitmap bmp1 = Bitmap.createBitmap(intermediateMatLines.width(), intermediateMatLines.height(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(intermediateMatLines,bmp1);

            ImageView img=(ImageView)findViewById(R.id.imageView);
            img.setImageBitmap(bmp1);

        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }



    }
}
