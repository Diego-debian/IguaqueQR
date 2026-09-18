package com.qrart.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.qrart.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainFlowInstrumentedTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private fun enterCreator() {
        composeRule.waitUntil(10_000) {
            composeRule.onAllNodesWithTag("welcome_continue", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithTag("welcome_continue", useUnmergedTree = true).performClick()
        composeRule.onNodeWithText("Enlace").assertIsDisplayed()
    }

    @Test
    fun welcomeOpensCreator() {
        composeRule.waitUntil(10_000) {
            composeRule.onAllNodesWithTag("welcome_title", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithTag("welcome_title", useUnmergedTree = true).assertIsDisplayed()
        enterCreator()
    }

    @Test
    fun emptyInputIsBlocked() {
        enterCreator()
        composeRule.onNode(hasSetTextAction()).performTextClearance()
        composeRule.onNodeWithText("Continuar").performClick()
        composeRule.onNodeWithText("Introduce un enlace o texto para continuar").assertIsDisplayed()
    }

    @Test
    fun wizardExposesSixStepsAndSupportsBackNavigation() {
        enterCreator()
        listOf("Enlace", "Destino", "Forma", "Marco", "Lectura", "Resultado")
            .forEach { composeRule.onNodeWithText(it).assertIsDisplayed() }

        composeRule.onNodeWithText("Continuar").performClick()
        composeRule.onNodeWithText("Confirma el destino").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Volver").performClick()
        composeRule.onNodeWithText("Empieza por el contenido").assertIsDisplayed()
    }

    @Test
    fun bottomNavigationSwitchesBetweenCreatorAndHistory() {
        enterCreator()
        composeRule.onNodeWithText("Historial").performClick()
        composeRule.onNodeWithText("Historial").assertIsDisplayed()
        composeRule.onNodeWithText("Crear QR").performClick()
        composeRule.onNodeWithText("Enlace").assertIsDisplayed()
    }

    @Test
    fun generationShowsQrPreview() {
        enterCreator()
        repeat(4) { composeRule.onNodeWithText("Continuar").performClick() }
        composeRule.onNodeWithText("Revisar").performClick()
        composeRule.onNodeWithTag("generate_qr").performClick()
        composeRule.waitUntil(15_000) {
            composeRule.onAllNodesWithTag("qr_preview").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithTag("qr_preview").assertIsDisplayed()
    }
}
