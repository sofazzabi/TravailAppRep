import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.*;
class ClientFenetre extends Frame implements Runnable, ActionListener
{
    TextArea Output;
    TextField Input;
    Socket socket = null;
    BufferedReader in ;
    PrintWriter out ;
    public ClientFenetre(InetAddress hote, int port)
    {
        super("Client en fenetre");
// mise en forme de la fenetre (frame)
        setSize(500,700);
        setLayout(new BorderLayout());
        add( Output=new TextArea(),BorderLayout.CENTER );
        Output.setEditable(false);
        add( Input=new TextField(), BorderLayout.SOUTH );
        Input.addActionListener(this);
        pack();
        show();
        Input.requestFocus();
// ajout d'un window adapter pour reagir si on ferme la fenetre
        addWindowListener(new WindowAdapter()
                          { public void windowClosing (WindowEvent e)
                          {
                              setVisible(false); dispose(); System.exit(0);
                          }
                          }
        );
//... (ouvrir la connexion sur le host/port du serveur)
        try {
            hote = InetAddress.getLocalHost();
            socket = new Socket(hote,port);
            out =new PrintWriter(socket.getOutputStream());
            in =new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Output.append("Connected successfully \n");


        }
        catch (IOException e){
            Output.append("Error : could not connect to server \n");
            e.printStackTrace();
        }
        Thread t=new Thread(this);
        t.start();
    }

    public void run () {
// boucle qui receptionne les messages du serveur
// et les affiche dans le textarea
        String msg;
        try {
            int compteur =0;
            while(compteur<10){
                msg = in.readLine();
                Output.append(msg+"\n");
                compteur++;
                out.println("j'ai fait "+compteur+" appels");
                try{
                    Output.append("pause de 2 secondes\n");
                    Thread.sleep(2000);
                }
                catch(InterruptedException e) {}

            }
            out.println("");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }


    }
    public void actionPerformed (ActionEvent e)
    {
        if (e.getSource()==Input)
        {
            String phrase=Input.getText();
//... (envoie au serveur)
// efface la zone de saisie
            out.println(phrase);
            out.flush();
            Input.setText("");
        }
    }
    protected void finalize()
    {
// Fermer ici toute les soquettes
        try{
            out.close();
            in.close();
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args)
    {
        InetAddress hote = null;
        int port = 1979;
        Socket socket = null;
//... ( gestion des parameters )
        ClientFenetre chatwindow = new ClientFenetre(hote, port);
    }}
