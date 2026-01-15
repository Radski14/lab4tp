package com.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import java.io.*;
import java.net.Socket;

/**
 * Główna klasa klienta gry Go.
 * Odpowiada za wyświetlanie interfejsu graficznego, obsługę interakcji użytkownika
 * oraz komunikację z serwerem gry przez gniazda.
 */
public class ClientMain extends Application {

    private static final int SIZE = 19;
    private static final double CELL = 32;
    private static final double MARGIN = 30;

    private ObjectOutputStream out;
    private ObjectInputStream in;

    private boolean yourTurn = false;
    private boolean gameOver = false;
    private boolean scoringMode = false;

    private Pane stoneLayer = new Pane();
    private Label status = new Label("Connecting...");

    private Button passBtn = new Button("PASS");
    private Button resignBtn = new Button("RESIGN");
    private Button doneBtn = new Button("DONE");

    /**
     * Inicjalizuje interfejs graficzny, nawiązuje połączenie z serwerem i konfiguruje scenę.
     *
     * @param stage Główny kontener (okno) aplikacji JavaFX.
     * @throws Exception Jeśli wystąpi błąd podczas łączenia z serwerem lub inicjalizacji GUI.
     */
    @Override
    public void start(Stage stage) throws Exception {
        // Połączenie z serwerem na localhost
        Socket socket = new Socket("localhost", 12345);
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());

        double sizePx = MARGIN * 2 + CELL * (SIZE - 1);
        Canvas boardCanvas = new Canvas(sizePx, sizePx);
        drawBoard(boardCanvas.getGraphicsContext2D());

        stoneLayer.setPrefSize(sizePx, sizePx);
        stoneLayer.setOnMouseClicked(e -> handleClick(e.getX(), e.getY()));

        StackPane board = new StackPane(boardCanvas, stoneLayer);

        // Konfiguracja przycisków
        passBtn.setOnAction(e -> sendMove(new Move(-1, -1, true, false, false)));
        resignBtn.setOnAction(e -> sendMove(new Move(-1, -1, false, true, false)));
        doneBtn.setOnAction(e -> sendMove(new Move(-1, -1, false, false, true)));
        doneBtn.setVisible(false);

        HBox controls = new HBox(10, passBtn, resignBtn, doneBtn);
        controls.setAlignment(javafx.geometry.Pos.CENTER);

        VBox root = new VBox(10, board, controls, status);
        root.setAlignment(javafx.geometry.Pos.CENTER);

        stage.setScene(new Scene(root));
        stage.setHeight(850);
        stage.setTitle("GO Client");
        stage.show();

