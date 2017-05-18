
public class serwer{

    public static void main(String[] args)  {
        String currentDir = System.getProperty("user.dir")+"/testkeysore.p12";
        System.setProperty("javax.net.ssl.keyStore",currentDir);
        System.setProperty("javax.net.ssl.keyStorePassword","dzikidzik");
        System.setProperty("javax.net.ssl.keyStoreType","PKCS12");
        System.setProperty("javax.net.ssl.trustStore",currentDir);
        System.setProperty("javax.net.ssl.trustStorePassword","dzikidzik");
        System.setProperty("javax.net.ssl.trustStoreType","PKCS12");






        socketSSL socketssl = socketSSL.getInstance();
        while(true) {
            new SupportSSL(socketssl.AceptSSL()).start();
            System.out.println("Wow wow nowy klient");
        }
    }

}