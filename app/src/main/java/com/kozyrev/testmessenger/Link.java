package com.kozyrev.testmessenger;

/**
 * Model class to represent a link
 */
class Link {

  private String url;

  private String title;

  String getTitle() {
    return title;
  }

  String getUrl() {
    return url;
  }

  void setTitle(String title) {
    this.title = title;
  }

  void setUrl(String url) {
    this.url = url;
  }
}
