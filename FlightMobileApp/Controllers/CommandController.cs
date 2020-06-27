using FlightMobileApp.Models;
using Microsoft.AspNetCore.Http;
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
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        public async Task<ActionResult> SendCommandToSimulator(Command command)
        {
            try
            {
                Task<Result> result;
                result = commandManager.SendCommand(command);
                result.Wait();
                if (result.Result == Result.NotOk)
                {
                    return await Task.FromResult(BadRequest());
                }
                else
                {
                    return await Task.FromResult(Ok());
                }
            }
            catch
            {
                return await Task.FromResult(StatusCode(500));
            }
        }
    }
}
