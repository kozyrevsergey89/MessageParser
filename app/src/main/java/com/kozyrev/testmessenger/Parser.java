package com.kozyrev.testmessenger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class Parser {
  private Pattern mentionsPattern = Pattern.compile("\\B@([a-z0-9_-]+)", Pattern.CASE_INSENSITIVE);
  private Pattern emoticonsPattern = Pattern.compile("\\(([\\w]{1,15})\\)", Pattern.CASE_INSENSITIVE);
  private Pattern titlePattern = Pattern.compile("<title>(.*?)</title>", Pattern.CASE_INSENSITIVE);

  private static final String URL_KEY = "url";
  private static final String TITLE_KEY = "title";
  private static final String MENTIONS_KEY = "mentions";
  private static final String EMOTICONS_KEY = "emoticons";
  private static final String LINKS_KEY = "links";

  JSONObject parseJsonMessage(String text) throws JSONException {
    List<String> mentions = new ArrayList<>();
    Matcher mentionsMatcher = mentionsPattern.matcher(text);
    while (mentionsMatcher.find()) {
      mentions.add(mentionsMatcher.group(1));
    }

    List<String> emoticons = new ArrayList<>();
    Matcher emoticonsMatcher = emoticonsPattern.matcher(text);
    while (emoticonsMatcher.find()) {
      emoticons.add(emoticonsMatcher.group(1));
    }

    List<Link> links = new ArrayList<>();
    Matcher linksMatcher = android.util.Patterns.WEB_URL.matcher(text);
    while (linksMatcher.find()) {
      Link link = new Link();
      String url = linksMatcher.group();
      link.setUrl(url);
      link.setTitle(extractTitleFromUrl(url));
      links.add(link);
    }
    return dataToJson(mentions, emoticons, links);
  }

  private JSONObject dataToJson(List<String> mentions, List<String> emoticons, List<Link> links) throws JSONException {
    JSONObject jsonObject = null;
    JSONArray jsonMentions = new JSONArray();
    for (String mention : mentions) {
      jsonMentions.put(mention);
    }
    JSONArray jsonEmoticons = new JSONArray();
    for (String emoticon : emoticons) {
      jsonEmoticons.put(emoticon);
    }
    JSONArray jsonLinks = new JSONArray();
    for (Link link : links) {
      JSONObject jsonLink = new JSONObject();
      jsonLink.put(URL_KEY, link.getUrl());
      jsonLink.put(TITLE_KEY, link.getTitle());
      jsonLinks.put(jsonLink);
    }
    if (jsonMentions.length() > 0 || jsonEmoticons.length() > 0 || jsonLinks.length() > 0) {
      jsonObject = new JSONObject();
      if (jsonMentions.length() > 0) {
        jsonObject.put(MENTIONS_KEY, jsonMentions);
      }
      if (jsonEmoticons.length() > 0) {
        jsonObject.put(EMOTICONS_KEY, jsonEmoticons);
      }
      if (jsonLinks.length() > 0) {
        jsonObject.put(LINKS_KEY, jsonLinks);
      }
    }
    return jsonObject;
  }

  private String extractTitleFromUrl(String url) {
    try {
      String content = DownloadUtils.downloadUrl(url);
      Matcher matcher = titlePattern.matcher(content);
      if (matcher.find()) {
        return matcher.group(1);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
