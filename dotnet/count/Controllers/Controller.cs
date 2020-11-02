using System.Net.Http;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using Steeltoe.Common.Discovery;

namespace Microsoft.Azure.SpringCloud.Sample.Count.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class Controller : ControllerBase
    {
        private readonly ILogger<Controller> logger;
        private readonly DiscoveryHttpClientHandler discoveryHandler;

        public Controller(IDiscoveryClient discovery, ILogger<Controller> logger)
        {
            discoveryHandler = new DiscoveryHttpClientHandler(discovery, logger);
            this.logger = logger;
        }

        [HttpGet]
        public async Task<string> Get()
        {
            using var client = new HttpClient(discoveryHandler, false);
            var response = await client.GetAsync("http://access-stat/count");

            return await response.Content.ReadAsStringAsync();
        }
    }
}
