package com.example.playchat;

import com.jfoenix.controls.JFXButton;
import com.vdurmont.emoji.EmojiParser;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientController implements Initializable {

    @FXML
    private JFXButton button_send;

    @FXML
    private JFXButton file_button;

    @FXML
    private JFXButton emoji_button;

    @FXML
    private ScrollPane sp_main;

    @FXML
    private TextField tf_message;

    @FXML
    private VBox vBox_message;

    @FXML
    private Label lblUserName;

    @FXML
    private AnchorPane pane;

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String name;
    private EmojiPicker emojiPicker;
    private FileChooser fileChooser;

    /**
     * Initializes the client controller after the FXML file is loaded.
     * Sets up event handlers and initializes the emoji picker.
     *
     * @param location  The location used to resolve relative paths for the root object.
     * @param resources The resources used to localize the root object.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Adjust the scroll pane to always show the latest messages
        vBox_message.heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            sp_main.setVvalue(newValue.doubleValue());
        });

        // Send button action
        button_send.setOnAction(event -> {
            String messageToSend = tf_message.getText().trim();
            if (!messageToSend.isEmpty()) {
                sendMessage(name + " : " + messageToSend);
                addLabel(name + " : " + messageToSend, MessageType.TEXT, true);
                tf_message.clear();
            }
        });

        // handle emojis
        handleEmojiSelection();

        // Set the client's username
        setUserName();

        // Start the client
        startClient();
    }

    /**
     * Handles the selection of emojis.
     * Initializes the emoji picker, shows/hides the picker, and inserts the selected emoji into the text field.
     */
    private void handleEmojiSelection() {
        // Initialize the emoji picker
        emojiPicker = new EmojiPicker();
        VBox vBox = new VBox(emojiPicker);
        vBox.setPrefSize(160, 200);
        vBox.setLayoutX(390);
        vBox.setLayoutY(127);
        vBox.setStyle("-fx-font-size: 30");
        pane.getChildren().add(vBox);
        emojiPicker.setVisible(false);

        // Show/hide emoji picker
        emoji_button.setOnAction(event -> {
            emojiPicker.setVisible(!emojiPicker.isVisible());
        });

        // Insert selected emoji into the text field
        emojiPicker.getEmojiListView().setOnMouseClicked(event -> {
            String selectedEmoji = emojiPicker.getEmojiListView().getSelectionModel().getSelectedItem();
            if (selectedEmoji != null) {
                tf_message.setText(tf_message.getText() + EmojiParser.parseToUnicode(selectedEmoji));
            }
            emojiPicker.setVisible(false);
        });
    }


    /**
     * Constructs a new ClientController with the specified name.
     *
     * @param name The name of the client.
     */
    public ClientController(String name) {
        this.name = name;
    }

    /**
     * Starts the client by connecting to the server, sending the client's name, and reading messages from the server.
     * Displays any received messages or images in the chat UI.
     */
    private void startClient() {
        try {
            // Connect to the server
            socket = new Socket("localhost", 5000);
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Send the client's name to the server
            sendMessage(name);

            // Start reading messages from the server
            readMessages();
        } catch (IOException e) {
            closeAll();
            e.printStackTrace();
        }
    }

    /**
     * Reads messages from the server in a separate thread.
     * Determines whether a message is a text message or an image message and updates the chat UI accordingly.
     */
    private void readMessages() {
        new Thread(() -> {
            try {
                String message;
                while ((message = bufferedReader.readLine()) != null) {
                    if (message.matches(".*\\.(png|jpe?g|gif)$")) {
                        // Image message
                        String senderName = message.split("[-]")[0];
                        String imageUrl = message.split("[-]")[1];
                        Platform.runLater(() -> addImageLabel(senderName, imageUrl));
                    } else {
                        // Text message
                        final String finalMessage = message;
                        Platform.runLater(() -> addLabel(finalMessage, MessageType.TEXT, false));
                    }
                }
            } catch (IOException e) {
                closeAll();
                e.printStackTrace();
            }
        }).start();
    }


    /**
     * Sends a message to the server.
     *
     */
    private void sendMessage(String message) {
        try {
            // Write the message to the BufferedWriter and flush the buffer
            bufferedWriter.write(message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            // If an exception occurs, close the connection and print the stack trace
            closeAll();
            e.printStackTrace();
        }
    }


    /**
     * Adds a message label to the chat UI.
     * The label represents a message from a client.
     *
     * @param messageFromClient The message content from the client.
     * @param type             The type of the message (text or image).
     * @param isSentMessage    Indicates whether the message is sent by the client or received.
     */
    public void addLabel(String messageFromClient, MessageType type, boolean isSentMessage) {
        HBox hBox = new HBox();
        Text text = new Text(messageFromClient);
        TextFlow textFlow = new TextFlow(text);

        // Set background color and text color based on sent or received message
        if (isSentMessage) {
            textFlow.setStyle("-fx-background-color: #ACBBBC; -fx-background-radius: 20px;");
            text.setFill(Color.BLACK);
        } else {
            textFlow.setStyle("-fx-background-color: #00FF00; -fx-background-radius: 20px;");
            text.setFill(Color.BLACK);
        }

        textFlow.setPadding(new Insets(5, 5, 5, 10));

        hBox.getChildren().add(textFlow);

        // Set alignment based on sent or received message
        hBox.setAlignment(isSentMessage ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(1)); // Set the padding around the HBox

        // Add the message label to the chat UI
        Platform.runLater(() -> vBox_message.getChildren().add(hBox));
    }


    private void addImageLabel(String senderName, String imageUrl) {
        ImageView imageView;
        if (imageUrl.startsWith("http")) {
            // Image URL
            Image fxImage = new Image(imageUrl);
            imageView = new ImageView(fxImage);
        } else {
            // Local file path
            File file = new File(imageUrl);
            try {
                BufferedImage bufferedImage = ImageIO.read(file);
                Image fxImage = SwingFXUtils.toFXImage(bufferedImage, null);
                imageView = new ImageView(fxImage);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        // Dynamically adjust the image dimensions based on the original image size
        double maxWidth = 200;
        double maxHeight = 200;
        double imageWidth = imageView.getImage().getWidth();
        double imageHeight = imageView.getImage().getHeight();
        double scaleFactor = Math.min(maxWidth / imageWidth, maxHeight / imageHeight);

        imageView.setFitWidth(imageWidth * scaleFactor);
        imageView.setFitHeight(imageHeight * scaleFactor);

        HBox hBox = new HBox();
        hBox.setPadding(new Insets(5, 5, 5, 10));
        hBox.getChildren().add(imageView);
        hBox.setAlignment(Pos.CENTER_LEFT);

        Platform.runLater(() -> vBox_message.getChildren().add(hBox));
    }

    /**
     * Sets the username in the label.
     * The username is displayed in the user interface.
     */
    private void setUserName() {
        lblUserName.setText(name);
    }

    /**
     * Closes all the resources associated with the client connection.
     * This method is responsible for closing the BufferedReader, BufferedWriter, and Socket instances.
     * It handles any IOException that may occur during the closing process.
     */
    private void closeAll() {
        try {
            if (bufferedReader != null)
                bufferedReader.close();
            if (bufferedWriter != null)
                bufferedWriter.close();
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void btnFileButtonOnAction(ActionEvent event) {
        // Open file chooser dialog to select an image
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(getStage());
        if (selectedFile != null) {
            // Send the selected image
            sendImage(selectedFile.getAbsolutePath());
        }
    }

    /**
     * Sends an image to the server.
     * The method loads the image from the given file path, resizes it, and adds it to the UI.
     * It also sends the image path to the server using the BufferedWriter.
     *
     * @param filePath The file path of the selected image
     */
    private void sendImage(String filePath) {
        try {
            // Load the image from the file path
            Image image = new Image(new FileInputStream(filePath));
            ImageView imageView = new ImageView(image);

            // Dynamically adjust the image dimensions based on the original image size
            double maxWidth = 200;
            double maxHeight = 200;
            double imageWidth = imageView.getImage().getWidth();
            double imageHeight = imageView.getImage().getHeight();
            double scaleFactor = Math.min(maxWidth / imageWidth, maxHeight / imageHeight);

            imageView.setFitWidth(imageWidth * scaleFactor);
            imageView.setFitHeight(imageHeight * scaleFactor);

            HBox hBox = new HBox();
            hBox.setPadding(new Insets(5, 5, 5, 10));
            hBox.getChildren().add(imageView);
            hBox.setAlignment(Pos.CENTER_RIGHT);

            vBox_message.getChildren().add(hBox);

            // Send the image path to the server
            PrintWriter printWriter = new PrintWriter(bufferedWriter);
            printWriter.println(name + "-" + filePath);
            printWriter.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the current stage.
     * This method is used to access the stage/window associated with the UI elements.
     *
     * @return The current stage
     */
    private Stage getStage() {
        return (Stage) button_send.getScene().getWindow();
    }


    @FXML
    void btnSendOnAction(ActionEvent event) {
        // Get the message to send from the text field
        String messageToSend = tf_message.getText().trim();

        if (!messageToSend.isEmpty()) {
            // Send the message to the server
            sendMessage(name + " : " + messageToSend);

            // Add the message label to the UI
            addLabel(name + " : " + messageToSend, MessageType.TEXT, true);

            // Clear the text field
            tf_message.clear();
        }
    }

    @FXML
    void txtMesssageFieldOnAction(ActionEvent event) {
        // Call the btnSendOnAction method when click enter button to send msg
        btnSendOnAction(event);
    }

    /**
     * Represents the type of message.
     * The MessageType enum is used to distinguish between text and image messages.
     */
    private enum MessageType {
        TEXT,
        IMAGE
    }

}
