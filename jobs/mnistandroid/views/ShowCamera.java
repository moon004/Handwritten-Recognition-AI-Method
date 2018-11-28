package jobs.mnistandroid.views;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.util.Log;
import android.util.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;


public class ShowCamera extends SurfaceView implements SurfaceHolder.Callback
{
            Camera camera;

        SurfaceHolder holder;

        public ShowCamera(Context context,Camera camera) {
            super(context);
            this.camera = camera;
            holder = getHolder();
            holder.addCallback(this);

        }
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
                Camera.Parameters params = camera.getParameters();
                List<Camera.Size> sizes = params.getSupportedPictureSizes();
                Camera.Size mSize;
                for (Camera.Size size:sizes){
                    Log.d("Debug","Camera Available WxH: " + size.width+" "+size.height);
                    mSize = size;
                }
                params.setPictureSize(640,480);
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

                camera.setParameters(params);


            // Change orientation

            if(this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE)
            {
                params.set("orientation","Portrait");
                camera.setDisplayOrientation(90);
                params.setRotation(90);
            }
            else
            {
                params.set("orientation","landscape");
                camera.setDisplayOrientation(0);
                params.setRotation(0);
            }

            camera.setParameters(params);
            try {
                camera.setPreviewDisplay(holder);
                camera.startPreview();
            }catch(IOException e)
            {
                e.printStackTrace();
            }




        }
}
