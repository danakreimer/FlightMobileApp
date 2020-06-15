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
    public class TelnetClient : ITelnetClient
    {
        public event PropertyChangedEventHandler PropertyChanged;

        private Socket socket;

        private readonly int timeoutTime = 10000000;

        // Data buffer for incoming data.  
        private byte[] bytes = new byte[1024];

        private string errorMessage = String.Empty;
        public string ErrorMessage
        {
            get
            {
                return this.errorMessage;
            }

            set
            {
                this.errorMessage = value;
                NotifyPropertyChanged("ErrorMessage");
            }
        }

        private bool isConnected = false;
        public bool IsConnected
        {
            get
            {
                return this.isConnected;
            }

            set
            {
                this.isConnected = value;
                NotifyPropertyChanged("IsConnected");
            }
        }

        public void Connect(IPAddress iPAddress, int port, Action onConnected)
        {
            new Thread(() =>
            {
                // Establish the remote endpoint for the socket
                IPHostEntry ipHostInfo = Dns.GetHostEntry(Dns.GetHostName());

                // Convert the port
                IPEndPoint remoteEP = new IPEndPoint(iPAddress, port);

                // Create a socket
                Socket socket = new Socket(iPAddress.AddressFamily,
                    SocketType.Stream, ProtocolType.Tcp);

                // Try to connect the socket to the remote endpoint
                try
                {
                    socket.Connect(remoteEP);
                    IsConnected = true;
                    ErrorMessage = String.Empty;
                    this.socket = socket;
                    onConnected();
                }
                catch (SocketException)
                {
                    IsConnected = false;
                    ErrorMessage = $"Failed connecting to socket - {iPAddress}:{port}";
                }
            }).Start();
        }

        public void Disconnect()
        {
            // Release the socket if it's still connected
            if (this.socket.Connected)
            {
                this.socket.Shutdown(SocketShutdown.Both);
                this.socket.Close();
            }

            IsConnected = false;
        }

        public string Read()
        {
            bool canRead = this.socket.Poll(timeoutTime, SelectMode.SelectRead);
            if (!canRead)
            {
                throw new SocketTimeoutException();
            }

            int bytesRec = this.socket.Receive(this.bytes);

            return Encoding.ASCII.GetString(bytes, 0, bytesRec);
        }

        public void Write(string command)
        {
            bool canWrite = this.socket.Poll(timeoutTime, SelectMode.SelectWrite);
            if (!canWrite)
            {
                throw new SocketTimeoutException();
            }

            // Encode the data string into a byte array.  
            byte[] msg = Encoding.ASCII.GetBytes(command);

            // Send the data through the socket.  
            this.socket.Send(msg);
        }

        public void NotifyPropertyChanged(string propName)
        {
            if (this.PropertyChanged != null)
                this.PropertyChanged(this, new PropertyChangedEventArgs(propName));
        }
    }
}
