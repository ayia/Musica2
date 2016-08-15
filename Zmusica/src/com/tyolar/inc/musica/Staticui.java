package com.tyolar.inc.musica;

import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.tyolar.inc.musica.DAO.PlayListDAO;
import com.tyolar.inc.musica.composants.Playlist_Editor;

import com.tyolar.inc.musica.model.PlayList;
import com.tyolar.inc.musica.model.song;

public class Staticui {
	public static void show_add_playlist_view(final Activity ac,final song song) {
		try {
			PlayList[] listactor = new PlayList[0];
			try {
				listactor = PlayListDAO.get(ac).toArray(
						new PlayList[PlayListDAO.get(ac)
								.size()]);
			} catch (Exception d) {
				listactor = new PlayList[0];
			}
			final int size = listactor.length;
			String[] plsnames = new String[listactor.length + 1];

			for (int i = 0; i < listactor.length; i++)
				plsnames[i] = listactor[i].getName();

			plsnames[listactor.length] = ac.getResources().getString(
					R.string.dmusic_playlist_create_playlist);

			final NiftyDialogBuilder dsi =GetDialog(ac);
			dsi.withTitle(ac.getResources().getString(R.string.dmusic_playlist_add_to_title))
					.items(plsnames)
					.withButton2Text(ac.getResources().getString(R.string.cancel))
					.itemsCallback(new NiftyDialogBuilder.ListCallback() {
						@Override
						public void onSelection(NiftyDialogBuilder dialog,
								View view, int which, CharSequence text) {
							try {
								if (which == size) {

									create_playlist(ac,song);

								} else {
									PlayList d = PlayListDAO.get(
											ac).get(which);

									if (d.getSongs() == null)
										d.setSongs(new ArrayList<song>());
									d.getSongs().add(song);
									PlayListDAO.remove(ac, d);
									PlayListDAO.save(ac, d);
									dsi.dismiss();
									Toast.makeText(
											ac,
											ac.getResources().getString(
													R.string.one_song_added),
											Toast.LENGTH_LONG).show();

								}
							} catch (Exception e) {
								dsi.dismiss();
								Toast.makeText(
										ac,
								u		ac.getResources()
												.getString(
														R.string.error),
										Toast.LENGTH_LONG).show();
							}

						}

					}).show();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(ac,
					ac.getResources().getString(R.string.error),

					Toast.LENGTH_LONG).show();
		}

	}
	
