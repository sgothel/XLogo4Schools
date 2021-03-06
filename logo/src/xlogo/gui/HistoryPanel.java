/* XLogo4Schools - A Logo Interpreter specialized for use in schools, based on XLogo by Loic Le Coq
 * Copyright (C) 2013 Marko Zivkovic
 * 
 * Contact Information: marko88zivkovic at gmail dot com
 * 
 * This program is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by the Free 
 * Software Foundation; either version 2 of the License, or (at your option) 
 * any later version.  This program is distributed in the hope that it will be 
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General 
 * Public License for more details.  You should have received a copy of the 
 * GNU General Public License along with this program; if not, write to the Free 
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA 02110-1301, USA.
 * 
 * 
 * This Java source code belongs to XLogo4Schools, written by Marko Zivkovic
 * during his Bachelor thesis at the computer science department of ETH Zurich,
 * in the year 2013 and/or during future work.
 * 
 * It is a reengineered version of XLogo written by Loic Le Coq, published
 * under the GPL License at http://xlogo.tuxfamily.org/
 * 
 * Contents of this file were initially written by Loic Le Coq,
 * modifications, extensions, refactorings might have been applied by Marko Zivkovic 
 */

package xlogo.gui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.text.rtf.RTFEditorKit;
import javax.swing.text.*;

import java.io.*;

import xlogo.storage.WSManager;
import xlogo.storage.global.GlobalConfig;
import xlogo.storage.user.UserConfig;
import xlogo.storage.workspace.WorkspaceConfig;
import xlogo.utils.Utils;
import xlogo.utils.ExtensionFichier;
import xlogo.StyledDocument.DocumentLogoHistorique;
import xlogo.Application;
import xlogo.kernel.DrawPanel;
import xlogo.kernel.LogoError;
import xlogo.messages.async.AsyncMediumAdapter;
import xlogo.messages.async.AsyncMessage;
import xlogo.messages.async.AsyncMessenger;
import xlogo.messages.async.history.HistoryMessenger;
import xlogo.messages.async.history.HistoryWriter;
import xlogo.Logo;

/**
 * Title : XLogo
 * Description : XLogo is an interpreter for the Logo
 * programming language
 * 
 * @author Loïc Le Coq
 */
public class HistoryPanel extends JPanel implements HistoryWriter
{
	
	private static final long		serialVersionUID	= 1L;
	// numéro identifiant la police de
	// l'historique avec "ecris"
	public static int				fontPrint			= GlobalConfig.getFontId(WSManager.getWorkspaceConfig().getFont());	// TODO
																															// how
																															// to
																															// remove
																															// fontPrint?
																															// Error@static?
	private ImageIcon				ianimation			= Utils.dimensionne_image("animation.png", this);
	private JLabel					label_animation		= new JLabel(ianimation);
	private MouseAdapter			mouseAdapt;
	private Color					couleur_texte		= Color.BLUE;
	private int						taille_texte		= 12;
	private JPanel					jPanel1				= new JPanel();
	private JScrollPane				jScrollPane1		= new JScrollPane();
	private Historique				historique			= new Historique();
	private DocumentLogoHistorique	dsd;
	private BorderLayout			borderLayout1		= new BorderLayout();
	private Application				cadre;
	
	public HistoryPanel()
	{
	}
	
	public HistoryPanel(Application cadre) {
  	historique.setFont(WSManager.getWorkspaceConfig().getFont());
    this.cadre=cadre;
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
	dsd=new DocumentLogoHistorique();
	historique.setDocument(dsd);
	
	  /*
	   * Added by Marko Zivkovic to decouple HistoryPanel from the rest of the old XLogo classes
	   */
	  HistoryMessenger.getInstance().setMedium(new AsyncMediumAdapter<AsyncMessage<HistoryWriter>, HistoryWriter>(){
		
		public boolean isReady()
		{
			return getThis().historique.isDisplayable();
		}
		
		public HistoryWriter getMedium()
		{
			return getThis();
		}
		
		public void addMediumReadyListener(final AsyncMessenger messenger)
		{
			historique.addAncestorListener(new AncestorListener(){
				public void ancestorRemoved(AncestorEvent event)
				{
					maybeMediumReadyEvent();
				}
				public void ancestorMoved(AncestorEvent event)
				{
					maybeMediumReadyEvent();
				}
				public void ancestorAdded(AncestorEvent event)
				{
					maybeMediumReadyEvent();
				}
				
				private void maybeMediumReadyEvent()
				{
					if (isDisplayable())
						messenger.onMediumReady();
				}
				
			});
		}
	});
  }
	
