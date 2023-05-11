package ru.sfu.planner;

import android.widget.EditText;
import com.google.gson.Gson;
import org.osmdroid.util.GeoPoint;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public final class ServiceAPI {

   public static String download(String urlPath) throws IOException {
      StringBuilder xmlResult = new StringBuilder();
      BufferedReader reader = null;
      InputStream stream = null;
      HttpsURLConnection connection = null;
      try {
         URL url = new URL(urlPath);
         connection = (HttpsURLConnection) url.openConnection();
         stream = connection.getInputStream();
         reader = new BufferedReader(new InputStreamReader(stream));
         String line;
         while ((line=reader.readLine()) != null) {
            xmlResult.append(line);
         }
         return xmlResult.toString();
      } finally {
         if (reader != null) {
            reader.close();
         }
         if (stream != null) {
            stream.close();
         }
         if (connection != null) {
            connection.disconnect();
         }
      }
   }

   public static String parseXML(String xmlData){
      boolean inEntry = false;
      String textValue = "";

      try{
         XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
         factory.setNamespaceAware(true);
         XmlPullParser xpp = factory.newPullParser();

         xpp.setInput(new StringReader(xmlData));
         int eventType = xpp.getEventType();
         while(eventType != XmlPullParser.END_DOCUMENT){

            String tagName = xpp.getName();
            switch (eventType){
               case XmlPullParser.START_TAG:
                  if("result".equalsIgnoreCase(tagName)){
                     inEntry = true;
                  }
                  break;
               case XmlPullParser.TEXT:
                  textValue = xpp.getText();
                  break;
               case XmlPullParser.END_TAG:
                  if(inEntry){
                     if("result".equalsIgnoreCase(tagName)){
                        inEntry = false;
                        return textValue;
                     }
                  }
                  break;
               default:
            }
            eventType = xpp.next();
         }
      } catch (Exception e){
         e.printStackTrace();
      }
      return null;
   }

   public static void setAddress(GeoPoint geoPoint, EditText newAddress) {
      new Thread(() -> {
         try {
            String url = "https://nominatim.openstreetmap.org/reverse?" +
                    "addressdetails=0&accept-language=ru-RU&lat=" +
                    geoPoint.getLatitude() + "&lon=" + geoPoint.getLongitude();
            String content = ServiceAPI.download(url);
            String address = ServiceAPI.parseXML(content);
            newAddress.post(() -> newAddress.setText(address));
         } catch (IOException ex){
            System.err.println(ex.getMessage());
         }
      }).start();
   }


//   public static WeatherItem.Daily parseJSON(String jsonData) {
//      Gson gson = new Gson();
//      WeatherItem weatherItem = gson.fromJson(jsonData, WeatherItem.class);
//      return weatherItem.daily;
//   }
//   public static class WeatherItem {
//      public Daily daily;
//      public static class Daily {
//         public List<Double> temperature_2m_max;
//         public List<String> time;
//      }
//   }

}
