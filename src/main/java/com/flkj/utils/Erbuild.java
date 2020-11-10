package com.flkj.utils;

import com.flkj.pojo.Er;

/**
 * @author ：www
 * @date ：Created in 20-8-14 下午4:31
 * @description：
 * @modified By：
 * @version:
 */
public class Erbuild {
	public static Er erbuild(long eid,String eoid,long eids,String eoids,long re) {
		Er er = new Er();
		er.setEid(eid);
		er.setEids(eids);
		er.setEoid(eoid);
		er.setEoids(eoids);
		er.setRelation(re);
		return er;
	}
}
