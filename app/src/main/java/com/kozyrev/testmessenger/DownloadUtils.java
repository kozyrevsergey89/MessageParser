package com.kozyrev.testmessenger;

import android.text.TextUtils;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Utils for network related operations
 */
@SuppressWarnings({ "UtilityClass" }) final class DownloadUtils {

  private static final String TAG = DownloadUtils.class.getSimpleName();
  private static final String HTTP = "http";
  private static final String HTTP_PREFIX = "http://";
  private static final String HTTPS_PREFIX = "https://";
  private static final int LENGTH_TO_LOAD = 5000;

  /**
   * Download web page content by given url.
   * If the url does not contain http/https prefixes - try to load using https then http
   *
   * @param url Parsed url from text message
   * @return Text representation of web page by given url
   */
  static String downloadGivenUrl(String url) {
    // if given url starts with 'http' prefix - try to load straight
    if (url.startsWith(HTTP)) {
      return downloadUrl(url);
    } else {
      // if given url does not start with 'http' prefix - try to load https, if not available then http
      String httpsRes = null;
      httpsRes = downloadUrl(HTTPS_PREFIX + url);
      if (!TextUtils.isEmpty(httpsRes)) {
        return httpsRes;
      } else {
        return downloadUrl(HTTP_PREFIX + url);
      }
    }
  }

  /**
   * Download only take the first {@link #LENGTH_TO_LOAD} characters of the web page by given url
   *
   * @param mentionedUrl Url to load
   * @return String representation of first {@link #LENGTH_TO_LOAD} characters of the web page
   */
  private static String downloadUrl(String mentionedUrl) {
    InputStream is = null;
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
      return readIt(is, LENGTH_TO_LOAD);
    } catch (IOException e) {
      Log.e(TAG, "error while loading page", e);
    } finally {
      try {
        if (is != null) {
          is.close();
        }
      } catch (IOException e) {
        Log.e(TAG, "error during closing input stream: ", e);
      }
    }
    // if some IOException happened just return null
    return null;
  }

  private static String readIt(InputStream stream, int len) throws IOException {
    Reader reader = new InputStreamReader(stream, "UTF-8");
    char[] buffer = new char[len];
    reader.read(buffer);
    return new String(buffer);
  }

  private DownloadUtils() {
  }
}