	private HistoryPanel getThis()
	{
		return this;
	}
	
	public Color getCouleurtexte()
	{
		return couleur_texte;
	}
	
	public int police()
	{
		return taille_texte;
	}
	
	public void vide_texte()
	{
		historique.setText("");
	}
	
	/**
	 * Made private by Marko Zivkovic.
	 * This functionality is now publicly available through the implementation of {@link HistoryWriter#writeMessage(String, String)}
	 * @param sty
	 * @param texte
	 */
	private void ecris(String sty, String texte)
	{
		try
		{
			int longueur = historique.getDocument().getLength();
			if (texte.length() > 32000)
				throw new LogoError( Logo.messages.getString("chaine_trop_longue"));
			if (longueur + texte.length() < 65000)
			{
				try
				{
					dsd.setStyle(sty);
					dsd.insertString(dsd.getLength(), texte, null);
					historique.setCaretPosition(dsd.getLength());
				}
				catch (BadLocationException e)
				{}
			}
			else
			{
				vide_texte();
			}
		}
		catch (LogoError e2)
		{}
	}
	
	private void jbInit() throws Exception
	{
		
		this.setLayout(borderLayout1);
		this.setMinimumSize(new Dimension(4, 4));
		this.setPreferredSize(new Dimension(600, 40));
		historique.setForeground(Color.black);
		historique.setEditable(false);
		this.add(jPanel1, BorderLayout.EAST);
		label_animation.setToolTipText(Logo.messages.getString("animation_active"));
		this.add(jScrollPane1, BorderLayout.CENTER);
		jScrollPane1.getViewport().add(historique, null);
	}
	
	public void active_animation()
	{
		add(label_animation, BorderLayout.WEST);
		DrawPanel.classicMode = DrawPanel.MODE_ANIMATION;
		mouseAdapt = new MouseAdapter(){
			public void mouseClicked(MouseEvent e)
			{
				stop_animation();
				cadre.getDrawPanel().repaint();
			}
		};
		label_animation.addMouseListener(mouseAdapt);
		validate();
	}
	
	public void stop_animation()
	{
		DrawPanel.classicMode = DrawPanel.MODE_CLASSIC;
		remove(label_animation);
		label_animation.removeMouseListener(mouseAdapt);
		validate();
	}
	
	// Change Syntax Highlighting for the editor
	public void initStyles(int c_comment, int sty_comment, int c_primitive, int sty_primitive, int c_parenthese,
			int sty_parenthese, int c_operande, int sty_operande)
	{
		WorkspaceConfig wc = WSManager.getWorkspaceConfig();
		dsd.initStyles(wc.getCommentColor(), wc.getCommentStyle(), wc.getPrimitiveColor(), wc.getPrimitiveStyle(),
				wc.getBraceColor(), wc.getBraceStyle(), wc.getOperandColor(), wc.getOperandStyle());
	}
	
	// Enable or disable Syntax Highlighting
	public void setColoration(boolean b)
	{
		dsd.setColoration(b);
	}
	
	public void changeFont(Font f)
	{
		historique.setFont(f);
	}
	
	public void updateText()
	{
		historique.setText();
	}
	
	public DocumentLogoHistorique getDsd()
	{
		return dsd;
	}
	
	public StyledDocument sd_Historique()
	{
		return historique.getStyledDocument();
	}
	
	class Historique extends JTextPane implements ActionListener
	{
		private static final long	serialVersionUID	= 1L;
		private JPopupMenu			popup				= new JPopupMenu();
		private JMenuItem			jpopcopier			= new JMenuItem();
		private JMenuItem			jpopselect			= new JMenuItem();
		private JMenuItem			jpopsave			= new JMenuItem();
		
