package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.applicanttype.exception.InvalidApplicantArgumentException;
import io.mosip.kernel.core.applicanttype.spi.ApplicantType;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
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
import io.mosip.registration.controller.device.ScanPopUpViewController;
import io.mosip.registration.dto.demographic.DocumentDetailsDTO;
import io.mosip.registration.dto.demographic.MoroccoIdentity;
import io.mosip.registration.dto.mastersync.DocumentCategoryDto;
import io.mosip.registration.entity.DocumentCategory;
import io.mosip.registration.service.impl.DocumentCategoryService;
import io.mosip.registration.service.impl.ValidDocumentService;
import io.mosip.registration.util.scan.DocumentScanFacade;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.StringConverter;

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
	private DemographicDetailController demographicDetailController;

	@FXML
	protected GridPane documentScan;

	@FXML
	private GridPane documentPane;

	@FXML
	private GridPane exceptionPane;

	@FXML
	protected ImageView docPreviewImgView;

	@FXML
	protected Button docPreviewNext;

	@FXML
	protected Button docPreviewPrev;

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
	private ValidDocumentService validDocumentService;

	@Autowired
	private DocumentCategoryService documentCategoryService;

	@Autowired
	private BiometricExceptionController biometricExceptionController;

	@Value("${DOCUMENT_SCANNER_ENABLED}")
	private String isScannerEnabled;

	private List<BufferedImage> docPages;

	@Autowired
	private ApplicantType applicantTypeService;

	@FXML
	private Label registrationNavlabel;

	@FXML
	private Button continueBtn;
	@FXML
	private Button backBtn;

	@FXML
	private void initialize() {
		LOGGER.info(RegistrationConstants.DOCUMNET_SCAN_CONTROLLER, APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Entering the LOGIN_CONTROLLER");
		try {
			if (getRegistrationDTOFromSession() != null
					&& getRegistrationDTOFromSession().getSelectionListDTO() != null) {
				registrationNavlabel.setText(RegistrationConstants.UIN_NAV_LABEL);
			}
			switchedOnForBiometricException = new SimpleBooleanProperty(false);
			toggleFunctionForBiometricException();

			// populateDocumentCategories();
		} catch (RuntimeException exception) {
			LOGGER.error("REGISTRATION - CONTROLLER", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					exception.getMessage() + ExceptionUtils.getStackTrace(exception));
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UNABLE_LOAD_REG_PAGE);
		}
	}

	protected <T> void populateDocumentCategories() throws InvalidApplicantArgumentException, ParseException {

		/* clearing all the previously added fields */
		docScanVbox.getChildren().clear();
		documentComboBoxes.clear();
		documentVBoxes.clear();
		initializePreviewSection();

		MoroccoIdentity identityDto = getIdentityDto();
		String applicantType = null;
		String gender = null;
		if (demographicDetailController.getSelectedGenderCode() != null) {
			gender = demographicDetailController.getSelectedGenderCode();
		}
		String dateOfBirth = identityDto.getDateOfBirth();
		String individualType = null;
		if (demographicDetailController.getSelectedNationalityCode() != null) {
			individualType = demographicDetailController.getSelectedNationalityCode();
		}
		if (gender != null && dateOfBirth != null && individualType != null) {
			SimpleDateFormat inputFormat = new SimpleDateFormat(RegistrationConstants.ATTR_FORINGER_DOB_PARSING);
			SimpleDateFormat outputFormat = new SimpleDateFormat(RegistrationConstants.ATTR_FORINGER_DOB_FORMAT);
			Date date = inputFormat.parse(dateOfBirth);
			String formattedDob = outputFormat.format(date);
			Map<String, Object> applicantTypeMap = new HashMap<>();
			applicantTypeMap.put(RegistrationConstants.ATTR_INDIVIDUAL_TYPE, individualType);
			applicantTypeMap.put(RegistrationConstants.ATTR_DATE_OF_BIRTH, formattedDob);
			applicantTypeMap.put(RegistrationConstants.ATTR_GENDER_TYPE, gender);
			applicantType = applicantTypeService.getApplicantType(applicantTypeMap);
			getRegistrationDTOFromSession().getRegistrationMetaDataDTO().setApplicantTypeCode(applicantType);
		} else {
			/* TODO - to be removed after the clarification of UIN update */
			applicantType = "007";
			getRegistrationDTOFromSession().getRegistrationMetaDataDTO().setApplicantTypeCode(null);
		}

		if (applicantType != null) {
			List<DocumentCategory> documentCategories = documentCategoryService
					.getDocumentCategoriesByLangCode(ApplicationContext.applicationLanguage());
			docScanVbox.setSpacing(25);
			if (documentCategories != null && documentCategories.size() > 0)
				prepareDocumentScanSection(applicantType, documentCategories);
		}

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
							documentDetailsDTO.getValue().substring(documentDetailsDTO.getValue().indexOf("_") + 1));
				}
			}
		} else if (documentVBoxes.isEmpty() && documentsMap != null) {
			documentsMap.clear();
		}
	}

	private Map<String, DocumentDetailsDTO> getDocumentsMapFromSession() {
		return getRegistrationDTOFromSession().getDemographicDTO().getApplicantDocumentDTO().getDocuments();
	}

	@SuppressWarnings("unchecked")
	private <T> void prepareDocumentScanSection(String applicantType, List<DocumentCategory> documentCategories) {
		for (DocumentCategory documentCategory : documentCategories) {

			String docCategoryCode = documentCategory.getCode();

			List<DocumentCategoryDto> documentCategoryDtos = null;

			try {
				documentCategoryDtos = validDocumentService.getDocumentCategories(applicantType, docCategoryCode,
						ApplicationContext.applicationLanguage());
			} catch (RuntimeException runtimeException) {
				LOGGER.error("REGISTRATION - LOADING LIST OF DOCUMENTS FAILED ", APPLICATION_NAME,
						RegistrationConstants.APPLICATION_ID,
						runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));
			}

			if (documentCategoryDtos != null && !documentCategoryDtos.isEmpty()) {
				HBox hBox = new HBox();

				ComboBox<DocumentCategoryDto> comboBox = new ComboBox<>();
				comboBox.setId(docCategoryCode);
				comboBox.setPromptText(docCategoryCode);
				comboBox.getStyleClass().add("documentCombobox");
				StringConverter<T> uiRenderForComboBox = FXUtils.getInstance().getStringConverterForComboBox();
				comboBox.setConverter((StringConverter<DocumentCategoryDto>) uiRenderForComboBox);
				comboBox.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

				/*
				 * adding all the dynamically created combo boxes in a map inorder to show it in
				 * the edit page
				 */
				documentComboBoxes.put(docCategoryCode, comboBox);

				VBox documentVBox = new VBox();
				documentVBox.getStyleClass().add("scanVBox");
				documentVBox.setId(docCategoryCode);

				documentVBoxes.put(docCategoryCode, documentVBox);

				Button scanButton = new Button();
				scanButton.setText(RegistrationUIConstants.SCAN);
				scanButton.setId(docCategoryCode);
				scanButton.getStyleClass().add("documentContentButton");
				scanButton.setGraphic(new ImageView(new Image(
						this.getClass().getResourceAsStream(RegistrationConstants.SCAN), 12, 12, true, true)));
				scanButton.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {

						auditFactory.audit(AuditEvent.REG_DOC_POA_SCAN, Components.REG_DOCUMENTS,
								SessionContext.userId(), AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

						Button clickedBtn = (Button) event.getSource();
						clickedBtn.getId();
						scanDocument(comboBox, documentVBox, documentCategory.getCode(),
								RegistrationUIConstants.PLEASE_SELECT + " " + documentCategory.getCode() + " "
										+ RegistrationUIConstants.DOCUMENT);
					}
				});

				hBox.getChildren().addAll(comboBox, documentVBox, scanButton);
				docScanVbox.getChildren().add(hBox);

				comboBox.getItems().addAll(documentCategoryDtos);
			}

		}
	}

	private String findApplicantType(String gender, Integer age, String individualType) {
		String applicantType = null;
		String male = "MLE";
		String female = "FLE";
		if ("National".equalsIgnoreCase(individualType)) {

			if (male.equalsIgnoreCase(gender)) {
				if (isChild(age)) {
					applicantType = "005";
				} else {
					applicantType = "006";
				}

			} else if (female.equalsIgnoreCase(gender)) {
				if (isChild(age)) {
					applicantType = "008";
				} else {
					applicantType = "007";
				}
			}
		} else {

			if (male.equalsIgnoreCase(gender)) {
				if (isChild(age)) {
					applicantType = "001";
				} else {
					applicantType = "002";
				}

			} else if (female.equalsIgnoreCase(gender)) {
				if (isChild(age)) {
					applicantType = "003";
				} else {
					applicantType = "004";
				}
			}

		}
		return applicantType;
	}

	private boolean isChild(Integer age) {
		return age <= Integer.valueOf(String.valueOf(ApplicationContext.map().get(RegistrationConstants.MIN_AGE)));
	}

	private MoroccoIdentity getIdentityDto() {
		return (MoroccoIdentity) getRegistrationDTOFromSession().getDemographicDTO().getDemographicInfoDTO()
				.getIdentity();
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
					RegistrationConstants.APPLICATION_ID, "One Document can be added to the Category");

			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.SCAN_DOC_CATEGORY_MULTIPLE);
		} else if (!vboxElement.getChildren().isEmpty() && vboxElement.getChildren().stream()
				.noneMatch(index -> index.getId().contains(documents.getValue().getName()))) {
			LOGGER.info(RegistrationConstants.DOCUMNET_SCAN_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Select only one document category for scan");

			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.SCAN_DOC_CATEGORY_MULTIPLE);
		} else {
			LOGGER.info(RegistrationConstants.DOCUMNET_SCAN_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Displaying Scan window to scan Documents");

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
		if ("yes".equalsIgnoreCase(isScannerEnabled)) {
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
			if ("yes".equalsIgnoreCase(isScannerEnabled)) {
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

	private void scanFromStubbed(Stage popupStage) throws IOException {
		byte[] byteArray = documentScanFacade.getScannedDocument();

		LOGGER.info(RegistrationConstants.DOCUMNET_SCAN_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Converting byte array to image");

		if (byteArray.length > Integer
				.parseInt(String.valueOf(ApplicationContext.map().get(RegistrationConstants.DOC_SIZE)))) {
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.SCAN_DOC_SIZE);
		} else {
			if (selectedDocument != null) {

				scanPopUpViewController.getScanImage().setImage(convertBytesToImage(byteArray));

				LOGGER.info(RegistrationConstants.DOCUMNET_SCAN_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
						RegistrationConstants.APPLICATION_ID, "Adding documents to Screen");

				DocumentDetailsDTO documentDetailsDTO = new DocumentDetailsDTO();

				getDocumentsMapFromSession().put(selectedDocument, documentDetailsDTO);
				attachDocuments(documentDetailsDTO, selectedComboBox.getValue(), selectedDocVBox, byteArray);

				popupStage.close();

				LOGGER.info(RegistrationConstants.DOCUMNET_SCAN_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
						RegistrationConstants.APPLICATION_ID, "Documents added successfully");
			}
		}
	}

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

	public void attachScannedDocument(Stage popupStage) throws IOException {

		LOGGER.info(RegistrationConstants.DOCUMNET_SCAN_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Converting byte array to image");
		if (scannedPages == null || scannedPages.isEmpty()) {
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.SCAN_DOCUMENT_EMPTY);
			return;
		}
		byte[] byteArray;
		if (!"pdf".equalsIgnoreCase(String.valueOf(ApplicationContext.map().get(RegistrationConstants.DOC_TYPE)))) {
			byteArray = documentScanFacade.asImage(scannedPages);
		} else {
			byteArray = documentScanFacade.asPDF(scannedPages);
		}
		if (byteArray == null) {
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.SCAN_DOCUMENT_CONVERTION_ERR);
			return;
		}
		if (byteArray.length > Integer
				.parseInt(String.valueOf(ApplicationContext.map().get(RegistrationConstants.DOC_SIZE)))) {
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.SCAN_DOC_SIZE);
		} else {
			if (selectedDocument != null) {
				LOGGER.info(RegistrationConstants.DOCUMNET_SCAN_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
						RegistrationConstants.APPLICATION_ID, "Adding documents to Screen");

				DocumentDetailsDTO documentDetailsDTO = new DocumentDetailsDTO();

				getDocumentsMapFromSession().put(selectedDocument, documentDetailsDTO);
				attachDocuments(documentDetailsDTO, selectedComboBox.getValue(), selectedDocVBox, byteArray);

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
			byte[] byteArray) {

		LOGGER.info(RegistrationConstants.DOCUMNET_SCAN_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Attaching documemnts to Pane");

		documentDetailsDTO.setDocument(byteArray);
		documentDetailsDTO.setType(document.getName());
		documentDetailsDTO.setFormat(String.valueOf(ApplicationContext.map().get(RegistrationConstants.DOC_TYPE)));
		documentDetailsDTO.setValue(selectedDocument.concat("_").concat(document.getName()));

		LOGGER.info(RegistrationConstants.DOCUMNET_SCAN_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Set details to DocumentDetailsDTO");

		LOGGER.info(RegistrationConstants.DOCUMNET_SCAN_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Set DocumentDetailsDTO to RegistrationDTO");

		addDocumentsToScreen(documentDetailsDTO.getValue(), documentDetailsDTO.getFormat(), vboxElement);

		generateAlert(RegistrationConstants.ALERT_INFORMATION, RegistrationUIConstants.SCAN_DOC_SUCCESS);

		LOGGER.info(RegistrationConstants.DOCUMNET_SCAN_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Setting scrollbar policy for scrollpane");

	}

	private void addDocumentsToScreen(String document, String documentFormat, VBox vboxElement) {

		GridPane gridPane = new GridPane();
		gridPane.setId(document);
		gridPane.add(new Label("     "), 0, vboxElement.getChildren().size());
		gridPane.add(createHyperLink(document.concat("." + documentFormat)), 1, vboxElement.getChildren().size());
		gridPane.add(new Label("  "), 2, vboxElement.getChildren().size());
		gridPane.add(createImageView(vboxElement), 3, vboxElement.getChildren().size());

		vboxElement.getChildren().add(gridPane);

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
		if ("pdf".equalsIgnoreCase(documentName.substring(documentName.lastIndexOf(".") + 1))) {
			try {
				docPages = documentScanFacade.pdfToImages(document);
				if (!docPages.isEmpty()) {
					docPreviewImgView.setImage(SwingFXUtils.toFXImage(docPages.get(0), null));

					docPreviewLabel.setVisible(true);
					if (docPages.size() > 1) {
						docPageNumber.setText("1");
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
		docPreviewImgView.setImage(SwingFXUtils.toFXImage(docPages.get(index), null));
		docPageNumber.setText(String.valueOf(pageNumber));
	}

	/**
	 * This method will create Image to delete scanned document
	 */
	private ImageView createImageView(VBox vboxElement) {

		LOGGER.info(RegistrationConstants.DOCUMNET_SCAN_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Binding OnAction event Image to delete the attached document");

		Image image = new Image(this.getClass().getResourceAsStream(RegistrationConstants.CLOSE_IMAGE_PATH), 15, 15,
				true, true);
		ImageView imageView = new ImageView(image);
		imageView.setCursor(Cursor.HAND);

		LOGGER.info(RegistrationConstants.DOCUMNET_SCAN_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Creating Image to delete the attached document");

		imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {

				auditFactory.audit(AuditEvent.REG_DOC_POA_DELETE, Components.REG_DOCUMENTS, SessionContext.userId(),
						AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

				initializePreviewSection();

				GridPane gridpane = (GridPane) ((ImageView) event.getSource()).getParent();
				String key = ((VBox) gridpane.getParent()).getId();
				getDocumentsMapFromSession().remove(key);

				vboxElement.getChildren().remove(gridpane);
			}

		});

		LOGGER.info(RegistrationConstants.DOCUMNET_SCAN_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Image added to delete the attached document");

		return imageView;
	}

	/**
	 * This method will create Hyperlink to view scanned document
	 */
	private Hyperlink createHyperLink(String document) {

		LOGGER.info(RegistrationConstants.DOCUMNET_SCAN_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Creating Hyperlink to display Scanned document");

		Hyperlink hyperLink = new Hyperlink();
		hyperLink.setId(document);
		hyperLink.setGraphic(new ImageView(new Image(this.getClass().getResourceAsStream(RegistrationConstants.VIEW))));

		LOGGER.info(RegistrationConstants.DOCUMNET_SCAN_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID,
				"Binding OnAction event to Hyperlink to display Scanned document");

		hyperLink.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {

				auditFactory.audit(AuditEvent.REG_DOC_POA_VIEW, Components.REG_DOCUMENTS, SessionContext.userId(),
						AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

				GridPane pane = (GridPane) ((Hyperlink) actionEvent.getSource()).getParent();

				String documentKey = ((VBox) pane.getParent()).getId();
				DocumentDetailsDTO selectedDocumentToDisplay = getDocumentsMapFromSession().get(documentKey);

				if (selectedDocumentToDisplay != null) {
					displayDocument(selectedDocumentToDisplay.getDocument(),
							selectedDocumentToDisplay.getValue() + "." + selectedDocumentToDisplay.getFormat());
				}

			}
		});

		LOGGER.info(RegistrationConstants.DOCUMNET_SCAN_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Hyperlink added to display Scanned document");

		return hyperLink;
	}

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

	public void clearDocSection() {
		clearAllDocs();
		initializePreviewSection();
	}

	private void clearAllDocs() {

		for (String docCategoryKey : documentVBoxes.keySet()) {

			documentVBoxes.get(docCategoryKey).getChildren().clear();
		}

	}

	public void initializePreviewSection() {

		docPreviewLabel.setVisible(false);
		docPreviewNext.setVisible(false);
		docPreviewPrev.setVisible(false);

		docPreviewNext.setDisable(true);
		docPreviewPrev.setDisable(true);
		docPageNumber.setText("");
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
			if (!detailMap.get(RegistrationConstants.BIOMETRIC_EXCEPTION).get(RegistrationConstants.VISIBILITY)) {
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
				((Map<String, Map<String, Boolean>>) ApplicationContext.map()
						.get(RegistrationConstants.REGISTRATION_MAP)).get(RegistrationConstants.BIOMETRIC_EXCEPTION)
								.put(RegistrationConstants.VISIBILITY, true);

				bioExceptionToggleLabel1.setLayoutX(30);

			} else {
				((Map<String, Map<String, Boolean>>) ApplicationContext.map()
						.get(RegistrationConstants.REGISTRATION_MAP)).get(RegistrationConstants.BIOMETRIC_EXCEPTION)
								.put(RegistrationConstants.VISIBILITY, false);

				bioExceptionToggleLabel1.setLayoutX(0);
			}

			switchedOnForBiometricException.addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) {
					clearAllValues();
					if (newValue) {
						bioExceptionToggleLabel1.setLayoutX(30);
						toggleBiometricException = true;
						((Map<String, Map<String, Boolean>>) ApplicationContext.map()
								.get(RegistrationConstants.REGISTRATION_MAP))
										.get(RegistrationConstants.BIOMETRIC_EXCEPTION)
										.put(RegistrationConstants.VISIBILITY, true);

					} else {
						bioExceptionToggleLabel1.setLayoutX(0);

						toggleBiometricException = false;
						faceCaptureController.clearExceptionImage();
						((Map<String, Map<String, Boolean>>) ApplicationContext.map()
								.get(RegistrationConstants.REGISTRATION_MAP))
										.get(RegistrationConstants.BIOMETRIC_EXCEPTION)
										.put(RegistrationConstants.VISIBILITY, false);

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

	public void uinUpdate() {
		if (getRegistrationDTOFromSession().getSelectionListDTO().isChild()) {
			bioExceptionToggleLabel1.setDisable(true);
			bioExceptionToggleLabel2.setDisable(true);
		}

		if (getRegistrationDTOFromSession().getSelectionListDTO().isBiometricException()) {
			switchedOnForBiometricException.setValue(true);
			toggleBiometricException = true;
			SessionContext.userMap().put(RegistrationConstants.TOGGLE_BIO_METRIC_EXCEPTION, toggleBiometricException);
		} else {
			switchedOnForBiometricException.setValue(false);
			toggleBiometricException = false;
			SessionContext.userMap().put(RegistrationConstants.TOGGLE_BIO_METRIC_EXCEPTION, toggleBiometricException);
			faceCaptureController.clearExceptionImage();
		}
	}

	@FXML
	private void back() {
		auditFactory.audit(AuditEvent.REG_DOC_BACK, Components.REG_DOCUMENTS, SessionContext.userId(),
				AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

		registrationController.showCurrentPage(RegistrationConstants.DOCUMENT_SCAN,
				getPageDetails(RegistrationConstants.DOCUMENT_SCAN, RegistrationConstants.PREVIOUS));
	}

	@FXML
	private void skip() {

		if (getRegistrationDTOFromSession().getSelectionListDTO() != null) {
			SessionContext.map().put("documentScan", false);
			updateUINMethodFlow();
			registrationController.showUINUpdateCurrentPage();
		} else {
			registrationController.showCurrentPage(RegistrationConstants.DOCUMENT_SCAN,
					getPageDetails(RegistrationConstants.DOCUMENT_SCAN, RegistrationConstants.NEXT));
		}

	}

	@FXML
	private void next() {

		auditFactory.audit(AuditEvent.REG_DOC_NEXT, Components.REG_DOCUMENTS, SessionContext.userId(),
				AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

		biometricExceptionController.disableNextBtn();
		if (getRegistrationDTOFromSession().getSelectionListDTO() != null) {
			if (registrationController.validateDemographicPane(documentScanPane)) {
				SessionContext.map().put("documentScan", false);
				updateUINMethodFlow();

				registrationController.showUINUpdateCurrentPage();

			}
		} else {
			if (RegistrationConstants.ENABLE.equalsIgnoreCase(
					String.valueOf(ApplicationContext.map().get(RegistrationConstants.DOC_DISABLE_FLAG)))) {
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

}
