package com.kozyrev.testmessenger;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SendClearTextBehaviorTest {

  private String mStringToBetyped;

  @Rule
  public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
      MainActivity.class);

  @Before
  public void initValidString() {
    // Specify a valid string.
    mStringToBetyped = "Espresso";
  }

  @Test
  public void sendClearsEditText() {
    // Type text and then press the button.
    onView(withId(R.id.chat_text))
        .perform(typeText(mStringToBetyped), closeSoftKeyboard());
    onView(withId(R.id.chat_send)).perform(click());

    // Check that the text was cleared
    onView(withId(R.id.chat_text))
        .check(matches(withText("")));
  }

  @Test
  public void mentionsFound() {
    // Type text and then press the button.
    onView(withId(R.id.chat_text))
        .perform(typeText("@see @him"), closeSoftKeyboard());
    onView(withId(R.id.chat_send)).perform(click());

    // Check that the text parsed correctly
    onData(allOf(is(instanceOf(String.class)),
        equalTo("{\"mentions\":[\"see\",\"him\"]}")));
  }

  @Test
  public void emotionsFound() {
    // Type text and then press the button.
    onView(withId(R.id.chat_text))
        .perform(typeText("hello world, btw (awesome) (test)"), closeSoftKeyboard());
    onView(withId(R.id.chat_send)).perform(click());

    // Check that the text parsed correctly
    onData(allOf(is(instanceOf(String.class)),
        equalTo("{\"emoticons\":[\"awesome\",\"test\"]}")));
  }

  @Test
  public void linksFound() {
    // Type text and then press the button.
    onView(withId(R.id.chat_text))
        .perform(typeText("hello world, btw watch facebook.com post about atlassian.com"), closeSoftKeyboard());
    onView(withId(R.id.chat_send)).perform(click());

    // Check that the text parsed correctly
    onData(allOf(is(instanceOf(String.class)),
        contains("atlassian.com")));
  }
}
