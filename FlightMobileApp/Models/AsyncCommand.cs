using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace FlightMobileApp.Models
{
    public enum Result { Ok, NotOk };

    // AsyncCommand class.
    public class AsyncCommand {
        // Combination of command and task which enable the asynchronous.
        public Command Command { get; private set; }
        public Task<Result> Task { get => Completion.Task; }
        public TaskCompletionSource<Result> Completion { get; private set; }
        public AsyncCommand(Command input)
        {
            Command = input;
            // Enables the creation of a task which enables await and for asyncification.
            Completion = new TaskCompletionSource<Result>(
                TaskCreationOptions.RunContinuationsAsynchronously);
        }
    }
}
