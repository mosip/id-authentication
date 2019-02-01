package io.mosip.registration.controller;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.HashSet;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.VirtualKeyboardKeys;
import io.mosip.registration.controller.reg.RegistrationController;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.PolygonBuilder;

public class VirtualKeyboard {

	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(RegistrationController.class);

	private static final Set<Integer> KEY_LENGTHS = new HashSet<>();

	private VBox root;

	private boolean capsLock;

	private StringBuilder vkType=new StringBuilder();

	private final ResourceBundle keyboard = ResourceBundle.getBundle("keyboards.keyboard",
			new Locale(AppConfig.getApplicationProperty("local_language")));

	private String getKey(String keyCode) {

		return keyboard.getString(keyCode);
	}

	/**
	 * Creates a Virtual Keyboard.
	 * 
	 * @param target
	 *            The node that will receive KeyEvents from this keyboard. If
	 *            target is null, KeyEvents will be dynamically forwarded to the
	 *            focus owner in the Scene containing this keyboard.
	 */

	private static VirtualKeyboard instance = null;

	public static VirtualKeyboard getInstance() {
		if (instance == null) {
			instance = new VirtualKeyboard();
		}

		return instance;
	}

	private VirtualKeyboard(ReadOnlyObjectProperty<Node> target) {
		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Opening virtual keyboard");

		this.root = new VBox(5);
		root.setPadding(new Insets(10));
		root.setId("virtualKeyboard");
		final Modifiers modifiers = new Modifiers();
		KEY_LENGTHS.add(1);
		KEY_LENGTHS.add(2);

		String[][] unshifted = null;
		try {
			unshifted = new String[][] {
					{ getKey(VirtualKeyboardKeys.unshift_BackQuote), getKey(VirtualKeyboardKeys.unshift_One),
							getKey(VirtualKeyboardKeys.unshift_Two), getKey(VirtualKeyboardKeys.unshift_Three),
							getKey(VirtualKeyboardKeys.unshift_Four), getKey(VirtualKeyboardKeys.unshift_Five),
							getKey(VirtualKeyboardKeys.unshift_Six), getKey(VirtualKeyboardKeys.unshift_Seven),
							getKey(VirtualKeyboardKeys.unshift_Eight), getKey(VirtualKeyboardKeys.unshift_Nine),
							getKey(VirtualKeyboardKeys.unshift_Zero), getKey(VirtualKeyboardKeys.unshift_Minus),
							getKey(VirtualKeyboardKeys.unshift_Equals) },
					{ getKey(VirtualKeyboardKeys.unshift_Q), getKey(VirtualKeyboardKeys.unshift_W),
							getKey(VirtualKeyboardKeys.unshift_E), getKey(VirtualKeyboardKeys.unshift_R),
							getKey(VirtualKeyboardKeys.unshift_T), getKey(VirtualKeyboardKeys.unshift_Y),
							getKey(VirtualKeyboardKeys.unshift_U), getKey(VirtualKeyboardKeys.unshift_I),
							getKey(VirtualKeyboardKeys.unshift_O), getKey(VirtualKeyboardKeys.unshift_P),
							getKey(VirtualKeyboardKeys.unshift_OpenBracket),
							getKey(VirtualKeyboardKeys.unshift_CloseBracket),
							getKey(VirtualKeyboardKeys.unshift_BackSlash) },
					{ getKey(VirtualKeyboardKeys.unshift_A), getKey(VirtualKeyboardKeys.unshift_S),
							getKey(VirtualKeyboardKeys.unshift_D), getKey(VirtualKeyboardKeys.unshift_F),
							getKey(VirtualKeyboardKeys.unshift_G), getKey(VirtualKeyboardKeys.unshift_H),
							getKey(VirtualKeyboardKeys.unshift_J), getKey(VirtualKeyboardKeys.unshift_K),
							getKey(VirtualKeyboardKeys.unshift_L), getKey(VirtualKeyboardKeys.unshift_SemiColon),
							getKey(VirtualKeyboardKeys.unshift_Quote) },
					{ getKey(VirtualKeyboardKeys.unshift_Z), getKey(VirtualKeyboardKeys.unshift_X),
							getKey(VirtualKeyboardKeys.unshift_C), getKey(VirtualKeyboardKeys.unshift_V),
							getKey(VirtualKeyboardKeys.unshift_B), getKey(VirtualKeyboardKeys.unshift_N),
							getKey(VirtualKeyboardKeys.unshift_M), getKey(VirtualKeyboardKeys.unshift_Comma),
							getKey(VirtualKeyboardKeys.unshift_Period), getKey(VirtualKeyboardKeys.unshift_Slash) } };
		} catch (MissingResourceException exception) {
			LOGGER.error("Virtual Keyboard", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					exception.getMessage());
		}
		String[][] shifted = null;
		try {
			shifted = new String[][] {
					{ getKey(VirtualKeyboardKeys.shift_BackQuote), getKey(VirtualKeyboardKeys.shift_One),
							getKey(VirtualKeyboardKeys.shift_Two), getKey(VirtualKeyboardKeys.shift_Three),
							getKey(VirtualKeyboardKeys.shift_Four), getKey(VirtualKeyboardKeys.shift_Five),
							getKey(VirtualKeyboardKeys.shift_Six), getKey(VirtualKeyboardKeys.shift_Seven),
							getKey(VirtualKeyboardKeys.shift_Eight), getKey(VirtualKeyboardKeys.shift_Nine),
							getKey(VirtualKeyboardKeys.shift_Zero), getKey(VirtualKeyboardKeys.shift_Minus),
							getKey(VirtualKeyboardKeys.shift_Equals) },
					{ getKey(VirtualKeyboardKeys.shift_Q), getKey(VirtualKeyboardKeys.shift_W),
							getKey(VirtualKeyboardKeys.shift_E), getKey(VirtualKeyboardKeys.shift_R),
							getKey(VirtualKeyboardKeys.shift_T), getKey(VirtualKeyboardKeys.shift_Y),
							getKey(VirtualKeyboardKeys.shift_U), getKey(VirtualKeyboardKeys.shift_I),
							getKey(VirtualKeyboardKeys.shift_O), getKey(VirtualKeyboardKeys.shift_P),
							getKey(VirtualKeyboardKeys.shift_OpenBracket),
							getKey(VirtualKeyboardKeys.shift_CloseBracket),
							getKey(VirtualKeyboardKeys.shift_BackSlash) },
					{ getKey(VirtualKeyboardKeys.shift_A), getKey(VirtualKeyboardKeys.shift_S),
							getKey(VirtualKeyboardKeys.shift_D), getKey(VirtualKeyboardKeys.shift_F),
							getKey(VirtualKeyboardKeys.shift_G), getKey(VirtualKeyboardKeys.shift_H),
							getKey(VirtualKeyboardKeys.shift_J), getKey(VirtualKeyboardKeys.shift_K),
							getKey(VirtualKeyboardKeys.shift_L), getKey(VirtualKeyboardKeys.shift_SemiColon),
							getKey(VirtualKeyboardKeys.shift_Quote) },
					{ getKey(VirtualKeyboardKeys.shift_Z), getKey(VirtualKeyboardKeys.shift_X),
							getKey(VirtualKeyboardKeys.shift_C), getKey(VirtualKeyboardKeys.shift_V),
							getKey(VirtualKeyboardKeys.shift_B), getKey(VirtualKeyboardKeys.shift_N),
							getKey(VirtualKeyboardKeys.shift_M), getKey(VirtualKeyboardKeys.shift_Comma),
							getKey(VirtualKeyboardKeys.shift_Period), getKey(VirtualKeyboardKeys.shift_Slash) } };
		} catch (MissingResourceException exception) {
			LOGGER.error("Virtual Keyboard", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					exception.getMessage());
		}

		final KeyCode[][] codes = new KeyCode[][] {
				{ KeyCode.BACK_QUOTE, KeyCode.DIGIT1, KeyCode.DIGIT2, KeyCode.DIGIT3, KeyCode.DIGIT4, KeyCode.DIGIT5,
						KeyCode.DIGIT6, KeyCode.DIGIT7, KeyCode.DIGIT8, KeyCode.DIGIT9, KeyCode.DIGIT0,
						KeyCode.SUBTRACT, KeyCode.EQUALS },
				{ KeyCode.Q, KeyCode.W, KeyCode.E, KeyCode.R, KeyCode.T, KeyCode.Y, KeyCode.U, KeyCode.I, KeyCode.O,
						KeyCode.P, KeyCode.OPEN_BRACKET, KeyCode.CLOSE_BRACKET, KeyCode.BACK_SLASH },
				{ KeyCode.A, KeyCode.S, KeyCode.D, KeyCode.F, KeyCode.G, KeyCode.H, KeyCode.J, KeyCode.K, KeyCode.L,
						KeyCode.SEMICOLON, KeyCode.QUOTE },
				{ KeyCode.Z, KeyCode.X, KeyCode.C, KeyCode.V, KeyCode.B, KeyCode.N, KeyCode.M, KeyCode.COMMA,
						KeyCode.PERIOD, KeyCode.SLASH } };

		final Button escape = createNonshiftableButton("Esc", KeyCode.ESCAPE, modifiers, target);
		final Button backspace = createNonshiftableButton("Backspace", KeyCode.BACK_SPACE, modifiers, target);
		final Button delete = createNonshiftableButton("Del", KeyCode.DELETE, modifiers, target);
		final Button enter = createNonshiftableButton("Enter", KeyCode.ENTER, modifiers, target);
		final Button tab = createNonshiftableButton("Tab", KeyCode.TAB, modifiers, target);

		final Button cursorLeft = createCursorKey(KeyCode.LEFT, modifiers, target, 15.0, 5.0, 15.0, 15.0, 5.0, 10.0);
		final Button cursorRight = createCursorKey(KeyCode.RIGHT, modifiers, target, 5.0, 5.0, 5.0, 15.0, 15.0, 10.0);
		final Button cursorUp = createCursorKey(KeyCode.UP, modifiers, target, 10.0, 0.0, 15.0, 10.0, 5.0, 10.0);
		final Button cursorDown = createCursorKey(KeyCode.DOWN, modifiers, target, 10.0, 10.0, 15.0, 0.0, 5.0, 0.0);
		final VBox cursorUpDown = new VBox(2);
		cursorUpDown.getChildren().addAll(cursorUp, cursorDown);

		final Node[][] extraLeftButtons = new Node[][] { { escape }, { tab }, { modifiers.capsLockKey() },
				{ modifiers.shiftKey() } };
		final Node[][] extraRightButtons = new Node[][] { { backspace }, { delete }, { enter },
				{ modifiers.secondShiftKey() } };

		try {
			for (int buttonRow = 0; buttonRow < unshifted.length; buttonRow++) {
				HBox hbox = new HBox(5);
				hbox.setAlignment(Pos.CENTER);
				root.getChildren().add(hbox);

				hbox.getChildren().addAll(extraLeftButtons[buttonRow]);
				for (int buttonColumn = 0; buttonColumn < unshifted[buttonRow].length; buttonColumn++) {
					hbox.getChildren().add(createShiftableButton(unshifted[buttonRow][buttonColumn],
							shifted[buttonRow][buttonColumn], codes[buttonRow][buttonColumn], modifiers, target));
				}
				hbox.getChildren().addAll(extraRightButtons[buttonRow]);
			}
		} catch (NullPointerException exception) {
			root = null;
			LOGGER.error("Virtual Keyboard ", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					exception.getMessage());
		}

		final Button spaceBar = createNonshiftableButton(" ", KeyCode.SPACE, modifiers, target);
		spaceBar.setMaxWidth(Double.POSITIVE_INFINITY);
		HBox.setHgrow(spaceBar, Priority.ALWAYS);

		final HBox bottomRow = new HBox(5);
		bottomRow.setAlignment(Pos.CENTER);
		bottomRow.getChildren().addAll(modifiers.ctrlKey(), modifiers.altKey(), modifiers.metaKey(), spaceBar,
				cursorLeft, cursorUpDown, cursorRight);
		root.getChildren().add(bottomRow);
	}

