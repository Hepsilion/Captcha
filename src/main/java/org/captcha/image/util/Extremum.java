package org.captcha.image.util;

import java.util.ArrayList;

public class Extremum {
	public static ArrayList<Integer> getMinExtrem(int[] data){
		int min=data[0];
		ArrayList<Integer> list=new ArrayList<Integer>();

		int miniSpan=5; // �涨��С���
		int lastEqIndex=0;

		for(int i=1; i<data.length-1; i++){
			if(data[i]>data[i+1])
				min=data[i+1];

			// ����101���������͵ļ�ֵ
			if(data[i]<data[i+1] && data[i]<=min){
				// �ж�������Сֵ֮��ľ����Ƿ�������С���
				if((list.size()!=0 && (i-list.get(list.size()-1)>=miniSpan)) || list.size()==0) 
					list.add(i);
			}

			// ����100000001�������͵ļ�ֵ��ֻȡ��һ��0�������һ��0Ϊ��ֵ��
			if(data[i]==data[i+1]){
				if(lastEqIndex+1!=i){
					if((list.size()!=0 && (i-list.get(list.size()-1)>=miniSpan)) || list.size()==0)
						list.add(i);
				}
				lastEqIndex=i;
			}
		}

		for(Integer integer:list)
			System.out.print(integer+" ");

		return list;
	}
}
