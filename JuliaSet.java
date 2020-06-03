import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.lang.Math;

public class JuliaSet extends JPanel implements AdjustmentListener,ActionListener {

	JFrame frame;
	JScrollBar[] scrollBar;
	JPanel scrollPanel, rPanel, masterPanel;
	JCheckBox invert;
	JButton reset;
	JLabel equation;
	ButtonGroup group;
	JRadioButton[] rButtons;

	//int[] rgb= new int[3];
	boolean flip= false;
	String initialEq= "constant: ";

	BufferedImage image;
	double zoom= 1.0;
	float maxIter= 300f;
	double aVal= 0;
	double bVal= 0;
	int xExp= 2;
	int yExp= 2;

	int constantVal= 1000;
	int zoomVal= 100;
	int iterVal= 300;
	int inc= 1;

	//width and height of screen
	int w= 1000;
	int h= 800;

	public JuliaSet() {

		frame= new JFrame("Julia Set");
		frame.add(this);

		//scrollbars and scrollpanel
		scrollBar= new JScrollBar[4];
		scrollPanel= new JPanel();
		scrollPanel.setLayout(new GridLayout(scrollBar.length,1));
		//a
		scrollBar[0]= new JScrollBar(JScrollBar.HORIZONTAL,0,0,-constantVal,constantVal);
		//b
		scrollBar[1]= new JScrollBar(JScrollBar.HORIZONTAL,0,0,-constantVal,constantVal);
		//zoom
		scrollBar[2]= new JScrollBar(JScrollBar.HORIZONTAL,0,0,0,zoomVal-1);
		//iter
		scrollBar[3]= new JScrollBar(JScrollBar.HORIZONTAL,300,0,0,iterVal);

		for (int i=0; i<scrollBar.length; i++) {
			if (scrollBar[i]!=null) {
				scrollBar[i].addAdjustmentListener(this);
				scrollBar[i].setUnitIncrement(inc);
				scrollPanel.add(scrollBar[i]);
			}
		}

		//invert checkbox
		invert= new JCheckBox();
		invert.addActionListener(this);

		//reset button
		reset= new JButton("reset");
		reset.addActionListener(this);

		//text displaying equation
		equation= new JLabel(initialEq,JLabel.CENTER);

		//radio buttons and radio panel
		group= new ButtonGroup();
		rButtons= new JRadioButton[] {new JRadioButton("Circle"),new JRadioButton("Quadratic"),new JRadioButton("Cubic"),new JRadioButton("Quartic")};
		rPanel= new JPanel();
		rPanel.setLayout(new GridLayout(1,rButtons.length));
		for (int i=0; i<rButtons.length; i++) {
			rButtons[i].addActionListener(this);
			group.add(rButtons[i]);
			rPanel.add(rButtons[i]);
		}

		//master panel
		masterPanel= new JPanel();
		masterPanel.setLayout(new BorderLayout());
		masterPanel.add(scrollPanel,BorderLayout.CENTER);
		masterPanel.add(invert,BorderLayout.EAST);
		masterPanel.add(reset,BorderLayout.WEST);
		masterPanel.add(equation,BorderLayout.NORTH);
		frame.add(masterPanel,BorderLayout.SOUTH);
		frame.add(rPanel,BorderLayout.NORTH);

		frame.setSize(w,h);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		//g.setColor(new Color(rgb[0],rgb[1],rgb[2]));
		g.fillRect(0,0,frame.getWidth(),frame.getHeight());

		image= new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);

		for (int i=0; i<h; i++) {
			for (int j=0; j<w; j++) {
				double zx= 1.5*(j-w/2.0)/(0.5*zoom*w);
				double zy= (i-h/2.0)/(0.5*zoom*h);
				float iter= maxIter;
				while ((power(zx,xExp)+power(zy,yExp))<6.0 && iter>0) {
					double temp= power(zx,xExp)-power(zy,yExp)+aVal;
					zy= 2.0*zx*zy+bVal;
					zx= temp;
					iter--;
				}

				int c;
				if (iter>0f)
					c= Color.HSBtoRGB((maxIter/iter)%1,1,1);
				else
					c= Color.HSBtoRGB(maxIter/iter,1,0);

				if (flip)
					c= c^0x00ffffff;

				image.setRGB(j,i,c);
			}
		}

		g.drawImage(image,0,0,null);
	}

	public void adjustmentValueChanged(AdjustmentEvent e) {
		if (e.getSource()==scrollBar[0]) {
			aVal= scrollBar[0].getValue()/(double)constantVal;
		}
		if (e.getSource()==scrollBar[1]) {
			bVal= scrollBar[1].getValue()/(double)constantVal;
		}
		if (e.getSource()==scrollBar[2]) {
			zoom= scrollBar[2].getValue()+1;
		}
		if (e.getSource()==scrollBar[3]) {
			maxIter= scrollBar[3].getValue();
		}

		refresh();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==invert) {
			flip= !flip;
		}
		if (e.getSource()==reset) {
			for (int i=0; i<scrollBar.length; i++) {
				if (scrollBar[i]!=null) {
					scrollBar[i].setValue(0);
				}
			}
			scrollBar[3].setValue(300);
			flip= false;
		}

		//0:Circle 1:Quadratic 2:Cubic 3:Quartic
		if (rButtons[0].isSelected()) {
			xExp= 2;
			yExp= 2;
		}
		if (rButtons[1].isSelected()) {
			xExp= 2;
			yExp= 1;
		}
		if (rButtons[2].isSelected()) {
			xExp= 3;
			yExp= 1;
		}
		if (rButtons[3].isSelected()) {
			xExp= 4;
			yExp= 1;
		}
		refresh();
	}

	public void refresh() {
		String text= initialEq;
		double a= scrollBar[0].getValue()/(double)constantVal;
		double b= scrollBar[1].getValue()/(double)constantVal;

		text+=a;

		if (b<0)
			text+=b+"i";
		if (b>0)
			text+="+"+b+"i";

		equation.setText(text);
		repaint();

	}

	public double power(double d, int pow) {
		switch(pow) {
			case 1:
				return d;
			case 2:
				return d*d;
			case 3:
				return d*d*d;
			default:
				return 0;
		}
	}

	public static void main(String[] args)
	{
		JuliaSet app=new JuliaSet();
	}
}