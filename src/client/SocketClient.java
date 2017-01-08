package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import javax.swing.JOptionPane;

/**
 * @author he
 *
 */
public class SocketClient{
	//private static String myadress;
	private static InetAddress myadress;
	private static ObjectOutputStream objectOutput;
	private static Socket socket;
	private static ObjectInputStream objectInput;
	
	//static String serverUri = "192.168.1.95"; // si serveur sur une autre machine
	private static String serverUrl = "127.0.0.1"; //127.0.0.1 si serveur sur son propor host
	private static int port = 8053;
	private static ClientGUI client;
	
	private static String type;
	private static boolean myTurn;
	private static String icon, opIcon;
	private static String stopType;
	 
	public static void main(String[] args) throws Exception{
		client = new ClientGUI();
		client.init();
		client.getFrame().setVisible(true);
        client.comfirmServerInfo();
        connectServer();
		while(true){
			System.out.println("Client starts game...");
	        try {startGame();} catch (Exception e) { handleServerExit();}
	        if (playAgain()) { 
	        	objectOutput.writeObject(new String[]{"NewGame"});
				System.out.println("Client restarts game...");
	        	}
	        else {
	        	objectOutput.writeObject(new String[]{"Disconnect"});
	        	System.exit(0); 
	        	break;
	        }
		}	
		socket.close();
	}
	
	static void connectServer() throws Exception{
		myadress = InetAddress.getLocalHost();
		//myadress = HostAdress.getHostAddress();
		System.out.println("Je suis un client :" + myadress);
		try {
			socket = new Socket(serverUrl, port);
		}catch (IOException e) {
			JOptionPane.showMessageDialog(client.getFrame(), 
					"\tOn ne peut pas connecter le serveur " + serverUrl + " à la port " + port);
			System.exit(0);
		}
		System.out.println("J'ai créé le socket :" + serverUrl + " " + port);
        objectOutput = new ObjectOutputStream(socket.getOutputStream());
		objectInput = new ObjectInputStream(socket.getInputStream());		
	}
	static void startGame() throws Exception{
		client.clearBoard();
        client.getMessageLabel().setText("Attendez votre adversaire...");
				
		// traiter les commands
        String[] cmd;
		while (true) {
			System.out.println("Scaning commands...");

			cmd = (String[]) objectInput.readObject();
			System.out.println("Client received a command: " + String.join(" ", cmd));
	
            if (cmd[0].equals("Start")) {
				String type = cmd[1];
				icon = type.equals("x") ? "croix" : "rond";
	            opIcon  = type.equals("x") ? "rond" : "croix"; 
				myTurn = (type.equals("x")) ? true: false;
				String mes = myTurn ? "A vous jouer ": "Votre adversaire à jouer ";
				client.getMessageLabel().setText("Commencez... "+mes);
				client.getFrame().setTitle("Tic Tac Toe " + type.toUpperCase());
            }
            
            if (cmd[0].equals("Step")) {
            	client.validerStep(Integer.parseInt(cmd[1]), icon);
            	System.out.println("Step valided: "+icon);
				client.getMessageLabel().setText("Votre adversaire à jouer...");
            	myTurn = false;	
			}
            
            if (cmd[0].equals("OpStep")) {
            	client.validerStep(Integer.parseInt(cmd[1]), opIcon);
				client.getMessageLabel().setText("A vous jouer...");
            	myTurn = true;
				
			}
            
			if (cmd[0].equals("Disconnect")) {
				stopType = "Disconnect";
				break;
			}
			
			if (cmd[0].equals("Win")) {
				stopType = "Win";
				break;		
			}
			
			if (cmd[0].equals("Lose")) {
				stopType = "Lose";
				break;
			}
			
			if (cmd[0].equals("Tie")) {
				stopType = "Tie";
				break;
			}
			
			if (cmd[0].equals("ServerExit")) {
				JOptionPane.showMessageDialog(client.getFrame(), "Le serveur a arrêté.","", 
						JOptionPane.OK_OPTION);
				System.exit(0); 			
			}
		}
	}

	private static boolean playAgain() {
		client.getMessageLabel().setText(null);
		String mes;
		switch (stopType) {
		case "Win":
			mes = "<html><body>Vous avez gagné, BRAVO !!!";
			break;
		case "Tie":
			mes = "<html><body>Vous avez fait un match null.";
			break;
		case "Lose":
			mes =  "<html><body>Votre adversaire a gagné le jeu.";
			break;
		default:
			mes = "<html><body>Votre adversaire a terminé le jeu.";
			break;
		}
        int response = JOptionPane.showConfirmDialog(client.getFrame(), mes+
        		"<br>Vous voulez recommencer ?</body></html>",
        		"", JOptionPane.YES_NO_OPTION);
        return response == JOptionPane.YES_OPTION;
    }
	
	public static void handleServerExit() {
		JOptionPane.showMessageDialog(client.getFrame(), "Problème sur la connection du serveur.",
				"", JOptionPane.OK_OPTION);
		System.exit(0); 
	}
	
	public static String getType() {
		return type;
	}

	public static void setType(String type) {
		SocketClient.type = type;
	}

	public static ObjectOutputStream getObjectOutput() {
		return objectOutput;
	}

	public static Socket getSocket() {
		return socket;
	}

	public static ObjectInputStream getObjectInput() {
		return objectInput;
	}

	public static int getPort() {
		return port;
	}

	public static boolean isMyTurn() {
		return myTurn;
	}

	public static void setMyTurn(boolean myTurn) {
		SocketClient.myTurn = myTurn;
	}

	public static void setIcon(String icon) {
		SocketClient.icon = icon;
	}

	public static void setOpIcon(String opIcon) {
		SocketClient.opIcon = opIcon;
	}

	public static String getIcon() {
		return icon;
	}

	public static String getOpIcon() {
		return opIcon;
	}

	public static String getServerUrl() {
		return serverUrl;
	}

	public static void setServerUrl(String serverUrl) {
		SocketClient.serverUrl = serverUrl;
	}

	public static void setPort(int port) {
		SocketClient.port = port;
	}

}
