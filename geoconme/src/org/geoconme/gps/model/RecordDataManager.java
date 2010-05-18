package org.geoconme.gps.model;

import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

public class RecordDataManager{

	private RecordStore rs;

	public RecordDataManager(String confdb){     
		try {
			rs = RecordStore.openRecordStore(confdb,true);
		} catch (RecordStoreFullException e) {
			e.printStackTrace();
		} catch (RecordStoreNotFoundException e) {
			e.printStackTrace();
		} catch (RecordStoreException e) {
			e.printStackTrace();
		}
	}

	public void closeRecordStore(){
		try {
			rs.closeRecordStore();
		} catch (RecordStoreNotOpenException e) {
			e.printStackTrace();
		} catch (RecordStoreException e) {
			e.printStackTrace();
		}
	}

	public void saveConf(String conf){
		if(conf==null){
			try {
				throw new Exception();
			} catch (Exception e) {
				e.printStackTrace();
			}}else{
				byte[] dados = conf.getBytes();
				try {

					rs.addRecord(dados,0,dados.length);  

				} catch (RecordStoreNotOpenException e) {
					e.printStackTrace();
				} catch (RecordStoreFullException e) {
					e.printStackTrace();
				} catch (RecordStoreException e) {
					e.printStackTrace();
				}
			}
	}

	public String[] getConf(){
		int total = 0;
		try {
			total = rs.getNumRecords();     
		} catch (RecordStoreNotOpenException e) {
			e.printStackTrace();
		}
		String[] confStringArray = null;
		String confString = null;
		if(total > 0){
			confStringArray = new String[total];
			for(int i=1; i<=total; i++){

				try {
					confString = new String(rs.getRecord(i));

				} catch (RecordStoreNotOpenException e) {
					confString = "";  
					e.printStackTrace();

				} catch (InvalidRecordIDException e) {
					confString = "";  
					e.printStackTrace();

				} catch (RecordStoreException e) {
					confString = "";
					e.printStackTrace();
				}

				confStringArray[0] = confString;
			}
		}else{
			confStringArray = new String[1];
			confStringArray[0] = "";
		}


		return confStringArray;
	}
}
