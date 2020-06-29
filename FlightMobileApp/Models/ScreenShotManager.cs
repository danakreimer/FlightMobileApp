using Microsoft.Extensions.Configuration;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Threading.Tasks;

namespace FlightMobileApp.Models
{
    // ScreenShotManager class.
    public class ScreenShotManager
    {
        private string portHttp;
        private string ip;

        // Constructor.
        public ScreenShotManager(IConfiguration configur)
        {
            this.portHttp = configur.GetValue<string>("Connect:PortHttp");
            this.ip = configur.GetValue<string>("Connect:ip");
        }

        // Get Screenshot by http request.
        public async Task<Byte[]> GetScreenshot()
        {
            string url = "http://" + ip + ":" + portHttp + "/screenshot";
            try
            {
                // Create new http client.
                using var client = new HttpClient
                {
                    // Time out if has been more than 10 seconds.
                    Timeout = TimeSpan.FromSeconds(10)
                };
                // Send GET request to the Url and return response as byte array- asynchronic.
                return await client.GetByteArrayAsync(url);
            }
            // Catch exception if we couldn't get screenshot and throw with suttable message.
            catch (Exception)
            {
                throw new Exception("Error Getting Screenshot");
            }
        }
    }

}
