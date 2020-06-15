using FlightMobileApp.Models;
using Microsoft.AspNetCore.Mvc;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace FlightMobileApp.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class CommandController : ControllerBase
    {
        private readonly ICommandManager commandManager;
        // Constructor
        public CommandController(ICommandManager commandManager)
        {
            this.commandManager = commandManager;
        }

        // POST: api/command
        [HttpPost(Name = "SendCommandToSimulator")]
        public void SendCommandToSimulator([FromBody] Command command)
        {
            try
            {
                commandManager.SendCommand(command);
                // TODO: return success status///////////////////////////////////////////////////
            }
            catch (Exception)
            {
                // TODO: return failier status./////////////////////////////////////////////////
            }
        }
    }
}
