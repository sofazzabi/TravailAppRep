import java.util.Vector;
import java.net.*;
import java.io.*;

class ServeurIRC{
Vector V;
public static void main (String args[]){
int port = 1973;
if( args.length == 1 )
port = Integer.parseInt( args [ 0 ] );
new ServeurIRC(port);
}

public ServeurIRC (int port){
V=new Vector();
try
{
ServerSocket server=new ServerSocket(port);
System.out.println("Server is ready");
while (true){
    Socket socket = server.accept();
    ajouterClient(new ThreadClient(socket,this));
}
}
catch (Exception e){System.err.println(e);}
}

synchronized public void EnvoyerATous (String s){
for (int i=0;i<V.size();i++){
ThreadClient Client=(ThreadClient)V.elementAt(i);
Client.Envoyer(s);
}
}

public void Envoyer_p(String nom,String s){
    String name = s.split(":",2)[0];
    name = name.trim();
    s = s.split(":",2)[1];
for (int i=0;i<V.size();i++){
ThreadClient Client=(ThreadClient)V.elementAt(i);
if(name.equals(Client.nom())) {
                            Client.Envoyer(nom+"> "+s);
                              }
}
}

public void ajouterClient(ThreadClient c){
V.addElement(c);
}

synchronized public void EnvoyerListeClients (PrintWriter out){
out.println("::list::");
out.flush();
out.println("Liste des connectés:");
out.flush();
for(int i=0;i<V.size();i++){
       ThreadClient Client =(ThreadClient) V.elementAt(i);
       out.println("-"+Client.nom());
       out.flush();
    }
out.println("::end_list::");
out.flush();
}

synchronized public void SupprimerClient (ThreadClient c){
V.removeElement(c);
}

synchronized public boolean exist(String name){
    for(int i=0;i<V.size();i++){
       ThreadClient Client =(ThreadClient) V.elementAt(i);
       if(name.compareTo(Client.nom())==0){return true;}
    }
    return false;
}

}