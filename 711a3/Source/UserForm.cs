using System;
using System.Drawing;
using System.Windows.Forms;
using System.Collections.Generic;

// Combination of controller and model implemented as a partial class such that they may be reached from the same namespace
namespace Assignment
{
    public partial class Middleware : Form
    {
        private Button buttonSend;
        private Label labelSent;
        private Label labelRecieved;
        private Label labelReady;
        private ListBox listBoxSent;
        private ListBox listBoxRecieved;
        private ListBox listBoxReady;

        public Middleware()
        {
            // middleware
            this.sequenceNumber = 0;
            this.timestamp = 0;
            this.holding = new List<ValueTuple<Message, int[]>>();

            // userForm
            this.Name = "Middleware " + mid;
            this.BackColor = Color.White;
            this.Text = this.Name;
            this.Size = new Size(875, 350);

            // buttonSend
            this.buttonSend = new Button();
            this.buttonSend.Name = "Send";
            this.buttonSend.ForeColor = System.Drawing.Color.Black;
            this.buttonSend.Text = this.buttonSend.Name;
            this.buttonSend.Font = new System.Drawing.Font(this.buttonSend.Font.Name, 20F);
            this.buttonSend.Size = new Size(100, 50);
            this.buttonSend.Location = new Point(5, 5);
            this.buttonSend.Click += new System.EventHandler((object source, EventArgs e) =>
            {
                this.createAndSendMessage();
            });
            this.Controls.Add(this.buttonSend);

            // labelSent
            this.labelSent = new Label();
            this.labelSent.Name = "Sent";
            this.labelSent.Text = this.labelSent.Name;
            this.labelSent.ForeColor = System.Drawing.Color.Black;
            this.labelSent.Font = new System.Drawing.Font(this.labelSent.Font.Name, 24F);
            this.labelSent.Size = new Size(200, 35);
            this.labelSent.Location = new Point(5, 60);
            this.Controls.Add(this.labelSent);

            // listBoxSent
            this.listBoxSent = new ListBox();
            this.listBoxSent.ForeColor = System.Drawing.Color.Black;
            this.listBoxSent.Size = new Size(280, 200);
            this.listBoxSent.Location = new Point(5, 100);
            this.Controls.Add(this.listBoxSent);

            // labelRecieved
            this.labelRecieved = new Label();
            this.labelRecieved.Name = "Recieved";
            this.labelRecieved.Text = this.labelRecieved.Name;
            this.labelRecieved.ForeColor = System.Drawing.Color.Black;
            this.labelRecieved.Font = new System.Drawing.Font(this.labelRecieved.Font.Name, 24F);
            this.labelRecieved.Size = new Size(200, 35);
            this.labelRecieved.Location = new Point(290, 60);
            this.Controls.Add(this.labelRecieved);

            // listBoxRecieved
            this.listBoxRecieved = new ListBox();
            this.listBoxRecieved.ForeColor = System.Drawing.Color.Black;
            this.listBoxRecieved.Size = new Size(280, 200);
            this.listBoxRecieved.Location = new Point(290, 100);
            this.Controls.Add(this.listBoxRecieved);

            // labelReady
            this.labelReady = new Label();
            this.labelReady.Name = "Ready";
            this.labelReady.Text = this.labelReady.Name;
            this.labelReady.ForeColor = System.Drawing.Color.Black;
            this.labelReady.Font = new System.Drawing.Font(this.labelReady.Font.Name, 24F);
            this.labelReady.Size = new Size(200, 35);
            this.labelReady.Location = new Point(575, 60);
            this.Controls.Add(this.labelReady);

            // listBoxReady
            this.listBoxReady = new ListBox();
            this.listBoxReady.ForeColor = System.Drawing.Color.Black;
            this.listBoxReady.Size = new Size(280, 200);
            this.listBoxReady.Location = new Point(575, 100);
            this.Controls.Add(this.listBoxReady);
        }

        public static int Main(String[] args)
        {
            Middleware middleware = new Middleware();
            middleware.ReceiveMulticast();
            Application.Run(middleware);
            return 0;
        }
    }
}