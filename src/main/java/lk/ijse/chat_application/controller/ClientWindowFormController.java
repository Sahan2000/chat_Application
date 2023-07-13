package lk.ijse.chat_application.controller;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import lk.ijse.chat_application.controller.client.Client;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;

public class ClientWindowFormController implements Initializable {
    private final String[] emojis = {
            "\uD83D\uDE00", // 😀
            "\uD83D\uDE01", // 😁
            "\uD83D\uDE02", // 😂
            "\uD83D\uDE03", // 🤣
            "\uD83D\uDE04", // 😄
            "\uD83D\uDE05", // 😅
            "\uD83D\uDE06", // 😆
            "\uD83D\uDE07", // 😇
            "\uD83D\uDE08", // 😈
            "\uD83D\uDE09", // 😉
            "\uD83D\uDE0A", // 😊
            "\uD83D\uDE0B", // 😋
            "\uD83D\uDE0C", // 😌
            "\uD83D\uDE0D", // 😍
            "\uD83D\uDE0E", // 😎
            "\uD83D\uDE0F", // 😏
            "\uD83D\uDE10", // 😐
            "\uD83D\uDE11", // 😑
            "\uD83D\uDE12", // 😒
            "\uD83D\uDE13"  // 😓
    };

    @FXML
    private TextField txtMsgArea;
    private Client client;

    @FXML
    private VBox vBox;

    @FXML
    private AnchorPane emojiAnchorpane;

    @FXML
    private GridPane emojiGridpane;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        emojiAnchorpane.setVisible(false);
        int buttonIndex = 0;
        for (int row = 0; row < 4; row++) {
            for (int column = 0; column < 4; column++) {
                if (buttonIndex < emojis.length) {
                    String emoji = emojis[buttonIndex];
                    JFXButton emojiButton = createEmojiButton(emoji);
                    emojiGridpane.add(emojiButton, column, row);
                    buttonIndex++;
                } else {
                    break;
                }
            }
        }
    }

    private JFXButton createEmojiButton(String emoji) {
        JFXButton button = new JFXButton(emoji);
        button.getStyleClass().add("emoji-button");
        button.setOnAction(this::emojiButtonAction);
        button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        GridPane.setFillWidth(button, true);
        GridPane.setFillHeight(button, true);
        button.setStyle("-fx-font-size: 15; -fx-text-fill: black; -fx-background-color: #F0F0F0; -fx-border-radius: 50");
        return button;
    }

    @FXML
    void btnEmojiOnAction(ActionEvent event) {
        emojiAnchorpane.setVisible(!emojiAnchorpane.isVisible());
    }

    @FXML
    void btnSendOnAction(ActionEvent event) {
        try {
            String text = txtMsgArea.getText();
            if (text != null) {
                appendText(text);
                client.sendMessage(text);
                txtMsgArea.clear();
            } else {
                new Alert(Alert.AlertType.INFORMATION, "message is empty").show();
            }
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Something went wrong : server down").show();
        }
    }

    private void appendText(String text) {
        HBox hBox = new HBox();
        hBox.setStyle("-fx-alignment: center-right;-fx-fill-height: true;-fx-min-height: 50;-fx-pref-width: 520;-fx-max-width: 520;-fx-padding: 10");
        Label messageLbl = new Label(text);
        messageLbl.setStyle("-fx-background-color:  #008011;-fx-background-radius:15;-fx-font-size: 18;-fx-font-weight: normal;-fx-text-fill: white;-fx-wrap-text: true;-fx-alignment: center-left;-fx-content-display: left;-fx-padding: 10;-fx-max-width: 350;");
        hBox.getChildren().add(messageLbl);
        vBox.getChildren().add(hBox);
        new Thread(() -> {
            playSound("media/messageSend.mp3");
        }).start();
    }

    private void playSound(String sound) {
        try {
            new Player(new BufferedInputStream(getClass().getClassLoader().getResourceAsStream(sound))).play();
        } catch (JavaLayerException e) {
            new Alert(Alert.AlertType.ERROR, "Audio not available").show();
        }
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void writeMessage(String message) {
        HBox hBox = new HBox();
        hBox.setStyle("-fx-alignment: center-left;-fx-fill-height: true;-fx-min-height: 50;-fx-pref-width: 520;-fx-max-width: 520;-fx-padding: 10");
        Label messageLbl = new Label(message);
        messageLbl.setStyle("-fx-background-color:   #2980b9;-fx-background-radius:15;-fx-font-size: 18;-fx-font-weight: normal;-fx-text-fill: white;-fx-wrap-text: true;-fx-alignment: center-left;-fx-content-display: left;-fx-padding: 10;-fx-max-width: 350;");
        hBox.getChildren().add(messageLbl);
        Platform.runLater(() -> {

            vBox.getChildren().add(hBox);
        });

    }

    public void imageOnAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image File");
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg");
        fileChooser.getExtensionFilters().add(imageFilter);
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            try {
                byte[] bytes = Files.readAllBytes(selectedFile.toPath());
                HBox hBox = new HBox();
                hBox.setStyle("-fx-fill-height: true; -fx-min-height: 50; -fx-pref-width: 520; -fx-max-width: 520; -fx-padding: 10; -fx-alignment: center-right;");

                // Display the image in an ImageView or any other UI component
                ImageView imageView = new ImageView(new Image(new FileInputStream(selectedFile)));
                imageView.setStyle("-fx-padding: 10px;");
                imageView.setFitHeight(180);
                imageView.setFitWidth(100);

                hBox.getChildren().addAll(imageView);
                vBox.getChildren().add(hBox);

                client.sendImage(bytes);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void setImage(byte[] bytes, String sender) {
        HBox hBox = new HBox();
        Label messageLbl = new Label(sender);
        messageLbl.setStyle("-fx-background-color:   #2980b9;-fx-background-radius:15;-fx-font-size: 18;-fx-font-weight: normal;-fx-text-fill: white;-fx-wrap-text: true;-fx-alignment: center;-fx-content-display: left;-fx-padding: 10;-fx-max-width: 350;");

        hBox.setStyle("-fx-fill-height: true; -fx-min-height: 50; -fx-pref-width: 520; -fx-max-width: 520; -fx-padding: 10; " + (sender.equals(client.getName()) ? "-fx-alignment: center-right;" : "-fx-alignment: center-left;"));
        // Display the image in an ImageView or any other UI component
        Platform.runLater(() -> {
            ImageView imageView = new ImageView(new Image(new ByteArrayInputStream(bytes)));
            imageView.setStyle("-fx-padding: 10px;");
            imageView.setFitHeight(180);
            imageView.setFitWidth(100);

            hBox.getChildren().addAll(messageLbl, imageView);
            vBox.getChildren().add(hBox);

        });
    }

    private void emojiButtonAction(ActionEvent event) {
        JFXButton button = (JFXButton) event.getSource();
        txtMsgArea.appendText(button.getText());
    }
}
