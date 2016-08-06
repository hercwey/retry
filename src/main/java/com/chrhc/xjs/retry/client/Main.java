/**
 * 
 */
package com.chrhc.xjs.retry.client;

import java.util.Random;

import com.chrhc.xjs.retry.RetryAble;
import com.chrhc.xjs.retry.RetryService;
import com.chrhc.xjs.retry.RetryService.OnRetryListener;
import com.chrhc.xjs.retry.RetryTask;
import com.chrhc.xjs.retry.persist.RetryPersistService;

/**
 * @author 605162215@qq.com
 *
 * 2016��8��6�� ����1:05:30
 */
public class Main {
	
	public static class Business implements RetryAble{
		public boolean retryAble() throws Exception{
			try{
				System.out.println("[Business]do business...");
				Thread.sleep(1000);
				int rnd = new Random().nextInt(100);
				System.out.println("[Business]rnd:"+rnd);
				if(rnd > 80){
					System.out.println("[Business]do business end");
					return true;
				}else{
					throw new Exception();
				}
			}catch(Exception e){
				System.out.println("[Business]do business exception!");
				throw e;
			}
		}
	}

	public static void main(String[] args) {
		//����service
		RetryService service = new RetryService(new int[]{1,3,5,7,9}, new RetryPersistService());
		//����service
		service.start(new OnRetryListener(){
			@Override
			public void onRetryArrived(RetryTask retryTask) {
				System.out.println("[main]onDelayedArrived:"+retryTask);
			}
			public void onRetryFailed(RetryTask retryTask){
				System.out.println("[main]onRetryFailed");
			}
	        public void onRetrySuccessed(RetryTask retryTask){
	        	System.out.println("[main]onRetrySuccessed");
	        }
		});
		//��ҵ���߼�����
		Business business = new Business();
		try{
			business.retryAble();
		}catch(Exception e){
			System.out.println("[main]business ecxception, try redo");
			//ʧ������
			service.add(Business.class);
		}
	}
}