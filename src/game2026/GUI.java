package game2026;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.*;

public class GUI extends Application {

	public static final int size = 20; 
	public static final int scene_height = size * 20 + 100;
	public static final int scene_width = size * 20 + 200;

	public static Image image_floor, image_wall;
	public static Image hero_right,hero_left,hero_up,hero_down;

	public static Player me;
	public static List<Player> players = new ArrayList<Player>();

	private Label[][] fields;
	private TextArea scoreList;
	private Client client;
	
	public static String[] board = {    // 20x20
			"wwwwwwwwwwwwwwwwwwww",
			"w        ww        w",
			"w w  w  www w  w  ww",
			"w w  w   ww w  w  ww",
			"w  w               w",
			"w w w w w w w  w  ww",
			"w w     www w  w  ww",
			"w w     w w w  w  ww",
			"w   w w  w  w  w   w",
			"w     w  w  w  w   w",
			"w ww ww        w  ww",
			"w  w w    w    w  ww",
			"w        ww w  w  ww",
			"w         w w  w  ww",
			"w        w     w  ww",
			"w  w              ww",
			"w  w www  w w  ww ww",
			"w w      ww w     ww",
			"w   w   ww  w      w",
			"wwwwwwwwwwwwwwwwwwww"
	};

	
	// -------------------------------------------
	// | Maze: (0,0)              | Score: (1,0) |
	// |-----------------------------------------|
	// | boardGrid (0,1)          | scorelist    |
	// |                          | (1,1)        |
	// -------------------------------------------

	@Override
	public void start(Stage primaryStage) {
		try {
			// Opgave 6: Dialogvindue til indtastning af navn
			TextInputDialog dialog = new TextInputDialog("Player");
			dialog.setTitle("Spillernavn");
			dialog.setHeaderText("Indtast dit spillernavn");
			dialog.setContentText("Navn:");
			Optional<String> result = dialog.showAndWait();
			String playerName = result.orElse("Player_" + (int)(Math.random() * 100));


			GridPane grid = new GridPane();
			grid.setHgap(10);
			grid.setVgap(10);
			grid.setPadding(new Insets(0, 10, 0, 10));

			Text mazeLabel = new Text("Maze:");
			mazeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
	
			Text scoreLabel = new Text("Score:");
			scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

			scoreList = new TextArea();
			scoreList.setEditable(false);

			GridPane boardGrid = new GridPane();

			image_wall  = new Image(getClass().getResourceAsStream("Image/wall4.png"),size,size,false,false);
			image_floor = new Image(getClass().getResourceAsStream("Image/floor1.png"),size,size,false,false);

			hero_right  = new Image(getClass().getResourceAsStream("Image/heroRight.png"),size,size,false,false);
			hero_left   = new Image(getClass().getResourceAsStream("Image/heroLeft.png"),size,size,false,false);
			hero_up     = new Image(getClass().getResourceAsStream("Image/heroUp.png"),size,size,false,false);
			hero_down   = new Image(getClass().getResourceAsStream("Image/heroDown.png"),size,size,false,false);

			fields = new Label[20][20];
			for (int j=0; j<20; j++) {
				for (int i=0; i<20; i++) {
					switch (board[j].charAt(i)) {
					case 'w':
						fields[i][j] = new Label("", new ImageView(image_wall));
						break;
					case ' ':
						fields[i][j] = new Label("", new ImageView(image_floor));
						break;
					default: throw new Exception("Illegal field value: "+board[j].charAt(i) );
					}
					boardGrid.add(fields[i][j], i, j);
				}
			}

			
			grid.add(mazeLabel,  0, 0); 
			grid.add(scoreLabel, 1, 0); 
			grid.add(boardGrid,  0, 1);
			grid.add(scoreList,  1, 1);
						
			Scene scene = new Scene(grid,scene_width,scene_height);
			client = new Client("localhost", 6789, this, playerName);

			primaryStage.setScene(scene);
			primaryStage.setTitle("Netværksspil - " + playerName);
			primaryStage.show();

			scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
				switch (event.getCode()) {
				case W:  client.sendMove(0, -1, "up");   break;
				case S:  client.sendMove(0,+1,"down");  break;
				case A:  client.sendMove(-1,0,"left");  break;
				case D:  client.sendMove(+1,0,"right"); break;
				default: break;
				}
			});

		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void updateBoard(List<Player> players) {
		for (int j = 0; j < 20; j++) {
			for (int i = 0; i < 20; i++) {
				if (board[j].charAt(i) == 'w') {
					fields[i][j].setGraphic(new ImageView(image_wall));
				} else {
					fields[i][j].setGraphic(new ImageView(image_floor));
				}
			}
		}

		StringBuilder scores = new StringBuilder();
		for (Player p : players) {
			Image img = hero_up;
			if (p.getDirection().equals("right")) img = hero_right;
			if (p.getDirection().equals("left")) img = hero_left;
			if (p.getDirection().equals("down")) img = hero_down;

			fields[p.getXpos()][p.getYpos()].setGraphic(new ImageView(img));
			scores.append(p.toString()).append("\n");
		}
			scoreList.setText(scores.toString());
	}
}

