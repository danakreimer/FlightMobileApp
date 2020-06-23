using FlightMobileApp.Models;
using System.Drawing.Imaging;

using System.Drawing;

using System;
using System.Threading.Tasks;

namespace FlightMobileApp.Models
{
    public interface ICommandManager
    {
        public Task<Result> SendCommand(Command command);
    }
}

