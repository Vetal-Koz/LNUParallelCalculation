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
    public partial class Form2 : Form
    {
        private bool reduce;
        private int count = 0;
        static System.Windows.Forms.Timer timer = new System.Windows.Forms.Timer();
        public Form2()
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
        private void myTimer_Tick(object sender, EventArgs e)
        {
            Graphics g = panel1.CreateGraphics();
            Pen pen = new Pen(Color.Black, 1);

            g.Clear(Color.White);
            g.DrawRectangle(pen, 0, 0, count, count);

            if (count >= panel1.Height)
                reduce = false;
            else if (count == 0)
                reduce = true;
            if (reduce)
                count++;
            else
                count--;

            g.Dispose();
            pen.Dispose();
        }

        private void button2_Click(object sender, EventArgs e)
        {
            timer.Stop();
        }
    }
}
