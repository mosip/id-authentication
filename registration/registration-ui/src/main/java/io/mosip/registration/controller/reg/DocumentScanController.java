package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.controller.device.ScanPopUpViewController;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.demographic.DocumentDetailsDTO;
import io.mosip.registration.util.scan.DocumentScanFacade;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This controller class is to handle the screen of the Demographic document
 * section details
 * 
 * @author M1045980
 * 
 * 
 *
 */
@Controller
public class DocumentScanController extends BaseController {

	private boolean isChild;

	@FXML
	private ComboBox<String> poaDocuments;

	@FXML
	private VBox poaBox;

	@FXML
	private ScrollPane poaScroll;

	@FXML
	private ComboBox<String> poiDocuments;

	@FXML
	private VBox poiBox;

	@FXML
	private ScrollPane poiScroll;

	private String selectedDocument;

	@Autowired
	private ScanPopUpViewController scanPopUpViewController;

	@Autowired
	private DocumentScanFacade documentScanFacade;

	@FXML
	private ComboBox<String> porDocuments;

	@FXML
	private ComboBox<String> dobDocuments;

	@FXML
	private VBox porBox;

	@FXML
	private VBox dobBox;

	@FXML
	private ScrollPane porScroll;

	@FXML
	private ScrollPane dobScroll;

	@FXML
	protected Button poaScanBtn;
	@FXML
	protected Button poiScanBtn;
	@FXML
	protected Button porScanBtn;
	@FXML
	protected Button dobScanBtn;
	@FXML
	protected AnchorPane documentScan;

	@Value("${DOCUMENT_SIZE}")
	public int documentSize;

	@Value("${SCROLL_CHECK}")
	public int scrollCheck;

