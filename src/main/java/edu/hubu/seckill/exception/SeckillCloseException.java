package edu.hubu.seckill.exception;

/**
 * ��ɱ�ر��쳣
 * @author sucan
 *
 */

public class SeckillCloseException extends SeckillExceprion {

	public SeckillCloseException(String message){
		super(message);
	}
	
	public SeckillCloseException(String message,Throwable cause){
		super(message,cause);
	}
}
