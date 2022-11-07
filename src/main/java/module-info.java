module com.example.ZarChat.Client {
    requires javafx.controls;
    requires javafx.fxml;

    exports com.example.ZarChat.Client;
    opens com.example.ZarChat.Client to javafx.fxml;
    exports com.example.ZarChat;
    opens com.example.ZarChat to javafx.fxml;
}