	@FXML
	private void initialize() {
		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Entering the LOGIN_CONTROLLER");
		try {
			auditFactory.audit(AuditEvent.GET_REGISTRATION_CONTROLLER, Components.REGISTRATION_CONTROLLER,
					"initializing the registration controller",
					SessionContext.getInstance().getUserContext().getUserId(),
					RegistrationConstants.ONBOARD_DEVICES_REF_ID_TYPE);

			isChild = true;
			loadListOfDocuments();
			setScrollFalse();
		} catch (RuntimeException exception) {
			LOGGER.error("REGISTRATION - CONTROLLER", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					exception.getMessage());
			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.UNABLE_LOAD_REG_PAGE);
		}
	}

	private void setPreviewContent() {
		poaScanBtn.setVisible(false);
		poiScanBtn.setVisible(false);
		porScanBtn.setVisible(false);
		dobScanBtn.setVisible(false);
	}

	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(DocumentScanController.class);

	/**
	 * This method scans and uploads Proof of Address documents
	 */
	@FXML
	private void scanPoaDocument() {

		scanDocument(poaDocuments, poaBox, RegistrationConstants.POA_DOCUMENT,
				RegistrationConstants.POA_DOCUMENT_EMPTY);
	}

	/**
	 * This method scans and uploads Proof of Identity documents
	 */
	@FXML
	private void scanPoiDocument() {

		scanDocument(poiDocuments, poiBox, RegistrationConstants.POI_DOCUMENT,
				RegistrationConstants.POI_DOCUMENT_EMPTY);
	}

	/**
	 * This method scans and uploads Proof of Relation documents
	 */
	@FXML
	private void scanPorDocument() {

		scanDocument(porDocuments, porBox, RegistrationConstants.POR_DOCUMENT,
				RegistrationConstants.POR_DOCUMENT_EMPTY);
	}

	/**
	 * This method scans and uploads Proof of Date of birth documents
	 */
	@FXML
	private void scanDobDocument() {

		scanDocument(dobDocuments, dobBox, RegistrationConstants.DOB_DOCUMENT,
				RegistrationConstants.DOB_DOCUMENT_EMPTY);
	}

	/**
	 * This method scans and uploads documents
	 */
	private void scanDocument(ComboBox<String> documents, VBox vboxElement, String document, String errorMessage) {

		if (documents.getValue() == null) {
			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Select atleast one document for scan");

			generateAlert(RegistrationConstants.ALERT_ERROR, errorMessage);
			documents.requestFocus();
		} else if (!vboxElement.getChildren().isEmpty() && vboxElement.getChildren().stream()
				.noneMatch(index -> index.getId().contains(documents.getValue()))) {
			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Select only one document category for scan");

			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.SCAN_DOC_CATEGORY_MULTIPLE);
		} else {
			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Displaying Scan window to scan Documents");

			selectedDocument = document;
			scanWindow();
		}

	}

	/**
	 * This method will display Scan window to scan and upload documents
	 */
	private void scanWindow() {

		scanPopUpViewController.init(this, RegistrationConstants.SCAN_DOC_TITLE);

		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Scan window displayed to scan and upload documents");
	}

	/**
	 * This method will allow to scan and upload documents
	 */
	@Override
	public void scan(Stage popupStage) {

		try {

			byte[] byteArray = documentScanFacade.getScannedDocument();

			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Converting byte array to image");

			if (byteArray.length > documentSize) {
				generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.SCAN_DOC_SIZE);
			} else {
				if (selectedDocument != null) {
					LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
							RegistrationConstants.APPLICATION_ID, "Adding documents to Screen");

					switch (selectedDocument) {
					case RegistrationConstants.POA_DOCUMENT:
						attachDocuments(poaDocuments.getValue(), poaBox, poaScroll, byteArray);
						SessionContext.getInstance().getMapObject().put("poa", poaDocuments.getValue());
						break;
					case RegistrationConstants.POI_DOCUMENT:
						attachDocuments(poiDocuments.getValue(), poiBox, poiScroll, byteArray);
						SessionContext.getInstance().getMapObject().put("poi", poiDocuments.getValue());
						break;
					case RegistrationConstants.POR_DOCUMENT:
						attachDocuments(porDocuments.getValue(), porBox, porScroll, byteArray);
						SessionContext.getInstance().getMapObject().put("por", porDocuments.getValue());
						break;
					case RegistrationConstants.DOB_DOCUMENT:
						attachDocuments(dobDocuments.getValue(), dobBox, dobScroll, byteArray);
						SessionContext.getInstance().getMapObject().put("dob", dobDocuments.getValue());
						break;
					default:
					}

					popupStage.close();

					LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
							RegistrationConstants.APPLICATION_ID, "Documents added successfully");
				}
			}

		} catch (IOException ioException) {
			LOGGER.error(LoggerConstants.LOG_REG_REGISTRATION_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					String.format("%s -> Exception while scanning documents for registration  %s -> %s",
							RegistrationConstants.USER_REG_DOC_SCAN_UPLOAD_EXP, ioException.getMessage(),
							ioException.getCause()));

			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.SCAN_DOCUMENT_ERROR);
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LoggerConstants.LOG_REG_REGISTRATION_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					String.format("%s -> Exception while scanning documents for registration  %s",
							RegistrationConstants.USER_REG_DOC_SCAN_UPLOAD_EXP, runtimeException.getMessage()));

			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.SCAN_DOCUMENT_ERROR);
		}

	}

	/**
	 * This method will add Hyperlink and Image for scanned documents
	 */
	private void attachDocuments(String document, VBox vboxElement, ScrollPane scrollPane, byte[] byteArray) {

		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Attaching documemnts to Pane");

		scanPopUpViewController.getScanImage().setImage(convertBytesToImage(byteArray));

		String documentName = document;

		ObservableList<Node> nodes = vboxElement.getChildren();
		if (!nodes.isEmpty() && nodes.stream().anyMatch(index -> index.getId().contains(document))) {
			documentName = document.concat("_").concat(String.valueOf(nodes.size()));
		}

		DocumentDetailsDTO documentDetailsDTO = new DocumentDetailsDTO();
		documentDetailsDTO.setDocument(byteArray);
		documentDetailsDTO.setCategory(selectedDocument);
		documentDetailsDTO.setFormat(RegistrationConstants.WEB_CAMERA_IMAGE_TYPE);
		documentDetailsDTO.setValue(documentName);

		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Set details to DocumentDetailsDTO");

		getRegistrationDtoContent().getDemographicDTO().getApplicantDocumentDTO().getDocumentDetailsDTO()
				.add(documentDetailsDTO);

		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Set DocumentDetailsDTO to RegistrationDTO");

		addDocumentsToScreen(documentDetailsDTO.getValue(), vboxElement, scrollPane);

		generateAlert(RegistrationConstants.ALERT_INFORMATION, RegistrationConstants.SCAN_DOC_SUCCESS);

		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Setting scrollbar policy for scrollpane");

	}

	private void addDocumentsToScreen(String document, VBox vboxElement, ScrollPane scrollPane) {

		GridPane gridPane = new GridPane();
		gridPane.setId(document);
		gridPane.add(createHyperLink(document), 0, vboxElement.getChildren().size());
		gridPane.add(createImageView(vboxElement, scrollPane), 1, vboxElement.getChildren().size());

		vboxElement.getChildren().add(gridPane);

		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Scan document added to Vbox element");

		if (vboxElement.getChildren().size() >= scrollCheck) {
			scrollPane.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
			scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		} else {
			scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
			scrollPane.setVbarPolicy(ScrollBarPolicy.NEVER);
		}
	}
	
	/**
	 * This method will set scrollbar policy for scroll pane
	 */
	private void setScrollFalse() {
		poaScroll.setHbarPolicy(ScrollBarPolicy.NEVER);
		poaScroll.setVbarPolicy(ScrollBarPolicy.NEVER);
		poiScroll.setHbarPolicy(ScrollBarPolicy.NEVER);
		poiScroll.setVbarPolicy(ScrollBarPolicy.NEVER);
		porScroll.setHbarPolicy(ScrollBarPolicy.NEVER);
		porScroll.setVbarPolicy(ScrollBarPolicy.NEVER);
		dobScroll.setHbarPolicy(ScrollBarPolicy.NEVER);
		dobScroll.setVbarPolicy(ScrollBarPolicy.NEVER);
	}

	/**
	 * This method will display the scanned document
	 */
	private void displayDocument(byte[] document, String documentName) {

		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Converting bytes to Image to display scanned document");

		Image img = convertBytesToImage(document);
		ImageView view = new ImageView(img);
		Scene scene = new Scene(new StackPane(view), 700, 600);
		Stage primaryStage = new Stage();
		primaryStage.setTitle(documentName);
		primaryStage.setScene(scene);
		primaryStage.sizeToScene();
		primaryStage.show();

		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Scanned document displayed succesfully");
	}

	/**
	 * This method will create Image to delete scanned document
	 */
	private ImageView createImageView(VBox vboxElement, ScrollPane scrollPane) {

		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Binding OnAction event Image to delete the attached document");

		Image image = new Image(this.getClass().getResourceAsStream(RegistrationConstants.CLOSE_IMAGE_PATH));
		ImageView imageView = new ImageView(image);
		imageView.setCursor(Cursor.HAND);

		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Creating Image to delete the attached document");

		imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				GridPane gridpane = (GridPane) ((ImageView) event.getSource()).getParent();
				vboxElement.getChildren().remove(gridpane);
				getRegistrationDtoContent().getDemographicDTO().getApplicantDocumentDTO().getDocumentDetailsDTO()
						.removeIf(document -> document.getValue().equals(gridpane.getId()));
				if (vboxElement.getChildren().isEmpty()) {
					scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
					scrollPane.setVbarPolicy(ScrollBarPolicy.NEVER);
				}
			}

		});

		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Image added to delete the attached document");

		return imageView;
	}

	/**
	 * This method will create Hyperlink to view scanned document
	 */
	private Hyperlink createHyperLink(String document) {

		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Creating Hyperlink to display Scanned document");

		Hyperlink hyperLink = new Hyperlink();
		hyperLink.setId(document);
		hyperLink.setText(document);

		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID,
				"Binding OnAction event to Hyperlink to display Scanned document");

		hyperLink.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				GridPane pane = (GridPane) ((Hyperlink) actionEvent.getSource()).getParent();
				getRegistrationDtoContent().getDemographicDTO().getApplicantDocumentDTO().getDocumentDetailsDTO()
						.stream().filter(detail -> detail.getValue().equals(pane.getId())).findFirst()
						.ifPresent(doc -> displayDocument(doc.getDocument(), doc.getValue()));

			}
		});

		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Hyperlink added to display Scanned document");

		return hyperLink;
	}

	private void docScanEdit() {
		// for Document scan
		if (getRegistrationDtoContent().getDemographicDTO().getApplicantDocumentDTO() != null
				&& getRegistrationDtoContent().getDemographicDTO().getApplicantDocumentDTO()
						.getDocumentDetailsDTO() != null) {
			getRegistrationDtoContent().getDemographicDTO().getApplicantDocumentDTO().getDocumentDetailsDTO().stream()
					.filter(doc -> doc.getCategory().equals(RegistrationConstants.POA_DOCUMENT)).findFirst()
					.ifPresent(document -> addDocumentsToScreen(document.getValue(), poaBox, poaScroll));
			getRegistrationDtoContent().getDemographicDTO().getApplicantDocumentDTO().getDocumentDetailsDTO().stream()
					.filter(doc -> doc.getCategory().equals(RegistrationConstants.POI_DOCUMENT)).findFirst()
					.ifPresent(document -> addDocumentsToScreen(document.getValue(), poiBox, poiScroll));
			getRegistrationDtoContent().getDemographicDTO().getApplicantDocumentDTO().getDocumentDetailsDTO().stream()
					.filter(doc -> doc.getCategory().equals(RegistrationConstants.POR_DOCUMENT)).findFirst()
					.ifPresent(document -> addDocumentsToScreen(document.getValue(), porBox, porScroll));
			getRegistrationDtoContent().getDemographicDTO().getApplicantDocumentDTO().getDocumentDetailsDTO().stream()
					.filter(doc -> doc.getCategory().equals(RegistrationConstants.DOB_DOCUMENT)).findFirst()
					.ifPresent(document -> addDocumentsToScreen(document.getValue(), dobBox, dobScroll));

		}

	}

	/**
	 * 
	 * Loading the the labels of local language fields
	 * 
	 */
	private void loadListOfDocuments() {
		try {
			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Loading list of documents");

			poaDocuments.getItems().addAll(RegistrationConstants.getPoaDocumentList());
			poiDocuments.getItems().addAll(RegistrationConstants.getPoaDocumentList());
			porDocuments.getItems().addAll(RegistrationConstants.getPoaDocumentList());
			dobDocuments.getItems().addAll(RegistrationConstants.getPoaDocumentList());

			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Loaded list of documents");

		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - LOADING LIST OF DOCUMENTS FAILED ", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, runtimeException.getMessage());
		}
	}

	public boolean validateDocuments() {
		if (poaBox.getChildren().isEmpty()) {
			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.POA_DOCUMENT_EMPTY);
		} else {
			if (poiBox.getChildren().isEmpty()) {
				generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.POI_DOCUMENT_EMPTY);
			} else {
				if (isChild && porBox.getChildren().isEmpty()) {
					generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.POR_DOCUMENT_EMPTY);
				} else {
					if (dobBox.getChildren().isEmpty()) {
						generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.DOB_DOCUMENT_EMPTY);
					} else {
						return true;
					}
				}
			}
		}

		return false;
	}

	public RegistrationDTO getRegistrationDtoContent() {
		return (RegistrationDTO) SessionContext.getInstance().getMapObject()
				.get(RegistrationConstants.REGISTRATION_DATA);
	}

	protected void prepareEditPageContent() {
		if (getRegistrationDtoContent().getDemographicDTO().getApplicantDocumentDTO() != null
				&& getRegistrationDtoContent().getDemographicDTO().getApplicantDocumentDTO()
						.getDocumentDetailsDTO() != null) {
			getRegistrationDtoContent().getDemographicDTO().getApplicantDocumentDTO().getDocumentDetailsDTO().stream()
					.filter(doc -> doc.getCategory().equals(RegistrationConstants.POA_DOCUMENT)).findFirst()
					.ifPresent(document -> addDocumentsToScreen(document.getValue(), poaBox, poaScroll));
			getRegistrationDtoContent().getDemographicDTO().getApplicantDocumentDTO().getDocumentDetailsDTO().stream()
					.filter(doc -> doc.getCategory().equals(RegistrationConstants.POI_DOCUMENT)).findFirst()
					.ifPresent(document -> addDocumentsToScreen(document.getValue(), poiBox, poiScroll));
			getRegistrationDtoContent().getDemographicDTO().getApplicantDocumentDTO().getDocumentDetailsDTO().stream()
					.filter(doc -> doc.getCategory().equals(RegistrationConstants.POR_DOCUMENT)).findFirst()
					.ifPresent(document -> addDocumentsToScreen(document.getValue(), porBox, porScroll));
			getRegistrationDtoContent().getDemographicDTO().getApplicantDocumentDTO().getDocumentDetailsDTO().stream()
					.filter(doc -> doc.getCategory().equals(RegistrationConstants.DOB_DOCUMENT)).findFirst()
					.ifPresent(document -> addDocumentsToScreen(document.getValue(), dobBox, dobScroll));
		}
		poaDocuments.setValue((String) SessionContext.getInstance().getMapObject().get("poa"));
		poiDocuments.setValue((String) SessionContext.getInstance().getMapObject().get("poi"));
		porDocuments.setValue((String) SessionContext.getInstance().getMapObject().get("por"));
		dobDocuments.setValue((String) SessionContext.getInstance().getMapObject().get("dob"));

	}

}
