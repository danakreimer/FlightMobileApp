using FlightMobileApp.Models;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace FlightMobileApp.Controllers
{
    // CommandController class.
    [Route("api/[controller]")]
    [ApiController]
    public class CommandController : ControllerBase
    {
        private readonly ICommandManager commandManager;

        // Constructor.
        public CommandController(ICommandManager commandManager)
        {
            this.commandManager = commandManager;
        }

        /*
         * This asyncronic method gets a command and send it to commandManager
         * for setting the propreties values. It get a task result as a response
         * if the set was Ok or not and return suitable status code asynchronously.
         */
        // POST: api/command
        [HttpPost(Name = "SendCommandToSimulator")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        public async Task<ActionResult> SendCommandToSimulator(Command command)
        {
            try
            {
                Task<Result> result;
                // Send the command for setting the propreties with it.
                result = commandManager.SendCommand(command);
                // Waits for the Task to complete execution.
                result.Wait();
                // If returned result not OK- return bad request.
                if (result.Result == Result.NotOk)
                {
                    return await Task.FromResult(BadRequest());
                }
                else
                {
                    // If returned resuld OK- return OK.
                    return await Task.FromResult(Ok());
                }
            }
            catch
            {
                // Encountered an error.
                return await Task.FromResult(StatusCode(500));
            }
        }
    }
}
