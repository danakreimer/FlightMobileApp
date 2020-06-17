using FlightMobileApp.Models;
using FlightMobileApp.Utils;
using System;
using System.Data;
using System.Net;

namespace FlightMobileApp.Models
{
	
	class CommandManager : ICommandManager
	{
		private ITelnetClient telnetClient;
		public CommandManager(ITelnetClient telnetClient)
		{
			this.telnetClient = telnetClient;
			IPAddress ip;
			bool parseIP = IPAddress.TryParse("127.0.0.1", out ip);
			if (parseIP)
			{
				this.telnetClient.Connect(ip, 5401, new Action(() => { }));
			}
			

		}
		public void SendCommand(Command command)
		{
			string pathAileron = "/controls/flight/aileron";
			string pathElevator = "/controls/flight/elevator";
			string pathRudder = "/controls/flight/rudder";
			string pathThrottle = "/controls/engines/current-engine/throttle";
			if(!SetAndCheck(pathAileron, command.Aileron))
			{
				throw new Exception();
			}
			if(!SetAndCheck(pathElevator, command.Elevator))
			{
				throw new Exception();
			}
			if(!SetAndCheck(pathRudder, command.Rudder))
			{
				throw new Exception();
			}
			if (!SetAndCheck(pathThrottle, command.Throttle))
			{
				throw new Exception();
			}

		}
		public void GetScreenshot()
		{
			////////////////////////////////////////////////////////////////////////////////////////////
		}
		public bool SetAndCheck(string path, double val)
		{
			string setRequest = "set " + path + " " + val + " \n";
			string getRequest = "get " + path + " \n";
			telnetClient.Write(setRequest);
			telnetClient.Write(getRequest);
			double returnValue;
			bool response = double.TryParse(telnetClient.Read().Replace("\n", ""), out returnValue);
			if (response)
			{
				if(returnValue != val)
				{
					return false;
				}
				else
				{
					return true;
				}
			}
			else
			{
				return false;
			}
		}
	}
}

