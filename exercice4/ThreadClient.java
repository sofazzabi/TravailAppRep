import java.net.*;
import java.io.*;

class ThreadClient extends Thread
{
BufferedReader In;
PrintWriter Out;
ServeurIRC serveur;
Socket socket;
String nom="??";

public ThreadClient (Socket socket, ServeurIRC s){
    try{
    String msg;
    this.socket=socket;
    Out = new PrintWriter(socket.getOutputStream());
    In = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    serveur=s;
    
    while(true){
        msg=In.readLine();
        if(msg.contains(" ")) Envoyer("Nom ne doit pas contenir d'espace !");
        else if(serveur.exist(msg)==false && msg.equals("")==false) break;
        else Envoyer("Nom deja existant !");
    }
    nom=msg;
    Envoyer("::ok::");
    Envoyer("Welcome "+nom);
    serveur.EnvoyerATous(nom+" connected");
    }
    catch (IOException e ) {e.printStackTrace();}
start();
}

public void run (){


    serveur.EnvoyerATous("::update::");
    System.out.println("Client "+nom+" connected");

try{
    while(true){
      String msg=In.readLine();
      if(msg.equalsIgnoreCase("list"))serveur.EnvoyerListeClients(Out);
      else if(msg.equalsIgnoreCase("exit"))break;
      else if(msg.contains(":"))serveur.Envoyer_p(nom,msg);
      else serveur.EnvoyerATous(nom+"> "+msg);
    }

      Envoyer("::end::");
      serveur.EnvoyerATous(nom+" disconnected");
      serveur.EnvoyerATous("::update::");
      System.out.println("Client "+nom+" disconnected");
      serveur.SupprimerClient(this);
    In.close();
    Out.close();
    socket.close();
}
catch (IOException e ) {e.printStackTrace();}
}

public void Envoyer(String s){ // Envoie vers le client
Out.println(s);
Out.flush();
}

public String nom() { return nom; }

}