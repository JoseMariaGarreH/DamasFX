package com.example.damasfx.Controladores;

import com.example.damasfx.Enumerados.PieceType;
import com.example.damasfx.Gestion.SceneLoader;
import com.example.damasfx.Gestion.UserManagement;
import com.example.damasfx.Modelo.*;
import com.example.damasfx.VDataBase.DataBase;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

public class PlayController implements Initializable {
    private static final Logger logger = LogManager.getLogger(PlayController.class);
    @FXML
    private GridPane root;
    @FXML
    private Circle turnIndicatorCircle;
    @FXML
    private Label turnIndicatorLabel;
    @FXML
    private Text outputNameFirst;
    @FXML
    private Text outputNameSecond;
    private static final int WIDTH = 8;
    private static final int HEIGHT = 8;
    private static final int MAX_MOVES_WITHOUT_CAPTURE = 200;
    private static final Board[][] board = new Board[WIDTH][HEIGHT];
    private static int movesWithoutCapture = 0;
    private static Board selectedTile = null;
    private static PieceType currentPlayer = PieceType.RED;
    private UserManagement userCollection = DataBase.getInstance().getUserCollection();
    private Properties properties = new Properties();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadProperties();
        UserManagement sesionManagement = new UserManagement();
        userCollection.setCurrentUser(userCollection.getUserById(sesionManagement.getLoggedInUser()));
        userCollection.setFirstUser(userCollection.getUserById(sesionManagement.getLoggedInUser()));
        outputNameFirst.setText(userCollection.getFirstUser().getLogin());
        outputNameSecond.setText(userCollection.getSecondUser().getLogin());

        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                Board board = new Board((x + y) % 2 == 0, x, y, this);
                PlayController.board[x][y] = board;
                root.add(board, x, y);
            }
        }
        chargePieces();
    }

    private void loadProperties() {
        try{
            InputStream input = SecondUserController.class.getClassLoader().getResourceAsStream("general.properties");
            properties.load(input);
        } catch (IOException ex) {
            logger.error("Error cargando fichero de propiedades", ex);
        }
    }


    private void chargePieces() {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                if ((x + y) % 2 != 0 && y < 3) {
                    Piece piece = new Piece(PieceType.RED);
                    board[x][y].setPiece(piece);
                }
                if ((x + y) % 2 != 0 && y > 4) {
                    Piece piece = new Piece(PieceType.WHITE);
                    board[x][y].setPiece(piece);
                }
            }
        }
    }

    public void clickBoard(Board tile) {
        if (tile.hasPiece()) {
            if (tile.getPiece().getType() == currentPlayer) {
                if (selectedTile != null) {
                    selectedTile.clearHighlight();
                }
                selectedTile = tile;
                selectedTile.highlight();
            }
        } else if (selectedTile != null) {
            movePiece(selectedTile, tile);
        }
    }

    private void movePiece(Board from, Board to) {
        int fromX = GridPane.getColumnIndex(from);
        int fromY = GridPane.getRowIndex(from);
        int toX = GridPane.getColumnIndex(to);
        int toY = GridPane.getRowIndex(to);

        if (isValidMove(from, to)) {
            Piece piece = from.getPiece();
            from.removePiece();
            to.setPiece(piece);

            boolean captured = Math.abs(toX - fromX) == 2 && Math.abs(toY - fromY) == 2;
            if (captured) {
                int middleX = (fromX + toX) / 2;
                int middleY = (fromY + toY) / 2;
                board[middleX][middleY].removePiece();
                movesWithoutCapture = 0; // Reiniciar contador de movimientos sin captura
            } else {
                movesWithoutCapture++;
            }

            // Promoción a rey
            if ((piece.getType() == PieceType.RED && toY == HEIGHT - 1) ||
                    (piece.getType() == PieceType.WHITE && toY == 0)) {
                piece.setKing(true); // Aquí se muestra el indicador de rey
            }

            from.clearHighlight();
            selectedTile = null;

            if (captured && hasCaptureMoves(to)) {
                to.highlight();
                selectedTile = to;
            } else {
                checkForWinner(); // Verificar ganador antes de cambiar el turno
                switchTurn();
            }
        } else {
            to.showInvalidMoveIcon();
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), event -> {
                to.hideInvalidMoveIcon(); // Oculta el icono
            }));
            timeline.setCycleCount(1); // Indica que solo se debe ejecutar una vez
            timeline.play();
        }
    }

    private void checkForDraw() {
        int redPieces = countPieces(PieceType.RED);
        int whitePieces = countPieces(PieceType.WHITE);

        if (movesWithoutCapture >= MAX_MOVES_WITHOUT_CAPTURE || (whitePieces <= 2 && redPieces == whitePieces)) {
            showDraw();
        }
    }

    private boolean isValidMove(Board from, Board to) {
        int fromX = GridPane.getColumnIndex(from);
        int fromY = GridPane.getRowIndex(from);
        int toX = GridPane.getColumnIndex(to);
        int toY = GridPane.getRowIndex(to);

        int dx = Math.abs(toX - fromX);
        int dy = Math.abs(toY - fromY);

        Piece piece = from.getPiece();
        if (piece == null || to.hasPiece()) {
            return false;
        }

        // Movimientos normales para piezas normales y reyes
        if (dx == 1 && dy == 1) {
            // Fichas normales solo se mueven hacia adelante, los reyes en ambas direcciones
            if (piece.getIsKing() ||
                    (piece.getType() == PieceType.RED && toY > fromY) ||
                    (piece.getType() == PieceType.WHITE && toY < fromY)) {
                return true;
            }
        }

        // Capturas para piezas normales y reyes
        if (dx == 2 && dy == 2) {
            int middleX = (fromX + toX) / 2;
            int middleY = (fromY + toY) / 2;
            Board middleTile = board[middleX][middleY];
            Piece middlePiece = middleTile.getPiece();

            if (middlePiece != null && middlePiece.getType() != piece.getType()) {
                return piece.getIsKing() ||
                        (piece.getType() == PieceType.RED && toY > fromY) ||
                        (piece.getType() == PieceType.WHITE && toY < fromY);
            }
        }

        return false;
    }

    private boolean hasCaptureMoves(Board tile) {
        int x = GridPane.getColumnIndex(tile);
        int y = GridPane.getRowIndex(tile);
        Piece piece = tile.getPiece();

        if (piece == null) {
            return false;
        }

        int[] dx = {-2, -2, 2, 2};
        int[] dy = {-2, 2, -2, 2};

        for (int i = 0; i < 4; i++) {
            int toX = x + dx[i];
            int toY = y + dy[i];

            if (toX >= 0 && toX < WIDTH && toY >= 0 && toY < HEIGHT) {
                Board toTile = board[toX][toY];
                if (isValidMove(tile, toTile)) {
                    return true;
                }
            }
        }

        return false;
    }

    private void switchTurn() {
        currentPlayer = (currentPlayer == PieceType.RED) ? PieceType.WHITE : PieceType.RED;
        updateTurnIndicator();

        if (!hasValidMoves(currentPlayer)) {
            showWinner(currentPlayer == PieceType.RED ? PieceType.WHITE : PieceType.RED);
        } else {
            checkForDraw();
        }
    }


    private void updateTurnIndicator() {
        turnIndicatorCircle.setFill((currentPlayer == PieceType.RED ? Color.RED : Color.WHITE));
        turnIndicatorLabel.setText("Turno: " + (currentPlayer == PieceType.RED ? "Rojo" : "Blanco"));
    }

    private void showDraw() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Empate");
        alert.setHeaderText(null);

        alert.setContentText("Empate, se han realizado demasiados movimientos sin captura");

        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/com/example/damasfx/img/apreton.png")));
        icon.setFitHeight(64);
        icon.setFitWidth(64);
        alert.setGraphic(icon);

        alert.showAndWait();
        resetGame();
    }

    private boolean hasValidMoves(PieceType player) {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                Board tile = board[x][y];
                Piece piece = tile.getPiece();
                if (piece != null && piece.getType() == player) {
                    int[] dx = {-1, -1, 1, 1, -2, -2, 2, 2};
                    int[] dy = {-1, 1, -1, 1, -2, 2, -2, 2};

                    for (int i = 0; i < 8; i++) {
                        int toX = x + dx[i];
                        int toY = y + dy[i];

                        if (toX >= 0 && toX < WIDTH && toY >= 0 && toY < HEIGHT) {
                            Board toTile = board[toX][toY];
                            if (isValidMove(tile, toTile)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private void checkForWinner() {
        int redPieces = countPieces(PieceType.RED);
        int whitePieces = countPieces(PieceType.WHITE);

        if (redPieces == 0) {
            updateScores(PieceType.WHITE);
            showWinner(PieceType.WHITE);
        } else if (whitePieces == 0) {
            updateScores(PieceType.RED);
            showWinner(PieceType.RED);
        }
    }


    private void updateScores(PieceType winner) {
        Users firstUser = userCollection.getFirstUser();
        Users secondUser = userCollection.getSecondUser();

        int firstUserScore = firstUser.getScore();
        int secondUserScore = secondUser.getScore();

        if (winner == PieceType.WHITE) {
            firstUserScore = Math.max(0, firstUserScore + 3);
            secondUserScore = Math.max(0, secondUserScore - 1);
        } else if (winner == PieceType.RED) {
            firstUserScore = Math.max(0, firstUserScore - 1);
            secondUserScore = Math.max(0, secondUserScore + 3);
        }

        firstUser.setScore(firstUserScore);
        secondUser.setScore(secondUserScore);

        userCollection.modifyUser(firstUser);
        userCollection.modifyUser(secondUser);
    }


    private int countPieces(PieceType type) {
        int count = 0;
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                if (board[x][y].hasPiece() && board[x][y].getPiece().getType() == type) {
                    count++;
                }
            }
        }
        return count;
    }

    private void showWinner(PieceType winner) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Victoria");

        String winnerText = (winner == PieceType.RED) ? "Rojas han ganado se llevan 3 puntos y las Blancas pierden 1 punto" : "Blancas han ganado se llevan 3 puntos y las Rojas pierden 1 punto";
        alert.setContentText("¡Las fichas " + winnerText + "!");

        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/com/example/damasfx/img/copa.png")));
        icon.setFitHeight(64);
        icon.setFitWidth(64);
        alert.setGraphic(icon);

        alert.showAndWait();
        resetGame();
    }

    private void resetGame() {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                board[x][y].removePiece();
            }
        }
        chargePieces();
        currentPlayer = PieceType.RED;
        updateTurnIndicator();
    }

    public void onReset(ActionEvent event) {
        resetGame();
    }

    public void onExit(ActionEvent event) {
        SceneLoader.loadScene(properties.getProperty("menu_view"), event);
    }
}
