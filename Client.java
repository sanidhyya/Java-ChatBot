import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;

public class Client extends JFrame {
    Socket socket;
    BufferedReader br;
    PrintWriter out;

    //declare component 
    private JLabel heading = new JLabel("Client Area");
    private JTextArea messageArea = new JTextArea();
    private JTextField messageInput =  new JTextField();
    private Font font = new Font("Roboto",Font.PLAIN,20);

    public Client(){
        
        try{
            System.out.println("Sending request to server..");
            socket = new Socket("127.0.0.1",8080);
            System.out.println("Connection done");

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());
            
            createGUI();
            handleEvents();
            startReading();
            startWriting();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleEvents(){
        messageInput.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e){

            }
            @Override
            public void keyPressed(KeyEvent e){
                
            }

            @Override
            public void keyReleased(KeyEvent e){
                System.out.println("Key Released"+e.getKeyCode());
                if(e.getKeyCode() == 10){
                    //System.out.println("You have pressed enter button");
                    String contentToSend = messageInput.getText();
                    messageArea.append("Me :"+contentToSend+"\n");
                    out.println(contentToSend);
                    out.flush();
                    messageInput.setText("");
                }
            }
            
        });
    }

    private void createGUI(){
        this.setTitle("Client Messager[END]");
        this.setSize(500,600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //coding for component
        heading.setFont(font);
        messageArea.setFont(font);
        messageInput.setFont(font);

        heading.setIcon(new ImageIcon("s.png"));
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);
        heading.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        messageArea.setEditable(false);
        messageInput.setHorizontalAlignment(SwingConstants.CENTER);
        //frame ka layout set karenge
        this.setLayout(new BorderLayout());

        //adding the components to the frame
        this.add(heading, BorderLayout.NORTH);
        JScrollPane jscrollpane = new JScrollPane(messageArea);
        this.add(jscrollpane, BorderLayout.CENTER);
        this.add(messageInput, BorderLayout.SOUTH);
        this.setVisible(true);
    }


    public void startReading(){
        //thread - read kaarke deta rahega 
        Runnable r1 = ()->{
            System.out.println("Reader Started..");
            try{
            while (true) {
                
             String msg = br.readLine();
             if(msg.equals("exit")){
                System.out.println("Server terminated the chat");
                JOptionPane.showMessageDialog(this,"Server Terminated the chat");
                messageInput.setEnabled(false);

                socket.close();
                break;
             }
             //System.out.println("Server :"+msg);
             messageArea.append("Server"+msg+"\n");
            }
        }catch(Exception e){
            System.out.println("Connection is closed");
        }
        }; 
         new Thread(r1).start();
    }

    public void startWriting(){
        //thread - user se data lega and then send karega client tak
        Runnable r2 = ()->{
            System.out.println("Writer Started..");
                try {
            while(!socket.isClosed()){
            
                    //taking data from console
                    BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));

                    String content = br1.readLine();
                    out.println(content);
                    out.flush();
                    if(content.equals("exit")){
                        socket.close();
                        break;
                }
                
            }
            System.out.println("Connection closed..");
        }catch(Exception e){
            e.printStackTrace();
        }
        };
        new Thread(r2).start();
    }
    public static void main(String[] args) {
        System.out.println("this is client");
        new Client();
    }
}