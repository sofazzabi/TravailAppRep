import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

class ClientFenetre extends JFrame implements Runnable,ActionListener{
TextArea Output;
TextArea List;
JTextField Input;
Socket socket = null;
BufferedReader in = null;
PrintWriter out = null;
boolean connected;
JLabel welcome_label;
JButton send_button;
JButton clear_button;

public ClientFenetre(InetAddress hote, int port){
// mise en forme de la fenetre (frame)
super("Java Chat");
setSize(500,700);
setLocation(400,200);
setLayout(new BorderLayout());
Container frame_cont = getContentPane();
// ajout d'un window adapter pour reagir si on ferme la fenetre
addWindowListener(new WindowAdapter ()
{ public void windowClosing (WindowEvent e)
{
    if(connected==true){
            out.println("exit");out.flush(); }
    else {
            setVisible(false); dispose(); System.exit(0);
    }
}
}
);
//North Panel
JPanel P_north=new JPanel(new FlowLayout(FlowLayout.CENTER));
welcome_label=new JLabel("Welcome");
P_north.add(welcome_label);
P_north.setSize(500,100);
frame_cont.add(P_north,BorderLayout.NORTH);
//South Panel
JPanel P_south=new JPanel();
Input=new JTextField(50);
Input.addActionListener(this);
Input.disable();
ImageIcon send_img = new ImageIcon("send.jpg");
ImageIcon clear_img = new ImageIcon("clear.jpg");
send_button = new JButton(send_img);
send_button.setMargin(new Insets(0,0,0,0));
send_button.addActionListener(this);
clear_button = new JButton(clear_img);
clear_button.setMargin(new Insets(0,0,0,0));
clear_button.addActionListener(this);
P_south.add(Input);
P_south.add(clear_button);
P_south.add(send_button);
frame_cont.add(P_south,BorderLayout.SOUTH);
//Center Panel
frame_cont.add(List=new TextArea(10,20),BorderLayout.EAST);
frame_cont.add(Output=new TextArea(),BorderLayout.CENTER);
Output.setEditable(false);
List.setEditable(false);
List.setFocusable(false);
Output.setBackground(new Color(255,255,255));
pack();
this.setMinimumSize(new Dimension(getWidth(),getHeight()));
setVisible(true);
Input.requestFocus();
//ouvrir la connexion sur le host/port du serveur
try{
    hote=InetAddress.getLocalHost();
    socket=new Socket(hote,port);
    out = new PrintWriter(socket.getOutputStream());
    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    connected=true;
    Output.append("Connected to server...\n");
}
catch (IOException e ) {Output.append("Error: could not connect to server.\n");Input.setEnabled(false);connected=false;e.printStackTrace();}
//run thread
Thread t=new Thread(this);
if(connected==true) t.start();
}

public void run (){
    String msg;
// boucle qui receptionne les messages du serveur
// et les affiche dans le textarea
try{
//Saisie du nom
    while(true){
         get_name();
         msg = in.readLine();
         if(msg.equals("::ok::"))break;
         Output.append(msg+"\n");
         }
//message d'accueil    
    msg=in.readLine();
    welcome_label.setText(msg);
    Input.enable();
//reception des messages
    while(true){
                      msg=in.readLine();
                      if(msg.equals("::list::")) get_list();
                      else if(msg.equals("::update::")){out.println("list");out.flush();}
                      else if(msg.equals("::end::"))break;
                      else Output.append(msg+"\n");         
         }
    setVisible(false); dispose(); System.exit(0);
    }
    catch (IOException e ) {e.printStackTrace();}
}

public void actionPerformed (ActionEvent e){
if (e.getSource()==Input || e.getSource()==send_button)
{
String phrase=Input.getText();
// envoie au serveur
// efface la zone de saisie
if(phrase.equals("")==false){
    out.println(phrase);
    out.flush();
    }
Input.setText("");
}
if (e.getSource()==clear_button)
{

    Output.setText("");
}
}

private void get_name(){
    String name;
    while(true){
    name=JOptionPane.showInputDialog(this,"Enter your Name","Input",JOptionPane.QUESTION_MESSAGE);
    if(name!=null)
    if(name.equals("")==false )break;
    }
    out.println(name);
    out.flush();
}

private void get_list(){
try{ 
    List.setText("");
    while(true){
    String msg = in.readLine();
    if(msg.equals("::end_list::"))break;
    List.append(msg+"\n");
    }
}
catch (IOException e ) {e.printStackTrace();}
}

protected void finalize(){
// Fermer ici toute les soquettes
try{    
    out.close();
    in.close();
    socket.close();
    }
catch (IOException e ) {e.printStackTrace();}
}

public static void main(String[] args){
InetAddress hote = null;
int port = 1973; // par defaut
//... ( gestion des parametres )
ClientFenetre chatwindow = new ClientFenetre(hote, port);
}

}