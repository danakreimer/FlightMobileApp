using FlightMobileApp.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Configuration;
using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Threading.Tasks;

namespace FlightMobileApp.Controllers
{
    // ScreenShotController class.
    [Route("/[controller]")]
    [ApiController]
    public class ScreenShotController : ControllerBase
    {
        private readonly ScreenShotManager screenShotManager;

        // Constructor.
        public ScreenShotController(ScreenShotManager screenShotManager)
        {
            this.screenShotManager = screenShotManager;
        }

        // GET: api/Screenshot
        [HttpGet]
        public async Task<IActionResult> Get()
        {
            try
            {
                // Try to get ScreenShot 
                // (we can do await because it return a task, with response as byte array). 
                Byte[] response = await screenShotManager.GetScreenshot();
                // Return the screenshot with the suitable format.
                return File(response, "image/jpg");
            } 
            catch
            {
                // Encountered an error executing a request.
                return await Task.FromResult(StatusCode(500));
            }
        }
    }
}
