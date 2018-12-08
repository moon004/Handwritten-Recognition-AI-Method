package jobs.mnistandroid;

/*
   Copyright 2016 Narrative Nights Inc. All Rights Reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

   From: https://raw.githubusercontent
   .com/miyosuda/TensorFlowAndroidMNIST/master/app/src/main/java/jp/narr/tensorflowmnist
   /DrawModel.java
*/

//An activity is a single, focused thing that the user can do. Almost all activities interact with the user,
//so the Activity class takes care of creating a window for you in which you can place your UI with setContentView(View)
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import android.widget.ImageView;
//PointF holds two float coordinates
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;
//A mapping from String keys to various Parcelable values (interface for data container values, parcels)
import android.os.Bundle;
//Object used to report movement (mouse, pen, finger, trackball) events.
// //Motion events may hold either absolute or relative movements and other data, depending on the type of device.
//This class represents the basic building block for user interface components.
// A View occupies a rectangular area on the screen and is responsible for drawing
import android.view.View;
//A user interface element the user can tap or click to perform an action.
import android.widget.Button;
//A user interface element that displays text to the user. To provide user-editable text, see EditText.
import android.widget.FrameLayout;
import android.widget.TextView;
//Resizable-array implementation of the List interface. Implements all optional list operations, and permits all elements,
// including null. In addition to implementing the List interface, this class provides methods to
// //manipulate the size of the array that is used internally to store the list.
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
// basic list
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
//encapsulates a classified image
//public interface to the classification class, exposing a name and the recognize function
import jobs.mnistandroid.models.Classification;
//contains logic for reading labels, creating classifier, and classifying
//class for drawing MNIST digits by finger
//class for drawing the entire app
import jobs.mnistandroid.views.DrawView;
import jobs.mnistandroid.views.ShowCamera;
import mariannelinhares.mnistandroid.R;

