package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.model.UserMartialProfileEntity
import com.example.ui.dashboard.MartialRadarChart
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    val sampleProfile = UserMartialProfileEntity(
      name = "Bruce L.",
      primaryArt = "Jeet Kune Do",
      hipMobility = 82f,
      centerlineStability = 88f,
      thoracicMobility = 76f,
      kineticTorque = 84f,
      recoveryReadiness = 90f,
      breathControl = 85f
    )
    composeTestRule.setContent {
      MyApplicationTheme {
        MartialRadarChart(profile = sampleProfile)
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}

