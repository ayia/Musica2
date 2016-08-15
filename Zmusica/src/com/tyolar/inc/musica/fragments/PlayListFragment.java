package com.tyolar.inc.musica.fragments;

import java.util.ArrayList;
import java.util.Random;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;



import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.tyolar.inc.musica.BaseActivity;
import com.tyolar.inc.musica.CFragment;
import com.tyolar.inc.musica.R;
import com.tyolar.inc.musica.Staticui;
import com.tyolar.inc.musica.app2;
import com.tyolar.inc.musica.DAO.PlayListDAO;
import com.tyolar.inc.musica.adapter.PlayListAdapter;
import com.tyolar.inc.musica.composants.Playlist_Editor;
import com.tyolar.inc.musica.model.PlayList;
import com.tyolar.inc.musica.model.song;

public class PlayListFragment extends CFragment {
	GridView playlist_grid;
	PlayListAdapter adapter;
	Button addplaylist;

	public PlayListFragment() {
		super();
	}

	public PlayListFragment(String titel) {
		super(titel);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_playlist, container,
				false);
		playlist_grid = (GridView) rootView.findViewById(R.id.playlist_grid);
		addplaylist = (Button) rootView.findViewById(R.id.add_new_playlist);
		loadMusicPlayList((BaseActivity) getActivity(), this);
		app2 mapp = (app2) getActivity().getApplicationContext();
		// Tracker t = mapp.getTracker(app2.TrackerName.APP_TRACKER);
		// t.setScreenName("MyAlbumsFragment");
		// t.send(new HitBuilders.AppViewBuilder().build());
		mapp.getInstance().trackScreenView("PlayList Screen");
		return rootView;

	}

	public void notifyDataSetChanged() {

		loadMusicPlayList((BaseActivity) getActivity(), this);
	}

	public void loadMusicPlayList(final BaseActivity contexta,
			final Fragment context) {
		PlayList[] array;
		playlist_grid.setAdapter(null);
		try {
			array = PlayListDAO.get(context.getActivity())
					.toArray(
							new PlayList[PlayListDAO.get(context.getActivity())
									.size()]);
			adapter = new PlayListAdapter(contexta, array, this);
			addplaylist.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					create_playlist(null);

				}
			});

			playlist_grid.setAdapter(adapter);
			playlist_grid.setVisibility(View.VISIBLE);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	
	public void create_playlist(final song song) {
		// TODO Auto-generated method stub
		final Playlist_Editor pls�editor = new Playlist_Editor(getActivity());
		Random generator = new Random();
		final int ida = generator.nextInt(1000000);
		final NiftyDialogBuilder dialogBuilder=Staticui.GetDialog(getActivity());
		dialogBuilder
	    .withTitle(getActivity()
				.getResources()
				.getString(
						R.string.dmusic_download_cancel_dialog_prompt_collection_playlist))                                  //.withTitle(null)  no title
	                                         //def Effectstype.Slidetop
	   .withButton1Text("OK")                                      //def gone
	    .withButton2Text(getActivity().getResources().getString(R.string.cancel))                           //def gone
	    .isCancelableOnTouchOutside(true)                           //def    | isCancelable(true)
	    .setCustomView(pls�editor,getContext())         //.setCustomView(View or ResId,context)
	    .setButton1Click(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        try{
				PlayList pls = new PlayList(ida, pls�editor
						.getPlaylist_name(), pls�editor
						.getPlaylist_description(), null);
				ArrayList<song> list = new ArrayList<song>() {
					{
						if (song != null)
							add(song);
						adapter.notifyDataSetChanged();
					}
				};
				pls.setSongs(list);
				PlayListDAO.save(getActivity(), pls);
			
				dialogBuilder.dismiss();
				loadMusicPlayList((BaseActivity) getActivity(),
						getthis());
				Toast.makeText(
						getActivity(),
						getResources().getString(
								R.string.playlist_created),
						Toast.LENGTH_LONG).show();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				Toast.makeText(getActivity(),
						getResources().getString(R.string.error),
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
		
		pls�editor.getname_playlist().addTextChangedListener(new TextWatcher() {
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// don't care about this one
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String newText = pls�editor.getname_playlist().getText()
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

	private Fragment getthis() {
		return this;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		//
		// if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
		// playlist_grid.setNumColumns(3);;
		// } else {
		// playlist_grid.setNumColumns(4);
		// }
		super.onConfigurationChanged(newConfig);
	}

}
