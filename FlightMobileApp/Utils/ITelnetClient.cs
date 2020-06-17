using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.ComponentModel;
using System.Net;

namespace FlightMobileApp.Utils
{
    interface ITelnetClient : INotifyPropertyChanged
    {
        string ErrorMessage { get; set; }
        bool IsConnected { get; set; }
        void Connect(IPAddress ip, int port, Action onConnected);
        void Write(string command);
        string Read();
        void Disconnect();
    }
}
