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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

public class MainActivity_res extends Activity {

	public static final int DIALOG_DOWNLOAD_JSON_PROGRESS = 0;
    private ProgressDialog mProgressDialog;
    
    ArrayList<HashMap<String, Object>> MyArrList;

    @SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_rate);
        
        // Permission StrictMode
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        
	    // Download JSON File	
		new DownloadJSONFileAsync().execute();
        
    }
       
    
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_DOWNLOAD_JSON_PROGRESS:
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
    
    // Show All Content
    public void ShowAllContent()
    {
        // listView1
        final ListView lstView1 = (ListView)findViewById(R.id.listView1); 
        lstView1.setAdapter(new ImageAdapter(MainActivity_res.this,MyArrList));
        
    }
    
    

    public class ImageAdapter extends BaseAdapter 
    {
        private Context context;
        private ArrayList<HashMap<String, Object>> MyArr = new ArrayList<HashMap<String, Object>>();

        public ImageAdapter(Context c, ArrayList<HashMap<String, Object>> myArrList) 
        {
        	// TODO Auto-generated method stub
            context = c;
            MyArr = myArrList;
        }
 
        public int getCount() {
        	// TODO Auto-generated method stub
            return MyArr.size();
        }
 
        public Object getItem(int position) {
        	// TODO Auto-generated method stub
            return position;
        }
 
        public long getItemId(int position) {
        	// TODO Auto-generated method stub
            return position;
        }
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			
			
			 
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 
		 
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.activity_column, null); 
			}

			// ColImage
			ImageView imageView = (ImageView) convertView.findViewById(R.id.ColImgPath);
			imageView.getLayoutParams().height = 80;
			imageView.getLayoutParams().width = 80;
			imageView.setPadding(10, 10, 10, 10);
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        	 try
        	 {
        		 imageView.setImageBitmap((Bitmap)MyArr.get(position).get("ImageThumBitmap"));
        	 } catch (Exception e) {
        		 // When Error
        		 imageView.setImageResource(android.R.drawable.ic_menu_report_image);
        	 }
        	 // Click on Image
        	 imageView.setOnClickListener(new View.OnClickListener() {
                 public void onClick(View v) {
     				String strImageID = MyArr.get(position).get("ImageID").toString();
    				String strImageName = MyArr.get(position).get("ImageName").toString();
    				String strImagePathFull = MyArr.get(position).get("ImagePathFull").toString();
    				//String strAddress = MyArr.get(position).get("address").toString();
    				Intent newActivity = new Intent(MainActivity_res.this,VoteActivity_res.class);
    				newActivity.putExtra("ImageID", strImageID);
    				newActivity.putExtra("ImageName", strImageName);
    				newActivity.putExtra("ImagePathFull", strImagePathFull);
    			//	newActivity.putExtra("address", strAddress);
    				startActivity(newActivity);
                 }
             });
        	 
        	 
				
			/*// ColImgID
			TextView txtImgID = (TextView) convertView.findViewById(R.id.ColImgID);
			txtImgID.setPadding(5, 0, 0, 0);
			txtImgID.setText("ID : " + MyArr.get(position).get("ImageID").toString());
			*/
			// ColImgName
			TextView txtPicName = (TextView) convertView.findViewById(R.id.ColImgName);
			txtPicName.setPadding(5, 0, 0, 0);
			txtPicName.setText(MyArr.get(position).get("ImageName").toString());	
		 
			// ColratingBar
			RatingBar Rating = (RatingBar) convertView.findViewById(R.id.ColratingBar);
			Rating.setPadding(10, 0, 0, 0);
			Rating.setEnabled(false);
			Rating.setMax(5);
			Rating.setRating(Float.valueOf(MyArr.get(position).get("Rating").toString()));
	
			
			return convertView;
				
		}

    } 
    
    
    
    // Download JSON in Background
    public class DownloadJSONFileAsync extends AsyncTask<String, Void, Void> {
    	
        protected void onPreExecute() {
        	super.onPreExecute();
        	showDialog(DIALOG_DOWNLOAD_JSON_PROGRESS);
        }

        @Override
        protected Void doInBackground(String... params) {
            // TODO Auto-generated method stub
        	
        	String url = "http://rateup.aceplc.com/restaurant/getGallery.php";
        	
        	JSONArray data;
			try {
				data = new JSONArray(getJSONUrl(url));
				
		    	MyArrList = new ArrayList<HashMap<String, Object>>();
				HashMap<String, Object> map;
				
				for(int i = 0; i < data.length(); i++){
	                JSONObject c = data.getJSONObject(i);
	    			map = new HashMap<String, Object>();
	    			map.put("ImageID", (String)c.getString("ImageID"));
	    			map.put("ImageName", (String)c.getString("ImageName"));
	    			
	    			// Thumbnail Get ImageBitmap To Object
	    			map.put("ImagePathThum", (String)c.getString("ImagePath_Thumbnail"));
	    			map.put("ImageThumBitmap", (Bitmap)loadBitmap(c.getString("ImagePath_Thumbnail")));
	    			
	    			// Full (for View Popup)
	    			map.put("ImagePathFull", (String)c.getString("ImagePath_FullPhoto"));
	    			
	    			map.put("Rating", (String)c.getString("Rating"));
	    		    //map.put("Address", (String)c.getString("Address"));
	    			MyArrList.add(map);
				}
	    		
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

    		return null;
        }

        protected void onPostExecute(Void unused) {
        	ShowAllContent(); // When Finish Show Content
            dismissDialog(DIALOG_DOWNLOAD_JSON_PROGRESS);
            removeDialog(DIALOG_DOWNLOAD_JSON_PROGRESS);
        }
        

    }

    
    /*** Get JSON Code from URL ***/
  	public String getJSONUrl(String url) {
  		StringBuilder str = new StringBuilder();
  		HttpClient client = new DefaultHttpClient();
  		HttpGet httpGet = new HttpGet(url);
  		try {
  			HttpResponse response = client.execute(httpGet);
  			StatusLine statusLine = response.getStatusLine();
  			int statusCode = statusLine.getStatusCode();
  			if (statusCode == 200) { // Download OK
  				HttpEntity entity = response.getEntity();
  				InputStream content = entity.getContent();
  				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
  				String line;
  				while ((line = reader.readLine()) != null) {
  					str.append(line);
  				}
  			} else {
  				Log.e("Log", "Failed to download file..");
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
	        in = new BufferedInputStream(new URL(url).openStream(), IO_BUFFER_SIZE);

	        final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
	        out = new BufferedOutputStream(dataStream, IO_BUFFER_SIZE);
	        copy(in, out);
	        out.flush();

	        final byte[] data = dataStream.toByteArray();
	        BitmapFactory.Options options = new BitmapFactory.Options();
	        //options.inSampleSize = 1;

	        bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,options);
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
	 
	 private static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] b = new byte[IO_BUFFER_SIZE];
        int read;
        while ((read = in.read(b)) != -1) {
            out.write(b, 0, read);
        }
    }
	 /***** Get Image Resource from URL (End) *****/
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
