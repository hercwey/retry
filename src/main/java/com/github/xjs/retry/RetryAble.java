/**
 * 
 */
package com.github.xjs.retry;

/**
 * @author 605162215@qq.com
 *
 * 2016��8��6�� ����2:18:28
 */
public interface RetryAble {

	public boolean retry()throws Exception;
	
}