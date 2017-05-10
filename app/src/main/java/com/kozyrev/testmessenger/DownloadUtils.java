package com.kozyrev.testmessenger;

import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

class DownloadUtils {

  // Given a URL, establishes an HttpUrlConnection and retrieves
  // the web page content as a InputStream, which it returns as
  // a string.
  static String downloadUrl(String mentionedUrl) throws IOException {
    InputStream is = null;
    // Only take the first 5000 characters of the retrieved
    // web page content.
    int len = 5000;

    try {
      // try fixing url if it was mentioned without http:// prefix
      URL url = new URL(mentionedUrl.startsWith("http") ? mentionedUrl : "http://" + mentionedUrl);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setReadTimeout(10000 /* milliseconds */);
      conn.setConnectTimeout(15000 /* milliseconds */);
      conn.setRequestMethod("GET");
      conn.setDoInput(true);
      // Start the query
      conn.connect();
      int response = conn.getResponseCode();
      Log.d(Parser.class.getSimpleName(), "The response is: " + response);
      is = conn.getInputStream();

      // Convert the InputStream into a string
      return readIt(is, len);

      // Makes sure that the InputStream is closed after we finished using it.
    } finally {
      try {
        if (is != null) {
          is.close();
        }
      } catch (IOException e) {
        Log.e(Parser.class.getSimpleName(), "error during closing input stream: ", e);
      }
    }
  }

  // Reads an InputStream and converts it to a String.
  private static String readIt(InputStream stream, int len) throws IOException {
    Reader reader = new InputStreamReader(stream, "UTF-8");
    char[] buffer = new char[len];
    reader.read(buffer);
    return new String(buffer);
  }
}
