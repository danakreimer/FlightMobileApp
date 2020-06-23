using FlightMobileApp.Models;
using FlightMobileApp.Utils;
using System;
using System.Data;
using System.Net;
using Microsoft.Extensions.Configuration;
using System.Collections.Concurrent;
using System.Threading.Tasks;

namespace FlightMobileApp.Models
{
	
	class CommandManager : ICommandManager
	{
		private readonly ITelnetClient telnetClient;
		private readonly BlockingCollection<AsyncCommand> queue;
		private string ip;
		private int portSocket;
		private bool parsePort;

		public CommandManager(ITelnetClient telnetClient, IConfiguration configur)
		{
			queue = new BlockingCollection<AsyncCommand>();
			this.telnetClient = telnetClient;
			parsePort = Int32.TryParse(configur.GetValue<string>("Connect:PortSocket"), out portSocket);
			this.ip = configur.GetValue<string>("Connect:ip");
			initialConnectAndRequest();
			Task.Factory.StartNew(SendInTread);
		}

		public void initialConnectAndRequest()
		{
			if (parsePort)
			{
				this.telnetClient.Connect(ip, portSocket);
			}
			string initRequest = "data\n";
			telnetClient.Write(initRequest);
		}

		public Task<Result> SendCommand(Command command)
		{
			var asyncCommand = new AsyncCommand(command);
			queue.Add(asyncCommand);
			return asyncCommand.Task;
		}

		public void SendInTread()
		{
			foreach(AsyncCommand asyncCommand in queue.GetConsumingEnumerable())
			{
				Result result = Result.Ok;
				string pathAileron = "/controls/flight/aileron";
				string pathElevator = "/controls/flight/elevator";
				string pathRudder = "/controls/flight/rudder";
				string pathThrottle = "/controls/engines/current-engine/throttle";
				if (!SetAndCheck(pathAileron, asyncCommand.Command.Aileron))
				{
					result = Result.NotOk;
				}
				if (!SetAndCheck(pathElevator, asyncCommand.Command.Elevator))
				{
					result = Result.NotOk;
				}
				if (!SetAndCheck(pathRudder, asyncCommand.Command.Rudder))
				{
					result = Result.NotOk;
				}
				if (!SetAndCheck(pathThrottle, asyncCommand.Command.Throttle))
				{
					result = Result.NotOk;
				}
				asyncCommand.Completion.SetResult(result);
			}
		}


		public bool SetAndCheck(string path, double val)
		{
			string setRequest = "set " + path + " " + val + "\n";
			string getRequest = "get " + path + "\n";
			telnetClient.Write(setRequest);
			telnetClient.Write(getRequest);
			double returnValue;

			bool response = double.TryParse(telnetClient.Read().Replace("\n", ""), out returnValue);
			if (response)
			{
				if (returnValue != val)
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