		Historique()
		{
			// this.setBackground(new Color(255,255,220));
			popup.add(jpopcopier);
			popup.add(jpopselect);
			popup.add(jpopsave);
			jpopselect.addActionListener(this);
			jpopcopier.addActionListener(this);
			jpopsave.addActionListener(this);
			setText();
			MouseListener popupListener = new MouseAdapter(){
				public void mouseClicked(MouseEvent e)
				{
					if (e.getButton() == 1)
					{
						int i = getCaretPosition();
						int borneinf = borne(i, -1);
						int bornesup = borne(i, 1);
						if (borneinf == 0)
							borneinf = borneinf - 1;
						select(borneinf + 1, bornesup - 2);
						cadre.setCommandText(getSelectedText());
						// historique.setCaretPosition(historique.getDocument().getLength());
						cadre.focusCommandLine();
					}
				}
				
				public void mouseReleased(MouseEvent e)
				{
					maybeShowPopup(e);
					cadre.focusCommandLine();
				}
				
				public void mousePressed(MouseEvent e)
				{
					maybeShowPopup(e);
				}
				
				private void maybeShowPopup(MouseEvent e)
				{
					if (e.isPopupTrigger())
					{
						popup.show(e.getComponent(), e.getX(), e.getY());
					}
				}
			};
			addMouseListener(popupListener);
		}
		
		int borne(int i, int increment)
		{
			boolean continuer = true;
			while (continuer && i != 0)
			{
				select(i - 1, i);
				String t = historique.getSelectedText();
				if (t.equals("\n"))
				{
					continuer = false;
				}
				i = i + increment;
			}
			return (i);
		}
		
		void setText()
		{
			jpopselect.setText(Logo.messages.getString("menu.edition.selectall"));
			jpopcopier.setText(Logo.messages.getString("menu.edition.copy"));
			jpopsave.setText(Logo.messages.getString("menu.file.textzone.rtf"));
			jpopselect.setActionCommand(Logo.messages.getString("menu.edition.selectall"));
			jpopcopier.setActionCommand(Logo.messages.getString("menu.edition.copy"));
			jpopsave.setActionCommand(Logo.messages.getString("menu.file.textzone.rtf"));
		}
		
		public void actionPerformed(ActionEvent e)
		{
			UserConfig uc = WSManager.getUserConfig();
			WorkspaceConfig wc = WSManager.getInstance().getWorkspaceConfigInstance();
			String cmd = e.getActionCommand();
			if (Logo.messages.getString("menu.edition.copy").equals(cmd))
			{   // Copier
				copy();
			}
			else if (Logo.messages.getString("menu.edition.selectall").equals(cmd))
			{   // Selectionner tout
				requestFocus();
				selectAll();
				cadre.focusCommandLine();
			}
			else if (cmd.equals(Logo.messages.getString("menu.file.textzone.rtf")))
			{
				RTFEditorKit myRTFEditorKit = new RTFEditorKit();
				StyledDocument myStyledDocument = getStyledDocument();
				try
				{
					JFileChooser jf = new JFileChooser(Utils.SortieTexte(uc.getDefaultFolder()));
					String[] ext = { ".rtf" };
					jf.addChoosableFileFilter(new ExtensionFichier(Logo.messages.getString("fichiers_rtf"), ext));
					Utils.recursivelySetFonts(jf, wc.getFont());
					int retval = jf.showDialog(cadre.getFrame(), Logo.messages.getString("menu.file.save"));
					if (retval == JFileChooser.APPROVE_OPTION)
					{
						String path = jf.getSelectedFile().getPath();
						String path2 = path.toLowerCase();  // on garde la casse
															// du path pour les
															// systèmes
															// d'exploitation
															// faisant la
															// différence
						if (!path2.endsWith(".rtf"))
							path += ".rtf";
						FileOutputStream myFileOutputStream = new FileOutputStream(path);
						myRTFEditorKit.write(myFileOutputStream, myStyledDocument, 0, myStyledDocument.getLength() - 1);
						myFileOutputStream.close();
						
					}
				}
				catch (FileNotFoundException e1)
				{}
				catch (IOException e2)
				{}
				catch (BadLocationException e3)
				{}
				catch (NullPointerException e4)
				{}
			}
		}
	}
	
	public void writeMessage(String messageType, String message)
	{
		ecris(messageType, message);
	}
}
