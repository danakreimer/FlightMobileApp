using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;

namespace FlightMobileApp.Utils
{
    class SocketTimeoutException : SocketException
    {
        public SocketTimeoutException() : base(10060) { }
    }
}