	public static  NiftyDialogBuilder GetDialog(android.content.Context  ctx){
		final NiftyDialogBuilder dialogBuilder=NiftyDialogBuilder.getInstance(ctx);
		dialogBuilder.withTitleColor(ctx.getResources().getColor(R.color.pure_white))                                  //def
	    .withDividerColor("#11000000")                              //def
	     .withCostumeColor(ctx.getResources().getColor(R.color.pure_white))                              //def
	    .withMessageColor(ctx.getResources().getColor(R.color.orange))                              //def  | withMessageColor(int resid)
	    .withDialogColor(ctx.getResources().getColor(R.color.orange))                               //def  | withDialogColor(int resid)
	    .withIcon(ctx.getResources().getDrawable(R.drawable.ic_launcher_white))
	    .withDuration(700)                                          //def
	    .withEffect(Effectstype.Sidefill) 
	       .setButton2Click(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	dialogBuilder.dismiss();
	        }
	    })
	    ;                                      //def Effectstype.Slidetop
	   
		
		return dialogBuilder;
	}
	
	
	public static void create_playlist(final Activity ac,final song song) {
		
		final Playlist_Editor plsçeditor = new Playlist_Editor(ac);
		Random generator = new Random();
		final int ida = generator.nextInt(1000000);
		final NiftyDialogBuilder dialogBuilder= GetDialog(ac);
		dialogBuilder
	    .withTitle(ac.getResources().getString(
				R.string.dmusic_download_cancel_dialog_prompt_collection_playlist))                                  //.withTitle(null)  no title
	     .withButton1Text("OK")                                      //def gone
	    .withButton2Text(ac.getResources().getString(R.string.cancel))                                  //def gone
	    .isCancelableOnTouchOutside(true)                           //def    | isCancelable(true)
	    .setCustomView(plsçeditor,ac)         //.setCustomView(View or ResId,context)
	    .setButton1Click(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        try{
	        	PlayList pls = new PlayList(ida, plsçeditor
						.getPlaylist_name(), plsçeditor
						.getPlaylist_description(), null);
				ArrayList<song> list = new ArrayList<song>() {
					{
						if (song != null)
							add(song);
					}
				};
				pls.setSongs(list);
				PlayListDAO.save(ac, pls);

				dialogBuilder.dismiss();

				Toast.makeText(
						ac,
						ac.getResources().getString(
								R.string.playlist_created),
						Toast.LENGTH_LONG).show();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				Toast.makeText(
						ac,
						ac.getResources().getString(
								R.string.error),
						Toast.LENGTH_LONG).show();

			}
	                    }
	    })
	 
	    .show();
			
		dialogBuilder.getOkButton().setEnabled(false);
		
		plsçeditor.getname_playlist().addTextChangedListener(new TextWatcher() {
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// don't care about this one
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String newText = plsçeditor.getname_playlist().getText()
						.toString();
				if (newText.trim().length() == 0) {
					dialogBuilder.getOkButton().setEnabled(false);
				} else {
					dialogBuilder.getOkButton().setEnabled(true);

				}
			};

			public void afterTextChanged(Editable s) {
				// don't care about this one
			}
		});

			
		
	}
	public static void create_playlist(final Activity ac,final ArrayList<song> list) {
		// TODO Auto-generated method stub
		final Playlist_Editor plsçeditor = new Playlist_Editor(
				ac);
		Random generator = new Random();
		final int ida = generator.nextInt(1000000);
		
		final NiftyDialogBuilder dialogBuilder= GetDialog(ac);
		dialogBuilder
	    .withTitle(ac.getResources().getString(
				R.string.dmusic_download_cancel_dialog_prompt_collection_playlist))                                  //.withTitle(null)  no title
	     .withButton1Text("OK")                                      //def gone
	    .withButton2Text(ac.getResources().getString(R.string.cancel))                                  //def gone
	    .isCancelableOnTouchOutside(true)                           //def    | isCancelable(true)
	    .setCustomView(plsçeditor,ac)         //.setCustomView(View or ResId,context)
	    .setButton1Click(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        try{
	        	PlayList pls = new PlayList(ida, plsçeditor
						.getPlaylist_name(), plsçeditor
						.getPlaylist_description(), null);
				pls.setSongs(list);
				PlayListDAO.save(ac, pls);

				dialogBuilder.dismiss();

				Toast.makeText(
						ac,
						ac.getResources().getString(
								R.string.playlist_created),
						Toast.LENGTH_LONG).show();
	        } catch (Exception e) {
				// TODO Auto-generated catch block
				Toast.makeText(
						ac,
						ac.getResources().getString(
								R.string.error),
						Toast.LENGTH_LONG).show();

			}

	                    }
	    })
	    .setButton2Click(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	dialogBuilder.dismiss();
	        }
	    })
	    .show();
		
dialogBuilder.getOkButton().setEnabled(false);
		
		plsçeditor.getname_playlist().addTextChangedListener(new TextWatcher() {
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// don't care about this one
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String newText = plsçeditor.getname_playlist().getText()
						.toString();
				if (newText.trim().length() == 0) {
					dialogBuilder.getOkButton().setEnabled(false);
				} else {
					dialogBuilder.getOkButton().setEnabled(true);

				}
			};

			public void afterTextChanged(Editable s) {
				// don't care about this one
			}
		});
	}



}
