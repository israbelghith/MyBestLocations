package israa.belghith.mybestlocations;

public class Config {
    /*
    Ip serveur :
    10.0.2.2
    ipv4: 192.168... LAN
    www... hebergé
     */
    public  static  String IPServeur="192.168.9.179";//192.168.8.103;
    public  static String URL_GETALL="http://"+IPServeur+":81/servicephp/getall.php";
    public  static String URL_ADDPOSITION="http://"+IPServeur+":81/servicephp/addposition.php";
}
