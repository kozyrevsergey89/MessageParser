package com.kozyrev.testmessenger;

import android.text.TextUtils;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

class DownloadUtils {

  private static final String TAG = DownloadUtils.class.getSimpleName();

  private static final String HTTPS = "https";
  private static final String HTTP = "http";
  private static final String HTTP_PREFIX = "http://";
  private static final String HTTPS_PREFIX = "https://";

  static String downloadGivenUrl(String mentionedUrl) {
    // if given url starts with 'http' prefix - try to load straight
    if (mentionedUrl.startsWith(HTTP)) {
      try {
        return downloadUrl(mentionedUrl);
      } catch (IOException e) {
        Log.e(TAG, "network error", e);
      }
    } else {
      // if given url doesn't start with 'http' prefix - try to load https, if not available then http
      String httpsRes = null;
      try {
        httpsRes = downloadUrl(HTTPS_PREFIX + mentionedUrl);
      } catch (IOException e) {
        Log.e(TAG, "network error", e);
      }
      if (!TextUtils.isEmpty(httpsRes)) {
        return httpsRes;
      } else {
        try {
          return downloadUrl(HTTP_PREFIX + mentionedUrl);
        } catch (IOException e) {
          Log.e(TAG, "network error", e);
        }
      }
    }
    // if some IOException happened and no result at the moment
    return null;
  }

  private static String downloadUrl(String mentionedUrl) throws IOException {
    InputStream is = null;
    // Only take the first 5000 characters of the retrieved
    // web page content to find <title>
    int len = 5000;

    try {
      URL url = new URL(mentionedUrl);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setReadTimeout(10000 /* milliseconds */);
      conn.setConnectTimeout(15000 /* milliseconds */);
      conn.setRequestMethod("GET");
      conn.setDoInput(true);
      conn.connect();
      int response = conn.getResponseCode();
      Log.d(Parser.class.getSimpleName(), "The response is: " + response);
      is = conn.getInputStream();

      return readIt(is, len);
    } finally {
      try {
        if (is != null) {
          is.close();
        }
      } catch (IOException e) {
        Log.e(TAG, "error during closing input stream: ", e);
      }
    }
  }

  private static String readIt(InputStream stream, int len) throws IOException {
    Reader reader = new InputStreamReader(stream, "UTF-8");
    char[] buffer = new char[len];
    reader.read(buffer);
    return new String(buffer);
  }
}
