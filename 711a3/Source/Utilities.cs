using System;
using System.Net;
using System.Net.Sockets;
using M = Message;

public class Utilities
{

    // End of message token
    public static String EOM = "<EOM>";
    // Seperator token
    public static String SEP = "<SEP>";

    // Determine the IP address of the localhost
    public static IPAddress determineIP()
    {
        IPHostEntry ipHostInfo = Dns.GetHostEntry(Dns.GetHostName());
        IPAddress address = null;
        foreach (IPAddress ip in ipHostInfo.AddressList)
        {
            if (ip.AddressFamily == AddressFamily.InterNetwork)
            {
                address = ip;
                break;
            }
        }
        return address;
    }
}
