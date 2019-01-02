package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.controller.device.ScanPopUpViewController;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.demographic.DocumentDetailsDTO;
import io.mosip.registration.dto.demographic.Identity;
import io.mosip.registration.util.scan.DocumentScanFacade;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
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
 * @since 1.0.0
 */
@Controller
public class DocumentScanController extends BaseController {

	private static final Logger LOGGER = AppConfig.getLogger(DocumentScanController.class);
	private boolean isChild;

	@FXML
	private ComboBox<String> poaDocuments;

	@FXML
	private VBox poaBox;

	@FXML
	private ComboBox<String> poiDocuments;

	@FXML
	private VBox poiBox;

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
	protected Button poaScanBtn;
	@FXML
	protected Button poiScanBtn;
	@FXML
	protected Button porScanBtn;
	@FXML
	protected Button dobScanBtn;
	@FXML
	protected AnchorPane documentScan;

	List<BufferedImage> scannedPages;

	@Value("${DOCUMENT_SIZE}")
	public int documentSize;

	@Value("${SCROLL_CHECK}")
	public int scrollCheck;
	
	@Value("${DOCUMENT_SCANNER_ENABLED}")
	private String isScannerEnabled;

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
		} catch (RuntimeException exception) {
			LOGGER.error("REGISTRATION - CONTROLLER", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					exception.getMessage());
			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.UNABLE_LOAD_REG_PAGE);
		}
	}

	private void setPreviewContent() {
		poaScanBtn.setVisible(false);
		poiScanBtn.setVisible(false);
		porScanBtn.setVisible(false);
		dobScanBtn.setVisible(false);
	}

	/**
	 * This method scans and uploads Proof of Address documents
	 */
	@FXML
	private void scanPoaDocument() {

		scanDocument(poaDocuments, poaBox, RegistrationConstants.POA_DOCUMENT,
				RegistrationUIConstants.POA_DOCUMENT_EMPTY);
	}

	/**
	 * This method scans and uploads Proof of Identity documents
	 */
	@FXML
	private void scanPoiDocument() {

		scanDocument(poiDocuments, poiBox, RegistrationConstants.POI_DOCUMENT,
				RegistrationUIConstants.POI_DOCUMENT_EMPTY);
	}

	/**
	 * This method scans and uploads Proof of Relation documents
	 */
	@FXML
	private void scanPorDocument() {

		scanDocument(porDocuments, porBox, RegistrationConstants.POR_DOCUMENT,
				RegistrationUIConstants.POR_DOCUMENT_EMPTY);
	}

	/**
	 * This method scans and uploads Proof of Date of birth documents
	 */
	@FXML
	private void scanDobDocument() {

		scanDocument(dobDocuments, dobBox, RegistrationConstants.DOB_DOCUMENT,
				RegistrationUIConstants.DOB_DOCUMENT_EMPTY);
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
		} else if (!vboxElement.getChildren().isEmpty()) {
			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "One Document can be added to the Category");

			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.SCAN_DOC_CATEGORY_MULTIPLE);
		} else if (!vboxElement.getChildren().isEmpty() && vboxElement.getChildren().stream()
				.noneMatch(index -> index.getId().contains(documents.getValue()))) {
			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Select only one document category for scan");

			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.SCAN_DOC_CATEGORY_MULTIPLE);
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
		if ("yes".equalsIgnoreCase(isScannerEnabled)) {
			scanPopUpViewController.setDocumentScan(true);
		}
		scanPopUpViewController.init(this, RegistrationUIConstants.SCAN_DOC_TITLE);

		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Scan window displayed to scan and upload documents");
	}

	/**
	 * This method will allow to scan and upload documents
	 */
	@Override
	public void scan(Stage popupStage) {

		try {

			// TODO this check has to removed after when the stubbed data is no more needed
			if ("yes".equalsIgnoreCase(isScannerEnabled)) {
				scanFromScanner();
			} else {
				scanFromStubbed(popupStage);
			}

		} catch (IOException ioException) {
			LOGGER.error(LoggerConstants.LOG_REG_REGISTRATION_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					String.format("%s -> Exception while scanning documents for registration  %s -> %s",
							RegistrationConstants.USER_REG_DOC_SCAN_UPLOAD_EXP, ioException.getMessage(),
							ioException.getCause()));

			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.SCAN_DOCUMENT_ERROR);
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LoggerConstants.LOG_REG_REGISTRATION_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					String.format("%s -> Exception while scanning documents for registration  %s",
							RegistrationConstants.USER_REG_DOC_SCAN_UPLOAD_EXP, runtimeException.getMessage()));

			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.SCAN_DOCUMENT_ERROR);
		}

	}

	private void scanFromStubbed(Stage popupStage) throws IOException {
		byte[] byteArray = documentScanFacade.getScannedDocument();

		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Converting byte array to image");

		if (byteArray.length > documentSize) {
			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.SCAN_DOC_SIZE);
		} else {
			if (selectedDocument != null) {
				LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
						RegistrationConstants.APPLICATION_ID, "Adding documents to Screen");

				DocumentDetailsDTO documentDetailsDTO = new DocumentDetailsDTO();

				switch (selectedDocument) {
				case RegistrationConstants.POA_DOCUMENT:
					getRegistrationDtoContent().getDemographicDTO().getDemographicInfoDTO().getIdentity()
							.setProofOfAddress(documentDetailsDTO);
					attachDocuments(documentDetailsDTO, poaDocuments.getValue(), poaBox, byteArray);
					SessionContext.getInstance().getMapObject().put("poa", poaDocuments.getValue());
					break;
				case RegistrationConstants.POI_DOCUMENT:
					getRegistrationDtoContent().getDemographicDTO().getDemographicInfoDTO().getIdentity()
							.setProofOfIdentity(documentDetailsDTO);
					attachDocuments(documentDetailsDTO, poiDocuments.getValue(), poiBox, byteArray);
					SessionContext.getInstance().getMapObject().put("poi", poiDocuments.getValue());
					break;
				case RegistrationConstants.POR_DOCUMENT:
					getRegistrationDtoContent().getDemographicDTO().getDemographicInfoDTO().getIdentity()
							.setProofOfRelationship(documentDetailsDTO);
					attachDocuments(documentDetailsDTO, porDocuments.getValue(), porBox, byteArray);
					SessionContext.getInstance().getMapObject().put("por", porDocuments.getValue());
					break;
				case RegistrationConstants.DOB_DOCUMENT:
					getRegistrationDtoContent().getDemographicDTO().getDemographicInfoDTO().getIdentity()
							.setDateOfBirthProof(documentDetailsDTO);
					attachDocuments(documentDetailsDTO, dobDocuments.getValue(), dobBox, byteArray);
					SessionContext.getInstance().getMapObject().put("dob", dobDocuments.getValue());
					break;
				default:
				}

				popupStage.close();

				LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
						RegistrationConstants.APPLICATION_ID, "Documents added successfully");
			}
		}
	}

	private void scanFromScanner() throws IOException {
		if (!documentScanFacade.isConnected()) {
			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.SCAN_DOCUMENT_CONNECTION_ERR);
			return;
		}

		scanPopUpViewController.getScanningMsg().setVisible(true);

		BufferedImage bufferedImage = documentScanFacade.getScannedDocumentFromScanner();

		if (bufferedImage == null) {
			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.SCAN_DOCUMENT_ERROR);
			return;
		}
		if (scannedPages == null) {
			scannedPages = new ArrayList<>();
		}
		scannedPages.add(bufferedImage);

		byte[] byteArray = documentScanFacade.getImageBytesFromBufferedImage(bufferedImage);
		/* show the scanned page in the preview */
		scanPopUpViewController.getScanImage().setImage(convertBytesToImage(byteArray));

		scanPopUpViewController.getTotalScannedPages().setText(String.valueOf(scannedPages.size()));

		scanPopUpViewController.getScanningMsg().setVisible(false);
	}

	public void attachScannedDocument(Stage popupStage) throws IOException {

		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Converting byte array to image");
		if (scannedPages == null || scannedPages.isEmpty()) {
			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.SCAN_DOCUMENT_EMPTY);
			return;
		}
		byte[] byteArray = documentScanFacade.getSingleImageFromList(scannedPages);

		if (byteArray.length > documentSize) {
			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.SCAN_DOC_SIZE);
		} else {
			if (selectedDocument != null) {
				LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
						RegistrationConstants.APPLICATION_ID, "Adding documents to Screen");

				DocumentDetailsDTO documentDetailsDTO = new DocumentDetailsDTO();

				switch (selectedDocument) {
				case RegistrationConstants.POA_DOCUMENT:
					getRegistrationDtoContent().getDemographicDTO().getDemographicInfoDTO().getIdentity()
							.setProofOfAddress(documentDetailsDTO);
					attachDocuments(documentDetailsDTO, poaDocuments.getValue(), poaBox, byteArray);
					SessionContext.getInstance().getMapObject().put("poa", poaDocuments.getValue());
					break;
				case RegistrationConstants.POI_DOCUMENT:
					getRegistrationDtoContent().getDemographicDTO().getDemographicInfoDTO().getIdentity()
							.setProofOfIdentity(documentDetailsDTO);
					attachDocuments(documentDetailsDTO, poiDocuments.getValue(), poiBox, byteArray);
					SessionContext.getInstance().getMapObject().put("poi", poiDocuments.getValue());
					break;
				case RegistrationConstants.POR_DOCUMENT:
					getRegistrationDtoContent().getDemographicDTO().getDemographicInfoDTO().getIdentity()
							.setProofOfRelationship(documentDetailsDTO);
					attachDocuments(documentDetailsDTO, porDocuments.getValue(), porBox, byteArray);
					SessionContext.getInstance().getMapObject().put("por", porDocuments.getValue());
					break;
				case RegistrationConstants.DOB_DOCUMENT:
					getRegistrationDtoContent().getDemographicDTO().getDemographicInfoDTO().getIdentity()
							.setDateOfBirthProof(documentDetailsDTO);
					attachDocuments(documentDetailsDTO, dobDocuments.getValue(), dobBox, byteArray);
					SessionContext.getInstance().getMapObject().put("dob", dobDocuments.getValue());
					break;
				default:
				}

				scannedPages.clear();
				popupStage.close();

				LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
						RegistrationConstants.APPLICATION_ID, "Documents added successfully");
			}
		}
	}

	/**
	 * This method will add Hyperlink and Image for scanned documents
	 */
	private void attachDocuments(DocumentDetailsDTO documentDetailsDTO, String document, VBox vboxElement,
			byte[] byteArray) {

		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Attaching documemnts to Pane");

		documentDetailsDTO.setDocument(byteArray);
		documentDetailsDTO.setCategory(document);
		documentDetailsDTO.setFormat(RegistrationConstants.WEB_CAMERA_IMAGE_TYPE);
		documentDetailsDTO.setValue(selectedDocument.concat("_").concat(document));

		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Set details to DocumentDetailsDTO");

		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Set DocumentDetailsDTO to RegistrationDTO");

		addDocumentsToScreen(documentDetailsDTO.getValue(), vboxElement);

		generateAlert(RegistrationConstants.ALERT_INFORMATION, RegistrationUIConstants.SCAN_DOC_SUCCESS);

		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Setting scrollbar policy for scrollpane");

	}

	private void addDocumentsToScreen(String document, VBox vboxElement) {

		GridPane gridPane = new GridPane();
		gridPane.setId(document);
		gridPane.add(createHyperLink(document), 0, vboxElement.getChildren().size());
		gridPane.add(createImageView(vboxElement), 1, vboxElement.getChildren().size());

		vboxElement.getChildren().add(gridPane);

		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Scan document added to Vbox element");

	}

	/**
	 * This method will display the scanned document
	 */
	private void displayDocument(byte[] document, String documentName) {

		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Converting bytes to Image to display scanned document");

		Image img = convertBytesToImage(document);
		ImageView view = new ImageView(img);
		Scene scene = new Scene(new StackPane(view));
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
	private ImageView createImageView(VBox vboxElement) {

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

				switch (((VBox) gridpane.getParent()).getId()) {
				case "poaBox":
					getRegistrationDtoContent().getDemographicDTO().getDemographicInfoDTO().getIdentity()
							.setProofOfAddress(null);
					break;
				case "poiBox":
					getRegistrationDtoContent().getDemographicDTO().getDemographicInfoDTO().getIdentity()
							.setProofOfIdentity(null);
					break;
				case "porBox":
					getRegistrationDtoContent().getDemographicDTO().getDemographicInfoDTO().getIdentity()
							.setProofOfRelationship(null);
					break;
				case "dobBox":
					getRegistrationDtoContent().getDemographicDTO().getDemographicInfoDTO().getIdentity()
							.setDateOfBirthProof(null);
					break;
				default:
				}

				vboxElement.getChildren().remove(gridpane);
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

				DocumentDetailsDTO selectedDocumentToDisplay = null;

				switch (((VBox) pane.getParent()).getId()) {
				case "poaBox":
					selectedDocumentToDisplay = getRegistrationDtoContent().getDemographicDTO().getDemographicInfoDTO()
							.getIdentity().getProofOfAddress();
					break;
				case "poiBox":
					selectedDocumentToDisplay = getRegistrationDtoContent().getDemographicDTO().getDemographicInfoDTO()
							.getIdentity().getProofOfIdentity();
					break;
				case "porBox":
					selectedDocumentToDisplay = getRegistrationDtoContent().getDemographicDTO().getDemographicInfoDTO()
							.getIdentity().getProofOfRelationship();
					break;
				case "dobBox":
					selectedDocumentToDisplay = getRegistrationDtoContent().getDemographicDTO().getDemographicInfoDTO()
							.getIdentity().getDateOfBirthProof();
					break;
				default:
				}

				if (selectedDocumentToDisplay != null) {
					displayDocument(selectedDocumentToDisplay.getDocument(), selectedDocumentToDisplay.getValue());
				}

			}
		});

		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Hyperlink added to display Scanned document");

		return hyperLink;
	}

	private void docScanEdit() {
		// for Document scan
		if (getRegistrationDtoContent().getDemographicDTO() != null) {

			Identity identity = getRegistrationDtoContent().getDemographicDTO().getDemographicInfoDTO().getIdentity();

			if (identity.getProofOfAddress() != null) {
				addDocumentsToScreen(identity.getProofOfAddress().getValue(), poaBox);
			}
			if (identity.getProofOfIdentity() != null) {
				addDocumentsToScreen(identity.getProofOfIdentity().getValue(), poiBox);
			}
			if (identity.getProofOfRelationship() != null) {
				addDocumentsToScreen(identity.getProofOfRelationship().getValue(), porBox);
			}
			if (identity.getDateOfBirthProof() != null) {
				addDocumentsToScreen(identity.getDateOfBirthProof().getValue(), dobBox);
			}

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

			poaDocuments.getItems().addAll(RegistrationConstants.DOCUMENT_LIST);
			poiDocuments.getItems().addAll(RegistrationConstants.DOCUMENT_LIST);
			porDocuments.getItems().addAll(RegistrationConstants.DOCUMENT_LIST);
			dobDocuments.getItems().addAll(RegistrationConstants.DOCUMENT_LIST);

			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Loaded list of documents");

		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - LOADING LIST OF DOCUMENTS FAILED ", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, runtimeException.getMessage());
		}
	}

	public boolean validateDocuments() {
		if (poaBox.getChildren().isEmpty()) {
			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.POA_DOCUMENT_EMPTY);
		} else {
			if (poiBox.getChildren().isEmpty()) {
				generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.POI_DOCUMENT_EMPTY);
			} else {
				if (isChild && porBox.getChildren().isEmpty()) {
					generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.POR_DOCUMENT_EMPTY);
				} else {
					if (dobBox.getChildren().isEmpty()) {
						generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.DOB_DOCUMENT_EMPTY);
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
		if (getRegistrationDtoContent().getDemographicDTO() != null) {

			Identity identity = getRegistrationDtoContent().getDemographicDTO().getDemographicInfoDTO().getIdentity();

			if (identity.getProofOfAddress() != null) {
				addDocumentsToScreen(identity.getProofOfAddress().getValue(), poaBox);
			}
			if (identity.getProofOfIdentity() != null) {
				addDocumentsToScreen(identity.getProofOfIdentity().getValue(), poiBox);
			}
			if (identity.getProofOfRelationship() != null) {
				addDocumentsToScreen(identity.getProofOfRelationship().getValue(), porBox);
			}
			if (identity.getDateOfBirthProof() != null) {
				addDocumentsToScreen(identity.getDateOfBirthProof().getValue(), dobBox);
			}
		}
		poaDocuments.setValue((String) SessionContext.getInstance().getMapObject().get("poa"));
		poiDocuments.setValue((String) SessionContext.getInstance().getMapObject().get("poi"));
		porDocuments.setValue((String) SessionContext.getInstance().getMapObject().get("por"));
		dobDocuments.setValue((String) SessionContext.getInstance().getMapObject().get("dob"));

	}

}
