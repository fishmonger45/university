using System;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;
using System.Collections.Generic;
using System.ComponentModel;
using U = Utilities;
using M = Message;

namespace Assignment
{
    public partial class Middleware
    {
        public int sequenceNumber;
        public int timestamp;

        public List<ValueTuple<Message, int[]>> holding;

        public readonly static int NETWORK_PORT = 8081;
        public static int NUMBER_NODES = 5;

        // Increment and return a sequence number 
        public int generateSequenceNumber()
        {
            this.sequenceNumber++;
            return sequenceNumber;
        }

        // Increment and return a timestamp
        public int generateTimeStamp()
        {
            this.timestamp++;
            return this.timestamp;
        }

        // Create and multicast send a message to the network for it to deliver
        public async void createAndSendMessage()
        {
            try
            {
                // Connect to network
                Socket socket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
                IPAddress ipAddress = U.determineIP();
                IPEndPoint networkEP = new IPEndPoint(ipAddress, NETWORK_PORT);
                socket.Connect(networkEP);

                // Create message with new (incremented) sequence number and timestamp
                Message message = new Message(MessageType.Normal, mid, this.generateSequenceNumber(), generateTimeStamp(), false);
                byte[] payload = message.serializeBytes();

                // Send message, shutdown and close connection
                socket.Send(payload);
                socket.Shutdown(SocketShutdown.Both);
                socket.Close();

                // Add message to the end of the sent list
                this.listBoxSent.Items.Add(message.body);
                // Add message to holding list
                this.holding.Add(new ValueTuple<Message, int[]>(message, new int[] { this.timestamp, 1 }));
            }
            catch (Exception e)
            {
                Console.WriteLine("Unexpected exception : {0}", e.ToString());
            }
        }

        // This method sets up a socket for receiving messages from the Network
        public async void ReceiveMulticast()
        {
            // Data buffer for incoming data.
            byte[] payload = new Byte[1024];

            // Determine the IP address of localhost
            IPAddress ipAddress = U.determineIP();
            IPEndPoint localEndPoint = new IPEndPoint(ipAddress, mport);

            // Create a TCP/IP socket for receiving message from the Network.
            TcpListener listener = new TcpListener(localEndPoint);
            listener.Start(10);

            try
            {
                string message = null;

                // Start listening for connections.
                while (true)
                {
                    Console.WriteLine("Waiting for a connection...");

                    // Program is suspended while waiting for an incoming connection.
                    TcpClient tcpClient = await listener.AcceptTcpClientAsync();

                    Console.WriteLine("connected");
                    message = null;

                    // Receive one message from the network
                    while (true)
                    {
                        payload = new byte[1024];
                        NetworkStream readStream = tcpClient.GetStream();
                        int bytesRec = await readStream.ReadAsync(payload, 0, 1024);
                        message += Encoding.ASCII.GetString(payload, 0, bytesRec);

                        // All messages ends with "<EOM>"
                        // Check whether a complete message has been received
                        if (message.IndexOf(U.EOM) > -1)
                        {
                            break;
                        }
                    }

                    // handle message
                    messageHook(message);
                }

            }
            catch (Exception e)
            {
                Console.WriteLine(e.ToString());
            }
        }

        // Hook when a message is recieved such that it can be handled
        public void messageHook(String payload)
        {
            Message recievedMessage = M.deserialize(payload);
            switch (recievedMessage.type)
            {
                case MessageType.Normal:
                    // only accept message if it's not sent by you
                    if (recievedMessage.originId != mid)
                    {
                        // Generate a timestamp for this message
                        recievedMessage.timestamp = generateTimeStamp();
                        // Add message with new timestamp to holding queue with one confirmation
                        this.holding.Add(new ValueTuple<Message, int[]>(recievedMessage, new int[] { recievedMessage.timestamp, 1 }));
                        // Display message as recieved
                        this.listBoxRecieved.Items.Add(recievedMessage.body);
                        // Propose a timestamp for the message
                        proposeTimestamp(recievedMessage);
                    }
                    else
                    {
                        // Skip proposal if recieved message comes from you
                        this.listBoxRecieved.Items.Add(recievedMessage.body);
                    }
                    // Send all ready messages
                    sendHolding();
                    break;

                case MessageType.Propose:
                    // Only message sender can accept this propose message
                    if (mid == recievedMessage.originId)
                    {
                        // Find sent message refered to by propose message
                        int tupleIndex = this.findTuple(recievedMessage);
                        ValueTuple<Message, int[]> tuple = this.holding[tupleIndex];
                        // Increase maximum observed timestamp
                        tuple.Item2[0] = Math.Max(tuple.Item2[0], recievedMessage.timestamp);
                        // Increase number of proposals recieved
                        tuple.Item2[1]++;
                        // Update holding
                        this.holding[tupleIndex] = tuple;

                        Console.WriteLine("Number of proposals for message: {0}", tuple.Item2);
                        // All confirmations recieved for message, start accepting it
                        if (tuple.Item2[1] == NUMBER_NODES)
                        {
                            // Finalize the timestamp of the message to the maximum observed
                            tuple.Item1.timestamp = tuple.Item2[0];
                            // Update with new timestamp
                            this.holding[tupleIndex] = tuple;
                            // Only used for message lookup (recieve message used for lookup)
                            acceptTimestamp(recievedMessage);
                        }
                        Console.WriteLine("received timestamp:{0}", recievedMessage.timestamp);
                    }
                    else
                    {
                        // Only accept a proposal that isn't from you
                        Console.WriteLine("skipping propose message (not for me)");
                    }
                    // Send all avaliable messages
                    sendHolding();
                    break;

                case MessageType.Accept:
                    int tupleIndex2 = this.findTuple(recievedMessage);
                    ValueTuple<Message, int[]> tuple2 = this.holding[tupleIndex2];
                    // Update to finalized timestamp
                    tuple2.Item1.timestamp = recievedMessage.timestamp;
                    // Mark message as finalized and ready to send
                    tuple2.Item1.isFinalized = true;
                    // Update message in holding
                    this.holding[tupleIndex2] = tuple2;
                    // Update node timestamp to be the maximum recieved
                    this.timestamp = Math.Max(this.timestamp, tuple2.Item1.timestamp);
                    // Message from holding has changed, now update the holding with finalized message
                    sortHolding();
                    // Send all ready messages
                    sendHolding();
                    break;

                default:
                    // Enumueration of message types not met
                    Console.WriteLine("Unexpected message type");
                    Environment.Exit(1);
                    break;

            }

        }

