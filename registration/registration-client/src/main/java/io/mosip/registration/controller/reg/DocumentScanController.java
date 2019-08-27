package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.pdfgenerator.spi.PDFGenerator;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.AuditReferenceIdTypes;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.controller.FXUtils;
import io.mosip.registration.controller.device.FaceCaptureController;
import io.mosip.registration.controller.device.FingerPrintCaptureController;
import io.mosip.registration.controller.device.GuardianBiometricsController;
import io.mosip.registration.controller.device.IrisCaptureController;
import io.mosip.registration.controller.device.ScanPopUpViewController;
import io.mosip.registration.dto.demographic.DocumentDetailsDTO;
import io.mosip.registration.dto.mastersync.DocumentCategoryDto;
import io.mosip.registration.entity.DocumentCategory;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.doc.category.DocumentCategoryService;
import io.mosip.registration.service.sync.MasterSyncService;
import io.mosip.registration.util.scan.DocumentScanFacade;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/**
 * {@code DocumentScanController} is to handle the screen of the Demographic
 * document section details
 * 
 * @author M1045980
 * @since 1.0.0
 */
@Controller
public class DocumentScanController extends BaseController {

	private static final Logger LOGGER = AppConfig.getLogger(DocumentScanController.class);

	@FXML
	private Label bioExceptionToggleLabel1;

	@FXML
	private Label bioExceptionToggleLabel2;

	private boolean toggleBiometricException;

	private SimpleBooleanProperty switchedOnForBiometricException;

	@Autowired
	private RegistrationController registrationController;

	private String selectedDocument;

	private ComboBox<DocumentCategoryDto> selectedComboBox;

	private VBox selectedDocVBox;

	private Map<String, ComboBox<DocumentCategoryDto>> documentComboBoxes = new HashMap<>();

	private Map<String, VBox> documentVBoxes = new HashMap<>();

	@Autowired
	private ScanPopUpViewController scanPopUpViewController;

	@Autowired
	private DocumentScanFacade documentScanFacade;

	@Autowired
	private PDFGenerator pdfGenerator;

	@FXML
	protected GridPane documentScan;

	@FXML
	private GridPane documentPane;

	@FXML
	private GridPane exceptionPane;

	@FXML
	protected ImageView docPreviewImgView;

	@FXML
	protected Label docPreviewNext;

	@FXML
	protected Label docPreviewPrev;

	@FXML
	protected Label docPageNumber;

	@FXML
	protected Label docPreviewLabel;
	@FXML
	public GridPane documentScanPane;

	@FXML
	private VBox docScanVbox;

	private List<BufferedImage> scannedPages;

	@Autowired
	private FaceCaptureController faceCaptureController;

	@Autowired
	private FingerPrintCaptureController fingerPrintCaptureController;

	@Autowired
	private IrisCaptureController irisCaptureController;

	@Autowired
	private GuardianBiometricsController guardianBiometricsController;

	@Autowired
	private MasterSyncService masterSyncService;

	@Autowired
	private DocumentCategoryService documentCategoryService;

	@Autowired
	private BiometricExceptionController biometricExceptionController;

	@Autowired
	private DemographicDetailController demographicDetailController;

	private List<BufferedImage> docPages;

	@FXML
	private Label registrationNavlabel;

	@FXML
	private Button continueBtn;
	@FXML
	private Button backBtn;
	@FXML
	private ImageView backImageView;
	@FXML
	private Label biometricExceptionReq;

