package iss.workshop.team8flipgame;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

import iss.workshop.team8flipgame.activity.ImagePickingActivity;
import iss.workshop.team8flipgame.model.Image;

public class ImageScraper extends AsyncTask<String, Image, Void>{

    private ICallback callback;
    private String html;

    public ImageScraper(ICallback callback){
        this.callback = callback;
    }
    @Override
    protected Void doInBackground(String... strings) {

//        try converting url to html scripts
        String html = HTMLscraper(strings[0]);
        if(html == null) {
            if(this.callback != null) {
                callback.makeToast("Check the search address, page invalid");
                return null;
            }
        }

//        convert html scripts back to image urls
        ArrayList<URL> imageURLs = imageURLs(html);
        if(imageURLs.size()< ImagePickingActivity.noOfImagesRequired()){
            if(imageURLs.size()==0){
                if(this.callback != null) callback.makeToast("There no possible images.  Please try another search term.");
                return null;
            }
            if(this.callback != null) callback.makeToast("There are only "+imageURLs.size()+" possible images.  Please try another search term.");
            return null;
        }

//        scrapping the images based on url
        scrapBitmaps(imageURLs);
        return null;
    }

    //    converting input URL to html script
    String HTMLscraper(String urlinput){
        StringBuffer sb = new StringBuffer();
        try{
            URL url = new URL(urlinput);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream inputStream = connection.getInputStream();

            Scanner scanner = new Scanner(inputStream);
            while(scanner.hasNext()){
                sb.append(scanner.nextLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return sb.toString();
    }

    //    finding all image urls in a html script
    ArrayList<URL> imageURLs(String html){
        ArrayList<URL> imageURLs = new ArrayList<>();
        int startPos = 0;
        while(true){
            // finding the img tags
            int startTagPos = html.indexOf("<img",startPos);
            int endTagPos = html.indexOf(">",startTagPos);
            if(startTagPos ==-1 || endTagPos == -1){
                break;
            }
            String imageURL = html.substring(startTagPos,endTagPos+1);
            if(imageURL.contains("http")){
                int startUrlPos = imageURL.indexOf("http");
                int endUrlPos = imageURL.indexOf("\"",startUrlPos);
                try{
                    imageURLs.add(new URL(imageURL.substring(startUrlPos,endUrlPos)));
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            startPos=endTagPos;
            //

        }
        HashSet<URL> hashUrl = new HashSet<URL>(imageURLs);
        ArrayList<URL> cleanImageURLs = new ArrayList<URL>(hashUrl);
        return cleanImageURLs;
    }


    //    method to convert image urls to bitmaps
    void scrapBitmaps(final ArrayList<URL> imageURLs){
        for (int i = 0; i< ImagePickingActivity.noOfImagesRequired(); i++){
            final URL url = imageURLs.get(i);
            new Thread(new Runnable(){
                @Override
                public void run(){
                    try{
                        downloadImage(url);
                    }catch(IOException e)
                    {
                        e.printStackTrace();
                    }}
            }).start();
        }
    }

    //    sub-method of above
    void downloadImage(URL url) throws IOException{
        if (isCancelled()) return;
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        String cookie = connection.getHeaderField( "Set-Cookie");
        connection.disconnect();
        connection = (HttpURLConnection) url.openConnection();
        cookie = cookie != null? cookie.split(";")[0] : null;
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36");
        connection.setRequestProperty("Cookie", cookie);
        connection.connect();
        if(connection.getResponseCode() == 200) {
            InputStream inputStream = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            publishProgress(new Image(bitmap));
        }
        connection.disconnect();
    }

    //    interface to send bitmaps via Image(class object) to activity
    @Override
    protected void onProgressUpdate(Image... image){
        if(this.callback != null){
            this.callback.onBitmapReady(image[0])
            ;
        }
    }

    //    additional interfaces
    public interface ICallback{
        Image getImage(Image image);
        void onBitmapReady(Image image);
        void makeToast(String message);
    }
}
