namespace WinFormsParallel
{
    internal static class Program
    {
        /// <summary>
        ///  The main entry point for the application.
        /// </summary>
        [STAThread]
        static void Main()
        {
            // To customize application configuration such as set high DPI settings or default font,
            // see https://aka.ms/applicationconfiguration.
            ApplicationConfiguration.Initialize();
            Thread[] threads = new Thread[4];

            threads[0] = new Thread(() => { Application.Run(new Form1()); });
            threads[1] = new Thread(() => { Application.Run(new Form2()); });
            threads[2] = new Thread(() => { Application.Run(new Form3()); });
            threads[3] = new Thread(() => { Application.Run(new Form4()); });

            foreach (var x in threads)
                x.Start();
            foreach (var x in threads)
                x.Join();
        
        }
    }
}