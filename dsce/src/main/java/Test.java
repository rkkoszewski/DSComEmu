import com.robertkoszewski.dsce.client.devices.DSDevice.Mode;
import com.robertkoszewski.dsce.emulator.DreamScreenHDEmulator;
import com.robertkoszewski.dsce.emulator.utils.RobotScreenGrabber;
import com.robertkoszewski.dsce.emulator.utils.SimpleAverageSampler;
import com.robertkoszewski.dsce.emulator.variant.SwingDreamScreenHDEmulator;

/*******************************************************************************
 * Copyright (c) 2018 Robert Koszewski
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *******************************************************************************/

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*

		Color color = Color.RED;
		System.out.println("RED: R:" + color.getRed() + " G:" + color.getGreen() + " B:"+ color.getBlue());
		
		color = Color.BLUE;
		System.out.println("BLUE: R:" + color.getRed() + " G:" + color.getGreen() + " B:"+ color.getBlue());
		
		
		int a = Color.RED.getRGB();
		int b = Color.BLUE.getRGB();
		int x = (int) (((((a) ^ (b)) & 0xfffefefeL) >> 1) + ((a & (b))));
		System.out.println(x);
		
		
		x = (int) (((((x) ^ (b)) & 0xfffefefeL) >> 1) + ((x & (b))));
		System.out.println(x);
		
		
		color = new Color(x);
		System.out.println("AVG: R:" + color.getRed() + " G:" + color.getGreen() + " B:"+ color.getBlue());
		
		
		System.out.println("RED: " + ((Color.RED.getRGB() >> 16) & 0xFF));
		
		
		System.exit(0);
		*/

		/*
		JFrame frame = new JFrame();
		frame.setSize(new Dimension(512, 512));
		frame.getContentPane().setLayout(new FlowLayout());

		final JLabel label = new JLabel();
		frame.getContentPane().add(label);
		
		new Thread() {
			@Override
			public void run() {
				while(true) {
					
					//System.out.println("LOOP");
					BufferedImage image;
					try {
						image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
						label.setIcon(new ImageIcon(image));
					} catch (HeadlessException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (AWTException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					
					
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}.start();

		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		*/
		
		DreamScreenHDEmulator dsemu = new SwingDreamScreenHDEmulator(new SimpleAverageSampler(new RobotScreenGrabber()));
		dsemu.start();
		dsemu.setMode(Mode.VIDEO);

		
		/*
		
		RobotScreenGrabber grabber = new RobotScreenGrabber();
		
		
		grabber.start();
		
		while(true) {
			
			System.out.println(grabber.hasFrame());
			
			if(grabber.hasFrame()) {
				
				grabber.getFrame();
				System.out.println("GOT FRAME");
				
			}
			
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		*/
	}

}
