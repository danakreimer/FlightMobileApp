using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace FlightMobileApp.Models
{
	public class Command
	{
		/**public Command(double aileron, double throtle, double rudder, double elevator)
		{
			Aileron = aileron;
			Throttle = throtle;
			Rudder = rudder;
			Elevator = elevator; 
		}**/

		[JsonProperty("aileron")]
		public double Aileron { get; set; }
		[JsonProperty("rudder")]
		public double Rudder { get; set; }
		[JsonProperty("elevator")]
		public double Elevator { get; set; }
		[JsonProperty("throttle")]
		public double Throttle { get; set; }
	}
}