        // Propose a timestamp using the messages timestamp
        public void proposeTimestamp(Message message)
        {
            Socket socket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            IPAddress ipAddress = U.determineIP();
            IPEndPoint networkEP = new IPEndPoint(ipAddress, NETWORK_PORT);
            socket.Connect(networkEP);

            Message proposeMessage = new Message(MessageType.Propose, message.originId, message.sequenceNumber, message.timestamp, message.body, false);

            socket.Send(proposeMessage.serializeBytes());
            socket.Shutdown(SocketShutdown.Both);
            socket.Close();
        }

        // Accept a timestamp using the messages final timestamp
        public void acceptTimestamp(Message message)
        {
            Socket socket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            IPAddress ipAddress = U.determineIP();
            IPEndPoint networkEP = new IPEndPoint(ipAddress, NETWORK_PORT);
            socket.Connect(networkEP);


            int tupleIndex = this.findTuple(message);
            // This is the new updated timestamp from proposal
            ValueTuple<Message, int[]> tuple = this.holding[tupleIndex];
            Message acceptMessage = new Message(MessageType.Accept, message.originId, message.sequenceNumber, tuple.Item1.timestamp, message.body, false);

            socket.Send(acceptMessage.serializeBytes());
            socket.Shutdown(SocketShutdown.Both);
            socket.Close();
        }

        // Find a tuple index via using the tuple body as a unique identifier
        public int findTuple(Message proposal)
        {
            ValueTuple<Message, int[]>[] list = this.holding.ToArray();
            for (int i = 0; i < list.Length; i++)
            {
                if (proposal.body == list[i].Item1.body)
                {
                    return i;
                }
            }
            return -1;
        }

        // Sort the holding queue using the timestamp primarily and ids as a secondary
        public void sortHolding()
        {
            this.holding.Sort((t1, t2) =>
            {
                if (t1.Item1.timestamp < t2.Item1.timestamp)
                {
                    return -1;
                }
                else if (t2.Item1.timestamp < t1.Item1.timestamp)
                {
                    return 1;
                }
                else
                {
                    // Timestamps are equal
                    if (t1.Item1.originId < t2.Item1.originId)
                    {
                        return -1;
                    }
                    else if (t2.Item1.originId < t1.Item1.originId)
                    {
                        return 1;
                    }
                    else
                    {
                        // Conflicting case.
                        // This case occurs when the message that has been accepted has been accepted to a timestamp that is already in use by the system,
                        // This should only cause conflict on the message origin machine and a deterministic way of setting this up is that the accepted message should be after
                        // the unaccepted one so that the unaccepted one gets a different timestamp.
                        return 1;
                    }
                }
            });
        }

        // See if there is anything to add to the sent list (pop head continuously until you can't pop no more)
        public void sendHolding()
        {
            while (true)
            {
                if (this.holding.Count > 0)
                {
                    ValueTuple<Message, int[]> tuple = this.holding[0];
                    if (tuple.Item1.isFinalized)
                    {
                        // Pop from the head of the list
                        this.holding.RemoveAt(0);
                        // Add to sent messages
                        this.listBoxReady.Items.Add(tuple.Item1.body);
                        Console.WriteLine("head message finalized: {0}", tuple.Item1.serializeString());
                    }
                    else
                    {
                        // Only remove a message from the holding if the head is finalized
                        break;
                    }
                }
                else
                {
                    break;
                }
            }
        }
    }

}