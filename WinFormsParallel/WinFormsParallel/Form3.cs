using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using static System.Runtime.InteropServices.JavaScript.JSType;

namespace WinFormsParallel
{
    public partial class Form3 : Form
    {
        private bool reduce;
        private int count = 0;
        static System.Windows.Forms.Timer timer = new System.Windows.Forms.Timer();
        public Form3()
        {
            timer.Enabled = true;
            timer.Interval = 20;
            timer.Tick += new EventHandler(myTimer_Tick);
            timer.Start();
            InitializeComponent();
        }

        private void myTimer_Tick(object? sender, EventArgs e)
        {
            Graphics g = panel1.CreateGraphics();
            Pen pen = new Pen(Color.Brown, 1);

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

        private void panel1_Paint(object sender, PaintEventArgs e)
        {

        }

        private void button1_Click(object sender, EventArgs e)
        {
            timer.Start();
        }

        private void button2_Click(object sender, EventArgs e)
        {
            timer.Stop();
        }

        private void Form3_Load(object sender, EventArgs e)
        {

        }
    }
}
