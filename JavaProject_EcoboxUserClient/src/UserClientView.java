/***
 * 
 * 
 * 
 * 
 * 
 *******************************************************************************************************************************************
 *                                                                                                                                         *
 *     /\    DISCLAIMER     UGLY, UN-OPTIMIZED, "ALPHA-PROTOTYPING" CODE                                                                   *
 *    /  \   DISCLAIMER     DO NOT READ FURTHER UNTIL YOU HAVE FOUND A CURE FOR EYE CANCER                                                 *
 *   / !! \  DISCLAIMER     #KAPPA                                                                                                         *
 *  /______\ DISCLAIMER     Seriously though. Don't judge, this was written in a rush and will be improved, revised, and refactored soon.  *
 *                                                                                                                                         *
 *******************************************************************************************************************************************
 *
 *
 *
 *
 * (I'll only warn you once)
 ***/


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class UserClientView {

	JFrame frame;
	
	ViewDataLayer vdl;
	public void setListener(ViewDataLayer vdl){
		for(Views v : views.keySet()) views.get(v).setListener(vdl);
		this.vdl = vdl;
	}
	
	public void refresh(Views v){
		views.get(v).refresh();
	}
	
	public UserClientView(ViewDataLayer vdl){
		
		setListener(vdl);
		
		views.put(Views.LOGIN, new LoginView());
		views.put(Views.LOADING, new LoadingView());
		views.put(Views.LIST, new ListView());
		views.put(Views.DETAILED, new DetailedView(vdl));
		
		frame = new JFrame("Ecobox Test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		
		frame.pack();
		frame.setVisible(true);
		
		view(Views.LOGIN);	
		
	}
	
	
	private HashMap<Views, ClientView> views = new HashMap<Views, ClientView>();
	
	public void view(Views v){
		views.get(v).refresh();
		frame.getContentPane().removeAll();
		frame.add(views.get(v));
		frame.revalidate();
		frame.repaint();
		frame.pack();
		frame.setVisible(true);
	}
	
	
}

abstract class ClientView extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public abstract void refresh();
	ViewDataLayer vdl;
	public void setListener(ViewDataLayer vdl){
		this.vdl = vdl;
	}
}

class LoginView extends ClientView {

	private static final long serialVersionUID = 1L;

	public LoginView(){
		this.setLayout(new BorderLayout());
		JTextField text = new JTextField();
		add(text, BorderLayout.CENTER);
		JButton button = new JButton("Login as guest");
		add(button, BorderLayout.SOUTH);
		
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				vdl.submitLogin(text.getText(), "", true);
			}
		});
		
		setVisible(true);
		
	}
	
	@Override
	public void refresh() {
		
	}
	
	
}

class LoadingView extends ClientView {

	private static final long serialVersionUID = 1L;

	public LoadingView(){
		add(new JLabel("Loading"));
	}
	
	public void refresh() {
		// TODO Auto-generated method stub
		
	}
	
	
}

class ListView extends ClientView {

	private static final long serialVersionUID = 1L;

	public ListView(){
		setLayout(new GridLayout(0, 1));
	}
	
	private JPanel RPiGroup(RPi rpi){
		JPanel group = new JPanel(new GridLayout(2, 1));
		JLabel label = new JLabel(rpi.NAME);
		label.setForeground(rpi.status?Color.GREEN:Color.RED);
		group.add(label);
		String s = "";
		for(int i = 0; i < rpi.owners.length; i++){
			s += rpi.owners[i];
			if(i == rpi.owners.length-1) s += ",";
		}
		JLabel label2 = new JLabel(s);
		group.add(label2);
		group.addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {
				vdl.openRPi(rpi.ID);
			}
		});
		return group;
	}
	
	public void refresh() {
		for(RPi rpi : vdl.getData()){
			add(RPiGroup(rpi));
		}
	}
	
	
}

class DetailedView extends ClientView {
	HashMap<String, Integer> pos = new HashMap<String, Integer>();
	HashMap<String, Boolean> streams = new HashMap<String, Boolean>();
	public DetailedView(ViewDataLayer vdl){
		setLayout(new GridLayout(3, vdl.beta_reqs().length));
		for(String s : vdl.beta_reqs()){
			streams.put(s, false);
			add(new JLabel(s+":"));
		}
		for(int i = 0; i < vdl.beta_reqs().length; i++){
			add(new JLabel());
			pos.put(vdl.beta_reqs()[i], i + vdl.beta_reqs().length);
		}
		for(int i = 0; i < vdl.beta_reqs().length; i++){
			JButton b = new JButton("Open Stream");
			final String h = vdl.beta_reqs()[i];
			b.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					streams.put(h, !streams.get(h));
					vdl.stream(h, streams.get(h));
					b.setText(streams.get(h)?"Close Stream":"Open Stream");
				}
			});
			add(b);
		}
	}

	public void refresh(){
		for(String s : vdl.beta_reqs()){
			((JLabel)getComponent(pos.get(s))).setText(String.valueOf(RPi.findByID(vdl.getData(), vdl.currentRPi()).data.get(s)));
		}
		//((JLabel)this.getComponent(3)).setText(String.valueOf(RPi.findByID(vdl.getData(),	vdl.currentRPi()).data.get("Random")));
		//((JLabel)this.getComponent(4)).setText((String)RPi.findByID(vdl.getData(), vdl.currentRPi()).data.get("TimeMsg"));
		//int[][] pixels = (int[][]) RPi.findByID(vdl.getData(), vdl.currentRPi()).data.get("Webcam");
		//i = new BufferedImage(pixels[0][0], pixels[0][1], BufferedImage.TYPE_INT_ARGB);
		//i.setRGB(0, 0, pixels[0][0], pixels[0][1], pixels[1], 0, pixels[0][0]);
		//((JLabel)this.getComponent(5)).setIcon(new ImageIcon(i));
		
	}
}
