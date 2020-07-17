package iss.workshop.team8flipgame;

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

    ICallback callback;
    String html;
    public ImageScraper(ICallback callback){
        this.callback = callback;
    }
    @Override
    protected Void doInBackground(String... strings) {
        String html = HTMLscraper(strings[0]);
        if(html == null) {
            if(this.callback != null) callback.makeToast("Check the search address");
            return null;
        }
        ArrayList<URL> imageURLs = imageURLs(html);

        if(imageURLs.size()< ImagePickingActivity.getImageNo()){
            if(this.callback != null) callback.makeToast("There are only "+imageURLs.size()+" possible images.  Please try another search term.");
            return null;
        }
        scrapBitmaps(imageURLs);
        return null;
    }

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

    void scrapBitmaps(final ArrayList<URL> imageURLs){
        for ( int i = 0; i< ImagePickingActivity.getImageNo(); i++){
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

    @Override
    protected void onProgressUpdate(Image... image){
        if(this.callback != null){
            this.callback.onBitmapReady(image[0])
            ;
        }
    }

    public interface ICallback{
        Image getImage(Image image);
        void onBitmapReady(Image image);
        void makeToast(String message);
    }
}
