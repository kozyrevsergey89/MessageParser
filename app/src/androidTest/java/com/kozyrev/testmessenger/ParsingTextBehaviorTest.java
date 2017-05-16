package com.kozyrev.testmessenger;

import android.app.Instrumentation;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;

@RunWith(AndroidJUnit4.class)
public class ParsingTextBehaviorTest {

  @Rule
  public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
      MainActivity.class);
  private MessageServiceIdlingResource mIdlingResource;

  @Before
  public void registerIntentServiceIdlingResource() {
    Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
    mIdlingResource = new MessageServiceIdlingResource(instrumentation.getTargetContext());
    Espresso.registerIdlingResources(mIdlingResource);
  }

  @After
  public void unregisterIntentServiceIdlingResource() {
    Espresso.unregisterIdlingResources(mIdlingResource);
  }

  @Test
  public void sendClearsEditText() {
    // Type text and then press the button.
    onView(withId(R.id.chat_text))
        .perform(typeText("atlassian"), closeSoftKeyboard());
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
    onView(withId(R.id.outputs_recycler_view)).check(matches(hasDescendant(withText("{\"mentions\":[\"see\"," +
        "\"him\"]}"))));
  }

  @Test
  public void emotionsFound() {
    // Type text and then press the button.
    onView(withId(R.id.chat_text))
        .perform(typeText("hello world, btw (awesome) (test)"), closeSoftKeyboard());
    onView(withId(R.id.chat_send)).perform(click());

    // Check that the text parsed correctly
    onView(withId(R.id.outputs_recycler_view)).check(matches(hasDescendant(withText("{\"emoticons\":[\"awesome\","
        + "\"test\"]}"))));
  }

  @Test
  public void linksFound() {
    // Type text and then press the button.
    onView(withId(R.id.chat_text))
        .perform(typeText("hello world, btw watch facebook.com post about atlassian.com"), closeSoftKeyboard());
    onView(withId(R.id.chat_send)).perform(click());

    // Check that the text parsed correctly
    onView(withId(R.id.outputs_recycler_view)).check(matches(hasDescendant(withText(containsString("atlassian.com"))
    )));
  }
}
