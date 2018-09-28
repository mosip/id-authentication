package org.mosip.registration.controller;

import java.io.File;
import java.io.FileInputStream;

import org.mosip.registration.dto.RegistrationApprovalUiDto;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ViewAcknowledgementForm extends TableCell<RegistrationApprovalUiDto, Boolean> {
	final Hyperlink link = new Hyperlink("Click Here");

	final VBox paddedButton = new VBox();
	final HBox mainHolder = new HBox();
	final DoubleProperty buttonY = new SimpleDoubleProperty();

	ViewAcknowledgementForm(final Stage stage, final TableView<RegistrationApprovalUiDto> table) {

		paddedButton.setPadding(new Insets(3));
		paddedButton.getChildren().add(link);
		mainHolder.getChildren().add(paddedButton);
		link.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				table.getSelectionModel().select(getTableRow().getIndex());
				showAck(table.getSelectionModel().getSelectedItem().getAcknowledgementFormPath());
			}
		});
	}

	@Override
	protected void updateItem(Boolean item, boolean empty) {
		super.updateItem(item, empty);
		if (!empty) {
			setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
			setGraphic(paddedButton);
		}
	}

	private static void showAck(String acknowledmentFormFileName) {
		Stage primaryStage = new Stage();
		try {
			primaryStage.setTitle("Acknowlegement Form");
			FileInputStream file = new FileInputStream(new File(acknowledmentFormFileName));
			ImageView imageView = new ImageView(new Image(file));

			HBox hbox = new HBox(imageView);

			Scene scene = new Scene(hbox, 800, 600);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
