package com.example.damasfx.Controllers;

import com.example.damasfx.Enums.PieceType;
import com.example.damasfx.Utils.SceneLoader;
import com.example.damasfx.Utils.UserManagement;
import com.example.damasfx.Models.*;
import com.example.damasfx.Services.DataBase;
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
    private static final int WIDTH = 8; // Variable del ancho del tablero
    private static final int HEIGHT = 8; // Variable del ancho del largo del tablero
    private static final int MAX_MOVES_WITHOUT_CAPTURE = 30; // Definición de la constante para el número máximo de movimientos sin captura antes de considerar un empate
    private static final Board[][] board = new Board[WIDTH][HEIGHT]; // Creación de una matriz bidimensional de objetos Board que representa el tablero de juego, con dimensiones 8 x 8
    private static int movesWithoutCapture = 0; // Variable que cuenta el número de movimientos realizados sin ninguna captura
    private static Board selectedSquare = null; // Referencia a la casilla actualmente seleccionada en el tablero,
    // inicializada como null para indicar que no hay ninguna seleccionada al inicio
    private static PieceType currentPlayer = PieceType.RED; // Variable que indica el jugador actual que por defecto es rojo
    private UserManagement userCollection = DataBase.getInstance().getUserCollection(); // Gestión de usuarios
    private Properties properties = new Properties(); // Propiedades cargadas desde un archivo de configuración

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadProperties();

        // Crea una instancia de la clase UserManagement para gestionar usuarios
        UserManagement sesionManagement = new UserManagement();

        // Establece el usuario actual en la colección de usuarios utilizando el ID del usuario que ha iniciado sesión
        userCollection.setCurrentUser(userCollection.getUserById(sesionManagement.getLoggedInUser()));

        // Establece el primer usuario en la colección de usuarios utilizando el ID del usuario que ha iniciado sesión
        userCollection.setFirstUser(userCollection.getUserById(sesionManagement.getLoggedInUser()));

        // Actualiza el texto de la interfaz con el login del primer usuario
        outputNameFirst.setText(userCollection.getFirstUser().getLogin());

        // Actualiza el texto de la interfaz con el login del segundo usuario (aunque aquí parece que falta la asignación del segundo usuario)
        outputNameSecond.setText(userCollection.getSecondUser().getLogin());

        // Doble bucle anidado para inicializar una matriz de objetos Board en una cuadrícula de tamaño 8x8
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                // Crea un nuevo objeto Board, alternando entre dos estados en función de la posición
                Board board = new Board((x + y) % 2 == 0,  this);
                // Asigna el objeto Board a la posición correspondiente en la matriz estática PlayController.board
                PlayController.board[x][y] = board;
                // Añade el objeto Board al GridPanel root en la posición especificada por x e y
                root.add(board, x, y);
            }
        }

        // Método para cargar piezas en el tablero
        chargePieces();
    }

    // Método para cargar las propiedades desde un archivo de configuración.
    private void loadProperties() {
        try {
            InputStream input = SecondUserController.class.getClassLoader().getResourceAsStream("general.properties");
            properties.load(input);
        } catch (IOException ex) {
            logger.error("Error cargando fichero de propiedades", ex);
        }
    }


    private void chargePieces() {
        // Recorre sobre todas las filas del tablero
        for (int y = 0; y < HEIGHT; y++) {
            // Recorre sobre todas las columnas del tablero
            for (int x = 0; x < WIDTH; x++) {
                // Comprueba si la celda está en un cuadro oscuro y si está en las primeras 3 filas
                if ((x + y) % 2 != 0 && y < 3) {
                    // Crea una nueva pieza de tipo roja
                    Piece piece = new Piece(PieceType.RED);
                    // Coloca la pieza en la celda correspondiente del tablero
                    board[x][y].setPiece(piece);
                }
                // Comprueba si la celda está en un cuadro oscuro y si está en las últimas 3 filas
                if ((x + y) % 2 != 0 && y > 4) {
                    // Crea una nueva pieza de tipo blanca
                    Piece piece = new Piece(PieceType.WHITE);
                    // Coloca la pieza en la celda correspondiente del tablero
                    board[x][y].setPiece(piece);
                }
            }
        }
    }

    public void clickBoard(Board square) {
        // Comprueba que en la casilla haya pieza
        if (square.hasPiece()) {
            // Comprueba que la ficha sea del mismo color que la del jugador actual
            if (square.getPiece().getType() == currentPlayer) {
                // Sí hay una casilla previamente seleccionada, la deselecciona
                if (selectedSquare != null) {
                    selectedSquare.clearHighlight();
                }
                // Actualiza la casilla seleccionada a la nueva casilla
                selectedSquare = square;
                // Resalta la nueva casilla seleccionada
                selectedSquare.highlight();
            }
        } else if (selectedSquare != null) {
            // Si no hay pieza en la casilla y hay una casilla seleccionada, mueve la pieza
            movePiece(selectedSquare, square);
        }
    }


    private void movePiece(Board from, Board to) {
        // Obtiene las coordenadas de columna y fila de la casilla origen
        int fromX = GridPane.getColumnIndex(from);
        int fromY = GridPane.getRowIndex(from);
        // Obtiene las coordenadas de columna y fila de la casilla destino
        int toX = GridPane.getColumnIndex(to);
        int toY = GridPane.getRowIndex(to);

        // Verifica si el movimiento es válido
        if (isValidMove(from, to)) {
            // Obtiene la pieza de la casilla origen
            Piece piece = from.getPiece();
            // Remueve la pieza de la casilla origen
            from.removePiece();
            // Coloca la pieza en la casilla destino
            to.setPiece(piece);

            // Comprueba si se ha capturado una pieza
            boolean captured = Math.abs(toX - fromX) == 2 && Math.abs(toY - fromY) == 2;
            if (captured) {
                // Calcula las coordenadas de la casilla intermedia
                int middleX = (fromX + toX) / 2;
                int middleY = (fromY + toY) / 2;
                // Remueve la pieza capturada de la casilla intermedia
                board[middleX][middleY].removePiece();
                // Reinicia el contador de movimientos sin captura
                movesWithoutCapture = 0;
            } else {
                // Incrementa el contador de movimientos sin captura
                movesWithoutCapture++;
            }

            // Promoción a rey si la pieza roja llega a la última fila o la pieza blanca a la primera fila
            if ((piece.getType() == PieceType.RED && toY == HEIGHT - 1) ||
                    (piece.getType() == PieceType.WHITE && toY == 0)) {
                piece.setKing(true); // Establece la pieza como rey
            }

            // Limpia la selección y el resaltado de la casilla
            from.clearHighlight();
            selectedSquare = null;

            // Si se ha capturado una pieza y hay más movimientos de captura disponibles
            if (captured && hasCaptureMoves(to)) {
                to.highlight(); // Resalta la casilla destino
                selectedSquare = to; // Establece la casilla destino como la casilla seleccionada
            } else {
                checkForWinner(); // Verifica si hay un ganador antes de cambiar el turno
                switchTurn(); // Cambia el turno
            }
        } else {
            // Si el movimiento no es válido, muestra un icono de movimiento inválido
            to.showInvalidMoveIcon();
            // Crea una animación para ocultar el icono después de 0.5 segundos
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), event -> {
                to.hideInvalidMoveIcon(); // Oculta el icono de movimiento inválido
            }));
            timeline.setCycleCount(1); // Configura la animación para ejecutarse una vez
            timeline.play(); // Inicia la animación
        }
    }

    private void checkForDraw() {
        // Cuenta el número de piezas rojas en el tablero
        int redPieces = countPieces(PieceType.RED);

        // Cuenta el número de piezas blancas en el tablero
        int whitePieces = countPieces(PieceType.WHITE);

        // Verifica si se ha alcanzado el máximo número de movimientos sin captura
        // o si ambos jugadores tienen 2 o menos piezas y la cantidad de piezas de cada jugador es igual
        if (movesWithoutCapture >= MAX_MOVES_WITHOUT_CAPTURE || (whitePieces <= 2 && redPieces == whitePieces)) {
            // Muestra que el juego ha terminado en empate
            updateScores(null);
            showDraw();
        }
    }

    private boolean isValidMove(Board from, Board to) {
        // Obtiene las coordenadas de la casilla que esta actualmente
        int fromX = GridPane.getColumnIndex(from);
        int fromY = GridPane.getRowIndex(from);
        // Obtiene las coordenadas de la casilla a la que se va a dirigir
        int toX = GridPane.getColumnIndex(to);
        int toY = GridPane.getRowIndex(to);

        // Calcula la diferencia absoluta en las coordenadas
        int dx = Math.abs(toX - fromX);
        int dy = Math.abs(toY - fromY);

        // Obtiene la pieza de la casilla 'from'
        Piece piece = from.getPiece();
        // Verifica si la casilla 'from' no tiene pieza o si la casilla 'to' ya tiene una pieza
        if (piece == null || to.hasPiece()) {
            return false; // El movimiento no es válido
        }

        // Verificación de movimientos normales para piezas normales y reyes
        if (dx == 1 && dy == 1) {
            // Fichas normales solo se mueven hacia adelante, los reyes pueden moverse en ambas direcciones
            if (piece.getIsKing() || // Si la pieza es un rey
                    (piece.getType() == PieceType.RED && toY > fromY) || // Pieza roja moviéndose hacia adelante
                    (piece.getType() == PieceType.WHITE && toY < fromY)) { // Pieza blanca moviéndose hacia adelante
                return true; // El movimiento es válido
            }
        }

        // Verificación de capturas para piezas normales y reyes
        if (dx == 2 && dy == 2) {
            // Calcula la posición de la casilla en el medio del movimiento
            int middleX = (fromX + toX) / 2;
            int middleY = (fromY + toY) / 2;
            Board middleSquare = board[middleX][middleY]; // Casilla del medio
            Piece middlePiece = middleSquare.getPiece(); // Pieza en la casilla del medio

            // Verifica si hay una pieza en la casilla del medio y si es del tipo contrario
            if (middlePiece != null && middlePiece.getType() != piece.getType()) {
                return piece.getIsKing() || // Si la pieza es un rey
                        (piece.getType() == PieceType.RED && toY > fromY) || // Pieza roja capturando hacia adelante
                        (piece.getType() == PieceType.WHITE && toY < fromY); // Pieza blanca capturando hacia adelante
            }
        }

        // Si ninguna de las condiciones anteriores se cumple, el movimiento no es válido
        return false;
    }


    // Método para verificar si una pieza en una casilla tiene movimientos de captura disponibles
    private boolean hasCaptureMoves(Board square) {
        // Obtiene las coordenadas de la casilla
        int x = GridPane.getColumnIndex(square);
        int y = GridPane.getRowIndex(square);
        // Obtiene la pieza de la casilla
        Piece piece = square.getPiece();

        // Verifica si la casilla  no tiene una pieza
        if (piece == null) {
            return false; // No hay movimientos de captura si no hay pieza
        }

        // Arreglos para las posibles direcciones de captura (diagonales de dos casillas)
        int[] dx = {-2, -2, 2, 2};
        int[] dy = {-2, 2, -2, 2};

        // Recorre sobre las cuatro posibles direcciones de captura
        for (int i = 0; i < 4; i++) {
            // Calcula las coordenadas de la casilla de destino para un posible movimiento de captura
            int toX = x + dx[i];
            int toY = y + dy[i];

            // Verifica si la casilla de destino está dentro de los límites del tablero
            if (toX >= 0 && toX < WIDTH && toY >= 0 && toY < HEIGHT) {
                // Obtiene la casilla de destino
                Board toSquare = board[toX][toY];
                // Verifica si el movimiento de la casilla de origen a la casilla de destino es válido
                if (isValidMove(square, toSquare)) {
                    return true; // Si hay un movimiento de captura válido, retorna true
                }
            }
        }

        // Si ninguna dirección de captura es válida, retorna false
        return false;
    }


    private void switchTurn() {
        currentPlayer = (currentPlayer == PieceType.RED) ? PieceType.WHITE : PieceType.RED;
        updateTurnIndicator();

        // Verificar si el jugador actual tiene movimientos válidos
        if (!hasValidMoves(currentPlayer)) {
            showWinner(currentPlayer == PieceType.RED ? PieceType.WHITE : PieceType.RED);
            return; // Termina el turno
        }

        // Verificar si el oponente tiene movimientos válidos
        PieceType opponent = (currentPlayer == PieceType.RED) ? PieceType.WHITE : PieceType.RED;
        if (!hasValidMoves(opponent)) {
            showWinner(currentPlayer); // Si el oponente no tiene movimientos válidos, el jugador actual gana
            return; // Termina el turno
        }

        checkForDraw();
    }



    private void updateTurnIndicator() {
        // Para el manejo del cambio de turnos, si el jugador actual es el rojo, se coloreará de color rojo
        // En caso contrario coloreará de color blanco si el jugador actual es el blanco
        turnIndicatorCircle.setFill((currentPlayer == PieceType.RED ? Color.RED : Color.WHITE));
        // También se cambiará el texto según el turno que sea, igual que los circulos.
        turnIndicatorLabel.setText("Turno: " + (currentPlayer == PieceType.RED ? "Rojo" : "Blanco"));
    }

    // Método que muestra el empate
    private void showDraw() {
        // Crea una nueva alerta
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Empate");
        alert.setHeaderText(null);
        alert.setContentText("Empate, se reparten los puntos y cada uno gana 1 punto");

        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/com/example/damasfx/img/apreton.png")));
        icon.setFitHeight(64);
        icon.setFitWidth(64);
        alert.setGraphic(icon);

        // Muestra la alerta y espera a que el usuario la cierre
        alert.showAndWait();
        // Llama al método para reiniciar el juego
        resetGame();
    }

    private boolean hasValidMoves(PieceType player) {
        // Recorre sobre todas las filas del tablero.
        for (int y = 0; y < HEIGHT; y++) {
            // Recorre sobre todas las columnas del tablero.
            for (int x = 0; x < WIDTH; x++) {
                // Obtiene la casilla actual del tablero.
                Board square = board[x][y];
                // Obtiene la pieza en la casilla actual.
                Piece piece = square.getPiece();

                // Si hay una pieza en la casilla actual y es del tipo del jugador
                if (piece != null && piece.getType() == player) {
                    // Define los posibles movimientos de la pieza
                    int[] dx = {-1, -1, 1, 1, -2, -2, 2, 2};
                    int[] dy = {-1, 1, -1, 1, -2, 2, -2, 2};

                    // Recorre todos los movimientos posibles
                    for (int i = 0; i < 8; i++) {
                        // Calcula las nuevas coordenadas de destino
                        int toX = x + dx[i];
                        int toY = y + dy[i];

                        // Verifica si las nuevas coordenadas están dentro del tablero.
                        if (toX >= 0 && toX < WIDTH && toY >= 0 && toY < HEIGHT) {
                            // Obtiene la casilla de destino.
                            Board toSquare = board[toX][toY];

                            // Verifica si el movimiento desde la casilla actual a la casilla de destino es válido.
                            if (isValidMove(square, toSquare)) {
                                // Si se encuentra al menos un movimiento válido, retorna true.
                                return true;
                            }
                        }
                    }
                }
            }
        }
        // Si no se encuentran movimientos válidos, devuelve false.
        return false;
    }



    private void checkForWinner() {
        // Cuenta el número de piezas rojas en el tablero
        int redPieces = countPieces(PieceType.RED);
        // Cuenta el número de piezas blancas en el tablero
        int whitePieces = countPieces(PieceType.WHITE);

        // Si no quedan piezas rojas, el jugador con piezas blancas gana
        if (redPieces == 0) {
            // Actualiza la puntuación, dandole puntos al jugador con piezas blancas
            updateScores(PieceType.WHITE);
            // Muestra un mensaje indicando que el jugador con piezas blancas ha ganado
            showWinner(PieceType.WHITE);
            // Si no quedan piezas blancas, el jugador con piezas rojas gana
        } else if (whitePieces == 0) {
            // Actualiza la puntuación, dandole puntos al jugador con piezas rojas
            updateScores(PieceType.RED);
            // Muestra un mensaje indicando que el jugador con piezas rojas ha ganado
            showWinner(PieceType.RED);
        }
    }


    private void updateScores(PieceType winner) {
        // Obtiene los usuarios desde la colección de usuarios
        Users firstUser = userCollection.getFirstUser();
        Users secondUser = userCollection.getSecondUser();

        // Obtiene los puntos actuales de los usuarios
        int firstUserScore = firstUser.getScore();
        int secondUserScore = secondUser.getScore();

        // Si el ganador es el jugador con piezas blancas
        if (winner == PieceType.WHITE) {
            // Incrementa los puntos del primer usuario en 3 puntos
            firstUserScore = firstUserScore + 3;
            // Decrementa los puntos del segundo usuario en 1 punto (En caso de que sea negativo coge el más grande)
            secondUserScore = Math.max(0, secondUserScore - 1);
            // Si el ganador es el jugador con piezas rojas
        } else if (winner == PieceType.RED) {
            // Decrementa los puntos del primer usuario en 1 punto (En caso de que sea negativo coge el más grande)
            firstUserScore = Math.max(0, firstUserScore - 1);
            // Incrementa los puntos del segundo usuario en 3 puntos
            secondUserScore = secondUserScore + 3;
        }else{
            // Incrementa los puntos de los dos usuarios a 1 en caso de empate
            firstUserScore = firstUserScore + 1;
            secondUserScore = secondUserScore + 1;
        }

        // Establece los nuevos puntos para los usuarios
        firstUser.setScore(firstUserScore);
        secondUser.setScore(secondUserScore);

        // Modifica los usuarios en la colección para que reflejen en la tabla
        userCollection.modifyUser(firstUser);
        userCollection.modifyUser(secondUser);
    }


    private int countPieces(PieceType type) {
        int count = 0; // Inicializa el contador a 0

        // Recorre sobre todas las filas del tablero
        for (int y = 0; y < HEIGHT; y++) {
            // Recorre sobre todas las columnas del tablero
            for (int x = 0; x < WIDTH; x++) {
                // Verifica si la casilla en las posiciones x e y tienen una pieza
                if (board[x][y].hasPiece() && board[x][y].getPiece().getType() == type) {
                    count++; // Incrementa el contador si la pieza es del tipo especificado
                }
            }
        }
        return count; // Retorna el número total de piezas del tipo especificado
    }


    // Método que muestra la victoria del usuario
    private void showWinner(PieceType winner) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Victoria");

        // si el ganador es el rojo muestra mensaje para jugador rojo, si fuera al contrario para el blanco
        String winnerText = (winner == PieceType.RED) ? "Rojas han ganado se llevan 3 puntos y las Blancas pierden 1 punto" : "Blancas han ganado se llevan 3 puntos y las Rojas pierden 1 punto";
        alert.setContentText("¡Las fichas " + winnerText + "!");

        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/com/example/damasfx/img/copa.png")));
        icon.setFitHeight(64);
        icon.setFitWidth(64);
        alert.setGraphic(icon);

        // Para la vista hasta que el usuario presione al botón aceptar
        alert.showAndWait();
        // Retablece el juego
        resetGame();
    }



    private void resetGame() {
        // Recorre sobre todas las filas del tablero
        for (int y = 0; y < HEIGHT; y++) {
            // Recorre sobre todas las columnas del tablero
            for (int x = 0; x < WIDTH; x++) {
                // Remueve la pieza de la casilla en la posición x e y
                board[x][y].removePiece();
            }
        }
        // Carga las piezas en sus posiciones iniciales
        chargePieces();
        movesWithoutCapture = 0;
        // Establece el jugador actual como el jugador rojo
        currentPlayer = PieceType.RED;
        // Actualiza el indicador de turno
        updateTurnIndicator();
    }

    // Método que renicia el tablero, y lo vuelve al estado inicial
    @FXML
    public void onReset(ActionEvent event) {
        if(selectedSquare != null) {
            selectedSquare.clearHighlight();
        }
        resetGame();
    }

    // Método cámbio de escena al menú
    @FXML
    public void onExit(ActionEvent event) {
        SceneLoader.loadScene(properties.getProperty("menu_view"), event);
    }
}