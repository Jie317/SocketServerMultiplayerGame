package server;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.FlowLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 * @author he
 *
 */
public class ServerGUI {
	private JFrame frame;
	private JTextPane txtpnBienvenuAuTic;
	private JLabel lblNewLabel_1;
	private DefaultListModel<String> listModel;
	private JList<String> list;
	private JScrollPane showIP;

	public ServerGUI(String myadress, int port) {
		initialize();
		setServerInfo(myadress, port);
	}

	private void initialize() {
		frame = new JFrame("TicTacToe Serveur");
		frame.setBounds(300, 300, 300, 300);
		frame.getContentPane().setLayout(null);
		frame.addWindowListener(new WindowAdapter() { 
			public void windowClosing(WindowEvent e) {
			try { 
				for (Player p: SocketServer.playerList){
					p.myOutput.writeObject(new String[]{"ServerExit"});
				}SocketServer.exit(); } catch (Exception e1){} } });
		
		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 300, 100);
		frame.getContentPane().add(panel);
		
		txtpnBienvenuAuTic = new JTextPane();
		txtpnBienvenuAuTic.setBounds(20, 20, 260, 96);
		txtpnBienvenuAuTic.setEditable(false);
		panel.add(txtpnBienvenuAuTic);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(0, 100, 300, 20);
		frame.getContentPane().add(panel_1);
		FlowLayout fl_panel_1 = new FlowLayout(FlowLayout.CENTER, 10, 2);
		panel_1.setLayout(fl_panel_1);
		
		JLabel lblNewLabel = new JLabel("Joueurs :");
		panel_1.add(lblNewLabel);
		
		lblNewLabel_1 = new JLabel("0");
		panel_1.add(lblNewLabel_1);
	
		listModel = new DefaultListModel<String>();
        list = new JList<String>(listModel);
        list.setVisibleRowCount(5);
        
        showIP = new JScrollPane(list);
		showIP.setBounds(20, 120, 260, 120);
		frame.getContentPane().add(showIP);
		
	}

	public void setServerInfo(String myadress, int port) {
		txtpnBienvenuAuTic.setContentType( "text/html" );
		txtpnBienvenuAuTic.setText(String.format("<html><body align=\"center\" \n"
				+ "<br>Bienvenu au TIC TAC TOE Serveur !!!<br><br>Serveur Adresse : %s <br> Port : %d</body></html>", 
				myadress, port));	
	}

	public JFrame getFrame() {
		return frame;
	}

	public JTextPane getTxtpnBienvenuAuTic() {
		return txtpnBienvenuAuTic;
	}

	public JLabel getLblNewLabel_1() {
		return lblNewLabel_1;
	}

	public DefaultListModel<String> getListModel() {
		return listModel;
	}

	public void setListModel(DefaultListModel<String> listModel) {
		this.listModel = listModel;
	}

	public JList<String> getList() {
		return list;
	}

	public void setList(JList<String> list) {
		this.list = list;
	}

}
