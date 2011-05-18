package com.mediaportal.ampdroid.api.gmawebservice;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.deser.CustomDeserializerFactory;
import org.codehaus.jackson.map.deser.StdDeserializerProvider;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.mediaportal.ampdroid.api.CustomDateDeserializer;
import com.mediaportal.ampdroid.api.IMediaAccessApi;
import com.mediaportal.ampdroid.api.JsonClient;
import com.mediaportal.ampdroid.data.EpisodeDetails;
import com.mediaportal.ampdroid.data.Movie;
import com.mediaportal.ampdroid.data.MovieFull;
import com.mediaportal.ampdroid.data.MusicAlbum;
import com.mediaportal.ampdroid.data.Series;
import com.mediaportal.ampdroid.data.SeriesEpisode;
import com.mediaportal.ampdroid.data.SeriesFull;
import com.mediaportal.ampdroid.data.SeriesSeason;
import com.mediaportal.ampdroid.data.SupportedFunctions;
import com.mediaportal.ampdroid.data.VideoShare;

public class GmaJsonWebserviceApi implements IMediaAccessApi {
   private GmaJsonWebserviceMovieApi m_moviesAPI;
   private GmaJsonWebserviceSeriesApi m_seriesAPI;

   private String mServer;
   private int mPort;

   private final String GET_SUPPORTED_FUNCTIONS = "MP_GetSupportedFunctions";

   private final String JSON_PREFIX = "http://";
   private final String JSON_SUFFIX = "/json";

   private JsonClient mJsonClient;
   private ObjectMapper mJsonObjectMapper;

   @SuppressWarnings("unchecked")
   public GmaJsonWebserviceApi(String _server, int _port) {
      mServer = _server;
      mPort = _port;

      mJsonClient = new JsonClient(JSON_PREFIX + mServer + ":" + mPort + JSON_SUFFIX);
      mJsonObjectMapper = new ObjectMapper();
      mJsonObjectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

      CustomDeserializerFactory sf = new CustomDeserializerFactory();
      mJsonObjectMapper.setDeserializerProvider(new StdDeserializerProvider(sf));
      sf.addSpecificMapping(Date.class, new CustomDateDeserializer());
      
      m_moviesAPI = new GmaJsonWebserviceMovieApi(mJsonClient, mJsonObjectMapper);
      m_seriesAPI = new GmaJsonWebserviceSeriesApi(mJsonClient, mJsonObjectMapper);
      //m_musicAPI = new GmaWebserviceMusicApi(m_wcfService, mJsonObjectMapper);
   }
   
   @Override
   public String getServer() {
      return mServer;
   }

