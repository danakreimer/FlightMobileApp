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
	// CommandManager class.
	class CommandManager : ICommandManager
	{
		private readonly ITelnetClient telnetClient;
		private readonly BlockingCollection<AsyncCommand> queue;
		private string ip;
		private int portSocket;
		private bool parsePort;

		// Constructor.
		public CommandManager(ITelnetClient telnetClient, IConfiguration configur)
		{
			// AsyncCommand's queue.
			queue = new BlockingCollection<AsyncCommand>();
			this.telnetClient = telnetClient;
			// Getting port and ip from configuration.
			parsePort = Int32.TryParse(configur.GetValue<string>("Connect:PortSocket"), out portSocket);
			this.ip = configur.GetValue<string>("Connect:ip");
			// Connect by this ip and port.
			initialConnectAndRequest();
			Task.Factory.StartNew(SendInTread);
		}

		/*
		 * This method do the connection with the suitable ip and port
		 * and write "data" as initial request for beginning.
		 * Write an error message if there was a failure.
		 */
		public void initialConnectAndRequest()
		{
			string initRequest = "data\n";
			try
			{
				if (parsePort)
				{
					// Connect and send the initRequest.
					this.telnetClient.Connect(ip, portSocket);
					telnetClient.Write(initRequest);
				}
				else
				{
					// If it didn't secceed to parse port-Error.
					Console.WriteLine("Error: Connection failed\n");
				}
			}
			catch (Exception)
			{
				// Failure with connection.
				Console.WriteLine("Error: Connection failed\n");
			}
		}

		/*
		 *This method create new asyncCommand with the command it get,
		 * add to async queue and return it's task.
		*/
		public Task<Result> SendCommand(Command command)
		{
			var asyncCommand = new AsyncCommand(command);
			queue.Add(asyncCommand);
			// Return a task which it will await on.
			return asyncCommand.Task;
		}

		// This method go over asyncCommand in queue, set propreties and check if OK.
		public void SendInTread()
		{
			// Go over asyncCommand in queue
			foreach (AsyncCommand asyncCommand in queue.GetConsumingEnumerable())
			{
				Result result = Result.Ok;
				// Prepear the path for the set request for each value in command.
				string pathAileron = "/controls/flight/aileron";
				string pathElevator = "/controls/flight/elevator";
				string pathRudder = "/controls/flight/rudder";
				string pathThrottle = "/controls/engines/current-engine/throttle";
				// For each value send the path for set request and check if ok.
				if (!SetAndCheck(pathAileron, asyncCommand.Command.Aileron))
				{
					// If it returned false - the result isn't Ok.
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
				// Set if result is ok or not (if one of the set isn't ok) for this asyncCommand
				asyncCommand.Completion.SetResult(result);
			}
		}

		/*
		 * This method get a path for set/get request for a specific
		 * proprety in command and a new value we want to set with.
		 * It also check that the value that exists in the simulator
		 * matches the sent value and there in no error.
		 */
		public bool SetAndCheck(string path, double val)
		{
			double returnValue;
			bool response;
			string setRequest = "set " + path + " " + val + "\n";
			string getRequest = "get " + path + "\n";

			try
			{
				// Set request with the new value.
				telnetClient.Write(setRequest);
				// Get request to this spesific item we set.
				telnetClient.Write(getRequest);
				// Get a response.
				response = double.TryParse(telnetClient.Read().Replace("\n", ""), out returnValue);
				if (response)
				{
					// Check if the value that exists in the simulator matches the sent value.
					if (returnValue != val)
					{
						// There is no match - there is an error.
						return false;
					}
					else
					{
						// Match.
						return true;
					}
				}
				else
				{
					return false;
				}
			}
			// Catch exception and return false- there is an Error.
			catch (Exception)
			{
				return false;
			}
		}
	}
}

