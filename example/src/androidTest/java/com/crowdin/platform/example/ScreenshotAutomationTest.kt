package com.crowdin.platform.example

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import com.crowdin.platform.Crowdin
import com.crowdin.platform.screenshot.ScreenshotCallback
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
@LargeTest
class ScreenshotAutomationTest {

    @Test
    fun screenshotCapture() {
        // Launch SplashScreenActivity
        val splashScenario = ActivityScenario.launch(SplashScreenActivity::class.java)

        // Wait for MainActivity
        waitForActivity<MainActivity>()?.let { activity ->
            // Dashboard
            onView(withId(R.id.root_dashboard)).check(matches(isDisplayed()))
            onView(withId(R.id.recyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

            Thread.sleep(2000)
            captureAndSendScreenshot(activity, "dashboard")

            // Add task
            onView(withId(R.id.drawerLayout)).perform(DrawerActions.open())
            onView(withId(R.id.nav_add_task)).perform(click())
            onView(withId(R.id.add_task_root)).check(matches(isDisplayed()))
            Thread.sleep(2000)
            captureAndSendScreenshot(activity, "add_task")
            Espresso.pressBack()

            // Category
            onView(withId(R.id.drawerLayout)).perform(DrawerActions.open())
            onView(withId(R.id.nav_category)).perform(click())
            onView(withId(R.id.categoryFragment)).check(matches(isDisplayed()))
            onView(withId(R.id.addCategoryFAB)).perform(click())
            Thread.sleep(2000)
            captureAndSendScreenshot(activity, "category_add")
            Espresso.pressBack()

            // Edit category
            onView(withId(R.id.recyclerView))
                .perform(
                    RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                        0,
                        clickChildViewWithId(R.id.imgEditCategory)
                    )
                )
            Thread.sleep(2000)
            captureAndSendScreenshot(activity, "category_edit")
            Espresso.pressBack()

            // Delete category
            onView(withId(R.id.recyclerView))
                .perform(
                    RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                        0,
                        clickChildViewWithId(R.id.imgDeleteCategory)
                    )
                )
            Thread.sleep(2000)
            captureAndSendScreenshot(activity, "category_delete")
            Espresso.pressBack()
            Espresso.pressBack()

            // History
            onView(withId(R.id.drawerLayout)).perform(DrawerActions.open())
            onView(withId(R.id.nav_history)).perform(click())
            onView(withId(R.id.txtNoHistory)).check(matches(isDisplayed()))
            captureAndSendScreenshot(activity, "history")
            Espresso.pressBack()

            // Settings
            onView(withId(R.id.drawerLayout)).perform(DrawerActions.open())
            onView(withId(R.id.nav_settings)).perform(click())
            onView(withId(R.id.languageTypeTv)).check(matches(isDisplayed()))
            captureAndSendScreenshot(activity, "settings")
        } ?: throw IllegalStateException("MainActivity not found!")

        splashScenario.close()
    }

    private inline fun <reified T : Activity> waitForActivity(): T? {
        var activity: T? = null
        val latch = CountDownLatch(1)
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            ActivityLifecycleMonitorRegistry.getInstance().addLifecycleCallback { resumedActivity, stage ->
                if (stage == Stage.RESUMED && resumedActivity is T) {
                    activity = resumedActivity
                    latch.countDown()
                }
            }
        }

        if (!latch.await(30, TimeUnit.SECONDS)) {
            Log.e("ScreenshotAutomationTest", "Timeout waiting for activity: ${T::class.java.simpleName}")
        }

        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            ActivityLifecycleMonitorRegistry.getInstance().removeLifecycleCallback { _, _ -> }
        }

        return activity
    }

    private fun captureAndSendScreenshot(activity: Activity, name: String) {
        Log.d("ScreenshotAutomationTest", "Capture and send screenshot")
        val latch = CountDownLatch(1)

        val rootView = activity.window.decorView.rootView
        val bitmap = captureBitmap(rootView)

        Log.d("ScreenshotAutomationTest", "bitmap generated")
        Crowdin.sendScreenshot(
            bitmap = bitmap,
            screenshotName = name,
            screenshotCallback = object : ScreenshotCallback {
                override fun onSuccess() {
                    Log.d("ScreenshotAutomationTest", "Screenshot upload onSuccess")
                    latch.countDown()
                }

                override fun onFailure(throwable: Throwable) {
                    Log.e("ScreenshotAutomationTest", "Screenshot upload onFailure: ${throwable.message}")
                    throw AssertionError("Screenshot upload failed: ${throwable.message}")
                }
            }
        )

        latch.await(30, TimeUnit.SECONDS)
    }

    private fun captureBitmap(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun clickChildViewWithId(id: Int): ViewAction =
        object : ViewAction {
            override fun getConstraints(): Matcher<View> = allOf(isAssignableFrom(View::class.java), isDisplayed())

            override fun getDescription(): String = "Click on a child view with the specified ID."

            override fun perform(uiController: UiController?, view: View) {
                val childView: View = view.findViewById(id)
                childView.performClick()
            }
        }
}