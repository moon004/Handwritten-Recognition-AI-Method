package jobs.mnistandroid;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import static android.graphics.Color.WHITE;

public class Processing {
    private static final String Tag = "Processing Debug";



    public int pixels[];
    public int pixels_len;
    public Bitmap src;
    public int numbass;
    private int height;
    private int width;
    public Processing() {

    }


//    public static List<Object> winpopulate(int pos, int[] pixels, Bitmap src){
//        // pixels.length = width x height
//        // height = pixels.length/width
//        // height is quotient, width is remainder  1.) change from pos into coordinates
//        // pos, init_posy wont change, posx, posxm, posy, mov_pos,mov_posm will change.
//        int size_xr = 0;
//        int size_xl = 0;
//        int size_y = 0;
//        boolean first = true;
//        int mov_pos = pos;
//        int mov_posm = pos;
//        int[][] src_xy = new int[width][height];
//        int posx = mov_pos % width; // Remainder
//        int posxm = mov_posm % width; // mirror of posx used to check left
//        int posy = mov_pos / width; // Quotient
//        int init_posy = pos / width; // initial posy pointer, value wont change
//        int init_posx = pos % width;
//        int pixvaluer = (src.getPixel(posx,posy) & 0xff)/255;
//        int pixvaluel  = (src.getPixel(posxm,posy) & 0xff)/255;
//
//        while(true){
//
//            boolean gotblkpix = false;
//
//            while(pixvaluer == 0 && first){ // First iteration where size_y = 0
//                size_xr++;
//                mov_pos++;
//                posx = checkpos(mov_pos, false);
//                pixvaluer = (src.getPixel(posx,posy) & 0xff)/255;
//            }
//            while((mov_pos <= pos + size_xr) && !first){ // 2nd iterations onwards, check right
//                if (pixvaluer == 0 && posx == init_posx + size_xr)
//                {
//                    size_xr++;
//                }
//                mov_pos++;
//                posx = checkpos(mov_pos, false);
//                pixvaluer  = (src.getPixel(posx,posy) & 0xff)/255;
//            }
//            while((mov_posm >= pos - size_xl) && !first){ // 2nd iterations onwards, check left
//                if (pixvaluel == 0 && posxm == init_posx - size_xl)
//                {
//                    size_xl++;
//                }
//                mov_posm--;
//                posxm = checkpos(mov_posm, false);
//                pixvaluel  = (src.getPixel(posxm,posy) & 0xff)/255;
//            } //                            != 0 is 1 also equal to white
//
//
//            for (int i=init_posx-size_xl;i<=init_posx+size_xr;i++)
//            {   int range_i = Math.min(height*width, Math.max(i,0));
//                if ((src.getPixel(range_i,posy)& 0xff)/255 == 0)
//                {
//                    gotblkpix = true;
//                }
//
//            }
//            if (!gotblkpix) // If gotblkpix is true then skip this loop
//            {
//                break;
//            }
//            else
//            {
//                mov_pos = pos; // move the x pointer back to the original place (pos)
//                first = false;
//                posy++;
//                size_y++;
//            }
//        }
//        int size_xlr = size_xl + size_xr;
//        List<Object> retelem = new ArrayList<Object>();
//                                //The beginning x,y to read        the origin point, x ,     y  ,   width,     height
//        Bitmap cropwindow = Bitmap.createBitmap(src, range(init_posx - size_xl), init_posy, size_xlr, size_y);
//        Rect windowrect = new Rect(range(init_posx-size_xl),init_posy,init_posx+size_xr,init_posy+size_y);
//        Bitmap rescrop = Bitmap.createScaledBitmap(cropwindow,28,28,false);
//        retelem.add(rescrop);
//        retelem.add(windowrect);
//        retelem.add(size_y);
//        return retelem;
//    }
//    public static ArrayList<Object> winpopulate1(int pos, Bitmap src) {
//        /*
//            put black pix into array list, then eliminate non connected pixel.
//            create function to check connected component.
//            scan all the black pixels, save their posx and posy into arraylist.
//            sort the arraylist, then segment each digit into different bitmap
//            then we apply neighbour scale down to 28x28 bitmap for each bitmap.
//            return, bitmap, and the changed of original bitmap
//         */
//        int size_xr = 0;
//        int size_xl = 0;
//        int size_y = 0;
//        int mov_pos = pos;
//        int mov_posm = pos;
//        int[][] src_xy = new int[width][height];
//        int posx = mov_pos % width; // Remainder
//        int posy_u = mov_pos / width; // Quotient
//        int posy_d = mov_pos / width;
//        int init_posy = pos / width; // initial posy pointer, value wont change
//        int init_posx = pos % width;
//        ArrayList<ArrayList<String>> pix_draw = new ArrayList<ArrayList<String>>();
//        while(mov_pos >= 0 && mov_pos <= width*height)
//        {
//            int blkpixu = checkpix(posx,posy_u,src); // check for blk pix for up
//            int blkpixd = checkpix(posx, posy_d,src); // check for blk pix for down
//            if (blkpixu == 0){
//                int Z_O_u = (1 - checkpix(posx,posy_u + 1,src));
//
//
//                src.setPixel(posx,posy_u,0xffffffff); //Set posx,posy to be white
//                posy_u = posy_u + (1 - Z_O_u);
//            }
//            if (blkpixd == 0){
//                int Z_O_d = (1 - checkpix(posx,posy_d - 1,src));
//
//                src.setPixel(posx,posy_d, 0xfffffff);
//                posy_d = posy_d + (1 - Z_O_d);
//            }
//
//
//        }
//
//        int size_xlr = size_xl + size_xr;
//        int draw_width = range(init_posx - size_xl) + size_xlr;
//        int draw_height = size_y;
//
//        ArrayList<Bitmap> bmparray = new ArrayList<Bitmap>();
//        ArrayList<Object> retelem = new ArrayList<Object>();
//        for (ArrayList<Integer> elem_pix : pix_draw) {
//            int pix_width = checkpos(elem_pix.size(),false);
//            int pix_height = checkpos(elem_pix.size(),true);
//            Bitmap bitdraw = Bitmap.createBitmap(pix_width,pix_height, Bitmap.Config.ARGB_8888);
//            for (int i = 0; i <= elem_pix.size(); i++) {
//                posx = checkpos(i - pos, false);
//
//            }
//
//            bmparray.add(bitdraw);
//        }
//        retelem.add(src);
//        return retelem;
//    } // End bracket for winpopulate1 function