	/**
	 * @return the bioExceptionToggleLabel1
	 */
	public Label getBioExceptionToggleLabel1() {
		return bioExceptionToggleLabel1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.fxml.Initializable#initialize()
	 */
	@FXML
	private void initialize() {
		LOGGER.info(RegistrationConstants.DOCUMNET_SCAN_CONTROLLER, APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Entering the DOCUMENT_SCAN_CONTROLLER");

		Image backInWhite = new Image(getClass().getResourceAsStream(RegistrationConstants.BACK_FOCUSED));
		Image backImage = new Image(getClass().getResourceAsStream(RegistrationConstants.BACK));
		backBtn.hoverProperty().addListener((ov, oldValue, newValue) -> {
			if (newValue) {
				backImageView.setImage(backInWhite);
			} else {
				backImageView.setImage(backImage);
			}
		});

		try {
			if (getRegistrationDTOFromSession() != null
					&& getRegistrationDTOFromSession().getSelectionListDTO() != null) {

				registrationNavlabel.setText(ApplicationContext.applicationLanguageBundle()
						.getString(RegistrationConstants.UIN_UPDATE_UINUPDATENAVLBL));
			}

			switchedOnForBiometricException = new SimpleBooleanProperty(false);
			toggleFunctionForBiometricException();

			if (getRegistrationDTOFromSession() != null
					&& getRegistrationDTOFromSession().getRegistrationMetaDataDTO().getRegistrationCategory() != null
					&& getRegistrationDTOFromSession().getRegistrationMetaDataDTO().getRegistrationCategory()
							.equals(RegistrationConstants.PACKET_TYPE_LOST)) {

				registrationNavlabel.setText(
						ApplicationContext.applicationLanguageBundle().getString(RegistrationConstants.LOSTUINLBL));
				docScanVbox.setDisable(true);
				continueBtn.setDisable(false);
			} else {
				continueBtn.setDisable(true);
			}

			// populateDocumentCategories();
		} catch (RuntimeException exception) {
			LOGGER.error("REGISTRATION - DOCUMENT_SCAN_CONTROLLER", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					exception.getMessage() + ExceptionUtils.getStackTrace(exception));
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UNABLE_LOAD_REG_PAGE);
		}
	}

	/**
	 * To populate the document categories
	 */
	protected <T> void populateDocumentCategories() {

		/* clearing all the previously added fields */
		docScanVbox.getChildren().clear();
		documentComboBoxes.clear();
		documentVBoxes.clear();
		initializePreviewSection();

		List<DocumentCategory> documentCategories = documentCategoryService
				.getDocumentCategoriesByLangCode(ApplicationContext.applicationLanguage());

		DocumentCategory pobCategory = new DocumentCategory();
		for (DocumentCategory documentCategory : documentCategories) {
			if (documentCategory.getCode().equalsIgnoreCase(RegistrationConstants.DOB_DOCUMENT)) {
				pobCategory = documentCategory;
			}
		}
		if (pobCategory.getCode() != null) {
			documentCategories.remove(pobCategory);
		}

		docScanVbox.setSpacing(5);
		if (documentCategories != null && !documentCategories.isEmpty())
			prepareDocumentScanSection(documentCategories);

		/*
		 * populate the documents for edit if its already present or fetched from pre
		 * reg
		 */
		Map<String, DocumentDetailsDTO> documentsMap = getDocumentsMapFromSession();
		if (documentsMap != null && !documentsMap.isEmpty() && !documentVBoxes.isEmpty()) {
			Set<String> docCategoryKeys = documentVBoxes.keySet();
			documentsMap.keySet().retainAll(docCategoryKeys);
			for (String docCategoryKey : docCategoryKeys) {
				DocumentDetailsDTO documentDetailsDTO = documentsMap.get(docCategoryKey);
				if (documentDetailsDTO != null) {
					addDocumentsToScreen(documentDetailsDTO.getValue(), documentDetailsDTO.getFormat(),
							documentVBoxes.get(docCategoryKey));
					FXUtils.getInstance().selectComboBoxValue(documentComboBoxes.get(docCategoryKey),
							documentDetailsDTO.getValue().substring(
									documentDetailsDTO.getValue().indexOf(RegistrationConstants.UNDER_SCORE) + 1));
				}
			}
		} else if (documentVBoxes.isEmpty() && documentsMap != null) {
			documentsMap.clear();
		}

		if (getRegistrationDTOFromSession().getSelectionListDTO() != null && RegistrationConstants.DISABLE
				.equalsIgnoreCase(getValueFromApplicationContext(RegistrationConstants.DOC_DISABLE_FLAG))) {
			documentPane.setVisible(false);
		}
		validateDocumentsPane();
	}

	private Map<String, DocumentDetailsDTO> getDocumentsMapFromSession() {
		return getRegistrationDTOFromSession().getDemographicDTO().getApplicantDocumentDTO().getDocuments();
	}

	/**
	 * To prepare the document section
	 */
	@SuppressWarnings("unchecked")
	private <T> void prepareDocumentScanSection(List<DocumentCategory> documentCategories) {

		/* show the scan doc info label for format and size */
		Label fileSizeInfoLabel = new Label();
		fileSizeInfoLabel.setWrapText(true);
		fileSizeInfoLabel.setText(RegistrationUIConstants.SCAN_DOC_INFO);
		docScanVbox.getChildren().add(fileSizeInfoLabel);

		for (DocumentCategory documentCategory : documentCategories) {

			String docCategoryCode = documentCategory.getCode();

			String docCategoryName = documentCategory.getName();

			List<DocumentCategoryDto> documentCategoryDtos = null;

			try {
				documentCategoryDtos = masterSyncService.getDocumentCategories(docCategoryCode,
						ApplicationContext.applicationLanguage());
			} catch (RuntimeException runtimeException) {
				LOGGER.error("REGISTRATION - LOADING LIST OF DOCUMENTS FAILED ", APPLICATION_NAME,
						RegistrationConstants.APPLICATION_ID,
						runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));
			} catch (RegBaseCheckedException checkedException) {
				LOGGER.error("REGISTRATION - LOADING LIST OF DOCUMENTS FAILED ", APPLICATION_NAME,
						RegistrationConstants.APPLICATION_ID,
						checkedException.getMessage() + ExceptionUtils.getStackTrace(checkedException));
			}

			if (documentCategoryDtos != null && !documentCategoryDtos.isEmpty()) {
				HBox hBox = new HBox();

				ComboBox<DocumentCategoryDto> comboBox = new ComboBox<>();
				comboBox.setPrefWidth(docScanVbox.getWidth() / 2);
				comboBox.setId(docCategoryCode);

				comboBox.valueProperty().addListener((v, oldValue, newValue) -> {
					validateDocumentsPane();
				});
				ImageView indicatorImage = new ImageView(
						new Image(this.getClass().getResourceAsStream(RegistrationConstants.CLOSE_IMAGE_PATH), 15, 15,
								true, true));
				comboBox.setPromptText(docCategoryName);
				comboBox.getStyleClass().add(RegistrationConstants.DOC_COMBO_BOX);
				Label documentLabel = new Label(docCategoryName);
				documentLabel.getStyleClass().add(RegistrationConstants.DEMOGRAPHIC_FIELD_LABEL);
				documentLabel.setPrefWidth(docScanVbox.getWidth() / 2);
				documentLabel.setVisible(false);
				comboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
					documentLabel.setVisible(true);
				});
				StringConverter<T> uiRenderForComboBox = FXUtils.getInstance().getStringConverterForComboBox();
				comboBox.setConverter((StringConverter<DocumentCategoryDto>) uiRenderForComboBox);
				if (applicationContext.isPrimaryLanguageRightToLeft()) {
					comboBox.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
					documentLabel.setAlignment(Pos.CENTER_RIGHT);
				}

				/*
				 * adding all the dynamically created combo boxes in a map inorder to show it in
				 * the edit page
				 */
				documentComboBoxes.put(docCategoryCode, comboBox);

				VBox documentVBox = new VBox();
				documentVBox.getStyleClass().add(RegistrationConstants.SCAN_VBOX);
				documentVBox.setId(docCategoryCode);

				documentVBoxes.put(docCategoryCode, documentVBox);

				Button scanButton = new Button();
				scanButton.setText(RegistrationUIConstants.SCAN);
				scanButton.setId(docCategoryCode);
				scanButton.getStyleClass().add(RegistrationConstants.DOCUMENT_CONTENT_BUTTON);
				scanButton.setGraphic(new ImageView(new Image(
						this.getClass().getResourceAsStream(RegistrationConstants.SCAN), 12, 12, true, true)));
				scanButton.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {

						auditFactory.audit(
								AuditEvent.valueOf(
										String.format("REG_DOC_%S_SCAN", ((Button) event.getSource()).getId())),
								Components.REG_DOCUMENTS, SessionContext.userId(),
								AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

						Button clickedBtn = (Button) event.getSource();
						clickedBtn.getId();
						scanDocument(comboBox, documentVBox, documentCategory.getCode(),
								RegistrationUIConstants.PLEASE_SELECT + RegistrationConstants.SPACE
										+ documentCategory.getCode() + " " + RegistrationUIConstants.DOCUMENT);
					}
				});
				scanButton.hoverProperty().addListener((ov, oldValue, newValue) -> {
					if (newValue) {
						scanButton.setGraphic(new ImageView(
								new Image(this.getClass().getResourceAsStream(RegistrationConstants.SCAN_FOCUSED), 12,
										12, true, true)));
					} else {
						scanButton.setGraphic(new ImageView(new Image(
								this.getClass().getResourceAsStream(RegistrationConstants.SCAN), 12, 12, true, true)));
					}
				});
				hBox.getChildren().addAll(new VBox(new Label(), indicatorImage), comboBox, documentVBox, scanButton);
				docScanVbox.getChildren().addAll(new HBox(new Label("       "), documentLabel), hBox);
				hBox.setId(documentCategory.getCode());
				documentLabel.setId(documentCategory.getCode() + RegistrationConstants.LABEL);
				comboBox.getItems().addAll(documentCategoryDtos);
			}

		}
	}

	/**
	 * This method scans and uploads documents
	 */
	private void scanDocument(ComboBox<DocumentCategoryDto> documents, VBox vboxElement, String document,
			String errorMessage) {

		if (documents.getValue() == null) {
			LOGGER.info(RegistrationConstants.DOCUMNET_SCAN_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Select atleast one document for scan");

			generateAlert(RegistrationConstants.ERROR, errorMessage);
			documents.requestFocus();
		} else if (!vboxElement.getChildren().isEmpty()) {
			LOGGER.info(RegistrationConstants.DOCUMNET_SCAN_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "only One Document can be added to the Category");

			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.SCAN_DOC_CATEGORY_MULTIPLE);
		} else if (!vboxElement.getChildren().isEmpty() && vboxElement.getChildren().stream()
				.noneMatch(index -> index.getId().contains(documents.getValue().getName()))) {
			LOGGER.info(RegistrationConstants.DOCUMNET_SCAN_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Select only one document category for scan");

			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.SCAN_DOC_CATEGORY_MULTIPLE);
		} else {
			LOGGER.info(RegistrationConstants.DOCUMNET_SCAN_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Displaying Scan window to scan Documents");
			documents.getValue().setCode(document);
			selectedDocument = document;
			selectedComboBox = documents;
			selectedDocVBox = vboxElement;
			scanWindow();
		}

	}

	/**
	 * This method will display Scan window to scan and upload documents
	 */
	private void scanWindow() {
		if (RegistrationConstants.YES
				.equalsIgnoreCase(getValueFromApplicationContext(RegistrationConstants.DOC_SCANNER_ENABLED))) {
			scanPopUpViewController.setDocumentScan(true);
		}
		scanPopUpViewController.init(this, RegistrationUIConstants.SCAN_DOC_TITLE);

		LOGGER.info(RegistrationConstants.DOCUMNET_SCAN_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Scan window displayed to scan and upload documents");
	}

	/**
	 * This method will allow to scan and upload documents
	 */
	@Override
	public void scan(Stage popupStage) {
		try {

			// TODO this check has to removed after when the stubbed data is no
			// more needed
			if (RegistrationConstants.YES
					.equalsIgnoreCase(getValueFromApplicationContext(RegistrationConstants.DOC_SCANNER_ENABLED))) {
				scanFromScanner();
			} else {
				scanFromStubbed(popupStage);
			}
		} catch (IOException ioException) {
			LOGGER.error(LoggerConstants.LOG_REG_REGISTRATION_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					String.format("%s -> Exception while scanning documents for registration  %s -> %s",
							RegistrationConstants.USER_REG_DOC_SCAN_UPLOAD_EXP, ioException.getMessage(),
							ExceptionUtils.getStackTrace(ioException)));

			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.SCAN_DOCUMENT_ERROR);
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LoggerConstants.LOG_REG_REGISTRATION_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					String.format("%s -> Exception while scanning documents for registration  %s",
							RegistrationConstants.USER_REG_DOC_SCAN_UPLOAD_EXP, runtimeException.getMessage())
							+ ExceptionUtils.getStackTrace(runtimeException));

			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.SCAN_DOCUMENT_ERROR);
		}

	}

	/**
	 * This method will get the stubbed data for the scan
	 */
	private void scanFromStubbed(Stage popupStage) throws IOException {
		byte[] byteArray = documentScanFacade.getScannedDocument();
		String documentSize = getValueFromApplicationContext(RegistrationConstants.DOC_SIZE);
		
		LOGGER.info(RegistrationConstants.DOCUMNET_SCAN_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Converting byte array to image");

		if (byteArray.length > Integer.parseInt(documentSize)) {
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.SCAN_DOC_SIZE.replace("1", documentSize));
		} else {
			if (selectedDocument != null) {

				scanPopUpViewController.getScanImage().setImage(convertBytesToImage(byteArray));

				LOGGER.info(RegistrationConstants.DOCUMNET_SCAN_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
						RegistrationConstants.APPLICATION_ID, "Adding documents to Screen");

				DocumentDetailsDTO documentDetailsDTO = new DocumentDetailsDTO();

				getDocumentsMapFromSession().put(selectedDocument, documentDetailsDTO);
				attachDocuments(documentDetailsDTO, selectedComboBox.getValue(), selectedDocVBox, byteArray, true);

				popupStage.close();

				LOGGER.info(RegistrationConstants.DOCUMNET_SCAN_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
						RegistrationConstants.APPLICATION_ID, "Documents added successfully");
			}
		}
	}

	/**
	 * This method is to scan from the scanner
	 */
	private void scanFromScanner() throws IOException {

		/* setting the scanner factory */
		if (!documentScanFacade.setScannerFactory()) {
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.SCAN_DOCUMENT_CONNECTION_ERR);
			return;
		}
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

	/**
	 * This method is to attach the document to the screen
	 */
	public void attachScannedDocument(Stage popupStage) throws IOException {

		LOGGER.info(RegistrationConstants.DOCUMNET_SCAN_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Converting byte array to image");
		String documentSize = getValueFromApplicationContext(RegistrationConstants.DOC_SIZE);
		
		if (scannedPages == null || scannedPages.isEmpty()) {
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.SCAN_DOCUMENT_EMPTY);
			return;
		}
		byte[] byteArray;
		if (!"pdf".equalsIgnoreCase(getValueFromApplicationContext(RegistrationConstants.DOC_TYPE))) {
			byteArray = documentScanFacade.asImage(scannedPages);
		} else {
			byteArray = pdfGenerator.asPDF(scannedPages);
		}
		if (byteArray == null) {
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.SCAN_DOCUMENT_CONVERTION_ERR);
			return;
		}

		if (byteArray.length > Integer.parseInt(documentSize)) {
			scannedPages.clear();
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.SCAN_DOC_SIZE.replace("1", documentSize));
		} else {
			if (selectedDocument != null) {
				LOGGER.info(RegistrationConstants.DOCUMNET_SCAN_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
						RegistrationConstants.APPLICATION_ID, "Adding documents to Screen");

				DocumentDetailsDTO documentDetailsDTO = new DocumentDetailsDTO();

				getDocumentsMapFromSession().put(selectedDocument, documentDetailsDTO);
				attachDocuments(documentDetailsDTO, selectedComboBox.getValue(), selectedDocVBox, byteArray, false);

				scannedPages.clear();
				popupStage.close();

				LOGGER.info(RegistrationConstants.DOCUMNET_SCAN_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
						RegistrationConstants.APPLICATION_ID, "Documents added successfully");
			}
		}
	}

	/**
	 * This method will add Hyperlink and Image for scanned documents
	 */
	private void attachDocuments(DocumentDetailsDTO documentDetailsDTO, DocumentCategoryDto document, VBox vboxElement,
			byte[] byteArray, boolean isStubbed) {

		LOGGER.info(RegistrationConstants.DOCUMNET_SCAN_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Attaching documemnts to Pane");

		documentDetailsDTO.setDocument(byteArray);
		documentDetailsDTO.setType(document.getName());

		String docType = getValueFromApplicationContext(RegistrationConstants.DOC_TYPE);
		if (isStubbed) {
			docType = RegistrationConstants.SCANNER_IMG_TYPE;
		}

		documentDetailsDTO.setFormat(docType);
		documentDetailsDTO
				.setValue(selectedDocument.concat(RegistrationConstants.UNDER_SCORE).concat(document.getName()));
		LOGGER.info(RegistrationConstants.DOCUMNET_SCAN_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Set details to DocumentDetailsDTO");

		LOGGER.info(RegistrationConstants.DOCUMNET_SCAN_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Set DocumentDetailsDTO to RegistrationDTO");
		addDocumentsToScreen(documentDetailsDTO.getValue(), documentDetailsDTO.getFormat(), vboxElement);

		validateDocumentsPane();

		generateAlert(RegistrationConstants.ALERT_INFORMATION, RegistrationUIConstants.SCAN_DOC_SUCCESS);
	}

	/**
	 * This method will add document to the screen
	 */
	private void addDocumentsToScreen(String document, String documentFormat, VBox vboxElement) {

		GridPane gridPane = new GridPane();
		gridPane.setId(document);
		gridPane.setVgap(20);
		gridPane.setHgap(20);
		gridPane.add(createHyperLink(document.concat(RegistrationConstants.DOT + documentFormat)), 1, 0);
		gridPane.add(createImageView(vboxElement), 2, 0);

		vboxElement.getChildren().add(gridPane);

		((ImageView) ((VBox) (((HBox) vboxElement.getParent()).getChildren().get(0))).getChildren().get(1))
				.setImage(new Image(this.getClass().getResourceAsStream(RegistrationConstants.DONE_IMAGE_PATH), 15, 15,
						true, true));

		LOGGER.info(RegistrationConstants.DOCUMNET_SCAN_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Scan document added to Vbox element");

	}

	/**
	 * This method will display the scanned document
	 */
	private void displayDocument(byte[] document, String documentName) {

		/*
		 * TODO - pdf to images to be replaced wit ketrnal and setscanner factory has to
		 * be removed here
		 */
		documentScanFacade.setScannerFactory();
		LOGGER.info(RegistrationConstants.DOCUMNET_SCAN_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Converting bytes to Image to display scanned document");
		/* clearing the previously loaded pdf pages inorder to clear up the memory */
		initializePreviewSection();
		if (RegistrationConstants.PDF
				.equalsIgnoreCase(documentName.substring(documentName.lastIndexOf(RegistrationConstants.DOT) + 1))) {
			try {
				docPages = documentScanFacade.pdfToImages(document);
				if (!docPages.isEmpty()) {
					docPreviewImgView.setImage(SwingFXUtils.toFXImage(docPages.get(0), null));

					docPreviewLabel.setVisible(true);
					if (docPages.size() > 1) {
						docPageNumber.setText(RegistrationConstants.ONE);
						docPreviewNext.setVisible(true);
						docPreviewPrev.setVisible(true);
						docPreviewNext.setDisable(false);
					}
				}
			} catch (IOException ioException) {
				LOGGER.error("DOCUMENT_SCAN_CONTROLLER", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
						ioException.getMessage() + ExceptionUtils.getStackTrace(ioException));
				generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.PREVIEW_DOC);
				return;
			}
		} else {
			docPreviewLabel.setVisible(true);
			docPreviewImgView.setImage(convertBytesToImage(document));
		}
		LOGGER.info(RegistrationConstants.DOCUMNET_SCAN_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Scanned document displayed succesfully");
	}

	/**
	 * This method will preview the next document
	 */
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

	/**
	 * This method will preview the previous document
	 */
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

	/**
	 * This method will determine if the document is empty
	 */
	private boolean isDocsNotEmpty() {
		return StringUtils.isNotEmpty(docPageNumber.getText()) && docPages != null && !docPages.isEmpty();
	}

	/**
	 * This method will set the inde and page number for the document
	 * 
	 * @param index
	 *            - index of the preview section
	 * @param pageNumber
	 *            - page number for the preview section
	 */
	private void setDocPreview(int index, int pageNumber) {
		docPreviewImgView.setImage(SwingFXUtils.toFXImage(docPages.get(index), null));
		docPageNumber.setText(String.valueOf(pageNumber));
	}

	/**
	 * This method will create Image to delete scanned document
	 * 
	 * @param field
	 *            the {@link VBox}
	 */
	private ImageView createImageView(VBox vboxElement) {

		LOGGER.info(RegistrationConstants.DOCUMNET_SCAN_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Binding OnAction event for Image to delete the attached document");

		Image image = new Image(this.getClass().getResourceAsStream(RegistrationConstants.CLOSE_IMAGE_PATH), 15, 15,
				true, true);
		ImageView imageView = new ImageView(image);
		imageView.setCursor(Cursor.HAND);

		LOGGER.info(RegistrationConstants.DOCUMNET_SCAN_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Creating Image to delete the attached document");

		imageView.setOnMouseClicked((event) -> {
			auditFactory.audit(
					AuditEvent.valueOf(String.format("REG_DOC_%S_DELETE",
							((ImageView) event.getSource()).getParent().getParent().getId())),
					Components.REG_DOCUMENTS, SessionContext.userId(),
					AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

			HBox hbox = (HBox) vboxElement.getParent();
			ComboBox<String> comboBox = (ComboBox) hbox.getChildren().get(1);
			comboBox.getSelectionModel().clearSelection();
			(((VBox) hbox.getParent()).lookup(RegistrationConstants.HASH + hbox.getId() + RegistrationConstants.LABEL))
					.setVisible(false);

			((ImageView) ((VBox) ((hbox).getChildren().get(0))).getChildren().get(1)).setImage(new Image(
					this.getClass().getResourceAsStream(RegistrationConstants.CLOSE_IMAGE_PATH), 15, 15, true, true));

			initializePreviewSection();

			GridPane gridpane = (GridPane) ((ImageView) event.getSource()).getParent();
			String key = ((VBox) gridpane.getParent()).getId();
			getDocumentsMapFromSession().remove(key);

			ObservableList<Node> nodes = ((HBox) vboxElement.getParent()).getChildren();
			for (Node node : nodes) {
				if (node instanceof ComboBox<?>) {
					ComboBox<?> document = (ComboBox<?>) node;
					document.setValue(null);
				}
			}

			vboxElement.getChildren().remove(gridpane);

			validateDocumentsPane();
		});

		LOGGER.info(RegistrationConstants.DOCUMNET_SCAN_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Image added to delete the attached document");

		return imageView;
	}

	/**
	 * This method will create Hyperlink to view scanned document
	 * 
	 * @param field
	 *            the {@link String}
	 */
	private Hyperlink createHyperLink(String document) {

		LOGGER.info(RegistrationConstants.DOCUMNET_SCAN_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Creating Hyperlink to display Scanned document");

		Hyperlink hyperLink = new Hyperlink();
		hyperLink.setId(document);
		// hyperLink.setGraphic(new ImageView(new
		// Image(this.getClass().getResourceAsStream(RegistrationConstants.VIEW))));
		hyperLink.getStyleClass().add(RegistrationConstants.DOCUMENT_VIEW_ICON);
		hyperLink.setTooltip(new Tooltip(RegistrationConstants.EYETOOLTIP));
		LOGGER.info(RegistrationConstants.DOCUMNET_SCAN_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID,
				"Binding OnAction event to Hyperlink to display Scanned document");

		hyperLink.setOnAction((actionEvent) -> {

			GridPane pane = (GridPane) ((Hyperlink) actionEvent.getSource()).getParent();
			String documentKey = ((VBox) pane.getParent()).getId();

			auditFactory.audit(AuditEvent.valueOf(String.format("REG_DOC_%S_VIEW", documentKey)),
					Components.REG_DOCUMENTS, SessionContext.userId(),
					AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

			DocumentDetailsDTO selectedDocumentToDisplay = getDocumentsMapFromSession().get(documentKey);

			if (selectedDocumentToDisplay != null) {
				displayDocument(selectedDocumentToDisplay.getDocument(), selectedDocumentToDisplay.getValue()
						+ RegistrationConstants.DOT + selectedDocumentToDisplay.getFormat());
			}
		});

		LOGGER.info(RegistrationConstants.DOCUMNET_SCAN_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Hyperlink added to display Scanned document");

		return hyperLink;
	}

	/**
	 * This method will prepare the edit page content
	 */
	protected void prepareEditPageContent() {

		if (getRegistrationDTOFromSession().getDemographicDTO() != null) {

			FXUtils fxUtils = FXUtils.getInstance();

			if (documentComboBoxes != null && !documentComboBoxes.isEmpty()) {

				Map<String, DocumentDetailsDTO> documentsMap = getDocumentsMapFromSession();
				for (String docCategoryKey : documentsMap.keySet()) {

					addDocumentsToScreen(documentsMap.get(docCategoryKey).getValue(),
							documentsMap.get(docCategoryKey).getFormat(), documentVBoxes.get(docCategoryKey));
					fxUtils.selectComboBoxValue(documentComboBoxes.get(docCategoryKey),
							documentsMap.get(docCategoryKey).getValue());
				}

			}

		}

	}

	/**
	 * This method will clear the document section
	 */
	public void clearDocSection() {
		clearAllDocs();
		initializePreviewSection();
	}

	/**
	 * This method will clear for all the documents
	 */
	private void clearAllDocs() {

		for (String docCategoryKey : documentVBoxes.keySet()) {

			documentVBoxes.get(docCategoryKey).getChildren().clear();
		}

	}

	/**
	 * This method will intialize the preview section
	 */
	public void initializePreviewSection() {

		docPreviewLabel.setVisible(false);
		docPreviewNext.setVisible(false);
		docPreviewPrev.setVisible(false);

		docPreviewNext.setDisable(true);
		docPreviewPrev.setDisable(true);
		docPageNumber.setText(RegistrationConstants.EMPTY);
		docPreviewImgView.setImage(null);
		docPages = null;
	}

	/**
	 * Toggle functionality for biometric exception
	 */
	@SuppressWarnings("unchecked")
	private void toggleFunctionForBiometricException() {
		try {
			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Entering into toggle function for Biometric exception");

			Map<String, Map<String, Boolean>> detailMap = (Map<String, Map<String, Boolean>>) applicationContext
					.getApplicationMap().get(RegistrationConstants.REGISTRATION_MAP);

			if (!detailMap.get(RegistrationConstants.DOCUMENT_SCAN).get(RegistrationConstants.DOCUMENT_PANE)) {

				documentPane.setVisible(false);
			}
			if (!detailMap.get(RegistrationConstants.IRIS_CAPTURE).get(RegistrationConstants.VISIBILITY) && !detailMap
					.get(RegistrationConstants.FINGERPRINT_CAPTURE).get(RegistrationConstants.VISIBILITY)) {
				exceptionPane.setVisible(false);
			}

			if (SessionContext.userMap().get(RegistrationConstants.TOGGLE_BIO_METRIC_EXCEPTION) == null) {

				toggleBiometricException = false;
				SessionContext.userMap().put(RegistrationConstants.TOGGLE_BIO_METRIC_EXCEPTION,
						toggleBiometricException);

			} else {
				toggleBiometricException = (boolean) SessionContext.userMap()
						.get(RegistrationConstants.TOGGLE_BIO_METRIC_EXCEPTION);
			}

			if (toggleBiometricException) {
				updatePageFlow(RegistrationConstants.BIOMETRIC_EXCEPTION, true);
				bioExceptionToggleLabel1.setLayoutX(30);
			} else {
				updatePageFlow(RegistrationConstants.BIOMETRIC_EXCEPTION, false);
				bioExceptionToggleLabel1.setLayoutX(0);
			}

			switchedOnForBiometricException.addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) {
					clearAllValues();
					if (newValue) {
						bioExceptionToggleLabel1.setLayoutX(30);
						toggleBiometricException = true;
						updatePageFlow(RegistrationConstants.BIOMETRIC_EXCEPTION, true);
						biometricExceptionController.fingerException();
						biometricExceptionController.clearIrisException();
					} else {
						bioExceptionToggleLabel1.setLayoutX(0);

						toggleBiometricException = false;
						faceCaptureController.clearExceptionImage();
						updatePageFlow(RegistrationConstants.BIOMETRIC_EXCEPTION, false);
						if ((boolean) SessionContext.map().get(RegistrationConstants.IS_Child)) {
							updatePageFlow(RegistrationConstants.GUARDIAN_BIOMETRIC, true);
						}
					}
					SessionContext.userMap().put(RegistrationConstants.TOGGLE_BIO_METRIC_EXCEPTION,
							toggleBiometricException);
				}
			});
			bioExceptionToggleLabel1.setOnMouseClicked((event) -> {
				switchedOnForBiometricException.set(!switchedOnForBiometricException.get());
			});
			bioExceptionToggleLabel2.setOnMouseClicked((event) -> {
				switchedOnForBiometricException.set(!switchedOnForBiometricException.get());
			});
			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Exiting the toggle function for Biometric exception");
		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - TOGGLING FOR BIOMETRIC EXCEPTION SWITCH FAILED ", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));
		}
	}

	/**
	 * This method is to go to previous page
	 */
	@FXML
	private void back() {
		auditFactory.audit(AuditEvent.REG_DOC_BACK, Components.REG_DOCUMENTS, SessionContext.userId(),
				AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

		registrationController.showCurrentPage(RegistrationConstants.DOCUMENT_SCAN,
				getPageDetails(RegistrationConstants.DOCUMENT_SCAN, RegistrationConstants.PREVIOUS));
	}

	/**
	 * This method is to go to next page
	 */
	@FXML
	private void next() {
		auditFactory.audit(AuditEvent.REG_DOC_NEXT, Components.REG_DOCUMENTS, SessionContext.userId(),
				AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

		biometricExceptionController.disableNextBtn();
		fingerPrintCaptureController.clearImage();
		irisCaptureController.clearIrisBasedOnExceptions();
		guardianBiometricsController.manageBiometricsListBasedOnExceptions();
		if (getRegistrationDTOFromSession().getSelectionListDTO() != null) {
			if (registrationController.validateDemographicPane(documentScanPane)) {
				SessionContext.map().put(RegistrationConstants.UIN_UPDATE_DOCUMENTSCAN, false);
				updateUINMethodFlow();
				demographicDetailController.saveDetail();
				registrationController.showUINUpdateCurrentPage();
			}
		} else {
			if (RegistrationConstants.ENABLE
					.equalsIgnoreCase(getValueFromApplicationContext(RegistrationConstants.DOC_DISABLE_FLAG))) {
				if (registrationController.validateDemographicPane(documentScanPane)) {
					registrationController.showCurrentPage(RegistrationConstants.DOCUMENT_SCAN,
							getPageDetails(RegistrationConstants.DOCUMENT_SCAN, RegistrationConstants.NEXT));
				}
			} else {
				registrationController.showCurrentPage(RegistrationConstants.DOCUMENT_SCAN,
						getPageDetails(RegistrationConstants.DOCUMENT_SCAN, RegistrationConstants.NEXT));

			}
		}

	}

	private void validateDocumentsPane() {
		if(RegistrationConstants.DISABLE.equalsIgnoreCase(
				String.valueOf(ApplicationContext.map().get(RegistrationConstants.DOC_DISABLE_FLAG)))) {
			continueBtn.setDisable(false);
		} else {
			if (registrationController.validateDemographicPane(documentScanPane)) {
				continueBtn.setDisable(false);
			} else {
				continueBtn.setDisable(true);
			}
		}
	}

	public List<BufferedImage> getScannedPages() {
		return scannedPages;
	}

	public void setScannedPages(List<BufferedImage> scannedPages) {
		this.scannedPages = scannedPages;
	}

	/**
	 * Sets the value of the biometric exception required based on the individual
	 * whose biometric exceptions has to be captured. If exception of Parent or
	 * Guardian is required, text will be displayed as Parent Or guardian biometrics
	 * exception required. While for Individual, text will be displayed as Biometric
	 * exception required.
	 * 
	 * @param isParentOrGuardianBiometricsCaptured
	 *            boolean value indicating whose biometric exception has to be
	 *            captured either individual or parent/ guardian
	 */
	public void setExceptionDescriptionText(boolean isParentOrGuardianBiometricsCaptured) {
		ResourceBundle applicationLanguage = ApplicationContext.applicationLanguageBundle();

		String exceptionFaceDescription = applicationLanguage.getString("biometricexceptionrequired");

		if (isParentOrGuardianBiometricsCaptured) {
			exceptionFaceDescription = applicationLanguage.getString("parentOrGuardian").concat(" ")
					.concat(exceptionFaceDescription.toLowerCase());
		}

		biometricExceptionReq.setText(exceptionFaceDescription);
	}
}
