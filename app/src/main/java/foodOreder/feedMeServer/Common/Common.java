package foodOreder.feedMeServer.Common;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import java.util.Calendar;
import java.util.Locale;

import foodOreder.feedMeServer.Interface.RetrofitClient;
import foodOreder.feedMeServer.Model.Request;
import foodOreder.feedMeServer.Model.User;
import foodOreder.feedMeServer.Remote.APIService;
import foodOreder.feedMeServer.Remote.FCMRetrofitClient;
import foodOreder.feedMeServer.Remote.IGeoCoordinates;

public class Common {
    public static User currentUser;
    public final static String UPDATE = "Update";
    public final static String DELETE = "Delete";
    public final static String ADD_TO_BANNER = "Add To BannerActivity";
    public static Request currentRequest;

    private static final String BASE_URL = "https://fcm.googleapis.com/";

    public static APIService getFCMService() {
        return FCMRetrofitClient.getClient(BASE_URL).create(APIService.class);
    }


    public static final String baseUrl = "https://maps.googleapis.com";

    public static IGeoCoordinates getGeoCodeService() {
        return RetrofitClient.getClient(baseUrl).create(IGeoCoordinates.class);
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        float scaleX = newWidth / (float) bitmap.getWidth();
        float scaleY = newHeight / (float) bitmap.getHeight();

        float pivotX = 0, pivotY = 0;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaleX, scaleY, pivotX, pivotY);
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG));
        return scaledBitmap;


    }


    public static String getDate(long time){
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        StringBuilder date = new StringBuilder(
                android.text.format.DateFormat.format("dd-MM-yyyy HH:mm", calendar).toString());

        return date.toString();

    }


}
