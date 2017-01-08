package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import net_utils.HostAdress;
/**
 * @author he
 *
 */
public class SocketServer {
	static final int port = 8053;
	static int nombreJoeurs;
	static ServerGUI serverGUI;
	static private Socket socketService;
	static Game game;
	static int playerNo;
	static ServerSocket socketEcoute;
	static ArrayList<Player> playerList = new ArrayList<>();
	static ArrayList<Player> playersPair = new ArrayList<>();

	public static void main(String[] args) throws Exception {
		String myadress = HostAdress.getHostAddress();
		System.out.println("Je suis le serveur : " + myadress);
		System.out.println("J'écoute sur le port " + port );
		// créer GUI pour serveur
		serverGUI = new ServerGUI(myadress, port);
		serverGUI.getFrame().setVisible(true);
			
		socketEcoute = new ServerSocket(port);
		boolean end = false;
		nombreJoeurs = 0;
		while (!end) {
			System.out.println("\n<<<<<<< En attente d'une connexion...");
			socketService = socketEcoute.accept(); 
			Player player = new Player(socketService);
			player.start();
			addPlayerAndStart(player);	
		}
	}

	public static void exit() {
		System.out.println("Terminé !");
		System.exit(0);}

	public static void updateplayerList(Player player, String op) {
		String playerAdr = player.mySocket.getRemoteSocketAddress().toString();
		if (op.equals("add")) {
			playerList.add(player);
			SocketServer.serverGUI.getListModel().addElement(playerAdr);
		}
		else {
			playerList.remove(player);
			SocketServer.serverGUI.getListModel().removeElement(playerAdr);
		}
		playerNo = playerList.size();
		SocketServer.serverGUI.getLblNewLabel_1().setText(Integer.toString(playerNo));
	}

	public static void addPlayerAndStart(Player player) {
		playersPair.add(player);
		if (playersPair.size() == 2) {
			new Game().startGame(playersPair.get(0), playersPair.get(1));
			System.out.println("Started a game...");
			playersPair.clear();
		}
	}
}



class Game {	
	boolean started = false;
	int [] board = {0,0,0,
					0,0,0,
					0,0,0};
	
	public void startGame(Player player1, Player player2) {
		player1.type = "x";
		player2.type = "o";
		player1.setOpponent(player2);
		player2.setOpponent(player1);
		player1.setGame(this);
		player2.setGame(this);
		started = true;
		try {
			player1.myOutput.writeObject(new String[]{"Start", "x"});
			player2.myOutput.writeObject(new String[]{"Start", "y"});
		} catch (IOException e) {}}

	public boolean checkWin() {
       return
           (board[0] != 0 && board[0] == board[1] && board[0] == board[2])
         ||(board[3] != 0 && board[3] == board[4] && board[3] == board[5])
         ||(board[6] != 0 && board[6] == board[7] && board[6] == board[8])
         ||(board[0] != 0 && board[0] == board[3] && board[0] == board[6])
         ||(board[1] != 0 && board[1] == board[4] && board[1] == board[7])
         ||(board[2] != 0 && board[2] == board[5] && board[2] == board[8])
         ||(board[0] != 0 && board[0] == board[4] && board[0] == board[8])
         ||(board[2] != 0 && board[2] == board[4] && board[2] == board[6]);
   }
	
	public boolean filledUp() {
		for (int i = 0; i < board.length; i++) {
			if (board[i] == 0) return false;
		}
		return true;
	}

}
	class Player extends Thread{
		String type;
		ObjectOutputStream myOutput;
		ObjectInputStream myInput;
		private Player opponent;
		Socket mySocket;
		String[] cmd;
		private Game game;
		
		public Player(Socket mySocket) throws Exception {
			this.mySocket = mySocket;
			// Construire ObjectOutputStream avant ObjectInputStream !!!
			myOutput = new ObjectOutputStream(mySocket.getOutputStream()); 
			myInput = new ObjectInputStream(mySocket.getInputStream());
		}
			
		public void setGame(Game game) {
			this.game = game;
		}

		public void run() {
			SocketServer.updateplayerList(this, "add");
			try {			
				while (true) {
					cmd = (String[]) myInput.readObject();
					System.out.println("Received command from client: "+String.join(" ", cmd));

					if (game != null && game.started && cmd[0].equals("Step")
							&& game.board[Integer.parseInt(cmd[1])] == 0) {
						System.out.println("Valided and transfered the step to the two clients....");
						opponent.myOutput.writeObject(new String[]{"OpStep",cmd[1]}); 
						myOutput.writeObject(cmd);
						game.board[Integer.parseInt(cmd[1])] = (type.equals("x")) ? 1: 2;
						if (game.checkWin()) {
							myOutput.writeObject(new String[]{"Win"});
							opponent.myOutput.writeObject(new String[]{"Lose"});
						}
						if (game.filledUp()) {
							myOutput.writeObject(new String[]{"Tie"});
							opponent.myOutput.writeObject(new String[]{"Tie"});
						}
					}
					if (cmd[0].equals("Disconnect")) {
						try { opponent.myOutput.writeObject(cmd);} catch (Exception e) {}
						SocketServer.updateplayerList(this, "remove");
						break;
					}
					if (cmd[0].equals("NewGame")) {
						game = null;
						SocketServer.addPlayerAndStart(this);
						
					}
				}
				
			} catch (IOException | ClassNotFoundException e) {} finally {
				try {mySocket.close();} catch (IOException e) {}
			}
		}

		public Player getOpponent() {
			return opponent;
		}

		public void setOpponent(Player opponent) {
			this.opponent = opponent;
		}
	}

	
	
	