	/**
	 * Creates a VirtualKeyboard which uses the focusProperty of the scene to
	 * which it is attached as its target
	 */
	private VirtualKeyboard() {
		this(null);
	}

	/**
	 * Visual component displaying this keyboard. The returned node has a style
	 * class of "virtual-keyboard". Buttons in the view have a style class of
	 * "virtual-keyboard-button".
	 * 
	 * @return a view of the keyboard.
	 */
	public Node view() {
		return root;
	}

	private Button createShiftableButton(final String unshifted, final String shifted, final KeyCode code,
			Modifiers modifiers, final ReadOnlyObjectProperty<Node> target) {
		final ReadOnlyBooleanProperty letter = new SimpleBooleanProperty(
				unshifted.length() == 1 && Character.isLetter(unshifted.charAt(0)));
		final StringBinding text = Bindings.when(modifiers.shiftDown().or(modifiers.capsLockOn().and(letter)))
				.then(shifted).otherwise(unshifted);
		Button button = createButton(text, code, modifiers, target);
		return button;
	}

	private Button createNonshiftableButton(final String text, final KeyCode code, final Modifiers modifiers,
			final ReadOnlyObjectProperty<Node> target) {
		StringProperty textProperty = new SimpleStringProperty(text);
		Button button = createButton(textProperty, code, modifiers, target);
		return button;
	}
	private Button createButton(final ObservableStringValue text, final KeyCode code, final Modifiers modifiers,
			final ReadOnlyObjectProperty<Node> target) {
		final Button button = new Button();
		button.textProperty().bind(text);

		button.setFocusTraversable(false);

		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				vkType.append("vk");
				Node targetNode;
				if (target != null) {
					targetNode = target.get();
				} else {
					targetNode = view().getScene().getFocusOwner();
					if(!targetNode.getId().contains("Local"))
						targetNode=null;
				}

				if (targetNode != null) {
					final String character;
					if (KEY_LENGTHS.contains(text.get().length())) {
						character = text.get();
					} else {
						character = KeyEvent.CHAR_UNDEFINED;
					}
					final KeyEvent keyPressEvent = createKeyEvent(button, targetNode, KeyEvent.KEY_PRESSED, character,
							code, modifiers);
					targetNode.fireEvent(keyPressEvent);
					final KeyEvent keyReleasedEvent = createKeyEvent(button, targetNode, KeyEvent.KEY_RELEASED,
							character, code, modifiers);
					targetNode.fireEvent(keyReleasedEvent);
					if (character != KeyEvent.CHAR_UNDEFINED) {
						final KeyEvent keyTypedEvent = createKeyEvent(button, targetNode, KeyEvent.KEY_TYPED, character,
								code, modifiers);
						targetNode.fireEvent(keyTypedEvent);
					}
					modifiers.releaseKeys();
					vkType.delete(0, vkType.length());
				}
			}
		});
		return button;
	}

	private KeyEvent createKeyEvent(Object source, EventTarget target, EventType<KeyEvent> eventType, String character,
			KeyCode code, Modifiers modifiers) {
		return new KeyEvent(source, target, eventType, character, code.toString(), code, modifiers.shiftDown().get(),
				modifiers.ctrlDown().get(), modifiers.altDown().get(), modifiers.metaDown().get());
	}

	private Button createCursorKey(KeyCode code, Modifiers modifiers, ReadOnlyObjectProperty<Node> target,
			Double... points) {
		Button button = createNonshiftableButton("", code, modifiers, target);
		final Node graphic = PolygonBuilder.create().points(points).build();
		graphic.setStyle("-fx-fill: -fx-mark-color;");
		button.setGraphic(graphic);
		button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		return button;
	}

	private static class Modifiers {
		private final ToggleButton shift;
		private final ToggleButton shift2;
		private final ToggleButton ctrl;
		private final ToggleButton alt;
		private final ToggleButton meta;
		private final ToggleButton capsLock;

		Modifiers() {
			this.shift = createToggle("Shift");
			this.shift2 = createToggle("Shift");
			this.ctrl = createToggle("Ctrl");
			this.alt = createToggle("Alt");
			this.meta = createToggle("Meta");
			this.capsLock = createToggle("Caps");

			shift2.selectedProperty().bindBidirectional(shift.selectedProperty());
		}

		private ToggleButton createToggle(final String text) {
			final ToggleButton tb = new ToggleButton(text);
			tb.setFocusTraversable(false);
			return tb;
		}

		public Node shiftKey() {
			return shift;
		}

		public Node secondShiftKey() {
			return shift2;
		}

		public Node ctrlKey() {
			return ctrl;
		}

		public Node altKey() {
			return alt;
		}

		public Node metaKey() {
			return meta;
		}

		public Node capsLockKey() {
			return capsLock;
		}

		public BooleanProperty shiftDown() {
			return shift.selectedProperty();
		}

		public BooleanProperty ctrlDown() {
			return ctrl.selectedProperty();
		}

		public BooleanProperty altDown() {
			return alt.selectedProperty();
		}

		public BooleanProperty metaDown() {
			return meta.selectedProperty();
		}

		public BooleanProperty capsLockOn() {
			return capsLock.selectedProperty();
		}

		public void releaseKeys() {
			shift.setSelected(false);
			ctrl.setSelected(false);
			alt.setSelected(false);
			meta.setSelected(false);
		}
	}

	public void changeControlOfKeyboard(TextField textField) {
		textField.setOnKeyPressed(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				if(!vkType.toString().contains("vk")) {
				KeyEvent e = ((KeyEvent) event);
				if (e.getCode().getName().equals("Caps Lock")) {
					if (capsLock) {
						capsLock = false;
					} else {
						capsLock = true;
					}
				}
				String key;
				if (capsLock) {
					try {
						key = keyboard.getString("shift_" + e.getCode().getName().replaceAll("\\s", ""));
					} catch (MissingResourceException exception) {
						LOGGER.error("Virtual Keyboard", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
								exception.getMessage());
						key = null;
					}
					if (key != null) {
						textField.fireEvent(new KeyEvent(KeyEvent.KEY_TYPED, key, e.getCode().getName(), e.getCode(),
								false, false, false, false));
						textField.setEditable(false);
					}
				} else {
					try {
						key = keyboard.getString("unshift_" + e.getCode().getName().replaceAll("\\s", ""));
					} catch (MissingResourceException exception) {
						LOGGER.error("Virtual Keyboard", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
								exception.getMessage());
						key = null;
					}
					if (key != null) {
						textField.fireEvent(new KeyEvent(KeyEvent.KEY_TYPED, key, e.getCode().getName(), e.getCode(),
								false, false, false, false));
						textField.setEditable(false);
					}
				}
			}
			}});

		textField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> obsVal, final String oldValue,
					final String newValue) {
				Platform.runLater(() -> {
					textField.setEditable(true);
				});

			}
		});
		
		

	}

	public void focusListener(TextField field, double y, Node keyboardNode) {
		field.focusedProperty().addListener(new ChangeListener<Boolean>()
		{
		    @Override
		    public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
		    {
		        if (newPropertyValue)
		        {
		        	keyboardNode.setLayoutY(y);
		        }
		    }
		});
	}
}
