package twizyTropBien;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FillLayout;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JTextArea;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.layout.RowData;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.GridData;
import swing2swt.layout.BorderLayout;
import org.eclipse.swt.custom.StackLayout;
import swing2swt.layout.FlowLayout;
import swing2swt.layout.BoxLayout;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Text;

import twizyTropBien.TraitementImage;

public class Principale {

	protected Shell shell;
	Label lblNewLabel;
	Label Console;
	private int currentImg = 0;
	private int imgDataBaseLenght = 10;
	private ArrayList<Image> dataBase;
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Principale window = new Principale();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		
		PrintStream printStream = new PrintStream(new CustomOutputStream(Console));
		System.setOut(printStream);
		System.setErr(printStream);
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	void nextImage(int dir)
	{
		if(dir == 1)
		{
			currentImg = (currentImg+1)%imgDataBaseLenght;
		}
		else
		{
			currentImg =((currentImg+imgDataBaseLenght-1)%imgDataBaseLenght);
		}
		lblNewLabel.setImage(dataBase.get(currentImg));
		//canvas.setBackgroundImage(dataBase.get(currentImg));
		Console.setText("");
		System.out.println("#####################");
		System.out.println("Image "+(currentImg+1));
	}

	

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		
		dataBase = new ArrayList<Image>();
		for (int i = 1; i < imgDataBaseLenght+1; i++) {
			Image img = new Image(Display.getDefault(),"s_p"+Integer.toString(i)+".jpg");
			System.out.println("img"+Integer.toString(i)+".png");
			dataBase.add(img);
		}		
		
		
		
		shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("SWT Application");
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		
		SashForm sashForm = new SashForm(shell, SWT.NONE);
		
		SashForm sashForm_1 = new SashForm(sashForm, SWT.VERTICAL);
		
		Group group = new Group(sashForm_1, SWT.NONE);
		group.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Button btnNewButton = new Button(group, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				nextImage(-1);
			}
		});
		btnNewButton.setText("<-");
		
		Button btnIdentify = new Button(group, SWT.NONE);
		btnIdentify.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TraitementImage.Identify("s_p"+Integer.toString(currentImg+1)+".jpg",lblNewLabel);
			}
		});
		btnIdentify.setText("Identify");
		
		Button btnNewButton_2 = new Button(group, SWT.NONE);
		btnNewButton_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				AnalyseVideo.Analyse();
			}
		});
		
		btnNewButton_2.setText("Video");
		
		Button btnNewButton_1 = new Button(group, SWT.NONE);
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				nextImage(1);
			}
		});
		btnNewButton_1.setText("->");
		
		Group group_1 = new Group(sashForm_1, SWT.NONE);
		group_1.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Console = new Label(group_1, SWT.NONE);
		
		sashForm_1.setWeights(new int[] {1, 5});
		
		lblNewLabel = new Label(sashForm, SWT.NONE);
		sashForm.setWeights(new int[] {1, 5});

	}
}
