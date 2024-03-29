﻿using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace FlightMobileApp.Models
{
	// Command class.
	public class Command
	{
		// The propreties of each command.
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
