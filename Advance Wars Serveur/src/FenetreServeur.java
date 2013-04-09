import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.*;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

public class FenetreServeur extends JFrame implements ActionListener
{

	protected Container w;
	protected JLabel lab1;
	protected JButton butCharger, butLancer;
	protected JTextArea txtArea;
	protected JScrollPane scrollTxt;
	public static final int MAXPLAYERS = 4;
	protected int effectivePlayers = 0;
	protected File fileXml;
	private ServeurAW t;

	public FenetreServeur()
	{
		this.fileXml = null;
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBounds(200, 200, 400, 260);
		try
		{
			this.setTitle("Serveur Advance Wars @"
					+ InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e)
		{
			// TODO Bloc catch généré automatiquement
			e.printStackTrace();
		}
		this.setResizable(false);
		this.w = this.getContentPane();
		this.w.setLayout(null);

		/* Initialisation */
		this.lab1 = new JLabel("En attente des clients...  "
				+ this.effectivePlayers + "/" + FenetreServeur.MAXPLAYERS);
		this.butCharger = new JButton("Charger une carte");
		this.butLancer = new JButton("Lancer");
		this.butLancer.setEnabled(false);

		this.txtArea = new JTextArea("");
		this.txtArea.setEditable(false);
		this.scrollTxt = new JScrollPane(this.txtArea);
		this.scrollTxt.setAutoscrolls(true);

		/* Ajoute au content pane */
		this.w.add(lab1);
		this.w.add(butCharger);
		this.w.add(butLancer);
		this.w.add(scrollTxt);

		/* Positionne */
		this.lab1.setBounds(20, 20, 200, 20);
		this.butCharger.setBounds(220, 20, 150, 20);
		this.butLancer.setBounds(220, 50, 150, 20);
		this.scrollTxt.setBounds(20, 80, 350, 120);

		/* Evenements */
		this.butLancer.addActionListener(this);
		this.butCharger.addActionListener(this);

		this.setVisible(true);
		
		this.t = new ServeurAW(this);
		this.t.start();
		

	}

	public void printlnInTextArea(String text)
	{
		this.txtArea.setText(this.txtArea.getText()+text+"\n");
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource()==this.butLancer)
		{
			this.t.sendToAllClients(-1, "BGN"+this.effectivePlayers); //BGN = begin, param 1 = nombre de joueurs
		}
		
		if(e.getSource()==this.butCharger)
		{
			String fileName;
			JFileChooser jfc = new JFileChooser(System.getProperty("user.dir",".")+"/map");
			
			FileFilter filter = new FileFilter()
			{
				public boolean accept(File f){
					if(f.isDirectory()) return true;
					else if(f.getName().endsWith(".xml")) return true;
						else return false;
				}
							
				public String getDescription(){
					return "XML files";
				}
			};		
			jfc.removeChoosableFileFilter(jfc.getAcceptAllFileFilter());
			jfc.setFileFilter(filter);
			if(jfc.showDialog(this,"Choisir une carte")==JFileChooser.APPROVE_OPTION){
		         fileName = jfc.getSelectedFile().getPath();
		         
		        this.fileXml = new File(fileName);
			}
		}
		
		
	}

	public static void main(String[] args)
	{
		FenetreServeur f = new FenetreServeur();

	}

}
