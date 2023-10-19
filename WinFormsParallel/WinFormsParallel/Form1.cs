namespace WinFormsParallel
{
    public partial class Form1 : Form
    {
        private bool goBack;
        private int count = 0;
        static System.Windows.Forms.Timer timer = new System.Windows.Forms.Timer();
        public Form1()
        {
            timer.Enabled = true;
            timer.Interval = 30;
            timer.Tick += new EventHandler(myTimer_Tick);
            timer.Start();
            InitializeComponent();
        }

        private void button1_Click(object sender, EventArgs e)
        {
            timer.Start();
        }

        private void button2_Click(object sender, EventArgs e)
        {
            timer.Stop();
        }
        private void myTimer_Tick(object sender, EventArgs e)
        {
            Graphics g = panel1.CreateGraphics();
            Pen pen = new Pen(Color.Yellow, 1);

            g.Clear(Color.White);
            g.DrawEllipse(pen, count, 0, 200, 200);

            if (count + 200 == panel1.Width)
                goBack = false;
            else if (count == 0)
                goBack = true;
            if (goBack)
                count++;
            else
                count--;
            g.Dispose();
            pen.Dispose();
        }

    }
}