using FlightMobileApp.Models;
using System.Drawing.Imaging;

using System.Drawing;

using System;

namespace FlightMobileApp.Models
{
    public interface ICommandManager
    {
        public void SendCommand(Command command);
        public void GetScreenshot();
    }
}

