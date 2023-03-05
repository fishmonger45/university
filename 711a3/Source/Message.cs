
using System;
using U = Utilities;
public enum MessageType
{
    Normal, // Normal message
    Propose, // Propose timestamp
    Accept, // Accept timestamp
}

public class Message
{
    public MessageType type;
    public int originId;
    public int sequenceNumber;
    public int timestamp;
    public String body;
    public Boolean isFinalized;

    public Message(MessageType type, int originId, int sequenceNumber, int timestamp, String body, Boolean isFinalized)
    {
        this.type = type;
        this.originId = originId;
        this.sequenceNumber = sequenceNumber;
        this.timestamp = timestamp;
        this.body = body;
        this.isFinalized = isFinalized;
    }
    public Message(MessageType type, int originId, int sequenceNumber, int timestamp, Boolean isFinalized)
    {
        this.type = type;
        this.originId = originId;
        this.sequenceNumber = sequenceNumber;
        this.timestamp = timestamp;
        this.body = String.Format("Msg #{0} from Middleware {1}", this.sequenceNumber, this.originId);
        this.isFinalized = isFinalized;
    }

    public String serializeString()
    {
        return String.Format("{0}{6}{1}{6}{2}{6}{3}{6}{4}{6}{5}{6}{7}",
            this.body, this.type, this.originId, this.sequenceNumber, this.timestamp, this.isFinalized, U.SEP, U.EOM);
        
    }

    public byte[] serializeBytes()
    {
        String message = this.serializeString();
        return System.Text.Encoding.ASCII.GetBytes(message);
    }

    public static Message deserialize(String payload)
    {
        String[] xs = payload.Split(new string[] { U.SEP }, StringSplitOptions.None);
        String body = xs[0];
        MessageType type = (MessageType)Enum.Parse(typeof(MessageType), xs[1], true);
        int originId = Int32.Parse(xs[2]);
        int sequenceNumber = Int32.Parse(xs[3]);
        int timestamp = Int32.Parse(xs[4]);
        Boolean isFinalized = bool.Parse(xs[5]);
        return new Message(type, originId, sequenceNumber, timestamp, body, isFinalized);
    }
}