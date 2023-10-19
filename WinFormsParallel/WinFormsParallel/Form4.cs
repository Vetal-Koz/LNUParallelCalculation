using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace WinFormsParallel
{
    public partial class Form4 : Form
    {
        private bool goBack;
        private int count = 0;
        static System.Windows.Forms.Timer timer = new System.Windows.Forms.Timer();
        public Form4()
        {
            timer.Enabled = true;
            timer.Interval = 1;
            timer.Tick += new EventHandler(myTimer_Tick);
            timer.Start();
            InitializeComponent();
        }

        private void myTimer_Tick(object? sender, EventArgs e)
        {
            Graphics g = panel1.CreateGraphics();
            Pen pen = new Pen(Color.Red, 1);

            g.Clear(Color.Black);
            g.DrawEllipse(pen, 250, count, 200, 200);

            if (count + 200 >= panel1.Height)
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

        private void button1_Click(object sender, EventArgs e)
        {
            timer.Start();
        }

        private void button2_Click(object sender, EventArgs e)
        {
            timer.Stop();
        }
    }
}