        startReceiver();
    }

    /**
     * Obsługuje kliknięcie myszką w obszar planszy.
     * Przelicza współrzędne pikselowe na współrzędne siatki gry.
     *
     * @param mx Pozycja X myszy w pikselach.
     * @param my Pozycja Y myszy w pikselach.
     */
    private void handleClick(double mx, double my) {
        if (gameOver) return;
        if (!scoringMode && !yourTurn) return;

        int x = (int) Math.round((mx - MARGIN) / CELL);
        int y = (int) Math.round((my - MARGIN) / CELL);

        if (x < 0 || y < 0 || x >= SIZE || y >= SIZE) return;

        // Wysłanie ruchu (w trybie scoring serwer potraktuje to jako wskazanie kamienia do usunięcia)
        sendMove(new Move(x, y, false, false, false));
    }

    /**
     * Aktualizuje elementy interfejsu użytkownika na podstawie otrzymanego stanu gry.
     * Zarządza widocznością i dostępnością przycisków.
     *
     * @param state Obiekt {@link GameState} zawierający nowe dane z serwera.
     */
    private void updateUI(GameState state) {
        yourTurn = state.yourTurn;
        status.setText(state.message);

        // Wykrywanie fazy punktacji na podstawie komunikatów serwera
        if (state.message.contains("SCORING PHASE") || state.message.contains("removed")) {
            scoringMode = true;
        } else if (state.message.contains("GAME OVER")) {
            gameOver = true;
            scoringMode = false;
        }

        // Aktualizacja stanów przycisków w zależności od fazy gry
        if (scoringMode && !gameOver) {
            passBtn.setVisible(false);
            resignBtn.setVisible(false);
            doneBtn.setVisible(true);
            doneBtn.setDisable(!yourTurn);
        } else {
            passBtn.setVisible(true);
            resignBtn.setVisible(true);
            doneBtn.setVisible(false);
            passBtn.setDisable(!yourTurn || gameOver);
            resignBtn.setDisable(gameOver);
        }

        redrawStones(state.board);
    }

    /**
     * Rysuje statyczną siatkę planszy na obiekcie Canvas.
     *
     * @param g Kontekst graficzny, na którym odbywa się rysowanie.
     */
    private void drawBoard(GraphicsContext g) {
        g.setFill(Color.web("#DEB887")); // Drewniany kolor planszy
        g.fillRect(0, 0, g.getCanvas().getWidth(), g.getCanvas().getHeight());
        g.setStroke(Color.BLACK);
        for (int i = 0; i < SIZE; i++) {
            double p = MARGIN + i * CELL;
            g.strokeLine(MARGIN, p, MARGIN + CELL * (SIZE - 1), p); // Poziome
            g.strokeLine(p, MARGIN, p, MARGIN + CELL * (SIZE - 1)); // Pionowe
        }
    }

    /**
     * Wysyła obiekt ruchu do serwera w sposób bezpieczny dla wyjątków.
     *
     * @param m Obiekt {@link Move} do przesłania.
     */
    private void sendMove(Move m) {
        try {
            out.writeObject(m);
            out.flush();
        } catch (Exception e) {
            status.setText("Connection error");
        }
    }

    /**
     * Uruchamia wątek demona, który nieustannie nasłuchuje na pakiety danych z serwera.
     * Po odebraniu stanu gry, aktualizacja UI jest delegowana do wątku głównego.
     */
    private void startReceiver() {
        Thread t = new Thread(() -> {
            try {
                while (true) {
                    GameState s = (GameState) in.readObject();
                    // Aktualizacja UI musi odbywać się w wątku JavaFX Application Thread
                    Platform.runLater(() -> updateUI(s));
                }
            } catch (Exception e) {
                Platform.runLater(() -> status.setText("Disconnected"));
            }
        });
        t.setDaemon(true);
        t.start();
    }

    /**
     * Czyści warstwę kamieni i rysuje je ponownie na podstawie tekstowej reprezentacji planszy.
     *
     * @param board String zawierający znaki 'B', 'W' lub '.' oddzielone spacjami.
     */
    private void redrawStones(String board) {
        stoneLayer.getChildren().clear();
        String[] rows = board.split("\n");
        for (int y = 0; y < SIZE; y++) {
            String[] cells = rows[y].trim().split(" ");
            for (int x = 0; x < SIZE; x++) {
                if (cells[x].equals("B")) stoneLayer.getChildren().add(stone(x, y, Color.BLACK));
                else if (cells[x].equals("W")) stoneLayer.getChildren().add(stone(x, y, Color.WHITE));
            }
        }
    }

    /**
     * Tworzy graficzny obiekt kamienia (koło) o odpowiednim kolorze i pozycji.
     *
     * @param x Współrzędna siatki X.
     * @param y Współrzędna siatki Y.
     * @param c Kolor wypełnienia koła (Color.BLACK lub Color.WHITE).
     * @return Obiekt {@link Circle} reprezentujący kamień.
     */
    private Circle stone(int x, int y, Color c) {
        Circle s = new Circle(MARGIN + x * CELL, MARGIN + y * CELL, CELL * 0.45);
        s.setFill(c);
        s.setStroke(Color.BLACK);
        return s;
    }

    /**
     * Punkt wejścia aplikacji.
     * @param args Argumenty linii komend.
     */
    public static void main(String[] args) { launch(); }
}