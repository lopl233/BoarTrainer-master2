import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.io.InputStream;
import javax.net.ssl.SSLSocketFactory;

public class socketSSL {
    private static socketSSL instance = null;
    private static SSLServerSocket sslserversocket;



    protected socketSSL(){
        SSLServerSocketFactory sslserversocketfactory =
                (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
        try {
            sslserversocket =
                    (SSLServerSocket)sslserversocketfactory.createServerSocket(7632);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static SSLSocket AceptSSL(){
        SSLSocket sslsocket = null;
        try {
            sslsocket = (SSLSocket)sslserversocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sslsocket;
    }

    public static socketSSL getInstance()  {
        synchronized(socketSSL.class){
            if(instance == null) {
                instance = new socketSSL();
            }
            return instance;
        }
    }//koniec getInstance()


}//koniec klasy socketSSL