import android.hardware.Camera;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener{



    public String TAGD = "Debug";
    public ImageView imageView;
    // ui elements
    private Button classBtn;
    private TextView resText;
    protected int previewWidth = 0;
    protected int previewHeight = 0;
    private Bitmap cnncanvas = null;
    private Bitmap cropwindow = null;
    private List<float[]> cnnlist = new ArrayList<float[]>();
    public static int numbassign = -16777215;

    private Classifier classifier;
    Camera camera;
    FrameLayout frameLayout;
    ShowCamera showCamera;

    private static final int INPUT_SIZE = 28;

    // views

    private DrawView drawView;

    Uri file;
    @Override
    // In the onCreate() method, you perform basic application startup logic that should happen
    //only once for the entire life of the activity.
    protected void onCreate(Bundle savedInstanceState) {
        //initialization
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //SHOWING CAMERA THINGY
        frameLayout = (FrameLayout)findViewById(R.id.frameLayout);
        //Open the fcking camera
        camera = Camera.open();
        showCamera = new ShowCamera(this,camera);
        frameLayout.addView(showCamera);

        //------------own code------------------
        cnncanvas = Bitmap.createBitmap(INPUT_SIZE,INPUT_SIZE, Config.ARGB_8888);

        //class button
        //when tapped, this performs classification on the drawn image
        classBtn = (Button) findViewById(R.id.btn_class);
        classBtn.setOnClickListener(this);

        // res text
        //this is the text that shows the output of the classification
        resText = (TextView) findViewById(R.id.tfRes);

        loadModel();
        // tensorflow
        //load up our saved model to perform inference from local storage
    }



    private void loadModel() {
        //The Runnable interface is another way in which you can implement multi-threading other than extending the
        // //Thread class due to the fact that Java allows you to extend only one class. Runnable is just an interface,
        // //which provides the method run.
        // //Threads are implementations and use Runnable to call the method run().
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //add 2 classifiers to our classifier arraylist
                    //the tensorflow classifier and the keras classifier
                    classifier =
                            TensorFlowImageClassifier.create(getAssets(), "Tensorflow",
                                    "opt_mnist_convnet-tf.pb", "labels.txt", INPUT_SIZE,
                                    "input", "output");

                } catch (final Exception e) {
                    //if they aren't found, throw an error!
                    throw new RuntimeException("Error initializing classifiers!", e);
                }
            }
        }).start();
    }


    @SuppressLint("DefaultLocale")
    @Override
    public void onClick(View view) {
        //when the user clicks something
    if (view.getId() == R.id.btn_class)
        {
            if(camera!=null)
            {
                resText.setText("");
                camera.takePicture(mShutterCallback,null,mPictureCallback);

            }
        }
    }
    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera)
        {
            Bitmap photo = BitmapFactory.decodeByteArray(data, 0, data.length);//change to bitmap from camera
            Bitmap rotphoto = rotateImage(photo,90);
            Bitmap bnwphoto = drawView.createBlackAndWhite(rotphoto);  //Black and White , Bitmap
            int bnwwidth = bnwphoto.getWidth();
            int bnwheight = bnwphoto.getHeight();
            int pixels[] = new int[bnwwidth*bnwheight];
            bnwphoto.getPixels(pixels,0,bnwwidth,0,0,bnwwidth,bnwheight);
            //int b[] = new int[pixels.length];

            Processing prc = new Processing(bnwphoto);
            List<ArrayList<Integer>> blkpixarr = new ArrayList<ArrayList<Integer>>();
            List<ArrayList<Integer>> pairnumber = new ArrayList<ArrayList<Integer>>();
            pairnumber.add(new ArrayList<Integer>(Arrays.asList(-16777215,-16777215)));
            Integer result = 0;
//            for (int i = 0;i <= 480; i++){
//                for (int j=i; j <= pixels.length-1; j += bnwwidth){
//                    b[j] = (pixels[j] & 0xff) / 255;// int b is either 0(black) or 1(white)
//                    // 1. If below FALSE, straight away jump to i++
//                    // 2. we save all blk pixel coordinates into array 1st
//                    // 3. From there we separate and draw out the bitmap and then resize to 28x28.
//                    // 4. from the 28x28 bitmap we then cnnlist.add
//                    if (b[j] == 0 && Processing.checkconn(bnwphoto,j)) {
//                        Integer posy = Processing.checkpos(j, true);
//                        blkpixarr.add(new ArrayList<Integer>(Arrays.asList(i, posy)));
//                        int a = (bnwphoto.getPixel(i,posy) & 0xff)/255;
//                        System.out.println(a);
//                    }
//                }
//            }
                for (int i = 0; i<= bnwphoto.getWidth()-1; i++){
                    for (int j=i; j <= pixels.length -1; j+= bnwwidth){
                        if (((pixels[j] & 0xff)) == 0){
                            int posy = prc.checkpos(j,true);
                            blkpixarr.add(new ArrayList<Integer>(Arrays.asList(i, posy)));
                            ArrayList<Integer> prevlst = blkpixarr.get(Math.max(blkpixarr.size() - 2,0));
                            if (!nbpix(prevlst, i, posy)) {
                                if(i - prevlst.get(0) >= 2) {
                                    for(ArrayList<Integer> nb:pairnumber){
                                       numbassign = Math.max(nb.get(1),numbassign);//check get(1) enuf coz it is always the higher
                                    }
                                    numbassign++;
                                } else {
                                    numbassign = prc.resetnumbass(i,posy,numbassign);
                                }
                            }
                            ArrayList<Integer> rvalue = prc.thechecker(i,posy,numbassign,pairnumber);
                            if ((rvalue) != null){
                                pairnumber.add(rvalue);
                                numbassign = prc.resetnumbass(i,posy,numbassign);
                            }
                        }
                    }
                }
                bnwphoto = prc.getsrc(); // update the bnwphoto
                int num = 0;
                pairnumber = Processing.checkrepeated(pairnumber);
            // Check each blkpixarr x,y and equalkan pairnumber
            for (int pos = 0; pos <= blkpixarr.size()-1;pos++){
                int posx = blkpixarr.get(pos).get(0);
                int posy = blkpixarr.get(pos).get(1);
                for (int h = pairnumber.size() - 1; h >= 0 ; h--){
                    int stats = bnwphoto.getPixel(posx,posy);
                    // if bnwphoto.getpixel(posx,posy) => th 1st value,
                    if(stats == pairnumber.get(h).get(1) && stats > pairnumber.get(h).get(0)){
                        bnwphoto.setPixel(posx,posy,pairnumber.get(h).get(0));
                    }
                }
                numbassign = Math.max(bnwphoto.getPixel(posx,posy),numbassign);
            }
            for(int m=-16777215; m<= numbassign;m++){
                    int ml = bnwwidth;
                    int mr = 0;
                    int mt = bnwheight;
                    int mb = 0;
                    ReturningValues rv = safesublist(bnwphoto,blkpixarr,m); // rv.blksorted, rv.index
                    // [0] is blksorted [1] is blkpixarr
                    for (Object o : rv.blksorted){
                        ArrayList<Integer> array = (ArrayList<Integer>) o;
                        ml = Math.min(array.get(0),ml);
                        mr = Math.max(array.get(0),mr);
                        mt = Math.min(array.get(1),mt);
                        mb = Math.max(array.get(1),mb);
                    }
                    int fbmpwidth = (int)((mr - ml)*1.46);
                    int fbmpheight = (int)((mb - mt)*1.28);
                    if (fbmpwidth <= 10 && fbmpheight <= 20 ){
                        continue;
                    }
                    Bitmap finalbmp = createImage(fbmpwidth,fbmpheight,0x00000000);
                    // draw white(got value) dot on the finalbmp
                    for (Object o : rv.blksorted) {
                        ArrayList<Integer> arr = (ArrayList<Integer>) o;
                        // put white on the number pix
                        finalbmp.setPixel(arr.get(0)-ml + (int)(fbmpwidth*0.20),
                                arr.get(1)- mt + (int)(fbmpheight*0.12), 0xffffffff);
                    }
                    //need to change finalbmp to 28x28
                    finalbmp = Bitmap.createScaledBitmap(finalbmp,28,28,true);
                    // cnnpix is now flpix[]
                    float cnnpix[] = Processing.checkbitmap(finalbmp);
                    //add into the cnnlist, call it by for(float[] element : cnnlist)
                    cnnlist.add(cnnpix);
            }

            String text = "";
            //for each element in our cnnlist
            for(float[] element : cnnlist){
                //perform classification on the image   This recognize is a long function
                final Classification results = classifier.recognize(element);

                if (results != null) {
                    text += String.format("%s ", results.getLabel());
                }
        }
            String sdPresent = android.os.Environment.getExternalStorageState();
            if (sdPresent.equals("mounted")){
                generateNoteOnSD(getApplicationContext(),"FYP",text);
        }
            resText.setText(text);
            camera.startPreview();
        }
    };


    public static Bitmap createImage(int width, int height, int color) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(color);
        canvas.drawRect(0F, 0F, (float) width, (float) height, paint);
        return bitmap;
    }
    private static List<ArrayList<Integer>> magicsort(List<ArrayList<Integer>> blkpixarr){
        int arrsize = blkpixarr.size();
        List<ArrayList<Integer>> returnlist = new ArrayList<ArrayList<Integer>>();
        int j = 0;
        while (j <= arrsize)
        {
            for (int k=j ; k <= arrsize-1; k++)
            {   //add j into returnlist if neighbour is found
                if (nbcheck(blkpixarr,blkpixarr.get(k)) || nbcheck(returnlist,blkpixarr.get(k)))
                {   //if same skip the loop, blkpixarr is undisturbed
                    if (j != k) {
                        blkpixarr.add(Processing.range(k+2,arrsize),blkpixarr.get(j));
                        blkpixarr.set(j,blkpixarr.get(k));
                        blkpixarr.remove(k);
                    }
                    returnlist.add( new ArrayList<Integer>(Arrays.asList
                            (blkpixarr.get(j).get(0),blkpixarr.get(j).get(1))));
                    break;
                }
                if (k==arrsize - 1 && !nbcheck(returnlist,blkpixarr.get(k))){
                    returnlist.add(new ArrayList<Integer>(Arrays.asList
                            (blkpixarr.get(j).get(0),blkpixarr.get(j).get(1))));
                }
            }
            j++;
        } return returnlist;
    }

    public static class ReturningValues{
        public List<ArrayList<Integer>> blksorted;


        public ReturningValues(List<ArrayList<Integer>> blksorted){
            this.blksorted = blksorted;

        }
    }

    public static ReturningValues safesublist(Bitmap src, List<ArrayList<Integer>> blkpixarr,int numbass){
        List<ArrayList<Integer>> blksorted = new ArrayList<ArrayList<Integer>>(); // blksorted has been reset
            for (Integer m = 0; m <= blkpixarr.size() -1 ; m++){ // loop over blkpixarr
                int posx = blkpixarr.get(m).get(0);
                int posy = blkpixarr.get(m).get(1);
                if (src.getPixel(posx,posy) == numbass){
                    blksorted.add(new ArrayList<Integer>(Arrays.asList(posx, posy)));
                }
            }
        ReturningValues rv = new ReturningValues(blksorted);
        return rv;
        }


    private static boolean nbcheck(List<ArrayList<Integer>> al1, ArrayList<Integer> al) {

        boolean result = false;
        //need to instantiate the class in order to access non static class
        // **static method belongs to the class, non static method belong to object
        MainActivity actinstance = new MainActivity();

        for (ArrayList<Integer> elem : actinstance.reverse(al1)) {
            float distance = (float) Math.sqrt(Math.abs(al.get(0) - elem.get(0)) + Math.abs(al.get(1) - elem.get(1)));
            result = distance <= 1.5f && distance >= 1;
            if (result){
                return true;
            }

        }
        return false;
    }

    private static boolean nbpix(ArrayList<Integer> al1,int posx, int posy) {
        boolean result = false;
        //need to instantiate the class in order to access non static class
        // **static method belongs to the class, non static method belong to object

        float distance = (float) Math.sqrt(Math.abs(posx - al1.get(0)) + Math.abs(posy - al1.get(1)));
        result = (distance <= 1.5f && distance >= 1) || distance == 0;
        if (result){
            return true;
        }

        return false;
    }

    private Iterable<ArrayList<Integer>> reverse(List<ArrayList<Integer>> item) {
        List<ArrayList<Integer>> reversed = new ArrayList<>();
        for (int i = item.size() - 1; i >= 0; i--){
            reversed.add(item.get(i));
        }
        return reversed;
    }

    private final Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            AudioManager mgr = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
            mgr.playSoundEffect(AudioManager.FLAG_PLAY_SOUND);
        }
    };

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix,
                true);
    }


    public void generateNoteOnSD(Context context, String sFileName, String sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "");

            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);

            FileWriter writer = new FileWriter(gpxfile,true);
            writer.write(sBody);

            writer.flush();
            writer.close();
            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class Pair<B,RECT> {
        private final B cropwindow;
        private final RECT winrect;

        public Pair(B cropwindow, RECT winrect){
            this.cropwindow = cropwindow;
            this.winrect = winrect;
        }
        public B getcropwindow() {
            return cropwindow;
        }
        public RECT getrect() {
            return winrect;
        }

    }

}

