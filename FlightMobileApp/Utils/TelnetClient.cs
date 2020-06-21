using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Net;
using System.Net.Sockets;
using System.ComponentModel;
using System.Threading;

namespace FlightMobileApp.Utils
{
    // MyTelnetClient Class.
    public class TelnetClient : ITelnetClient
    {
        private TcpClient client;
        private NetworkStream stream;

        // Constructor.
        public TelnetClient() { }

        // Connect to server.
        public void Connect(string ip, int port)
        {
            // Create a TcpClient.
            this.client = new TcpClient(ip, port);
            // Get a client stream for reading and writing.
            this.stream = client.GetStream();
            Write("date\n");
        }

        // Write message to server.
        public void Write(string command)
        {
            // Translate the passed command into ASCII and store it as a Byte array.
            byte[] data = System.Text.Encoding.ASCII.GetBytes(command);
            // Send the command to the connected TcpServer.
            stream.Write(data, 0, data.Length);
            // Write the command sent to the console.
            Console.WriteLine("Sent: {0}", command);
        }

        // Receive server response.
        public string Read()
        {
            // Buffer to store the response bytes.
            byte[] data = new byte[256];
            // String to store the response ASCII representation.
            string responseData;
            // Read the first batch of the TcpServer response bytes.
            int bytes = stream.Read(data, 0, data.Length);
            responseData = System.Text.Encoding.ASCII.GetString(data, 0, bytes);
            // Write the response received to the console.
            Console.WriteLine("Received: {0}", responseData);
            return responseData;
        }

        // Disconnect from server.
        public void Disconnect()
        {
            // Close everything.
            stream.Close();
            client.Close();
        }
    }
}
