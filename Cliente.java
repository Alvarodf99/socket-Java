/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sockets;

import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import java.net.*;
import java.util.ArrayList;

/**
 *
 * @author Alvaro
 */
public class Cliente {
    
    

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        MarcoCliente mimarco = new MarcoCliente();
        mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

class MarcoCliente extends JFrame {

    public MarcoCliente() {
        setBounds(600, 300, 280, 350);
        LaminaMarcoCliente milamina = new LaminaMarcoCliente();
        add(milamina);
        setVisible(true);
        
        addWindowListener(new EnvioOnline());
    }
}

class EnvioOnline extends WindowAdapter{
    
    @Override
    public void windowOpened(WindowEvent e){
        try {
            Socket miSocket = new Socket("192.168.0.10",9999);
            PaqueteEnvio datos = new PaqueteEnvio();
            datos.setMensaje(" online");
            ObjectOutputStream paqueteDatos = new ObjectOutputStream(miSocket.getOutputStream());
            paqueteDatos.writeObject(datos);
            miSocket.close();
            
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }
}

class LaminaMarcoCliente extends JPanel implements Runnable {

    public LaminaMarcoCliente() {
        
        String nickUsuario = JOptionPane.showInputDialog("Nick: ");
        
        JLabel lNick = new JLabel("Nick: ");

        add(lNick);
         
        nick = new JLabel();
        nick.setText(nickUsuario);
        add(nick);

        JLabel texto = new JLabel("Online: ");
        add(texto);

        ip = new JComboBox();
       /* ip.addItem("Usuario 1");
        ip.addItem("Usuario 2");
        ip.addItem("Usuario 3");*/
        //ip.addItem("192.168.24.163");
        //ip.addItem("192.168.24.164");

        
        add(ip);

        campoChat = new JTextArea(12, 20);
        add(campoChat);

        campo1 = new JTextField(20);
        add(campo1);

        miboton = new JButton("Enviar");
        EnviaTexto mievento = new EnviaTexto();
        miboton.addActionListener(mievento);
        add(miboton);
       

        Thread hilo = new Thread(this);
        hilo.start();
    }

    @Override
    public void run() {
        try {

            ServerSocket servidor = new ServerSocket(9090);
            ArrayList<String> listaIp = new ArrayList<String>();

            Socket cliente;
            PaqueteEnvio paqueteRecibido;

            while (true) {

                cliente = servidor.accept();

                paqueteRecibido = new PaqueteEnvio();

                ObjectInputStream recibido = new ObjectInputStream(cliente.getInputStream());

                paqueteRecibido = (PaqueteEnvio) recibido.readObject();

                
                if (!paqueteRecibido.getMensaje().equals(" online")) {
                    
                    campoChat.append(paqueteRecibido.getNick() + ": " + paqueteRecibido.getMensaje() + "\n");

                }
                else
                {
                    //campoChat.append("\n" + paqueteRecibido.getIp());
                    
                    ArrayList <String> IpsMenu = new ArrayList<String>();
                    IpsMenu = paqueteRecibido.getIps();
                    
                    ip.removeAllItems();
                    
                    for (String s: IpsMenu) {
                        ip.addItem(s);
                    }

                }

                //recibido.close();
                //cliente.close();

            }

        } catch (Exception e) {
        }
    }

    private class EnviaTexto implements ActionListener { //Clase privada que se llama enviaTexto y que implementa la interfaz ActionListener

        @Override
        public void actionPerformed(ActionEvent e) {//Declaramos metodo abstracto de la interfaz
            
            
            campoChat.append("Yo: " + campo1.getText() + "\n");
            try {

                Socket miSocket = new Socket("26.8.153.36", 9999); //Creamos un objeto de la clase Socket, y le pasamos a su constructor nuestra ip y un puerto en desuso

                PaqueteEnvio datos = new PaqueteEnvio(); //creamos objeto de la clase PaqueteEnvio
                datos.setNick(nick.getText()); //Usamos el metodo setNick para guardar la informacion del TextField nick
                datos.setIp(ip.getSelectedItem().toString()); //Usamos el metodo setNick para guardar la informacion del TextField ip
                datos.setMensaje(campo1.getText()); //Usamos el metodo setNick para guardar la informacion del TextField campo1

                ObjectOutputStream paqueteDatos = new ObjectOutputStream(miSocket.getOutputStream()); //Creamos objeto de la clase ObjectOutputStream que escribe los datos del socket

                paqueteDatos.writeObject(datos); //Escribe en el objeto paqueteDatos el objeto paqueteEnvio

                paqueteDatos.close(); //Se cierra el ObjectoutputStream

                miSocket.close(); //Se cierra el Socket
                
                campo1.setText("");

            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }

            // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    private JTextField campo1;
    private JComboBox ip;
    private JTextArea campoChat;
    private JButton miboton;
    private JLabel nick;

}

class PaqueteEnvio implements Serializable { //Implementa la interfaz seralizable para transformar los datos a bytes

    private String nick, ip, mensaje;
    private ArrayList<String> Ips;

    
    public ArrayList<String> getIps() {
        return Ips;
    }

    public void setIps(ArrayList<String> Ips) {
        this.Ips = Ips;
    }
    public PaqueteEnvio() {

    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

}
