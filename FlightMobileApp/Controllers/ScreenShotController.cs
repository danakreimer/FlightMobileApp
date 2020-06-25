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
    [Route("/[controller]")]
    [ApiController]
    public class ScreenShotController : ControllerBase
    {
        private readonly ScreenShotManager screenShotManager;

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
                Byte[] response = await screenShotManager.GetScreenshot();
                return File(response, "image/jpg");
            } 
            catch
            {
                return await Task.FromResult(StatusCode(500));
            }
        }
    }
}
