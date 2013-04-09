import java.io.*;
import java.net.*;

import javax.swing.JOptionPane;


/**
 * 
 * La classe Serveur2D représente le thread principal du serveur, celui-ci attend
 * les connexions des clients et leur associe un nouveau thread ainsi qu'un numéro.
 * Il transmet chaque message des clients à tous les autres.
 * 
 * @author Geoffrey Yoccoz
 *
 */

public class ServeurAW extends Thread {
	
	public static final int port = 8811;
	private FenetreServeur fenetre;	//fenetre qui lance le thread
	private ThreadClient [] tabThread;		//les threadsClients
	
		
	public ServeurAW(FenetreServeur fenetre)	//on recupere la fenetre pour renvoyer des informations
	{
		this.fenetre=fenetre;
		this.tabThread= new ThreadClient[FenetreServeur.MAXPLAYERS];	//un socket par client
		
	}
	
	public void sendToClient(int numClient, String message)
	{
		this.fenetre.printlnInTextArea("Envoi au client "+numClient+" de '"+message+"'");
		this.tabThread[numClient].getSockOut().println(message);
	}
	
	public void sendToAllClients(int sender, String message)
	{
		for(int i=0; i<fenetre.effectivePlayers; i++)
		{
			if(i != sender)	//on ne renvoie pas le message à l'expediteur
			{
				//On envoie le message à tous les autres clients
				this.sendToClient(i, message);				
			}				
		}
	}
	
	public void sendFileToAllClients(File f)
	{
		for(int i=0; i<fenetre.effectivePlayers; i++)
			this.sendFileToClient(i, f);
	}
	
	public void sendFileToClient(int numClient, File f)
	{
		
		this.sendToClient(numClient, "MAP"+f.getName());	//MAP+nomFichier
//		this.sendToClient(numClient,(int)f.length()+"");	//taille du fichier
		BufferedReader fromFile = null;
		try
		{
			fromFile = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
			PrintWriter toClient = this.tabThread[numClient].getSockOut();			
			
			while(fromFile.ready())
			{
				toClient.println(fromFile.readLine());
			}
			toClient.println("END");
		} catch (Exception e)
		{
			// TODO Bloc catch généré automatiquement
			e.printStackTrace();
		}
		
		
//		BufferedInputStream fstream;
//		int total = 0;
//		try
//		{
//			fstream = new BufferedInputStream(new FileInputStream(f));
//			BufferedOutputStream sockOut = new BufferedOutputStream(((ThreadClient)this.tabThread[numClient]).getSocket().getOutputStream());
//			int lu = 0;
//			
//			byte [] buffer = new byte [4096];
//			
//			while((lu = fstream.read(buffer))!= -1) //buffer lu depuis le fichier
//			{
//				
//				sockOut.write(buffer,0,lu);		//envoie la taille lue au client
//				total+=lu;
//				sockOut.flush();
//				System.out.println("Envoye : "+total/1024+"k");
//				
//			}
//			
//			sockOut.flush();						
//			System.out.println("Transfert au joueur "+numClient+" termine !");
//			fstream.close();
//		} 
//		catch (Exception e)
//		{
//			// TODO Bloc catch généré automatiquement
//			e.printStackTrace();
//		}
		
		
	}
		
	public void run()
	{
		ServerSocket s=null;
		try
		{
			s = new ServerSocket(ServeurAW.port);
		} catch (IOException e1)
		{
			// TODO Bloc catch généré automatiquement
			e1.printStackTrace();
			JOptionPane.showMessageDialog(this.fenetre, "Le port "+ServeurAW.port+" est déjà utilisé.\nLe serveur s'éxécute peut-être déjà.","Erreur !",JOptionPane.ERROR_MESSAGE);
		}
		for(int i=0; i<FenetreServeur.MAXPLAYERS; i++)	//pour chaque client
		{
			try
			{
				this.fenetre.printlnInTextArea("En attente du client "+i+" ...");
				
				Socket socket = s.accept();	//on attend la connexion du client
				this.fenetre.printlnInTextArea(socket.toString());
				
				/*On crée un thread dédié au client*/
				tabThread[i] = new ThreadClient(socket,i, this);
				tabThread[i].start();
				this.fenetre.effectivePlayers++;
				
				/*Met à jour la JFrame*/
				this.fenetre.lab1.setText("En attente des clients...  "+this.fenetre.effectivePlayers+"/"+FenetreServeur.MAXPLAYERS);
				this.fenetre.printlnInTextArea("[Connexion] Client "+i+" = "+socket.getInetAddress().getHostAddress());
			//	if(i>1 && this.fenetre.fileXml != null)	//si il y a au moins 2 clients et qu'on a choisi une carte
					this.fenetre.butLancer.setEnabled(true);
				
				this.sendToClient(i,"NUM"+i);
//				this.sendToAllClients(-1, "Client "+i+" connecte");
				
				do
				{
					try
					{
						sleep(200);
					} catch (InterruptedException e)
					{
						// TODO Bloc catch généré automatiquement
						e.printStackTrace();
					}//on attend que le fichier soit selectionné avant de l'envoyer
				}while(this.fenetre.fileXml == null);
				
				this.sendFileToClient(i, this.fenetre.fileXml);
				
			
			}
			catch(IOException e)
			{
				this.fenetre.printlnInTextArea("Erreur dans Serveur2D "+ e +"\n");
				e.printStackTrace();
			}
		}
	}	
	
}
