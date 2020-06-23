using Microsoft.Extensions.Configuration;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Threading.Tasks;

namespace FlightMobileApp.Models
{
    public class ScreenShotManager
    {
        private string portHttp;
        private string ip;
        public ScreenShotManager(IConfiguration configur)
        {
            this.portHttp = configur.GetValue<string>("Connect:PortHttp");
            this.ip = configur.GetValue<string>("Connect:ip");
        }

        public async Task<Byte[]> GetScreenshot()
        {
            string url = "http://" + ip + ":" + portHttp + "/screenshot";
            try
            {
                using var client = new HttpClient
                {
                    Timeout = TimeSpan.FromSeconds(10)
                };
                return await client.GetByteArrayAsync(url);
            }
            catch (Exception)
            {
                throw new Exception("Error Getting Screenshot");
            }
        }
    }

}
