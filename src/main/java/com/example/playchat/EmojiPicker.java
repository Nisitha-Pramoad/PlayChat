package com.example.playchat;

import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Collection;
import java.util.List;

public class EmojiPicker extends VBox {
    private ListView<String> emojiListView;

    public EmojiPicker() {
        // Create the ListView to display the emojis
        emojiListView = new ListView<>();

        // Populate the emoji list
        Collection<Emoji> emojis = EmojiManager.getAll();
        ObservableList<String> emojiList = FXCollections.observableArrayList();
        for (Emoji emoji : emojis) {
            emojiList.add(emoji.getUnicode());
        }
        emojiListView.setItems(emojiList);

        // Create the HBox to hold the emoji ListView
        HBox hBox = new HBox(emojiListView);
        hBox.setPadding(new Insets(5));

        // Add the HBox to the EmojiPicker VBox
        getChildren().add(hBox);
    }

    /**
     * Returns the ListView containing the emojis.
     *
     * @return The emoji ListView.
     */
    public ListView<String> getEmojiListView() {
        return emojiListView;
    }
}
