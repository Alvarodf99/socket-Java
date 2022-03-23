/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sockets;

/**
 *
 * @author Alvaro
 */
import javax.swing.*;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Servidor {

    public static void main(String[] args) {
        // TODO Auto-generated method stub

        MarcoServidor mimarco = new MarcoServidor();

        mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }
}

class MarcoServidor extends JFrame implements Runnable {

    public MarcoServidor() {

        setBounds(1200, 300, 280, 350);

        JPanel milamina = new JPanel();

        milamina.setLayout(new BorderLayout());

        areatexto = new JTextArea();

        milamina.add(areatexto, BorderLayout.CENTER);

        add(milamina);

        setVisible(true);

        Thread hilo = new Thread(this);
        hilo.start();

    }

    private JTextArea areatexto;

    @Override
    public void run() { //Metodo abstracto de la interfaz Runnable

        String ip = "", nick = "", mensaje = "";
        ArrayList<String> listaIp = new ArrayList<String>();
                
        PaqueteEnvio paqueteRecibido; //Objeto de paqueteEnvio sin inicializar
        try {
            ServerSocket servidor = new ServerSocket(9999); //Socket que escucha el puerto 9999 siendo el servidor
            
            while (true) {

                Socket miSocket = servidor.accept(); //Creamos un objeto Socket inicializandolo como una escucha a la conexion de un socket y lo acepta

                //DETECTA ONLINE

                ObjectInputStream paqueteDatos = new ObjectInputStream(miSocket.getInputStream()); //Creacion de objeto de ObjectInputStream que recibe los datos del socket
                //paqueteRecibido = new PaqueteEnvio(); //Inicializacion de objeto de PaqueteEnvvio
                
                paqueteRecibido = (PaqueteEnvio) paqueteDatos.readObject(); //Inicializacion del objeto igualandolo a la lectura de los datos de paqueteDatos

                ip = paqueteRecibido.getIp();
                nick = paqueteRecibido.getNick();
                mensaje = paqueteRecibido.getMensaje();
                
                if (!mensaje.equals(" online")) 
                {
                    areatexto.append("\n" + nick + ": " + mensaje + ", para " + ip);
                    Socket enviaDestinatario = new Socket(ip, 9090);
                    ObjectOutputStream paqueteReenvio = new ObjectOutputStream(enviaDestinatario.getOutputStream());
                    paqueteReenvio.writeObject(paqueteRecibido);
                    paqueteReenvio.close();
                    miSocket.close();
                }
                else{
                    InetAddress localizacion = miSocket.getInetAddress();
                    String IpRemota = localizacion.getHostAddress();
                    System.out.println("Online" + IpRemota);
                    listaIp.add(IpRemota);
                    
                    paqueteRecibido.setIps(listaIp);
                    
                    for (String s : listaIp )
                    {
                        System.out.println("Array: " + s);
                        
                        Socket enviaDestinatario = new Socket(s, 9090);
                        ObjectOutputStream paqueteReenvio = new ObjectOutputStream(enviaDestinatario.getOutputStream());
                        paqueteReenvio.writeObject(paqueteRecibido);
                        
                        paqueteReenvio.close();
                        enviaDestinatario.close();
                        miSocket.close();
                        
                    }
              
                }
                
                //areatexto.append("\n" + nick + ": " + mensaje + ", para " + ip);
                
                
                Socket enviaDestinatario = new Socket(ip,9090);
                
                ObjectOutputStream paqueteReenvio = new ObjectOutputStream(enviaDestinatario.getOutputStream());
                paqueteReenvio.writeObject(paqueteRecibido);
                
                paqueteReenvio.close();
                enviaDestinatario.close();
                
                miSocket.close();

                
            }
        } catch (IOException | ClassNotFoundException ex) {

        }

    }
}
