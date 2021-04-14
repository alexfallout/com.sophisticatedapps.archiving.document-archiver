/*
 * Copyright 2021 by Stephan Sann (https://github.com/stephansann)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sophisticatedapps.archiving.documentarchiver.controller;

import com.sophisticatedapps.archiving.documentarchiver.BaseTest;
import com.sophisticatedapps.archiving.documentarchiver.GlobalConstants;
import com.sophisticatedapps.archiving.documentarchiver.util.FXMLUtil;
import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test for "com.sophisticatedapps.archiving.documentarchiver.controller.RootPaneController".
 */
@ExtendWith(ApplicationExtension.class)
class RootPaneControllerTest extends BaseTest {

    private BorderPane rootPane;
    private RootPaneController rootPaneController;

    /**
     * Will be called with {@code @Before} semantics, i. e. before each test method.
     *
     * @param aStage - Will be injected by the test runner.
     */
    @Start
    public void start(Stage aStage) {

        aStage.getProperties().put(GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY, null);
        aStage.getProperties().put(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY, null);

        FXMLUtil.ControllerRegionPair<RootPaneController,BorderPane> tmpRootPaneControllerRegionPair =
                FXMLUtil.loadAndRampUpRegion("view/RootPane.fxml", aStage);
        rootPane = tmpRootPaneControllerRegionPair.getRegion();
        rootPaneController = tmpRootPaneControllerRegionPair.getController();
    }

    @AfterEach
    public void cleanUpEach() {

        rootPaneController.rampDown();

        rootPane = null;
        rootPaneController = null;
    }

    @Test
    void testSetWidths() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        rootPaneController.setNewCurrentDocument(TEST_TEXT_FILE);

        WaitForAsyncUtils.waitForFxEvents();

        rootPaneController.stage.setWidth(888);
        MethodUtils.invokeMethod(rootPaneController, true, "setWidths");

        assertEquals(177.6, ((Pane)rootPane.getLeft()).getPrefWidth());
        assertEquals(444.0, ((Pane)rootPane.getCenter()).getPrefWidth());
        assertEquals(266.4, ((Pane)rootPane.getRight()).getPrefWidth());
    }

    @Test
    void testSetHeights() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        rootPaneController.setNewCurrentDocument(TEST_TEXT_FILE);

        WaitForAsyncUtils.waitForFxEvents();

        rootPaneController.stage.setHeight(555);
        MethodUtils.invokeMethod(rootPaneController, true, "setHeights");

        assertEquals(505, ((Pane)rootPane.getLeft()).getPrefHeight());
        assertEquals(505, ((Pane)rootPane.getCenter()).getPrefHeight());
        assertEquals(505, ((Pane)rootPane.getRight()).getPrefHeight());
    }

    @Test
    void testHandleCurrentDocumentChanged() {

        Platform.runLater(() -> {

            try {

                MethodUtils.invokeMethod(rootPaneController, true, "handleCurrentDocumentChanged", TEST_TEXT_FILE2);
            }
            catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {

                fail(e);
            }
        });

        WaitForAsyncUtils.waitForFxEvents();

        assertEquals("Archiving: ".concat(TEST_TEXT_FILE2.getPath()), rootPaneController.stage.getTitle());
        assertEquals(VBox.class, rootPane.getLeft().getClass());
        assertEquals(Pane.class, rootPane.getCenter().getClass());
        assertEquals(VBox.class, rootPane.getRight().getClass());
    }

    @Test
    void testHandleCurrentDocumentChanged_to_null_without_welcome_dialog() throws IllegalAccessException {

        MenuBarController tmpMockedMenuBarController = Mockito.mock(MenuBarController.class);
        FieldUtils.writeField(rootPaneController, "menuBarController", tmpMockedMenuBarController, true);
        FieldUtils.writeField(rootPaneController, "showWelcomeDialog", Boolean.FALSE, true);

        Platform.runLater(() -> {

            try {

                MethodUtils.invokeMethod(rootPaneController, true, "handleCurrentDocumentChanged", (File)null);
            }
            catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {

                fail(e);
            }
        });

        WaitForAsyncUtils.waitForFxEvents();

        verify(tmpMockedMenuBarController, Mockito.times(1)).handleOpenFilesMenuItemAction();
        assertEquals("Choose file(s)", rootPaneController.stage.getTitle());
        assertNull(rootPane.getLeft());
        assertNull(rootPane.getCenter());
        assertNull(rootPane.getRight());
    }

    @Test
    void testHandleCurrentDocumentChanged_to_null_with_welcome_dialog_selection_files() throws IllegalAccessException {

        @SuppressWarnings("unchecked")
        Dialog<ButtonType> tmpMockedDialog = Mockito.mock(Dialog.class);
        when(tmpMockedDialog.showAndWait()).thenReturn(Optional.of(ButtonType.YES));
        BaseController.DialogProvider tmpMockedDialogProvider = Mockito.mock(BaseController.DialogProvider.class);
        when(tmpMockedDialogProvider.provideWelcomeDialog()).thenReturn(tmpMockedDialog);
        MenuBarController tmpMockedMenuBarController = Mockito.mock(MenuBarController.class);

        FieldUtils.writeField(rootPaneController, "dialogProvider", tmpMockedDialogProvider, true);
        FieldUtils.writeField(rootPaneController, "menuBarController", tmpMockedMenuBarController, true);

        Platform.runLater(() -> {

            try {

                MethodUtils.invokeMethod(rootPaneController, true, "handleCurrentDocumentChanged", (File)null);
            }
            catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {

                fail(e);
            }
        });

        WaitForAsyncUtils.waitForFxEvents();

        verify(tmpMockedDialogProvider, Mockito.times(1)).provideWelcomeDialog();
        verify(tmpMockedMenuBarController, Mockito.times(1)).handleOpenFilesMenuItemAction();
        assertEquals("Choose file(s)", rootPaneController.stage.getTitle());
        assertNull(rootPane.getLeft());
        assertNull(rootPane.getCenter());
        assertNull(rootPane.getRight());
    }

    @Test
    void testHandleCurrentDocumentChanged_to_null_with_welcome_dialog_selection_directory() throws IllegalAccessException {

        @SuppressWarnings("unchecked")
        Dialog<ButtonType> tmpMockedDialog = Mockito.mock(Dialog.class);
        when(tmpMockedDialog.showAndWait()).thenReturn(Optional.of(ButtonType.NO));
        BaseController.DialogProvider tmpMockedDialogProvider = Mockito.mock(BaseController.DialogProvider.class);
        when(tmpMockedDialogProvider.provideWelcomeDialog()).thenReturn(tmpMockedDialog);
        MenuBarController tmpMockedMenuBarController = Mockito.mock(MenuBarController.class);

        FieldUtils.writeField(rootPaneController, "dialogProvider", tmpMockedDialogProvider, true);
        FieldUtils.writeField(rootPaneController, "menuBarController", tmpMockedMenuBarController, true);

        Platform.runLater(() -> {

            try {

                MethodUtils.invokeMethod(rootPaneController, true, "handleCurrentDocumentChanged", (File)null);
            }
            catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {

                fail(e);
            }
        });

        WaitForAsyncUtils.waitForFxEvents();

        verify(tmpMockedDialogProvider, Mockito.times(1)).provideWelcomeDialog();
        verify(tmpMockedMenuBarController, Mockito.times(1)).handleOpenDirectoryMenuItemAction();
        assertEquals("Choose file(s)", rootPaneController.stage.getTitle());
        assertNull(rootPane.getLeft());
        assertNull(rootPane.getCenter());
        assertNull(rootPane.getRight());
    }

}