    // check for connected points.


    public static int range(int mov_pos, int size) {
        return Math.min(size,Math.max(mov_pos,0)); //set range of mov_pos to be 0 <= mov_pos <= height*width
    }

    public Processing(Bitmap src1){
         this.src = src1;
        this.height = src.getHeight();
        this.width = src.getWidth();
        int pixels[] = new int[height*width];
        src.getPixels(pixels,0,width,0,0,width,height);
    }
    public Integer checkpos(int mov_pos, boolean Q_R){
        int a = range(mov_pos,this.height*this.width);
        int posvalue = Q_R ? a / this.width : a % this.width;
        return posvalue;
    }


    public static float[] checkbitmap(Bitmap src){
        int height = src.getHeight();
        int width = src.getWidth();
        int bwpixels[] = new int[height*width];
        float flpix[] = new float[height*width];
        src.getPixels(bwpixels,0,width,0,0,width,height);
        for (int l = 0; l<= bwpixels.length-1; l++) {
            flpix[l] = (float) ((0xff & bwpixels[l])/255.0);
        }
        return flpix;
    }


    public boolean checkconn(int pos){
        int width= src.getWidth();
        int height = src.getHeight();
        int posx = pos % width;  // current Main pointer of position x
        int posy = pos / width; // current Main pointer of position y
        int check2 = src.getPixel(Math.min(posx+1,width-1),posy);  // check right
        int check3 = src.getPixel(posx,Math.min(posy+1,height-1)); // check bottom
        int check4 = src.getPixel(Math.max(posx-1,0),posy);  //check left
        if ((check2 * check3 * check4) == 0) {
            return true;
        } else {
            return false;
        }
    }

                        //the pos here is sure black ald
    public ArrayList<Integer> thechecker(int posx, int posy, int numbass, List<ArrayList<Integer>> pn){
        int left = getleft(posx, posy,this.src);
        int top = gettop(posx,posy,this.src);
        if (left == WHITE && top == WHITE){ // if new pixel, we add the pixel color by 1

            src.setPixel(posx, posy, numbass);

        } else { // if got checked pixel, we take the old value
            src.setPixel(posx,posy,Math.min(left,top));
        }                                     // Take the last 1 and compare with current left and top
        if (left != top && (left != WHITE && top != WHITE) ){
            while(src.getPixel(posx,posy-1) != WHITE && posy>=0) //vertical checking, if not white set lowest.
            {
                src.setPixel(posx,posy-1, Math.min(left,top));
                posy -= 1;
            }
            MainActivity.numbassign = Math.min(left,top);
            if (left > top) {
                return new ArrayList<Integer>(Arrays.asList(Math.min(left, top), Math.max(left, top))); //always (min,max)
            } else {
                return null;
            }
        }
     return null;
    }
    public static List<ArrayList<Integer>> checkrepeated(List<ArrayList<Integer>> pn){
        pn.remove(0);
        for(int i = 0; i<= pn.size()-1;i++){
            for(int j=i+1; j<=pn.size()-1;j++) {
                if (pn.get(i).equals(pn.get(j))) {
                    pn.remove(j);
                    j--;
                }
            }
        }
        return pn;
    }
    public int resetnumbass(int posx,int posy, int numbass){
        int left = src.getPixel(Math.max(posx - 1,0), posy);
        int top = src.getPixel(posx,Math.max(posy-1,0));
        return left == WHITE && top == WHITE ? ++numbass:Math.min(left,top);
    }
    public Bitmap getsrc(){
        return this.src;
    }
    public static int getleft(int posx, int posy, Bitmap src){
        return posx > 0 ? (src.getPixel(posx - 1, posy)):WHITE;//prevent checking most left line
    }
    public static int gettop(int posx, int posy, Bitmap src){
        return posy > 0 ? (src.getPixel(posx,posy-1)):WHITE;//prevent checking top line
    }


}
