package edu.hubu.seckill.exception;

/**
 * ��ɱҵ������쳣
 * @author sucan
 *
 */

public class SeckillExceprion extends RuntimeException {

	public SeckillExceprion(String message){
		super(message);
	}
	
	public SeckillExceprion(String message,Throwable cause){
		super(message,cause);
	}
}
