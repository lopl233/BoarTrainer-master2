import javax.net.ssl.SSLSocket;
import java.io.*;
import java.io.InputStream;
import javax.net.ssl.SSLSocketFactory;

public class SSLConnector {
    private static SSLConnector instance = null;
    public static SSLSocket sslsocket ;



    protected SSLConnector(){
        SSLSocketFactory sslsocketfactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
        try {
            //sslsocket = (SSLSocket)sslsocketfactory.createSocket("188.116.11.90", 7632);
            sslsocket = (SSLSocket)sslsocketfactory.createSocket("localhost", 7632);
        } catch (Exception e) {

            System.out.println(e.toString());
            System.out.println("Connection refused");
        }



    }
    public static SSLConnector getInstance()  {
        synchronized(SSLConnector.class){
            if(instance == null) {
                instance = new SSLConnector();
            }
            return instance;
        }
    }//koniec getInstance()


}//koniec klasy SSLConnector