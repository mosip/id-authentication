package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.MappedCodeForLanguage;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.controller.device.ScanPopUpViewController;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.demographic.DocumentDetailsDTO;
import io.mosip.registration.dto.demographic.Identity;
import io.mosip.registration.entity.mastersync.MasterDocumentType;
import io.mosip.registration.service.MasterSyncService;
import io.mosip.registration.util.scan.DocumentScanFacade;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
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

	@Autowired
	MasterSyncService masterSync;

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

	@FXML
	protected ImageView docPreviewImgView;

	@FXML
	protected Button docPreviewNext;

	@FXML
	protected Button docPreviewPrev;

	@FXML
	protected Text docPageNumber;

	List<BufferedImage> scannedPages;

	private List<MasterDocumentType> documents;

	@Value("${DOCUMENT_SIZE}")
	public int documentSize;

	@Value("${DOCUMENT_SCANNER_ENABLED}")
	private String isScannerEnabled;

	@Value("${DOCUMENT_SCANNER_DOCTYPE}")
	private String scannerDocType;

	List<BufferedImage> docPages;

	@FXML
	private void initialize() {
		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Entering the LOGIN_CONTROLLER");
		try {
			auditFactory.audit(AuditEvent.GET_REGISTRATION_CONTROLLER, Components.REGISTRATION_CONTROLLER,
					"initializing the registration controller",
					SessionContext.getInstance().getUserContext().getUserId(),
					RegistrationConstants.ONBOARD_DEVICES_REF_ID_TYPE);

			isChild = true;
			loadListOfDocuments(poaDocuments, "POA");
			loadListOfDocuments(poiDocuments, "POI");
			loadListOfDocuments(porDocuments, "POR");
			loadListOfDocuments(dobDocuments, "POB");
		} catch (RuntimeException exception) {
			LOGGER.error("REGISTRATION - CONTROLLER", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					exception.getMessage());
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UNABLE_LOAD_REG_PAGE);
		}
	}

	public void setPreviewContent() {
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
			LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Select atleast one document for scan");

			generateAlert(RegistrationConstants.ERROR, errorMessage);
			documents.requestFocus();
		} else if (!vboxElement.getChildren().isEmpty()) {
			LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "One Document can be added to the Category");

			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.SCAN_DOC_CATEGORY_MULTIPLE);
		} else if (!vboxElement.getChildren().isEmpty() && vboxElement.getChildren().stream()
				.noneMatch(index -> index.getId().contains(documents.getValue()))) {
			LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Select only one document category for scan");

			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.SCAN_DOC_CATEGORY_MULTIPLE);
		} else {
			LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
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

		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
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

			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.SCAN_DOCUMENT_ERROR);
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LoggerConstants.LOG_REG_REGISTRATION_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					String.format("%s -> Exception while scanning documents for registration  %s",
							RegistrationConstants.USER_REG_DOC_SCAN_UPLOAD_EXP, runtimeException.getMessage()));

			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.SCAN_DOCUMENT_ERROR);
		}

	}

	private void scanFromStubbed(Stage popupStage) throws IOException {
		byte[] byteArray = documentScanFacade.getScannedDocument();

		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Converting byte array to image");

		if (byteArray.length > documentSize) {
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.SCAN_DOC_SIZE);
		} else {
			if (selectedDocument != null) {

				scanPopUpViewController.getScanImage().setImage(convertBytesToImage(byteArray));
				
				LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
						RegistrationConstants.APPLICATION_ID, "Adding documents to Screen");

				DocumentDetailsDTO documentDetailsDTO = new DocumentDetailsDTO();

				switch (selectedDocument) {
				case RegistrationConstants.POA_DOCUMENT:
					getRegistrationDtoContent().getDemographicDTO().getDemographicInfoDTO().getIdentity()
							.setProofOfAddress(documentDetailsDTO);
					attachDocuments(documentDetailsDTO, poaDocuments.getValue(), poaBox, byteArray);
					loadMapObject().put("poa", poaDocuments.getValue());
					break;
				case RegistrationConstants.POI_DOCUMENT:
					getRegistrationDtoContent().getDemographicDTO().getDemographicInfoDTO().getIdentity()
							.setProofOfIdentity(documentDetailsDTO);
					attachDocuments(documentDetailsDTO, poiDocuments.getValue(), poiBox, byteArray);
					loadMapObject().put("poi", poiDocuments.getValue());
					break;
				case RegistrationConstants.POR_DOCUMENT:
					getRegistrationDtoContent().getDemographicDTO().getDemographicInfoDTO().getIdentity()
							.setProofOfRelationship(documentDetailsDTO);
					attachDocuments(documentDetailsDTO, porDocuments.getValue(), porBox, byteArray);
					loadMapObject().put("por", porDocuments.getValue());
					break;
				case RegistrationConstants.DOB_DOCUMENT:
					getRegistrationDtoContent().getDemographicDTO().getDemographicInfoDTO().getIdentity()
							.setProofOfDateOfBirth(documentDetailsDTO);
					attachDocuments(documentDetailsDTO, dobDocuments.getValue(), dobBox, byteArray);
					loadMapObject().put("dob", dobDocuments.getValue());
					break;
				default:
				}

				popupStage.close();

				LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
						RegistrationConstants.APPLICATION_ID, "Documents added successfully");
			}
		}
	}

	private void scanFromScanner() throws IOException {
		if (!documentScanFacade.isConnected()) {
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.SCAN_DOCUMENT_CONNECTION_ERR);
			return;
		}

		scanPopUpViewController.getScanningMsg().setVisible(true);

		BufferedImage bufferedImage = documentScanFacade.getScannedDocumentFromScanner();

		if (bufferedImage == null) {
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.SCAN_DOCUMENT_ERROR);
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

		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Converting byte array to image");
		if (scannedPages == null || scannedPages.isEmpty()) {
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.SCAN_DOCUMENT_EMPTY);
			return;
		}
		byte[] byteArray;
		if (!"pdf".equalsIgnoreCase(scannerDocType)) {
			byteArray = documentScanFacade.asImage(scannedPages);
		} else {
			byteArray = documentScanFacade.asPDF(scannedPages);
		}
		if (byteArray == null) {
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.SCAN_DOCUMENT_CONVERTION_ERR);
			return;
		}
		if (byteArray.length > documentSize) {
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.SCAN_DOC_SIZE);
		} else {
			if (selectedDocument != null) {
				LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
						RegistrationConstants.APPLICATION_ID, "Adding documents to Screen");

				DocumentDetailsDTO documentDetailsDTO = new DocumentDetailsDTO();

				switch (selectedDocument) {
				case RegistrationConstants.POA_DOCUMENT:
					getRegistrationDtoContent().getDemographicDTO().getDemographicInfoDTO().getIdentity()
							.setProofOfAddress(documentDetailsDTO);
					attachDocuments(documentDetailsDTO, poaDocuments.getValue(), poaBox, byteArray);
					loadMapObject().put("poa", poaDocuments.getValue());
					break;
				case RegistrationConstants.POI_DOCUMENT:
					getRegistrationDtoContent().getDemographicDTO().getDemographicInfoDTO().getIdentity()
							.setProofOfIdentity(documentDetailsDTO);
					attachDocuments(documentDetailsDTO, poiDocuments.getValue(), poiBox, byteArray);
					loadMapObject().put("poi", poiDocuments.getValue());
					break;
				case RegistrationConstants.POR_DOCUMENT:
					getRegistrationDtoContent().getDemographicDTO().getDemographicInfoDTO().getIdentity()
							.setProofOfRelationship(documentDetailsDTO);
					attachDocuments(documentDetailsDTO, porDocuments.getValue(), porBox, byteArray);
					loadMapObject().put("por", porDocuments.getValue());
					break;
				case RegistrationConstants.DOB_DOCUMENT:
					getRegistrationDtoContent().getDemographicDTO().getDemographicInfoDTO().getIdentity()
							.setProofOfDateOfBirth(documentDetailsDTO);
					attachDocuments(documentDetailsDTO, dobDocuments.getValue(), dobBox, byteArray);
					loadMapObject().put("dob", dobDocuments.getValue());
					break;
				default:
				}

				scannedPages.clear();
				popupStage.close();

				LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
						RegistrationConstants.APPLICATION_ID, "Documents added successfully");
			}
		}
	}

	private Map<String, Object> loadMapObject() {
		return SessionContext.getInstance().getMapObject();
	}

	/**
	 * This method will add Hyperlink and Image for scanned documents
	 */
	private void attachDocuments(DocumentDetailsDTO documentDetailsDTO, String document, VBox vboxElement,
			byte[] byteArray) {

		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Attaching documemnts to Pane");

		documentDetailsDTO.setDocument(byteArray);
		documentDetailsDTO.setType(document);
		documentDetailsDTO.setFormat(scannerDocType);
		documentDetailsDTO.setValue(selectedDocument.concat("_").concat(document));

		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Set details to DocumentDetailsDTO");

		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Set DocumentDetailsDTO to RegistrationDTO");

		addDocumentsToScreen(documentDetailsDTO.getValue(), documentDetailsDTO.getFormat(), vboxElement);

		generateAlert(RegistrationConstants.ALERT_INFORMATION, RegistrationUIConstants.SCAN_DOC_SUCCESS);

		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Setting scrollbar policy for scrollpane");

	}

	private void addDocumentsToScreen(String document, String documentFormat, VBox vboxElement) {

		GridPane gridPane = new GridPane();
		gridPane.setId(document);
		gridPane.add(createHyperLink(document.concat("." + documentFormat)), 0, vboxElement.getChildren().size());
		gridPane.add(createImageView(vboxElement), 1, vboxElement.getChildren().size());

		vboxElement.getChildren().add(gridPane);

		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Scan document added to Vbox element");

	}

	/**
	 * This method will display the scanned document
	 */
	private void displayDocument(byte[] document, String documentName) {

		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Converting bytes to Image to display scanned document");
		/* clearing the previously loaded pdf pages inorder to clear up the memory */
		initializePreviewSection();
		if ("pdf".equalsIgnoreCase(documentName.substring(documentName.lastIndexOf(".") + 1))) {

			try {
				docPages = documentScanFacade.pdfToImages(document, documentName);
				if (!docPages.isEmpty()) {
					docPreviewImgView.setImage(
							convertBytesToImage(documentScanFacade.getImageBytesFromBufferedImage(docPages.get(0))));
					docPageNumber.setText("1");
					if (docPages.size() > 1) {
						docPreviewNext.setDisable(false);
					}
				}
			} catch (IOException e) {
				generateAlert(RegistrationConstants.ERROR, "Unable to preview the document");
				return;
			}

		} else {
			docPreviewImgView.setImage(convertBytesToImage(document));
			docPageNumber.setText("1");
		}
		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Scanned document displayed succesfully");
	}

	public void previewNextPage() {

		if (isDocsNotEmpty()) {
			int pageNumber = Integer.parseInt(docPageNumber.getText());
			if (docPages.size() > pageNumber) {
				setDocPreview(pageNumber, pageNumber + 1);
				docPreviewPrev.setDisable(false);
				if ((pageNumber + 1) == docPages.size()) {
					docPreviewNext.setDisable(true);
				}
			}
		}
	}

	public void previewPrevPage() {
		if (isDocsNotEmpty()) {
			int pageNumber = Integer.parseInt(docPageNumber.getText());
			if (pageNumber > 1) {
				setDocPreview(pageNumber - 2, pageNumber - 1);
				docPreviewNext.setDisable(false);
				if ((pageNumber - 1) == 1) {
					docPreviewPrev.setDisable(true);
				}
			}
		}
	}

	private boolean isDocsNotEmpty() {
		return StringUtils.isNotEmpty(docPageNumber.getText()) && docPages != null && !docPages.isEmpty();
	}

	private void setDocPreview(int index, int pageNumber) {
		try {
			docPreviewImgView.setImage(
					convertBytesToImage(documentScanFacade.getImageBytesFromBufferedImage(docPages.get(index))));
		} catch (IOException e) {
			generateAlert(RegistrationConstants.ERROR, "Unable to preview the document");
			return;
		}
		docPageNumber.setText(String.valueOf(pageNumber));
	}

	/**
	 * This method will create Image to delete scanned document
	 */
	private ImageView createImageView(VBox vboxElement) {

		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Binding OnAction event Image to delete the attached document");

		Image image = new Image(this.getClass().getResourceAsStream(RegistrationConstants.CLOSE_IMAGE_PATH));
		ImageView imageView = new ImageView(image);
		imageView.setCursor(Cursor.HAND);

		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
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
							.setProofOfDateOfBirth(null);
					break;
				default:
				}

				vboxElement.getChildren().remove(gridpane);
			}

		});

		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Image added to delete the attached document");

		return imageView;
	}

	/**
	 * This method will create Hyperlink to view scanned document
	 */
	private Hyperlink createHyperLink(String document) {

		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Creating Hyperlink to display Scanned document");

		Hyperlink hyperLink = new Hyperlink();
		hyperLink.setId(document);
		hyperLink.setText(document);

		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
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
							.getIdentity().getProofOfDateOfBirth();
					break;
				default:
				}

				if (selectedDocumentToDisplay != null) {
					displayDocument(selectedDocumentToDisplay.getDocument(), hyperLink.getText());
				}

			}
		});

		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Hyperlink added to display Scanned document");

		return hyperLink;
	}

	private void docScanEdit() {
		// for Document scan
		if (getRegistrationDtoContent().getDemographicDTO() != null) {

			Identity identity = getRegistrationDtoContent().getDemographicDTO().getDemographicInfoDTO().getIdentity();

			if (identity.getProofOfAddress() != null) {
				addDocumentsToScreen(identity.getProofOfAddress().getValue(), identity.getProofOfAddress().getFormat(),
						poaBox);
			}
			if (identity.getProofOfIdentity() != null) {
				addDocumentsToScreen(identity.getProofOfIdentity().getValue(),
						identity.getProofOfIdentity().getFormat(), poiBox);
			}
			if (identity.getProofOfRelationship() != null) {
				addDocumentsToScreen(identity.getProofOfRelationship().getValue(),
						identity.getProofOfRelationship().getFormat(), porBox);
			}
			if (identity.getProofOfDateOfBirth() != null) {
				addDocumentsToScreen(identity.getProofOfDateOfBirth().getValue(),
						identity.getProofOfDateOfBirth().getFormat(), dobBox);
			}

		}

	}

	/**
	 * 
	 * Loading the the labels of local language fields
	 * 
	 */
	private void loadListOfDocuments(ComboBox<String> selectionList, String docCode) {
		try {
			LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Loading list of documents");
			documents = masterSync.getDocumentCategories(docCode,
					MappedCodeForLanguage
							.valueOf(AppConfig.getApplicationProperty(RegistrationConstants.APPLICATION_LANGUAGE))
							.getMappedCode());
			List<String> documentNames = documents.stream().map(doc -> doc.getName()).collect(Collectors.toList());

			selectionList.getItems().addAll(documentNames);
			LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Loaded list of documents");

		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - LOADING LIST OF DOCUMENTS FAILED ", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, runtimeException.getMessage());
		}
	}

	public RegistrationDTO getRegistrationDtoContent() {
		return (RegistrationDTO) loadMapObject().get(RegistrationConstants.REGISTRATION_DATA);
	}

	protected void prepareEditPageContent() {
		if (getRegistrationDtoContent().getDemographicDTO() != null) {

			Identity identity = getRegistrationDtoContent().getDemographicDTO().getDemographicInfoDTO().getIdentity();

			if (identity.getProofOfAddress() != null) {
				addDocumentsToScreen(identity.getProofOfAddress().getValue(), identity.getProofOfAddress().getFormat(),
						poaBox);
			}
			if (identity.getProofOfIdentity() != null) {
				addDocumentsToScreen(identity.getProofOfIdentity().getValue(),
						identity.getProofOfIdentity().getFormat(), poiBox);
			}
			if (identity.getProofOfRelationship() != null) {
				addDocumentsToScreen(identity.getProofOfRelationship().getValue(),
						identity.getProofOfRelationship().getFormat(), porBox);
			}
			if (identity.getProofOfDateOfBirth() != null) {
				addDocumentsToScreen(identity.getProofOfDateOfBirth().getValue(),
						identity.getProofOfDateOfBirth().getFormat(), dobBox);
			}
		}
		poaDocuments.setValue((String) loadMapObject().get("poa"));
		poiDocuments.setValue((String) loadMapObject().get("poi"));
		porDocuments.setValue((String) loadMapObject().get("por"));
		dobDocuments.setValue((String) loadMapObject().get("dob"));

	}

	public void clearDocSection() {
		clearAllDocs();
		initializePreviewSection();
	}

	private void clearAllDocs() {
		poaBox.getChildren().clear();
		porBox.getChildren().clear();
		dobBox.getChildren().clear();
		poiBox.getChildren().clear();
	}

	private void initializePreviewSection() {
		docPreviewNext.setDisable(true);
		docPreviewPrev.setDisable(true);
		docPageNumber.setText("");
		docPreviewImgView.setImage(null);
		docPages = null;
	}

}
