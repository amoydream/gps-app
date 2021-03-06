package cn.com.gps169.bos.service.impl;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.gps169.bos.service.IVehicleMonitorService;
import cn.com.gps169.common.cache.IDataAcquireCacheManager;
import cn.com.gps169.common.cache.ICacheManager;
import cn.com.gps169.common.model.GpsInfo;
import cn.com.gps169.db.dao.VehicleMapper;
import cn.com.gps169.db.model.Vehicle;
import cn.com.gps169.db.model.VehicleExample;

/**
 *  车辆监控业务实现类
 * @author tianfei
 *
 */
@Service
public class VehicleMonitorServiceImpl implements IVehicleMonitorService {
	
	//车辆缓存
	@Autowired
	private ICacheManager cacheManager;
	
	@Autowired
	private IDataAcquireCacheManager dataAcquireCacheManager;
	
	@Autowired
	private VehicleMapper vehicleMapper;
	
	@Override
	public JSONArray queryVehicles(JSONObject params) {
		// 查询条件
		VehicleExample example = new VehicleExample();
		List<Vehicle> list = vehicleMapper.selectByExample(example);
		// 车辆列表信息、gps
		JSONArray array = new JSONArray();
		GpsInfo gps = null;
		for(Vehicle vehicle : list){
			//车辆位置
			gps = cacheManager.findGpsInfoBySim(vehicle.getSimNo());
			if(gps == null){
				continue;
			}
			array.add(JSONObject.fromObject(gps));
		}
		
		return array;
	}

	@Override
	public JSONObject queryRealLocation(int vehicleId, long updateTime) {
		JSONObject json = dataAcquireCacheManager.getGps(vehicleId);
		if(json != null && json.optLong("updated") > updateTime){
		    JSONObject j = new JSONObject();
		    j.put("longitude", json.get("longitude"));
		    j.put("latitude", json.get("latitude"));
		    j.put("speed", json.get("speed"));
		    j.put("updated", json.get("updated"));
		    json.put("position", j);
		    
			return json;
		}
		return new JSONObject();
	}

	@Override
	public JSONArray queryTrack() {
		JSONArray array = new JSONArray();
		//查询车辆信息
		VehicleExample example = new VehicleExample();
		List<Vehicle> list = vehicleMapper.selectByExample(example);
		JSONObject veh = null;	//车辆对象
		JSONObject track = null;
		JSONArray tracks = null; //车辆轨迹集合
		for(Vehicle v : list){
			veh = new JSONObject();
			veh.put("licensePlate", v.getPlateNo());
			veh.put("vid", v.getVehicleId());
			tracks = new JSONArray();
			//查询车辆行程
//			TripExample  e = new TripExample();
//			e.or().andVehicleIdEqualTo(v.getVehicleId());
//			List<Trip> trips = tripMapper.selectByExample(e);
//			for(Trip t : trips){
//				track = new JSONObject();
//				track.put("tid", t.getTripId());
//				track.put("recDay", t.getRecday());
//				tracks.add(track);
//			}
			veh.put("tracks", tracks);
			array.add(veh);
		}
		
		return array;
	}

	@Override
	public String queryTripById(int tripId) {
//		TripExample example = new TripExample();
//		example.or().andTripIdEqualTo(tripId);
//		List<Trip> list = tripMapper.selectByExampleWithBLOBs(example);
//		if(!list.isEmpty()){
//			return list.get(0).getGps();
//		}
		
		return "{}";
	}
	
	

}
