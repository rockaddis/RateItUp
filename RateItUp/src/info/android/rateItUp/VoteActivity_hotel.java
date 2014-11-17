package info.android.rateItUp;



import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class VoteActivity_hotel extends Activity{

	public static final int DIALOG_DOWNLOAD_FULL_PHOTO_PROGRESS = 0;
	private ProgressDialog mProgressDialog;
	GoogleMap theMap;

	public String ImageID;
	public String ImageName;
	public String ImageFullPath;
	public ArrayList<HashMap<String, Object>> MyArrList;
	public String Address;
	static final LatLng placeMark = new LatLng(9.003196, 38.766847);
	public RatingBar rating;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vote);

		// Show Map

		try {
			// find out if we already have it
			if (theMap == null) {
				// get the map
				theMap = ((MapFragment) getFragmentManager().findFragmentById(
						R.id.map)).getMap();

			}

			theMap.getUiSettings().setZoomGesturesEnabled(true);
			theMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			Marker TP = theMap.addMarker(new MarkerOptions()
					.position(placeMark).title("placeMark"));
			theMap.moveCamera(CameraUpdateFactory.newLatLngZoom(placeMark, 15));
			theMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

		} catch (Exception e) {
			e.printStackTrace();

		}

		// Permission StrictMode
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		Intent intent = getIntent();
		ImageID = String.valueOf(intent.getStringExtra("ImageID"));
		ImageName = String.valueOf(intent.getStringExtra("ImageName"));
		ImageFullPath = String.valueOf(intent.getStringExtra("ImagePathFull"));
		Address = String.valueOf(intent.getStringExtra("Address"));

		// Show Image Full
		new DownloadFullPhotoFileAsync().execute();

		// rating
		final RatingBar rating = (RatingBar) findViewById(R.id.rating);

		// Button Home
		final Button home = (Button) findViewById(R.id.btnHome);
		// Perform action on click
		home.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent newActivity = new Intent(VoteActivity_hotel.this,
						MainActivity.class);
				startActivity(newActivity);
			}
		});

		final AlertDialog.Builder adb = new AlertDialog.Builder(this);

		// Button btnVote
		final Button vote = (Button) findViewById(R.id.btnVote);
		// Perform action on click
		vote.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					if (rating.getRating() <= 0) {
						AlertDialog ad = adb.create();
						ad.setMessage("Please select rating point 1-5");
						ad.show();
					} else {
						// Disabled Button
						vote.setEnabled(false);
						home.setEnabled(false);

						String url = "http://rateup.aceplc.com/hotel/updateRating.php";

						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("ImageID", ImageID));
						params.add(new BasicNameValuePair("ratingPoint", String
								.valueOf(rating.getRating())));
						// params.add(new BasicNameValuePair("Address",
						// address));
						String resultServer = getHttpPost(url, params);

						/**
						 * Get result from Server (Return the JSON Code)
						 * StatusID = ? [0=Failed,1=Complete] Error = ? [On case
						 * error return custom error message]
						 * 
						 * Eg Save Failed =
						 * {"StatusID":"0","Error":"Not Update Data!"} Eg Save
						 * Complete = {"StatusID":"1","Error":""}
						 */

						/*** Default Value ***/
						String strStatusID = "0";
						String strError = "Unknow Status!";

						JSONObject c;
						try {
							c = new JSONObject(resultServer);
							strStatusID = c.getString("StatusID");
							strError = c.getString("Error");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						// Prepare Save Data
						if (strStatusID.equals("0")) {
							adb.setMessage(strError);
							adb.show();

							// Enabled Button
							vote.setEnabled(true);
							home.setEnabled(true);
						} else {
							// Show Toast
							Toast.makeText(
									VoteActivity_hotel.this,
									"Vote Finished (Point : "
											+ rating.getRating() + ")",
									Toast.LENGTH_LONG).show();
							Intent newAct = new Intent(getApplicationContext(),MainActivity_hotel.class);
							startActivity(newAct);
						}

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	// Show Image Full
	public void ShowImageFull(Bitmap imgFull) {
		/*
		 * // address TextView address = (TextView)
		 * findViewById(R.id.txtAddress); address.setText(Address);
		 */
		// fullimage
		TextView imgName = (TextView) findViewById(R.id.txtImageName);
		imgName.setText(ImageName);

		// fullimage
		// ImageView image = (ImageView) findViewById(R.id.fullimage);

		// try {
		// image.setImageBitmap(imgFull);
		// } catch (Exception e) {
		// When Error
		// image.setImageResource(android.R.drawable.ic_menu_report_image);
		// }

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_DOWNLOAD_FULL_PHOTO_PROGRESS:
			mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setMessage("Downloading.....");
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.setCancelable(true);
			mProgressDialog.show();
			return mProgressDialog;
		default:
			return null;
		}
	}

	// Download Full Photo in Background
	public class DownloadFullPhotoFileAsync extends
			AsyncTask<String, Void, Void> {

		Bitmap ImageFullBitmap = null;

		protected void onPreExecute() {
			super.onPreExecute();
			showDialog(DIALOG_DOWNLOAD_FULL_PHOTO_PROGRESS);
		}

		@Override
		protected Void doInBackground(String... params) {

			ImageFullBitmap = (Bitmap) loadBitmap(ImageFullPath);
			return null;
		}

		protected void onPostExecute(Void unused) {
			ShowImageFull(ImageFullBitmap); // When Finish Show Images
			dismissDialog(DIALOG_DOWNLOAD_FULL_PHOTO_PROGRESS);
			removeDialog(DIALOG_DOWNLOAD_FULL_PHOTO_PROGRESS);
		}

	}

	public String getHttpPost(String url, List<NameValuePair> params) {
		StringBuilder str = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);

		try {
			httpPost.setEntity(new UrlEncodedFormEntity(params));
			HttpResponse response = client.execute(httpPost);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) { // Status OK
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					str.append(line);
				}
			} else {
				Log.e("Log", "Failed to download result..");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return str.toString();
	}

	/***** Get Image Resource from URL (Start) *****/
	private static final String TAG = "Image";
	private static final int IO_BUFFER_SIZE = 4 * 1024;

	public static Bitmap loadBitmap(String url) {
		Bitmap bitmap = null;
		InputStream in = null;
		BufferedOutputStream out = null;

		try {
			in = new BufferedInputStream(new URL(url).openStream(),
					IO_BUFFER_SIZE);

			final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
			out = new BufferedOutputStream(dataStream, IO_BUFFER_SIZE);
			copy(in, out);
			out.flush();

			final byte[] data = dataStream.toByteArray();
			BitmapFactory.Options options = new BitmapFactory.Options();
			// options.inSampleSize = 1;

			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,
					options);
		} catch (IOException e) {
			Log.e(TAG, "Could not load Bitmap from: " + url);
		} finally {
			closeStream(in);
			closeStream(out);
		}

		return bitmap;
	}

	private static void closeStream(Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				android.util.Log.e(TAG, "Could not close stream", e);
			}
		}
	}

	private static void copy(InputStream in, OutputStream out)
			throws IOException {
		byte[] b = new byte[IO_BUFFER_SIZE];
		int read;
		while ((read = in.read(b)) != -1) {
			out.write(b, 0, read);
		}
	}
	/***** Get Image Resource from URL (End) *****/

}