   @Override
   public int getPort() {
      return mPort;
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   private Object getObjectsFromJson(String _jsonString, Class _class) {
      try {
         Object returnObjects = mJsonObjectMapper.readValue(_jsonString, _class);

         return returnObjects;
      } catch (JsonParseException e) {
         Log.e("aMPed JSON", e.toString());
      } catch (JsonMappingException e) {
         Log.e("aMPed JSON", e.toString());
      } catch (IOException e) {
         Log.e("aMPed JSON", e.toString());
      }
      return null;
   }

   @Override
   public String getAddress() {
      return mServer;
   }

   public SupportedFunctions getSupportedFunctions() {
      String methodName = GET_SUPPORTED_FUNCTIONS;
      String response = mJsonClient.Execute(methodName);

      if (response != null) {
         SupportedFunctions returnObject = (SupportedFunctions) getObjectsFromJson(response,
               SupportedFunctions.class);

         if (returnObject != null) {
            return returnObject;
         } else {
            Log.d("aMPdroid JSON", "Error parsing result from JSON method " + methodName);
         }
      } else {
         Log.d("aMPdroid JSON", "Error retrieving data for method" + methodName);
      }
      return null;
   }

   @Override
   public ArrayList<VideoShare> getVideoShares() {
      String methodName = "MP_GetVideoShares";
      String response = mJsonClient.Execute(methodName);

      if (response != null) {
         VideoShare[] returnObject = (VideoShare[]) getObjectsFromJson(response,
               VideoShare[].class);

         if (returnObject != null) {
            return new ArrayList<VideoShare>(Arrays.asList(returnObject));
         } else {
            Log.d("aMPdroid JSON", "Error parsing result from JSON method " + methodName);
         }
      } else {
         Log.d("aMPdroid JSON", "Error retrieving data for method" + methodName);
      }
      return null;
   }

   @Override
   public ArrayList<Movie> getAllMovies() {
      return m_moviesAPI.getAllMovies();
   }

   @Override
   public int getMovieCount() {
      return m_moviesAPI.getMovieCount();
   }

   @Override
   public MovieFull getMovieDetails(int _movieId) {
      return m_moviesAPI.getMovieDetails(_movieId);
   }

   @Override
   public ArrayList<Movie> getMovies(int _start, int _end) {
      return m_moviesAPI.getMovies(_start, _end);
   }

   @Override
   public ArrayList<Series> getAllSeries() {
      return m_seriesAPI.getAllSeries();
   }

   @Override
   public ArrayList<Series> getSeries(int _start, int _end) {
      return m_seriesAPI.getSeries(_start, _end);
   }

   @Override
   public SeriesFull getFullSeries(int _seriesId) {
      return m_seriesAPI.getFullSeries(_seriesId);
   }

   @Override
   public int getSeriesCount() {
      return m_seriesAPI.getSeriesCount();
   }

   @Override
   public ArrayList<SeriesSeason> getAllSeasons(int _seriesId) {
      return m_seriesAPI.getAllSeasons(_seriesId);
   }

   @Override
   public ArrayList<SeriesEpisode> getAllEpisodes(int _seriesId) {
      return m_seriesAPI.getAllEpisodes(_seriesId);
   }

   @Override
   public ArrayList<SeriesEpisode> getAllEpisodesForSeason(int _seriesId, int _seasonNumber) {
      return m_seriesAPI.getAllEpisodesForSeason(_seriesId, _seasonNumber);
   }

   @Override
   public ArrayList<SeriesEpisode> getEpisodesForSeason(int _seriesId, int _seasonNumber,
         int _begin, int _end) {
      return m_seriesAPI.getEpisodesForSeason(_seriesId, _seasonNumber, _begin, _end);
   }

   @Override
   public int getEpisodesCountForSeason(int _seriesId, int _seasonNumber) {
      return m_seriesAPI.getEpisodesCountForSeason(_seriesId, _seasonNumber);
   }

   @Override
   public EpisodeDetails getEpisode(int _seriesId, int _episodeId) {
      return m_seriesAPI.getFullEpisode(_seriesId, _episodeId);
   }

   @Override
   public Bitmap getBitmap(String _url) {
      URL myFileUrl = null;
      Bitmap bmImg = null;
      try {
         myFileUrl = new URL(JSON_PREFIX + mServer + ":" + mPort + JSON_SUFFIX
               + "/FS_GetImage/?path=" + URLEncoder.encode(_url, "UTF-8"));
         HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
         conn.setDoInput(true);
         conn.connect();
         InputStream is = conn.getInputStream();

         bmImg = BitmapFactory.decodeStream(is);
      } catch (MalformedURLException e) {
         e.printStackTrace();
      } catch (UnsupportedEncodingException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }

      return bmImg;
   }

   @Override
   public Bitmap getBitmap(String _url, int _maxWidth, int _maxHeight) {
      URL myFileUrl = null;
      Bitmap bmImg = null;
      try {
         myFileUrl = new URL(JSON_PREFIX + mServer + ":" + mPort + JSON_SUFFIX
               + "/FS_GetImageResized/?path=" + URLEncoder.encode(_url, "UTF-8") + "&maxWidth="
               + _maxWidth + "&maxHeight=" + _maxHeight);
         HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
         conn.setDoInput(true);
         conn.connect();
         InputStream is = conn.getInputStream();

         bmImg = BitmapFactory.decodeStream(is);
      } catch (MalformedURLException e) {
         e.printStackTrace();
      } catch (UnsupportedEncodingException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }

      return bmImg;
   }

   @Override
   public String getDownloadUri(String _filePath) {
      String fileUrl = null;
      try {
         fileUrl = JSON_PREFIX + mServer + ":" + mPort + JSON_SUFFIX + "/FS_GetMediaItem/?path="
               + URLEncoder.encode(_filePath, "UTF-8");
      } catch (UnsupportedEncodingException e) {
         e.printStackTrace();
      }
      return fileUrl;

   }

   @Override
   public ArrayList<MusicAlbum> getAllAlbums() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public List<MusicAlbum> getAlbums(int _start, int _end) {
      // TODO Auto-generated method stub
      return null;
   }


}