package info.android.rateItUp;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class HomeFragment extends Fragment implements OnClickListener{
	
	public HomeFragment(){}
	Button btn_bar;
	Button btn_res;
	Button btn_mall;
	Button btn_gym;
	Button btn_hotel;
	Button btn_event;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        btn_bar = (Button) rootView.findViewById (R.id.button1);
        btn_bar.setOnClickListener(this);
        
        btn_res = (Button) rootView.findViewById (R.id.button2);
        btn_res.setOnClickListener(this);
        
        btn_hotel = (Button) rootView.findViewById (R.id.button3);
        btn_hotel.setOnClickListener(this);
        
        btn_event = (Button) rootView.findViewById (R.id.button4);
        btn_event.setOnClickListener(this);
        
        btn_mall = (Button) rootView.findViewById (R.id.button5);
        btn_mall.setOnClickListener(this);
        
        btn_gym = (Button) rootView.findViewById (R.id.button6);
        btn_gym.setOnClickListener(this);
        
        
        return rootView;
    }
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		
		switch (v.getId()) {
		case R.id.button1:
			Intent intent_bar  =  new Intent(getActivity(), MainActivity_bar.class);
			startActivity(intent_bar);
			break;
		case R.id.button2:
			Intent intent_res  =  new Intent(getActivity(), MainActivity_res.class);
			startActivity(intent_res);
			break;
			
		case R.id.button3:
			Intent intent_hotel  =  new Intent(getActivity(), MainActivity_hotel.class);
			startActivity(intent_hotel);
			break;
		case R.id.button4:
			Intent intent_event  =  new Intent(getActivity(), MainActivity_cen.class);
			startActivity(intent_event);
			break;
		case R.id.button5:
			Intent intent_mall  =  new Intent(getActivity(), MainActivity_mall.class);
			startActivity(intent_mall);
			break;
		case R.id.button6:
			Intent intent_gym  =  new Intent(getActivity(), MainActivity_gym.class);
			startActivity(intent_gym);
			break;
		default:
			break;
		}
		
	
	}
	
	
	
	
}
