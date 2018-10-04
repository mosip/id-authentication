package io.mosip.registration.controller;

import static io.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegConstants.APPLICATION_NAME;
import static io.mosip.registration.util.reader.PropertyFileReader.getPropertyValue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;
import io.mosip.registration.constants.RegistrationUIExceptionCode;
import io.mosip.registration.dto.RegistrationApprovalUiDto;
import io.mosip.registration.exception.RegBaseUncheckedException;
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

/**
*
*{@code ViewAckFormController} is the controller class to view the acknowledgement form
* @author Mahesh Kumar
*/
public class ViewAcknowledgementController extends TableCell<RegistrationApprovalUiDto, Boolean> {
	/**
	 * Instance of {@link MosipLogger}
	 */
	private static MosipLogger LOGGER;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}
	final Hyperlink link = new Hyperlink("Click Here");

	final VBox paddedButton = new VBox();
	final HBox mainHolder = new HBox();
	final DoubleProperty buttonY = new SimpleDoubleProperty();

	/**
	 * constructor for {@code ViewAckFormController} controller
	 * @param table
	 */
	public ViewAcknowledgementController(TableView<RegistrationApprovalUiDto> table) {

		paddedButton.setPadding(new Insets(3));
		paddedButton.getChildren().add(link);
		mainHolder.getChildren().add(paddedButton);

		link.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				table.getSelectionModel().select(getTableRow().getIndex());
				try {
					showAck(table.getSelectionModel().getSelectedItem().getAcknowledgementFormPath());
				} catch (IOException ioException) {
					throw new RegBaseUncheckedException(RegistrationUIExceptionCode.REG_UI_VIEW_ACK_FORM_IO_EXCEPTION,
							"Unable to view the acknowledgemnt form", ioException);
				}
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

	/**
	 * {@code showAck} method is to view acknowledge form
	 * @param acknowledmentFormFileName
	 */
	private static void showAck(String acknowledmentFormFileName) throws IOException {
		
		Stage primaryStage = new Stage();
		FileInputStream file = new FileInputStream(new File(acknowledmentFormFileName));
		primaryStage.setTitle("Acknowlegement Form");
		ImageView imageView = new ImageView(new Image(file));

		HBox hbox = new HBox(imageView);

		Scene scene = new Scene(hbox, 800, 600);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		LOGGER.debug("REGISTRATION - VIEW-ACK - REGISTRATION_VIEW-ACK_CONTROLLER",
				getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID), "view ack form has been ended");

	}

}
