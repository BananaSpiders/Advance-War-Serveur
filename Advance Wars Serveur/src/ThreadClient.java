import java.io.*;
import java.net.*;


/**
 * 
 * Cette classe représente un thread associé à UN client, elle est créée par Serveur2D.
 * Son rôle est d'attendre les messages du client et de le renvoyer à tous les autres clients.
 *  
 * @author Geoffrey Yoccoz
 * @see ServeurAW
 */

public class ThreadClient extends Thread {

		private Socket socket;
		private String strIn;
		private int numClient;
		private ServeurAW serveur;
		private PrintWriter sockOut;
		private BufferedReader sockIn;
		
		public PrintWriter getSockOut()
		{
			return this.sockOut;
		}
		public BufferedReader getSockIn()
		{
			return this.sockIn;
		}
		
		public Socket getSocket()
		{
			return this.socket;
		}
		
		public ThreadClient(Socket client,int numClient, ServeurAW serveur)
		{
			this.socket = client;
			this.serveur = serveur;
			this.numClient = numClient;
			
			try
			{
				/*Flux entree/sortie*/
				this.sockIn = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
				this.sockOut = new PrintWriter(this.socket.getOutputStream(),true);
//								
			}
			catch(IOException e)
			{
				System.out.println("Erreur thread client !");
				e.printStackTrace();
			}
		}

		public void run()
		{
			while(true)
			{
				try
				{
					this.strIn = this.sockIn.readLine();
					this.serveur.sendToAllClients(this.numClient, strIn);
				} catch (IOException e)
				{
					// TODO Bloc catch généré automatiquement
					e.printStackTrace();
				}
				
			}
		}

		
		
		
}
