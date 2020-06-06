package twizyTropBien;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

import utilitaireAgreg.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;
public class AnalyseVideo {
	static { 
	    try {
	    //System.load("./opencv_ffmpeg2413_64.dll");
	    } catch (UnsatisfiedLinkError e) {
	        System.err.println("Native code library failed to load.\n" + e);
	        System.exit(1);
	    }
	}


	public static void Analyse() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		// Create new MAT object
		Mat frame = new Mat();

		// Create new VideoCapture object
		VideoCapture camera = new VideoCapture("video1.mp4");

		// Create new JFrame object
		JFrame jframe = new JFrame("Video Title");

		// Inform jframe what to do in the event that you close the program
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create a new JLabel object vidpanel
		JLabel vidPanel = new JLabel();

		// assign vidPanel to jframe
		jframe.setContentPane(vidPanel);

		// set frame size
		jframe.setSize(1024, 780);

		// make jframe visible
		jframe.setVisible(true);
		
		HashMap<Integer, Mat> bestFrames = new HashMap<Integer, Mat>();
		HashMap<Integer, Integer> bestSim = new HashMap<Integer, Integer>();
		Mat transformee,saturee,objetrond;
		List<MatOfPoint> ListeContours;
		int tbuff=6;
		int[] nulltab = new int[tbuff];
		for(int i=0;i<tbuff;i++) {nulltab[i]=-1;}
		int[] buffer=nulltab.clone();
		int[] tab;
		// j->indice dans le buffer, p->valeur temporaire de similitude
		// r->s'incrémente jusqu'à rmax tant que aucun panneau
		int j=0,p,r=0,rmax=10,bestp=-1,sim=0;
		Mat frametemp=null;
		while (camera.read(frame)) {
			// If next video frame is available
			//if (camera.read(frame)) {
				
				transformee=MaBibliothequeTraitementImageEtendue.transformeBGRversHSV(frame);
				//la methode seuillage est ici extraite de l'archivage jar du meme nom 
				saturee=MaBibliothequeTraitementImage.seuillage(transformee, 6, 170, 110);
				objetrond = null;
				//Création d'une liste des contours à partir de l'image saturée
				ListeContours= MaBibliothequeTraitementImageEtendue .ExtractContours(saturee);
				//Pour tous les contours de la liste
				for (MatOfPoint contour: ListeContours  ){
					// isole la forme
					objetrond=MaBibliothequeTraitementImage.DetectForm(frame,contour);
					// calcul de la similitude
					tab=(identifiepanneau(objetrond));p=tab[0];
					// si panneau détecté on l'ajoute au buffer 
					if(p>=0) {buffer[j]=p;r=0;sim=tab[1];frametemp=frame.clone();}
					// sinon on vide le buffer quand r==rmax
					else {r++;if(r>rmax)buffer=nulltab.clone();}
					// on affiche ensuite le panneau le plus représenté dans le buffer
					j=(j+1)%tbuff;
					if(j==0) {
						bestp=bestPanneau(buffer);
						if(bestp>=0) {
						if(bestFrames.containsKey(bestp)) {
							if(bestSim.get(bestp)<sim) {bestFrames.replace(bestp, frametemp);bestSim.replace(bestp, sim);}
						}
						else {bestFrames.put(bestp, frametemp);bestSim.put(bestp, sim);}}
					}}
				// Create new image icon object and convert Mat to Buffered
				// Image
				ImageIcon image = new ImageIcon(Mat2BufferedImage(frame));
				// Update the image in the vidPanel
				vidPanel.setIcon(image);
				// Update the vidPanel in the JFrame
				vidPanel.repaint();

			//}
		}
		for(int key : bestFrames.keySet()) {MaBibliothequeTraitementImage.afficheImage("similitude : "+bestSim.get(key)/1000, bestFrames.get(key));}
	}

	public static BufferedImage Mat2BufferedImage(Mat m) {
		// Method converts a Mat to a Buffered Image
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if (m.channels() > 1) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		int bufferSize = m.channels() * m.cols() * m.rows();
		byte[] b = new byte[bufferSize];
		m.get(0, 0, b); // get all the pixels
		BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(b, 0, targetPixels, 0, b.length);
		return image;
	}

	
	public static BufferedImage Mat2bufferedImage(Mat image) {
		MatOfByte bytemat = new MatOfByte();
		Highgui.imencode(".jpg", image, bytemat);
		byte[] bytes = bytemat.toArray();
		InputStream in = new ByteArrayInputStream(bytes);
		BufferedImage img = null;
		try {
			img = ImageIO.read(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return img;
	}



	public static int[] identifiepanneau(Mat objetrond){
		double [] scores=new double [6];
		int indexmax=-1;
		double scoremax=-1;
		if (objetrond!=null){
			scores[0]=MaBibliothequeTraitementImage.tauxDeSimilitude(objetrond,"ref30.jpg");
			scores[1]=MaBibliothequeTraitementImage.tauxDeSimilitude(objetrond,"ref50.jpg");
			scores[2]=MaBibliothequeTraitementImage.tauxDeSimilitude(objetrond,"ref70.jpg");
			scores[3]=MaBibliothequeTraitementImage.tauxDeSimilitude(objetrond,"ref90.jpg");
			scores[4]=MaBibliothequeTraitementImage.tauxDeSimilitude(objetrond,"ref110.jpg");
			scores[5]=MaBibliothequeTraitementImage.tauxDeSimilitude(objetrond,"refdouble.jpg");
			/*scores[0]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"ref30.jpg");
			scores[1]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"ref50.jpg");
			scores[2]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"ref70.jpg");
			scores[3]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"ref90.jpg");
			scores[4]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"ref110.jpg");
			scores[5]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"refdouble.jpg");*/

			scoremax=scores[0];

			for(int j=1;j<scores.length;j++){
				if (scores[j]>scoremax){scoremax=scores[j];indexmax=j;}}
		}
		int s = (int)(scoremax*1000);
		int[] tab={indexmax,s};
		return tab;
	}
	
	
	public static int bestPanneau(int[] buffer){
		ArrayList<Integer> freq = new ArrayList<Integer>();
		ArrayList<Integer> parcouru = new ArrayList<Integer>();
		for(int ind:buffer) {
			if(parcouru.contains(ind)) {
				freq.set(parcouru.indexOf(ind),freq.get(parcouru.indexOf(ind))+1);
			}
			else {parcouru.add(ind);
			freq.add(1);}			
			}
		int max=0;
		for(int i=0;i<freq.size();i++) {
			if(freq.get(i)>max) {max=freq.get(i);}
		}
		int indexmax = parcouru.get(freq.indexOf(max));
		switch(indexmax){
		case -1:;break;
		case 0:System.out.println("Panneau 30 détécté");break;
		case 1:System.out.println("Panneau 50 détécté");break;
		case 2:System.out.println("Panneau 70 détécté");break;
		case 3:System.out.println("Panneau 90 détécté");break;
		case 4:System.out.println("Panneau 110 détécté");break;
		case 5:System.out.println("Panneau interdiction de dépasser détécté");break;
		}
		return indexmax;
		
	}


}