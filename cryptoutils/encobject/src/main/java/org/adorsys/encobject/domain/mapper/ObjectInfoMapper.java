package org.adorsys.encobject.domain.mapper;

import org.adorsys.encobject.domain.ObjectInfo;
import org.adorsys.encobject.domain.pbf.ObjectInfoData;

public class ObjectInfoMapper {
	public static ObjectInfo fromPbf(ObjectInfoData d){
		ObjectInfo o = new ObjectInfo();
		o.setChecksum(d.getChecksum());
		o.setCompAlg(d.getCompAlg());
		o.setDesc(d.getDesc());
		o.setEncAlg(d.getEncAlg());
		o.setSize(d.getSize());
		return o;
	}
	
	public static ObjectInfoData toPbf(ObjectInfo o){
		return ObjectInfoData.newBuilder()
		.setChecksum(o.getChecksum())
		.setCompAlg(o.getCompAlg())
		.setDesc(o.getDesc())
		.setSize(o.getSize())
		.setEncAlg(o.getEncAlg()).build();
	}
}
