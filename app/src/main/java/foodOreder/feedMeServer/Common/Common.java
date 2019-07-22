package foodOreder.feedMeServer.Common;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import foodOreder.feedMeServer.Interface.RetrofitClient;
import foodOreder.feedMeServer.Model.Request;
import foodOreder.feedMeServer.Model.User;
import foodOreder.feedMeServer.Remote.IGeoCoordinates;
import retrofit2.Retrofit;

public class Common {
    public static User CommonUser;
    public final static String UPDATE ="Update";
    public final static String DELETE ="Delete";
    public static Request currentRequest;

    public static final String baseUrl = "https://maps.googleapis.com";

    public static IGeoCoordinates getGeoCodeService(){
     return RetrofitClient.getClient(baseUrl).create(IGeoCoordinates.class);
    }
    public static Bitmap scaleBitmap(Bitmap bitmap,int newWidth,int newHeight){
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth,newHeight,Bitmap.Config.ARGB_8888);

        float scaleX = newWidth/(float)bitmap.getWidth();
        float scaleY = newHeight/(float)bitmap.getHeight();

        float pivotX=0 , pivotY=0;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaleX,scaleY,pivotX,pivotY);
        Canvas canvas =new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap,0,0, new Paint(Paint.FILTER_BITMAP_FLAG));
        return scaledBitmap;


    }


}
